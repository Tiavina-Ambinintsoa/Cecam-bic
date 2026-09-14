package mg.cecam.bic.audit;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "journal_audit")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class JournalAudit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_evenement", nullable = false)
    private LocalDateTime dateEvenement;

    @Column(nullable = false)
    private String utilisateur;

    @Column(nullable = false)
    private String entite; // "CLIENT", "CONTRAT", "ECHEANCE"

    @Column(name = "entite_id", nullable = false)
    private Long entiteId;

    @Column(nullable = false)
    private String action; // "CREATION", "MODIFICATION", "AJOUT_ADRESSE", "CHANGEMENT_PHASE", "PAIEMENT"

    @Column(length = 1000)
    private String detail;
}