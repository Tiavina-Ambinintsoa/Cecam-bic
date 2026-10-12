package mg.cecam.bic.auth.dto;

import mg.cecam.bic.auth.Utilisateur;
import mg.cecam.bic.common.enums.Role;

public record UtilisateurResponse(
        Long id,
        String nomUtilisateur,
        Role role,
        Boolean actif
) {
    public static UtilisateurResponse fromEntity(Utilisateur utilisateur) {
        return new UtilisateurResponse(
                utilisateur.getId(),
                utilisateur.getNomUtilisateur(),
                utilisateur.getRole(),
                utilisateur.getActif()
        );
    }
}
