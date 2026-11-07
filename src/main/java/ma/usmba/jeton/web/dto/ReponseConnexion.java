package ma.usmba.jeton.web.dto;

import java.util.List;

public record ReponseConnexion(
        String jeton,
        String type,
        String identifiant,
        String nomComplet,
        long expireDans,
        List<String> roles) {
}
