package ma.usmba.jeton.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class PointEntreeNonAutorise implements AuthenticationEntryPoint {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest requete,
                         HttpServletResponse reponse,
                         AuthenticationException exception) throws IOException {

        reponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        reponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        reponse.setCharacterEncoding("UTF-8");

        Map<String, Object> corps = new LinkedHashMap<>();
        corps.put("horodatage", OffsetDateTime.now().toString());
        corps.put("statut", 401);
        corps.put("erreur", "Non authentifie");
        corps.put("message", "Un jeton JWT valide est requis dans l'en-tete Authorization: Bearer <jeton>");
        corps.put("chemin", requete.getRequestURI());

        mapper.writeValue(reponse.getOutputStream(), corps);
    }
}
