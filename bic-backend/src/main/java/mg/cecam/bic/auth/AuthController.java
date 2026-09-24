package mg.cecam.bic.auth;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.auth.dto.LoginRequest;
import mg.cecam.bic.auth.dto.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private static final int TENTATIVES_MAX = 5;
    private static final Duration DUREE_BLOCAGE = Duration.ofMinutes(15);

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private final Map<String, AtomicInteger> tentatives = new ConcurrentHashMap<>();
    private final Map<String, Instant> blocages = new ConcurrentHashMap<>();

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String identifiant = request.nomUtilisateur() == null ? "" : request.nomUtilisateur().trim();

        Instant blocage = blocages.get(identifiant);
        if (blocage != null && blocage.isAfter(Instant.now())) {
            long minutes = Math.max(1, Duration.between(Instant.now(), blocage).toMinutes());
            return probleme(HttpStatus.TOO_MANY_REQUESTS, "TROP_DE_TENTATIVES",
                    "Trop de tentatives. Réessayez dans " + minutes + " minute(s).");
        }

        Optional<Utilisateur> trouve = utilisateurRepository.findByNomUtilisateur(identifiant);

        boolean motDePasseValide = trouve
                .map(u -> passwordEncoder.matches(request.motDePasse(), u.getMotDePasse()))
                .orElse(false);

        if (!motDePasseValide) {
            enregistrerEchec(identifiant);
            return probleme(HttpStatus.UNAUTHORIZED, "IDENTIFIANTS_INVALIDES",
                    "Nom d'utilisateur ou mot de passe incorrect.");
        }

        Utilisateur utilisateur = trouve.get();
        if (!Boolean.TRUE.equals(utilisateur.getActif())) {
            return probleme(HttpStatus.FORBIDDEN, "COMPTE_DESACTIVE",
                    "Ce compte est désactivé. Contactez l'administrateur.");
        }

        tentatives.remove(identifiant);
        blocages.remove(identifiant);

        String token = jwtUtil.genererToken(utilisateur.getNomUtilisateur(), utilisateur.getRole().name());
        return ResponseEntity.ok(new LoginResponse(token, utilisateur.getNomUtilisateur(),
                utilisateur.getRole().name()));
    }

    private void enregistrerEchec(String identifiant) {
        int n = tentatives.computeIfAbsent(identifiant, k -> new AtomicInteger()).incrementAndGet();
        if (n >= TENTATIVES_MAX) {
            blocages.put(identifiant, Instant.now().plus(DUREE_BLOCAGE));
            tentatives.remove(identifiant);
        }
    }

    private ResponseEntity<ProblemDetail> probleme(HttpStatus statut, String code, String message) {
        ProblemDetail pd = ProblemDetail.forStatus(statut);
        pd.setTitle("Connexion refusée");
        pd.setDetail(message);
        pd.setProperty("code", code);
        return ResponseEntity.status(statut).body(pd);
    }
}