// mg/cecam/bic/contrat/ContratController.java  — MODIFIÉ
package mg.cecam.bic.contrat;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mg.cecam.bic.common.enums.MotifCloture;
import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.contrat.dto.ContratRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contrats")
@RequiredArgsConstructor
public class ContratController {

    private final ContratService contratService;

    @PostMapping
    public ResponseEntity<Contrat> creer(@Valid @RequestBody ContratRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contratService.creerDemande(request));
    }

    @GetMapping
    public ResponseEntity<List<Contrat>> lister(@RequestParam(required = false) Long clientId) {
        List<Contrat> contrats = clientId != null
                ? contratService.listerParClient(clientId)
                : contratService.listerTous();
        return ResponseEntity.ok(contrats);
    }

    /** motif n'est lu que pour un passage en FERME ; null vaut FIN_A_TERME. */
    public record PhaseRequest(PhaseDemande phase, MotifCloture motif) {}

    @PatchMapping("/{id}/phase")
    public ResponseEntity<Contrat> changerPhase(@PathVariable Long id, @RequestBody PhaseRequest req) {
        return ResponseEntity.ok(contratService.changerPhase(id, req.phase(), req.motif()));
    }
}