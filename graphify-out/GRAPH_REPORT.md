# Graph Report - BIC  (2026-10-12)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 1231 nodes · 2718 edges · 102 communities (42 shown, 60 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 106 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `e1c20852`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- JwtAuthenticationFilter.java
- App.tsx
- SecurityConfig.java
- Button
- rapport.ts
- ContratForm.tsx
- MiseAJourDemandePage.tsx
- RapportService
- mg.cecam.bic.common.enums.PhaseDemande
- bigdecimal
- localdatetime
- AffaireIndividuelle
- components.json
- ClientForm.tsx
- package.json
- compilerOptions
- Contrat
- Echeance
- dropdown-menu.tsx
- ModificationContratDemande
- RapportSolvabiliteResponse
- ScoreService
- ContratService
- list
- org.springframework.http.ResponseEntity
- dependencies
- compilerOptions
- lombok.RequiredArgsConstructor
- Client
- ContratService.java
- CalendrierService.java
- ContratController.java
- BucketRetard
- .toDetailContrat
- RapportController
- SaisieHistoriquePage.tsx
- ClientService
- ModificationContratResponse
- Emploi.java
- Emploi
- devDependencies
- ContratLien
- HistoriquePaiementMensuel
- EcheanceController
- JournalAudit
- Adresse
- BienLeasing
- Garantie
- AlerteService.java
- ContratRepository
- LabelMapper.java
- .financements
- ModificationContratService
- ClientRequest
- ClientRepository
- Identifiant
- CategorieTiers
- GrilleScore
- ClientController
- Genre
- PhaseDemande
- BicBackendApplication.java
- OrigineDonnee
- org.springframework.data.jpa.repository.JpaRepository
- .calculer
- demande_modification_contrat
- 20261012_add_affaire_individuelle.sql
- BicBackendApplicationTests.java
- scripts
- useIdleLogout
- tsconfig.json
- vite.config.ts
- NatureGarantie
- LigneFinancementDTO.java
- ScoreColorMapper
- PageShell.tsx
- approuver
- ImportLigneDTO.java
- ContactDTO.java
- GrilleScoreDTO.java
- IdentifiantDTO.java
- login
- mg.cecam:bic-backend

## God Nodes (most connected - your core abstractions)
1. `Contrat` - 76 edges
2. `RapportService` - 50 edges
3. `Client` - 43 edges
4. `Echeance` - 38 edges
5. `react` - 30 edges
6. `ScoreService` - 28 edges
7. `ContratService` - 26 edges
8. `RapportSolvabiliteResponse` - 25 edges
9. `Button()` - 22 edges
10. `ContratForm()` - 21 edges

## Surprising Connections (you probably didn't know these)
- `AuthController` --references--> `JwtUtil`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/auth/AuthController.java → bic-backend/src/main/java/mg/cecam/bic/auth/JwtUtil.java
- `AuthController` --references--> `UtilisateurRepository`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/auth/AuthController.java → bic-backend/src/main/java/mg/cecam/bic/auth/UtilisateurRepository.java
- `UtilisateurController` --references--> `UtilisateurRepository`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/auth/UtilisateurController.java → bic-backend/src/main/java/mg/cecam/bic/auth/UtilisateurRepository.java
- `AffaireIndividuelle` --references--> `Client`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/client/AffaireIndividuelle.java → bic-backend/src/main/java/mg/cecam/bic/client/Client.java
- `ClientEnregistrementResponse` --references--> `ClientResponse`  [EXTRACTED]
  bic-frontend/src/api/clientApi.ts → bic-frontend/src/types/client.ts

## Import Cycles
- None detected.

## Communities (102 total, 60 thin omitted)

### Community 0 - "JwtAuthenticationFilter.java"
Cohesion: 0.05
Nodes (9): AppUserDetailsService, JwtAuthenticationFilter, JwtUtil, Utilisateur, UtilisateurRepository, Role, ADMIN, AGENT_CREDIT (+1 more)

### Community 1 - "App.tsx"
Cohesion: 0.08
Nodes (32): authApi, LoginResponse, axiosClient, App(), AppShell(), pageTransition, RapportPageRoute(), AdminRoute() (+24 more)

### Community 2 - "SecurityConfig.java"
Cohesion: 0.07
Nodes (5): AuthController, LoginRequest, LoginResponse, GlobalExceptionHandler, SecurityConfig

### Community 3 - "Button"
Cohesion: 0.14
Nodes (31): Button(), Card(), CardContent(), CardHeader(), CardTitle(), Input(), Label(), SelectItem() (+23 more)

### Community 4 - "rapport.ts"
Cohesion: 0.07
Nodes (29): rapportApi, ScoreCardProps, ProblemDetail, Adresse, AffaireIndividuelle, Alerte, BienLeasing, CalendrierCredit (+21 more)

### Community 5 - "ContratForm.tsx"
Cohesion: 0.09
Nodes (24): buttonVariants, affichageVersIso(), DateInput, DateInputProps, isoVersAffichage(), formatAffichage(), MontantInput, MontantInputProps (+16 more)

