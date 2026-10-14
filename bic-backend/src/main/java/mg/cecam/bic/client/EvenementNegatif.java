package mg.cecam.bic.client;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "evenement_negatif")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class EvenementNegatif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(nullable = false)
    private String type;               

    @Column(name = "description_evenement")
    private String descriptionEvenement; 

    @Column(name = "date_evenement")
    private LocalDate dateEvenement;

    @Column(name = "code_etablissement_crypte", length = 40)
    private String codeEtablissementCrypte;

    @Column(nullable = false)
    @Builder.Default
    private boolean actif = true;
}