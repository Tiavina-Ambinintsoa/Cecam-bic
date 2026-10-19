package mg.cecam.bic.client;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mg.cecam.bic.client.dto.ClientEnregistrementResponse;
import mg.cecam.bic.client.dto.ClientRequest;
import mg.cecam.bic.client.dto.ClientResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final ClientRepository clientRepository;

    @PutMapping("/{id}")
    public ResponseEntity<Client> mettreAJour(@PathVariable Long id, @Valid @RequestBody ClientRequest request) {
        return ResponseEntity.ok(clientService.mettreAJour(id, request));
    }

    @PostMapping("/{id}/adresses")
    public ResponseEntity<Adresse> ajouterAdresse(@PathVariable Long id, @RequestBody ClientRequest.AdresseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.ajouterAdresse(id, request));
    }

    @PostMapping
    public ResponseEntity<ClientEnregistrementResponse> creer(@Valid @RequestBody ClientRequest request) {
        ClientEnregistrementResponse result = clientService.enregistrerOuRecuperer(request);
        return ResponseEntity.status(result.clientTrouve() ? HttpStatus.OK : HttpStatus.CREATED).body(result);
    }

    @GetMapping("/recherche")
    public ResponseEntity<Client> rechercher(
            @RequestParam(required = false) String typeIdentifiant,
            @RequestParam(required = false) String numero,
            @RequestParam(required = false) String codeClientCb,
            @RequestParam(required = false) String codeClientEtablissement) {
        Client client = null;
        if (codeClientCb != null && !codeClientCb.isBlank()) {
            client = clientRepository.findByCodeClientCb(codeClientCb).orElse(null);
        } else if (codeClientEtablissement != null && !codeClientEtablissement.isBlank()) {
            client = clientRepository.findByCodeClientEtablissement(codeClientEtablissement).orElse(null);
        } else if (typeIdentifiant != null && numero != null) {
            client = clientService.rechercherParIdentifiant(typeIdentifiant, numero);
        }
        return client == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(client);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientResponse> obtenir(@PathVariable Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client introuvable"));

        ClientResponse response = new ClientResponse(
                client.getId(),
                client.getCodeClientCb(),
                client.getTitre(),
                client.getCategorieTiersCode(),
                client.getPrenom(),
                client.getDeuxiemePrenom(),
                client.getNom(),
                client.getDateNaissance(),
                client.getVilleNaissance(),
                client.getPaysNaissance(),
                client.getGenre(),
                client.getNationalite(),
                client.getEtatCivil()
        );

        return ResponseEntity.ok(response);
    }
}
