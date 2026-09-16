// mg/cecam/bic/rapport/dto/CalendrierCreditDTO.java  — MODIFIÉ
package mg.cecam.bic.rapport.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * Grille de statut mensuelle d'UN contrat — l'équivalent des bandeaux
 * OK/OK/OK verts de CRIF page 3. La cellule porte un code de statut,
 * plus un montant.
 */
public record CalendrierCreditDTO(
        String codeContratCb,
        String codeContratEtablissement,
        String typeContrat,
        String phase,
        BigDecimal montantFinance,
        List<LigneAnneeDTO> lignes
) {}