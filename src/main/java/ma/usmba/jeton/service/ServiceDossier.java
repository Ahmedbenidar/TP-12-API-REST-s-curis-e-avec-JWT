package ma.usmba.jeton.service;

import ma.usmba.jeton.depot.DossierDepot;
import ma.usmba.jeton.entite.Dossier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ServiceDossier {

    private final DossierDepot dossierDepot;

    public ServiceDossier(DossierDepot dossierDepot) {
        this.dossierDepot = dossierDepot;
    }

    public List<Dossier> listerTous() {
        return dossierDepot.findAll();
    }

    public List<Dossier> listerParDeposant(String deposant) {
        return dossierDepot.findByDeposantOrderByDateDepotDesc(deposant);
    }

    public long compterParStatut(String statut) {
        return dossierDepot.countByStatut(statut);
    }

    @Transactional
    public Dossier changerStatut(Long id, String statut) {
        Dossier dossier = dossierDepot.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Dossier introuvable : " + id));
        dossier.setStatut(statut);
        return dossierDepot.save(dossier);
    }
}
