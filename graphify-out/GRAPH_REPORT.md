# Graph Report - BIC  (2026-10-27)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 1481 nodes · 3597 edges · 132 communities (56 shown, 76 thin omitted)
- Extraction: 95% EXTRACTED · 5% INFERRED · 0% AMBIGUOUS · INFERRED: 194 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `f303ac78`
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
- EncoursCategorieDTO
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
10. `RapportSolvabiliteResponse` - 26 edges

## Surprising Connections (you probably didn't know these)
- `RapportSolvabiliteResponse` --references--> `AlerteDTO`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/rapport/dto/RapportSolvabiliteResponse.java → bic-backend/src/main/java/mg/cecam/bic/rapport/dto/AlerteDTO.java
- `RapportService` --references--> `CalendrierService`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/rapport/RapportService.java → bic-backend/src/main/java/mg/cecam/bic/rapport/CalendrierService.java
- `LigneAnneeDTO` --references--> `CelluleMoisDTO`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/rapport/dto/LigneAnneeDTO.java → bic-backend/src/main/java/mg/cecam/bic/rapport/dto/CelluleMoisDTO.java
- `ModificationContratDemande` --references--> `Contrat`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/contrat/ModificationContratDemande.java → bic-backend/src/main/java/mg/cecam/bic/contrat/Contrat.java
- `ContratService` --references--> `ModificationContratDemandeRepository`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/contrat/ContratService.java → bic-backend/src/main/java/mg/cecam/bic/contrat/ModificationContratDemandeRepository.java

## Import Cycles
- None detected.

## Communities (132 total, 76 thin omitted)

### Community 0 - "ScoreService"
Cohesion: 0.07
Nodes (14): BucketRetard, A_JOUR, JOURS_120_149, JOURS_150_179, JOURS_180_PLUS, JOURS_1_29, JOURS_30_59, JOURS_60_89 (+6 more)

### Community 1 - "SecurityConfig.java"
Cohesion: 0.09
Nodes (10): StatutDemandeModification, APPROUVEE, EN_ATTENTE, REFUSEE, ModificationContratResponse, ModificationContratController, RefusRequest, ModificationContratDemande (+2 more)

### Community 2 - "App.tsx"
Cohesion: 0.14
Nodes (30): utilisateurApi, UtilisateurRequest, UtilisateurResponse, Button(), Card(), CardContent(), CardDescription(), CardHeader() (+22 more)

### Community 3 - "ModificationContratDemande"
Cohesion: 0.11
Nodes (28): ChampsObligatoiresNote(), affichageVersIso(), DateInput, DateInputProps, isoVersAffichage(), Input(), formatAffichage(), MontantInput (+20 more)

### Community 4 - "JwtAuthenticationFilter.java"
Cohesion: 0.11
Nodes (26): App(), AppShell(), pageTransition, RapportPageRoute(), AdminRoute(), ProtectedRoute(), HomeMenu(), Tache (+18 more)

### Community 5 - "ContratForm.tsx"
Cohesion: 0.09
Nodes (12): AdresseDTO, AffaireIndividuelleDTO, ClientInfoDTO, ConsultationDTO, ContactDTO, DernierContratDTO, DetailDemandeDTO, EmploiDTO (+4 more)

### Community 6 - "RapportSolvabiliteResponse"
Cohesion: 0.10
Nodes (3): Client, ClientRepository, ClientEnregistrementResponse

### Community 7 - "MiseAJourDemandePage.tsx"
Cohesion: 0.14
Nodes (13): BienLeasingDetailResponse, ClientLieDetailResponse, EcheanceDetailResponse, GarantieDetailResponse, HistoriqueContratDetailResponse, MoisDetailResponse, BienLeasingHistoriqueRequest, ClientLieHistoriqueRequest (+5 more)

### Community 8 - "JournalAudit"
Cohesion: 0.15
Nodes (6): ScoreResult, Capacite, Observation, Plafond, RevenuEvaluation, ScoreService

