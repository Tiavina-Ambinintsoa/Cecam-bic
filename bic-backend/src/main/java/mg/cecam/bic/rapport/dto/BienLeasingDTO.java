package mg.cecam.bic.rapport.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BienLeasingDTO(
        String codeBien, BigDecimal valeurBien, String etatBien,
        String marqueBien, LocalDate dateAcquisition, String immatriculation
) {
    public static BienLeasingDTO vide() {
        return new BienLeasingDTO(null, null, null, null, null, null);
    }
}