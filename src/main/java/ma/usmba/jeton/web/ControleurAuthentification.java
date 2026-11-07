package ma.usmba.jeton.web;

import jakarta.validation.Valid;
import ma.usmba.jeton.entite.Utilisateur;
import ma.usmba.jeton.jeton.ServiceJeton;
import ma.usmba.jeton.securite.UtilisateurPrincipal;
import ma.usmba.jeton.service.ServiceCompte;
import ma.usmba.jeton.web.dto.ProfilReponse;
import ma.usmba.jeton.web.dto.RequeteConnexion;
import ma.usmba.jeton.web.dto.RequeteInscription;
import ma.usmba.jeton.web.dto.ReponseConnexion;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class ControleurAuthentification {

    private static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final AuthenticationManager gestionnaireAuthentification;
    private final UserDetailsService detailsUtilisateur;
    private final ServiceJeton serviceJeton;
    private final ServiceCompte serviceCompte;

    public ControleurAuthentification(AuthenticationManager gestionnaireAuthentification,
                                      UserDetailsService detailsUtilisateur,
                                      ServiceJeton serviceJeton,
                                      ServiceCompte serviceCompte) {
        this.gestionnaireAuthentification = gestionnaireAuthentification;
        this.detailsUtilisateur = detailsUtilisateur;
        this.serviceJeton = serviceJeton;
        this.serviceCompte = serviceCompte;
    }

    @PostMapping("/connexion")
    public ReponseConnexion connexion(@Valid @RequestBody RequeteConnexion requete) {
        try {
            gestionnaireAuthentification.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requete.identifiant(), requete.motDePasse()));
        } catch (BadCredentialsException e) {
            throw new IdentifiantsInvalidesException();
        }

        UserDetails utilisateur = detailsUtilisateur.loadUserByUsername(requete.identifiant());
        String jeton = serviceJeton.generer(utilisateur);

        List<String> roles = utilisateur.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .toList();

        String nomComplet = utilisateur instanceof UtilisateurPrincipal principal
                ? principal.getNomComplet()
                : utilisateur.getUsername();

        return new ReponseConnexion(jeton, "Bearer", utilisateur.getUsername(),
                nomComplet, serviceJeton.dureeValidite() / 1000, roles);
    }

    @PostMapping("/inscription")
    public ResponseEntity<ProfilReponse> inscription(@Valid @RequestBody RequeteInscription requete) {
        Utilisateur cree = serviceCompte.inscrire(requete);
        return ResponseEntity.status(HttpStatus.CREATED).body(versProfil(cree));
    }

    @GetMapping("/moi")
    public ProfilReponse moi(@AuthenticationPrincipal UserDetails utilisateur) {
        return versProfil(serviceCompte.parIdentifiant(utilisateur.getUsername()));
    }

    private ProfilReponse versProfil(Utilisateur utilisateur) {
        String inscription = utilisateur.getDateInscription() == null
                ? null
                : utilisateur.getDateInscription().format(FORMAT);

        return new ProfilReponse(
                utilisateur.getId(),
                utilisateur.getIdentifiant(),
                utilisateur.getNomComplet(),
                utilisateur.getEmail(),
                utilisateur.isActif(),
                inscription,
                utilisateur.getRoles().stream().map(r -> r.getCode()).toList());
    }
}
