package ma.usmba.jeton.service;

import ma.usmba.jeton.depot.RoleDepot;
import ma.usmba.jeton.depot.UtilisateurDepot;
import ma.usmba.jeton.entite.Role;
import ma.usmba.jeton.entite.Utilisateur;
import ma.usmba.jeton.web.dto.RequeteInscription;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ServiceCompte {

    private final UtilisateurDepot utilisateurDepot;
    private final RoleDepot roleDepot;
    private final PasswordEncoder encodeur;

    public ServiceCompte(UtilisateurDepot utilisateurDepot, RoleDepot roleDepot, PasswordEncoder encodeur) {
        this.utilisateurDepot = utilisateurDepot;
        this.roleDepot = roleDepot;
        this.encodeur = encodeur;
    }

    public Utilisateur parIdentifiant(String identifiant) {
        return utilisateurDepot.chargerParIdentifiant(identifiant)
                .orElseThrow(() -> new IllegalArgumentException("Compte introuvable : " + identifiant));
    }

    @Transactional
    public Utilisateur inscrire(RequeteInscription requete) {
        if (utilisateurDepot.existsByIdentifiantIgnoreCase(requete.identifiant())) {
            throw new IllegalStateException("Cet identifiant est deja utilise : " + requete.identifiant());
        }

        Role roleMembre = roleDepot.findByCode("ROLE_MEMBRE")
                .orElseGet(() -> roleDepot.save(new Role("ROLE_MEMBRE", "Membre")));

        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setIdentifiant(requete.identifiant());
        utilisateur.setMotDePasse(encodeur.encode(requete.motDePasse()));
        utilisateur.setNomComplet(requete.nomComplet());
        utilisateur.setEmail(requete.email());
        utilisateur.setActif(true);
        utilisateur.ajouterRole(roleMembre);

        return utilisateurDepot.save(utilisateur);
    }
}
