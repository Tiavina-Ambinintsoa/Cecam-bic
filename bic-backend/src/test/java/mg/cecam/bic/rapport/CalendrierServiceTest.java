package mg.cecam.bic.rapport;

import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.StatutEcheance;
import mg.cecam.bic.contrat.Contrat;
import mg.cecam.bic.contrat.Echeance;
import mg.cecam.bic.rapport.dto.CelluleMoisDTO;
import mg.cecam.bic.rapport.dto.EncoursCategorieDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalendrierServiceTest {
    private final CalendrierService service = new CalendrierService();
    private final LocalDate reference = LocalDate.of(2025, 6, 30);

    @Test
    void coloreLesImpayesSelonLeStatutCrifEtMetOkEnVert() {
        Contrat contrat = Contrat.builder()
                .id(1L)
                .dateDebutContrat(LocalDate.of(2025, 5, 1))
                .phaseDemande(PhaseDemande.ACTIF)
                .build();
        List<Echeance> echeances = List.of(
                echeance(LocalDate.of(2025, 6, 15), "100", "100", LocalDate.of(2025, 6, 15)),
                echeance(LocalDate.of(2025, 6, 15), "80", "0", null),
                echeance(LocalDate.of(2025, 5, 26), "50", "0", null));

        EncoursCategorieDTO impayes = service.impayes(
                "Financements avec échéanciers", "CECAM", "MGA", List.of(contrat),
                Map.of(1L, echeances), reference);
        CelluleMoisDTO juin = impayes.lignes().stream()
                .filter(ligne -> ligne.annee() == 2025)
                .flatMap(ligne -> ligne.mois().stream())
                .filter(cellule -> cellule.mois().equals("JUIN"))
                .findFirst().orElseThrow();

        assertEquals(new BigDecimal("130"), juin.montant());
        assertEquals("30+", juin.statut());
        assertEquals("#FFE082", juin.couleurHex());
    }

    @Test
    void unMoisSansImpayesEstAfficheEnVertCommeOk() {
        Contrat contrat = Contrat.builder()
                .id(1L)
                .dateDebutContrat(LocalDate.of(2025, 5, 1))
                .phaseDemande(PhaseDemande.ACTIF)
                .build();

        EncoursCategorieDTO impayes = service.impayes(
                "Financements avec échéanciers", "CECAM", "MGA", List.of(contrat),
                Map.of(1L, List.of(echeance(LocalDate.of(2025, 6, 15), "100", "100", LocalDate.of(2025, 6, 15)))),
                reference);
        CelluleMoisDTO juin = impayes.lignes().getFirst().mois().get(5);

        assertEquals(BigDecimal.ZERO, juin.montant());
        assertEquals("OK", juin.statut());
        assertEquals("#C8E6C9", juin.couleurHex());
    }

    private Echeance echeance(LocalDate date, String montantDu, String montantPaye, LocalDate datePaiement) {
        return Echeance.builder()
                .numeroEcheance(1)
                .dateEcheance(date)
                .montantDu(new BigDecimal(montantDu))
                .montantPaye(new BigDecimal(montantPaye))
                .datePaiement(datePaiement)
                .statut(StatutEcheance.EN_RETARD)
                .build();
    }
}
