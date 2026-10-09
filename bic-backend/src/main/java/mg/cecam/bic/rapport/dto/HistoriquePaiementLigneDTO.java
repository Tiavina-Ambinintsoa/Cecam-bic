package mg.cecam.bic.rapport.dto;

public record HistoriquePaiementLigneDTO(
        int annee, String mois, Integer nombreEcheances, Integer nombreEcheancesImpayees,
        Integer joursDeRetard, String statut, String bucketRetard, String detailsSource
) {}
