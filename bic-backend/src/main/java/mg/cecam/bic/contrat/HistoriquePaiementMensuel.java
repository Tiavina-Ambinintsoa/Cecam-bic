package mg.cecam.bic.contrat;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "historique_paiement_mensuel", uniqueConstraints =
        @UniqueConstraint(name = "uq_historique_contrat_mois", columnNames = {"contrat_id", "mois"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HistoriquePaiementMensuel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrat_id", nullable = false)
    private Contrat contrat;

    /** Premier jour du mois observé dans la déclaration du bureau. */
    @Column(nullable = false)
    private LocalDate mois;

    private Integer nombreEcheances;
    private Integer nombreEcheancesImpayees;
    private Integer joursDeRetard;

    @Column(nullable = false, length = 80)
    private String statut;

    @Column(length = 240)
    private String detailsSource;
}
