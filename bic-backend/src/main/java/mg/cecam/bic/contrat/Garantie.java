package mg.cecam.bic.contrat;

import jakarta.persistence.*;
import lombok.*;
import mg.cecam.bic.common.enums.NatureGarantie;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "garantie")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Garantie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrat_id", nullable = false)
    private Contrat contrat;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private NatureGarantie nature = NatureGarantie.REELLE;

    @Column(name = "type_garantie", nullable = false)
    private String typeGarantie;

    @Column(name = "code_etablissement_garantie", length = 40)
    private String codeEtablissementGarantie;

    @Column(name = "nom_garant")
    private String nomGarant;

    @Column(name = "code_client_cb_garant")
    private String codeClientCbGarant;

    @Column(name = "montant_couvert")
    private BigDecimal montantCouvert;

    @Column(name = "date_debut_validite")
    private LocalDate dateDebutValidite;

    @Column(name = "date_fin_validite")
    private LocalDate dateFinValidite;
}