package mg.cecam.bic.auth.dto;
public record LoginResponse(String token, String nomUtilisateur, String role) {}