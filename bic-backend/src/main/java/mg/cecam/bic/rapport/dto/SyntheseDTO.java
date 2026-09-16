// mg/cecam/bic/rapport/dto/SyntheseDTO.java  — MODIFIÉ
package mg.cecam.bic.rapport.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Les montants sont volontairement nullables : CRIF affiche « - » et non « 0 »
 * quand le client n'a aucun contrat en base (cf. modèle Juliette
 * Rasoamiarimbolana). MontantFormatUtil.formatMontant(null) rend « - ».
 */
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