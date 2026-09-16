// mg/cecam/bic/rapport/dto/HistoriquePaiementLigneDTO.java  — MODIFIÉ
package mg.cecam.bic.rapport.dto;

/**
 * @param joursDeRetard colonne « Nombre Jours de retard » de CRIF, absente
 *                      de la version initiale alors que les données
 *                      (dateEcheance, datePaiement) permettaient de la calculer.
 */
public record HistoriquePaiementLigneDTO(
        int annee, String mois, int nombreEcheancesImpayees,
        Integer joursDeRetard, String statut
) {}