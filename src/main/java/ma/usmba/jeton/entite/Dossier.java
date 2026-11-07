package ma.usmba.jeton.entite;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "dossiers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dossier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String reference;

    @Column(nullable = false, length = 150)
    private String intitule;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal montant;

    @Column(nullable = false, length = 20)
    private String statut;

    @Column(nullable = false, length = 60)
    private String deposant;

    @Column(name = "date_depot")
    private LocalDate dateDepot;

    @PrePersist
    void preparerDepot() {
        if (dateDepot == null) {
            dateDepot = LocalDate.now();
        }
        if (statut == null || statut.isBlank()) {
            statut = "EN_ATTENTE";
        }
    }
}
