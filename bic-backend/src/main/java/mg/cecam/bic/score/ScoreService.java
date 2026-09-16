// mg/cecam/bic/score/ScoreService.java  — RÉÉCRIT
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
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Moteur de score CECAM.
 *
 * Six axes, tous alimentés : comportement de paiement, endettement,
 * exposition, ancienneté, nouveaux crédits, mixité. Plus un malus de
 * surendettement, car un ratio charge/revenu de 125 % n'est pas
 * représentable par un simple « 0 point sur un axe ».
 *
 * Trois changements de fond par rapport à la version initiale :
 *
 *  1. L'axe paiement est un RATIO de qualité, plus un cumul de « +2 ».
 *     Avant, il fallait 97 échéances propres pour saturer l'axe, ce qui
 *     était inatteignable sur des prêts de 5 à 6 mois.
 *  2. Le plafond secondaire passe de 0,40+0,60r à 0,70+0,30r. L'ancien
 *     écrêtait systématiquement, réduisant le score à une fonction du seul
 *     axe paiement (deux clients très différents sortaient au même score).
 *  3. Les statuts d'échéance sont RECALCULÉS à la date du rapport via
 *     Echeance.statutEffectif(). Le statut persisté ne vieillit pas : un
 *     client en défaut dont personne n'a mis à jour la base n'était pas
 *     pénalisé du tout.
 *
 * Toutes les données sont passées en paramètre (aucun accès repository aux
 * échéances) : c'est ce qui supprime le N+1 et garantit que le score et le
 * corps du rapport sont calculés sur le même instantané.
 */
@Service
@RequiredArgsConstructor
public class ScoreService {

    private final GrilleScoreRepository grilleScoreRepository;
    private final ScoreProperties p;

    /**
     * @param client            client noté
     * @param contratSaisi      la demande en cours de saisie (exclue de l'historique)
     * @param historique        tous les autres contrats du client
     * @param echeancesParContrat échéances préchargées, indexées par id de contrat
     * @param revenuAnnuel      revenu annuel déclaré, ou null si non renseigné
     * @param reference         date d'évaluation (= date du rapport)
     */
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

        if (echues.isEmpty()) {
            return ScoreResult.nonCalculable(
                    "Aucune échéance n'est encore arrivée à terme : le comportement de paiement "
                  + "de ce client n'est pas encore observable");
        }

        // ---------- Axe 1 : comportement de paiement ----------
        double poidsTotal = 0;
        double poidsPondere = 0;
        for (Echeance e : echues) {
            double poids = poidsAnciennete(e.getDateEcheance(), reference);
            poidsTotal += poids;
            poidsPondere += poids * qualite(e, reference);
        }
        double tauxQualite = poidsTotal == 0 ? 0 : poidsPondere / poidsTotal;
        double confiance = Math.min(1.0, (double) echues.size() / p.getMaturiteEcheances());
        double facteurConfiance = p.getPlancherConfiance() + (1 - p.getPlancherConfiance()) * confiance;
        double pointsPaiement = p.getMaxPaiement() * tauxQualite * facteurConfiance;

        // ---------- Axes financiers ----------
        boolean revenuConnu = revenuAnnuel != null && revenuAnnuel.signum() > 0;
        BigDecimal revenuMensuel = revenuConnu
                ? revenuAnnuel.divide(BigDecimal.valueOf(12), 2, RoundingMode.HALF_UP)
                : null;

