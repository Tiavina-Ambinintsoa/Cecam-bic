package mg.cecam.bic.score;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.client.Client;
import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.RoleClient;
import mg.cecam.bic.common.enums.StatutEcheance;
import mg.cecam.bic.contrat.Contrat;
import mg.cecam.bic.contrat.Echeance;
import mg.cecam.bic.referentiel.GrilleScore;
import mg.cecam.bic.referentiel.GrilleScoreRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ScoreService {

    private final GrilleScoreRepository grilleScoreRepository;
    private final ScoreProperties p;

    public ScoreResult calculer(Client client,
                                Contrat contratSaisi,
                                List<Contrat> historique,
                                Map<Long, List<Echeance>> echeancesParContrat,
                                BigDecimal revenuAnnuel,
                                LocalDate reference) {

        if (historique.isEmpty()) {
            return ScoreResult.nonCalculable("Ce client a été nouvellement créé dans le système");
        }

        List<Echeance> echues = historique.stream()
                .flatMap(c -> echeancesParContrat.getOrDefault(c.getId(), List.of()).stream())
                .filter(e -> !e.getDateEcheance().isAfter(reference))
                .toList();

        int moisObserves = moisObserves(historique, reference);

        if (echues.isEmpty() && moisObserves == 0) {
            return ScoreResult.nonCalculable(
                    "Aucun crédit de ce client n'a encore été décaissé : son comportement "
                  + "de paiement n'est pas observable");
        }

        double poidsTotal = 0;
        double poidsPondere = 0;
        for (Echeance e : echues) {
            double poids = poidsAnciennete(e.getDateEcheance(), reference)
                         * montant(e.getMontantDu());
            poidsTotal += poids;
            poidsPondere += poids * qualite(e, reference);
        }
        double tauxQualite = poidsTotal == 0 ? 1.0 : poidsPondere / poidsTotal;

        double confiance = Math.min(1.0, (double) moisObserves / p.getMaturiteMois());
        double facteurConfiance = p.getPlancherConfiance() + (1 - p.getPlancherConfiance()) * confiance;
        double pointsPaiement = p.getMaxPaiement() * tauxQualite * facteurConfiance;

        boolean revenuConnu = revenuAnnuel != null && revenuAnnuel.signum() > 0;
        BigDecimal revenuMensuel = revenuConnu
                ? revenuAnnuel.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP)
                : null;

        BigDecimal chargeMensuelle = chargeMensuelle(historique, contratSaisi);
        BigDecimal exposition = expositionDejaPortee(historique, echeancesParContrat);

        Double tauxEndettement = null;
        Double tauxExposition = null;
        double pointsEndettement;
        double pointsExposition;
        double malus = 0;

        if (revenuConnu) {
            tauxEndettement = chargeMensuelle.doubleValue() / revenuMensuel.doubleValue();
            tauxExposition = exposition.doubleValue() / revenuAnnuel.doubleValue();

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
        }

        double pointsAnciennete = 0;
        if (client.getDateAdhesion() != null) {
            long mois = Math.max(0, ChronoUnit.MONTHS.between(client.getDateAdhesion(), reference));
            pointsAnciennete = p.getMaxAnciennete()
                    * Math.min(1.0, (double) mois / p.getAncienneteMoisPlein());
        }

        long recents = historique.stream()
                .filter(c -> {
                    long m = ChronoUnit.MONTHS.between(c.getDateDemande(), reference);
                    return m >= 0 && m <= p.getFenetreContratRecentMois();
                })
                .count();
        long demandesDormantes = historique.stream()
                .filter(c -> c.getPhaseDemande() == PhaseDemande.DEMANDE_EN_COURS)
                .filter(c -> ChronoUnit.DAYS.between(c.getDateDemande(), reference) > p.getSeuilDemandeEnAttenteJours())
                .count();
        double pointsNouveaux = Math.max(0, p.getMaxNouveaux()
                - recents * p.getMalusParContratRecent()
                - demandesDormantes * p.getMalusParDemandeEnAttente());

        long types = historique.stream().map(Contrat::getTypeContrat).distinct().count();
        double pointsMixite = p.getMaxMixite() * (types >= 3 ? 1.0
                : types == 2 ? p.getMixiteRatioDeuxTypes()
                : p.getMixiteRatioUnType());

        double secondaireBrut = pointsEndettement + pointsExposition + pointsAnciennete
                              + pointsNouveaux + pointsMixite;
        double plafond = p.maxSecondaire()
                * (p.getPlafondSecondaireBase()
                 + p.getPlafondSecondairePart() * (pointsPaiement / p.getMaxPaiement()));
        double secondaireRetenu = Math.min(secondaireBrut, plafond);

        double brut = p.getScoreBase() + pointsPaiement + secondaireRetenu - malus;
        int score = Math.max(p.getScoreBase(), Math.min(p.getScoreMax(), (int) Math.round(brut)));

        ScoreDetail detail = new ScoreDetail(
                arrondi(pointsPaiement), arrondi(pointsEndettement), arrondi(pointsExposition),
                arrondi(pointsAnciennete), arrondi(pointsNouveaux), arrondi(pointsMixite),
                arrondi(malus), arrondi(secondaireBrut), arrondi(plafond), arrondi(secondaireRetenu),
                echues.size(), moisObserves, arrondi(tauxQualite * 100) / 100.0,
                tauxEndettement == null ? null : arrondi(tauxEndettement * 100) / 100.0,
                tauxExposition == null ? null : arrondi(tauxExposition * 100) / 100.0);

        GrilleScore grille = grilleScorePour(score);
        return ScoreResult.calcule(score, grille.getIntervalle(), grille.getCategorieRisque(),
                grille.getCouleur(), detail);
    }

    private int moisObserves(List<Contrat> historique, LocalDate reference) {
        Set<YearMonth> mois = new HashSet<>();
        for (Contrat c : historique) {
            if (c.getDateDebutContrat() == null) continue;   
            LocalDate fin = c.getDateFinContrat() != null && c.getDateFinContrat().isBefore(reference)
                    ? c.getDateFinContrat() : reference;
            YearMonth courant = YearMonth.from(c.getDateDebutContrat());
            YearMonth borne = YearMonth.from(fin);
            while (!courant.isAfter(borne)) {
                mois.add(courant);
                courant = courant.plusMonths(1);
            }
        }
        return mois.size();
    }

    private double poidsAnciennete(LocalDate dateEcheance, LocalDate reference) {
        long mois = ChronoUnit.MONTHS.between(dateEcheance, reference);
        if (mois <= 12) return p.getPoidsRecent();
        if (mois <= 36) return p.getPoidsMoyen();
        return p.getPoidsAncien();
    }

    private double montant(BigDecimal m) {
        return m == null ? 1.0 : Math.max(1.0, m.doubleValue());
    }

    private double qualite(Echeance e, LocalDate reference) {
        StatutEcheance statut = e.statutEffectif(reference);
        if (statut == StatutEcheance.PAYE_A_TEMPS) return p.getQualitePayeATemps();
        if (statut == StatutEcheance.IMPAYE) return p.getQualiteImpaye();
        if (!e.estPayee()) return p.getQualiteImpaye();
        return e.joursDeRetard(reference) <= Echeance.SEUIL_IMPAYE_JOURS
                ? p.getQualiteRetardCourt()
                : p.getQualiteRetardLong();
    }

    private BigDecimal chargeMensuelle(List<Contrat> historique, Contrat contratSaisi) {
        BigDecimal total = historique.stream()
                .filter(c -> c.getPhaseDemande() == PhaseDemande.ACTIF)
                .filter(c -> c.getRoleClient() != RoleClient.GARANT)
                .map(this::mensualite)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (contratSaisi != null) {
            total = total.add(mensualite(contratSaisi));
        }
        return total;
    }

    private BigDecimal mensualite(Contrat c) {
        if (c.getMontantEcheanceMensuelle() != null && c.getMontantEcheanceMensuelle().signum() > 0) {
            return c.getMontantEcheanceMensuelle()
                    .divide(BigDecimal.valueOf(Math.max(1, c.pasEnMois())), 2, RoundingMode.HALF_UP);
        }
        int nb = c.getNombreTotalEcheances() == null ? 1 : Math.max(1, c.getNombreTotalEcheances());
        int dureeMois = Math.max(1, nb * c.pasEnMois());
        return c.totalDu().divide(BigDecimal.valueOf(dureeMois), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal expositionDejaPortee(List<Contrat> historique,
                                            Map<Long, List<Echeance>> echeances) {
        BigDecimal total = BigDecimal.ZERO;
        for (Contrat c : historique) {
            if (c.getRoleClient() == RoleClient.GARANT) continue;
            if (c.getPhaseDemande() == PhaseDemande.ACTIF) {
                total = total.add(echeances.getOrDefault(c.getId(), List.of()).stream()
                        .filter(e -> !e.estPayee())
                        .map(Echeance::getMontantDu)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));
            } else if (c.getPhaseDemande() == PhaseDemande.DEMANDE_EN_COURS) {
                total = total.add(c.totalDu());
            }
        }
        return total;
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

    private double arrondi(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}