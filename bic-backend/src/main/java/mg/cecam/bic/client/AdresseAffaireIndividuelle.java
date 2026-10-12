package mg.cecam.bic.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "adresse_affaire_individuelle")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AdresseAffaireIndividuelle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "affaire_individuelle_id", nullable = false)
    @JsonIgnore
    private AffaireIndividuelle affaireIndividuelle;

    @NotBlank
    @Column(name = "type_adresse", nullable = false)
    @Builder.Default
    private String typeAdresse = "Entreprise - Adresse Principale";

    @NotBlank
    @Column(name = "adresse_complete", nullable = false, length = 500)
    private String adresseComplete;

    @Column(name = "numero_rue")
    private String numeroRue;

    @Column(name = "code_postal")
    private String codePostal;

    private String ville;
    private String commune;
    private String region;
    private String pays;

    @Builder.Default
    @Column(nullable = false)
    private Boolean actuelle = true;

    @Column(name = "date_derniere_modification")
    private LocalDateTime dateDerniereModification;

    @PrePersist
    @PreUpdate
    void onSave() {
        this.dateDerniereModification = LocalDateTime.now();
    }
}
