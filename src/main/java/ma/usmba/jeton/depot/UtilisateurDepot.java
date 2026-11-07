package ma.usmba.jeton.depot;

import ma.usmba.jeton.entite.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UtilisateurDepot extends JpaRepository<Utilisateur, Long> {

    @Query("select distinct u from Utilisateur u left join fetch u.roles where lower(u.identifiant) = lower(:identifiant)")
    Optional<Utilisateur> chargerParIdentifiant(@Param("identifiant") String identifiant);

    boolean existsByIdentifiantIgnoreCase(String identifiant);
}
