package mg.cecam.bic.common.enums;

/** Tranches de retard des rapports CRIF : libellé détaillé, code de grille et couleur. */
public enum TrancheRetard {
    A_TEMPS("Payé à temps", "OK", "#8FD18F"),
    R_1_29("1-29 jours de retard", "1-29", "#CDE8A0"),
    R_30_59("30-59 jours de retard", "30+", "#F7E26B"),
    R_60_89("60-89 jours de retard", "60+", "#F5C242"),
    R_90_119("90-119 jours de retard", "90+", "#F29A38"),
    R_120_149("120-149 jours de retard", "120+", "#EC6F2D"),
    R_150_179("150-179 jours de retard", "150+", "#E04B2C"),
    R_180_PLUS("Plus de 180 jours de retard", "180+", "#C62828"),
    TROP_RECENT("Trop récent", "NOUV", "#ECEFF1"),
    /** Réservé aux mois non déclarés (NDIS) : pas encore alimenté. */
    NON_DISPONIBLE("-", "NDIS", "#CFD8DC");

    private final String libelle;
    private final String code;
    private final String couleurHex;

    TrancheRetard(String libelle, String code, String couleurHex) {
        this.libelle = libelle;
        this.code = code;
        this.couleurHex = couleurHex;
    }

    public String getLibelle() { return libelle; }
    public String getCode() { return code; }
    public String getCouleurHex() { return couleurHex; }

    /** 0 = à temps … 7 = 180 jours et plus ; -1 pour les tranches qui ne sont pas un retard. */
    public int rang() {
        return switch (this) {
            case A_TEMPS -> 0;
            case R_1_29 -> 1;
            case R_30_59 -> 2;
            case R_60_89 -> 3;
            case R_90_119 -> 4;
            case R_120_149 -> 5;
            case R_150_179 -> 6;
            case R_180_PLUS -> 7;
            case TROP_RECENT, NON_DISPONIBLE -> -1;
        };
    }

    public static TrancheRetard depuisJours(int jours) {
        if (jours <= 0) return A_TEMPS;
        if (jours < 30) return R_1_29;
        if (jours < 60) return R_30_59;
        if (jours < 90) return R_60_89;
        if (jours < 120) return R_90_119;
        if (jours < 150) return R_120_149;
        if (jours < 180) return R_150_179;
        return R_180_PLUS;
    }

    /** Utilisé par le gabarit PDF pour « Max Nombre de jours de retard ». */
    public static String libelleDepuisJours(Integer jours) {
        return jours == null ? "-" : depuisJours(jours).libelle;
    }
}