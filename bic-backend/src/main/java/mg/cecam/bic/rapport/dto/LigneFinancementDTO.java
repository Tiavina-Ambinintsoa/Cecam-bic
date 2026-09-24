package mg.cecam.bic.rapport.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public record LigneFinancementDTO(
        int numero,
        String codeContratCb,
        String codeContratEtablissement,
        String typeContrat,
        String phase,
        String role,
        String codeEtablissementDeclarant,
        List<LienClientDTO> clientsLies,

        LocalDate dateDemande,
        BigDecimal montantFinance,
        BigDecimal montantEcheanceMensuelle,
        Integer nombreTotalEcheances,
        String periodicitePaiement,
        LocalDateTime dateDerniereModification,

        LocalDate dateDebutContrat,
        LocalDate dateFinContrat,
        String note,
        List<LigneAnneeDTO> grilleStatut
) {}