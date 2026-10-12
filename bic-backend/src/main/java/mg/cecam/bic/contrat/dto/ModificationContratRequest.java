package mg.cecam.bic.contrat.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mg.cecam.bic.common.enums.MotifCloture;
import mg.cecam.bic.common.enums.PhaseDemande;

import java.math.BigDecimal;

public record ModificationContratRequest(
        @NotNull PhaseDemande phaseDemande,
        MotifCloture motifCloture,
        @Size(max = 40) String codeContratEtablissement,
        @Size(max = 40) String codeDemandeEtablissement,
        @DecimalMin("0.0") BigDecimal montantEcheanceMensuelle,
        @NotBlank @Size(max = 40) String devise,
        @NotBlank @Size(max = 500) String justification
) {}
