package mg.cecam.bic.rapport.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record DetailContratDTO(
        String codeContratCb,
        String codeContratEtablissement,
        String codeEtablissementDeclarant,
        String typeContrat,
        String phase,
        String role,
        String devise,

        LocalDate dateDemande,
        LocalDate dateDebutContrat,
        LocalDate dateFinContrat,
        LocalDate datePremiereEcheance,
        LocalDateTime dateDerniereModification,

        BigDecimal montantFinance,
        BigDecimal montantTotalDu,
        BigDecimal montantEcheanceMensuelle,
        int nombreTotalEcheances,
        String periodicitePaiement,
        String modeReglement,

        BigDecimal montantProchaineEcheance,
        LocalDate dateProchaineEcheance,
        LocalDate dateDernierReglement,

        List<HistoriquePaiementLigneDTO> historiquePaiement,

        int nombreEcheancesRestantes,
        BigDecimal montantRestantDu,
        int nombreEcheancesImpayees,
        BigDecimal montantImpayes,
        BigDecimal maxMontantImpayes,
        int maxNombreEcheancesImpayees,
        LocalDate dateMaxNombreEcheancesImpayees,
        int nombreJoursRetard,
        int maxNombreJoursRetard,
        LocalDate dateMaxNombreJoursRetard,

        String pireStatut,
        LocalDate datePireStatut,

        String codeRestructuration,
        LocalDate dateCodeRestructuration,
        String contratOrigine,
        String nouveauContrat,
        String note,                    

        BienLeasingDTO bienLeasing,       
        List<LienClientDTO> clientsLies,   
        List<GarantieDTO> garanties
) {}