package mg.cecam.bic.rapport.dto;

import java.time.LocalDateTime;
import java.util.List;

public record RapportSolvabiliteResponse(
        String identifiantRapport,
        LocalDateTime dateRequete,
        String statutClient,
        String codeClientCb,
        ClientInfoDTO client,
        List<ContactDTO> contacts,
        List<AdresseDTO> adressesActuelles,
        List<AdresseDTO> adressesHistoriques,
        List<IdentifiantDTO> identifiants,
        DetailDemandeDTO detailDemande,
        EmploiDTO emploi,
        List<LienClientDTO> liens,
        List<AlerteDTO> alertes,
        ScoreDTO score,
        List<GrilleScoreDTO> grille,
        SyntheseDTO synthese,
        List<SyntheseCategorieDTO> syntheseParCategorie,
        List<EncoursCategorieDTO> encoursParCategorie,

        List<LigneFinancementDTO> financementsNonDecaisses, 
        List<LigneFinancementDTO> financementsDecaisses,    

        List<DetailContratDTO> detailContrats
) {}