package mg.cecam.bic.client;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mg.cecam.bic.client.dto.ClientEnregistrementResponse;
import mg.cecam.bic.client.dto.ClientRequest;
import mg.cecam.bic.client.dto.ClientResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * Endpoints REST pour la gestion des clients (sociétaires).
 * <p>La création est accessible à tous les utilisateurs authentifiés ;
 * la mise à jour, la suppression et la liste paginée sont réservées aux administrateurs.</p>
 */
@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final ClientRepository clientRepository;

    /** Liste paginée de tous les clients — admin uniquement. */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<ClientResponse>> lister(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String q) {
        PageRequest pageable = PageRequest.of(page, Math.min(size, 100));
        Page<Client> clients = (q != null && !q.isBlank())
                ? clientRepository.rechercherParMotCle(q.trim(), pageable)
                : clientRepository.findAllByOrderByIdDesc(pageable);
        Page<ClientResponse> response = clients.map(c -> new ClientResponse(
                c.getId(), c.getCodeClientCb(), c.getTitre(), c.getCategorieTiersCode(),
                c.getPrenom(), c.getDeuxiemePrenom(), c.getNom(),
                c.getDateNaissance(), c.getVilleNaissance(), c.getPaysNaissance(),
                c.getGenre(), c.getNationalite(), c.getEtatCivil(),
                c.getTelephone(), c.getCodeClientEtablissement(), c.getDateAdhesion()));
        return ResponseEntity.ok(response);
    }

    /** Supprime un client et tous ses contrats liés — admin uniquement. */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        if (!clientRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Client introuvable");
        }
        clientService.supprimer(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Client> mettreAJour(@PathVariable Long id, @Valid @RequestBody ClientRequest request) {
        return ResponseEntity.ok(clientService.mettreAJour(id, request));
    }

    @PostMapping("/{id}/adresses")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Adresse> ajouterAdresse(@PathVariable Long id, @Valid @RequestBody ClientRequest.AdresseRequest request) {
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
                client.getEtatCivil(),
                client.getTelephone(),
                client.getCodeClientEtablissement(),
                client.getDateAdhesion()
        );

        return ResponseEntity.ok(response);
    }
}