        BigDecimal chargeMensuelle = chargeMensuelle(historique, contratSaisi);
        BigDecimal exposition = exposition(historique, contratSaisi, echeancesParContrat, reference);

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
            // Revenu non déclaré : on neutralise plutôt que de pénaliser à tort.
            pointsEndettement = p.getMaxEndettement() * p.getNeutreSansRevenu();
            pointsExposition = p.getMaxExposition() * p.getNeutreSansRevenu();
        }

        // ---------- Axe 4 : ancienneté de la relation ----------
        double pointsAnciennete = 0;
        if (client.getDateAdhesion() != null) {
            long mois = ChronoUnit.MONTHS.between(client.getDateAdhesion(), reference);
            mois = Math.max(0, mois); // garde-fou : une date d'adhésion future donnait un score négatif
            pointsAnciennete = p.getMaxAnciennete()
                    * Math.min(1.0, (double) mois / p.getAncienneteMoisPlein());
        }

        // ---------- Axe 5 : appétit de crédit récent ----------
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

        // ---------- Axe 6 : mixité ----------
        long types = historique.stream().map(Contrat::getTypeContrat).distinct().count();
        double pointsMixite = p.getMaxMixite() * (types >= 3 ? 1.0
                : types == 2 ? p.getMixiteRatioDeuxTypes()
                : p.getMixiteRatioUnType());

        // ---------- Agrégation ----------
        double secondaireBrut = pointsEndettement + pointsExposition + pointsAnciennete
                              + pointsNouveaux + pointsMixite;
        double plafond = p.maxSecondaire()
                * (p.getPlafondSecondaireBase()
                 + p.getPlafondSecondairePart() * (pointsPaiement / p.getMaxPaiement()));
        double secondaireRetenu = Math.min(secondaireBrut, plafond);

        double brut = p.getScoreBase() + pointsPaiement + secondaireRetenu - malus;
        int score = (int) Math.round(brut);
        score = Math.max(p.getScoreBase(), Math.min(p.getScoreMax(), score));

        ScoreDetail detail = new ScoreDetail(
                arrondi(pointsPaiement), arrondi(pointsEndettement), arrondi(pointsExposition),
                arrondi(pointsAnciennete), arrondi(pointsNouveaux), arrondi(pointsMixite),
                arrondi(malus), arrondi(secondaireBrut), arrondi(plafond), arrondi(secondaireRetenu),
                echues.size(), arrondi(tauxQualite * 100) / 100.0,
                tauxEndettement == null ? null : arrondi(tauxEndettement * 100) / 100.0,
                tauxExposition == null ? null : arrondi(tauxExposition * 100) / 100.0);

        GrilleScore grille = grilleScorePour(score);
        return ScoreResult.calcule(score, grille.getIntervalle(), grille.getCategorieRisque(),
                grille.getCouleur(), detail);
    }

    // ------------------------------------------------------------------
    // Sous-calculs
    // ------------------------------------------------------------------

    /** Pondération d'ancienneté, appliquée symétriquement aux bonus et aux malus. */
    private double poidsAnciennete(LocalDate dateEcheance, LocalDate reference) {
        long mois = ChronoUnit.MONTHS.between(dateEcheance, reference);
        if (mois <= 12) return p.getPoidsRecent();
        if (mois <= 36) return p.getPoidsMoyen();
        return p.getPoidsAncien();
    }

    private double qualite(Echeance e, LocalDate reference) {
        StatutEcheance statut = e.statutEffectif(reference);
        if (statut == StatutEcheance.PAYE_A_TEMPS) return p.getQualitePayeATemps();
        if (statut == StatutEcheance.IMPAYE) return p.getQualiteImpaye();
        // EN_RETARD : payée tardivement, ou échue depuis peu
        if (!e.estPayee()) return p.getQualiteImpaye();
        return e.joursDeRetard(reference) <= Echeance.SEUIL_IMPAYE_JOURS
                ? p.getQualiteRetardCourt()
                : p.getQualiteRetardLong();
    }

    /** Charge mensuelle : contrats actifs portés + mensualité estimée de la demande saisie. */
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

    /** Mensualité déclarée, sinon reconstituée à partir du total dû et de la périodicité. */
    private BigDecimal mensualite(Contrat c) {
        if (c.getMontantEcheanceMensuelle() != null && c.getMontantEcheanceMensuelle().signum() > 0) {
            return c.getMontantEcheanceMensuelle()
                    .divide(BigDecimal.valueOf(Math.max(1, c.pasEnMois())), 2, RoundingMode.HALF_UP);
        }
        int nb = c.getNombreTotalEcheances() == null ? 1 : Math.max(1, c.getNombreTotalEcheances());
        int dureeMois = Math.max(1, nb * c.pasEnMois());
        return c.totalDu().divide(BigDecimal.valueOf(dureeMois), 2, RoundingMode.HALF_UP);
    }

    /**
     * Exposition potentielle : encours restant + demandes en cours déjà en base
     * + demande saisie. C'est le risque total que porterait CECAM si tout était
     * accordé aujourd'hui.
     */
    private BigDecimal exposition(List<Contrat> historique, Contrat contratSaisi,
                                  Map<Long, List<Echeance>> echeances, LocalDate reference) {
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
        if (contratSaisi != null) {
            total = total.add(contratSaisi.totalDu());
        }
        return total;
    }

    /** Barème linéaire décroissant : max en deçà du seuil vert, 0 au-delà du seuil rouge. */
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
                        "La grille de score ne couvre pas la valeur " + score
                      + " : vérifiez la continuité des intervalles en base"));
    }

    private double arrondi(double v) {
        return Math.round(v * 100.0) / 100.0;
    }
}