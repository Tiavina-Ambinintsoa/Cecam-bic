package mg.cecam.bic.contrat;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "revenu_demande")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RevenuDemande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrat_id", nullable = false)
    private Contrat contrat;

    @Column(name = "type_revenu", nullable = false, length = 40)
    private String typeRevenu;

    @Column(name = "description", nullable = false, length = 255)
    private String description;

    @Column(name = "montant_annuel", nullable = false, precision = 19, scale = 2)
    private BigDecimal montantAnnuel;

    @Column(name = "devise", nullable = false, length = 40)
    private String devise;
}
