package mg.cecam.bic.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank String nomUtilisateur,
        @NotBlank String motDePasse) {}