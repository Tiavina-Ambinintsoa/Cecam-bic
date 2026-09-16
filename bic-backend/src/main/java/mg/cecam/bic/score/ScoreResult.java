// mg/cecam/bic/score/ScoreResult.java  — MODIFIÉ
package mg.cecam.bic.score;

public record ScoreResult(
        boolean calculable,
        Integer valeur,
        String intervalle,
        String categorieRisque,
        String couleur,
        String message,
        ScoreDetail detail
) {
    public static ScoreResult calcule(int valeur, String intervalle, String categorieRisque,
                                      String couleur, ScoreDetail detail) {
        return new ScoreResult(true, valeur, intervalle, categorieRisque, couleur, null, detail);
    }

    public static ScoreResult nonCalculable(String message) {
        return new ScoreResult(false, null, null, null, null, message, null);
    }
}