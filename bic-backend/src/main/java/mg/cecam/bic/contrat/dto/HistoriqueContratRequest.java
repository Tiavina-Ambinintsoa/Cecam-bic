package mg.cecam.bic.contrat.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import mg.cecam.bic.common.enums.MotifCloture;
import mg.cecam.bic.common.enums.NatureGarantie;
import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.RoleClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
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
        @Valid List<EcheanceHistoriqueRequest> echeances,
        /** null = ne pas toucher aux garanties existantes ; liste vide = les supprimer. */
        @Valid List<GarantieSaisie> garanties,
        /** null = ne pas toucher aux clients liés existants ; liste vide = les supprimer. */
        @Valid List<LienSaisie> liens
) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record EcheanceHistoriqueRequest(
            @NotNull Integer numeroEcheance,
            @NotNull LocalDate dateEcheance,
            @NotNull BigDecimal montantDu,
            BigDecimal montantPaye,
            LocalDate datePaiement
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record GarantieSaisie(
            NatureGarantie nature,
            @NotBlank String typeGarantie,
            String codeEtablissementGarantie,
            String nomGarant,
            String codeClientCbGarant,
            BigDecimal montantCouvert,
            LocalDate dateDebutValidite,
            LocalDate dateFinValidite
    ) {}

    /** Co-titulaire ou garant d'un contrat (le titulaire est toujours le client du contrat). */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LienSaisie(
            @NotBlank String codeClientCb,
            String nomClient,
            @NotNull RoleClient roleClient
    ) {}
}