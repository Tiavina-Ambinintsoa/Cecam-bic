package mg.cecam.bic.rapport;

import mg.cecam.bic.audit.JournalAudit;
import mg.cecam.bic.client.Client;
import mg.cecam.bic.client.ClientService;
import mg.cecam.bic.contrat.Contrat;
import mg.cecam.bic.rapport.dto.RisqueGlobalDTO;
import mg.cecam.bic.rapport.dto.ScoreDTO;
import mg.cecam.bic.referentiel.GrilleScore;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RapportClientStatusTest {

    @Test
    void ficheRetrouveeParCinEstPreexistanteMemeSansAncienContrat() {
        Client client = Client.builder().dateAdhesion(LocalDate.of(2026, 3, 1)).build();
        Contrat demande = Contrat.builder().dateDemande(LocalDate.of(2026, 3, 1)).build();
        JournalAudit audit = JournalAudit.builder()
                .action("CONSULTATION")
                .detail(ClientService.DETAIL_NOUVELLE_DEMANDE_CLIENT_EXISTANT)
                .build();

        assertTrue(RapportService.clientPreexistant(client, demande, List.of(), List.of(audit)));
    }

    @Test
    void clientCreeLeJourMemeSansPreuveDeFicheExistanteNestPasDeclarePreexistant() {
        LocalDate date = LocalDate.of(2026, 3, 1);
        Client client = Client.builder().dateAdhesion(date).build();
        Contrat demande = Contrat.builder().dateDemande(date).build();

        assertFalse(RapportService.clientPreexistant(client, demande, List.of(), List.of()));
    }

    @Test
    void couvertureRelèveLeRisqueGlobalSansChangerLeScoreCB() {
        ScoreDTO score = new ScoreDTO(true, 700, "A", "Risque très faible", "VERT", "#16803C",
                null, null, Map.of(), List.of());
        List<GrilleScore> grille = List.of(
                bande("E", 300, 399, "Risque très élevé"),
                bande("D", 400, 459, "Risque élevé"),
                bande("C", 460, 499, "Risque moyen"),
                bande("B", 500, 529, "Risque faible"),
                bande("A", 530, 850, "Risque très faible"));

        RisqueGlobalDTO risqueTresFaibleGarantie = RapportService.calculerRisqueGlobal(
                score, BigDecimal.valueOf(49.99), grille);
        RisqueGlobalDTO risqueMoyenGarantie = RapportService.calculerRisqueGlobal(
                score, BigDecimal.valueOf(83), grille);
        RisqueGlobalDTO risqueSansMajoration = RapportService.calculerRisqueGlobal(
                score, BigDecimal.valueOf(100), grille);

        assertEquals("D", risqueTresFaibleGarantie.intervalle());
        assertEquals("C", risqueMoyenGarantie.intervalle());
        assertEquals("A", risqueSansMajoration.intervalle());
        assertEquals("A", score.intervalle());
        assertNull(RapportService.calculerRisqueGlobal(
                new ScoreDTO(false, null, null, null, null, null, null, null, Map.of(), List.of()),
                BigDecimal.valueOf(20), grille));
    }

    private GrilleScore bande(String intervalle, int minimum, int maximum, String categorie) {
        return GrilleScore.builder()
                .intervalle(intervalle)
                .scoreMin(minimum)
                .scoreMax(maximum)
                .categorieRisque(categorie)
                .couleur("VERT")
                .build();
    }
}
