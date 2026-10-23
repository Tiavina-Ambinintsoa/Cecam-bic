package mg.cecam.bic.rapport.dto;

public record RisqueGlobalDTO(
        String intervalle,
        String categorieRisque,
        String couleurHex,
        String justification
) {}
