# Graph Report - BIC  (2026-10-19)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 1361 nodes · 3134 edges · 131 communities (47 shown, 84 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 137 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `0a8d8329`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- ScoreService
- SecurityConfig.java
- App.tsx
- ModificationContratDemande
- JwtAuthenticationFilter.java
- ContratForm.tsx
- RapportSolvabiliteResponse
- MiseAJourDemandePage.tsx
- JournalAudit
- rapport.ts
- localdate
- lombok.RequiredArgsConstructor
- GestionUtilisateursPage.tsx
- org.springframework.http.ResponseEntity
- bigdecimal
- ClientService
- .toDetailContrat
- Contrat.java
- ImportPage.tsx
- components.json
- ContratService
- package.json
- compilerOptions
- RapportService
- Contrat
- dropdown-menu.tsx
- Echeance
- SaisieHistoriquePage.tsx
- .construire
- dependencies
- compilerOptions
- Client
- EcheanceController.java
- list
- .historiquePaiement
- Garantie
- ContratController
- Contrat
- devDependencies
- AffaireIndividuelle
- Utilisateur
- ContratLien
- clientSchema.ts
- Adresse
- AdresseAffaireIndividuelle
- ClientRequest
- BienLeasing
- ContratRepository
- HistoriquePaiementMensuel
- org.springframework.data.jpa.repository.JpaRepository
- Emploi
- axiosClient.ts
- .toScoreDto
- ClientRepository
- Identifiant
- CategorieTiers
- GrilleScore
- GestionUtilisateursPage
- Genre
- PhaseDemande
- DetailContratDTO
- BicBackendApplication.java
- ModeRattachement
- demande_modification_contrat
- 20261012_add_affaire_individuelle.sql
- BicBackendApplicationTests.java
- scripts
- tsconfig.json
- vite.config.ts
- MotifCloture
- RoleClient
- apiError.ts
- DemandesModificationPage
- PageShell.tsx
- ImportLigneDTO.java
- .rechercher
- .enregistrerPaiement
- ContactDTO.java
- GrilleScoreDTO.java
- RepartitionLigneDTO.java
- ScoreDTO.java
- client
- login
- Adresse
- enums/CategorieTiers.java
- Transient
- PatchMapping
- PutMapping
- mg.cecam:bic-backend

## God Nodes (most connected - your core abstractions)
1. `Contrat` - 84 edges
2. `RapportService` - 56 edges
3. `Client` - 45 edges
4. `Echeance` - 44 edges
5. `ScoreService` - 37 edges
6. `ContratService` - 31 edges
7. `react` - 30 edges
8. `RapportSolvabiliteResponse` - 26 edges
9. `Button()` - 26 edges
10. `Input()` - 23 edges

## Surprising Connections (you probably didn't know these)
- `CalendrierCreditDTO` --references--> `LigneAnneeDTO`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/rapport/dto/CalendrierCreditDTO.java → bic-backend/src/main/java/mg/cecam/bic/rapport/dto/LigneAnneeDTO.java
- `AuthController` --references--> `JwtUtil`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/auth/AuthController.java → bic-backend/src/main/java/mg/cecam/bic/auth/JwtUtil.java
- `ScoreService` --references--> `ScoreProperties`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/score/ScoreService.java → bic-backend/src/main/java/mg/cecam/bic/score/ScoreProperties.java
- `JwtAuthenticationFilter` --references--> `AppUserDetailsService`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/auth/JwtAuthenticationFilter.java → bic-backend/src/main/java/mg/cecam/bic/auth/AppUserDetailsService.java
- `AffaireIndividuelle` --references--> `Client`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/client/AffaireIndividuelle.java → bic-backend/src/main/java/mg/cecam/bic/client/Client.java

## Import Cycles
- None detected.

## Communities (131 total, 84 thin omitted)

### Community 0 - "ScoreService"
Cohesion: 0.06
Nodes (4): JwtAuthenticationFilter, JwtUtil, SecurityConfig, ScoreProperties

### Community 1 - "SecurityConfig.java"
Cohesion: 0.08
Nodes (34): authApi, LoginResponse, axiosClient, App(), AppShell(), pageTransition, RapportPageRoute(), AdminRoute() (+26 more)

