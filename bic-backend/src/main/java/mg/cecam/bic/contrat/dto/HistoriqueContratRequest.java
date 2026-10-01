package mg.cecam.bic.contrat.dto;

import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.RoleClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record HistoriqueContratRequest(
        Long clientId,
        String codeContratCb,
        String codeContratEtablissement,
        String typeContrat,
        RoleClient roleClient,
        PhaseDemande phaseDemande,
        LocalDate dateDemande,
        LocalDate dateDebutContrat,
        LocalDate dateFinContrat,
        BigDecimal montantFinance,
        BigDecimal montantTotalDu,
        BigDecimal montantEcheanceMensuelle,
        Integer nombreTotalEcheances,
        String devise,
        String periodicitePaiement,
        List<EcheanceSaisie> echeances
) {
    public record EcheanceSaisie(
            int numeroEcheance, LocalDate dateEcheance, BigDecimal montantDu,
            BigDecimal montantPaye, LocalDate datePaiement, String statut
    ) {}
}