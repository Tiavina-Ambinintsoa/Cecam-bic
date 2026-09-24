package mg.cecam.bic.score;

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
        int moisObserves,
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