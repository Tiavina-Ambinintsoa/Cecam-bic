// mg/cecam/bic/rapport/dto/ScoreDTO.java  — MODIFIÉ
package mg.cecam.bic.rapport.dto;

import mg.cecam.bic.score.ScoreDetail;

public record ScoreDTO(
        boolean calculable, Integer valeur, String intervalle,
        String categorieRisque, String couleur, String couleurHex, String message,
        ScoreDetail detail   // AJOUT : décomposition auditable du score
) {}