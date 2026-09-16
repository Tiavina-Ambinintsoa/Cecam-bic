// mg/cecam/bic/rapport/dto/EncoursCategorieDTO.java  — NOUVEAU
package mg.cecam.bic.rapport.dto;

import java.util.List;

/**
 * Section « Situation Financière par Catégorie — Montant Encours » du rapport
 * CRIF (page 3). Une grille par catégorie de contrat, pas par contrat, et la
 * cellule porte l'ENCOURS restant au mois considéré — pas le montant payé.
 *
 * C'est la moitié manquante de l'ancien « Calendrier de remboursement », qui
 * mélangeait cette grille avec la grille de statut par contrat.
 */
public record EncoursCategorieDTO(
        String categorie,
        String codeEtablissement,
        List<LigneAnneeDTO> lignes
) {}