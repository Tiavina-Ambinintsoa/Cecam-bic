// HistoriquePaiementLigneDTO.java
package mg.cecam.bic.rapport.dto;

public record HistoriquePaiementLigneDTO(int annee, String mois, int nombreEcheancesImpayees, String statut) {}