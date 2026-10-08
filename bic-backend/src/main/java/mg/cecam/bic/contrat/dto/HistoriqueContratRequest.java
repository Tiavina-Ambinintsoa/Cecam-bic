package mg.cecam.bic.contrat.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import mg.cecam.bic.common.enums.MotifCloture;
import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.RoleClient;
import mg.cecam.bic.common.enums.StatutEcheance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record HistoriqueContratRequest(
        @NotNull Long clientId,
        String codeContratCb,
        String codeContratEtablissement,
        String codeEtablissementDeclarant,
        @NotNull String typeContrat,
        String typeRelationEntreprise,
        @NotNull RoleClient roleClient,
        @NotNull LocalDate dateDemande,
        LocalDate dateDebutContrat,
        LocalDate dateFinContrat,
        LocalDate datePremiereEcheance,
        @NotNull @Positive BigDecimal montantFinance,
        BigDecimal montantTotalDu,
        BigDecimal montantEcheanceMensuelle,
        @NotNull @Positive Integer nombreTotalEcheances,
        @NotNull String devise,
        String periodicitePaiement,
        @NotNull PhaseDemande phaseDemande,
        MotifCloture motifCloture,
        String codeRestructuration,
        LocalDate dateCodeRestructuration,
        String note,
        @Valid List<EcheanceHistoriqueRequest> echeances
) {
    public record EcheanceHistoriqueRequest(
            @NotNull Integer numeroEcheance,
            @NotNull LocalDate dateEcheance,
            @NotNull BigDecimal montantDu,
            BigDecimal montantPaye,
            LocalDate datePaiement,
            @NotNull StatutEcheance statut
    ) {}
}
