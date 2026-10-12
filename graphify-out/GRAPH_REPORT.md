# Graph Report - BIC  (2026-10-12)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 1352 nodes · 2911 edges · 132 communities (48 shown, 84 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 121 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `de394ed8`
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
- vite.config.ts
- MotifCloture
- RoleClient
- apiError.ts
- DemandesModificationPage
- PageShell.tsx
- ImportLigneDTO.java
- ContactDTO.java
- GrilleScoreDTO.java
- RepartitionLigneDTO.java
- ScoreDTO.java
- login
- mg.cecam:bic-backend

## God Nodes (most connected - your core abstractions)
1. `RapportService` - 54 edges
2. `Client` - 33 edges
3. `Contrat` - 32 edges
4. `react` - 31 edges
5. `ScoreService` - 26 edges
6. `ContratService` - 26 edges
7. `RapportSolvabiliteResponse` - 24 edges
8. `Button()` - 24 edges
9. `GestionUtilisateursPage()` - 23 edges
10. `Input()` - 21 edges

## Surprising Connections (you probably didn't know these)
- `LigneAnneeDTO` --references--> `CelluleMoisDTO`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/rapport/dto/LigneAnneeDTO.java → bic-backend/src/main/java/mg/cecam/bic/rapport/dto/CelluleMoisDTO.java
- `ScoreService` --references--> `ContratRepository`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/score/ScoreService.java → bic-backend/src/main/java/mg/cecam/bic/contrat/ContratRepository.java
- `AuthController` --references--> `JwtUtil`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/auth/AuthController.java → bic-backend/src/main/java/mg/cecam/bic/auth/JwtUtil.java
- `AuthController` --references--> `UtilisateurRepository`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/auth/AuthController.java → bic-backend/src/main/java/mg/cecam/bic/auth/UtilisateurRepository.java
- `AuditService` --references--> `JournalAuditRepository`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/audit/AuditService.java → bic-backend/src/main/java/mg/cecam/bic/audit/JournalAuditRepository.java

## Import Cycles
- None detected.

## Communities (132 total, 84 thin omitted)

### Community 0 - "ScoreService"
Cohesion: 0.06
Nodes (6): LabelMapper, MontantFormatUtil, CalendrierService, CelluleMoisDTO, ScoreResult, ScoreService

### Community 1 - "SecurityConfig.java"
Cohesion: 0.06
Nodes (5): AuthController, LoginRequest, LoginResponse, GlobalExceptionHandler, SecurityConfig

### Community 2 - "App.tsx"
Cohesion: 0.09
Nodes (28): App(), AppShell(), pageTransition, RapportPageRoute(), AdminRoute(), ProtectedRoute(), HomeMenu(), Tache (+20 more)

### Community 3 - "ModificationContratDemande"
Cohesion: 0.10
Nodes (10): StatutDemandeModification, APPROUVEE, EN_ATTENTE, REFUSEE, ModificationContratResponse, ModificationContratController, RefusRequest, ModificationContratDemande (+2 more)

### Community 4 - "JwtAuthenticationFilter.java"
Cohesion: 0.07
Nodes (4): AppUserDetailsService, JwtAuthenticationFilter, JwtUtil, ScoreProperties

### Community 5 - "ContratForm.tsx"
Cohesion: 0.15
Nodes (28): Button(), Card(), CardContent(), CardHeader(), CardTitle(), ChampsObligatoiresNote(), Input(), Label() (+20 more)

### Community 6 - "RapportSolvabiliteResponse"
Cohesion: 0.06
Nodes (5): EncoursCategorieDTO, RapportSolvabiliteResponse, SyntheseCategorieDTO, SyntheseDTO, RapportPdfService

### Community 7 - "MiseAJourDemandePage.tsx"
Cohesion: 0.14
Nodes (20): contratApi, modificationContratApi, ToastContext, ToastContextValue, ToastItem, useToast(), PHASES, DEVISES (+12 more)

### Community 8 - "JournalAudit"
Cohesion: 0.10
Nodes (6): AuditController, JournalAudit, JournalAuditRepository, AlerteController, AlerteService, AlerteDTO

### Community 9 - "rapport.ts"
Cohesion: 0.08
Nodes (25): ScoreCardProps, Adresse, AffaireIndividuelle, Alerte, BienLeasing, CalendrierCredit, CelluleMois, ClientInfo (+17 more)

### Community 12 - "GestionUtilisateursPage.tsx"
Cohesion: 0.13
Nodes (16): utilisateurApi, UtilisateurRequest, UtilisateurResponse, buttonVariants, SelectContent(), SelectScrollDownButton(), SelectScrollUpButton(), SelectValue() (+8 more)

### Community 13 - "org.springframework.http.ResponseEntity"
Cohesion: 0.17
Nodes (3): UtilisateurRequest, UtilisateurResponse, UtilisateurController

