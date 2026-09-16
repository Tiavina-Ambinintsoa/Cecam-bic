// mg/cecam/bic/rapport/dto/GarantieDTO.java  — MODIFIÉ
package mg.cecam.bic.rapport.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record GarantieDTO(
        String nature,                      // « Garantie Réelle » / « Garantie Personnelle »
        String typeGarantie,
        String codeEtablissementGarantie,   // AJOUT : CRIF, ex. NAM11327 (5)
        String nomGarant,
        String codeClientCbGarant,
        BigDecimal montantCouvert,
        LocalDate dateDebutValidite,
        LocalDate dateFinValidite
) {}