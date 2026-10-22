# Graph Report - BIC  (2026-10-22)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 1435 nodes · 3377 edges · 129 communities (47 shown, 82 thin omitted)
- Extraction: 95% EXTRACTED · 5% INFERRED · 0% AMBIGUOUS · INFERRED: 160 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `ec3bd884`
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
- RoleClient
- apiError.ts
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
- Transient
- 20261009_add_data_provenance.sql

## God Nodes (most connected - your core abstractions)
1. `Contrat` - 91 edges
2. `RapportService` - 59 edges
3. `Echeance` - 45 edges
4. `ScoreService` - 40 edges
5. `Client` - 34 edges
6. `ContratService` - 32 edges
7. `react` - 30 edges
8. `Button()` - 28 edges
9. `RapportSolvabiliteResponse` - 26 edges
10. `ClientForm()` - 26 edges

## Surprising Connections (you probably didn't know these)
- `RapportService` --references--> `CalendrierService`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/rapport/RapportService.java → bic-backend/src/main/java/mg/cecam/bic/rapport/CalendrierService.java
- `CalendrierCreditDTO` --references--> `LigneAnneeDTO`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/rapport/dto/CalendrierCreditDTO.java → bic-backend/src/main/java/mg/cecam/bic/rapport/dto/LigneAnneeDTO.java
- `LigneFinancementDTO` --references--> `LigneAnneeDTO`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/rapport/dto/LigneFinancementDTO.java → bic-backend/src/main/java/mg/cecam/bic/rapport/dto/LigneAnneeDTO.java
- `RapportSolvabiliteResponse` --references--> `EncoursCategorieDTO`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/rapport/dto/RapportSolvabiliteResponse.java → bic-backend/src/main/java/mg/cecam/bic/rapport/dto/EncoursCategorieDTO.java
- `ModificationContratDemande` --references--> `Contrat`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/contrat/ModificationContratDemande.java → bic-backend/src/main/java/mg/cecam/bic/contrat/Contrat.java

## Import Cycles
- None detected.

## Communities (129 total, 82 thin omitted)

### Community 0 - "ScoreService"
Cohesion: 0.06
Nodes (14): StatutEcheance, A_VENIR, EN_RETARD, IMPAYE, PAYE_A_TEMPS, LabelMapper, MontantFormatUtil, EcheanceHistoriqueRequest (+6 more)

### Community 1 - "SecurityConfig.java"
Cohesion: 0.09
Nodes (10): StatutDemandeModification, APPROUVEE, EN_ATTENTE, REFUSEE, ModificationContratResponse, ModificationContratController, RefusRequest, ModificationContratDemande (+2 more)

### Community 2 - "App.tsx"
Cohesion: 0.09
Nodes (31): App(), AppShell(), pageTransition, RapportPageRoute(), AdminRoute(), ProtectedRoute(), HomeMenu(), Tache (+23 more)

### Community 3 - "ModificationContratDemande"
Cohesion: 0.10
Nodes (31): ChampsObligatoiresNote(), affichageVersIso(), DateInput, DateInputProps, isoVersAffichage(), Input(), formatAffichage(), MontantInput (+23 more)

### Community 4 - "JwtAuthenticationFilter.java"
Cohesion: 0.12
Nodes (32): Button(), Card(), CardContent(), CardDescription(), CardHeader(), CardTitle(), Label(), useToast() (+24 more)

### Community 5 - "ContratForm.tsx"
Cohesion: 0.09
Nodes (10): ContratLien, AlerteDTO, BienLeasingDTO, DetailContratDTO, DetailDemandeDTO, EmploiDTO, GarantieDTO, LienClientDTO (+2 more)

### Community 7 - "MiseAJourDemandePage.tsx"
Cohesion: 0.12
Nodes (10): PhaseRequest, ContratRequest, DernierContratRequest, RevenuDemandeRequest, BienLeasingHistoriqueRequest, ClientLieHistoriqueRequest, GarantieHistoriqueRequest, HistoriqueContratRequest (+2 more)

### Community 8 - "JournalAudit"
Cohesion: 0.09
Nodes (23): authApi, LoginResponse, axiosClient, clientApi, ClientEnregistrementResponse, rapportApi, ClientLieLigne, defaults (+15 more)

### Community 9 - "rapport.ts"
Cohesion: 0.10
Nodes (6): AdresseDTO, AffaireIndividuelleDTO, ClientInfoDTO, ConsultationDTO, ContactDTO, IdentifiantDTO

### Community 10 - "localdate"
Cohesion: 0.12
Nodes (3): Echeance, EcheanceRepository, HistoriquePaiementLigneDTO

### Community 11 - "lombok.RequiredArgsConstructor"
Cohesion: 0.15
Nodes (3): UtilisateurRequest, UtilisateurResponse, UtilisateurController

### Community 12 - "GestionUtilisateursPage.tsx"
Cohesion: 0.15
Nodes (6): ScoreResult, Capacite, Observation, Plafond, RevenuEvaluation, ScoreService

