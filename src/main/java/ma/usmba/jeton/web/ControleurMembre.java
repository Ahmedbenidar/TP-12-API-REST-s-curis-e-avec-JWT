package ma.usmba.jeton.web;

import ma.usmba.jeton.securite.UtilisateurPrincipal;
import ma.usmba.jeton.service.ServiceDossier;
import ma.usmba.jeton.web.dto.DossierDto;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/membre")
public class ControleurMembre {

    private final ServiceDossier serviceDossier;

    public ControleurMembre(ServiceDossier serviceDossier) {
        this.serviceDossier = serviceDossier;
    }

    @GetMapping("/tableau-de-bord")
    public Map<String, Object> tableauDeBord(@AuthenticationPrincipal UtilisateurPrincipal principal) {
        String identifiant = principal.getUsername();
        List<DossierDto> dossiers = serviceDossier.listerParDeposant(identifiant).stream()
                .map(DossierDto::depuis)
                .toList();

        Map<String, Object> reponse = new LinkedHashMap<>();
        reponse.put("identifiant", identifiant);
        reponse.put("nomComplet", principal.getNomComplet());
        reponse.put("roles", principal.getCodesRoles());
        reponse.put("nombreDossiers", dossiers.size());
        reponse.put("dossiers", dossiers);
        reponse.put("source", "JWT valide - aucune session serveur");
        return reponse;
    }

    @GetMapping("/mes-dossiers")
    public List<DossierDto> mesDossiers(@AuthenticationPrincipal UtilisateurPrincipal principal) {
        return serviceDossier.listerParDeposant(principal.getUsername()).stream()
                .map(DossierDto::depuis)
                .toList();
    }
}
