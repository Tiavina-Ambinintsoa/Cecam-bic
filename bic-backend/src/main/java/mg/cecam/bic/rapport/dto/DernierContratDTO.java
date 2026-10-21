package mg.cecam.bic.rapport.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DernierContratDTO(
        LocalDate dateOctroi,
        BigDecimal montant,
        LocalDate datePrevueRemboursement,
        LocalDate dateRemboursement,
        Integer joursRetard,
        String statut,
        String libelleAvance
) {}
