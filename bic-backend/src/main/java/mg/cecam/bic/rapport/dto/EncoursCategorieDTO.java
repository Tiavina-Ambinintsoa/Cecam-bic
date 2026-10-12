package mg.cecam.bic.rapport.dto;

import java.util.List;

public record EncoursCategorieDTO(
        String categorie,
        String codeEtablissement,
        String devise,
        List<LigneAnneeDTO> lignes
) {}
