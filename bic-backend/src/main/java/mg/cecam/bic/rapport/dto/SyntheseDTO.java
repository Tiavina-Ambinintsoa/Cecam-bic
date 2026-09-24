package mg.cecam.bic.rapport.dto;

import java.math.BigDecimal;
import java.util.List;

public record SyntheseDTO(
        int nombreTotalContrat,
        int nombreEtablissementsDeclarants,
        String contratManquantReciprocite,
        String devise,
        BigDecimal expositionPotentielle,
        BigDecimal montantTotalRestantDu,
        BigDecimal montantTotalImpayes,
        BigDecimal montantTotalDemandes,
        BigDecimal totalGarantieSignature,
        List<RepartitionLigneDTO> repartition
) {}