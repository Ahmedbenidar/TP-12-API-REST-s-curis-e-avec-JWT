package ma.usmba.jeton.config;

import ma.usmba.jeton.depot.DossierDepot;
import ma.usmba.jeton.depot.RoleDepot;
import ma.usmba.jeton.depot.UtilisateurDepot;
import ma.usmba.jeton.entite.Dossier;
import ma.usmba.jeton.entite.Role;
import ma.usmba.jeton.entite.Utilisateur;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class InitialisationDonnees implements CommandLineRunner {

    private final UtilisateurDepot utilisateurDepot;
    private final RoleDepot roleDepot;
    private final DossierDepot dossierDepot;
    private final PasswordEncoder encodeur;

    public InitialisationDonnees(UtilisateurDepot utilisateurDepot,
                                 RoleDepot roleDepot,
                                 DossierDepot dossierDepot,
                                 PasswordEncoder encodeur) {
        this.utilisateurDepot = utilisateurDepot;
        this.roleDepot = roleDepot;
        this.dossierDepot = dossierDepot;
        this.encodeur = encodeur;
    }

    @Override
    @Transactional
    public void run(String... args) {
        Role admin = role("ROLE_ADMIN", "Administrateur");
        Role membre = role("ROLE_MEMBRE", "Membre");
        Role observateur = role("ROLE_OBSERVATEUR", "Observateur");

        if (utilisateurDepot.count() == 0) {
            utilisateurDepot.save(compte("a.benidar", "admin123", "Ahmed Benidar",
                    "ahmed.benidar@usmba.ac.ma", admin, membre));
            utilisateurDepot.save(compte("s.idalen", "membre123", "Salma Idalen",
                    "salma.idalen@usmba.ac.ma", membre));
            utilisateurDepot.save(compte("k.ouazzani", "membre123", "Karim Ouazzani",
                    "karim.ouazzani@usmba.ac.ma", membre, observateur));
        }

        if (dossierDepot.count() == 0) {
            dossierDepot.save(dossier("DOS-2024-001", "Demande de bourse de recherche",
                    new BigDecimal("15000.00"), "VALIDE", "s.idalen", LocalDate.of(2024, 2, 12)));
            dossierDepot.save(dossier("DOS-2024-002", "Attestation de scolarite",
                    new BigDecimal("0.00"), "VALIDE", "s.idalen", LocalDate.of(2024, 2, 20)));
            dossierDepot.save(dossier("DOS-2024-003", "Remboursement de frais de stage",
                    new BigDecimal("2400.50"), "EN_ATTENTE", "s.idalen", LocalDate.of(2024, 3, 4)));
            dossierDepot.save(dossier("DOS-2024-004", "Inscription au seminaire Jakarta EE",
                    new BigDecimal("800.00"), "EN_ATTENTE", "k.ouazzani", LocalDate.of(2024, 3, 11)));
            dossierDepot.save(dossier("DOS-2024-005", "Materiel informatique",
                    new BigDecimal("12500.00"), "REFUSE", "k.ouazzani", LocalDate.of(2024, 3, 18)));
        }
    }

    private Role role(String code, String libelle) {
        return roleDepot.findByCode(code).orElseGet(() -> roleDepot.save(new Role(code, libelle)));
    }

    private Utilisateur compte(String identifiant, String motDePasse, String nomComplet,
                               String email, Role... roles) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setIdentifiant(identifiant);
        utilisateur.setMotDePasse(encodeur.encode(motDePasse));
        utilisateur.setNomComplet(nomComplet);
        utilisateur.setEmail(email);
        utilisateur.setActif(true);
        for (Role role : roles) {
            utilisateur.ajouterRole(role);
        }
        return utilisateur;
    }

    private Dossier dossier(String reference, String intitule, BigDecimal montant,
                            String statut, String deposant, LocalDate dateDepot) {
        Dossier dossier = new Dossier();
        dossier.setReference(reference);
        dossier.setIntitule(intitule);
        dossier.setMontant(montant);
        dossier.setStatut(statut);
        dossier.setDeposant(deposant);
        dossier.setDateDepot(dateDepot);
        return dossier;
    }
}
