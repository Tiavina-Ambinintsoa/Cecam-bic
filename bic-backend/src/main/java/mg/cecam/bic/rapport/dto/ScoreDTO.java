package mg.cecam.bic.rapport.dto;

public record ScoreDTO(
        boolean calculable, Integer valeur, String intervalle,
        String categorieRisque, String couleur, String couleurHex, String message,
        String avertissement
) {}
