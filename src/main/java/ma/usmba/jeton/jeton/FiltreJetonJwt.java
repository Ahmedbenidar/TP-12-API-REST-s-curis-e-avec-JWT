package ma.usmba.jeton.jeton;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class FiltreJetonJwt extends OncePerRequestFilter {

    private static final String PREFIXE = "Bearer ";

    private final ServiceJeton serviceJeton;
    private final UserDetailsService detailsUtilisateur;

    public FiltreJetonJwt(ServiceJeton serviceJeton, UserDetailsService detailsUtilisateur) {
        this.serviceJeton = serviceJeton;
        this.detailsUtilisateur = detailsUtilisateur;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest requete,
                                    HttpServletResponse reponse,
                                    FilterChain chaine)
            throws ServletException, IOException {

        String entete = requete.getHeader("Authorization");

        if (entete != null && entete.startsWith(PREFIXE)
                && SecurityContextHolder.getContext().getAuthentication() == null) {
            String jeton = entete.substring(PREFIXE.length()).trim();
            try {
                String identifiant = serviceJeton.extraireIdentifiant(jeton);
                UserDetails utilisateur = detailsUtilisateur.loadUserByUsername(identifiant);

                if (serviceJeton.estValide(jeton, utilisateur)) {
                    UsernamePasswordAuthenticationToken authentification =
                            new UsernamePasswordAuthenticationToken(
                                    utilisateur, null, utilisateur.getAuthorities());
                    authentification.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(requete));
                    SecurityContextHolder.getContext().setAuthentication(authentification);
                    requete.setAttribute("jeton.sub", identifiant);
                    requete.setAttribute("jeton.roles", serviceJeton.extraireRoles(jeton));
                }
            } catch (RuntimeException e) {
                requete.setAttribute("jeton.erreur", e.getMessage());
            }
        }

        chaine.doFilter(requete, reponse);
    }
}
