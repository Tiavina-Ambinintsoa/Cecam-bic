# Graph Report - BIC  (2026-10-05)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 948 nodes · 2160 edges · 59 communities (34 shown, 25 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 85 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `150a465d`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- JwtAuthenticationFilter.java
- App.tsx
- SecurityConfig.java
- RapportSolvabiliteResponse
- ContratForm.tsx
- MiseAJourDemandePage.tsx
- Client
- ContratService
- StatutEcheance
- rapport.ts
- Contrat
- org.springframework.transaction.annotation.Transactional
- components.json
- package.json
- list
- bigdecimal
- RapportService
- compilerOptions
- ScoreService
- ContratLien
- dropdown-menu.tsx
- RechercheIndividuPage.tsx
- dependencies
- compilerOptions
- lombok.RequiredArgsConstructor
- RapportService.java
- Echeance
- ContratService.java
- devDependencies
- .toDetailContrat
- JournalAudit
- Emploi
- ScoreService.java
- BienLeasing
- Garantie
- AlerteService.java
- GrilleScore
- axiosClient.ts
- react
- MontantFormatUtil
- CategorieTiers
- Genre
- AppUserDetailsService.java
- EcheanceController
- BicBackendApplication.java
- ModeRattachement
- BicBackendApplicationTests.java
- scripts
- apiError.ts
- tsconfig.json
- vite.config.ts
- NatureGarantie
- PageShell.tsx
- ImportLigneDTO.java
- mg.cecam:bic-backend

## God Nodes (most connected - your core abstractions)
1. `Contrat` - 64 edges
2. `RapportService` - 46 edges
3. `Client` - 37 edges
4. `Echeance` - 36 edges
5. `react` - 28 edges
6. `RapportSolvabiliteResponse` - 23 edges
7. `StatutEcheance` - 23 edges
8. `PhaseDemande` - 22 edges
9. `ScoreService` - 21 edges
10. `ClientForm()` - 20 edges

## Surprising Connections (you probably didn't know these)
- `CalendrierCreditDTO` --references--> `LigneAnneeDTO`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/rapport/dto/CalendrierCreditDTO.java → bic-backend/src/main/java/mg/cecam/bic/rapport/dto/LigneAnneeDTO.java
- `EcheanceHistoriqueRequest` --references--> `StatutEcheance`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/contrat/dto/HistoriqueContratRequest.java → bic-backend/src/main/java/mg/cecam/bic/common/enums/StatutEcheance.java
- `JwtAuthenticationFilter` --references--> `AppUserDetailsService`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/auth/JwtAuthenticationFilter.java → bic-backend/src/main/java/mg/cecam/bic/auth/AppUserDetailsService.java
- `SecurityConfig` --references--> `JwtAuthenticationFilter`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/config/SecurityConfig.java → bic-backend/src/main/java/mg/cecam/bic/auth/JwtAuthenticationFilter.java
- `AuthController` --references--> `JwtUtil`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/auth/AuthController.java → bic-backend/src/main/java/mg/cecam/bic/auth/JwtUtil.java

## Import Cycles
- None detected.

## Communities (59 total, 25 thin omitted)

### Community 0 - "JwtAuthenticationFilter.java"
Cohesion: 0.05
Nodes (7): JwtAuthenticationFilter, JwtUtil, Utilisateur, Role, ADMIN, AGENT_CREDIT, ScoreProperties

### Community 1 - "App.tsx"
Cohesion: 0.08
Nodes (36): authApi, App(), AppShell(), pageTransition, RapportPageRoute(), AdminRoute(), ProtectedRoute(), HomeMenu() (+28 more)

### Community 2 - "SecurityConfig.java"
Cohesion: 0.07
Nodes (5): AuthController, LoginRequest, LoginResponse, GlobalExceptionHandler, SecurityConfig

### Community 3 - "RapportSolvabiliteResponse"
Cohesion: 0.06
Nodes (14): AdresseDTO, ClientInfoDTO, ConsultationDTO, ContactDTO, DetailDemandeDTO, EmploiDTO, GrilleScoreDTO, IdentifiantDTO (+6 more)

