package mg.cecam.bic.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @NotBlank
    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "date_derniere_modification", nullable = false)
    private LocalDateTime dateDerniereModification;

    @OneToMany(mappedBy = "affaireIndividuelle", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AdresseAffaireIndividuelle> adresses = new ArrayList<>();

    @PrePersist
    @PreUpdate
    void onSave() {
        this.dateDerniereModification = LocalDateTime.now();
    }
}
