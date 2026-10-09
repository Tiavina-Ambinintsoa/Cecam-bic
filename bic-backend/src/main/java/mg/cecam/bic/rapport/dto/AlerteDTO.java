package mg.cecam.bic.rapport.dto;

import java.time.LocalDate;

public record AlerteDTO(
        Long clientId, String codeClientCb, String nomComplet, Long contratId,
        String codeContratCb, String typeContrat, LocalDate dateAlerte,
        String description, String niveau, String agence, String utilisateur,
        long echeancesImpayees, long echeancesEnRetard,
        long retard30a59, long retard60a89, long retard90a119,
        long retard120a149, long retard150a179, long retard180Plus,
        int maxJoursRetard
) {}