### Community 2 - "App.tsx"
Cohesion: 0.14
Nodes (31): utilisateurApi, UtilisateurRequest, UtilisateurResponse, Button(), buttonVariants, Card(), CardContent(), CardDescription() (+23 more)

### Community 3 - "ModificationContratDemande"
Cohesion: 0.12
Nodes (31): ChampsObligatoiresNote(), affichageVersIso(), DateInput, DateInputProps, isoVersAffichage(), Input(), MontantInput, SelectContent() (+23 more)

### Community 4 - "JwtAuthenticationFilter.java"
Cohesion: 0.06
Nodes (31): rapportApi, ProblemDetail, RapportPage(), telechargerEtVisualiserPdf(), Adresse, AffaireIndividuelle, Alerte, BienLeasing (+23 more)

### Community 5 - "ContratForm.tsx"
Cohesion: 0.09
Nodes (3): AuthController, LoginResponse, GlobalExceptionHandler

### Community 6 - "RapportSolvabiliteResponse"
Cohesion: 0.12
Nodes (24): contratApi, modificationContratApi, formatAffichage(), MontantInputProps, RadioGroup(), RadioGroupItem(), getFieldBorderClass(), getStatutContrat() (+16 more)

### Community 7 - "MiseAJourDemandePage.tsx"
Cohesion: 0.13
Nodes (3): UtilisateurRequest, UtilisateurResponse, UtilisateurController

### Community 9 - "rapport.ts"
Cohesion: 0.14
Nodes (4): ScoreResult, Observation, RevenuEvaluation, ScoreService

### Community 10 - "localdate"
Cohesion: 0.11
Nodes (5): RoleClient, CO_TITULAIRE, GARANT, TITULAIRE, ScoreColorMapper

### Community 11 - "lombok.RequiredArgsConstructor"
Cohesion: 0.17
Nodes (7): PhaseRequest, BienLeasingHistoriqueRequest, ClientLieHistoriqueRequest, GarantieHistoriqueRequest, HistoriqueContratRequest, HistoriquePaiementMensuelRequest, ModificationContratRequest

### Community 13 - "org.springframework.http.ResponseEntity"
Cohesion: 0.12
Nodes (11): AlerteDTO, BienLeasingDTO, ConsultationDTO, ContactDTO, DernierContratDTO, DetailContratDTO, EmploiDTO, IdentifiantDTO (+3 more)

### Community 14 - "bigdecimal"
Cohesion: 0.11
Nodes (21): name, private, type, version, class-variance-authority, eslint, @eslint/js, eslint-plugin-react-hooks (+13 more)

### Community 16 - ".toDetailContrat"
Cohesion: 0.09
Nodes (21): aliases, components, hooks, lib, ui, utils, iconLibrary, menuAccent (+13 more)

### Community 18 - "ImportPage.tsx"
Cohesion: 0.10
Nodes (20): compilerOptions, allowArbitraryExtensions, allowImportingTsExtensions, erasableSyntaxOnly, jsx, lib, module, moduleDetection (+12 more)

### Community 19 - "components.json"
Cohesion: 0.10
Nodes (18): adresseSchema, affaireIndividuelleAdresseSchema, affaireIndividuelleContactSchema, affaireIndividuelleIdentifiantSchema, affaireIndividuelleSchema, ClientFormInput, ClientFormValues, clientSchema (+10 more)

### Community 21 - "package.json"
Cohesion: 0.20
Nodes (9): AppBreadcrumb(), segmentsForPath(), DropdownMenu(), DropdownMenuContent(), DropdownMenuItem(), DropdownMenuSub(), DropdownMenuSubContent(), DropdownMenuSubTrigger() (+1 more)

### Community 22 - "compilerOptions"
Cohesion: 0.16
Nodes (6): UtilisateurRepository, EmploiRepository, EvenementNegatifRepository, ContratLienRepository, EcheanceRepository, GrilleScoreRepository

### Community 24 - "Contrat"
Cohesion: 0.14
Nodes (6): StatutDemandeModification, APPROUVEE, EN_ATTENTE, REFUSEE, ModificationContratDemande, ModificationContratDemandeRepository

