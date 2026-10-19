# Graph Report - BIC  (2026-10-19)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 1373 nodes · 3087 edges · 127 communities (50 shown, 77 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 133 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `d2a39088`
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
- vite.config.ts
- MotifCloture
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
- 20261009_add_data_provenance.sql

## God Nodes (most connected - your core abstractions)
1. `Contrat` - 76 edges
2. `RapportService` - 55 edges
3. `ScoreService` - 36 edges
4. `Echeance` - 34 edges
5. `Client` - 32 edges
6. `react` - 30 edges
7. `ContratService` - 29 edges
8. `Button()` - 26 edges
9. `Input()` - 23 edges
10. `GestionUtilisateursPage()` - 23 edges

## Surprising Connections (you probably didn't know these)
- `RapportService` --references--> `CalendrierService`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/rapport/RapportService.java → bic-backend/src/main/java/mg/cecam/bic/rapport/CalendrierService.java
- `LigneAnneeDTO` --references--> `CelluleMoisDTO`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/rapport/dto/LigneAnneeDTO.java → bic-backend/src/main/java/mg/cecam/bic/rapport/dto/CelluleMoisDTO.java
- `JwtAuthenticationFilter` --references--> `AppUserDetailsService`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/auth/JwtAuthenticationFilter.java → bic-backend/src/main/java/mg/cecam/bic/auth/AppUserDetailsService.java
- `AuthController` --references--> `JwtUtil`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/auth/AuthController.java → bic-backend/src/main/java/mg/cecam/bic/auth/JwtUtil.java
- `ScoreService` --references--> `ScoreProperties`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/score/ScoreService.java → bic-backend/src/main/java/mg/cecam/bic/score/ScoreProperties.java

## Import Cycles
- None detected.

## Communities (127 total, 77 thin omitted)

### Community 0 - "ScoreService"
Cohesion: 0.05
Nodes (20): BucketRetard, A_JOUR, JOURS_120_149, JOURS_150_179, JOURS_180_PLUS, JOURS_1_29, JOURS_30_59, JOURS_60_89 (+12 more)

### Community 1 - "SecurityConfig.java"
Cohesion: 0.06
Nodes (4): JwtAuthenticationFilter, JwtUtil, SecurityConfig, ScoreProperties

### Community 2 - "App.tsx"
Cohesion: 0.16
Nodes (25): utilisateurApi, UtilisateurRequest, UtilisateurResponse, Button(), buttonVariants, Card(), CardContent(), CardDescription() (+17 more)

### Community 3 - "ModificationContratDemande"
Cohesion: 0.10
Nodes (27): App(), AppShell(), pageTransition, RapportPageRoute(), AdminRoute(), ProtectedRoute(), HomeMenu(), Tache (+19 more)

### Community 4 - "JwtAuthenticationFilter.java"
Cohesion: 0.06
Nodes (30): rapportApi, ProblemDetail, RapportPage(), telechargerEtVisualiserPdf(), Adresse, AffaireIndividuelle, Alerte, BienLeasing (+22 more)

### Community 5 - "ContratForm.tsx"
Cohesion: 0.14
Nodes (6): DetailDemandeDTO, GarantieDTO, LienClientDTO, LigneFinancementDTO, RepartitionLigneDTO, RapportService

### Community 6 - "RapportSolvabiliteResponse"
Cohesion: 0.16
Nodes (3): UtilisateurRequest, UtilisateurResponse, UtilisateurController

### Community 7 - "MiseAJourDemandePage.tsx"
Cohesion: 0.14
Nodes (19): authApi, LoginResponse, axiosClient, SelectContent(), SelectItem(), SelectScrollDownButton(), SelectScrollUpButton(), SelectTrigger() (+11 more)

### Community 8 - "JournalAudit"
Cohesion: 0.16
Nodes (19): contratApi, modificationContratApi, RadioGroup(), RadioGroupItem(), today, DEVISES, PHASE_LABELS, PHASES (+11 more)

### Community 10 - "localdate"
Cohesion: 0.18
Nodes (4): ModificationContratResponse, ModificationContratController, RefusRequest, ModificationContratService

### Community 11 - "lombok.RequiredArgsConstructor"
Cohesion: 0.16
Nodes (4): ScoreResult, Observation, RevenuEvaluation, ScoreService

### Community 12 - "GestionUtilisateursPage.tsx"
Cohesion: 0.17
Nodes (17): ChampsObligatoiresNote(), affichageVersIso(), DateInput, DateInputProps, isoVersAffichage(), Input(), formatAffichage(), MontantInput (+9 more)

### Community 17 - "Contrat.java"
Cohesion: 0.09
Nodes (21): aliases, components, hooks, lib, ui, utils, iconLibrary, menuAccent (+13 more)

