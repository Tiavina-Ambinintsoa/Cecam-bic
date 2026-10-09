package mg.cecam.bic.score;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.client.Client;
import mg.cecam.bic.client.Emploi;
import mg.cecam.bic.client.EmploiRepository;
import mg.cecam.bic.common.enums.BucketRetard;
import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.RoleClient;
import mg.cecam.bic.contrat.Contrat;
import mg.cecam.bic.contrat.ContratRepository;
import mg.cecam.bic.contrat.Echeance;
import mg.cecam.bic.contrat.EcheanceRepository;
import mg.cecam.bic.contrat.HistoriquePaiementMensuel;
import mg.cecam.bic.contrat.HistoriquePaiementMensuelRepository;
import mg.cecam.bic.referentiel.GrilleScore;
import mg.cecam.bic.referentiel.GrilleScoreRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ScoreService {

    private final GrilleScoreRepository grilleScoreRepository;
    private final ContratRepository contratRepository;
    private final EcheanceRepository echeanceRepository;
    private final HistoriquePaiementMensuelRepository historiquePaiementMensuelRepository;
    private final EmploiRepository emploiRepository;
    private final ScoreProperties p;

    public ScoreResult calculer(Client client, Contrat contratEnCours, boolean clientTrouve) {
        LocalDate reference = LocalDate.now();
        Long contratEnCoursId = contratEnCours == null ? null : contratEnCours.getId();

        List<Contrat> historique = contratRepository.findByClient_Id(client.getId()).stream()
                .filter(c -> !Objects.equals(c.getId(), contratEnCoursId))
                .toList();
        List<Contrat> empruntsDecaisses = historique.stream()
                .filter(this::estEmprunteur)
                .filter(c -> c.getPhaseDemande() == PhaseDemande.ACTIF
                        || c.getPhaseDemande() == PhaseDemande.FERME)
                .toList();

        if (empruntsDecaisses.isEmpty()) {
            return ScoreResult.nonCalculable(clientTrouve
                    ? "Aucun contrat décaissé comme emprunteur ne permet d'évaluer son comportement de paiement."
                    : "Ce client vient d'être créé et ne possède pas encore d'historique de remboursement.");
        }

        Map<Long, List<Echeance>> echeancesParContrat = new HashMap<>();
        List<Echeance> observationsExactes = new ArrayList<>();
        List<HistoriquePaiementMensuel> observationsMensuelles = new ArrayList<>();
        Set<YearMonth> moisObserves = new HashSet<>();

        for (Contrat contrat : empruntsDecaisses) {
            List<Echeance> echeances = echeanceRepository
                    .findByContrat_IdOrderByNumeroEcheance(contrat.getId());
            echeancesParContrat.put(contrat.getId(), echeances);
            List<Echeance> echues = echeances.stream()
                    .filter(e -> !e.getDateEcheance().isAfter(reference))
                    .toList();

            if (!echues.isEmpty()) {
                observationsExactes.addAll(echues);
                echues.stream().map(e -> YearMonth.from(e.getDateEcheance())).forEach(moisObserves::add);
                continue;
            }

            List<HistoriquePaiementMensuel> historiqueMensuel = historiquePaiementMensuelRepository
                    .findByContrat_IdOrderByMoisDesc(contrat.getId()).stream()
                    .filter(h -> h.getMois() != null && !h.getMois().isAfter(reference))
                    .toList();
            for (HistoriquePaiementMensuel observation : historiqueMensuel) {
                if (qualite(observation) == null) {
                    if (!statutNonObservable(observation.getStatut())) {
                        return ScoreResult.nonCalculable(
                                "Un statut source ne précise pas un retard exploitable. Saisissez le nombre de jours ou un bucket exact avant le calcul.");
                    }
                    continue;
                }
                observationsMensuelles.add(observation);
                moisObserves.add(YearMonth.from(observation.getMois()));
            }
        }

        if (observationsExactes.isEmpty() && observationsMensuelles.isEmpty()) {
            return ScoreResult.nonCalculable(
                    "Aucune échéance échue ou observation mensuelle exploitable n'est enregistrée.");
        }

        double poidsTotal = 0;
        double poidsPondere = 0;
        for (Echeance echeance : observationsExactes) {
            double poids = poidsAnciennete(echeance.getDateEcheance(), reference)
                    * montant(echeance.getMontantDu());
            poidsTotal += poids;
            poidsPondere += poids * qualite(echeance, reference);
        }
        for (HistoriquePaiementMensuel observation : observationsMensuelles) {
            double poids = poidsAnciennete(observation.getMois(), reference)
                    * nombreEcheancesObservees(observation);
            poidsTotal += poids;
            poidsPondere += poids * Objects.requireNonNull(qualite(observation));
        }
        if (poidsTotal <= 0) {
            return ScoreResult.nonCalculable("Les observations disponibles ne permettent pas de pondérer le comportement de paiement.");
        }
        double tauxQualite = poidsPondere / poidsTotal;
        int nombreMoisObserves = moisObserves.size();

        double maturite = Math.max(1, p.getMaturiteMois());
        double confiance = Math.min(1.0, nombreMoisObserves / maturite);
        double facteurConfiance = p.getPlancherConfiance()
                + (1 - p.getPlancherConfiance()) * confiance;
        double pointsPaiement = p.getMaxPaiement() * tauxQualite * facteurConfiance;

        Emploi emploi = emploiRepository.findByClient_Id(client.getId()).orElse(null);
        BigDecimal revenuAnnuel = emploi == null ? null : emploi.getRevenuAnnuelTotal();
        if (revenuAnnuel != null && revenuAnnuel.signum() <= 0) {
            return ScoreResult.nonCalculable("Le revenu annuel renseigné doit être supérieur à zéro pour calculer les ratios financiers.");
        }
        boolean revenuConnu = revenuAnnuel != null;

        List<Contrat> contratsFinanciers = contratsFinanciers(historique, contratEnCours);
        BigDecimal chargeMensuelle = BigDecimal.ZERO;
        BigDecimal exposition = BigDecimal.ZERO;
        List<String> avertissements = new ArrayList<>();

        double pointsEndettement;
        double pointsExposition;
        Double tauxEndettement = null;
        Double tauxExposition = null;
        double malus = 0;

        if (revenuConnu) {
            String deviseRevenu = normaliserDevise(emploi.getDevise());
            if (deviseRevenu == null) {
                return ScoreResult.nonCalculable("La devise du revenu annuel est absente ; aucun taux financier ne peut être comparé.");
            }
            for (Contrat contrat : contratsFinanciers) {
                String deviseContrat = normaliserDevise(contrat.getDevise());
                if (deviseContrat == null || !deviseRevenu.equals(deviseContrat)) {
                    return ScoreResult.nonCalculable(
                            "Les devises du revenu et des engagements ne sont pas identiques ou ne sont pas renseignées. Aucune conversion n'est inventée.");
                }
                List<Echeance> echeances = echeancesParContrat.computeIfAbsent(contrat.getId(), id ->
                        echeanceRepository.findByContrat_IdOrderByNumeroEcheance(id));
                if (!donneesFinancieresCompletes(contrat, echeances)) {
                    return ScoreResult.nonCalculable(
                            "Les montants ou échéances d'un engagement actif/en cours sont incomplets ; les ratios financiers ne sont pas calculables.");
                }
                chargeMensuelle = chargeMensuelle.add(mensualite(contrat));
                BigDecimal expositionContrat = exposition(contrat, echeances);
                if (expositionContrat == null) {
                    return ScoreResult.nonCalculable(
                            "Le montant restant dû ou le montant d'une demande en cours est inconnu.");
                }
                exposition = exposition.add(expositionContrat);
            }

            tauxEndettement = chargeMensuelle.multiply(BigDecimal.valueOf(12))
                    .divide(revenuAnnuel, 8, RoundingMode.HALF_UP).doubleValue();
            tauxExposition = exposition.divide(revenuAnnuel, 8, RoundingMode.HALF_UP).doubleValue();
            pointsEndettement = interpoler(tauxEndettement,
                    p.getEndettementSeuilVert(), p.getEndettementSeuilRouge(), p.getMaxEndettement());
            pointsExposition = interpoler(tauxExposition,
                    p.getExpositionSeuilVert(), p.getExpositionSeuilRouge(), p.getMaxExposition());
            if (tauxEndettement > p.getEndettementSeuilRouge()) {
                malus = Math.min(p.getMalusSurendettementMax(),
                        (tauxEndettement - p.getEndettementSeuilRouge()) * p.getMalusSurendettementCoef());
            }
        } else {
            pointsEndettement = p.getMaxEndettement() * p.getNeutreSansRevenu();
            pointsExposition = p.getMaxExposition() * p.getNeutreSansRevenu();
            avertissements.add("Revenu annuel absent : les ratios d'endettement et d'exposition ne sont pas évalués.");
        }

        double pointsAnciennete = 0;
        if (client.getDateAdhesion() != null) {
            long mois = Math.max(0, ChronoUnit.MONTHS.between(client.getDateAdhesion(), reference));
            pointsAnciennete = p.getMaxAnciennete()
                    * Math.min(1.0, (double) mois / Math.max(1, p.getAncienneteMoisPlein()));
        }

        long creditsRecents = empruntsDecaisses.stream()
                .filter(c -> c.getDateDemande() != null)
                .filter(c -> {
                    long mois = ChronoUnit.MONTHS.between(c.getDateDemande(), reference);
                    return mois >= 0 && mois <= p.getFenetreContratRecentMois();
                })
                .count();
        long demandesDormantes = historique.stream()
                .filter(this::estEmprunteur)
                .filter(c -> c.getPhaseDemande() == PhaseDemande.DEMANDE_EN_COURS)
                .filter(c -> c.getDateDemande() != null)
                .filter(c -> ChronoUnit.DAYS.between(c.getDateDemande(), reference)
                        > p.getSeuilDemandeEnAttenteJours())
                .count();
        double pointsNouveaux = Math.max(0, p.getMaxNouveaux()
                - creditsRecents * p.getMalusParContratRecent()
                - demandesDormantes * p.getMalusParDemandeEnAttente());

        long types = empruntsDecaisses.stream()
                .map(Contrat::getTypeContrat)
                .filter(Objects::nonNull)
                .distinct()
                .count();
        double pointsMixite = p.getMaxMixite() * (types >= 3 ? 1.0
                : types == 2 ? p.getMixiteRatioDeuxTypes()
                : types == 1 ? p.getMixiteRatioUnType() : 0);

        double secondaireBrut = pointsEndettement + pointsExposition + pointsAnciennete
                + pointsNouveaux + pointsMixite;
        double plafond = p.maxSecondaire()
                * (p.getPlafondSecondaireBase()
                + p.getPlafondSecondairePart() * (pointsPaiement / p.getMaxPaiement()));
        double secondaireRetenu = Math.min(secondaireBrut, plafond);
        double brut = p.getScoreBase() + pointsPaiement + secondaireRetenu - malus;
        int score = Math.max(p.getScoreBase(), Math.min(p.getScoreMax(), (int) Math.round(brut)));

        GrilleScore grille = grilleScorePour(score);
        String message = avertissements.isEmpty() ? null : String.join(" ", avertissements);
        return ScoreResult.calcule(score, grille.getIntervalle(), grille.getCategorieRisque(),
                grille.getCouleur(), message);
    }

    private boolean estEmprunteur(Contrat contrat) {
        return contrat.getRoleClient() != RoleClient.GARANT;
    }

    private List<Contrat> contratsFinanciers(List<Contrat> historique, Contrat contratEnCours) {
        List<Contrat> contrats = new ArrayList<>(historique.stream()
                .filter(this::estEmprunteur)
                .filter(c -> c.getPhaseDemande() == PhaseDemande.ACTIF
                        || c.getPhaseDemande() == PhaseDemande.DEMANDE_EN_COURS)
                .toList());
        if (contratEnCours != null && estEmprunteur(contratEnCours)
                && (contratEnCours.getPhaseDemande() == PhaseDemande.ACTIF
                || contratEnCours.getPhaseDemande() == PhaseDemande.DEMANDE_EN_COURS)) {
            ajouterSiAbsent(contrats, contratEnCours);
        }
        return contrats;
    }

    private void ajouterSiAbsent(List<Contrat> contrats, Contrat candidat) {
        if (contrats.stream().noneMatch(c -> Objects.equals(c.getId(), candidat.getId()))) {
            contrats.add(candidat);
        }
    }

    private boolean donneesFinancieresCompletes(Contrat contrat, List<Echeance> echeances) {
        if (mensualite(contrat).signum() <= 0) return false;
        if (contrat.getPhaseDemande() == PhaseDemande.ACTIF) {
            return contrat.getMontantRestantDu() != null || !echeances.isEmpty();
        }
        return (contrat.getMontantTotalDu() != null && contrat.getMontantTotalDu().signum() > 0)
                || (contrat.getMontantFinance() != null && contrat.getMontantFinance().signum() > 0);
    }

    private BigDecimal exposition(Contrat contrat, List<Echeance> echeances) {
        if (contrat.getPhaseDemande() == PhaseDemande.DEMANDE_EN_COURS) {
            if (contrat.getMontantTotalDu() != null && contrat.getMontantTotalDu().signum() > 0) {
                return contrat.getMontantTotalDu();
            }
            return contrat.getMontantFinance();
        }
        if (contrat.getMontantRestantDu() != null) return contrat.getMontantRestantDu();
        if (echeances.isEmpty()) return null;
        return echeances.stream()
                .map(e -> resteEcheance(e.getMontantDu(), e.getMontantPaye()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal mensualite(Contrat contrat) {
        if (contrat.getMontantEcheanceMensuelle() != null
                && contrat.getMontantEcheanceMensuelle().signum() > 0) {
            return contrat.getMontantEcheanceMensuelle();
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal resteEcheance(BigDecimal montantDu, BigDecimal montantPaye) {
        BigDecimal reste = montantDu.subtract(montantPaye == null ? BigDecimal.ZERO : montantPaye);
        return reste.signum() > 0 ? reste : BigDecimal.ZERO;
    }

    private Double qualite(HistoriquePaiementMensuel observation) {
        if (observation.getJoursDeRetard() != null) {
            if (observation.getJoursDeRetard() < 0) return null;
            return qualiteRetardJours(observation.getJoursDeRetard());
        }
        BucketRetard bucket = BucketRetard.depuisStatut(observation.getStatut());
        return bucket == null ? null : qualite(bucket);
    }

    private boolean statutNonObservable(String statut) {
        if (statut == null || statut.isBlank()) return true;
        String normalise = Normalizer.normalize(statut, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9]+", "");
        return normalise.isBlank() || Set.of("TROPRECENT", "NONDISPONIBLE", "ND", "NA", "NC").contains(normalise);
    }

    private double qualite(BucketRetard bucket) {
        return switch (bucket) {
            case A_JOUR -> p.getQualitePayeATemps();
            case JOURS_1_29 -> p.getQualiteRetardCourt();
            case JOURS_30_59 -> qualiteRetardJours(30);
            case JOURS_60_89 -> qualiteRetardJours(60);
            case JOURS_90_119 -> qualiteRetardJours(90);
            case JOURS_120_149 -> qualiteRetardJours(120);
            case JOURS_150_179 -> qualiteRetardJours(150);
            case JOURS_180_PLUS -> p.getQualiteImpaye();
        };
    }

    private int nombreEcheancesObservees(HistoriquePaiementMensuel observation) {
        return observation.getNombreEcheances() == null
                ? 1 : Math.max(1, observation.getNombreEcheances());
    }

    private double montant(BigDecimal montant) {
        return montant == null ? 1.0 : Math.max(1.0, montant.doubleValue());
    }

    private double qualite(Echeance echeance, LocalDate reference) {
        int jours = echeance.joursDeRetard(reference);
        if (jours <= 0) return p.getQualitePayeATemps();
        return qualiteRetardJours(jours);
    }

    private double qualiteRetardJours(int jours) {
        if (jours < 30) return p.getQualiteRetardCourt();
        if (jours >= 180) return p.getQualiteImpaye();
        double progression = (jours - 30) / 150.0;
        return p.getQualiteRetardLong()
                + (p.getQualiteImpaye() - p.getQualiteRetardLong()) * progression;
    }

    private double poidsAnciennete(LocalDate dateObservation, LocalDate reference) {
        long mois = ChronoUnit.MONTHS.between(dateObservation, reference);
        if (mois <= 12) return p.getPoidsRecent();
        if (mois <= 36) return p.getPoidsMoyen();
        return p.getPoidsAncien();
    }

    private String normaliserDevise(String devise) {
        if (devise == null || devise.isBlank()) return null;
        String normalise = Normalizer.normalize(devise, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toUpperCase(Locale.ROOT)
                .replaceAll("[^A-Z0-9]+", "");
        if (normalise.isBlank()) return null;
        return normalise.equals("MGA") || normalise.contains("ARIARY") ? "MGA" : normalise;
    }

    private double interpoler(double taux, double seuilVert, double seuilRouge, double maximum) {
        if (taux <= seuilVert) return maximum;
        if (taux >= seuilRouge) return 0;
        return maximum * (seuilRouge - taux) / (seuilRouge - seuilVert);
    }

    private GrilleScore grilleScorePour(int score) {
        return grilleScoreRepository.findAll().stream()
                .sorted(Comparator.comparing(GrilleScore::getScoreMin))
                .filter(g -> score >= g.getScoreMin() && score <= g.getScoreMax())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "La grille de score ne couvre pas la valeur " + score));
    }
}
