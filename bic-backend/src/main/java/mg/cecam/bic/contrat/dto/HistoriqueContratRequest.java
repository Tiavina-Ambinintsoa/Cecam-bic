package mg.cecam.bic.contrat.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import mg.cecam.bic.common.enums.MotifCloture;
import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.NatureGarantie;
import mg.cecam.bic.common.enums.OrigineDonnee;
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
        LocalDate dateDemande,
        LocalDate dateDebutContrat,
        LocalDate dateFinContrat,
        LocalDate datePremiereEcheance,
        LocalDate dateDerniereModificationSource,
        String codeEtablissementCrypte,
        @Positive BigDecimal montantFinance,
        BigDecimal montantTotalDu,
        BigDecimal montantEcheanceMensuelle,
        @Positive Integer nombreTotalEcheances,
        @NotNull String devise,
        String periodicitePaiement,
        String modeReglement,
        BigDecimal montantProchaineEcheance,
        LocalDate dateProchaineEcheance,
        LocalDate dateDernierReglement,
        Integer nombreEcheancesRestantes,
        BigDecimal montantRestantDu,
        Integer nombreEcheancesImpayees,
        BigDecimal montantImpayes,
        BigDecimal maxMontantImpayes,
        Integer maxNombreEcheancesImpayees,
        LocalDate dateMaxNombreEcheancesImpayees,
        Integer nombreJoursRetard,
        Integer maxNombreJoursRetard,
        LocalDate dateMaxNombreJoursRetard,
        String pireStatut,
        LocalDate datePireStatut,
        @NotNull PhaseDemande phaseDemande,
        MotifCloture motifCloture,
        String codeRestructuration,
        LocalDate dateCodeRestructuration,
        String contratOrigine,
        String nouveauContrat,
        String note,
        @Valid List<EcheanceHistoriqueRequest> echeances,
        @Valid List<HistoriquePaiementMensuelRequest> historiquePaiementMensuel,
        @Valid List<GarantieHistoriqueRequest> garanties,
        BienLeasingHistoriqueRequest bienLeasing,
        @Valid List<ClientLieHistoriqueRequest> clientsLies,
        OrigineDonnee origineDonnee
) {
    public record EcheanceHistoriqueRequest(
            @NotNull Integer numeroEcheance,
            @NotNull LocalDate dateEcheance,
            @NotNull BigDecimal montantDu,
            BigDecimal montantPaye,
            LocalDate datePaiement,
            @NotNull StatutEcheance statut
    ) {}

    public record HistoriquePaiementMensuelRequest(
            @NotNull LocalDate mois,
            @PositiveOrZero Integer nombreEcheances,
            @PositiveOrZero Integer nombreEcheancesImpayees,
            @PositiveOrZero Integer joursDeRetard,
            @jakarta.validation.constraints.NotBlank String statut,
            String detailsSource
    ) {}

    public record GarantieHistoriqueRequest(
            @NotNull NatureGarantie nature,
            @jakarta.validation.constraints.NotBlank String typeGarantie,
            String codeEtablissementGarantie,
            String nomGarant,
            String codeClientCbGarant,
            BigDecimal montantCouvert,
            LocalDate dateDebutValidite,
            LocalDate dateFinValidite
    ) {}

    public record BienLeasingHistoriqueRequest(
            String codeBien,
            BigDecimal valeurBien,
            String etatBien,
            String marqueBien,
            LocalDate dateAcquisition,
            String immatriculation
    ) {}

    public record ClientLieHistoriqueRequest(
            @jakarta.validation.constraints.NotBlank String codeClientCb,
            String nomClient,
            @NotNull RoleClient roleClient
    ) {}
}