### Community 26 - "Echeance"
Cohesion: 0.11
Nodes (18): dependencies, axios, @base-ui/react, class-variance-authority, cn, @fontsource-variable/geist, @hookform/resolvers, lucide-react (+10 more)

### Community 27 - "SaisieHistoriquePage.tsx"
Cohesion: 0.17
Nodes (4): ModeRattachement, DEMANDE_EXISTANTE, ENTREPRISE, NOUVELLE_DEMANDE

### Community 29 - "dependencies"
Cohesion: 0.12
Nodes (16): compilerOptions, allowImportingTsExtensions, erasableSyntaxOnly, lib, module, moduleDetection, noEmit, noFallthroughCasesInSwitch (+8 more)

### Community 31 - "Client"
Cohesion: 0.17
Nodes (3): CodesEtablissementRequest, ContratController, EcheanceMensuelleRequest

### Community 32 - "EcheanceController.java"
Cohesion: 0.16
Nodes (14): clientApi, ClientEnregistrementResponse, AdresseRequest, AffaireIndividuelleAdresseRequest, AffaireIndividuelleContactRequest, AffaireIndividuelleIdentifiantRequest, AffaireIndividuelleRequest, ClientRequest (+6 more)

### Community 34 - ".historiquePaiement"
Cohesion: 0.19
Nodes (12): Champ(), ClientLieLigne, defaults, EcheanceLigne, FormValues, GarantieLigne, MoisLigne, optionalDate() (+4 more)

### Community 37 - "Contrat"
Cohesion: 0.15
Nodes (9): BucketRetard, A_JOUR, JOURS_120_149, JOURS_150_179, JOURS_180_PLUS, JOURS_1_29, JOURS_30_59, JOURS_60_89 (+1 more)

### Community 38 - "devDependencies"
Cohesion: 0.23
Nodes (3): CelluleMoisDTO, EncoursCategorieDTO, LigneAnneeDTO

### Community 41 - "ContratLien"
Cohesion: 0.15
Nodes (13): devDependencies, eslint, @eslint/js, eslint-plugin-react-hooks, eslint-plugin-react-refresh, globals, @types/node, @types/react (+5 more)

### Community 42 - "clientSchema.ts"
Cohesion: 0.17
Nodes (4): Utilisateur, Role, ADMIN, AGENT_CREDIT

### Community 44 - "AdresseAffaireIndividuelle"
Cohesion: 0.27
Nodes (4): ContratService, ContratRequest, DernierContratRequest, RevenuDemandeRequest

### Community 53 - "ClientRepository"
Cohesion: 0.27
Nodes (6): AdresseAffaireIndividuelleRequest, AdresseRequest, AffaireIndividuelleRequest, ClientRequest, EmploiRequest, IdentifiantRequest

### Community 55 - "CategorieTiers"
Cohesion: 0.20
Nodes (3): AdresseDTO, AffaireIndividuelleDTO, ClientInfoDTO

### Community 58 - "Genre"
Cohesion: 0.33
Nodes (4): SituationFinanciereMensuelleDTO, Cle, SituationFinanciereMensuelleService, Totaux

### Community 66 - "scripts"
Cohesion: 0.39
Nodes (6): GestionUtilisateursPage(), chargerUtilisateurs(), fermerFormulaire(), handleSubmit(), handleSupprimer(), handleToggleActif()

### Community 71 - "RoleClient"
Cohesion: 0.40
Nodes (4): ClientResponse, Genre, FEMME, HOMME

### Community 72 - "apiError.ts"
Cohesion: 0.33
Nodes (6): PhaseDemande, ABANDONNE, ACTIF, DEMANDE_EN_COURS, FERME, REFUSE

### Community 74 - "PageShell.tsx"
Cohesion: 0.40
Nodes (4): OrigineDonnee, DONNEE_FICTIVE, NON_RENSEIGNEE, RAPPORT_CRIF

### Community 75 - "ImportLigneDTO.java"
Cohesion: 0.40
Nodes (5): StatutEcheance, A_VENIR, EN_RETARD, IMPAYE, PAYE_A_TEMPS

