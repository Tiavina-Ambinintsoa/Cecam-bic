package mg.cecam.bic.contrat.dto;

import mg.cecam.bic.common.enums.MotifCloture;
import mg.cecam.bic.common.enums.NatureGarantie;
import mg.cecam.bic.common.enums.OrigineDonnee;
import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.RoleClient;
import mg.cecam.bic.common.enums.StatutEcheance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO complet représentant un contrat historique avec son détail granulaire
 * (échéances individuelles, mensualités CRIF, garanties et co-emprunteurs).
 */
public record HistoriqueContratDetailResponse(
        Long id,
        Long clientId,
        String clientNom,
        String clientPrenom,
        String clientCin,
        String clientCodeCb,
        String codeContratCb,
        String codeContratEtablissement,
        String codeEtablissementDeclarant,
        String codeEtablissementCrypte,
        String typeContrat,
        String typeRelationEntreprise,
        RoleClient roleClient,
        PhaseDemande phaseDemande,
        MotifCloture motifCloture,
        LocalDate dateDemande,
        LocalDate dateDebutContrat,
        LocalDate dateFinContrat,
        LocalDate datePremiereEcheance,
        LocalDate dateDerniereModificationSource,
        BigDecimal montantFinance,
        BigDecimal montantTotalDu,
        BigDecimal montantEcheanceMensuelle,
        Integer nombreTotalEcheances,
        String devise,
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
        String codeRestructuration,
        LocalDate dateCodeRestructuration,
        String contratOrigine,
        String nouveauContrat,
        String note,
        OrigineDonnee origineDonnee,
        List<EcheanceDetailResponse> echeances,
        List<MoisDetailResponse> historiquePaiementMensuel,
        List<GarantieDetailResponse> garanties,
        List<ClientLieDetailResponse> clientsLies,
        BienLeasingDetailResponse bienLeasing
) {
    public record EcheanceDetailResponse(
            Integer numeroEcheance,
            LocalDate dateEcheance,
            BigDecimal montantDu,
            BigDecimal montantPaye,
            LocalDate datePaiement,
            StatutEcheance statut
    ) {}

    public record MoisDetailResponse(
            LocalDate mois,
            Integer nombreEcheances,
            Integer nombreEcheancesImpayees,
            Integer joursDeRetard,
            String statut,
            String detailsSource
    ) {}

    public record GarantieDetailResponse(
            NatureGarantie nature,
            String typeGarantie,
            String codeEtablissementGarantie,
            String nomGarant,
            String codeClientCbGarant,
            BigDecimal montantCouvert,
            LocalDate dateDebutValidite,
            LocalDate dateFinValidite
    ) {}

    public record ClientLieDetailResponse(
            String codeClientCb,
            String nomClient,
            RoleClient roleClient
    ) {}

    public record BienLeasingDetailResponse(
            String codeBien,
            BigDecimal valeurBien,
            String etatBien,
            String marqueBien,
            LocalDate dateAcquisition,
            String immatriculation
    ) {}
}
