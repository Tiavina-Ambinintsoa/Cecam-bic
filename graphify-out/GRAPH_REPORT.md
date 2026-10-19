# Graph Report - BIC  (2026-10-19)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 1370 nodes · 3087 edges · 131 communities (50 shown, 81 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 131 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `9492860f`
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
- useIdleLogout
- tsconfig.json
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
- enums/CategorieTiers.java
- mg.cecam:bic-backend

## God Nodes (most connected - your core abstractions)
1. `Contrat` - 76 edges
2. `RapportService` - 55 edges
3. `ScoreService` - 36 edges
4. `Echeance` - 34 edges
5. `Client` - 32 edges
6. `react` - 32 edges
7. `ContratService` - 28 edges
8. `Button()` - 26 edges
9. `Input()` - 23 edges
10. `GestionUtilisateursPage()` - 23 edges

## Surprising Connections (you probably didn't know these)
- `JwtAuthenticationFilter` --references--> `AppUserDetailsService`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/auth/JwtAuthenticationFilter.java → bic-backend/src/main/java/mg/cecam/bic/auth/AppUserDetailsService.java
- `AuthController` --references--> `JwtUtil`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/auth/AuthController.java → bic-backend/src/main/java/mg/cecam/bic/auth/JwtUtil.java
- `ScoreService` --references--> `ScoreProperties`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/score/ScoreService.java → bic-backend/src/main/java/mg/cecam/bic/score/ScoreProperties.java
- `HistoriqueContratRequest` --references--> `OrigineDonnee`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/contrat/dto/HistoriqueContratRequest.java → bic-backend/src/main/java/mg/cecam/bic/common/enums/OrigineDonnee.java
- `ClientEnregistrementResponse` --references--> `ClientResponse`  [EXTRACTED]
  bic-frontend/src/api/clientApi.ts → bic-frontend/src/types/client.ts

## Import Cycles
- None detected.

## Communities (131 total, 81 thin omitted)

### Community 0 - "ScoreService"
Cohesion: 0.06
Nodes (4): JwtAuthenticationFilter, JwtUtil, SecurityConfig, ScoreProperties

### Community 1 - "SecurityConfig.java"
Cohesion: 0.07
Nodes (38): authApi, App(), AppShell(), pageTransition, RapportPageRoute(), AdminRoute(), ProtectedRoute(), HomeMenu() (+30 more)

### Community 2 - "App.tsx"
Cohesion: 0.11
Nodes (36): ChampsObligatoiresNote(), affichageVersIso(), DateInput, DateInputProps, isoVersAffichage(), Input(), formatAffichage(), MontantInput (+28 more)

### Community 3 - "ModificationContratDemande"
Cohesion: 0.14
Nodes (28): utilisateurApi, UtilisateurRequest, UtilisateurResponse, Button(), buttonVariants, Card(), CardContent(), CardDescription() (+20 more)

### Community 4 - "JwtAuthenticationFilter.java"
Cohesion: 0.05
Nodes (6): EncoursCategorieDTO, RapportSolvabiliteResponse, SyntheseCategorieDTO, SyntheseDTO, RapportController, RapportPdfService

### Community 5 - "ContratForm.tsx"
Cohesion: 0.06
Nodes (31): rapportApi, ScoreCardProps, ProblemDetail, RapportPage(), telechargerEtVisualiserPdf(), Adresse, AffaireIndividuelle, Alerte (+23 more)

### Community 6 - "RapportSolvabiliteResponse"
Cohesion: 0.09
Nodes (4): AuthController, LoginRequest, LoginResponse, GlobalExceptionHandler

### Community 7 - "MiseAJourDemandePage.tsx"
Cohesion: 0.12
Nodes (4): ContratLien, ContratLienRepository, LienClientDTO, LigneFinancementDTO

### Community 8 - "JournalAudit"
Cohesion: 0.12
Nodes (3): Contrat, RepartitionLigneDTO, RapportService

### Community 9 - "rapport.ts"
Cohesion: 0.15
Nodes (4): ScoreResult, Observation, RevenuEvaluation, ScoreService

### Community 10 - "localdate"
Cohesion: 0.09
Nodes (3): ScoreColorMapper, GrilleScoreDTO, ScoreDTO

### Community 11 - "lombok.RequiredArgsConstructor"
Cohesion: 0.15
Nodes (8): PhaseRequest, BienLeasingHistoriqueRequest, ClientLieHistoriqueRequest, EcheanceHistoriqueRequest, GarantieHistoriqueRequest, HistoriqueContratRequest, HistoriquePaiementMensuelRequest, ModificationContratRequest

### Community 12 - "GestionUtilisateursPage.tsx"
Cohesion: 0.12
Nodes (19): LoginResponse, axiosClient, clientApi, ClientEnregistrementResponse, Champ(), ClientLieLigne, defaults, EcheanceLigne (+11 more)

### Community 13 - "org.springframework.http.ResponseEntity"
Cohesion: 0.18
Nodes (4): ModificationContratResponse, ModificationContratController, RefusRequest, ModificationContratService

