// mg/cecam/bic/rapport/dto/DetailContratDTO.java  — MODIFIÉ
package mg.cecam.bic.rapport.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Bloc « Détail du Financement avec échéancier N » du rapport CRIF.
 * Les champs ajoutés sont ceux que CRIF affiche et que le modèle savait
 * déjà calculer ou stocker : dates de contrat, prochaine échéance, dernier
 * règlement, pires indicateurs de retard.
 */
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
        int nombreJoursRetard,
        int maxNombreJoursRetard,
        LocalDate dateMaxNombreJoursRetard,

        String pireStatut,
        LocalDate datePireStatut,

        String codeRestructuration,
        String contratOrigine,
        String nouveauContrat,

        List<GarantieDTO> garanties
) {}