package ma.usmba.jeton.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GestionnaireExceptions {

    @ExceptionHandler(IdentifiantsInvalidesException.class)
    public ResponseEntity<Map<String, Object>> identifiantsInvalides(IdentifiantsInvalidesException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(corps(401, "Identifiants refuses", e.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> mauvaisIdentifiants(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(corps(401, "Identifiants refuses", "Identifiant ou mot de passe incorrect"));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> conflit(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(corps(409, "Conflit", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> introuvable(IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(corps(404, "Ressource introuvable", e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validation(MethodArgumentNotValidException e) {
        List<String> details = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        Map<String, Object> corps = corps(400, "Requete invalide", "Certains champs sont incorrects");
        corps.put("details", details);
        return ResponseEntity.badRequest().body(corps);
    }

    private Map<String, Object> corps(int statut, String erreur, String message) {
        Map<String, Object> corps = new LinkedHashMap<>();
        corps.put("horodatage", OffsetDateTime.now().toString());
        corps.put("statut", statut);
        corps.put("erreur", erreur);
        corps.put("message", message);
        return corps;
    }
}
