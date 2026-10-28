# Graph Report - BIC  (2026-10-27)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 1486 nodes · 3604 edges · 136 communities (49 shown, 87 thin omitted)
- Extraction: 95% EXTRACTED · 5% INFERRED · 0% AMBIGUOUS · INFERRED: 194 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `962b4a44`
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
- SyntheseCategorieDTO
- SyntheseDTO
- AdresseDTO
- AffaireIndividuelleDTO
- mg.cecam:bic-backend

## God Nodes (most connected - your core abstractions)
1. `Contrat` - 90 edges
2. `RapportService` - 60 edges
3. `ScoreService` - 42 edges
4. `Echeance` - 37 edges
5. `Client` - 36 edges
6. `ContratService` - 35 edges
7. `ScoreServiceBehaviorTest` - 33 edges
8. `react` - 30 edges
9. `Button()` - 28 edges
10. `ClientForm()` - 26 edges

## Surprising Connections (you probably didn't know these)
- `CalendrierCreditDTO` --references--> `LigneAnneeDTO`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/rapport/dto/CalendrierCreditDTO.java → bic-backend/src/main/java/mg/cecam/bic/rapport/dto/LigneAnneeDTO.java
- `RapportService` --references--> `CalendrierService`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/rapport/RapportService.java → bic-backend/src/main/java/mg/cecam/bic/rapport/CalendrierService.java
- `LigneAnneeDTO` --references--> `CelluleMoisDTO`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/rapport/dto/LigneAnneeDTO.java → bic-backend/src/main/java/mg/cecam/bic/rapport/dto/CelluleMoisDTO.java
- `ModificationContratResponse` --references--> `StatutDemandeModification`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/contrat/dto/ModificationContratResponse.java → bic-backend/src/main/java/mg/cecam/bic/common/enums/StatutDemandeModification.java
- `ModificationContratService` --references--> `ContratRepository`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/contrat/ModificationContratService.java → bic-backend/src/main/java/mg/cecam/bic/contrat/ContratRepository.java

## Import Cycles
- None detected.

## Communities (136 total, 87 thin omitted)

### Community 0 - "ScoreService"
Cohesion: 0.07
Nodes (14): BucketRetard, A_JOUR, JOURS_120_149, JOURS_150_179, JOURS_180_PLUS, JOURS_1_29, JOURS_30_59, JOURS_60_89 (+6 more)

### Community 1 - "SecurityConfig.java"
Cohesion: 0.08
Nodes (36): authApi, App(), AppShell(), pageTransition, RapportPageRoute(), AdminRoute(), ProtectedRoute(), HomeMenu() (+28 more)

### Community 2 - "App.tsx"
Cohesion: 0.13
Nodes (32): utilisateurApi, UtilisateurRequest, UtilisateurResponse, Button(), Card(), CardContent(), CardDescription(), CardHeader() (+24 more)

### Community 3 - "ModificationContratDemande"
Cohesion: 0.11
Nodes (28): ChampsObligatoiresNote(), affichageVersIso(), DateInput, DateInputProps, isoVersAffichage(), Input(), formatAffichage(), MontantInput (+20 more)

### Community 4 - "JwtAuthenticationFilter.java"
Cohesion: 0.12
Nodes (27): LoginResponse, axiosClient, rapportApi, buttonVariants, SelectContent(), SelectItem(), SelectScrollDownButton(), SelectScrollUpButton() (+19 more)

### Community 5 - "ContratForm.tsx"
Cohesion: 0.14
Nodes (5): UtilisateurController, ClientController, CodesEtablissementRequest, ContratController, EcheanceMensuelleRequest

### Community 6 - "RapportSolvabiliteResponse"
Cohesion: 0.13
Nodes (4): Echeance, EncoursCategorieDTO, SyntheseCategorieDTO, RapportService

