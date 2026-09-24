package mg.cecam.bic.common.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class MontantFormatUtil {
    private MontantFormatUtil() {}

    private static final DecimalFormat FORMAT;
    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.FRENCH);
        symbols.setGroupingSeparator(' ');
        FORMAT = new DecimalFormat("#,##0", symbols);
    }

    public static String formatMontant(BigDecimal valeur) {
        return valeur == null ? "-" : FORMAT.format(valeur);
    }

    public static String formatEntier(Integer valeur) {
        return valeur == null ? "-" : FORMAT.format(valeur.longValue());
    }

    public static String formatJoursRetard(Integer jours) {
        if (jours == null) return "-";
        return jours <= 0 ? "Payé à temps" : FORMAT.format(jours.longValue()) + " j";
    }
}