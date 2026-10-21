package mg.cecam.bic.contrat;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "dernier_contrat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DernierContrat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrat_id", nullable = false)
    private Contrat contrat;

    @Column(name = "date_octroi")
    private LocalDate dateOctroi;

    @Column(name = "montant", precision = 19, scale = 2)
    private BigDecimal montant;

    @Column(name = "date_prevue_remboursement")
    private LocalDate datePrevueRemboursement;

    @Column(name = "date_remboursement")
    private LocalDate dateRemboursement;

    @Column(name = "jours_retard")
    private Integer joursRetard;

    @Column(name = "statut", length = 50)
    private String statut;
}