### Community 9 - "rapport.ts"
Cohesion: 0.15
Nodes (22): buttonVariants, SelectContent(), SelectItem(), SelectScrollDownButton(), SelectScrollUpButton(), SelectTrigger(), SelectValue(), formatDateFr() (+14 more)

### Community 10 - "localdate"
Cohesion: 0.17
Nodes (3): UtilisateurRequest, UtilisateurResponse, UtilisateurController

### Community 12 - "GestionUtilisateursPage.tsx"
Cohesion: 0.08
Nodes (25): Adresse, AffaireIndividuelle, Alerte, BienLeasing, CalendrierCredit, CelluleMois, ClientInfo, Contact (+17 more)

### Community 14 - "bigdecimal"
Cohesion: 0.15
Nodes (20): modificationContratApi, DEVISES, PHASE_LABELS, PHASES, BienLeasingDetailResponse, ClientLieDetailResponse, ContratRequest, ContratResponse (+12 more)

### Community 15 - "ClientService"
Cohesion: 0.11
Nodes (5): CodesEtablissementRequest, ContratController, EcheanceMensuelleRequest, PhaseRequest, ContratRepository

### Community 18 - "ImportPage.tsx"
Cohesion: 0.13
Nodes (3): PaiementRequest, AlerteDTO, SituationFinanciereMensuelleDTO

### Community 19 - "components.json"
Cohesion: 0.11
Nodes (21): name, private, type, version, class-variance-authority, eslint, @eslint/js, eslint-plugin-react-hooks (+13 more)

### Community 20 - "ContratService"
Cohesion: 0.17
Nodes (3): DernierContratRepository, EcheanceRepository, ScoreServiceBehaviorTest

### Community 21 - "package.json"
Cohesion: 0.09
Nodes (21): aliases, components, hooks, lib, ui, utils, iconLibrary, menuAccent (+13 more)

### Community 24 - "Contrat"
Cohesion: 0.10
Nodes (20): compilerOptions, allowArbitraryExtensions, allowImportingTsExtensions, erasableSyntaxOnly, jsx, lib, module, moduleDetection (+12 more)

### Community 26 - "Echeance"
Cohesion: 0.13
Nodes (3): ImportResultatDTO, CalendrierCreditDTO, LigneAnneeDTO

### Community 28 - ".construire"
Cohesion: 0.20
Nodes (4): DetailContratDTO, GarantieDTO, LienClientDTO, LigneFinancementDTO

### Community 30 - "compilerOptions"
Cohesion: 0.18
Nodes (3): BicBackendApplicationTests, RapportClientStatusTest, ScoreServiceRulesTest

### Community 31 - "Client"
Cohesion: 0.16
Nodes (16): clientApi, ClientEnregistrementResponse, PageResponse, contratApi, AdresseRequest, AffaireIndividuelleAdresseRequest, AffaireIndividuelleContactRequest, AffaireIndividuelleIdentifiantRequest (+8 more)

### Community 32 - "EcheanceController.java"
Cohesion: 0.20
Nodes (9): AppBreadcrumb(), segmentsForPath(), DropdownMenu(), DropdownMenuContent(), DropdownMenuItem(), DropdownMenuSub(), DropdownMenuSubContent(), DropdownMenuSubTrigger() (+1 more)

### Community 33 - "list"
Cohesion: 0.11
Nodes (18): dependencies, axios, @base-ui/react, class-variance-authority, cn, @fontsource-variable/geist, @hookform/resolvers, lucide-react (+10 more)

### Community 34 - ".historiquePaiement"
Cohesion: 0.15
Nodes (14): Champ(), ClientLieLigne, defaults, EcheanceLigne, FormValues, GarantieLigne, MoisLigne, optionalDate() (+6 more)

### Community 36 - "ContratController"
Cohesion: 0.12
Nodes (16): compilerOptions, allowImportingTsExtensions, erasableSyntaxOnly, lib, module, moduleDetection, noEmit, noFallthroughCasesInSwitch (+8 more)

### Community 38 - "devDependencies"
Cohesion: 0.17
Nodes (4): ClientResponse, ContratRequest, DernierContratRequest, RevenuDemandeRequest

