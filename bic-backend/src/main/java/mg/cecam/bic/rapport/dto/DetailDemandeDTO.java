package mg.cecam.bic.rapport.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DetailDemandeDTO(
        String codeContratCb, String role, String typeRelationEntreprise, String typeContrat,
        String phaseDemande, String devise, String periodicitePaiement, String origineDonnee,
        BigDecimal montantFinance, BigDecimal montantEcheanceMensuelle,
        Integer nombreTotalEcheances, LocalDate dateDemande,
        String periodiciteBudget, BigDecimal montantRecette, BigDecimal montantDepense,
        BigDecimal ratioRestePourcentage
) {}