### Community 7 - "MiseAJourDemandePage.tsx"
Cohesion: 0.11
Nodes (9): AdresseDTO, AlerteDTO, ClientInfoDTO, ConsultationDTO, ContactDTO, EmploiDTO, IdentifiantDTO, RapportSolvabiliteResponse (+1 more)

### Community 8 - "JournalAudit"
Cohesion: 0.15
Nodes (6): ScoreResult, Capacite, Observation, Plafond, RevenuEvaluation, ScoreService

### Community 9 - "rapport.ts"
Cohesion: 0.12
Nodes (3): Contrat, ContratRepository, DernierContrat

### Community 10 - "localdate"
Cohesion: 0.14
Nodes (21): contratApi, modificationContratApi, DEVISES, PHASE_LABELS, PHASES, BienLeasingDetailResponse, ClientLieDetailResponse, ContratRequest (+13 more)

### Community 11 - "lombok.RequiredArgsConstructor"
Cohesion: 0.08
Nodes (25): Adresse, AffaireIndividuelle, Alerte, BienLeasing, CalendrierCredit, CelluleMois, ClientInfo, Contact (+17 more)

### Community 13 - "org.springframework.http.ResponseEntity"
Cohesion: 0.18
Nodes (4): ModificationContratResponse, ModificationContratController, RefusRequest, ModificationContratService

### Community 18 - "ImportPage.tsx"
Cohesion: 0.11
Nodes (21): name, private, type, version, class-variance-authority, eslint, @eslint/js, eslint-plugin-react-hooks (+13 more)

### Community 20 - "ContratService"
Cohesion: 0.09
Nodes (21): aliases, components, hooks, lib, ui, utils, iconLibrary, menuAccent (+13 more)

### Community 22 - "compilerOptions"
Cohesion: 0.10
Nodes (20): compilerOptions, allowArbitraryExtensions, allowImportingTsExtensions, erasableSyntaxOnly, jsx, lib, module, moduleDetection (+12 more)

### Community 26 - "Echeance"
Cohesion: 0.19
Nodes (12): BienLeasingDetailResponse, ClientLieDetailResponse, EcheanceDetailResponse, GarantieDetailResponse, HistoriqueContratDetailResponse, MoisDetailResponse, BienLeasingHistoriqueRequest, ClientLieHistoriqueRequest (+4 more)

### Community 27 - "SaisieHistoriquePage.tsx"
Cohesion: 0.10
Nodes (18): adresseSchema, affaireIndividuelleAdresseSchema, affaireIndividuelleContactSchema, affaireIndividuelleIdentifiantSchema, affaireIndividuelleSchema, ClientFormInput, ClientFormValues, clientSchema (+10 more)

### Community 28 - ".construire"
Cohesion: 0.20
Nodes (9): AppBreadcrumb(), segmentsForPath(), DropdownMenu(), DropdownMenuContent(), DropdownMenuItem(), DropdownMenuSub(), DropdownMenuSubContent(), DropdownMenuSubTrigger() (+1 more)

### Community 29 - "dependencies"
Cohesion: 0.14
Nodes (14): Champ(), ClientLieLigne, defaults, EcheanceLigne, FormValues, GarantieLigne, MoisLigne, optionalDate() (+6 more)

### Community 31 - "Client"
Cohesion: 0.11
Nodes (18): dependencies, axios, @base-ui/react, class-variance-authority, cn, @fontsource-variable/geist, @hookform/resolvers, lucide-react (+10 more)

### Community 32 - "EcheanceController.java"
Cohesion: 0.17
Nodes (15): clientApi, ClientEnregistrementResponse, PageResponse, AdresseRequest, AffaireIndividuelleAdresseRequest, AffaireIndividuelleContactRequest, AffaireIndividuelleIdentifiantRequest, AffaireIndividuelleRequest (+7 more)

### Community 33 - "list"
Cohesion: 0.12
Nodes (11): PhaseDemande, ABANDONNE, ACTIF, DEMANDE_EN_COURS, FERME, REFUSE, RoleClient, CO_TITULAIRE (+3 more)

