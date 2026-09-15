package mg.cecam.bic.score;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.client.Client;
import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.StatutEcheance;
import mg.cecam.bic.contrat.Contrat;
import mg.cecam.bic.contrat.ContratRepository;
import mg.cecam.bic.contrat.Echeance;
import mg.cecam.bic.contrat.EcheanceRepository;
import mg.cecam.bic.referentiel.GrilleScore;
import mg.cecam.bic.referentiel.GrilleScoreRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScoreService {

    private static final int SCORE_BASE = 300;
    private static final double MAX_PAIEMENT = 192.5;
    private static final double MAX_UTILISATION = 165;
    private static final double MAX_ANCIENNETE = 82.5;
    private static final double MAX_NOUVEAUX = 55;
    private static final double MAX_MIXITE = 55;
    private static final double MAX_SECONDAIRE = MAX_UTILISATION + MAX_ANCIENNETE + MAX_NOUVEAUX + MAX_MIXITE;

    private final ContratRepository contratRepository;
    private final EcheanceRepository echeanceRepository;
    private final GrilleScoreRepository grilleScoreRepository;

    public ScoreResult calculer(Client client, Contrat contratEnCours) {
        List<Contrat> historique = contratRepository.findByClient_Id(client.getId()).stream()
                .filter(c -> !c.getId().equals(contratEnCours.getId()))
                .toList();

        if (historique.isEmpty()) {
            return ScoreResult.nonCalculable("Ce client a été nouvellement créé dans le système");
        }

        double pointsPaiement = calculerPointsPaiement(historique);
        double pointsUtilisation = calculerPointsUtilisation(historique);
        double pointsAnciennete = calculerPointsAnciennete(client);
        double pointsNouveaux = calculerPointsNouveauxCredits(historique);
        double pointsMixite = calculerPointsMixite(historique);

        double secondaireBrut = pointsUtilisation + pointsAnciennete + pointsNouveaux + pointsMixite;
        double plafondSecondaire = MAX_SECONDAIRE * (0.4 + 0.6 * (pointsPaiement / MAX_PAIEMENT));
        double secondaireRetenu = Math.min(secondaireBrut, plafondSecondaire);

        int score = (int) Math.round(SCORE_BASE + pointsPaiement + secondaireRetenu);
        score = Math.max(300, Math.min(850, score));

        int scoreFinal = score;
        GrilleScore grille = grilleScoreRepository.findAll().stream()
                .filter(g -> scoreFinal >= g.getScoreMin() && scoreFinal <= g.getScoreMax())
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Aucun intervalle ne couvre le score " + scoreFinal));

        return ScoreResult.calcule(score, grille.getIntervalle(), grille.getCategorieRisque(), grille.getCouleur());
    }

    private double calculerPointsPaiement(List<Contrat> historique) {
        double brut = 0;
        for (Contrat c : historique) {
            for (Echeance e : echeanceRepository.findByContrat_IdOrderByNumeroEcheance(c.getId())) {
                if (e.getStatut() == StatutEcheance.A_VENIR || e.getDateEcheance().isAfter(LocalDate.now())) continue;
                long moisEcoules = ChronoUnit.MONTHS.between(e.getDateEcheance(), LocalDate.now());
                double decay = moisEcoules <= 12 ? 1.0 : moisEcoules <= 36 ? 0.5 : 0.2;
                brut += switch (e.getStatut()) {
                    case PAYE_A_TEMPS -> 2.0;
                    case EN_RETARD -> -30.0 * decay;
                    case IMPAYE -> -80.0 * decay;
                    case A_VENIR -> 0.0;
                };
            }
        }
        return Math.max(0, Math.min(MAX_PAIEMENT, brut));
    }

    private double calculerPointsUtilisation(List<Contrat> historique) {
        BigDecimal soldeRestant = BigDecimal.ZERO;
        BigDecimal totalFinance = BigDecimal.ZERO;

        for (Contrat c : historique) {
            if (c.getPhaseDemande() != PhaseDemande.ACTIF) continue;
            BigDecimal paye = echeanceRepository.findByContrat_IdOrderByNumeroEcheance(c.getId()).stream()
                    .map(e -> e.getMontantPaye() != null ? e.getMontantPaye() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            soldeRestant = soldeRestant.add(c.getMontantFinance().subtract(paye).max(BigDecimal.ZERO));
            totalFinance = totalFinance.add(c.getMontantFinance());
        }

        if (totalFinance.signum() == 0) return MAX_UTILISATION;
        double ratio = soldeRestant.doubleValue() / totalFinance.doubleValue();

        if (ratio < 0.10) return MAX_UTILISATION;
        if (ratio < 0.30) return MAX_UTILISATION * 0.75;
        if (ratio < 0.50) return MAX_UTILISATION * 0.35;
        return 0;
    }

    private double calculerPointsAnciennete(Client client) {
        long moisDepuisAdhesion = ChronoUnit.MONTHS.between(client.getDateAdhesion(), LocalDate.now());
        return Math.min(MAX_ANCIENNETE, (moisDepuisAdhesion / 60.0) * MAX_ANCIENNETE);
    }

    private double calculerPointsNouveauxCredits(List<Contrat> historique) {
        long recents = historique.stream()
                .filter(c -> ChronoUnit.MONTHS.between(c.getDateDemande(), LocalDate.now()) <= 12)
                .count();

        long demandesEnAttentePersistantes = historique.stream()
                .filter(c -> c.getPhaseDemande() == PhaseDemande.DEMANDE_EN_COURS)
                .filter(c -> ChronoUnit.DAYS.between(c.getDateDemande(), LocalDate.now()) > 60)
                .count();

        double points = MAX_NOUVEAUX - recents * 8 - demandesEnAttentePersistantes * 15;
        return Math.max(0, points);
    }

    private double calculerPointsMixite(List<Contrat> historique) {
        long typesDistincts = historique.stream().map(Contrat::getTypeContrat).distinct().count();
        if (typesDistincts >= 3) return MAX_MIXITE;
        if (typesDistincts == 2) return MAX_MIXITE * 0.6;
        return MAX_MIXITE * 0.2;
    }
}