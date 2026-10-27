package mg.cecam.bic.score;

import mg.cecam.bic.contrat.Contrat;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class CouvertureGarantie {

    private CouvertureGarantie() {}

    public static BigDecimal ratioPour(Contrat contrat) {
        BigDecimal ratioEnregistre = contrat.getRatioCouvertureGarantie();
        if (ratioEnregistre != null) {
            return ratioEnregistre.signum() >= 0 ? ratioEnregistre : null;
        }

        BigDecimal valeurGarantie = contrat.getValeurGarantie();
        BigDecimal totalDemande = contrat.getMontantTotalDemandeInteret() != null
                ? contrat.getMontantTotalDemandeInteret() : contrat.totalDu();
        if (valeurGarantie == null || valeurGarantie.signum() < 0
                || totalDemande == null || totalDemande.signum() <= 0) {
            return null;
        }

        return valeurGarantie.multiply(BigDecimal.valueOf(100))
                .divide(totalDemande, 2, RoundingMode.HALF_UP);
    }
}
