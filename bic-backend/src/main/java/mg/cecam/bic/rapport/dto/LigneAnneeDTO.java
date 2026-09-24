package mg.cecam.bic.rapport.dto;

import java.util.List;

public record LigneAnneeDTO(int annee, List<CelluleMoisDTO> mois) {}