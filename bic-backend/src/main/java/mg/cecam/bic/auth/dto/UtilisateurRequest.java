package mg.cecam.bic.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import mg.cecam.bic.common.enums.Role;

public record UtilisateurRequest(
        @NotBlank(message = "Le nom d'utilisateur est obligatoire")
        String nomUtilisateur,

        String motDePasse,

        @NotNull(message = "Le rôle est obligatoire")
        Role role,

        Boolean actif
) {}
