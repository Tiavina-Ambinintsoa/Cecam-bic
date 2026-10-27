package mg.cecam.bic.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Propriétés applicatives globales déclarées sous le préfixe {@code app.*}
 * dans {@code application.yml} (CORS, JWT).
 * <p>Permet la validation forte, l'autocomplétion IDE et la suppression des avertissements YAML.</p>
 */
@Component
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppProperties {

    private Cors cors = new Cors();
    private Jwt jwt = new Jwt();

    @Getter
    @Setter
    public static class Cors {
        private String allowedOrigins;
    }

    @Getter
    @Setter
    public static class Jwt {
        private String secret;
        private Long expirationMs;
    }
}
