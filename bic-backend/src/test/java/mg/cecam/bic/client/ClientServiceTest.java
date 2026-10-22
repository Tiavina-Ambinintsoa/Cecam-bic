package mg.cecam.bic.client;

import mg.cecam.bic.audit.AuditService;
import mg.cecam.bic.client.dto.ClientRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {
    @Mock private ClientRepository clientRepository;
    @Mock private EmploiRepository emploiRepository;
    @Mock private AuditService auditService;

    @Test
    void demandeAvecCinFormateRetrouveLaFicheExistante() {
        Client clientExistant = Client.builder().id(12L).build();
        when(clientRepository.findAllByCinNormalise("123456789012"))
                .thenReturn(List.of(clientExistant));

        ClientService service = new ClientService(clientRepository, emploiRepository, auditService);
        ClientRequest request = new ClientRequest(
                null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, List.of(),
                List.of(new ClientRequest.IdentifiantRequest("CIN", "123 456 789 012")),
                null, null);

        var result = service.enregistrerOuRecuperer(request);

        assertTrue(result.clientTrouve());
        assertEquals(clientExistant, result.client());
        verify(clientRepository).findAllByCinNormalise("123456789012");
        verify(auditService).enregistrer("CLIENT", clientExistant.getId(), "CONSULTATION",
                ClientService.DETAIL_NOUVELLE_DEMANDE_CLIENT_EXISTANT);
    }

    @Test
    void normalisationCinRetireLesSeparateurs() {
        assertEquals("123456789012", ClientService.normaliserCin("123-456 789.012"));
    }
}
