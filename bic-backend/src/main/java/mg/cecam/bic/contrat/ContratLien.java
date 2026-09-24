package mg.cecam.bic.contrat;

import jakarta.persistence.*;
import lombok.*;
import mg.cecam.bic.common.enums.RoleClient;

@Entity
@Table(name = "contrat_lien")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ContratLien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrat_id", nullable = false)
    private Contrat contrat;

    @Column(name = "code_client_cb", nullable = false, length = 20)
    private String codeClientCb;

    @Column(name = "nom_client")
    private String nomClient;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_client", nullable = false, length = 20)
    private RoleClient roleClient;
}