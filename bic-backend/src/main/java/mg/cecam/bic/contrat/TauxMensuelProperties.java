package mg.cecam.bic.contrat;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Map;

/**
 * Taux d'intérêt mensuels par type de crédit, configurables dans application.yml.
 * <p>Clé YAML : {@code app.taux-mensuel.*}</p>
 */
@Component
@ConfigurationProperties(prefix = "app.taux-mensuel")
@Getter @Setter
public class TauxMensuelProperties {

    private BigDecimal rfr = BigDecimal.valueOf(2.75);
    private BigDecimal lvm = BigDecimal.valueOf(2.75);
    private BigDecimal cim = BigDecimal.valueOf(2.75);
    private BigDecimal pro = BigDecimal.valueOf(3.5);
    private BigDecimal gcv = BigDecimal.valueOf(3.0);
    private BigDecimal defaut = BigDecimal.valueOf(3.0);

    /** Retourne le taux mensuel (en %) pour le type de crédit donné, ou le taux par défaut. */
    public BigDecimal pourType(String typeCredit) {
        if (typeCredit == null || typeCredit.isBlank()) return defaut;
        return switch (typeCredit.trim().toUpperCase(Locale.ROOT)) {
            case "RFR", "LVM", "CIM" -> rfr;
            case "PRO" -> pro;
            case "GCV" -> gcv;
            default -> defaut;
        };
    }
}
