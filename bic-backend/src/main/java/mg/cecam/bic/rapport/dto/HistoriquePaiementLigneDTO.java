package mg.cecam.bic.rapport.dto;

public record HistoriquePaiementLigneDTO(
        int annee, String mois, int nombreEcheancesImpayees,
        String retard, String statut
) {}