package mg.cecam.bic.rapport.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GarantieDTO(
        String nature,                
        String typeGarantie,
        String codeEtablissementGarantie,  
        String nomGarant,
        String codeClientCbGarant,
        BigDecimal montantCouvert,
        LocalDate dateDebutValidite,
        LocalDate dateFinValidite
) {}