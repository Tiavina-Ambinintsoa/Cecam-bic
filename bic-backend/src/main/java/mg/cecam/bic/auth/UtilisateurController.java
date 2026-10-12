package mg.cecam.bic.auth;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mg.cecam.bic.audit.AuditService;
import mg.cecam.bic.auth.dto.UtilisateurRequest;
import mg.cecam.bic.auth.dto.UtilisateurResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/utilisateurs")
@RequiredArgsConstructor
public class UtilisateurController {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UtilisateurResponse>> lister() {
        List<UtilisateurResponse> response = utilisateurRepository.findAll().stream()
                .map(UtilisateurResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UtilisateurResponse> obtenir(@PathVariable Long id) {
        return utilisateurRepository.findById(id)
                .map(u -> ResponseEntity.ok(UtilisateurResponse.fromEntity(u)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UtilisateurResponse> creer(@Valid @RequestBody UtilisateurRequest request) {
        if (request.motDePasse() == null || request.motDePasse().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        if (utilisateurRepository.findByNomUtilisateur(request.nomUtilisateur()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Utilisateur utilisateur = Utilisateur.builder()
                .nomUtilisateur(request.nomUtilisateur().trim())
                .motDePasse(passwordEncoder.encode(request.motDePasse()))
                .role(request.role())
                .actif(request.actif() != null ? request.actif() : true)
                .build();

        Utilisateur saved = utilisateurRepository.save(utilisateur);
        auditService.enregistrer("UTILISATEUR", saved.getId(), "CREATION",
                "Création de l'utilisateur " + saved.getNomUtilisateur());

        return ResponseEntity.status(HttpStatus.CREATED).body(UtilisateurResponse.fromEntity(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UtilisateurResponse> modifier(
            @PathVariable Long id,
            @Valid @RequestBody UtilisateurRequest request
    ) {
        Optional<Utilisateur> opt = utilisateurRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Utilisateur user = opt.get();

        if (!user.getNomUtilisateur().equalsIgnoreCase(request.nomUtilisateur().trim())) {
            if (utilisateurRepository.findByNomUtilisateur(request.nomUtilisateur().trim()).isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
        }

        user.setNomUtilisateur(request.nomUtilisateur().trim());
        if (request.role() != null) {
            user.setRole(request.role());
        }
        if (request.actif() != null) {
            user.setActif(request.actif());
        }
        if (request.motDePasse() != null && !request.motDePasse().isBlank()) {
            user.setMotDePasse(passwordEncoder.encode(request.motDePasse()));
        }

        Utilisateur updated = utilisateurRepository.save(user);
        auditService.enregistrer("UTILISATEUR", updated.getId(), "MODIFICATION",
                "Mise à jour de l'utilisateur " + updated.getNomUtilisateur());

        return ResponseEntity.ok(UtilisateurResponse.fromEntity(updated));
    }

    @PatchMapping("/{id}/activer")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UtilisateurResponse> basculerActif(@PathVariable Long id) {
        Optional<Utilisateur> opt = utilisateurRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Utilisateur user = opt.get();
        boolean newStatus = !Boolean.TRUE.equals(user.getActif());
        user.setActif(newStatus);

        Utilisateur updated = utilisateurRepository.save(user);
        String action = newStatus ? "ACTIVATION" : "DESACTIVATION";
        auditService.enregistrer("UTILISATEUR", updated.getId(), action,
                "Changement du statut actif à " + newStatus);

        return ResponseEntity.ok(UtilisateurResponse.fromEntity(updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        Optional<Utilisateur> opt = utilisateurRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Utilisateur user = opt.get();

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = (auth != null) ? auth.getName() : null;

        if (currentUsername != null && currentUsername.equalsIgnoreCase(user.getNomUtilisateur())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        utilisateurRepository.delete(user);
        auditService.enregistrer("UTILISATEUR", id, "SUPPRESSION",
                "Suppression de l'utilisateur " + user.getNomUtilisateur());

        return ResponseEntity.noContent().build();
    }
}