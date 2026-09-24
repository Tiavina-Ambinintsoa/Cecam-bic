package mg.cecam.bic.rapport.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record EmploiDTO(
        String statutEmploi, String nomEmployeur, String profession,
        LocalDate dateEmbauche, BigDecimal revenuAnnuelTotal, String devise
) {}