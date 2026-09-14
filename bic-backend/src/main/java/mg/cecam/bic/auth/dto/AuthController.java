package mg.cecam.bic.auth;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.auth.dto.LoginRequest;
import mg.cecam.bic.auth.dto.LoginResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findByNomUtilisateur(request.nomUtilisateur())
                .filter(u -> Boolean.TRUE.equals(u.getActif()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides"));

        if (!passwordEncoder.matches(request.motDePasse(), utilisateur.getMotDePasse())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Identifiants invalides");
        }

        String token = jwtUtil.genererToken(utilisateur.getNomUtilisateur(), utilisateur.getRole().name());
        return ResponseEntity.ok(new LoginResponse(token, utilisateur.getNomUtilisateur(), utilisateur.getRole().name()));
    }
}