### Community 40 - "Utilisateur"
Cohesion: 0.19
Nodes (3): UtilisateurRepository, EmploiRepository, EvenementNegatifRepository

### Community 41 - "ContratLien"
Cohesion: 0.15
Nodes (5): ModeRattachement, DEMANDE_EXISTANTE, ENTREPRISE, NOUVELLE_DEMANDE, CouvertureGarantie

### Community 42 - "clientSchema.ts"
Cohesion: 0.19
Nodes (9): authApi, LoginResponse, axiosClient, rapportApi, EntreeAudit, RapportPage(), telechargerEtVisualiserPdf(), RapportSolvabilite (+1 more)

### Community 44 - "AdresseAffaireIndividuelle"
Cohesion: 0.24
Nodes (3): EncoursCategorieDTO, SyntheseCategorieDTO, RapportService

### Community 46 - "BienLeasing"
Cohesion: 0.17
Nodes (5): StatutEcheance, A_VENIR, EN_RETARD, IMPAYE, PAYE_A_TEMPS

### Community 47 - "ContratRepository"
Cohesion: 0.18
Nodes (3): AuditService, JournalAudit, JournalAuditRepository

### Community 49 - "org.springframework.data.jpa.repository.JpaRepository"
Cohesion: 0.44
Nodes (5): AppProperties, Cors, Jwt, TauxMensuelProperties, ScoreProperties

### Community 50 - "Emploi"
Cohesion: 0.15
Nodes (13): devDependencies, eslint, @eslint/js, eslint-plugin-react-hooks, eslint-plugin-react-refresh, globals, @types/node, @types/react (+5 more)

### Community 51 - "axiosClient.ts"
Cohesion: 0.15
Nodes (12): adresseSchema, affaireIndividuelleAdresseSchema, affaireIndividuelleContactSchema, affaireIndividuelleIdentifiantSchema, affaireIndividuelleSchema, ClientFormInput, ClientFormValues, clientSchema (+4 more)

### Community 52 - ".toScoreDto"
Cohesion: 0.17
Nodes (4): Utilisateur, Role, ADMIN, AGENT_CREDIT

### Community 66 - "scripts"
Cohesion: 0.39
Nodes (6): GestionUtilisateursPage(), chargerUtilisateurs(), fermerFormulaire(), handleSubmit(), handleSupprimer(), handleToggleActif()

### Community 67 - "useIdleLogout"
Cohesion: 0.38
Nodes (6): AdresseAffaireIndividuelleRequest, AdresseRequest, AffaireIndividuelleRequest, ClientRequest, EmploiRequest, IdentifiantRequest

### Community 69 - "vite.config.ts"
Cohesion: 0.29
Nodes (6): ContratFormInput, ContratFormValues, contratSchema, dernierContratSchema, montantOptionnel, zod

### Community 70 - "MotifCloture"
Cohesion: 0.33
Nodes (6): PhaseDemande, ABANDONNE, ACTIF, DEMANDE_EN_COURS, FERME, REFUSE

### Community 72 - "apiError.ts"
Cohesion: 0.53
Nodes (4): ToastContext, ToastContextValue, ToastItem, react

### Community 74 - "PageShell.tsx"
Cohesion: 0.40
Nodes (4): OrigineDonnee, DONNEE_FICTIVE, NON_RENSEIGNEE, RAPPORT_CRIF

### Community 75 - "ImportLigneDTO.java"
Cohesion: 0.40
Nodes (4): RoleClient, CO_TITULAIRE, GARANT, TITULAIRE

### Community 77 - ".enregistrerPaiement"
Cohesion: 0.70
Nodes (3): Cle, SituationFinanciereMensuelleService, Totaux

### Community 78 - "ContactDTO.java"
Cohesion: 0.40
Nodes (5): StatutDernierContrat, A_TEMPS, ANTICIPATION, EN_RETARD, NON_REMBOURSE

### Community 79 - "GrilleScoreDTO.java"
Cohesion: 0.60
Nodes (3): demande_modification_contrat, idx_demande_modification_statut_date, uq_demande_modification_contrat_en_attente

