// mg/cecam/bic/contrat/dto/ContratRequest.java  — MODIFIÉ
package mg.cecam.bic.contrat.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import mg.cecam.bic.common.enums.ModeRattachement;
import mg.cecam.bic.common.enums.RoleClient;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContratRequest(
        @NotNull Long clientId,
        @NotNull ModeRattachement modeRattachement,
        @NotNull String typeContrat,
        String typeRelationEntreprise,
        String codeClientCbCible,
        String codeContratEtablissement,   // AJOUT : référence interne CECAM
        @NotNull RoleClient roleClient,
        @NotNull LocalDate dateDemande,
        @NotNull @Positive BigDecimal montantFinance,
        BigDecimal montantEcheanceMensuelle,
        @NotNull @Positive Integer nombreTotalEcheances,
        @NotNull String devise,
        String periodicitePaiement
) {}