### Community 14 - "bigdecimal"
Cohesion: 0.12
Nodes (6): ModificationContratRequest, BienLeasingDTO, DetailDemandeDTO, EmploiDTO, GarantieDTO, SituationFinanciereMensuelleDTO

### Community 17 - "Contrat.java"
Cohesion: 0.13
Nodes (11): OrigineDonnee, DONNEE_FICTIVE, NON_RENSEIGNEE, RAPPORT_CRIF, ContratRequest, BienLeasingHistoriqueRequest, ClientLieHistoriqueRequest, EcheanceHistoriqueRequest (+3 more)

### Community 18 - "ImportPage.tsx"
Cohesion: 0.13
Nodes (19): clientApi, ClientEnregistrementResponse, analyserCsv(), clientDepuisCsv(), CsvRow, dateIso(), ENTETES, ENTETES_REQUISES (+11 more)

### Community 19 - "components.json"
Cohesion: 0.09
Nodes (21): aliases, components, hooks, lib, ui, utils, iconLibrary, menuAccent (+13 more)

### Community 21 - "package.json"
Cohesion: 0.12
Nodes (19): name, private, type, version, class-variance-authority, eslint, @eslint/js, eslint-plugin-react-hooks (+11 more)

### Community 22 - "compilerOptions"
Cohesion: 0.10
Nodes (20): compilerOptions, allowArbitraryExtensions, allowImportingTsExtensions, erasableSyntaxOnly, jsx, lib, module, moduleDetection (+12 more)

### Community 24 - "Contrat"
Cohesion: 0.13
Nodes (4): Contrat, Cle, SituationFinanciereMensuelleService, Totaux

### Community 25 - "dropdown-menu.tsx"
Cohesion: 0.20
Nodes (9): AppBreadcrumb(), segmentsForPath(), DropdownMenu(), DropdownMenuContent(), DropdownMenuItem(), DropdownMenuSub(), DropdownMenuSubContent(), DropdownMenuSubTrigger() (+1 more)

### Community 27 - "SaisieHistoriquePage.tsx"
Cohesion: 0.16
Nodes (15): affichageVersIso(), DateInput, DateInputProps, isoVersAffichage(), ClientLieLigne, defaults, EcheanceLigne, FormValues (+7 more)

### Community 28 - ".construire"
Cohesion: 0.15
Nodes (3): ConsultationDTO, IdentifiantDTO, RapportController

### Community 29 - "dependencies"
Cohesion: 0.12
Nodes (17): dependencies, axios, @base-ui/react, class-variance-authority, cn, @fontsource-variable/geist, @hookform/resolvers, lucide-react (+9 more)

### Community 30 - "compilerOptions"
Cohesion: 0.12
Nodes (16): compilerOptions, allowImportingTsExtensions, erasableSyntaxOnly, lib, module, moduleDetection, noEmit, noFallthroughCasesInSwitch (+8 more)

### Community 32 - "EcheanceController.java"
Cohesion: 0.15
Nodes (7): StatutEcheance, A_VENIR, EN_RETARD, IMPAYE, PAYE_A_TEMPS, PaiementRequest, EcheanceController

### Community 33 - "list"
Cohesion: 0.14
Nodes (6): ImportResultatDTO, AffaireIndividuelleDTO, CalendrierCreditDTO, LienClientDTO, LigneAnneeDTO, LigneFinancementDTO

### Community 34 - ".historiquePaiement"
Cohesion: 0.14
Nodes (9): BucketRetard, A_JOUR, JOURS_120_149, JOURS_150_179, JOURS_180_PLUS, JOURS_1_29, JOURS_30_59, JOURS_60_89 (+1 more)

### Community 35 - "Garantie"
Cohesion: 0.14
Nodes (5): NatureGarantie, PERSONNELLE, REELLE, Garantie, GarantieRepository

### Community 36 - "ContratController"
Cohesion: 0.19
Nodes (4): CodesEtablissementRequest, ContratController, EcheanceMensuelleRequest, PhaseRequest

### Community 38 - "devDependencies"
Cohesion: 0.15
Nodes (13): devDependencies, eslint, @eslint/js, eslint-plugin-react-hooks, eslint-plugin-react-refresh, globals, @types/node, @types/react (+5 more)

### Community 40 - "Utilisateur"
Cohesion: 0.17
Nodes (4): Utilisateur, Role, ADMIN, AGENT_CREDIT

### Community 42 - "clientSchema.ts"
Cohesion: 0.17
Nodes (10): adresseSchema, affaireIndividuelleSchema, ClientFormInput, ClientFormValues, clientSchema, emploiSchema, ContratFormInput, ContratFormValues (+2 more)

### Community 45 - "ClientRequest"
Cohesion: 0.24
Nodes (6): AdresseAffaireIndividuelleRequest, AdresseRequest, AffaireIndividuelleRequest, ClientRequest, EmploiRequest, IdentifiantRequest

