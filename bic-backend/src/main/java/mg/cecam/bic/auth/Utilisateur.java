package mg.cecam.bic.auth;

import jakarta.persistence.*;
import lombok.*;
import mg.cecam.bic.common.enums.Role;

@Entity
@Table(name = "utilisateur")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom_utilisateur", unique = true, nullable = false)
    private String nomUtilisateur;

    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse; 

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Builder.Default
    private Boolean actif = true;
}