### Community 35 - "Garantie"
Cohesion: 0.12
Nodes (16): compilerOptions, allowImportingTsExtensions, erasableSyntaxOnly, lib, module, moduleDetection, noEmit, noFallthroughCasesInSwitch (+8 more)

### Community 39 - "AffaireIndividuelle"
Cohesion: 0.14
Nodes (5): ScoreColorMapper, DernierContratDTO, DetailDemandeDTO, GrilleScoreDTO, ScoreDTO

### Community 40 - "Utilisateur"
Cohesion: 0.20
Nodes (4): UtilisateurRepository, EmploiRepository, EcheanceRepository, GrilleScoreRepository

### Community 42 - "clientSchema.ts"
Cohesion: 0.16
Nodes (6): DetailContratDTO, GarantieDTO, HistoriquePaiementLigneDTO, LienClientDTO, LigneAnneeDTO, LigneFinancementDTO

### Community 45 - "ClientRequest"
Cohesion: 0.18
Nodes (3): AuditService, JournalAudit, JournalAuditRepository

### Community 47 - "ContratRepository"
Cohesion: 0.44
Nodes (5): AppProperties, Cors, Jwt, TauxMensuelProperties, ScoreProperties

### Community 49 - "org.springframework.data.jpa.repository.JpaRepository"
Cohesion: 0.15
Nodes (13): devDependencies, eslint, @eslint/js, eslint-plugin-react-hooks, eslint-plugin-react-refresh, globals, @types/node, @types/react (+5 more)

### Community 52 - ".toScoreDto"
Cohesion: 0.17
Nodes (4): Utilisateur, Role, ADMIN, AGENT_CREDIT

### Community 66 - "scripts"
Cohesion: 0.28
Nodes (3): ContratRequest, DernierContratRequest, RevenuDemandeRequest

### Community 69 - "vite.config.ts"
Cohesion: 0.39
Nodes (6): GestionUtilisateursPage(), chargerUtilisateurs(), fermerFormulaire(), handleSubmit(), handleSupprimer(), handleToggleActif()

### Community 70 - "MotifCloture"
Cohesion: 0.38
Nodes (6): AdresseAffaireIndividuelleRequest, AdresseRequest, AffaireIndividuelleRequest, ClientRequest, EmploiRequest, IdentifiantRequest

### Community 73 - "DemandesModificationPage"
Cohesion: 0.33
Nodes (5): StatutEcheance, A_VENIR, EN_RETARD, IMPAYE, PAYE_A_TEMPS

### Community 75 - "ImportLigneDTO.java"
Cohesion: 0.40
Nodes (4): ModeRattachement, DEMANDE_EXISTANTE, ENTREPRISE, NOUVELLE_DEMANDE

### Community 76 - ".rechercher"
Cohesion: 0.40
Nodes (3): NatureGarantie, PERSONNELLE, REELLE

### Community 77 - ".enregistrerPaiement"
Cohesion: 0.40
Nodes (4): OrigineDonnee, DONNEE_FICTIVE, NON_RENSEIGNEE, RAPPORT_CRIF

### Community 78 - "ContactDTO.java"
Cohesion: 0.40
Nodes (4): StatutDemandeModification, APPROUVEE, EN_ATTENTE, REFUSEE

### Community 79 - "GrilleScoreDTO.java"
Cohesion: 0.70
Nodes (3): Cle, SituationFinanciereMensuelleService, Totaux

### Community 80 - "RepartitionLigneDTO.java"
Cohesion: 0.40
Nodes (5): StatutDernierContrat, A_TEMPS, ANTICIPATION, EN_RETARD, NON_REMBOURSE

### Community 81 - "ScoreDTO.java"
Cohesion: 0.60
Nodes (3): demande_modification_contrat, idx_demande_modification_statut_date, uq_demande_modification_contrat_en_attente

