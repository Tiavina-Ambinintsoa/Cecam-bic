package mg.cecam.bic.client.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import mg.cecam.bic.common.enums.Genre;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ClientRequest(
        String titre,
        @NotBlank String categorieTiersCode,
        @NotBlank String prenom,
        String deuxiemePrenom,
        @NotBlank String nom,
        @NotNull LocalDate dateNaissance,
        String villeNaissance,
        String paysNaissance,
        @NotNull Genre genre,
        @NotBlank String nationalite,
        String etatCivil,
        String telephone,
        @Valid @NotNull List<AdresseRequest> adresses,
        @Valid @NotNull List<IdentifiantRequest> identifiants,
        EmploiRequest emploi
) {
    public record AdresseRequest(
            @NotBlank String typeAdresse, @NotBlank String adresseComplete,
            String numeroRue, String codePostal, String ville, String commune, String region, String pays
    ) {}

    public record IdentifiantRequest(@NotBlank String typeIdentifiant, @NotBlank String numero) {}

    public record EmploiRequest(
            String statutEmploi, String nomEmployeur, String profession,
            LocalDate dateEmbauche, BigDecimal revenuAnnuelTotal, String devise
    ) {}
}