### Community 4 - "ContratForm.tsx"
Cohesion: 0.09
Nodes (28): buttonVariants, affichageVersIso(), DateInput, DateInputProps, isoVersAffichage(), formatAffichage(), MontantInput, MontantInputProps (+20 more)

### Community 5 - "MiseAJourDemandePage.tsx"
Cohesion: 0.16
Nodes (22): Button(), Card(), CardContent(), CardHeader(), CardTitle(), ChampsObligatoiresNote(), Input(), Label() (+14 more)

### Community 6 - "Client"
Cohesion: 0.06
Nodes (3): Adresse, Client, Identifiant

### Community 7 - "ContratService"
Cohesion: 0.11
Nodes (11): MotifCloture, FIN_A_TERME, FIN_ANTICIPEE, ContratController, EcheanceMensuelleRequest, PhaseRequest, ContratService, ContratRequest (+3 more)

### Community 8 - "StatutEcheance"
Cohesion: 0.13
Nodes (10): StatutEcheance, A_VENIR, EN_RETARD, IMPAYE, PAYE_A_TEMPS, LabelMapper, CalendrierService, CelluleMoisDTO (+2 more)

### Community 9 - "rapport.ts"
Cohesion: 0.09
Nodes (22): ScoreCardProps, Adresse, Alerte, CalendrierCredit, CelluleMois, ClientInfo, Contact, DetailContrat (+14 more)

### Community 11 - "org.springframework.transaction.annotation.Transactional"
Cohesion: 0.14
Nodes (7): ClientController, ClientService, ClientEnregistrementResponse, AdresseRequest, ClientRequest, EmploiRequest, IdentifiantRequest

### Community 12 - "components.json"
Cohesion: 0.09
Nodes (21): aliases, components, hooks, lib, ui, utils, iconLibrary, menuAccent (+13 more)

### Community 13 - "package.json"
Cohesion: 0.11
Nodes (20): name, private, type, version, class-variance-authority, eslint, @eslint/js, eslint-plugin-react-hooks (+12 more)

### Community 14 - "list"
Cohesion: 0.17
Nodes (5): UtilisateurRepository, ClientRepository, ImportResultatDTO, ContratRepository, EcheanceRepository

### Community 17 - "compilerOptions"
Cohesion: 0.10
Nodes (20): compilerOptions, allowArbitraryExtensions, allowImportingTsExtensions, erasableSyntaxOnly, jsx, lib, module, moduleDetection (+12 more)

### Community 18 - "ScoreService"
Cohesion: 0.17
Nodes (4): ScoreDTO, ScoreDetail, ScoreResult, ScoreService

### Community 19 - "ContratLien"
Cohesion: 0.15
Nodes (4): ContratLien, ContratLienRepository, LienClientDTO, LigneFinancementDTO

### Community 20 - "dropdown-menu.tsx"
Cohesion: 0.20
Nodes (9): AppBreadcrumb(), segmentsForPath(), DropdownMenu(), DropdownMenuContent(), DropdownMenuItem(), DropdownMenuSub(), DropdownMenuSubContent(), DropdownMenuSubTrigger() (+1 more)

### Community 21 - "RechercheIndividuPage.tsx"
Cohesion: 0.20
Nodes (13): clientApi, ClientEnregistrementResponse, contratApi, AdresseRequest, ClientRequest, ClientResponse, EmploiRequest, Genre (+5 more)

### Community 22 - "dependencies"
Cohesion: 0.12
Nodes (17): dependencies, axios, @base-ui/react, class-variance-authority, cn, @fontsource-variable/geist, @hookform/resolvers, lucide-react (+9 more)

### Community 23 - "compilerOptions"
Cohesion: 0.12
Nodes (16): compilerOptions, allowImportingTsExtensions, erasableSyntaxOnly, lib, module, moduleDetection, noEmit, noFallthroughCasesInSwitch (+8 more)

### Community 24 - "lombok.RequiredArgsConstructor"
Cohesion: 0.29
Nodes (3): AuditController, UtilisateurController, AlerteController

### Community 26 - "RapportService.java"
Cohesion: 0.15
Nodes (5): RoleClient, CO_TITULAIRE, GARANT, TITULAIRE, ScoreColorMapper

