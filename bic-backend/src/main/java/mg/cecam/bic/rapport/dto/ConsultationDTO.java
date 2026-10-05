package mg.cecam.bic.rapport.dto;

import java.time.LocalDate;

public record ConsultationDTO(
        String codeEtablissement,
        LocalDate date,
        String type
) {}
