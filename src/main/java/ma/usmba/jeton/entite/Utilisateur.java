package ma.usmba.jeton.entite;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "utilisateurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String identifiant;

    @Column(name = "mot_de_passe", nullable = false, length = 120)
    private String motDePasse;

    @Column(length = 150)
    private String nomComplet;

    @Column(length = 150)
    private String email;

    @Column(nullable = false)
    private boolean actif = true;

    @Column(name = "date_inscription")
    private LocalDateTime dateInscription;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "utilisateurs_roles",
            joinColumns = @JoinColumn(name = "utilisateur_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Collection<Role> roles = new ArrayList<>();

    @PrePersist
    void preparerInscription() {
        if (dateInscription == null) {
            dateInscription = LocalDateTime.now();
        }
    }

    public void ajouterRole(Role role) {
        if (!roles.contains(role)) {
            roles.add(role);
        }
    }
}