### Community 15 - "ClientService"
Cohesion: 0.19
Nodes (4): UtilisateurController, CodesEtablissementRequest, ContratController, EcheanceMensuelleRequest

### Community 17 - "Contrat.java"
Cohesion: 0.09
Nodes (21): aliases, components, hooks, lib, ui, utils, iconLibrary, menuAccent (+13 more)

### Community 18 - "ImportPage.tsx"
Cohesion: 0.12
Nodes (19): name, private, type, version, class-variance-authority, eslint, @eslint/js, eslint-plugin-react-hooks (+11 more)

### Community 19 - "components.json"
Cohesion: 0.10
Nodes (20): compilerOptions, allowArbitraryExtensions, allowImportingTsExtensions, erasableSyntaxOnly, jsx, lib, module, moduleDetection (+12 more)

### Community 22 - "compilerOptions"
Cohesion: 0.11
Nodes (10): RoleClient, CO_TITULAIRE, GARANT, TITULAIRE, PaiementRequest, BienLeasingDTO, DetailDemandeDTO, EmploiDTO (+2 more)

### Community 25 - "dropdown-menu.tsx"
Cohesion: 0.14
Nodes (6): StatutDemandeModification, APPROUVEE, EN_ATTENTE, REFUSEE, ModificationContratDemande, ModificationContratDemandeRepository

### Community 26 - "Echeance"
Cohesion: 0.20
Nodes (9): AppBreadcrumb(), segmentsForPath(), DropdownMenu(), DropdownMenuContent(), DropdownMenuItem(), DropdownMenuSub(), DropdownMenuSubContent(), DropdownMenuSubTrigger() (+1 more)

### Community 27 - "SaisieHistoriquePage.tsx"
Cohesion: 0.11
Nodes (17): adresseSchema, affaireIndividuelleAdresseSchema, affaireIndividuelleContactSchema, affaireIndividuelleIdentifiantSchema, affaireIndividuelleSchema, ClientFormInput, ClientFormValues, clientSchema (+9 more)

### Community 28 - ".construire"
Cohesion: 0.12
Nodes (17): dependencies, axios, @base-ui/react, class-variance-authority, cn, @fontsource-variable/geist, @hookform/resolvers, lucide-react (+9 more)

### Community 29 - "dependencies"
Cohesion: 0.27
Nodes (12): contratApi, modificationContratApi, DEVISES, PHASE_LABELS, PHASES, ContratRequest, ContratResponse, MotifCloture (+4 more)

### Community 30 - "compilerOptions"
Cohesion: 0.12
Nodes (16): compilerOptions, allowImportingTsExtensions, erasableSyntaxOnly, lib, module, moduleDetection, noEmit, noFallthroughCasesInSwitch (+8 more)

### Community 35 - "Garantie"
Cohesion: 0.18
Nodes (3): AuditService, JournalAudit, JournalAuditRepository

### Community 36 - "ContratController"
Cohesion: 0.21
Nodes (6): UtilisateurRepository, EvenementNegatifRepository, BienLeasingRepository, GarantieRepository, HistoriquePaiementMensuelRepository, GrilleScoreRepository

### Community 38 - "devDependencies"
Cohesion: 0.14
Nodes (9): BucketRetard, A_JOUR, JOURS_120_149, JOURS_150_179, JOURS_180_PLUS, JOURS_1_29, JOURS_30_59, JOURS_60_89 (+1 more)

### Community 39 - "AffaireIndividuelle"
Cohesion: 0.31
Nodes (3): AuditController, AlerteController, AlerteDTO

### Community 42 - "clientSchema.ts"
Cohesion: 0.15
Nodes (13): devDependencies, eslint, @eslint/js, eslint-plugin-react-hooks, eslint-plugin-react-refresh, globals, @types/node, @types/react (+5 more)

### Community 43 - "Adresse"
Cohesion: 0.17
Nodes (4): Utilisateur, Role, ADMIN, AGENT_CREDIT

### Community 44 - "AdresseAffaireIndividuelle"
Cohesion: 0.17
Nodes (4): NatureGarantie, PERSONNELLE, REELLE, Garantie

### Community 48 - "HistoriquePaiementMensuel"
Cohesion: 0.18
Nodes (10): AdresseRequest, AffaireIndividuelleAdresseRequest, AffaireIndividuelleContactRequest, AffaireIndividuelleIdentifiantRequest, AffaireIndividuelleRequest, ContactRequest, DocumentIdentificationRequest, EmploiRequest (+2 more)

### Community 49 - "org.springframework.data.jpa.repository.JpaRepository"
Cohesion: 0.20
Nodes (5): StatutEcheance, A_VENIR, EN_RETARD, IMPAYE, PAYE_A_TEMPS

### Community 51 - "axiosClient.ts"
Cohesion: 0.22
Nodes (4): ImportResultatDTO, AffaireIndividuelleDTO, CalendrierCreditDTO, LigneAnneeDTO

### Community 52 - ".toScoreDto"
Cohesion: 0.20
Nodes (6): PhaseDemande, ABANDONNE, ACTIF, DEMANDE_EN_COURS, FERME, REFUSE

