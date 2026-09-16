// mg/cecam/bic/score/ScoreDetail.java  — MODIFIÉ
package mg.cecam.bic.score;

/**
 * Décomposition du score, désormais exposée dans le rapport.
 * Un score de solvabilité opposable doit être auditable : l'agent de crédit
 * comme le client doivent pouvoir voir d'où viennent les points.
 */
public record ScoreDetail(
        double pointsPaiement,
        double pointsEndettement,
        double pointsExposition,
        double pointsAnciennete,
        double pointsNouveauxCredits,
        double pointsMixite,
        double malusSurendettement,
        double secondaireBrut,
        double plafondSecondaire,
        double secondaireRetenu,
        int echeancesEchues,
        double tauxQualitePaiement,
        Double tauxEndettement,
        Double tauxExposition
) {
    public double total() {
        return pointsPaiement + secondaireRetenu - malusSurendettement;
    }

    public boolean plafondApplique() {
        return secondaireBrut > plafondSecondaire + 0.0001;
    }
}