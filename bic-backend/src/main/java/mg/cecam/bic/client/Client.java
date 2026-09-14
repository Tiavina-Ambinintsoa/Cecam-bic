package mg.cecam.bic.client;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import mg.cecam.bic.common.enums.Genre;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "client")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code_client_cb", unique = true, length = 20)
    private String codeClientCb;

    private String titre; 

    @NotBlank
    @Column(name = "categorie_tiers_code", nullable = false)
    private String categorieTiersCode;

    @NotBlank
    private String prenom;

    @Column(name = "deuxieme_prenom")
    private String deuxiemePrenom;

    @NotBlank
    private String nom;

    @NotNull
    @Column(name = "date_naissance", nullable = false)
    private LocalDate dateNaissance;

    @Column(name = "ville_naissance")
    private String villeNaissance;

    @Column(name = "pays_naissance")
    private String paysNaissance;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Genre genre;

    @NotBlank
    @Builder.Default
    private String nationalite = "Malgache";

    @Column(name = "etat_civil")
    private String etatCivil;

    @Column(name = "date_adhesion", updatable = false)
private LocalDate dateAdhesion; // fixée une seule fois à la création, jamais modifiée ensuite

@Column(name = "date_derniere_modification")
private LocalDateTime dateDerniereModification;

@PrePersist
void onCreate() {
    this.dateAdhesion = LocalDate.now();
    this.dateDerniereModification = LocalDateTime.now();
}

@PreUpdate
void onUpdate() {
    this.dateDerniereModification = LocalDateTime.now();
}

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Adresse> adresses = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Identifiant> identifiants = new ArrayList<>();

}