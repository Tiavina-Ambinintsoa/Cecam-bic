package mg.cecam.bic.rapport.dto;

import java.time.LocalDateTime;
import java.util.List;

public record AffaireIndividuelleDTO(
        String nom,
        LocalDateTime dateDerniereModification,
        List<AdresseDTO> adresses
) {}
