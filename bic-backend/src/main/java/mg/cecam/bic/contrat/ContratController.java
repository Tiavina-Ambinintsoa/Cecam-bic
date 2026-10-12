package mg.cecam.bic.contrat;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mg.cecam.bic.common.enums.MotifCloture;
import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.contrat.dto.ContratRequest;
import mg.cecam.bic.contrat.dto.HistoriqueContratRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/contrats")
@RequiredArgsConstructor
public class ContratController {

    private final ContratService contratService;
    private final ContratRepository contratRepository;

    @PostMapping
    public ResponseEntity<Contrat> creer(@Valid @RequestBody ContratRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contratService.creerDemande(request));
    }

    @GetMapping("/recherche")
    public ResponseEntity<Contrat> rechercher(@RequestParam(required = false) String identifiantContrat) {
        return contratRepository.findByIdentifiantContrat(identifiantContrat)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public record EcheanceMensuelleRequest(BigDecimal montantEcheanceMensuelle, String devise) {}

    public record CodesEtablissementRequest(String codeContratEtablissement, String codeDemandeEtablissement) {}

    @PatchMapping("/{id}/codes-etablissement")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> changerCodesEtablissement(@PathVariable Long id, @RequestBody CodesEtablissementRequest req) {
        contratService.modifierCodesEtablissement(id, req.codeContratEtablissement(), req.codeDemandeEtablissement());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/echeance-mensuelle")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> changerEcheanceMensuelle(@PathVariable Long id, @RequestBody EcheanceMensuelleRequest req) {
        contratService.changerEcheanceMensuelle(id, req.montantEcheanceMensuelle(), req.devise());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Contrat>> lister(@RequestParam(required = false) Long clientId) {
        List<Contrat> contrats = clientId != null
                ? contratService.listerParClient(clientId)
                : contratService.listerTous();
        return ResponseEntity.ok(contrats);
    }

    @PostMapping("/historique")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Contrat> saisirHistorique(@Valid @RequestBody HistoriqueContratRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contratService.saisirHistorique(req));
    }

    @PutMapping("/{id}/historique")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> modifierHistorique(@PathVariable Long id, @Valid @RequestBody HistoriqueContratRequest req) {
        contratService.modifierHistorique(id, req);
        return ResponseEntity.noContent().build();
    }

    public record PhaseRequest(PhaseDemande phase, MotifCloture motif) {}

    @PatchMapping("/{id}/phase")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Contrat> changerPhase(@PathVariable Long id, @RequestBody PhaseRequest req) {
        return ResponseEntity.ok(contratService.changerPhase(id, req.phase(), req.motif()));
    }
}
