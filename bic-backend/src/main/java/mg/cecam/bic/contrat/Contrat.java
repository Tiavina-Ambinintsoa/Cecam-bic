package mg.cecam.bic.contrat;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import mg.cecam.bic.client.Client;
import mg.cecam.bic.common.enums.ModeRattachement;
import mg.cecam.bic.common.enums.MotifCloture;
import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.RoleClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "contrat")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Contrat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code_client_cb_cible")
    private String codeClientCbCible;

    @Column(name = "code_contrat_cb", unique = true, length = 20)
    private String codeContratCb;

    @Column(name = "code_contrat_etablissement", length = 40)
    private String codeContratEtablissement;

    @Builder.Default
    @Column(name = "code_etablissement_declarant", length = 10)
    private String codeEtablissementDeclarant = "001";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "mode_rattachement", nullable = false, length = 30)
    private ModeRattachement modeRattachement;

    @NotBlank
    @Column(name = "type_contrat", nullable = false)
    private String typeContrat;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role_client", nullable = false, length = 20)
    private RoleClient roleClient;

    @NotNull
    @Column(name = "date_demande", nullable = false)
    private LocalDate dateDemande;

    @Column(name = "date_debut_contrat")
    private LocalDate dateDebutContrat;

    @Column(name = "date_fin_contrat")
    private LocalDate dateFinContrat;

    @Column(name = "date_premiere_echeance")
    private LocalDate datePremiereEcheance;

    @NotNull @Positive
    @Column(name = "montant_finance", nullable = false)
    private BigDecimal montantFinance;

    @Column(name = "montant_total_du")
    private BigDecimal montantTotalDu;

    @Column(name = "montant_echeance_mensuelle")
    private BigDecimal montantEcheanceMensuelle;

    @Column(name = "type_relation_entreprise")
    private String typeRelationEntreprise;

    @NotNull @Positive
    @Column(name = "nombre_total_echeances", nullable = false)
    private Integer nombreTotalEcheances;

    @NotBlank
    @Builder.Default
    private String devise = "Ariary malgache";

    @Column(name = "periodicite_paiement")
    private String periodicitePaiement;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "phase_demande", nullable = false, length = 20)
    private PhaseDemande phaseDemande = PhaseDemande.DEMANDE_EN_COURS;

    @Enumerated(EnumType.STRING)
    @Column(name = "motif_cloture", length = 20)
    private MotifCloture motifCloture;

    @Column(name = "code_restructuration", length = 40)
    private String codeRestructuration;

    @Column(name = "date_code_restructuration")
    private LocalDate dateCodeRestructuration;

    @Column(name = "contrat_origine", length = 20)
    private String contratOrigine;

    @Column(name = "nouveau_contrat", length = 20)
    private String nouveauContrat;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "date_derniere_modification")
    private LocalDateTime dateDerniereModification;

    @PrePersist
    @PreUpdate
    void onSave() {
        this.dateDerniereModification = LocalDateTime.now();
        if (this.montantTotalDu == null) {
            this.montantTotalDu = this.montantFinance;
        }
    }

    @Transient
    public BigDecimal totalDu() {
        return montantTotalDu != null ? montantTotalDu : montantFinance;
    }

    @Transient
    public int pasEnMois() {
        if (periodicitePaiement == null) return 1;
        String p = periodicitePaiement.trim().toUpperCase();
        if (p.startsWith("TRIM")) return 3;
        if (p.startsWith("SEM")) return 6;
        if (p.startsWith("AN")) return 12;
        return 1;
    }
}