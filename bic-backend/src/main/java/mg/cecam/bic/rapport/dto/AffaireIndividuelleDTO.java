package mg.cecam.bic.rapport.dto;

import java.time.LocalDateTime;

public record AffaireIndividuelleDTO(
        String nomAffaire, LocalDateTime dateDerniereModification, AdresseDTO adresse
) {}