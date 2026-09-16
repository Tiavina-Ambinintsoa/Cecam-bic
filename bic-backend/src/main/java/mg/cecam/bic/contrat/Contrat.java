// mg/cecam/bic/contrat/Contrat.java  — MODIFIÉ
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
    private String codeClientCbCible; // renseigné uniquement si modeRattachement = DEMANDE_EXISTANTE

    @Column(name = "code_contrat_cb", unique = true, length = 20)
    private String codeContratCb;

    /** Référence interne CECAM du contrat (CRIF : « Code Contrat Etablissement »). */
    @Column(name = "code_contrat_etablissement", length = 40)
    private String codeContratEtablissement;

    /** CRIF : « Code Etablissement Crypté ». CECAM = 001 par défaut. */
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

    // ------------------------------------------------------------------
    // Dates — CRIF en distingue trois, le modèle initial n'en avait qu'une
    // ------------------------------------------------------------------

    @NotNull
    @Column(name = "date_demande", nullable = false)
    private LocalDate dateDemande;

    /** Date de déblocage des fonds. Null tant que la demande n'est pas décaissée. */
    @Column(name = "date_debut_contrat")
    private LocalDate dateDebutContrat;

    /** Date d'échéance finale prévue ou date de clôture effective. */
    @Column(name = "date_fin_contrat")
    private LocalDate dateFinContrat;

    /** Base de l'échéancier. Souvent différente de dateDebutContrat (différé). */
    @Column(name = "date_premiere_echeance")
    private LocalDate datePremiereEcheance;

    // ------------------------------------------------------------------
    // Montants — capital et total dû sont deux notions différentes
    // ------------------------------------------------------------------

    /** Capital emprunté, hors intérêts et frais. */
    @NotNull @Positive
    @Column(name = "montant_finance", nullable = false)
    private BigDecimal montantFinance;

    /**
     * Total à rembourser : capital + intérêts + frais.
     * C'est cette valeur qui doit égaler la somme des montantDu des échéances,
     * et c'est elle qui sert de dénominateur au taux d'utilisation du score.
     * Si null, on retombe sur montantFinance (contrat sans intérêt déclaré).
     */
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

    /** MENSUELLE, TRIMESTRIELLE, SEMESTRIELLE, ANNUELLE, IN_FINE. */
    @Column(name = "periodicite_paiement")
    private String periodicitePaiement;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "phase_demande", nullable = false, length = 20)
    private PhaseDemande phaseDemande = PhaseDemande.DEMANDE_EN_COURS;

    /** Renseigné uniquement quand phaseDemande = FERME. */
    @Enumerated(EnumType.STRING)
    @Column(name = "motif_cloture", length = 20)
    private MotifCloture motifCloture;

    // Restructuration — champs présents dans le rapport CRIF
    @Column(name = "code_restructuration", length = 40)
    private String codeRestructuration;

    @Column(name = "date_code_restructuration")
    private LocalDate dateCodeRestructuration;

    @Column(name = "contrat_origine", length = 20)
    private String contratOrigine;

    @Column(name = "nouveau_contrat", length = 20)
    private String nouveauContrat;

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

    /** Total réellement dû sur la durée du contrat, intérêts compris. */
    @Transient
    public BigDecimal totalDu() {
        return montantTotalDu != null ? montantTotalDu : montantFinance;
    }

    /** Nombre de mois entre deux échéances, déduit de la périodicité. */
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