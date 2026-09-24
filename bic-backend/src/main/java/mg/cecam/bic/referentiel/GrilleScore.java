package mg.cecam.bic.referentiel;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "grille_score")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class GrilleScore {

    @Id
    private String intervalle;

    @Column(name = "score_min", nullable = false)
    private Integer scoreMin;

    @Column(name = "score_max", nullable = false)
    private Integer scoreMax;

    @Column(name = "categorie_risque", nullable = false)
    private String categorieRisque;

    @Column(nullable = false)
    private String couleur;
}