### Community 82 - "client"
Cohesion: 0.60
Nodes (3): adresse_affaire_individuelle, affaire_individuelle, idx_adresse_affaire_individuelle_parent

### Community 83 - "login"
Cohesion: 0.40
Nodes (5): scripts, build, dev, lint, preview

### Community 85 - "enums/CategorieTiers.java"
Cohesion: 0.40
Nodes (4): compilerOptions, paths, files, references

### Community 86 - "Transient"
Cohesion: 0.40
Nodes (3): @tailwindcss/vite, vite, @vitejs/plugin-react

### Community 87 - "PatchMapping"
Cohesion: 0.50
Nodes (3): Genre, FEMME, HOMME

### Community 88 - "PutMapping"
Cohesion: 0.50
Nodes (3): MotifCloture, FIN_A_TERME, FIN_ANTICIPEE

### Community 90 - "SyntheseCategorieDTO"
Cohesion: 0.67
Nodes (3): approuver(), charger(), refuser()

## Knowledge Gaps
- **50 isolated node(s):** `A_JOUR`, `JOURS_120_149`, `JOURS_150_179`, `JOURS_180_PLUS`, `JOURS_1_29` (+45 more)
  These have ≤1 connection - possible missing edges. (Counts symbols only; 548 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **87 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Contrat` connect `rapport.ts` to `ContratForm.tsx`, `RapportSolvabiliteResponse`, `MiseAJourDemandePage.tsx`, `JournalAudit`, `GestionUtilisateursPage.tsx`, `org.springframework.http.ResponseEntity`, `bigdecimal`, `Contrat`, `dropdown-menu.tsx`, `Echeance`, `list`, `.historiquePaiement`, `Contrat`, `AffaireIndividuelle`, `ContratLien`, `clientSchema.ts`, `HistoriquePaiementMensuel`, `Emploi`, `ClientRepository`, `CategorieTiers`, `PhaseDemande`, `DetailContratDTO`, `20261012_add_affaire_individuelle.sql`, `scripts`, `useIdleLogout`, `apiError.ts`, `GrilleScoreDTO.java`?**
  _High betweenness centrality (0.076) - this node is a cross-community bridge._
- **What connects `A_JOUR`, `JOURS_120_149`, `JOURS_150_179` to the rest of the system?**
  _50 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `ScoreService` be split into smaller, more focused modules?**
  _Cohesion score 0.07077922077922078 - nodes in this community are weakly interconnected._
- **Why does `Client` connect `components.json` to `Genre`, `ContratForm.tsx`, `Adresse`, `ClientService`, `.toDetailContrat`, `ClientRepository`, `GrilleScore`, `GestionUtilisateursPage`, `Echeance`, `compilerOptions`, `demande_modification_contrat`?**
  _High betweenness centrality (0.039) - this node is a cross-community bridge._
- **Should `SecurityConfig.java` be split into smaller, more focused modules?**
  _Cohesion score 0.0784313725490196 - nodes in this community are weakly interconnected._
- **Why does `RapportService` connect `RapportSolvabiliteResponse` to `ScoreService`, `MiseAJourDemandePage.tsx`, `rapport.ts`, `bigdecimal`, `package.json`, `dropdown-menu.tsx`, `Contrat`, `AffaireIndividuelle`, `Utilisateur`, `ContratLien`, `clientSchema.ts`, `HistoriquePaiementMensuel`, `Emploi`, `ClientRepository`, `PhaseDemande`, `ModeRattachement`, `20261012_add_affaire_individuelle.sql`, `BicBackendApplicationTests.java`, `RoleClient`, `apiError.ts`, `GrilleScoreDTO.java`?**
  _High betweenness centrality (0.025) - this node is a cross-community bridge._
- **Should `App.tsx` be split into smaller, more focused modules?**
  _Cohesion score 0.1347517730496454 - nodes in this community are weakly interconnected._