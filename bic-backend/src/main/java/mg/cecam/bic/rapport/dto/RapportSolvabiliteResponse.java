// RapportSolvabiliteResponse.java
package mg.cecam.bic.rapport.dto;

import java.time.LocalDateTime;
import java.util.List;

public record RapportSolvabiliteResponse(
        String identifiantRapport, LocalDateTime dateRequete, String statutClient, String codeClientCb,
        ClientInfoDTO client, List<AdresseDTO> adressesActuelles, List<AdresseDTO> adressesHistoriques,
        List<IdentifiantDTO> identifiants, DetailDemandeDTO detailDemande,
        EmploiDTO emploi, List<LienClientDTO> liens, ScoreDTO score, List<GrilleScoreDTO> grille,
        SyntheseDTO synthese, List<CalendrierCreditDTO> calendriers, List<DetailContratDTO> detailContrats
) {}