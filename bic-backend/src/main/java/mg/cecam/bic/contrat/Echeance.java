package mg.cecam.bic.contrat;

import jakarta.persistence.*;
import lombok.*;
import mg.cecam.bic.common.enums.StatutEcheance;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "echeance")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Echeance {

    public static final int SEUIL_IMPAYE_JOURS = 30;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrat_id", nullable = false)
    private Contrat contrat;

    @Column(name = "numero_echeance", nullable = false)
    private Integer numeroEcheance;

    @Column(name = "date_echeance", nullable = false)
    private LocalDate dateEcheance;

    @Column(name = "montant_du", nullable = false)
    private BigDecimal montantDu;

    @Column(name = "montant_paye")
    private BigDecimal montantPaye;

    @Column(name = "date_paiement")
    private LocalDate datePaiement;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatutEcheance statut;

    @Transient
    public boolean estPayee() {
        return montantPaye != null && montantPaye.signum() > 0;
    }

    @Transient
    public int joursDeRetard(LocalDate reference) {
        LocalDate fin = estPayee()
                ? (datePaiement != null ? datePaiement : dateEcheance)
                : reference;
        if (!fin.isAfter(dateEcheance)) return 0;
        return (int) ChronoUnit.DAYS.between(dateEcheance, fin);
    }

    @Transient
    public StatutEcheance statutEffectif(LocalDate reference) {
        if (estPayee()) {
            return joursDeRetard(reference) > 0 ? StatutEcheance.EN_RETARD : StatutEcheance.PAYE_A_TEMPS;
        }
        if (dateEcheance.isAfter(reference)) return StatutEcheance.A_VENIR;
        return joursDeRetard(reference) > SEUIL_IMPAYE_JOURS ? StatutEcheance.IMPAYE : StatutEcheance.EN_RETARD;
    }

    @Transient
    public boolean estEnSouffrance(LocalDate reference) {
        return !estPayee() && !dateEcheance.isAfter(reference);
    }
}