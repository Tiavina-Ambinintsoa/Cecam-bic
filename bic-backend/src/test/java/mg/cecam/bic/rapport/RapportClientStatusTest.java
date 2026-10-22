package mg.cecam.bic.rapport;

import mg.cecam.bic.audit.JournalAudit;
import mg.cecam.bic.client.Client;
import mg.cecam.bic.client.ClientService;
import mg.cecam.bic.contrat.Contrat;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
}