### Community 13 - "org.springframework.http.ResponseEntity"
Cohesion: 0.08
Nodes (25): Adresse, AffaireIndividuelle, Alerte, BienLeasing, CalendrierCredit, CelluleMois, ClientInfo, Contact (+17 more)

### Community 15 - "ClientService"
Cohesion: 0.13
Nodes (6): DernierContratDTO, RepartitionLigneDTO, ScoreDTO, SyntheseCategorieDTO, SyntheseDTO, RapportService

### Community 16 - ".toDetailContrat"
Cohesion: 0.19
Nodes (18): buttonVariants, SelectContent(), SelectItem(), SelectScrollDownButton(), SelectScrollUpButton(), SelectTrigger(), SelectValue(), formatDateFr() (+10 more)

### Community 19 - "components.json"
Cohesion: 0.09
Nodes (21): aliases, components, hooks, lib, ui, utils, iconLibrary, menuAccent (+13 more)

### Community 20 - "ContratService"
Cohesion: 0.11
Nodes (20): name, private, type, version, class-variance-authority, eslint, @eslint/js, eslint-plugin-react-hooks (+12 more)

### Community 21 - "package.json"
Cohesion: 0.10
Nodes (11): PhaseDemande, ABANDONNE, ACTIF, DEMANDE_EN_COURS, FERME, REFUSE, RoleClient, CO_TITULAIRE (+3 more)

### Community 22 - "compilerOptions"
Cohesion: 0.10
Nodes (20): compilerOptions, allowArbitraryExtensions, allowImportingTsExtensions, erasableSyntaxOnly, jsx, lib, module, moduleDetection (+12 more)

### Community 25 - "dropdown-menu.tsx"
Cohesion: 0.10
Nodes (18): adresseSchema, affaireIndividuelleAdresseSchema, affaireIndividuelleContactSchema, affaireIndividuelleIdentifiantSchema, affaireIndividuelleSchema, ClientFormInput, ClientFormValues, clientSchema (+10 more)

### Community 27 - "SaisieHistoriquePage.tsx"
Cohesion: 0.25
Nodes (13): contratApi, modificationContratApi, DEVISES, PHASE_LABELS, PHASES, ContratRequest, ContratResponse, DernierContratRequest (+5 more)

### Community 28 - ".construire"
Cohesion: 0.20
Nodes (9): AppBreadcrumb(), segmentsForPath(), DropdownMenu(), DropdownMenuContent(), DropdownMenuItem(), DropdownMenuSub(), DropdownMenuSubContent(), DropdownMenuSubTrigger() (+1 more)

### Community 29 - "dependencies"
Cohesion: 0.11
Nodes (18): dependencies, axios, @base-ui/react, class-variance-authority, cn, @fontsource-variable/geist, @hookform/resolvers, lucide-react (+10 more)

### Community 30 - "compilerOptions"
Cohesion: 0.26
Nodes (3): AuditController, AlerteController, AlerteService

### Community 33 - "list"
Cohesion: 0.19
Nodes (3): BicBackendApplicationTests, RapportClientStatusTest, ScoreServiceRulesTest

### Community 34 - ".historiquePaiement"
Cohesion: 0.12
Nodes (16): compilerOptions, allowImportingTsExtensions, erasableSyntaxOnly, lib, module, moduleDetection, noEmit, noFallthroughCasesInSwitch (+8 more)

### Community 37 - "Contrat"
Cohesion: 0.19
Nodes (6): EmploiRepository, EvenementNegatifRepository, ContratLienRepository, GarantieRepository, HistoriquePaiementMensuelRepository, GrilleScoreRepository

### Community 38 - "devDependencies"
Cohesion: 0.29
Nodes (8): utilisateurApi, UtilisateurRequest, UtilisateurResponse, ToastContext, ToastContextValue, ToastItem, lucide-react, react

### Community 43 - "Adresse"
Cohesion: 0.15
Nodes (13): devDependencies, eslint, @eslint/js, eslint-plugin-react-hooks, eslint-plugin-react-refresh, globals, @types/node, @types/react (+5 more)

### Community 45 - "ClientRequest"
Cohesion: 0.17
Nodes (4): Utilisateur, Role, ADMIN, AGENT_CREDIT

### Community 47 - "ContratRepository"
Cohesion: 0.17
Nodes (4): NatureGarantie, PERSONNELLE, REELLE, Garantie

### Community 50 - "Emploi"
Cohesion: 0.18
Nodes (9): BucketRetard, A_JOUR, JOURS_120_149, JOURS_150_179, JOURS_180_PLUS, JOURS_1_29, JOURS_30_59, JOURS_60_89 (+1 more)

### Community 51 - "axiosClient.ts"
Cohesion: 0.18
Nodes (10): AdresseRequest, AffaireIndividuelleAdresseRequest, AffaireIndividuelleContactRequest, AffaireIndividuelleIdentifiantRequest, AffaireIndividuelleRequest, ContactRequest, DocumentIdentificationRequest, EmploiRequest (+2 more)

