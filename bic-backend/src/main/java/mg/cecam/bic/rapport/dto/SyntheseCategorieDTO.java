// mg/cecam/bic/rapport/dto/SyntheseCategorieDTO.java  — MODIFIÉ
package mg.cecam.bic.rapport.dto;

import java.math.BigDecimal;

/**
 * Le libellé de la première ligne change d'une catégorie à l'autre chez CRIF :
 * « Montant Échéance Mensuelle » pour les financements avec échéancier,
 * « Plafond de Crédit » pour les cartes, « Montant Facture » pour les services.
 * Il est donc porté par le DTO au lieu d'être déduit d'un index dans le template.
 */
public record SyntheseCategorieDTO(
        String categorie,
        int contratsActifs,
        String libelleMontantPrincipal,
        BigDecimal montantPrincipalTitulaire, BigDecimal montantPrincipalGarant,
        BigDecimal montantRestantDuTitulaire, BigDecimal montantRestantDuGarant,
        BigDecimal montantImpayesTitulaire, BigDecimal montantImpayesGarant
) {
    public static SyntheseCategorieDTO vide(String categorie, String libelle) {
        return new SyntheseCategorieDTO(categorie, 0, libelle,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }
}