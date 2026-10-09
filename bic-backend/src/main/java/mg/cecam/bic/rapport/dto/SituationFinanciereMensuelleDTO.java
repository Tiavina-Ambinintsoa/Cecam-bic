package mg.cecam.bic.rapport.dto;

import java.math.BigDecimal;

public record SituationFinanciereMensuelleDTO(
        String mois, String categorie, String devise,
        BigDecimal encours, BigDecimal impayes, int contratsAvecEcheances
) {}
