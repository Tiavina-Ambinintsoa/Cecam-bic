package mg.cecam.bic.client;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Set;

@Entity
@Table(name = "emploi")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Emploi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false, unique = true)
    private Client client;

    @Column(name = "statut_emploi")
    private String statutEmploi;

    @Column(name = "nom_employeur")
    private String nomEmployeur;

    private String profession;

    @Column(name = "date_embauche")
    private LocalDate dateEmbauche;

    @Column(name = "revenu_annuel_total")
    private BigDecimal revenuAnnuelTotal;

    private String devise;

    public boolean estRenseigne() {
        String statut = normaliser(statutEmploi);
        boolean absenceEmploiDeclaree = Set.of("SANS EMPLOI", "SANS EMPLOI DECLARE", "SANS ACTIVITE",
                "NON EMPLOYE", "SANS PROFESSION", "NON RENSEIGNE").contains(statut);
        if (absenceEmploiDeclaree && (nomEmployeur == null || nomEmployeur.isBlank())
                && (profession == null || profession.isBlank()) && dateEmbauche == null) {
            return false;
        }
        return !statut.isBlank()
                || (nomEmployeur != null && !nomEmployeur.isBlank())
                || (profession != null && !profession.isBlank())
                || dateEmbauche != null
                || revenuAnnuelTotal != null;
    }

    private String normaliser(String valeur) {
        if (valeur == null) return "";
        return Normalizer.normalize(valeur, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .trim()
                .toUpperCase(Locale.ROOT);
    }
}
