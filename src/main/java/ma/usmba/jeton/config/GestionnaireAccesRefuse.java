package ma.usmba.jeton.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class GestionnaireAccesRefuse implements AccessDeniedHandler {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest requete,
                       HttpServletResponse reponse,
                       AccessDeniedException exception) throws IOException {

        Authentication authentification = SecurityContextHolder.getContext().getAuthentication();
        List<String> roles = authentification == null ? List.of()
                : authentification.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList();

        reponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
        reponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        reponse.setCharacterEncoding("UTF-8");

        Map<String, Object> corps = new LinkedHashMap<>();
        corps.put("horodatage", OffsetDateTime.now().toString());
        corps.put("statut", 403);
        corps.put("erreur", "Acces refuse");
        corps.put("message", "Votre jeton est valide mais ne possede pas le role requis");
        corps.put("roles", roles);
        corps.put("chemin", requete.getRequestURI());

        mapper.writeValue(reponse.getOutputStream(), corps);
    }
}
