package ma.usmba.jeton.web;

import ma.usmba.jeton.depot.UtilisateurDepot;
import ma.usmba.jeton.entite.Dossier;
import ma.usmba.jeton.entite.Utilisateur;
import ma.usmba.jeton.service.ServiceDossier;
import ma.usmba.jeton.web.dto.DossierDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/administration")
public class ControleurAdministration {

    private final ServiceDossier serviceDossier;
    private final UtilisateurDepot utilisateurDepot;

    public ControleurAdministration(ServiceDossier serviceDossier, UtilisateurDepot utilisateurDepot) {
        this.serviceDossier = serviceDossier;
        this.utilisateurDepot = utilisateurDepot;
    }

    @GetMapping("/statistiques")
    public Map<String, Object> statistiques() {
        List<Dossier> dossiers = serviceDossier.listerTous();
        long total = dossiers.size();
        long enAttente = serviceDossier.compterParStatut("EN_ATTENTE");
        long valides = serviceDossier.compterParStatut("VALIDE");

        Map<String, Object> reponse = new LinkedHashMap<>();
        reponse.put("comptes", utilisateurDepot.count());
        reponse.put("dossiersTotal", total);
        reponse.put("dossiersEnAttente", enAttente);
        reponse.put("dossiersValides", valides);
        reponse.put("accesReserve", "ROLE_ADMIN");
        return reponse;
    }

    @GetMapping("/dossiers")
    public List<DossierDto> listerDossiers() {
        return serviceDossier.listerTous().stream().map(DossierDto::depuis).toList();
    }

    @GetMapping("/comptes")
    public List<Map<String, Object>> listerComptes() {
        return utilisateurDepot.findAll().stream()
                .map(this::resumer)
                .toList();
    }

    @PatchMapping("/dossiers/{id}/statut")
    public ResponseEntity<DossierDto> modifierStatut(@PathVariable Long id,
                                                     @RequestParam String statut) {
        return ResponseEntity.ok(DossierDto.depuis(serviceDossier.changerStatut(id, statut)));
    }

    private Map<String, Object> resumer(Utilisateur utilisateur) {
        Map<String, Object> ligne = new LinkedHashMap<>();
        ligne.put("id", utilisateur.getId());
        ligne.put("identifiant", utilisateur.getIdentifiant());
        ligne.put("nomComplet", utilisateur.getNomComplet());
        ligne.put("actif", utilisateur.isActif());
        ligne.put("roles", utilisateur.getRoles().stream().map(r -> r.getCode()).toList());
        return ligne;
    }
}
