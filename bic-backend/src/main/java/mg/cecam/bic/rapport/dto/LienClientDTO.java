package mg.cecam.bic.rapport.dto;

import java.time.LocalDateTime;

public record LienClientDTO(
        String codeClientCb, String nom, String typeRelation, Integer occurrences,
        String etablissement, LocalDateTime dateDerniereModification, boolean flagParent
) {}