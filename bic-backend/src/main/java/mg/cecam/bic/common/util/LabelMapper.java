package mg.cecam.bic.common.util;

import mg.cecam.bic.common.enums.MotifCloture;
import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.RoleClient;
import mg.cecam.bic.common.enums.StatutEcheance;

import java.text.Normalizer;

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

    public static String phase(PhaseDemande p) {
        return phase(p, null);
    }

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

    public static String codeGrille(StatutEcheance s) {
        if (s == null) return "";
        return switch (s) {
            case PAYE_A_TEMPS -> "OK";
            case EN_RETARD -> "R";
            case IMPAYE -> "IMP";
            case A_VENIR -> "";
        };
    }

    public static String codeGrilleCrif(String statut, Integer jours) {
        String s = normaliser(statut);
        if (!s.isEmpty()) {
            if (s.equals("NOUV") || s.contains("RECENT")) return "NOUV";
            if (s.equals("NDIS") || s.contains("NON DISPONIBLE") || s.contains("INDISPONIBLE")) return "NDIS";
            if (s.contains("180")) return "180+";
            if (s.contains("150")) return "150+";
            if (s.contains("120")) return "120+";
            if (s.contains("90")) return "90+";
            if (s.contains("60")) return "60+";
            if (s.contains("30")) return "30+";
            if (s.contains("1-29") || s.contains("1 - 29")) return "1-29";
            if (s.equals("OK") || s.contains("PAYE A TEMPS") || s.equals("A TEMPS")) return "OK";
        }
        if (jours != null) {
            if (jours <= 0) return "NDIS";
            if (jours < 30) return "1-29";
            if (jours < 60) return "30+";
            if (jours < 90) return "60+";
            if (jours < 120) return "90+";
            if (jours < 150) return "120+";
            if (jours < 180) return "150+";
            return "180+";
        }
        if (s.contains("RETARD") || s.contains("IMPAYE")) return "NDIS";
        return "";
    }

    public static String libelleJoursRetardCrif(Integer jours, String statut) {
        String s = normaliser(statut);
        if (!s.isEmpty()) {
            if (s.equals("NOUV") || s.contains("RECENT")) return "Trop récent";
            if (s.equals("NDIS") || s.contains("NON DISPONIBLE") || s.contains("INDISPONIBLE")) return "-";
            if (s.contains("180")) return "Plus de 180 jours de retard";
            if (s.contains("150")) return "150-179 jours de retard";
            if (s.contains("120")) return "120-149 jours de retard";
            if (s.contains("90")) return "90-119 jours de retard";
            if (s.contains("60")) return "60-89 jours de retard";
            if (s.contains("30")) return "30-59 jours de retard";
            if (s.contains("1-29") || s.contains("1 - 29")) return "1-29 jours de retard";
            if (s.equals("OK") || s.contains("PAYE A TEMPS") || s.equals("A TEMPS")) return "Payé à temps";
        }
        if (jours != null) {
            if (jours <= 0) return "0 jour de retard";
            if (jours < 30) return "1-29 jours de retard";
            if (jours < 60) return "30-59 jours de retard";
            if (jours < 90) return "60-89 jours de retard";
            if (jours < 120) return "90-119 jours de retard";
            if (jours < 150) return "120-149 jours de retard";
            if (jours < 180) return "150-179 jours de retard";
            return "Plus de 180 jours de retard";
        }
        return "-";
    }

    private static String normaliser(String valeur) {
        if (valeur == null || valeur.isBlank()) return "";
        return Normalizer.normalize(valeur.trim().toUpperCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('–', '-')
                .replace('−', '-')
                .replaceAll("\\s+", " ");
    }

    public static String couleurCodeCrif(String code) {
        if (code == null) return "#FFFFFF";
        return switch (code) {
            case "OK" -> "#C8E6C9";
            case "NOUV", "NDIS", "-" -> "#FFFFFF";
            case "1-29" -> "#FFF59D";
            case "30+" -> "#FFE082";
            case "60+" -> "#FFB74D";
            case "90+" -> "#FF8A65";
            case "120+" -> "#FF7043";
            case "150+" -> "#EF5350";
            case "180+" -> "#E53935";
            default -> "#FFFFFF";
        };
    }
}
