package mg.cecam.bic.client;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "identifiant", uniqueConstraints = @UniqueConstraint(name = "uq_identifiant_type_numero", columnNames = {"type_identifiant", "numero"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Identifiant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @JsonIgnore
    private Client client;

    @NotBlank
    @Column(name = "type_identifiant", nullable = false)
    private String typeIdentifiant;

    @NotBlank
    @Column(nullable = false)
    private String numero;
}