### Community 57 - "GestionUtilisateursPage"
Cohesion: 0.32
Nodes (6): AdresseAffaireIndividuelleRequest, AdresseRequest, AffaireIndividuelleRequest, ClientRequest, EmploiRequest, IdentifiantRequest

### Community 65 - "BicBackendApplicationTests.java"
Cohesion: 0.39
Nodes (6): GestionUtilisateursPage(), chargerUtilisateurs(), fermerFormulaire(), handleSubmit(), handleSupprimer(), handleToggleActif()

### Community 67 - "useIdleLogout"
Cohesion: 0.40
Nodes (4): ClientResponse, Genre, FEMME, HOMME

### Community 71 - "RoleClient"
Cohesion: 0.40
Nodes (4): ModeRattachement, DEMANDE_EXISTANTE, ENTREPRISE, NOUVELLE_DEMANDE

### Community 72 - "apiError.ts"
Cohesion: 0.40
Nodes (4): OrigineDonnee, DONNEE_FICTIVE, NON_RENSEIGNEE, RAPPORT_CRIF

### Community 73 - "DemandesModificationPage"
Cohesion: 0.70
Nodes (3): Cle, SituationFinanciereMensuelleService, Totaux

### Community 74 - "PageShell.tsx"
Cohesion: 0.60
Nodes (3): demande_modification_contrat, idx_demande_modification_statut_date, uq_demande_modification_contrat_en_attente

### Community 75 - "ImportLigneDTO.java"
Cohesion: 0.60
Nodes (3): adresse_affaire_individuelle, affaire_individuelle, idx_adresse_affaire_individuelle_parent

### Community 76 - ".rechercher"
Cohesion: 0.40
Nodes (5): scripts, build, dev, lint, preview

### Community 77 - ".enregistrerPaiement"
Cohesion: 0.40
Nodes (4): compilerOptions, paths, files, references

### Community 78 - "ContactDTO.java"
Cohesion: 0.40
Nodes (3): @tailwindcss/vite, vite, @vitejs/plugin-react

### Community 79 - "GrilleScoreDTO.java"
Cohesion: 0.50
Nodes (3): MotifCloture, FIN_A_TERME, FIN_ANTICIPEE

### Community 80 - "RepartitionLigneDTO.java"
Cohesion: 0.67
Nodes (3): MiseAJourDemandePage(), chargerMesDemandes(), soumettre()

### Community 82 - "client"
Cohesion: 0.67
Nodes (3): approuver(), charger(), refuser()

## Knowledge Gaps
- **46 isolated node(s):** `mg.cecam:bic-backend`, `@fontsource-variable/geist`, `shadcn`, `tailwindcss`, `tw-animate-css` (+41 more)
  These have ≤1 connection - possible missing edges. (Counts symbols only; 561 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **81 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Contrat` connect `JournalAudit` to `MiseAJourDemandePage.tsx`, `rapport.ts`, `localdate`, `lombok.RequiredArgsConstructor`, `org.springframework.http.ResponseEntity`, `ClientService`, `.toDetailContrat`, `package.json`, `compilerOptions`, `RapportService`, `dropdown-menu.tsx`, `EcheanceController.java`, `.historiquePaiement`, `AdresseAffaireIndividuelle`, `ContratRepository`, `org.springframework.data.jpa.repository.JpaRepository`, `ClientRepository`, `CategorieTiers`, `GrilleScore`, `DetailContratDTO`, `BicBackendApplication.java`, `DemandesModificationPage`?**
  _High betweenness centrality (0.079) - this node is a cross-community bridge._
- **What connects `mg.cecam:bic-backend`, `@fontsource-variable/geist`, `shadcn` to the rest of the system?**
  _46 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `ScoreService` be split into smaller, more focused modules?**
  _Cohesion score 0.05844155844155844 - nodes in this community are weakly interconnected._
- **Why does `RapportService` connect `JournalAudit` to `.historiquePaiement`, `JwtAuthenticationFilter.java`, `Contrat`, `ContratController`, `MiseAJourDemandePage.tsx`, `AffaireIndividuelle`, `DemandesModificationPage`, `localdate`, `rapport.ts`, `lombok.RequiredArgsConstructor`, `.toDetailContrat`, `Emploi`, `ContratService`, `RapportService`, `ModeRattachement`?**
  _High betweenness centrality (0.044) - this node is a cross-community bridge._
- **Should `SecurityConfig.java` be split into smaller, more focused modules?**
  _Cohesion score 0.07467532467532467 - nodes in this community are weakly interconnected._
- **Why does `Client` connect `list` to `EcheanceController.java`, `vite.config.ts`, `MiseAJourDemandePage.tsx`, `Utilisateur`, `ContratLien`, `lombok.RequiredArgsConstructor`, `ClientRequest`, `bigdecimal`, `Identifiant`, `Contrat`, `GestionUtilisateursPage`, `Genre`, `Client`?**
  _High betweenness centrality (0.036) - this node is a cross-community bridge._
- **Should `App.tsx` be split into smaller, more focused modules?**
  _Cohesion score 0.10714285714285714 - nodes in this community are weakly interconnected._