### Community 80 - "RepartitionLigneDTO.java"
Cohesion: 0.60
Nodes (3): adresse_affaire_individuelle, affaire_individuelle, idx_adresse_affaire_individuelle_parent

### Community 81 - "ScoreDTO.java"
Cohesion: 0.40
Nodes (5): scripts, build, dev, lint, preview

### Community 82 - "client"
Cohesion: 0.70
Nodes (5): useIdleLogout(), effacerMinuteur(), enregistrerActivite(), programmerExpiration(), surChangementStockage()

### Community 83 - "login"
Cohesion: 0.40
Nodes (4): compilerOptions, paths, files, references

### Community 84 - "Adresse"
Cohesion: 0.40
Nodes (3): @tailwindcss/vite, vite, @vitejs/plugin-react

### Community 85 - "enums/CategorieTiers.java"
Cohesion: 0.50
Nodes (3): Genre, FEMME, HOMME

### Community 86 - "Transient"
Cohesion: 0.50
Nodes (3): MotifCloture, FIN_A_TERME, FIN_ANTICIPEE

### Community 87 - "PatchMapping"
Cohesion: 0.50
Nodes (3): NatureGarantie, PERSONNELLE, REELLE

## Knowledge Gaps
- **50 isolated node(s):** `A_JOUR`, `JOURS_120_149`, `JOURS_150_179`, `JOURS_180_PLUS`, `JOURS_1_29` (+45 more)
  These have ≤1 connection - possible missing edges. (Counts symbols only; 546 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **76 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Contrat` connect `dropdown-menu.tsx` to `SecurityConfig.java`, `ContratForm.tsx`, `MiseAJourDemandePage.tsx`, `JournalAudit`, `localdate`, `lombok.RequiredArgsConstructor`, `ClientService`, `ImportPage.tsx`, `ContratService`, `compilerOptions`, `RapportService`, `SaisieHistoriquePage.tsx`, `.construire`, `dependencies`, `compilerOptions`, `devDependencies`, `ContratLien`, `AdresseAffaireIndividuelle`, `ClientRepository`, `CategorieTiers`, `GrilleScore`, `20261012_add_affaire_individuelle.sql`, `RoleClient`, `.enregistrerPaiement`?**
  _High betweenness centrality (0.075) - this node is a cross-community bridge._
- **What connects `A_JOUR`, `JOURS_120_149`, `JOURS_150_179` to the rest of the system?**
  _50 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `ScoreService` be split into smaller, more focused modules?**
  _Cohesion score 0.06779661016949153 - nodes in this community are weakly interconnected._
- **Why does `Client` connect `RapportSolvabiliteResponse` to `devDependencies`, `MiseAJourDemandePage.tsx`, `AffaireIndividuelle`, `org.springframework.http.ResponseEntity`, `.toDetailContrat`, `ClientRepository`, `Identifiant`, `Echeance`, `PhaseDemande`, `ModeRattachement`?**
  _High betweenness centrality (0.044) - this node is a cross-community bridge._
- **Should `SecurityConfig.java` be split into smaller, more focused modules?**
  _Cohesion score 0.0898989898989899 - nodes in this community are weakly interconnected._
- **Why does `RapportService` connect `AdresseAffaireIndividuelle` to `ScoreService`, `ContratForm.tsx`, `MiseAJourDemandePage.tsx`, `ClientService`, `ContratService`, `compilerOptions`, `RapportService`, `dropdown-menu.tsx`, `.construire`, `dependencies`, `ClientRequest`, `ClientRepository`, `CategorieTiers`, `GrilleScore`, `GestionUtilisateursPage`, `Genre`, `BicBackendApplication.java`, `tsconfig.json`, `RoleClient`, `.enregistrerPaiement`?**
  _High betweenness centrality (0.027) - this node is a cross-community bridge._
- **Should `App.tsx` be split into smaller, more focused modules?**
  _Cohesion score 0.1383838383838384 - nodes in this community are weakly interconnected._