// mg/cecam/bic/rapport/dto/ClientInfoDTO.java  — MODIFIÉ
package mg.cecam.bic.rapport.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ClientInfoDTO(
        String codeClientCb,          // AJOUT : CRIF l'affiche en tête du bloc CLIENT
        String titre, String nomComplet, String prenom, String deuxiemePrenom, String nom,
        LocalDate dateNaissance, String villeNaissance, String paysNaissance,
        String genre, String nationalite, String etatCivil, String telephone,
        String categorieTiersCode, LocalDateTime dateDerniereModification
) {}