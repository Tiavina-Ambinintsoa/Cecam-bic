package mg.cecam.bic.rapport.dto;

import java.util.List;
import java.util.Map;

public record ScoreDTO(
        boolean calculable, Integer valeur, String intervalle,
        String categorieRisque, String couleur, String couleurHex, String message,
        String avertissement,
        Map<String, Double> composantes,
        List<String> plafonds
) {}
