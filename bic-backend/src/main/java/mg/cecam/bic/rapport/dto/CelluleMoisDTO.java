// mg/cecam/bic/rapport/dto/CelluleMoisDTO.java  — MODIFIÉ
package mg.cecam.bic.rapport.dto;

import java.math.BigDecimal;

/**
 * @param libelle texte affiché dans la cellule : « OK », « R », « IMP »
 *                pour la grille de statut, ou le montant formaté pour la
 *                grille d'encours. Chaîne vide si le mois est hors période.
 */
public record CelluleMoisDTO(
        String mois, boolean dansPeriode, BigDecimal montant,
        String statut, String libelle, String couleurHex
) {
    public static CelluleMoisDTO vide(String mois) {
        return new CelluleMoisDTO(mois, false, null, null, "", "transparent");
    }
}