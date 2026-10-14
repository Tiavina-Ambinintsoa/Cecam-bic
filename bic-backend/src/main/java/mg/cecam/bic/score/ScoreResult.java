package mg.cecam.bic.score;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record ScoreResult(
        boolean calculable,
        Integer valeur,
        String intervalle,
        String categorieRisque,
        String couleur,
        String message,
        boolean mauvais,
        boolean calculPartiel,
        List<String> plafonds,
        Map<String, Double> composantes
) {
    public static ScoreResult calcule(int valeur, String intervalle, String categorieRisque,
                                      String couleur, String message) {
        return new ScoreResult(true, valeur, intervalle, categorieRisque, couleur, message,
                false, false, List.of(), Map.of());
    }

    public static ScoreResult calcule(int valeur, String intervalle, String categorieRisque, String couleur,
                                      String message, boolean mauvais, boolean calculPartiel,
                                      List<String> plafonds, Map<String, Double> composantes) {
        return new ScoreResult(true, valeur, intervalle, categorieRisque, couleur, message,
                mauvais, calculPartiel, List.copyOf(plafonds), new LinkedHashMap<>(composantes));
    }

    public static ScoreResult nonCalculable(String message) {
        return new ScoreResult(false, null, null, null, null, message, false, false, List.of(), Map.of());
    }
}