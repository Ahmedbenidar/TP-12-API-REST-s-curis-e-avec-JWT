package ma.usmba.jeton.web.dto;

import ma.usmba.jeton.entite.Dossier;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DossierDto(
        Long id,
        String reference,
        String intitule,
        BigDecimal montant,
        String statut,
        String deposant,
        LocalDate dateDepot) {

    public static DossierDto depuis(Dossier dossier) {
        return new DossierDto(
                dossier.getId(),
                dossier.getReference(),
                dossier.getIntitule(),
                dossier.getMontant(),
                dossier.getStatut(),
                dossier.getDeposant(),
                dossier.getDateDepot());
    }
}
