package mg.cecam.bic.rapport;

import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.RoleClient;
import mg.cecam.bic.contrat.Contrat;
import mg.cecam.bic.contrat.Echeance;
import mg.cecam.bic.rapport.dto.SituationFinanciereMensuelleDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service
public class SituationFinanciereMensuelleService {
    private record Cle(String categorie, String devise) {}
    private static final class Totaux {
        BigDecimal encours = BigDecimal.ZERO;
        BigDecimal impayes = BigDecimal.ZERO;
        Set<Long> contrats = new HashSet<>();
    }

    public List<SituationFinanciereMensuelleDTO> calculer(List<Contrat> contrats,
            Map<Long, List<Echeance>> echeances, LocalDate reference) {
        List<Contrat> avecEcheanciers = contrats.stream()
                .filter(c -> c.getRoleClient() != RoleClient.GARANT)
                .filter(c -> c.getPhaseDemande() == PhaseDemande.ACTIF || c.getPhaseDemande() == PhaseDemande.FERME)
                .filter(c -> !echeances.getOrDefault(c.getId(), List.of()).isEmpty()).toList();
        LocalDate premiere = avecEcheanciers.stream()
                .flatMap(c -> echeances.getOrDefault(c.getId(), List.of()).stream())
                .map(Echeance::getDateEcheance).filter(Objects::nonNull)
                .min(Comparator.naturalOrder()).orElse(null);
        if (premiere == null) return List.of();

        YearMonth debut = YearMonth.from(premiere);
        YearMonth fin = YearMonth.from(reference);
        List<SituationFinanciereMensuelleDTO> lignes = new ArrayList<>();
        while (!debut.isAfter(fin)) {
            LocalDate borne = debut.atEndOfMonth();
            Map<Cle, Totaux> groupes = new HashMap<>();
            for (Contrat contrat : avecEcheanciers) {
                String devise = contrat.getDevise() == null ? "Non renseignée" : contrat.getDevise();
                Cle cle = new Cle(categorie(contrat), devise);
                for (Echeance e : echeances.getOrDefault(contrat.getId(), List.of())) {
                    if (e.getDateEcheance() == null || e.getMontantDu() == null) continue;
                    BigDecimal paye = BigDecimal.ZERO;
                    if (e.getDatePaiement() != null && !e.getDatePaiement().isAfter(borne)) {
                        paye = e.getMontantPaye() == null ? e.getMontantDu() : e.getMontantPaye();
                    } else if (e.getDatePaiement() == null && debut.equals(fin) && e.estPayee()) {
                        paye = e.getMontantPaye() == null ? e.getMontantDu() : e.getMontantPaye();
                    }
                    BigDecimal restant = e.getMontantDu().subtract(paye).max(BigDecimal.ZERO);
                    if (restant.signum() == 0) continue;
                    Totaux total = groupes.computeIfAbsent(cle, ignored -> new Totaux());
                    total.encours = total.encours.add(restant);
                    if (!e.getDateEcheance().isAfter(borne)) total.impayes = total.impayes.add(restant);
                    total.contrats.add(contrat.getId());
                }
            }
            for (var groupe : groupes.entrySet()) {
                Totaux t = groupe.getValue();
                lignes.add(new SituationFinanciereMensuelleDTO(debut.toString(), groupe.getKey().categorie(),
                        groupe.getKey().devise(), t.encours, t.impayes, t.contrats.size()));
            }
            debut = debut.plusMonths(1);
        }
        lignes.sort(Comparator.comparing(SituationFinanciereMensuelleDTO::mois)
                .thenComparing(SituationFinanciereMensuelleDTO::categorie)
                .thenComparing(SituationFinanciereMensuelleDTO::devise));
        return List.copyOf(lignes);
    }

    private String categorie(Contrat c) {
        String t = c.getTypeContrat() == null ? "" : c.getTypeContrat().toLowerCase(Locale.ROOT);
        if (t.contains("découvert") || t.contains("decouvert")) return "Financements sans échéanciers";
        if (t.contains("carte")) return "Cartes de Crédit";
        if (t.contains("service") || t.contains("abonnement")) return "Services";
        return "Financements avec échéanciers";
    }
}
