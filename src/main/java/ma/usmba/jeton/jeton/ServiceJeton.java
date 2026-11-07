package ma.usmba.jeton.jeton;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Component
public class ServiceJeton {

    private final ProprietesJeton proprietes;
    private final Key cle;

    public ServiceJeton(ProprietesJeton proprietes) {
        this.proprietes = proprietes;
        this.cle = Keys.hmacShaKeyFor(proprietes.secret().getBytes(StandardCharsets.UTF_8));
    }

    public String generer(UserDetails utilisateur) {
        List<String> roles = utilisateur.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Instant maintenant = Instant.now();
        Instant echeance = maintenant.plusMillis(proprietes.dureeValidite());

        return Jwts.builder()
                .setIssuer(proprietes.emetteur())
                .setSubject(utilisateur.getUsername())
                .claim("roles", roles)
                .setIssuedAt(Date.from(maintenant))
                .setExpiration(Date.from(echeance))
                .signWith(cle, SignatureAlgorithm.HS256)
                .compact();
    }

    public long dureeValidite() {
        return proprietes.dureeValidite();
    }

    public String extraireIdentifiant(String jeton) {
        return lireClaims(jeton).getSubject();
    }

    public List<String> extraireRoles(String jeton) {
        Object roles = lireClaims(jeton).get("roles");
        if (roles instanceof List<?> liste) {
            return liste.stream().map(String::valueOf).toList();
        }
        return List.of();
    }

    public boolean estValide(String jeton, UserDetails utilisateur) {
        try {
            Claims claims = lireClaims(jeton);
            return claims.getSubject().equals(utilisateur.getUsername())
                    && claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims lireClaims(String jeton) {
        return Jwts.parserBuilder()
                .setSigningKey(cle)
                .requireIssuer(proprietes.emetteur())
                .build()
                .parseClaimsJws(jeton)
                .getBody();
    }
}
