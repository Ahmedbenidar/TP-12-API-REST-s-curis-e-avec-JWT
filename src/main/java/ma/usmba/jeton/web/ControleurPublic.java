package ma.usmba.jeton.web;

import ma.usmba.jeton.service.ServiceDossier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class ControleurPublic {

    private final ServiceDossier serviceDossier;

    public ControleurPublic(ServiceDossier serviceDossier) {
        this.serviceDossier = serviceDossier;
    }

    @GetMapping("/etat")
    public Map<String, Object> etat() {
        Map<String, Object> reponse = new LinkedHashMap<>();
        reponse.put("service", "portail-jeton-api");
        reponse.put("statut", "operationnel");
        reponse.put("mode", "stateless (aucune session serveur)");
        reponse.put("dossiersEnregistres", serviceDossier.listerTous().size());
        reponse.put("horodatage", OffsetDateTime.now().toString());
        return reponse;
    }
}