### Community 6 - "MiseAJourDemandePage.tsx"
Cohesion: 0.12
Nodes (19): contratApi, modificationContratApi, ToastContext, ToastContextValue, ToastItem, PHASES, DEVISES, PHASE_LABELS (+11 more)

### Community 8 - "mg.cecam.bic.common.enums.PhaseDemande"
Cohesion: 0.15
Nodes (9): PhaseRequest, ContratRequest, BienLeasingHistoriqueRequest, ClientLieHistoriqueRequest, EcheanceHistoriqueRequest, GarantieHistoriqueRequest, HistoriqueContratRequest, HistoriquePaiementMensuelRequest (+1 more)

### Community 9 - "bigdecimal"
Cohesion: 0.11
Nodes (6): BienLeasingDTO, ConsultationDTO, EmploiDTO, GarantieDTO, SituationFinanciereMensuelleDTO, SyntheseCategorieDTO

### Community 12 - "components.json"
Cohesion: 0.09
Nodes (21): aliases, components, hooks, lib, ui, utils, iconLibrary, menuAccent (+13 more)

### Community 13 - "ClientForm.tsx"
Cohesion: 0.13
Nodes (18): clientApi, ClientEnregistrementResponse, adresseSchema, affaireIndividuelleSchema, ClientFormInput, ClientFormValues, clientSchema, emploiSchema (+10 more)

### Community 14 - "package.json"
Cohesion: 0.12
Nodes (19): name, private, type, version, class-variance-authority, eslint, @eslint/js, eslint-plugin-react-hooks (+11 more)

### Community 15 - "compilerOptions"
Cohesion: 0.10
Nodes (20): compilerOptions, allowArbitraryExtensions, allowImportingTsExtensions, erasableSyntaxOnly, jsx, lib, module, moduleDetection (+12 more)

### Community 18 - "dropdown-menu.tsx"
Cohesion: 0.19
Nodes (10): AppBreadcrumb(), segmentsForPath(), DropdownMenu(), DropdownMenuContent(), DropdownMenuItem(), DropdownMenuSub(), DropdownMenuSubContent(), DropdownMenuSubTrigger() (+2 more)

### Community 19 - "ModificationContratDemande"
Cohesion: 0.14
Nodes (6): StatutDemandeModification, APPROUVEE, EN_ATTENTE, REFUSEE, ModificationContratDemande, ModificationContratDemandeRepository

### Community 20 - "RapportSolvabiliteResponse"
Cohesion: 0.11
Nodes (4): ClientInfoDTO, DetailDemandeDTO, RapportSolvabiliteResponse, ScoreDTO

### Community 23 - "list"
Cohesion: 0.13
Nodes (7): ImportResultatDTO, AffaireIndividuelleDTO, CalendrierCreditDTO, EncoursCategorieDTO, LigneAnneeDTO, RepartitionLigneDTO, SyntheseDTO

### Community 24 - "org.springframework.http.ResponseEntity"
Cohesion: 0.20
Nodes (3): CodesEtablissementRequest, ContratController, EcheanceMensuelleRequest

### Community 25 - "dependencies"
Cohesion: 0.12
Nodes (17): dependencies, axios, @base-ui/react, class-variance-authority, cn, @fontsource-variable/geist, @hookform/resolvers, lucide-react (+9 more)

### Community 26 - "compilerOptions"
Cohesion: 0.12
Nodes (16): compilerOptions, allowImportingTsExtensions, erasableSyntaxOnly, lib, module, moduleDetection, noEmit, noFallthroughCasesInSwitch (+8 more)

### Community 27 - "lombok.RequiredArgsConstructor"
Cohesion: 0.29
Nodes (3): AuditController, UtilisateurController, AlerteController

### Community 29 - "ContratService.java"
Cohesion: 0.18
Nodes (4): ModeRattachement, DEMANDE_EXISTANTE, ENTREPRISE, NOUVELLE_DEMANDE

### Community 31 - "ContratController.java"
Cohesion: 0.18
Nodes (5): StatutEcheance, A_VENIR, EN_RETARD, IMPAYE, PAYE_A_TEMPS

### Community 32 - "BucketRetard"
Cohesion: 0.14
Nodes (9): BucketRetard, A_JOUR, JOURS_120_149, JOURS_150_179, JOURS_180_PLUS, JOURS_1_29, JOURS_30_59, JOURS_60_89 (+1 more)

### Community 36 - "SaisieHistoriquePage.tsx"
Cohesion: 0.18
Nodes (12): ChampsObligatoiresNote(), ClientLieLigne, defaults, EcheanceLigne, FormValues, GarantieLigne, MoisLigne, optionalDate() (+4 more)

### Community 38 - "ModificationContratResponse"
Cohesion: 0.25
Nodes (3): ModificationContratResponse, ModificationContratController, RefusRequest

### Community 41 - "devDependencies"
Cohesion: 0.15
Nodes (13): devDependencies, eslint, @eslint/js, eslint-plugin-react-hooks, eslint-plugin-react-refresh, globals, @types/node, @types/react (+5 more)

### Community 44 - "EcheanceController"
Cohesion: 0.18
Nodes (3): AuditService, PaiementRequest, EcheanceController

