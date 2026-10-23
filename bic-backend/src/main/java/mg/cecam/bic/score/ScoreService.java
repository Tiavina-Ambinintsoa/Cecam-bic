package mg.cecam.bic.score;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.client.Client;
import mg.cecam.bic.client.Emploi;
import mg.cecam.bic.client.EmploiRepository;
import mg.cecam.bic.client.EvenementNegatifRepository;
import mg.cecam.bic.common.enums.BucketRetard;
import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.RoleClient;
import mg.cecam.bic.contrat.*;
import mg.cecam.bic.referentiel.GrilleScore;
import mg.cecam.bic.referentiel.GrilleScoreRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ScoreService {

    private final GrilleScoreRepository grilleScoreRepository;
    private final ContratRepository contratRepository;
    private final ContratLienRepository contratLienRepository;
    private final EcheanceRepository echeanceRepository;
    private final HistoriquePaiementMensuelRepository historiquePaiementMensuelRepository;
    private final EmploiRepository emploiRepository;
    private final RevenuDemandeRepository revenuDemandeRepository;
    private final EvenementNegatifRepository evenementNegatifRepository;
    private final DernierContratRepository dernierContratRepository;
    private final ScoreProperties p;

    /** Observation de paiement normalisée : un mois (ou une échéance), un retard en jours, un poids unitaire. */
    private record Observation(LocalDate date, int jours, int nombre) {}
    private record Capacite(Double chargeAnnuelle, Double exposition) {}
    private record Plafond(int valeur, String motif) {
        String affichage() { return valeur + "|" + motif; }
    }

    private enum StatutDernierContrat { A_TEMPS, ANTICIPATION, EN_RETARD, NON_REMBOURSE }

    public ScoreResult calculer(Client client, Contrat contratEnCours, boolean clientTrouve) {
        return calculer(client, contratEnCours, clientTrouve, LocalDate.now(ZoneId.of("Indian/Antananarivo")));
    }

    public ScoreResult calculer(Client client, Contrat contratEnCours, boolean clientTrouve, LocalDate reference) {
        LocalDate ref = Objects.requireNonNull(reference, "La date de référence du score est obligatoire.");
        Long enCoursId = contratEnCours == null ? null : contratEnCours.getId();
        List<String> avertissements = new ArrayList<>();
        List<Plafond> plafonds = new ArrayList<>();
        Map<String, Double> gagne = new LinkedHashMap<>();
        Map<String, Double> dispo = new LinkedHashMap<>();

        List<Contrat> historique = contratRepository.findPourClient(client.getId()).stream()
                .filter(c -> !Objects.equals(c.getId(), enCoursId)).toList();

        // ---- Événements négatifs formels (ne bloquent plus le calcul : ils plafonnent le score)
        boolean evenementNegatif = !evenementNegatifRepository.findByClient_IdAndActifTrue(client.getId()).isEmpty()
                || historique.stream().anyMatch(c -> pireStatutActif(c, ref))
                || pireStatutActif(contratEnCours, ref);

        if (historique.stream().anyMatch(c -> pireStatutFormel(c.getPireStatut()) && !pireStatutActif(c, ref))) {
            avertissements.add("Ancien événement contentieux régularisé ou prescrit (> 36 ou 60 mois) : plafond 399 levé.");
        }

        List<Contrat> empruntsDecaisses = historique.stream().filter(this::estEmprunteur)
                .filter(c -> c.getPhaseDemande() == PhaseDemande.ACTIF || c.getPhaseDemande() == PhaseDemande.FERME)
                .toList();
        List<Contrat> actifs = contratsActifs(historique, contratEnCours);

        // ---- Observations de paiement (mensuelles en priorité, échéances datées pour les autres mois)
        List<Observation> observations = new ArrayList<>();
        Map<Long, List<Echeance>> echeancesParContrat = new HashMap<>();
        int retardCourant = 0;
        for (Contrat contrat : empruntsDecaisses) {
            List<Echeance> echeances = echeanceRepository.findByContrat_IdOrderByNumeroEcheance(contrat.getId());
            echeancesParContrat.put(contrat.getId(), echeances);
            Map<YearMonth, Integer> observationParMois = new HashMap<>();
            List<HistoriquePaiementMensuel> mensuel = historiquePaiementMensuelRepository
                    .findByContrat_IdOrderByMoisDesc(contrat.getId()).stream()
                    .filter(h -> h.getMois() != null && !h.getMois().isAfter(ref)).toList();
            for (HistoriquePaiementMensuel h : mensuel) {
                Integer jours = joursObserves(h);
                if (jours == null) {
                    if (!statutNonObservable(h.getStatut()))
                        avertissements.add("Une observation mensuelle source n'est pas exploitable et a été ignorée.");
                    continue;
                }
                if (statutImprecis(h.getStatut())) {
                    avertissements.add("Observation mensuelle source imprécise (« IMPAYÉ » ou « 30+ ») évaluée à la borne basse de 30 jours.");
                }
                observations.add(new Observation(h.getMois(), jours, nombreEcheances(h)));
                observationParMois.put(YearMonth.from(h.getMois()), observations.size() - 1);
            }
            boolean echeancierEchuDisponible = echeances.stream()
                    .anyMatch(e -> e.getDateEcheance() != null && !e.getDateEcheance().isAfter(ref));
            for (Echeance e : echeances) {
                if (e.getDateEcheance() == null || e.getDateEcheance().isAfter(ref)) continue;
                int jours = Math.max(0, e.joursDeRetard(ref));
                Integer indexObservation = observationParMois.get(YearMonth.from(e.getDateEcheance()));
                if (indexObservation != null) {
                    Observation observation = observations.get(indexObservation);
                    observations.set(indexObservation, new Observation(observation.date(),
                            Math.max(observation.jours(), jours), observation.nombre()));
                } else {
                    observations.add(new Observation(e.getDateEcheance(), jours, 1));
                }
                if (contrat.getPhaseDemande() == PhaseDemande.ACTIF && !e.estPayee() && jours > 0) {
                    retardCourant = Math.max(retardCourant, jours);
                }
            }
            if (contrat.getPhaseDemande() == PhaseDemande.ACTIF && !echeancierEchuDisponible) {
                retardCourant = Math.max(retardCourant, retardCourantSource(contrat, mensuel, ref));
            }
        }

        // ---- 3 derniers contrats déclarés (historique déclaré sur la demande)
        List<DernierContrat> derniersContrats = (contratEnCours == null || contratEnCours.getId() == null)
                ? List.of()
                : dernierContratRepository.findByContrat_IdOrderByDateOctroiAsc(contratEnCours.getId());

        int bonusAnticipation = 0;
        int malusRetardHistorique = 0;
        boolean creditNonRembourse = false;
        boolean aHistoriqueReel = !observations.isEmpty();

        // Une valeur inconnue n'est jamais assimilée à « à temps ».
        for (DernierContrat dc : derniersContrats) {
            StatutDernierContrat statut = statutDernierContrat(dc.getStatut());
            if (statut == null) {
                avertissements.add("Un contrat déclaré a un statut absent ou inconnu ; il est exclu de l'historique de paiement.");
                continue;
            }
            if (statut == StatutDernierContrat.NON_REMBOURSE) {
                creditNonRembourse = true;
                continue;
            }

            if (!aHistoriqueReel) {
                // Nouveau client : les 3 derniers contrats servent d'historique de substitution.
                LocalDate dateRefObs = dc.getDateRemboursement() != null ? dc.getDateRemboursement()
                        : dc.getDatePrevueRemboursement();
                if (dateRefObs == null || dateRefObs.isAfter(ref)) {
                    avertissements.add("Un contrat déclaré sans date de remboursement exploitable est exclu de l'historique.");
                    continue;
                }
                // Tolérance : pas de pénalité si jours_retard <= toleranceRetardJours
                int joursRaw = dc.getJoursRetard() == null ? 0 : dc.getJoursRetard();
                int retard = Math.max(0, joursRaw <= p.getToleranceRetardJours() ? 0 : joursRaw);
                if (statut == StatutDernierContrat.EN_RETARD) retard = Math.max(p.getToleranceRetardJours() + 1, retard);
                // Bonus anticipation : limité à bonusAnticipationParContrat pts, plafonné à bonusAnticipationMax
                if (statut == StatutDernierContrat.ANTICIPATION) bonusAnticipation += p.getBonusAnticipationParContrat();
                // Malus retard uniquement si EN_RETARD réel
                if (statut == StatutDernierContrat.EN_RETARD) malusRetardHistorique += p.getMalusRetardDeclareParContrat();
                observations.add(new Observation(dateRefObs, retard, 1));
            } else {
                // Client avec historique réel : les 3 derniers contrats donnent seulement un léger malus
                if (statut == StatutDernierContrat.EN_RETARD)
                    malusRetardHistorique += p.getMalusRetardDeclareAvecHistorique();
            }
        }

        if (creditNonRembourse) {
            plafonds.add(plafond(p.getPlafondCreditNonRembourse(), "crédit antérieur déclaré non remboursé"));
            avertissements.add("Historique : au moins un crédit antérieur déclaré non remboursé à échéance.");
        }

        // ---- A. Paiement
        boolean aHistorique = !observations.isEmpty();
        if (aHistorique) {
            double poidsTotal = 0, poidsPondere = 0;
            long nbRecentes = 0;
            for (Observation o : observations) {
                double w = poidsAnciennete(o.date(), ref) * o.nombre();
                poidsTotal += w;
                poidsPondere += w * qualite(o.jours());
                long mois = ChronoUnit.MONTHS.between(o.date(), ref);
                if (mois >= 0 && mois < p.getFenetreConfianceMois()) nbRecentes += o.nombre();
            }

            // Le résumé d'un prêt antérieur du dossier CECAM n'est pas un échéancier détaillé.
            if (aHistoriqueReel && nbRecentes < 6) {
                plafonds.add(plafond(p.getPlafondClasseC(), "moins de 6 observations de paiement réelles récentes (classe C max)"));
                avertissements.add("Historique détaillé récent limité (< 6 observations) : score plafonné en classe C.");
            } else if (aHistoriqueReel && nbRecentes < 12) {
                plafonds.add(plafond(p.getPlafondClasseB(), "moins de 12 observations de paiement réelles récentes (classe B max)"));
                avertissements.add("Historique détaillé récent intermédiaire (< 12 observations) : score plafonné en classe B.");
            }

            // P0.2 — Pire retard sur 24 mois
            int pireRetard24Mois = observations.stream()
                    .filter(o -> ChronoUnit.MONTHS.between(o.date(), ref) <= 24)
                    .mapToInt(Observation::jours)
                    .max().orElse(0);
            if (pireRetard24Mois >= 90) {
                plafonds.add(plafond(p.getPlafondClasseC(), "pire retard sur 24 mois >= 90 jours (classe C max)"));
                avertissements.add("Pire retard sur 24 mois >= 90 jours : score plafonné en classe C.");
            } else if (pireRetard24Mois >= 60) {
                plafonds.add(plafond(p.getPlafondClasseB(), "pire retard sur 24 mois >= 60 jours (classe B max)"));
                avertissements.add("Pire retard sur 24 mois 60-89 jours : score plafonné en classe B.");
            }

            double facteur;
            if (!aHistoriqueReel && !derniersContrats.isEmpty()) {
                double confiance = Math.min(1.0,
                        (double) nbRecentes / Math.max(1, p.getObservationsPleineConfiance()));
                facteur = p.getPlancherConfiance() + (1 - p.getPlancherConfiance()) * confiance;
                avertissements.add("Historique résumé du dossier CECAM : peu d'observations pour mesurer la régularité de remboursement.");
            } else {
                double confiance = Math.min(1.0, (double) nbRecentes / Math.max(1, p.getObservationsPleineConfiance()));
                facteur = p.getPlancherConfiance() + (1 - p.getPlancherConfiance()) * confiance;
                if (confiance < 1.0) avertissements.add("Historique de remboursement court (" + nbRecentes
                        + " échéance(s) observée(s) sur " + p.getFenetreConfianceMois() + " mois) : score à interpréter avec prudence.");
            }

            gagne.put("paiement", p.getPoidsPaiement() * (poidsPondere / poidsTotal) * Math.min(1.0, facteur));
            dispo.put("paiement", p.getPoidsPaiement());

            // ---- B. Arriérés en cours : uniquement si le client a des crédits actifs suivis
            if (!actifs.isEmpty()) {
                double ratioArr = retardCourant <= 0 ? 1.0
                        : retardCourant <= p.getToleranceRetardJours() ? 1.0
                        : retardCourant < 30 ? p.getRatioArrieres1a29()
                                - (p.getRatioArrieres1a29() - p.getRatioArrieres30a59())
                                * ((double) (retardCourant - p.getToleranceRetardJours()) / (30 - p.getToleranceRetardJours()))
                        : retardCourant < 60 ? p.getRatioArrieres30a59() : 0.0;
                gagne.put("arrieres_en_cours", p.getPoidsArrieresEnCours() * ratioArr);
                dispo.put("arrieres_en_cours", p.getPoidsArrieresEnCours());
                if (retardCourant >= 90) plafonds.add(plafond(p.getPlafondArrieres90(), "retard en cours >= 90 jours"));
                else if (retardCourant >= 60) plafonds.add(plafond(p.getPlafondArrieres60(), "retard en cours 60-89 jours"));
                else if (retardCourant >= 30) plafonds.add(plafond(p.getPlafondArrieres30(), "retard en cours 30-59 jours"));
                else if (retardCourant > p.getToleranceRetardJours()) {
                    // P0.2 — Arriéré en cours >= 1j plafonné en classe B
                    plafonds.add(plafond(p.getPlafondClasseB(), "arriéré en cours >= 1 jour (classe B max)"));
                }
                if (retardCourant > p.getToleranceRetardJours())
                    avertissements.add("Retard en cours sur un crédit actif (" + retardCourant + " jour(s) ou plus).");
            }
            // Note : si aucun crédit actif, le pilier arrieres_en_cours n'est pas évalué (pas pénalisé)
        } else {
            plafonds.add(plafond(p.getPlafondSansHistorique(), "aucun remboursement observé"));
        }

        // ---- C. Capacité financière (revenu annuel) : exclue si inconnue/incohérente, jamais « 0 point »
        Emploi emploi = emploiRepository.findByClient_Id(client.getId()).orElse(null);
        Contrat demande = (contratEnCours != null && estEmprunteur(contratEnCours)
                && contratEnCours.getPhaseDemande() == PhaseDemande.DEMANDE_EN_COURS) ? contratEnCours : null;
        RevenuEvaluation revenuEvaluation = evaluerRevenus(emploi, demande, avertissements);
        BigDecimal revenu = revenuEvaluation.montant();
        boolean capaciteRevenu = false;
        if (revenu == null) {
            avertissements.add("Revenu annuel non renseigné : endettement et exposition exclus du calcul (non pénalisés).");
        } else if (revenu.signum() <= 0) {
            avertissements.add("Revenu annuel nul ou négatif : ratios exclus du calcul.");
        } else {
            Capacite capacite = chargeEtExposition(actifs, demande, echeancesParContrat,
                    revenuEvaluation.devise(), ref, avertissements);
            if (capacite != null) {
                double rev = revenu.doubleValue();
                if (capacite.chargeAnnuelle() != null) {
                    double te = capacite.chargeAnnuelle() / rev;
                    gagne.put("endettement", interpoler(te, p.getEndettementSeuilVert(),
                            p.getEndettementSeuilRouge(), p.getPoidsEndettement()));
                    dispo.put("endettement", p.getPoidsEndettement());
                    capaciteRevenu = true;
                    if (te > 1.0) plafonds.add(plafond(p.getPlafondCharge12MoisSup100(),
                            "charge de remboursement sur 12 mois supérieure au revenu annuel"));
                    else if (te > p.getEndettementSeuilRouge()) plafonds.add(plafond(p.getPlafondCharge12MoisSup80(),
                            "charge sur 12 mois > 80 % du revenu annuel"));
                }
                if (capacite.exposition() != null) {
                    double tx = capacite.exposition() / rev;
                    gagne.put("exposition", interpoler(tx, p.getExpositionSeuilVert(),
                            p.getExpositionSeuilRouge(), p.getPoidsExposition()));
                    dispo.put("exposition", p.getPoidsExposition());
                    if (tx > p.getExpositionPlafondRatio()) plafonds.add(plafond(p.getPlafondExpositionExcessive(),
                            "encours + demande > 150 % du revenu annuel"));
                }
            }
        }

        // ---- D. Capacité budgétaire (taux d'effort mensuel et reste)
        boolean capaciteBudget = false;
        if (contratEnCours != null && contratEnCours.getMontantRecette() != null && contratEnCours.getMontantDepense() != null) {
            BigDecimal recetteBrute = contratEnCours.getMontantRecette();
            BigDecimal depenseBrute = contratEnCours.getMontantDepense();
            String perBudget = contratEnCours.getPeriodiciteBudget();
            boolean estAnnuel = perBudget != null && (perBudget.toUpperCase().contains("AN") || perBudget.toUpperCase().contains("ANNUEL"));
            double diviseur = estAnnuel ? 12.0 : 1.0;
            double recMensuelle = recetteBrute.doubleValue() / diviseur;
            double depMensuelle = depenseBrute.doubleValue() / diviseur;
            double surplusMensuel = recMensuelle - depMensuelle;

            if (!estAnnuel && recMensuelle > 15_000_000) {
                avertissements.add("Plausibilité budgétaire : recettes déclarées très élevées (> 15 M Ar/mois) ; vérifier s'il s'agit d'un montant annuel.");
            }

            double mensualiteDemande = 0;
            if (contratEnCours.getMontantEcheanceMensuelle() != null) {
                mensualiteDemande = contratEnCours.getMontantEcheanceMensuelle().doubleValue();
            } else if (contratEnCours.getMontantTotalDu() != null && contratEnCours.getNombreTotalEcheances() != null && contratEnCours.getNombreTotalEcheances() > 0) {
                mensualiteDemande = contratEnCours.getMontantTotalDu().doubleValue() / contratEnCours.getNombreTotalEcheances();
            }

            double mensualitesActives = actifs.stream()
                    .filter(c -> !Objects.equals(c.getId(), enCoursId))
                    .mapToDouble(c -> {
                        if (c.getMontantEcheanceMensuelle() != null) return c.getMontantEcheanceMensuelle().doubleValue();
                        if (c.getMontantRestantDu() != null && c.getNombreEcheancesRestantes() != null && c.getNombreEcheancesRestantes() > 0) {
                            return c.getMontantRestantDu().doubleValue() / c.getNombreEcheancesRestantes();
                        }
                        return 0.0;
                    }).sum();

            double chargeTotaleMensuelle = mensualiteDemande + mensualitesActives;
            double pts;
            if (surplusMensuel <= 0) {
                pts = 0;
                plafonds.add(plafond(p.getPlafondBudgetCritique(), "surplus budgétaire mensuel nul ou négatif"));
            } else {
                double tauxEffort = chargeTotaleMensuelle / surplusMensuel;
                if (tauxEffort <= 0.40) {
                    pts = p.getPoidsBudget();
                } else if (tauxEffort <= 0.80) {
                    pts = p.getPoidsBudget() * (1.0 - (tauxEffort - 0.40) / 0.40 * 0.65);
                } else if (tauxEffort <= 1.00) {
                    pts = p.getPoidsBudget() * 0.35 * (1.0 - (tauxEffort - 0.80) / 0.20);
                } else {
                    pts = 0;
                    plafonds.add(plafond(p.getPlafondBudgetFaible(), "taux d'effort budgétaire supérieur à 100 % du surplus disponible"));
                }
            }
            gagne.put("budget", Math.max(0.0, Math.min(p.getPoidsBudget(), pts)));
            dispo.put("budget", p.getPoidsBudget());
            capaciteBudget = true;
        } else if (contratEnCours != null && contratEnCours.getRatioRestePourcentage() != null) {
            double r = contratEnCours.getRatioRestePourcentage().doubleValue();
            double pts = r <= 0 ? 0 : r <= 25 ? 5 * r / 25 : r <= 50 ? 5 + 10 * (r - 25) / 25
                    : r < 100 ? 15 + 7 * (r - 50) / 50 : 30;
            gagne.put("budget", Math.min(1.0, pts / 30.0) * p.getPoidsBudget());
            dispo.put("budget", p.getPoidsBudget());
            capaciteBudget = true;
            if (r <= 0) plafonds.add(plafond(p.getPlafondBudgetCritique(), "recettes inférieures ou égales aux dépenses"));
            else if (r <= 25) plafonds.add(plafond(p.getPlafondBudgetFaible(), "reste budgétaire <= 25 % des dépenses"));
        } else if (demande != null) {
            avertissements.add("Recettes/dépenses non renseignées : capacité budgétaire exclue du calcul (non pénalisée).");
        }
        if (contratEnCours != null) {
            BigDecimal valGarantie = contratEnCours.getValeurGarantie();
            BigDecimal ratioGarantie = contratEnCours.getRatioCouvertureGarantie();
            if ((valGarantie == null || valGarantie.signum() <= 0) && ratioGarantie == null) {
                avertissements.add("Garantie non renseignée : la couverture doit être évaluée séparément du score de remboursement.");
            } else if (ratioGarantie != null && ratioGarantie.doubleValue() < 100) {
                avertissements.add("Garantie insuffisante (" + Math.round(ratioGarantie.doubleValue())
                        + " %) : couverture à examiner séparément du score de remboursement.");
            }
            BigDecimal ratioPatrimoine = contratEnCours.getRatioCouverturePatrimoine();
            if (ratioPatrimoine != null && ratioPatrimoine.doubleValue() <= 120) {
                avertissements.add("Couverture patrimoniale limitée (" + Math.round(ratioPatrimoine.doubleValue())
                        + " %) : à examiner séparément du score de remboursement.");
            }
        }

        boolean preuveCapacite = capaciteRevenu || capaciteBudget;
        if (!aHistorique && !preuveCapacite) {
            String message = clientTrouve
                    ? "Aucun remboursement observé, ni revenu, ni budget : score non calculable."
                    : "Ce client vient d'être créé et ne possède ni historique de remboursement ni données de capacité.";
            if (!avertissements.isEmpty()) message += " " + String.join(" ", avertissements);
            return ScoreResult.nonCalculable(message);
        }
        if (!preuveCapacite) plafonds.add(plafond(p.getPlafondSansCapacite(), "capacité de remboursement non vérifiable (ni revenu, ni budget)"));

        // ---- Score brut renormalisé + ajustements (anticipation, retards déclarés, risque groupe)
        double obtenu = gagne.values().stream().mapToDouble(Double::doubleValue).sum();
        double possible = dispo.values().stream().mapToDouble(Double::doubleValue).sum();
        double malusGroupe = Math.min(p.getMalusCoObligesMax(),
                p.getMalusParCoObligeARisque() * coObligesARisque(client, actifs));
        if (malusGroupe > 0) avertissements.add("Co-titulaire(s) d'un crédit de groupe en situation négative.");

        int bonusAnticipationEffectif = Math.min(p.getBonusAnticipationMax(), bonusAnticipation);
        if (bonusAnticipationEffectif > 0) {
            avertissements.add("Bonus d'anticipation : remboursement(s) en avance sur les crédits précédents (+" + bonusAnticipationEffectif + " pts).");
        }
        if (malusRetardHistorique > 0) {
            avertissements.add("Malus de retard : retard(s) de remboursement constaté(s) sur les crédits précédents (-" + malusRetardHistorique + " pts).");
        }

        double brut = p.getScoreBase() + (p.getScoreMax() - p.getScoreBase()) * (possible <= 0 ? 0 : obtenu / possible)
                + bonusAnticipationEffectif - malusRetardHistorique - malusGroupe;

        // P0.2 — Plafond si moins de 50 % des facteurs renseignés (classe C max)
        boolean partiel = !dispo.containsKey("endettement") || !dispo.containsKey("exposition")
                || !dispo.containsKey("paiement") || !capaciteBudget;
        double pointsPotentiels = p.getPoidsPaiement() + p.getPoidsArrieresEnCours()
                + p.getPoidsEndettement() + p.getPoidsExposition() + p.getPoidsBudget();
        if (partiel && pointsPotentiels > 0) {
            int couvertureFacteurs = (int) Math.round(possible / pointsPotentiels * 100);
            if (couvertureFacteurs < 50) {
                plafonds.add(plafond(p.getPlafondClasseC(), "moins de 50 % des facteurs renseignés (classe C max)"));
                avertissements.add("Moins de 50 % des facteurs renseignés (" + couvertureFacteurs + " %) : score plafonné en classe C.");
            } else {
                avertissements.add("Couverture des facteurs de score : " + couvertureFacteurs
                        + " % des facteurs configurés sont renseignés ; ce taux ne mesure pas leur vérification.");
            }
        }

        // ---- Plafonds, règle « mauvais »
        boolean mauvais = evenementNegatif || retardCourant >= 180;
        if (mauvais) plafonds.add(plafond(p.getPlafondBandeTresEleve(), "événement négatif formel ou retard en cours >= 180 jours"));
        double plafondMin = plafonds.stream().mapToInt(Plafond::valeur).min().orElse(p.getScoreMax());
        int score = (int) Math.round(Math.max(p.getScoreBase(), Math.min(p.getScoreMax(), Math.min(brut, plafondMin))));

        GrilleScore grille = grilleScorePour(score);
        // « Mauvais » seulement dans la bande très élevée (garanti par le plafond ci-dessus ; vérifié par prudence).
        if (mauvais && score > p.getPlafondBandeTresEleve()) mauvais = false;
        if (mauvais) avertissements.add("Risque très élevé : événement négatif formel ou retard >= 180 jours.");

        Map<String, Double> composantes = new LinkedHashMap<>();
        gagne.forEach((k, v) -> composantes.put(k, Math.round(v * 10) / 10.0));
        if (bonusAnticipationEffectif > 0) composantes.put("bonus_anticipation", (double) bonusAnticipationEffectif);
        if (malusRetardHistorique > 0) composantes.put("malus_retard_historique", (double) -malusRetardHistorique);
        composantes.put("points_disponibles", Math.round(possible * 10) / 10.0);
        composantes.put("malus_groupe", malusGroupe);
        composantes.put("score_avant_plafonds", Math.round(brut * 10) / 10.0);

        return ScoreResult.calcule(score, grille.getIntervalle(), grille.getCategorieRisque(), grille.getCouleur(),
                avertissements.isEmpty() ? null : String.join(" ", avertissements), mauvais, partiel,
                plafonds.stream().map(Plafond::affichage).toList(), composantes);
    }

    // ------------------------------------------------------------------ outils

    private Plafond plafond(int valeur, String motif) { return new Plafond(valeur, motif); }

    private boolean estEmprunteur(Contrat c) { return c.getRoleClient() != RoleClient.GARANT; }

    static boolean pireStatutFormel(String statut) {
        if (statut == null) return false;
        String s = Normalizer.normalize(statut, Normalizer.Form.NFD).replaceAll("\\p{M}+", "")
                .toUpperCase(Locale.ROOT);
        return s.contains("CONTENTIEUX") || s.contains("FAILLITE") || s.contains("DECHEANCE");
    }

    private boolean pireStatutActif(Contrat c, LocalDate ref) {
        if (c == null || !pireStatutFormel(c.getPireStatut())) return false;
        if (c.getDatePireStatut() != null) {
            long mois = ChronoUnit.MONTHS.between(c.getDatePireStatut(), ref);
            if (c.getPhaseDemande() == PhaseDemande.FERME && (c.getMontantRestantDu() == null || c.getMontantRestantDu().signum() <= 0)) {
                if (mois > 36) return false; // prescription après 3 ans si régularisé et fermé
            }
            if (mois > 60) return false; // prescription légale de 5 ans
        }
        return true;
    }

    public static String normaliserStatutDernierContrat(String statut) {
        if (statut == null || statut.isBlank()) return null;
        String normalise = Normalizer.normalize(statut, Normalizer.Form.NFD).replaceAll("\\p{M}+", "")
                .toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+", "_").replaceAll("^_+|_+$", "");
        return switch (normalise) {
            case "NORMAL", "A_TEMPS", "PAYE_A_TEMPS", "PAYE", "OK" -> StatutDernierContrat.A_TEMPS.name();
            case "ANTICIPATION", "EN_AVANCE" -> StatutDernierContrat.ANTICIPATION.name();
            case "EN_RETARD", "RETARD" -> StatutDernierContrat.EN_RETARD.name();
            case "NON_REMBOURSE", "IMPAYE" -> StatutDernierContrat.NON_REMBOURSE.name();
            default -> null;
        };
    }

    private StatutDernierContrat statutDernierContrat(String statut) {
        String normalise = normaliserStatutDernierContrat(statut);
        return normalise == null ? null : StatutDernierContrat.valueOf(normalise);
    }

    private List<Contrat> contratsActifs(List<Contrat> historique, Contrat enCours) {
        List<Contrat> l = new ArrayList<>(historique.stream().filter(this::estEmprunteur)
                .filter(c -> c.getPhaseDemande() == PhaseDemande.ACTIF).toList());
        if (enCours != null && estEmprunteur(enCours) && enCours.getPhaseDemande() == PhaseDemande.ACTIF
                && l.stream().noneMatch(c -> Objects.equals(c.getId(), enCours.getId()))) l.add(enCours);
        return l;
    }

    /** {charge de remboursement sur 12 mois, exposition totale} avec la demande évaluée (pro-forma), ou null si incomplet. */
    private Capacite chargeEtExposition(List<Contrat> actifs, Contrat demande, Map<Long, List<Echeance>> echeances,
                                        String deviseRevenu, LocalDate reference, List<String> avertissements) {
        if (deviseRevenu == null) { avertissements.add("Devise du revenu absente : ratios exclus."); return null; }
        double charge = 0, expo = 0;
        boolean chargeComplete = true;
        boolean expositionComplete = true;
        for (Contrat c : actifs) {
            String d = normaliserDevise(c.getDevise());
            if (!deviseRevenu.equals(d)) { avertissements.add("Devises différentes ou absentes : ratios exclus (aucune conversion inventée)."); return null; }
            BigDecimal reste = c.getMontantRestantDu();
            List<Echeance> echeancesContrat = echeances.computeIfAbsent(c.getId(),
                    id -> echeanceRepository.findByContrat_IdOrderByNumeroEcheance(id));
            if (reste == null) {
                if (echeancesContrat.isEmpty()) {
                    avertissements.add("Montant restant dû d'un crédit actif inconnu : capacité partiellement exclue.");
                    chargeComplete = false;
                    expositionComplete = false;
                    continue;
                }
                reste = echeancesContrat.stream().map(x -> x.getMontantDu().subtract(x.getMontantPaye() == null ? BigDecimal.ZERO : x.getMontantPaye()).max(BigDecimal.ZERO))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            }
            BigDecimal chargeAnnuelle = chargeAnnuelle(c, reste, echeancesContrat, reference);
            if (chargeAnnuelle == null) {
                avertissements.add("Périodicité ou échéance d'un crédit actif inconnue : endettement exclu.");
                chargeComplete = false;
            } else charge += chargeAnnuelle.doubleValue();
            expo += reste.doubleValue();
        }
        if (demande != null) {
            String d = normaliserDevise(demande.getDevise());
            if (!deviseRevenu.equals(d)) { avertissements.add("Devise de la demande différente du revenu : ratios exclus."); return null; }
            BigDecimal total = demande.getMontantTotalDemandeInteret();
            if (total == null || total.signum() <= 0) total = demande.getMontantTotalDu();
            if ((total == null || total.signum() <= 0) && demande.getMontantFinance() != null) {
                BigDecimal taux = demande.getTauxInteretMensuel();
                int pasMois = Contrat.moisEntreEcheances(demande.getPeriodicitePaiement());
                int nombreEcheances = demande.getNombreTotalEcheances() == null || demande.getNombreTotalEcheances() <= 0
                        ? 1 : demande.getNombreTotalEcheances();
                double facteurInterets = taux == null || pasMois == 0
                        ? 1.0 : 1.0 + taux.doubleValue() / 100.0 * pasMois * nombreEcheances;
                total = demande.getMontantFinance().multiply(BigDecimal.valueOf(facteurInterets));
                if (taux == null) avertissements.add("Taux d'intérêt absent : l'exposition estimée exclut les intérêts.");
            }
            if (total == null || total.signum() <= 0) {
                avertissements.add("Montant de la demande inconnu : capacité de la demande exclue.");
                chargeComplete = false;
                expositionComplete = false;
                return new Capacite(chargeComplete ? charge : null, expositionComplete ? expo : null);
            }
            BigDecimal chargeAnnuelleDemande = chargeAnnuelle(demande, total, List.of(), reference);
            if (chargeAnnuelleDemande == null) {
                avertissements.add("Périodicité de la demande inconnue : endettement exclu plutôt que supposé mensuel.");
                chargeComplete = false;
            } else charge += chargeAnnuelleDemande.doubleValue();
            double encoursInterne = expo;
            expo += total.doubleValue();
            BigDecimal creditsActifs = demande.getTotalCreditActif();
            if (creditsActifs == null) {
                creditsActifs = BigDecimal.ZERO;
                if (demande.getCreditActifDemandeur() != null) creditsActifs = creditsActifs.add(demande.getCreditActifDemandeur());
                if (demande.getCreditActifGarant() != null) creditsActifs = creditsActifs.add(demande.getCreditActifGarant());
                if (demande.getCreditActifFiance() != null) creditsActifs = creditsActifs.add(demande.getCreditActifFiance());
            }
            // P1.1 fix : l'exposition finale = max(encours interne, crédits actifs déclarés) + montant demandé
            expo = Math.max(encoursInterne, creditsActifs.doubleValue()) + total.doubleValue();
            if (creditsActifs.doubleValue() > encoursInterne) {
                avertissements.add("Des crédits actifs déclarés augmentent l'exposition, mais leurs échéances externes ne sont pas vérifiées.");
            }
        }
        return new Capacite(chargeComplete ? charge : null, expositionComplete ? expo : null);
    }

    private RevenuEvaluation evaluerRevenus(Emploi emploi, Contrat demande, List<String> avertissements) {
        BigDecimal total = (emploi != null && emploi.getRevenuAnnuelTotal() != null && emploi.getRevenuAnnuelTotal().signum() > 0)
                ? emploi.getRevenuAnnuelTotal() : null;
        String devise = emploi == null ? null : normaliserDevise(emploi.getDevise());
        List<RevenuDemande> revenus = demande == null
                ? List.of() : revenuDemandeRepository.findByContrat_Id(demande.getId());
        if (total == null && !revenus.isEmpty()) {
            RevenuDemande premier = revenus.stream()
                    .filter(r -> r.getMontantAnnuel() != null && r.getMontantAnnuel().signum() > 0)
                    .filter(r -> normaliserDevise(r.getDevise()) != null)
                    .findFirst().orElse(null);
            if (premier != null) {
                total = BigDecimal.ZERO;
                devise = normaliserDevise(premier.getDevise());
            }
        }
        for (RevenuDemande revenu : revenus) {
            String deviseSource = normaliserDevise(revenu.getDevise());
            if (revenu.getMontantAnnuel() == null || revenu.getMontantAnnuel().signum() <= 0) {
                avertissements.add("Un revenu complémentaire nul ou négatif est exclu du calcul.");
                continue;
            }
            if (devise == null || deviseSource == null || !devise.equals(deviseSource)) {
                avertissements.add("Un revenu complémentaire est exclu : devise absente ou différente (aucune conversion inventée).");
                continue;
            }
            // Règle anti-doublon : si le client a déjà un revenu d'emploi principal et que ce revenu complémentaire y correspond
            if (emploi != null && emploi.getRevenuAnnuelTotal() != null && emploi.getRevenuAnnuelTotal().signum() > 0) {
                String desc = (revenu.getDescription() != null ? revenu.getDescription() : "") + " "
                        + (revenu.getTypeRevenu() != null ? revenu.getTypeRevenu() : "");
                String normDesc = Normalizer.normalize(desc, Normalizer.Form.NFD).replaceAll("\\p{M}+", "").toUpperCase(Locale.ROOT);
                if (normDesc.contains("SALAIRE PRINCIPAL") || normDesc.contains("EMPLOI PRINCIPAL")
                        || (normDesc.contains("SALAIRE") && emploi.getRevenuAnnuelTotal().compareTo(revenu.getMontantAnnuel()) == 0)) {
                    avertissements.add("Revenu complémentaire assimilé au salaire principal ignoré pour éviter un double comptage.");
                    continue;
                }
            }
            total = (total == null ? BigDecimal.ZERO : total).add(revenu.getMontantAnnuel());
        }
        return new RevenuEvaluation(total, devise);
    }

    private record RevenuEvaluation(BigDecimal montant, String devise) {}

    static BigDecimal chargeAnnuelle(Contrat contrat, BigDecimal reste, List<Echeance> echeances, LocalDate reference) {
        if (reste != null && reste.signum() <= 0) return BigDecimal.ZERO;
        if (echeances.stream().anyMatch(e -> e.getDateEcheance() != null)) {
            BigDecimal totalDouzeMois = echeances.stream()
                    .filter(e -> e.getDateEcheance() != null && !e.getDateEcheance().isAfter(reference.plusMonths(12)))
                    .map(e -> e.getMontantDu().subtract(e.getMontantPaye() == null ? BigDecimal.ZERO : e.getMontantPaye())
                            .max(BigDecimal.ZERO))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            return totalDouzeMois.min(reste);
        }
        int pasMois = Contrat.moisEntreEcheances(contrat.getPeriodicitePaiement());
        if (pasMois == 0 && contrat.getPeriodicitePaiement() == null
                && contrat.getMontantEcheanceMensuelle() != null) pasMois = 1;
        int nombreEcheances = contrat.getNombreEcheancesRestantes() != null && contrat.getNombreEcheancesRestantes() > 0
                ? contrat.getNombreEcheancesRestantes()
                : contrat.getNombreTotalEcheances() == null ? 0 : contrat.getNombreTotalEcheances();
        if (pasMois == 0 || nombreEcheances <= 0 || reste == null || reste.signum() <= 0) return null;
        BigDecimal parEcheance = contrat.getMontantEcheanceMensuelle();
        if (parEcheance == null || parEcheance.signum() <= 0) {
            parEcheance = reste.divide(BigDecimal.valueOf(nombreEcheances), 8, RoundingMode.HALF_UP);
        }
        return parEcheance.multiply(BigDecimal.valueOf(Math.min(nombreEcheances, 12.0 / pasMois)))
                .min(reste);
    }

    /** Retard actuellement déclaré par la source (dernier mois observé, sinon champ du contrat). */
    private int retardCourantSource(Contrat c, List<HistoriquePaiementMensuel> mensuel, LocalDate ref) {
        Optional<HistoriquePaiementMensuel> dernier = mensuel.stream().max(Comparator.comparing(HistoriquePaiementMensuel::getMois));
        if (dernier.isPresent() && ChronoUnit.MONTHS.between(dernier.get().getMois(), ref) <= 2) {
            Integer j = joursObserves(dernier.get());
            if (j != null) return j;
        }
        if (c.getNombreJoursRetard() != null && c.getNombreEcheancesImpayees() != null && c.getNombreEcheancesImpayees() > 0) {
            return Math.max(0, c.getNombreJoursRetard());
        }
        return 0;
    }

    /** Co-titulaires (présents en base) d'un crédit actif de groupe qui sont « mauvais » ou en retard >= 30 j. */
    private int coObligesARisque(Client client, List<Contrat> actifs) {
        List<Long> ids = actifs.stream().filter(c -> c.getRoleClient() == RoleClient.CO_TITULAIRE || c.getRoleClient() == RoleClient.TITULAIRE)
                .map(Contrat::getId).toList();
        if (ids.isEmpty()) return 0;
        Set<Long> risque = new HashSet<>();
        for (ContratLien l : contratLienRepository.findByContrat_IdIn(ids)) {
            Client lie = l.getClientLie();
            if (lie == null || Objects.equals(lie.getId(), client.getId())) continue;
            boolean negatif = !evenementNegatifRepository.findByClient_IdAndActifTrue(lie.getId()).isEmpty();
            boolean retard = contratRepository.findByClient_Id(lie.getId()).stream()
                    .anyMatch(c -> c.getPhaseDemande() == PhaseDemande.ACTIF && c.getNombreJoursRetard() != null && c.getNombreJoursRetard() >= 30);
            if (negatif || retard) risque.add(lie.getId());
        }
        return risque.size();
    }

    static boolean statutImprecis(String statut) {
        if (statut == null || statut.isBlank()) return false;
        String s = Normalizer.normalize(statut, Normalizer.Form.NFD).replaceAll("\\p{M}+", "").toUpperCase(Locale.ROOT);
        return (s.contains("IMPAYE") || s.contains("30+")) && BucketRetard.depuisStatut(statut) == null;
    }

    /** Jours de retard d'une observation mensuelle ; null si non exploitable (pas d'invention de bucket). */
    static Integer joursObserves(HistoriquePaiementMensuel h) {
        BucketRetard b = BucketRetard.depuisStatut(h.getStatut());
        if (h.getJoursDeRetard() != null) {
            int jours = Math.max(0, h.getJoursDeRetard());
            if (b != null) return Math.max(jours, borneBasse(b));
            String statut = normaliserStatutDernierContrat(h.getStatut());
            if (jours == 0 && !statutNonObservable(h.getStatut())
                    && !StatutDernierContrat.ANTICIPATION.name().equals(statut)) return null;
            return jours;
        }
        if (b != null) return borneBasse(b);
        if (statutImprecis(h.getStatut())) return 30; // Seuil impayé par défaut pour statut vague
        return null;
    }

    private static int borneBasse(BucketRetard bucket) {
        return switch (bucket) {
            case A_JOUR -> 0; case JOURS_1_29 -> 1; case JOURS_30_59 -> 30; case JOURS_60_89 -> 60;
            case JOURS_90_119 -> 90; case JOURS_120_149 -> 120; case JOURS_150_179 -> 150; case JOURS_180_PLUS -> 180;
        };
    }

    private static boolean statutNonObservable(String statut) {
        if (statut == null || statut.isBlank()) return true;
        String n = Normalizer.normalize(statut, Normalizer.Form.NFD).replaceAll("\\p{M}+", "")
                .toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+", "");
        return n.isBlank() || Set.of("TROPRECENT", "NONDISPONIBLE", "ND", "NA", "NC", "NOUV", "NDIS").contains(n);
    }

    private int nombreEcheances(HistoriquePaiementMensuel h) {
        return h.getNombreEcheances() == null ? 1 : Math.max(1, h.getNombreEcheances());
    }

    private double qualite(int jours) {
        // Tolérance de grâce : pas de pénalité en dessous du seuil
        int tolerance = p.getToleranceRetardJours();
        if (jours <= tolerance) return p.getQualitePayeATemps();
        // Décote graduelle de (tolerance+1) à 29 jours : de qualitePayeATemps à qualiteRetardCourt
        if (jours < 30) {
            double ratio = (double) (jours - tolerance) / (30 - tolerance);
            return p.getQualitePayeATemps() - (p.getQualitePayeATemps() - p.getQualiteRetardCourt()) * ratio;
        }
        // 30j : qualiteRetardLong, puis décroissance linéaire jusqu'à 0 à 180j
        if (jours >= 180) return p.getQualiteImpaye();
        return p.getQualiteRetardLong() + (p.getQualiteImpaye() - p.getQualiteRetardLong()) * ((jours - 30) / 150.0);
    }

    private double poidsAnciennete(LocalDate obs, LocalDate ref) {
        long mois = ChronoUnit.MONTHS.between(obs, ref);
        return mois <= 12 ? p.getPoidsRecent() : mois <= 36 ? p.getPoidsMoyen() : p.getPoidsAncien();
    }

    private String normaliserDevise(String devise) {
        if (devise == null || devise.isBlank()) return null;
        String n = Normalizer.normalize(devise, Normalizer.Form.NFD).replaceAll("\\p{M}+", "")
                .toUpperCase(Locale.ROOT).replaceAll("[^A-Z0-9]+", "");
        if (n.isBlank()) return null;
        return n.equals("MGA") || n.contains("ARIARY") ? "MGA" : n;
    }

    private double interpoler(double taux, double vert, double rouge, double max) {
        if (taux <= vert) return max;
        if (taux >= rouge) return 0;
        return max * (rouge - taux) / (rouge - vert);
    }

    private GrilleScore grilleScorePour(int score) {
        return grilleScoreRepository.findAll().stream()
                .filter(g -> score >= g.getScoreMin() && score <= g.getScoreMax()).findFirst()
                .orElseThrow(() -> new IllegalStateException("La grille de score ne couvre pas la valeur " + score));
    }
}