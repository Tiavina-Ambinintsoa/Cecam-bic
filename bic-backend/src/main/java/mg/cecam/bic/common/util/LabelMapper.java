// mg/cecam/bic/common/util/LabelMapper.java  — MODIFIÉ
package mg.cecam.bic.common.util;

import mg.cecam.bic.common.enums.MotifCloture;
import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.RoleClient;
import mg.cecam.bic.common.enums.StatutEcheance;

public final class LabelMapper {
    private LabelMapper() {}

    public static String role(RoleClient r) {
        if (r == null) return "-";
        return switch (r) {
            case TITULAIRE -> "Titulaire";
            case CO_TITULAIRE -> "Co-Titulaire";
            case GARANT -> "Garant";
        };
    }

    /** Libellé simple, sans distinction de motif de clôture. */
    public static String phase(PhaseDemande p) {
        return phase(p, null);
    }

    /**
     * Libellé CRIF. Une phase FERME se lit « Fin à terme » ou « Fin Anticipée »
     * selon le motif, comme dans le rapport CRIF page 3.
     */
    public static String phase(PhaseDemande p, MotifCloture motif) {
        if (p == null) return "-";
        return switch (p) {
            case DEMANDE_EN_COURS -> "Demande en cours";
            case ACTIF -> "Actif";
            case REFUSE -> "Refusé";
            case ABANDONNE -> "Abandonné";
            case FERME -> motif == MotifCloture.FIN_ANTICIPEE ? "Fin Anticipée" : "Fin à terme";
        };
    }

    public static String statutEcheance(StatutEcheance s) {
        if (s == null) return "-";
        return switch (s) {
            case PAYE_A_TEMPS -> "Payé à temps";
            case EN_RETARD -> "En retard";
            case IMPAYE -> "Impayé";
            case A_VENIR -> "À venir";
        };
    }

    /** Code court affiché dans la grille mensuelle, façon CRIF (« OK », « R », « IMP »). */
    public static String codeGrille(StatutEcheance s) {
        if (s == null) return "";
        return switch (s) {
            case PAYE_A_TEMPS -> "OK";
            case EN_RETARD -> "R";
            case IMPAYE -> "IMP";
            case A_VENIR -> "";
        };
    }
}