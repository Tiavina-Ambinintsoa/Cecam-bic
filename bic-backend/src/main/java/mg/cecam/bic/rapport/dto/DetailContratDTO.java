// DetailContratDTO.java
package mg.cecam.bic.rapport.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record DetailContratDTO(
        String codeContratCb, String typeContrat, String phase, String role,
        LocalDate dateDebut, LocalDate dateFin, String devise,
        BigDecimal montantFinance, BigDecimal montantEcheanceMensuelle, int nombreTotalEcheances,
        List<HistoriquePaiementLigneDTO> historiquePaiement,
        int nombreEcheancesRestantes, BigDecimal montantRestantDu,
        int nombreEcheancesImpayees, BigDecimal montantImpayes,
        String pireStatut,
        List<GarantieDTO> garanties
) {}