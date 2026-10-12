package mg.cecam.bic.contrat.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.AssertTrue;
import mg.cecam.bic.common.enums.ModeRattachement;
import mg.cecam.bic.common.enums.RoleClient;

import java.math.BigDecimal;
import java.time.LocalDate;

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
        String periodicitePaiement
) {
    @AssertTrue(message = "Le rattachement à une entreprise ne fait pas partie du périmètre Individu.")
    public boolean isModeDansPerimetreIndividuel() {
        return modeRattachement != ModeRattachement.ENTREPRISE;
    }
}
