package mg.cecam.bic.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail statut(ResponseStatusException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(ex.getStatusCode());
        pd.setDetail(ex.getReason());
        pd.setType(URI.create("urn:bic:erreur"));
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail validation(MethodArgumentNotValidException ex) {
        Map<String, String> champs = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> champs.put(e.getField(), e.getDefaultMessage()));
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Saisie invalide");
        pd.setDetail("Certains champs ne sont pas valides.");
        pd.setProperty("code", "VALIDATION");
        pd.setProperty("champs", champs);
        return pd;
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ProblemDetail integrite(org.springframework.dao.DataIntegrityViolationException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        pd.setTitle("Conflit de données");
        pd.setDetail("Une contrainte d'intégrité ou d'unicité a été enfreinte lors de l'enregistrement.");
        pd.setProperty("code", "CONFLIT_INTEGRITE");
        return pd;
    }

    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ProblemDetail accesRefuse(org.springframework.security.access.AccessDeniedException ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
        pd.setTitle("Accès refusé");
        pd.setDetail("Vous ne disposez pas des privilèges nécessaires pour effectuer cette action.");
        pd.setProperty("code", "ACCES_REFUSE");
        return pd;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail interne(Exception ex) {
        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        pd.setTitle("Erreur interne");
        pd.setDetail("Le serveur n'a pas pu traiter la demande.");
        pd.setProperty("code", "INTERNE");
        return pd;
    }
}