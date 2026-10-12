package mg.cecam.bic.contrat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import mg.cecam.bic.contrat.dto.ModificationContratRequest;
import mg.cecam.bic.contrat.dto.ModificationContratResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/demandes-modification")
@RequiredArgsConstructor
public class ModificationContratController {

    private final ModificationContratService service;

    @PostMapping("/contrats/{contratId}")
    public ResponseEntity<ModificationContratResponse> soumettre(@PathVariable Long contratId,
                                                                  @Valid @RequestBody ModificationContratRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.soumettre(contratId, request));
    }

    @GetMapping("/mes-demandes")
    public ResponseEntity<List<ModificationContratResponse>> mesDemandes() {
        return ResponseEntity.ok(service.listerMesDemandes());
    }

    @GetMapping("/en-attente")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ModificationContratResponse>> demandesEnAttente() {
        return ResponseEntity.ok(service.listerEnAttente());
    }

    @PostMapping("/{id}/approuver")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ModificationContratResponse> approuver(@PathVariable Long id) {
        return ResponseEntity.ok(service.approuver(id));
    }

    public record RefusRequest(@Size(max = 500) String commentaire) {}

    @PostMapping("/{id}/refuser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ModificationContratResponse> refuser(@PathVariable Long id,
                                                                 @Valid @RequestBody RefusRequest request) {
        return ResponseEntity.ok(service.refuser(id, request.commentaire()));
    }
}
