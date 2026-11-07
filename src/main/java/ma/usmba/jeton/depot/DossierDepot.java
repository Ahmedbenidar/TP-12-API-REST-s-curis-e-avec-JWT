package ma.usmba.jeton.depot;

import ma.usmba.jeton.entite.Dossier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DossierDepot extends JpaRepository<Dossier, Long> {

    List<Dossier> findByDeposantOrderByDateDepotDesc(String deposant);

    long countByStatut(String statut);
}
