package mg.cecam.bic.common.enums;

import java.text.Normalizer;
import java.util.Locale;

public enum BucketRetard {
    A_JOUR("À jour"),
    JOURS_1_29("1–29 jours"),
    JOURS_30_59("30–59 jours"),
    JOURS_60_89("60–89 jours"),
    JOURS_90_119("90–119 jours"),
    JOURS_120_149("120–149 jours"),
    JOURS_150_179("150–179 jours"),
    JOURS_180_PLUS("180+ jours");

    private final String libelle;
    BucketRetard(String libelle) { this.libelle = libelle; }
    public String libelle() { return libelle; }

    public static BucketRetard depuisJours(Integer jours) {
        if (jours == null) return null;
        if (jours <= 0) return A_JOUR;
        if (jours < 30) return JOURS_1_29;
        if (jours < 60) return JOURS_30_59;
        if (jours < 90) return JOURS_60_89;
        if (jours < 120) return JOURS_90_119;
        if (jours < 150) return JOURS_120_149;
        if (jours < 180) return JOURS_150_179;
        return JOURS_180_PLUS;
    }

    public static BucketRetard depuisStatut(String statut) {
        if (statut == null || statut.isBlank()) return null;
        String s = Normalizer.normalize(statut, Normalizer.Form.NFD).replaceAll("\\p{M}+", "")
                .toUpperCase(Locale.ROOT).replace('–', '-').replace('—', '-').replace(" ", "");
        if (s.contains("180+") || s.contains("180JOUR") || s.contains("180PLUS")) return JOURS_180_PLUS;
        if (s.contains("150-179")) return JOURS_150_179;
        if (s.contains("120-149")) return JOURS_120_149;
        if (s.contains("90-119")) return JOURS_90_119;
        if (s.contains("60-89")) return JOURS_60_89;
        // A generic "impayé" source does not reveal the actual number of overdue days.
        if (s.contains("30-59")) return JOURS_30_59;
        if (s.contains("1-29") || s.contains("1A29") || s.contains("RETARDCOURT")) return JOURS_1_29;
        if (s.equals("OK") || s.contains("PAYEATEMPS") || s.contains("AJOUR")) return A_JOUR;
        return null;
    }
}
