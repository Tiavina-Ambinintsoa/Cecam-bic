package mg.cecam.bic.contrat;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "bien_leasing")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BienLeasing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrat_id", nullable = false, unique = true)
    private Contrat contrat;

    @Column(name = "code_bien", length = 40)
    private String codeBien;

    @Column(name = "valeur_bien")
    private BigDecimal valeurBien;

    @Column(name = "etat_bien", length = 20)
    private String etatBien;

    @Column(name = "marque_bien")
    private String marqueBien;

    @Column(name = "date_acquisition")
    private LocalDate dateAcquisition;

    @Column(name = "immatriculation", length = 60)
    private String immatriculation;
}