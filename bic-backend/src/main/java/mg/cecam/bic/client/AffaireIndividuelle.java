// AffaireIndividuelle.java (nouveau)
package mg.cecam.bic.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "affaire_individuelle")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AffaireIndividuelle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false, unique = true)
    @JsonIgnore
    private Client client;

    @Column(name = "nom_affaire", nullable = false)
    private String nomAffaire;

    @Column(name = "adresse_complete", length = 500)
    private String adresseComplete;

    @Column(name = "numero_rue")
    private String numeroRue;

    @Column(name = "code_postal")
    private String codePostal;

    private String ville;
    private String commune;
    private String region;
    private String pays;

    @Column(name = "date_derniere_modification")
    private LocalDateTime dateDerniereModification;

    @PrePersist
    @PreUpdate
    void onSave() {
        this.dateDerniereModification = LocalDateTime.now();
    }
}