### Community 51 - "LabelMapper.java"
Cohesion: 0.20
Nodes (7): MotifCloture, FIN_A_TERME, FIN_ANTICIPEE, RoleClient, CO_TITULAIRE, GARANT, TITULAIRE

### Community 54 - "ClientRequest"
Cohesion: 0.29
Nodes (6): AdresseAffaireIndividuelleRequest, AdresseRequest, AffaireIndividuelleRequest, ClientRequest, EmploiRequest, IdentifiantRequest

### Community 60 - "Genre"
Cohesion: 0.33
Nodes (4): ClientResponse, Genre, FEMME, HOMME

### Community 61 - "PhaseDemande"
Cohesion: 0.29
Nodes (6): PhaseDemande, ABANDONNE, ACTIF, DEMANDE_EN_COURS, FERME, REFUSE

### Community 63 - "OrigineDonnee"
Cohesion: 0.40
Nodes (4): OrigineDonnee, DONNEE_FICTIVE, NON_RENSEIGNEE, RAPPORT_CRIF

### Community 65 - ".calculer"
Cohesion: 0.70
Nodes (3): Cle, SituationFinanciereMensuelleService, Totaux

### Community 66 - "demande_modification_contrat"
Cohesion: 0.60
Nodes (3): demande_modification_contrat, idx_demande_modification_statut_date, uq_demande_modification_contrat_en_attente

### Community 67 - "20261012_add_affaire_individuelle.sql"
Cohesion: 0.60
Nodes (3): adresse_affaire_individuelle, affaire_individuelle, idx_adresse_affaire_individuelle_parent

### Community 69 - "scripts"
Cohesion: 0.40
Nodes (5): scripts, build, dev, lint, preview

### Community 70 - "useIdleLogout"
Cohesion: 0.70
Nodes (5): useIdleLogout(), effacerMinuteur(), enregistrerActivite(), programmerExpiration(), surChangementStockage()

### Community 71 - "tsconfig.json"
Cohesion: 0.40
Nodes (4): compilerOptions, paths, files, references

### Community 72 - "vite.config.ts"
Cohesion: 0.40
Nodes (3): @tailwindcss/vite, vite, @vitejs/plugin-react

### Community 73 - "NatureGarantie"
Cohesion: 0.50
Nodes (3): NatureGarantie, PERSONNELLE, REELLE

### Community 77 - "approuver"
Cohesion: 0.67
Nodes (3): approuver(), charger(), refuser()

## Knowledge Gaps
- **46 isolated node(s):** `ADMIN`, `AGENT_CREDIT`, `mg.cecam:bic-backend`, `@fontsource-variable/geist`, `shadcn` (+41 more)
  These have ≤1 connection - possible missing edges. (Counts symbols only; 526 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **60 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Contrat` connect `Contrat` to `RapportService`, `mg.cecam.bic.common.enums.PhaseDemande`, `bigdecimal`, `Echeance`, `ModificationContratDemande`, `ScoreService`, `ContratService`, `org.springframework.http.ResponseEntity`, `Client`, `CalendrierService.java`, `.toDetailContrat`, `RapportService.java`, `ContratLien`, `HistoriquePaiementMensuel`, `BienLeasing`, `Garantie`, `AlerteService.java`, `ContratRepository`, `.financements`, `ModificationContratService`, `OrigineDonnee`, `.calculer`?**
  _High betweenness centrality (0.079) - this node is a cross-community bridge._
- **What connects `ADMIN`, `AGENT_CREDIT`, `mg.cecam:bic-backend` to the rest of the system?**
  _46 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `JwtAuthenticationFilter.java` be split into smaller, more focused modules?**
  _Cohesion score 0.05075187969924812 - nodes in this community are weakly interconnected._
- **Why does `Client` connect `Client` to `RapportService.java`, `ClientService`, `RapportService`, `Emploi`, `bigdecimal`, `localdatetime`, `AffaireIndividuelle`, `mg.cecam.bic.common.enums.PhaseDemande`, `ContratLien`, `Adresse`, `Contrat`, `AlerteService.java`, `.financements`, `ScoreService`, `ClientRequest`, `ClientRepository`, `ContratService.java`, `OrigineDonnee`?**
  _High betweenness centrality (0.047) - this node is a cross-community bridge._
- **Should `App.tsx` be split into smaller, more focused modules?**
  _Cohesion score 0.083710407239819 - nodes in this community are weakly interconnected._
- **Why does `RapportService` connect `RapportService` to `.toDetailContrat`, `RapportController`, `RapportService.java`, `.calculer`, `mg.cecam.bic.common.enums.PhaseDemande`, `ContratLien`, `HistoriquePaiementMensuel`, `AlerteService.java`, `ContratRepository`, `Echeance`, `.financements`, `ScoreService`, `lombok.RequiredArgsConstructor`, `ContratService.java`, `CalendrierService.java`?**
  _High betweenness centrality (0.028) - this node is a cross-community bridge._
- **Should `SecurityConfig.java` be split into smaller, more focused modules?**
  _Cohesion score 0.06648936170212766 - nodes in this community are weakly interconnected._