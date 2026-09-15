package mg.cecam.bic.client;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "emploi")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Emploi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false, unique = true)
    private Client client;

    @Column(name = "statut_emploi")
    private String statutEmploi;

    @Column(name = "nom_employeur")
    private String nomEmployeur;

    private String profession;

    @Column(name = "date_embauche")
    private LocalDate dateEmbauche;

    @Column(name = "revenu_annuel_total")
    private BigDecimal revenuAnnuelTotal;

    private String devise;
}