### Community 60 - "DetailContratDTO"
Cohesion: 0.33
Nodes (3): CodesEtablissementRequest, ContratController, EcheanceMensuelleRequest

### Community 67 - "useIdleLogout"
Cohesion: 0.39
Nodes (6): GestionUtilisateursPage(), chargerUtilisateurs(), fermerFormulaire(), handleSubmit(), handleSupprimer(), handleToggleActif()

### Community 68 - "tsconfig.json"
Cohesion: 0.38
Nodes (6): AdresseAffaireIndividuelleRequest, AdresseRequest, AffaireIndividuelleRequest, ClientRequest, EmploiRequest, IdentifiantRequest

### Community 70 - "MotifCloture"
Cohesion: 0.40
Nodes (4): ClientResponse, Genre, FEMME, HOMME

### Community 74 - "PageShell.tsx"
Cohesion: 0.40
Nodes (4): ModeRattachement, DEMANDE_EXISTANTE, ENTREPRISE, NOUVELLE_DEMANDE

### Community 75 - "ImportLigneDTO.java"
Cohesion: 0.40
Nodes (4): OrigineDonnee, DONNEE_FICTIVE, NON_RENSEIGNEE, RAPPORT_CRIF

### Community 76 - ".rechercher"
Cohesion: 0.70
Nodes (3): Cle, SituationFinanciereMensuelleService, Totaux

### Community 77 - ".enregistrerPaiement"
Cohesion: 0.40
Nodes (5): StatutDernierContrat, A_TEMPS, ANTICIPATION, EN_RETARD, NON_REMBOURSE

### Community 78 - "ContactDTO.java"
Cohesion: 0.60
Nodes (3): demande_modification_contrat, idx_demande_modification_statut_date, uq_demande_modification_contrat_en_attente

### Community 79 - "GrilleScoreDTO.java"
Cohesion: 0.60
Nodes (3): adresse_affaire_individuelle, affaire_individuelle, idx_adresse_affaire_individuelle_parent

### Community 80 - "RepartitionLigneDTO.java"
Cohesion: 0.40
Nodes (5): scripts, build, dev, lint, preview

### Community 82 - "client"
Cohesion: 0.40
Nodes (4): compilerOptions, paths, files, references

### Community 83 - "login"
Cohesion: 0.40
Nodes (3): @tailwindcss/vite, vite, @vitejs/plugin-react

### Community 84 - "Adresse"
Cohesion: 0.50
Nodes (3): MotifCloture, FIN_A_TERME, FIN_ANTICIPEE

## Knowledge Gaps
- **50 isolated node(s):** `A_VENIR`, `EN_RETARD`, `IMPAYE`, `PAYE_A_TEMPS`, `APPROUVEE` (+45 more)
  These have ≤1 connection - possible missing edges. (Counts symbols only; 560 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **82 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Contrat` connect `RapportSolvabiliteResponse` to `EcheanceController.java`, `SecurityConfig.java`, `ScoreService`, `Garantie`, `ContratForm.tsx`, `MiseAJourDemandePage.tsx`, `rapport.ts`, `localdate`, `.rechercher`, `GestionUtilisateursPage.tsx`, `ContratRepository`, `ClientService`, `ClientRepository`, `package.json`, `CategorieTiers`, `Contrat`, `DetailContratDTO`, `BicBackendApplication.java`?**
  _High betweenness centrality (0.064) - this node is a cross-community bridge._
- **What connects `A_VENIR`, `EN_RETARD`, `IMPAYE` to the rest of the system?**
  _50 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `ScoreService` be split into smaller, more focused modules?**
  _Cohesion score 0.06370543541788427 - nodes in this community are weakly interconnected._
- **Why does `Client` connect `Client` to `ContratForm.tsx`, `MiseAJourDemandePage.tsx`, `lombok.RequiredArgsConstructor`, `bigdecimal`, `HistoriquePaiementMensuel`, `Contrat.java`, `org.springframework.data.jpa.repository.JpaRepository`, `.toScoreDto`, `GestionUtilisateursPage`, `ModeRattachement`?**
  _High betweenness centrality (0.047) - this node is a cross-community bridge._
- **Should `SecurityConfig.java` be split into smaller, more focused modules?**
  _Cohesion score 0.08549019607843138 - nodes in this community are weakly interconnected._
- **Why does `Echeance` connect `localdate` to `ScoreService`, `20261012_add_affaire_individuelle.sql`, `ContratForm.tsx`, `RapportSolvabiliteResponse`, `.rechercher`, `ClientService`, `package.json`, `RapportService`?**
  _High betweenness centrality (0.023) - this node is a cross-community bridge._
- **Should `App.tsx` be split into smaller, more focused modules?**
  _Cohesion score 0.09393939393939393 - nodes in this community are weakly interconnected._