### Community 18 - "ImportPage.tsx"
Cohesion: 0.11
Nodes (20): name, private, type, version, class-variance-authority, eslint, @eslint/js, eslint-plugin-react-hooks (+12 more)

### Community 19 - "components.json"
Cohesion: 0.10
Nodes (20): compilerOptions, allowArbitraryExtensions, allowImportingTsExtensions, erasableSyntaxOnly, jsx, lib, module, moduleDetection (+12 more)

### Community 21 - "package.json"
Cohesion: 0.11
Nodes (3): ScoreColorMapper, GrilleScoreDTO, ScoreDTO

### Community 22 - "compilerOptions"
Cohesion: 0.15
Nodes (16): clientApi, ClientEnregistrementResponse, ToastContext, ToastContextValue, ToastItem, analyserCsv(), clientDepuisCsv(), CsvRow (+8 more)

### Community 23 - "RapportService"
Cohesion: 0.14
Nodes (6): StatutDemandeModification, APPROUVEE, EN_ATTENTE, REFUSEE, ModificationContratDemande, ModificationContratDemandeRepository

### Community 24 - "Contrat"
Cohesion: 0.20
Nodes (9): AppBreadcrumb(), segmentsForPath(), DropdownMenu(), DropdownMenuContent(), DropdownMenuItem(), DropdownMenuSub(), DropdownMenuSubContent(), DropdownMenuSubTrigger() (+1 more)

### Community 25 - "dropdown-menu.tsx"
Cohesion: 0.11
Nodes (17): adresseSchema, affaireIndividuelleAdresseSchema, affaireIndividuelleContactSchema, affaireIndividuelleIdentifiantSchema, affaireIndividuelleSchema, ClientFormInput, ClientFormValues, clientSchema (+9 more)

### Community 27 - "SaisieHistoriquePage.tsx"
Cohesion: 0.16
Nodes (3): PaiementRequest, BienLeasingDTO, ConsultationDTO

### Community 29 - "dependencies"
Cohesion: 0.11
Nodes (18): dependencies, axios, @base-ui/react, class-variance-authority, cn, @fontsource-variable/geist, @hookform/resolvers, lucide-react (+10 more)

### Community 32 - "EcheanceController.java"
Cohesion: 0.12
Nodes (16): compilerOptions, allowImportingTsExtensions, erasableSyntaxOnly, lib, module, moduleDetection, noEmit, noFallthroughCasesInSwitch (+8 more)

### Community 33 - "list"
Cohesion: 0.16
Nodes (3): AuthController, LoginRequest, LoginResponse

### Community 34 - ".historiquePaiement"
Cohesion: 0.20
Nodes (5): UtilisateurRepository, EmploiRepository, EcheanceRepository, HistoriquePaiementMensuelRepository, GrilleScoreRepository

### Community 36 - "ContratController"
Cohesion: 0.14
Nodes (5): NatureGarantie, PERSONNELLE, REELLE, Garantie, GarantieRepository

### Community 38 - "devDependencies"
Cohesion: 0.17
Nodes (12): ClientLieLigne, defaults, EcheanceLigne, FormValues, GarantieLigne, MoisLigne, optionalDate(), optionalNumber() (+4 more)

### Community 39 - "AffaireIndividuelle"
Cohesion: 0.27
Nodes (3): AuditController, AlerteController, AlerteDTO

### Community 40 - "Utilisateur"
Cohesion: 0.14
Nodes (4): RoleClient, CO_TITULAIRE, GARANT, TITULAIRE

### Community 41 - "ContratLien"
Cohesion: 0.20
Nodes (3): CodesEtablissementRequest, ContratController, EcheanceMensuelleRequest

### Community 44 - "AdresseAffaireIndividuelle"
Cohesion: 0.19
Nodes (4): ClientInfoDTO, ContactDTO, EmploiDTO, IdentifiantDTO

### Community 45 - "ClientRequest"
Cohesion: 0.15
Nodes (13): devDependencies, eslint, @eslint/js, eslint-plugin-react-hooks, eslint-plugin-react-refresh, globals, @types/node, @types/react (+5 more)

### Community 46 - "BienLeasing"
Cohesion: 0.17
Nodes (4): Utilisateur, Role, ADMIN, AGENT_CREDIT

### Community 47 - "ContratRepository"
Cohesion: 0.18
Nodes (4): ImportResultatDTO, AffaireIndividuelleDTO, CalendrierCreditDTO, LigneAnneeDTO

### Community 49 - "org.springframework.data.jpa.repository.JpaRepository"
Cohesion: 0.17
Nodes (10): OrigineDonnee, DONNEE_FICTIVE, NON_RENSEIGNEE, RAPPORT_CRIF, BienLeasingHistoriqueRequest, ClientLieHistoriqueRequest, EcheanceHistoriqueRequest, GarantieHistoriqueRequest (+2 more)

