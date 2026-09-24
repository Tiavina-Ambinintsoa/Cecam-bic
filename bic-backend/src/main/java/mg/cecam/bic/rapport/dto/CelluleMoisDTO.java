package mg.cecam.bic.rapport.dto;

import java.math.BigDecimal;

public record CelluleMoisDTO(
        String mois, boolean dansPeriode, BigDecimal montant,
        String statut, String libelle, String couleurHex
) {
    public static CelluleMoisDTO vide(String mois) {
        return new CelluleMoisDTO(mois, false, null, null, "", "transparent");
    }
}