package mg.cecam.bic.score;

public record ScoreResult(
        boolean calculable,
        Integer valeur,
        String intervalle,
        String categorieRisque,
        String couleur,
        String message
) {
    public static ScoreResult calcule(int valeur, String intervalle, String categorieRisque,
                                      String couleur, String message) {
        return new ScoreResult(true, valeur, intervalle, categorieRisque, couleur, message);
    }

    public static ScoreResult nonCalculable(String message) {
        return new ScoreResult(false, null, null, null, null, message);
    }
}