### Community 56 - "GrilleScore"
Cohesion: 0.18
Nodes (10): AdresseRequest, AffaireIndividuelleAdresseRequest, AffaireIndividuelleContactRequest, AffaireIndividuelleIdentifiantRequest, AffaireIndividuelleRequest, ContactRequest, DocumentIdentificationRequest, EmploiRequest (+2 more)

### Community 59 - "PhaseDemande"
Cohesion: 0.25
Nodes (6): AdresseAffaireIndividuelleRequest, AdresseRequest, AffaireIndividuelleRequest, ClientRequest, EmploiRequest, IdentifiantRequest

### Community 62 - "ModeRattachement"
Cohesion: 0.33
Nodes (4): SituationFinanciereMensuelleDTO, Cle, SituationFinanciereMensuelleService, Totaux

### Community 66 - "scripts"
Cohesion: 0.39
Nodes (6): GestionUtilisateursPage(), chargerUtilisateurs(), fermerFormulaire(), handleSubmit(), handleSupprimer(), handleToggleActif()

### Community 68 - "tsconfig.json"
Cohesion: 0.29
Nodes (6): PhaseDemande, ABANDONNE, ACTIF, DEMANDE_EN_COURS, FERME, REFUSE

### Community 69 - "vite.config.ts"
Cohesion: 0.40
Nodes (4): ClientResponse, Genre, FEMME, HOMME

### Community 73 - "DemandesModificationPage"
Cohesion: 0.40
Nodes (4): ModeRattachement, DEMANDE_EXISTANTE, ENTREPRISE, NOUVELLE_DEMANDE

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
Cohesion: 0.70
Nodes (5): useIdleLogout(), effacerMinuteur(), enregistrerActivite(), programmerExpiration(), surChangementStockage()

### Community 78 - "ContactDTO.java"
Cohesion: 0.40
Nodes (4): compilerOptions, paths, files, references

### Community 79 - "GrilleScoreDTO.java"
Cohesion: 0.40
Nodes (3): @tailwindcss/vite, vite, @vitejs/plugin-react

### Community 80 - "RepartitionLigneDTO.java"
Cohesion: 0.50
Nodes (3): MotifCloture, FIN_A_TERME, FIN_ANTICIPEE

### Community 81 - "ScoreDTO.java"
Cohesion: 0.67
Nodes (3): approuver(), charger(), refuser()

## Knowledge Gaps
- **46 isolated node(s):** `A_JOUR`, `JOURS_120_149`, `JOURS_150_179`, `JOURS_180_PLUS`, `JOURS_1_29` (+41 more)
  These have ≤1 connection - possible missing edges. (Counts symbols only; 565 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **77 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Contrat` connect `ClientService` to `ScoreService`, `ContratForm.tsx`, `localdate`, `lombok.RequiredArgsConstructor`, `bigdecimal`, `.toDetailContrat`, `package.json`, `RapportService`, `Client`, `ContratController`, `Contrat`, `Utilisateur`, `ContratLien`, `AdresseAffaireIndividuelle`, `HistoriquePaiementMensuel`, `Emploi`, `Identifiant`, `CategorieTiers`, `BicBackendApplication.java`, `ModeRattachement`?**
  _High betweenness centrality (0.055) - this node is a cross-community bridge._
- **What connects `A_JOUR`, `JOURS_120_149`, `JOURS_150_179` to the rest of the system?**
  _46 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `ScoreService` be split into smaller, more focused modules?**
  _Cohesion score 0.050351721584598295 - nodes in this community are weakly interconnected._
- **Why does `RapportService` connect `ContratForm.tsx` to `ScoreService`, `lombok.RequiredArgsConstructor`, `ClientService`, `.toDetailContrat`, `package.json`, `.construire`, `compilerOptions`, `.historiquePaiement`, `ContratController`, `Contrat`, `AffaireIndividuelle`, `Utilisateur`, `clientSchema.ts`, `AdresseAffaireIndividuelle`, `Emploi`, `axiosClient.ts`, `Identifiant`, `Genre`, `ModeRattachement`?**
  _High betweenness centrality (0.035) - this node is a cross-community bridge._
- **Should `SecurityConfig.java` be split into smaller, more focused modules?**
  _Cohesion score 0.05925925925925926 - nodes in this community are weakly interconnected._
- **Why does `Client` connect `Garantie` to `useIdleLogout`, `RoleClient`, `rapport.ts`, `org.springframework.http.ResponseEntity`, `HistoriquePaiementMensuel`, `Emploi`, `.toScoreDto`, `package.json`, `ClientRepository`, `GestionUtilisateursPage`, `DetailContratDTO`, `Client`?**
  _High betweenness centrality (0.034) - this node is a cross-community bridge._
- **Should `ModificationContratDemande` be split into smaller, more focused modules?**
  _Cohesion score 0.10384615384615385 - nodes in this community are weakly interconnected._