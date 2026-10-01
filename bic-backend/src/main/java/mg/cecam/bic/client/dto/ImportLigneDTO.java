package mg.cecam.bic.client.dto;

public record ImportLigneDTO(
        String cin, String prenom, String deuxiemePrenom, String nom, String titre,
        String dateNaissance, String genre, String nationalite, String categorieTiersCode,
        String villeNaissance, String paysNaissance, String telephone, String adresseComplete,
        String codeContratCb, String typeContrat, String roleClient, String phaseDemande,
        String dateDemande, String dateDebutContrat, String dateFinContrat,
        String montantFinance, String montantEcheanceMensuelle, String devise
) {}