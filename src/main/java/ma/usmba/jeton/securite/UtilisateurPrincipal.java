package ma.usmba.jeton.securite;

import ma.usmba.jeton.entite.Role;
import ma.usmba.jeton.entite.Utilisateur;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UtilisateurPrincipal implements UserDetails {

    private final Utilisateur utilisateur;

    public UtilisateurPrincipal(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public String getNomComplet() {
        return utilisateur.getNomComplet();
    }

    public List<String> getCodesRoles() {
        return utilisateur.getRoles().stream().map(Role::getCode).toList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return utilisateur.getRoles().stream()
                .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role.getCode()))
                .toList();
    }

    @Override
    public String getPassword() {
        return utilisateur.getMotDePasse();
    }

    @Override
    public String getUsername() {
        return utilisateur.getIdentifiant();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return utilisateur.isActif();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return utilisateur.isActif();
    }
}
