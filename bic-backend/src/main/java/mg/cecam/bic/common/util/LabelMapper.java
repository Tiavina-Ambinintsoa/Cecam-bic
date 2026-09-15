package mg.cecam.bic.common.util;

import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.RoleClient;
import mg.cecam.bic.common.enums.StatutEcheance;

public final class LabelMapper {
    private LabelMapper() {}

    public static String role(RoleClient r) {
        return switch (r) {
            case TITULAIRE -> "Titulaire";
            case CO_TITULAIRE -> "Co-titulaire";
            case GARANT -> "Garant";
        };
    }

    public static String phase(PhaseDemande p) {
        return switch (p) {
            case DEMANDE_EN_COURS -> "Demande en cours";
            case ACTIF -> "Actif";
            case REFUSE -> "Refusé";
            case ABANDONNE -> "Abandonné";
            case FERME -> "Fermé";
        };
    }

    public static String statutEcheance(StatutEcheance s) {
        return switch (s) {
            case PAYE_A_TEMPS -> "Payé à temps";
            case EN_RETARD -> "En retard";
            case IMPAYE -> "Impayé";
            case A_VENIR -> "À venir";
        };
    }
}