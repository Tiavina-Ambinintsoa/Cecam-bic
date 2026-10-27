package mg.cecam.bic.client.dto;

import mg.cecam.bic.common.enums.Genre;

import java.time.LocalDate;

public record ClientResponse(
        Long id,
        String codeClientCb,
        String titre,
        String categorieTiersCode,
        String prenom,
        String deuxiemePrenom,
        String nom,
        LocalDate dateNaissance,
        String villeNaissance,
        String paysNaissance,
        Genre genre,
        String nationalite,
        String etatCivil,
        String telephone,
        String codeClientEtablissement,
        LocalDate dateAdhesion
) {
}