### Community 77 - ".enregistrerPaiement"
Cohesion: 0.60
Nodes (3): demande_modification_contrat, idx_demande_modification_statut_date, uq_demande_modification_contrat_en_attente

### Community 78 - "ContactDTO.java"
Cohesion: 0.60
Nodes (3): adresse_affaire_individuelle, affaire_individuelle, idx_adresse_affaire_individuelle_parent

### Community 79 - "GrilleScoreDTO.java"
Cohesion: 0.40
Nodes (5): scripts, build, dev, lint, preview

### Community 80 - "RepartitionLigneDTO.java"
Cohesion: 0.70
Nodes (5): useIdleLogout(), effacerMinuteur(), enregistrerActivite(), programmerExpiration(), surChangementStockage()

### Community 81 - "ScoreDTO.java"
Cohesion: 0.40
Nodes (4): compilerOptions, paths, files, references

### Community 82 - "client"
Cohesion: 0.40
Nodes (3): @tailwindcss/vite, vite, @vitejs/plugin-react

### Community 83 - "login"
Cohesion: 0.50
Nodes (3): MotifCloture, FIN_A_TERME, FIN_ANTICIPEE

### Community 84 - "Adresse"
Cohesion: 0.50
Nodes (3): NatureGarantie, PERSONNELLE, REELLE

### Community 87 - "PatchMapping"
Cohesion: 0.67
Nodes (3): approuver(), charger(), refuser()

## Knowledge Gaps
- **46 isolated node(s):** `CO_TITULAIRE`, `GARANT`, `TITULAIRE`, `mg.cecam:bic-backend`, `@fontsource-variable/geist` (+41 more)
  These have ≤1 connection - possible missing edges. (Counts symbols only; 556 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **84 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Contrat` connect `JournalAudit` to `rapport.ts`, `localdate`, `lombok.RequiredArgsConstructor`, `org.springframework.http.ResponseEntity`, `Contrat.java`, `ContratService`, `RapportService`, `Contrat`, `dropdown-menu.tsx`, `compilerOptions`, `Client`, `list`, `Garantie`, `devDependencies`, `AffaireIndividuelle`, `AdresseAffaireIndividuelle`, `ClientRequest`, `org.springframework.data.jpa.repository.JpaRepository`, `Emploi`, `axiosClient.ts`, `.toScoreDto`, `Genre`?**
  _High betweenness centrality (0.073) - this node is a cross-community bridge._
- **What connects `CO_TITULAIRE`, `GARANT`, `TITULAIRE` to the rest of the system?**
  _46 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `ScoreService` be split into smaller, more focused modules?**
  _Cohesion score 0.05925925925925926 - nodes in this community are weakly interconnected._
- **Why does `Client` connect `RapportService` to `list`, `MiseAJourDemandePage.tsx`, `JournalAudit`, `rapport.ts`, `localdate`, `lombok.RequiredArgsConstructor`, `GestionUtilisateursPage.tsx`, `Adresse`, `org.springframework.http.ResponseEntity`, `ClientService`, `HistoriquePaiementMensuel`, `ClientRepository`, `Identifiant`, `CategorieTiers`, `SaisieHistoriquePage.tsx`, `DetailContratDTO`, `BicBackendApplication.java`?**
  _High betweenness centrality (0.053) - this node is a cross-community bridge._
- **Should `SecurityConfig.java` be split into smaller, more focused modules?**
  _Cohesion score 0.08127721335268505 - nodes in this community are weakly interconnected._
- **Why does `Echeance` connect `ContratService` to `list`, `Garantie`, `devDependencies`, `MiseAJourDemandePage.tsx`, `JournalAudit`, `rapport.ts`, `.rechercher`, `ClientRequest`, `compilerOptions`, `dropdown-menu.tsx`, `Genre`, `.construire`, `compilerOptions`?**
  _High betweenness centrality (0.023) - this node is a cross-community bridge._
- **Should `App.tsx` be split into smaller, more focused modules?**
  _Cohesion score 0.13506012950971322 - nodes in this community are weakly interconnected._