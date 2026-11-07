package ma.usmba.jeton.securite;

import ma.usmba.jeton.depot.UtilisateurDepot;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ServiceDetailsUtilisateur implements UserDetailsService {

    private final UtilisateurDepot utilisateurDepot;

    public ServiceDetailsUtilisateur(UtilisateurDepot utilisateurDepot) {
        this.utilisateurDepot = utilisateurDepot;
    }

    @Override
    public UserDetails loadUserByUsername(String identifiant) throws UsernameNotFoundException {
        return utilisateurDepot.chargerParIdentifiant(identifiant)
                .map(UtilisateurPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException("Aucun compte pour : " + identifiant));
    }
}
