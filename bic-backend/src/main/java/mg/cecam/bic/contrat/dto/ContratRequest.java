package mg.cecam.bic.contrat.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Positive;
import mg.cecam.bic.common.enums.ModeRattachement;
import mg.cecam.bic.common.enums.RoleClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ContratRequest(
        @NotNull Long clientId,
        @NotNull ModeRattachement modeRattachement,
        @NotNull String typeContrat,
        String codeClientCbCible,
        String codeContratEtablissement, 
        @NotNull RoleClient roleClient,
        @NotNull LocalDate dateDemande,
        @NotNull @Positive BigDecimal montantFinance,
        BigDecimal montantEcheanceMensuelle,
        @NotNull @Positive Integer nombreTotalEcheances,
        @NotNull String devise,
        String periodicitePaiement,
        String periodiciteBudget,
        BigDecimal montantRecette,
        BigDecimal montantDepense,
        String typeGarantie,
        BigDecimal valeurGarantie,
        @PositiveOrZero BigDecimal valeurPatrimoine,
        @Size(max = 20) List<@Valid RevenuDemandeRequest> revenusComplementaires,
        @PositiveOrZero BigDecimal totalDepot,
        @PositiveOrZero BigDecimal totalCreditActif,
        String typeCredit,
        BigDecimal tauxInteretMensuel,
        @Size(max = 3) List<@Valid DernierContratRequest> derniersContrats
) {
    public record RevenuDemandeRequest(
            String typeRevenu,
            String description,
            @Positive BigDecimal montantAnnuel,
            String devise
    ) {}

    public record DernierContratRequest(
            LocalDate dateOctroi,
            BigDecimal montant,
            LocalDate datePrevueRemboursement,
            LocalDate dateRemboursement
    ) {}

    @AssertTrue(message = "Le rattachement à une entreprise ne fait pas partie du périmètre Individu.")
    public boolean isModeDansPerimetreIndividuel() {
        return modeRattachement != ModeRattachement.ENTREPRISE;
    }
}
