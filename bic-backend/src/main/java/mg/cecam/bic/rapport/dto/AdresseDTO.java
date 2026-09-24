package mg.cecam.bic.rapport.dto;

import java.time.LocalDateTime;

public record AdresseDTO(
        String typeAdresse, String adresseComplete, String numeroRue, String codePostal,
        String ville, String commune, String region, String pays, LocalDateTime dateDerniereModification
) {}