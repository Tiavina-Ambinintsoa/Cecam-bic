package mg.cecam.bic.score;

import mg.cecam.bic.contrat.HistoriquePaiementMensuel;
import mg.cecam.bic.contrat.Contrat;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class ScoreServiceRulesTest {

    @Test
    void statutDeclareInconnuNEstPasInterpreteCommeUnPaiementAHeure() {
        assertEquals("A_TEMPS", ScoreService.normaliserStatutDernierContrat(" payé à temps "));
        assertEquals("NON_REMBOURSE", ScoreService.normaliserStatutDernierContrat("IMPAYÉ"));
        assertNull(ScoreService.normaliserStatutDernierContrat("statut inconnu"));
        assertNull(ScoreService.normaliserStatutDernierContrat(null));
    }

    @Test
    void joursDeRetardNegatifsSontTraitesCommeUneAbsenceDeRetard() {
        HistoriquePaiementMensuel observation = HistoriquePaiementMensuel.builder()
                .mois(LocalDate.of(2026, 1, 1))
                .joursDeRetard(-12)
                .statut("ANTICIPATION")
                .build();

        assertEquals(0, ScoreService.joursObserves(observation));
    }

    @Test
    void statutsExplicitementAHeureSontReconnusSansInventerDeBucket() {
        HistoriquePaiementMensuel observation = HistoriquePaiementMensuel.builder()
                .mois(LocalDate.of(2026, 1, 1))
                .statut("A_TEMPS")
                .build();

        assertEquals(0, ScoreService.joursObserves(observation));
    }

    @Test
    void statutDeRetardContradictoireAvecUnJourZeroEstEcarte() {
        HistoriquePaiementMensuel observation = HistoriquePaiementMensuel.builder()
                .mois(LocalDate.of(2026, 1, 1))
                .joursDeRetard(0)
                .statut("EN_RETARD")
                .build();

        assertNull(ScoreService.joursObserves(observation));
    }

    @Test
    void periodiciteInconnueNeDevientPasImplicitementMensuelle() {
        assertEquals(1, Contrat.moisEntreEcheances("Mensuelle"));
        assertEquals(3, Contrat.moisEntreEcheances("Trimestrielle"));
        assertEquals(6, Contrat.moisEntreEcheances("Semestrielle"));
        assertEquals(12, Contrat.moisEntreEcheances("Annuelle"));
        assertEquals(0, Contrat.moisEntreEcheances("Hebdomadaire"));
        assertEquals(0, Contrat.moisEntreEcheances(null));
    }

    @Test
    void chargeAnnuelleRespecteLeNombreDEcheancesEtLaPeriodicite() {
        Contrat contrat = Contrat.builder()
                .periodicitePaiement("Trimestrielle")
                .nombreEcheancesRestantes(8)
                .montantEcheanceMensuelle(BigDecimal.valueOf(300))
                .build();

        assertTrue(BigDecimal.valueOf(1200).compareTo(ScoreService.chargeAnnuelle(
                contrat, BigDecimal.valueOf(2400), List.of(), LocalDate.of(2026, 1, 1))) == 0);
    }

    @Test
    void chargeAnnuelleInconnueEstExclueAuLieuDEtreSupposeeMensuelle() {
        Contrat contrat = Contrat.builder()
                .periodicitePaiement("Hebdomadaire")
                .nombreTotalEcheances(12)
                .montantEcheanceMensuelle(BigDecimal.valueOf(300))
                .build();

        assertNull(ScoreService.chargeAnnuelle(
                contrat, BigDecimal.valueOf(2400), List.of(), LocalDate.of(2026, 1, 1)));
    }

    @Test
    void statutFormelNormaliseLesAccents() {
        assertTrue(ScoreService.pireStatutFormel("Déchéance"));
        assertFalse(ScoreService.pireStatutFormel("À jour"));
    }
}
