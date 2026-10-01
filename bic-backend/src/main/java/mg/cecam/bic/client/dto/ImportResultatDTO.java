package mg.cecam.bic.client.dto;

import java.util.List;

public record ImportResultatDTO(int lignesLues, int lignesImportees, int lignesEnErreur, List<String> erreurs) {}