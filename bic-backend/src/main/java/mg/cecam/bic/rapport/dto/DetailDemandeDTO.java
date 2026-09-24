package mg.cecam.bic.rapport.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DetailDemandeDTO(
        String codeContratCb, String role, String typeRelationEntreprise, String typeContrat,
        String phaseDemande, String devise, String periodicitePaiement,
        BigDecimal montantFinance, BigDecimal montantEcheanceMensuelle,
        int nombreTotalEcheances, LocalDate dateDemande
) {}