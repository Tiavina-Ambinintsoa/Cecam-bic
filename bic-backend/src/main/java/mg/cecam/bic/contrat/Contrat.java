package mg.cecam.bic.contrat;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import mg.cecam.bic.client.Client;
import mg.cecam.bic.common.enums.ModeRattachement;
import mg.cecam.bic.common.enums.MotifCloture;
import mg.cecam.bic.common.enums.OrigineDonnee;
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

@Column(name = "code_demande_etablissement")
private String codeDemandeEtablissement;

    @Column(name = "code_etablissement_declarant", length = 10)
    private String codeEtablissementDeclarant;

    @Column(name = "utilisateur_declarant", length = 100)
    private String utilisateurDeclarant;

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

    @Column(name = "date_demande")
    private LocalDate dateDemande;

    @Column(name = "date_debut_contrat")
    private LocalDate dateDebutContrat;

    @Column(name = "date_fin_contrat")
    private LocalDate dateFinContrat;

    @Column(name = "date_premiere_echeance")
    private LocalDate datePremiereEcheance;

    @Column(name = "date_derniere_modification_source")
    private LocalDate dateDerniereModificationSource;

    @Column(name = "code_etablissement_crypte", length = 40)
    private String codeEtablissementCrypte;

    @Positive
    @Column(name = "montant_finance")
    private BigDecimal montantFinance;

    @Column(name = "montant_total_du")
    private BigDecimal montantTotalDu;

    @Column(name = "montant_echeance_mensuelle")
    private BigDecimal montantEcheanceMensuelle;

    @Column(name = "type_relation_entreprise")
    private String typeRelationEntreprise;

    @Positive
    @Column(name = "nombre_total_echeances")
    private Integer nombreTotalEcheances;

    @NotBlank
    @Builder.Default
    private String devise = "Ariary malgache";

    @NotNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "origine_donnee", nullable = false, length = 30, columnDefinition = "varchar(30) default 'NON_RENSEIGNEE'")
    private OrigineDonnee origineDonnee = OrigineDonnee.NON_RENSEIGNEE;

    @Column(name = "periodicite_paiement")
    private String periodicitePaiement;

    @Column(name = "periodicite_budget", length = 30)
    private String periodiciteBudget;

    @Column(name = "montant_recette", precision = 19, scale = 2)
    private BigDecimal montantRecette;

    @Column(name = "montant_depense", precision = 19, scale = 2)
    private BigDecimal montantDepense;

    @Column(name = "ratio_reste_pourcentage", precision = 10, scale = 2)
    private BigDecimal ratioRestePourcentage;

    @Column(name = "type_garantie")
    private String typeGarantie;

    @Column(name = "valeur_garantie", precision = 19, scale = 2)
    private BigDecimal valeurGarantie;

    @Column(name = "montant_total_demande_interet", precision = 19, scale = 2)
    private BigDecimal montantTotalDemandeInteret;

    @Column(name = "ratio_couverture_garantie", precision = 10, scale = 2)
    private BigDecimal ratioCouvertureGarantie;

    @Column(name = "valeur_patrimoine", precision = 19, scale = 2)
    private BigDecimal valeurPatrimoine;

    @Column(name = "marge_patrimoine", precision = 19, scale = 2)
    private BigDecimal margePatrimoine;

    @Column(name = "ratio_couverture_patrimoine", precision = 10, scale = 2)
    private BigDecimal ratioCouverturePatrimoine;

    @Column(name = "client_garant_id")
    private Long clientGarantId;

    @Column(name = "client_fiance_id")
    private Long clientFianceId;

    @Column(name = "depot_demandeur", precision = 19, scale = 2)
    private BigDecimal depotDemandeur;

    @Column(name = "depot_garant", precision = 19, scale = 2)
    private BigDecimal depotGarant;

    @Column(name = "depot_fiance", precision = 19, scale = 2)
    private BigDecimal depotFiance;

    @Column(name = "total_depot", precision = 19, scale = 2)
    private BigDecimal totalDepot;

    @Column(name = "credit_actif_demandeur", precision = 19, scale = 2)
    private BigDecimal creditActifDemandeur;

    @Column(name = "credit_actif_garant", precision = 19, scale = 2)
    private BigDecimal creditActifGarant;

    @Column(name = "credit_actif_fiance", precision = 19, scale = 2)
    private BigDecimal creditActifFiance;

    @Column(name = "total_credit_actif", precision = 19, scale = 2)
    private BigDecimal totalCreditActif;

    @Column(name = "mode_reglement")
    private String modeReglement;

    @Column(name = "montant_prochaine_echeance")
    private BigDecimal montantProchaineEcheance;

    @Column(name = "date_prochaine_echeance")
    private LocalDate dateProchaineEcheance;

    @Column(name = "date_dernier_reglement")
    private LocalDate dateDernierReglement;

    @Column(name = "nombre_echeances_restantes")
    private Integer nombreEcheancesRestantes;

    @Column(name = "montant_restant_du")
    private BigDecimal montantRestantDu;

    @Column(name = "nombre_echeances_impayees")
    private Integer nombreEcheancesImpayees;

    @Column(name = "montant_impayes")
    private BigDecimal montantImpayes;

    @Column(name = "max_montant_impayes")
    private BigDecimal maxMontantImpayes;

    @Column(name = "max_nombre_echeances_impayees")
    private Integer maxNombreEcheancesImpayees;

    @Column(name = "date_max_nombre_echeances_impayees")
    private LocalDate dateMaxNombreEcheancesImpayees;

    @Column(name = "nombre_jours_retard")
    private Integer nombreJoursRetard;

    @Column(name = "max_nombre_jours_retard")
    private Integer maxNombreJoursRetard;

    @Column(name = "date_max_nombre_jours_retard")
    private LocalDate dateMaxNombreJoursRetard;

    @Column(name = "pire_statut")
    private String pireStatut;

    @Column(name = "date_pire_statut")
    private LocalDate datePireStatut;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "phase_demande", nullable = false, length = 20)
    private PhaseDemande phaseDemande = PhaseDemande.DEMANDE_EN_COURS;

    @Enumerated(EnumType.STRING)
    @Column(name = "motif_cloture", length = 20)
    private MotifCloture motifCloture;

    @Column(name = "code_restructuration", length = 100)
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
    }

    @Transient
    public BigDecimal totalDu() {
        return montantTotalDu;
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