### Community 49 - "org.springframework.data.jpa.repository.JpaRepository"
Cohesion: 0.31
Nodes (3): UtilisateurRepository, EmploiRepository, GrilleScoreRepository

### Community 51 - "axiosClient.ts"
Cohesion: 0.31
Nodes (6): authApi, LoginResponse, axiosClient, rapportApi, RapportSolvabilite, axios

### Community 57 - "GestionUtilisateursPage"
Cohesion: 0.39
Nodes (6): GestionUtilisateursPage(), chargerUtilisateurs(), fermerFormulaire(), handleSubmit(), handleSupprimer(), handleToggleActif()

### Community 58 - "Genre"
Cohesion: 0.33
Nodes (4): ClientResponse, Genre, FEMME, HOMME

### Community 59 - "PhaseDemande"
Cohesion: 0.33
Nodes (6): PhaseDemande, ABANDONNE, ACTIF, DEMANDE_EN_COURS, FERME, REFUSE

### Community 62 - "ModeRattachement"
Cohesion: 0.40
Nodes (4): ModeRattachement, DEMANDE_EXISTANTE, ENTREPRISE, NOUVELLE_DEMANDE

### Community 63 - "demande_modification_contrat"
Cohesion: 0.60
Nodes (3): demande_modification_contrat, idx_demande_modification_statut_date, uq_demande_modification_contrat_en_attente

### Community 64 - "20261012_add_affaire_individuelle.sql"
Cohesion: 0.60
Nodes (3): adresse_affaire_individuelle, affaire_individuelle, idx_adresse_affaire_individuelle_parent

### Community 66 - "scripts"
Cohesion: 0.40
Nodes (5): scripts, build, dev, lint, preview

### Community 67 - "useIdleLogout"
Cohesion: 0.70
Nodes (5): useIdleLogout(), effacerMinuteur(), enregistrerActivite(), programmerExpiration(), surChangementStockage()

### Community 68 - "tsconfig.json"
Cohesion: 0.40
Nodes (4): compilerOptions, paths, files, references

### Community 69 - "vite.config.ts"
Cohesion: 0.40
Nodes (3): @tailwindcss/vite, vite, @vitejs/plugin-react

### Community 70 - "MotifCloture"
Cohesion: 0.50
Nodes (3): MotifCloture, FIN_A_TERME, FIN_ANTICIPEE

### Community 71 - "RoleClient"
Cohesion: 0.50
Nodes (4): RoleClient, CO_TITULAIRE, GARANT, TITULAIRE

### Community 73 - "DemandesModificationPage"
Cohesion: 0.83
Nodes (4): DemandesModificationPage(), approuver(), charger(), refuser()

## Knowledge Gaps
- **46 isolated node(s):** `mg.cecam:bic-backend`, `DONNEE_FICTIVE`, `NON_RENSEIGNEE`, `RAPPORT_CRIF`, `@fontsource-variable/geist` (+41 more)
  These have ≤1 connection - possible missing edges. (Counts symbols only; 582 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **84 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `RapportService` connect `RapportService` to `ScoreService`, `.historiquePaiement`, `Contrat`, `lombok.RequiredArgsConstructor`, `bigdecimal`, `.toDetailContrat`, `.toScoreDto`, `.construire`?**
  _High betweenness centrality (0.047) - this node is a cross-community bridge._
- **What connects `mg.cecam:bic-backend`, `DONNEE_FICTIVE`, `NON_RENSEIGNEE` to the rest of the system?**
  _46 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `ScoreService` be split into smaller, more focused modules?**
  _Cohesion score 0.05707450444292549 - nodes in this community are weakly interconnected._
- **Why does `Contrat` connect `Contrat` to `Garantie`, `ContratController`, `ModificationContratDemande`, `ContratLien`, `lombok.RequiredArgsConstructor`, `.rechercher`, `bigdecimal`, `BienLeasing`, `HistoriquePaiementMensuel`, `Contrat.java`, `ContratService`, `Echeance`, `Client`?**
  _High betweenness centrality (0.029) - this node is a cross-community bridge._
- **Should `SecurityConfig.java` be split into smaller, more focused modules?**
  _Cohesion score 0.06184012066365008 - nodes in this community are weakly interconnected._
- **Why does `Client` connect `Client` to `AffaireIndividuelle`, `ContratLien`, `localdate`, `lombok.RequiredArgsConstructor`, `Adresse`, `ClientRequest`, `ClientService`, `Contrat.java`, `Emploi`, `ClientRepository`, `Contrat`?**
  _High betweenness centrality (0.022) - this node is a cross-community bridge._
- **Should `App.tsx` be split into smaller, more focused modules?**
  _Cohesion score 0.09371980676328502 - nodes in this community are weakly interconnected._