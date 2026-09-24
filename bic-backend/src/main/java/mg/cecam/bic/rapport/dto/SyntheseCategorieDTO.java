package mg.cecam.bic.rapport.dto;

import java.math.BigDecimal;

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