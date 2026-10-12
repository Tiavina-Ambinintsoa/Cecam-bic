package mg.cecam.bic.contrat;

import jakarta.persistence.*;
import lombok.*;
import mg.cecam.bic.common.enums.MotifCloture;
import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.StatutDemandeModification;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "demande_modification_contrat")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ModificationContratDemande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contrat_id", nullable = false)
    private Contrat contrat;

    @Enumerated(EnumType.STRING)
    @Column(name = "phase_initiale", nullable = false, length = 30)
    private mg.cecam.bic.common.enums.PhaseDemande phaseInitiale;

    @Enumerated(EnumType.STRING)
    @Column(name = "motif_cloture_initial", length = 30)
    private mg.cecam.bic.common.enums.MotifCloture motifClotureInitial;

    @Column(name = "code_contrat_etablissement_initial", length = 40)
    private String codeContratEtablissementInitial;

    @Column(name = "code_demande_etablissement_initial", length = 40)
    private String codeDemandeEtablissementInitial;

    @Column(name = "montant_echeance_initial", precision = 19, scale = 2)
    private BigDecimal montantEcheanceInitial;

    @Column(name = "devise_initiale", length = 40)
    private String deviseInitiale;

    @Column(name = "code_contrat_etablissement", length = 40)
    private String codeContratEtablissement;

    @Column(name = "code_demande_etablissement", length = 40)
    private String codeDemandeEtablissement;

    @Enumerated(EnumType.STRING)
    @Column(name = "phase_demandee", nullable = false, length = 30)
    private PhaseDemande phaseDemandee;

    @Enumerated(EnumType.STRING)
    @Column(name = "motif_cloture", length = 30)
    private MotifCloture motifCloture;

    @Column(name = "montant_echeance_mensuelle", precision = 19, scale = 2)
    private BigDecimal montantEcheanceMensuelle;

    @Column(name = "devise", nullable = false, length = 40)
    private String devise;

    @Column(name = "justification", nullable = false, length = 500)
    private String justification;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    @Builder.Default
    private StatutDemandeModification statut = StatutDemandeModification.EN_ATTENTE;

    @Column(name = "soumis_par", nullable = false, length = 100)
    private String soumisPar;

    @Column(name = "date_soumission", nullable = false)
    @Builder.Default
    private LocalDateTime dateSoumission = LocalDateTime.now();

    @Column(name = "traite_par", length = 100)
    private String traitePar;

    @Column(name = "date_traitement")
    private LocalDateTime dateTraitement;

    @Column(name = "commentaire_admin", length = 500)
    private String commentaireAdmin;
}