### Community 29 - "devDependencies"
Cohesion: 0.15
Nodes (13): devDependencies, eslint, @eslint/js, eslint-plugin-react-hooks, eslint-plugin-react-refresh, globals, @types/node, @types/react (+5 more)

### Community 30 - ".toDetailContrat"
Cohesion: 0.25
Nodes (4): BienLeasingDTO, DetailContratDTO, GarantieDTO, HistoriquePaiementLigneDTO

### Community 33 - "ScoreService.java"
Cohesion: 0.22
Nodes (6): PhaseDemande, ABANDONNE, ACTIF, DEMANDE_EN_COURS, FERME, REFUSE

### Community 38 - "axiosClient.ts"
Cohesion: 0.31
Nodes (5): LoginResponse, axiosClient, rapportApi, Alerte, RapportSolvabilite

### Community 39 - "react"
Cohesion: 0.27
Nodes (4): ToastContext, ToastContextValue, ToastItem, react

### Community 42 - "Genre"
Cohesion: 0.33
Nodes (4): ClientResponse, Genre, FEMME, HOMME

### Community 46 - "ModeRattachement"
Cohesion: 0.40
Nodes (4): ModeRattachement, DEMANDE_EXISTANTE, ENTREPRISE, NOUVELLE_DEMANDE

### Community 48 - "scripts"
Cohesion: 0.40
Nodes (5): scripts, build, dev, lint, preview

### Community 50 - "tsconfig.json"
Cohesion: 0.40
Nodes (4): compilerOptions, paths, files, references

### Community 51 - "vite.config.ts"
Cohesion: 0.40
Nodes (3): @tailwindcss/vite, vite, @vitejs/plugin-react

### Community 52 - "NatureGarantie"
Cohesion: 0.50
Nodes (3): NatureGarantie, PERSONNELLE, REELLE

## Knowledge Gaps
- **175 isolated node(s):** `Tache`, `AuthState`, `Role`, `DemandeEnCoursState`, `EntreeAudit` (+170 more)
  These have ≤1 connection - possible missing edges. (Counts symbols only; 387 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **25 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Contrat` connect `Contrat` to `ScoreService.java`, `BienLeasing`, `Garantie`, `AlerteService.java`, `Client`, `ContratService`, `StatutEcheance`, `ModeRattachement`, `bigdecimal`, `list`, `RapportService`, `ScoreService`, `ContratLien`, `RapportService.java`, `Echeance`, `.toDetailContrat`?**
  _High betweenness centrality (0.068) - this node is a cross-community bridge._
- **What connects `Tache`, `AuthState`, `Role` to the rest of the system?**
  _175 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `JwtAuthenticationFilter.java` be split into smaller, more focused modules?**
  _Cohesion score 0.054901960784313725 - nodes in this community are weakly interconnected._
- **Why does `Client` connect `Client` to `Emploi`, `ScoreService.java`, `AlerteService.java`, `Genre`, `org.springframework.transaction.annotation.Transactional`, `Contrat`, `list`, `bigdecimal`, `RapportService`, `ScoreService`, `ContratLien`, `lombok`, `RapportService.java`, `ContratService.java`?**
  _High betweenness centrality (0.054) - this node is a cross-community bridge._
- **Should `App.tsx` be split into smaller, more focused modules?**
  _Cohesion score 0.0815686274509804 - nodes in this community are weakly interconnected._
- **Why does `RapportService` connect `RapportService` to `Emploi`, `ScoreService.java`, `BienLeasing`, `RapportSolvabiliteResponse`, `Garantie`, `AlerteService.java`, `GrilleScore`, `StatutEcheance`, `list`, `ScoreService`, `ContratLien`, `lombok.RequiredArgsConstructor`, `RapportService.java`, `ContratService.java`, `.toDetailContrat`, `JournalAudit`?**
  _High betweenness centrality (0.034) - this node is a cross-community bridge._
- **Should `SecurityConfig.java` be split into smaller, more focused modules?**
  _Cohesion score 0.06956521739130435 - nodes in this community are weakly interconnected._