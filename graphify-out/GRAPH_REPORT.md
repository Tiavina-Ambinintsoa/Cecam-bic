# Graph Report - BIC  (2026-10-22)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 1440 nodes · 3426 edges · 123 communities (52 shown, 71 thin omitted)
- Extraction: 95% EXTRACTED · 5% INFERRED · 0% AMBIGUOUS · INFERRED: 164 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `6689aff8`
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
- RepartitionLigneDTO.java
- 20261008_repair_embedded_contract_currency.sql

## God Nodes (most connected - your core abstractions)
1. `Contrat` - 93 edges
2. `RapportService` - 60 edges
3. `Echeance` - 45 edges
4. `ScoreService` - 43 edges
5. `Client` - 34 edges
6. `ContratService` - 32 edges
7. `react` - 30 edges
8. `Button()` - 28 edges
9. `RapportSolvabiliteResponse` - 27 edges
10. `ClientForm()` - 26 edges

## Surprising Connections (you probably didn't know these)
- `ModificationContratController` --references--> `ModificationContratService`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/contrat/ModificationContratController.java → bic-backend/src/main/java/mg/cecam/bic/contrat/ModificationContratService.java
- `ContratController` --references--> `ContratRepository`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/contrat/ContratController.java → bic-backend/src/main/java/mg/cecam/bic/contrat/ContratRepository.java
- `ContratController` --references--> `ContratService`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/contrat/ContratController.java → bic-backend/src/main/java/mg/cecam/bic/contrat/ContratService.java
- `RapportService` --references--> `CalendrierService`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/rapport/RapportService.java → bic-backend/src/main/java/mg/cecam/bic/rapport/CalendrierService.java
- `LigneAnneeDTO` --references--> `CelluleMoisDTO`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/rapport/dto/LigneAnneeDTO.java → bic-backend/src/main/java/mg/cecam/bic/rapport/dto/CelluleMoisDTO.java

## Import Cycles
- None detected.

## Communities (123 total, 71 thin omitted)

### Community 0 - "ScoreService"
Cohesion: 0.06
Nodes (8): UtilisateurRequest, UtilisateurResponse, UtilisateurController, CodesEtablissementRequest, ContratController, EcheanceMensuelleRequest, ModificationContratController, RefusRequest

### Community 1 - "SecurityConfig.java"
Cohesion: 0.06
Nodes (17): BucketRetard, A_JOUR, JOURS_120_149, JOURS_150_179, JOURS_180_PLUS, JOURS_1_29, JOURS_30_59, JOURS_60_89 (+9 more)

### Community 2 - "App.tsx"
Cohesion: 0.15
Nodes (27): utilisateurApi, UtilisateurRequest, UtilisateurResponse, Button(), Card(), CardContent(), CardDescription(), CardHeader() (+19 more)

### Community 4 - "JwtAuthenticationFilter.java"
Cohesion: 0.11
Nodes (28): App(), AppShell(), pageTransition, RapportPageRoute(), AdminRoute(), ProtectedRoute(), HomeMenu(), Tache (+20 more)

### Community 6 - "RapportSolvabiliteResponse"
Cohesion: 0.12
Nodes (29): buttonVariants, SelectContent(), SelectGroup(), SelectItem(), SelectLabel(), SelectScrollDownButton(), SelectScrollUpButton(), SelectTrigger() (+21 more)

### Community 7 - "MiseAJourDemandePage.tsx"
Cohesion: 0.10
Nodes (6): Contrat, ContratRepository, DernierContrat, Cle, SituationFinanciereMensuelleService, Totaux

### Community 8 - "JournalAudit"
Cohesion: 0.12
Nodes (4): AuditService, ClientController, ClientService, EcheanceController

### Community 9 - "rapport.ts"
Cohesion: 0.07
Nodes (30): rapportApi, RapportPage(), telechargerEtVisualiserPdf(), Adresse, AffaireIndividuelle, Alerte, BienLeasing, CalendrierCredit (+22 more)

### Community 10 - "localdate"
Cohesion: 0.12
Nodes (8): StatutDemandeModification, APPROUVEE, EN_ATTENTE, REFUSEE, ModificationContratResponse, ModificationContratDemande, ModificationContratDemandeRepository, ModificationContratService

### Community 11 - "lombok.RequiredArgsConstructor"
Cohesion: 0.13
Nodes (6): ScoreResult, Capacite, Observation, Plafond, RevenuEvaluation, ScoreService

### Community 12 - "GestionUtilisateursPage.tsx"
Cohesion: 0.11
Nodes (5): AuditController, JournalAudit, JournalAuditRepository, AlerteController, AlerteService

### Community 13 - "org.springframework.http.ResponseEntity"
Cohesion: 0.15
Nodes (18): ChampsObligatoiresNote(), affichageVersIso(), DateInput, DateInputProps, isoVersAffichage(), Input(), formatAffichage(), MontantInput (+10 more)

### Community 14 - "bigdecimal"
Cohesion: 0.11
Nodes (6): UtilisateurRepository, EvenementNegatif, EvenementNegatifRepository, EcheanceRepository, RevenuDemandeRepository, GrilleScoreRepository

### Community 15 - "ClientService"
Cohesion: 0.13
Nodes (7): AdresseAffaireIndividuelleRequest, AdresseRequest, AffaireIndividuelleRequest, ClientRequest, EmploiRequest, IdentifiantRequest, ImportResultatDTO

### Community 16 - ".toDetailContrat"
Cohesion: 0.13
Nodes (8): AlerteDTO, ConsultationDTO, DernierContratDTO, DetailDemandeDTO, EmploiDTO, IdentifiantDTO, RapportSolvabiliteResponse, RapportController

### Community 17 - "Contrat.java"
Cohesion: 0.16
Nodes (18): authApi, LoginResponse, axiosClient, contratApi, modificationContratApi, DEVISES, PHASE_LABELS, PHASES (+10 more)

### Community 18 - "ImportPage.tsx"
Cohesion: 0.17
Nodes (7): PhaseRequest, BienLeasingHistoriqueRequest, ClientLieHistoriqueRequest, GarantieHistoriqueRequest, HistoriqueContratRequest, HistoriquePaiementMensuelRequest, ModificationContratRequest

### Community 21 - "package.json"
Cohesion: 0.14
Nodes (3): BicBackendApplicationTests, RapportClientStatusTest, ScoreServiceRulesTest

### Community 22 - "compilerOptions"
Cohesion: 0.09
Nodes (21): aliases, components, hooks, lib, ui, utils, iconLibrary, menuAccent (+13 more)

### Community 23 - "RapportService"
Cohesion: 0.11
Nodes (20): name, private, type, version, class-variance-authority, eslint, @eslint/js, eslint-plugin-react-hooks (+12 more)

### Community 25 - "dropdown-menu.tsx"
Cohesion: 0.15
Nodes (5): EncoursCategorieDTO, RepartitionLigneDTO, SyntheseCategorieDTO, SyntheseDTO, RapportService

### Community 26 - "Echeance"
Cohesion: 0.10
Nodes (20): compilerOptions, allowArbitraryExtensions, allowImportingTsExtensions, erasableSyntaxOnly, jsx, lib, module, moduleDetection (+12 more)

### Community 27 - "SaisieHistoriquePage.tsx"
Cohesion: 0.17
Nodes (3): BienLeasingDTO, DetailContratDTO, GarantieDTO

### Community 28 - ".construire"
Cohesion: 0.10
Nodes (18): adresseSchema, affaireIndividuelleAdresseSchema, affaireIndividuelleContactSchema, affaireIndividuelleIdentifiantSchema, affaireIndividuelleSchema, ClientFormInput, ClientFormValues, clientSchema (+10 more)

### Community 29 - "dependencies"
Cohesion: 0.15
Nodes (15): clientApi, ClientEnregistrementResponse, ToastContext, ToastContextValue, ToastItem, analyserCsv(), clientDepuisCsv(), CsvRow (+7 more)

### Community 30 - "compilerOptions"
Cohesion: 0.20
Nodes (9): AppBreadcrumb(), segmentsForPath(), DropdownMenu(), DropdownMenuContent(), DropdownMenuItem(), DropdownMenuSub(), DropdownMenuSubContent(), DropdownMenuSubTrigger() (+1 more)

### Community 31 - "Client"
Cohesion: 0.11
Nodes (18): dependencies, axios, @base-ui/react, class-variance-authority, cn, @fontsource-variable/geist, @hookform/resolvers, lucide-react (+10 more)

### Community 34 - ".historiquePaiement"
Cohesion: 0.12
Nodes (16): compilerOptions, allowImportingTsExtensions, erasableSyntaxOnly, lib, module, moduleDetection, noEmit, noFallthroughCasesInSwitch (+8 more)

### Community 36 - "ContratController"
Cohesion: 0.14
Nodes (5): NatureGarantie, PERSONNELLE, REELLE, Garantie, GarantieRepository

### Community 37 - "Contrat"
Cohesion: 0.16
Nodes (4): ScoreColorMapper, GrilleScoreDTO, RisqueGlobalDTO, ScoreDTO

### Community 39 - "AffaireIndividuelle"
Cohesion: 0.19
Nodes (12): ClientLieLigne, defaults, EcheanceLigne, FormValues, GarantieLigne, MoisLigne, optionalDate(), optionalNumber() (+4 more)

### Community 42 - "clientSchema.ts"
Cohesion: 0.24
Nodes (4): ContratService, ContratRequest, DernierContratRequest, RevenuDemandeRequest

### Community 43 - "Adresse"
Cohesion: 0.15
Nodes (13): devDependencies, eslint, @eslint/js, eslint-plugin-react-hooks, eslint-plugin-react-refresh, globals, @types/node, @types/react (+5 more)

### Community 44 - "AdresseAffaireIndividuelle"
Cohesion: 0.17
Nodes (4): Utilisateur, Role, ADMIN, AGENT_CREDIT

### Community 49 - "org.springframework.data.jpa.repository.JpaRepository"
Cohesion: 0.18
Nodes (10): AdresseRequest, AffaireIndividuelleAdresseRequest, AffaireIndividuelleContactRequest, AffaireIndividuelleIdentifiantRequest, AffaireIndividuelleRequest, ContactRequest, DocumentIdentificationRequest, EmploiRequest (+2 more)

### Community 50 - "Emploi"
Cohesion: 0.20
Nodes (3): AdresseDTO, ClientInfoDTO, ContactDTO

### Community 56 - "GrilleScore"
Cohesion: 0.29
Nodes (4): CalendrierCreditDTO, LienClientDTO, LigneAnneeDTO, LigneFinancementDTO

### Community 60 - "DetailContratDTO"
Cohesion: 0.39
Nodes (6): GestionUtilisateursPage(), chargerUtilisateurs(), fermerFormulaire(), handleSubmit(), handleSupprimer(), handleToggleActif()

### Community 61 - "BicBackendApplication.java"
Cohesion: 0.40
Nodes (4): ClientResponse, Genre, FEMME, HOMME

### Community 62 - "ModeRattachement"
Cohesion: 0.33
Nodes (6): PhaseDemande, ABANDONNE, ACTIF, DEMANDE_EN_COURS, FERME, REFUSE

### Community 65 - "BicBackendApplicationTests.java"
Cohesion: 0.40
Nodes (4): ModeRattachement, DEMANDE_EXISTANTE, ENTREPRISE, NOUVELLE_DEMANDE

### Community 66 - "scripts"
Cohesion: 0.40
Nodes (4): OrigineDonnee, DONNEE_FICTIVE, NON_RENSEIGNEE, RAPPORT_CRIF

### Community 67 - "useIdleLogout"
Cohesion: 0.40
Nodes (5): StatutEcheance, A_VENIR, EN_RETARD, IMPAYE, PAYE_A_TEMPS

### Community 68 - "tsconfig.json"
Cohesion: 0.40
Nodes (5): StatutDernierContrat, A_TEMPS, ANTICIPATION, EN_RETARD, NON_REMBOURSE

### Community 69 - "vite.config.ts"
Cohesion: 0.60
Nodes (3): demande_modification_contrat, idx_demande_modification_statut_date, uq_demande_modification_contrat_en_attente

### Community 70 - "MotifCloture"
Cohesion: 0.60
Nodes (3): adresse_affaire_individuelle, affaire_individuelle, idx_adresse_affaire_individuelle_parent

### Community 71 - "RoleClient"
Cohesion: 0.40
Nodes (5): scripts, build, dev, lint, preview

### Community 72 - "apiError.ts"
Cohesion: 0.70
Nodes (5): useIdleLogout(), effacerMinuteur(), enregistrerActivite(), programmerExpiration(), surChangementStockage()

### Community 74 - "PageShell.tsx"
Cohesion: 0.40
Nodes (4): compilerOptions, paths, files, references

### Community 75 - "ImportLigneDTO.java"
Cohesion: 0.40
Nodes (3): @tailwindcss/vite, vite, @vitejs/plugin-react

### Community 76 - ".rechercher"
Cohesion: 0.50
Nodes (3): MotifCloture, FIN_A_TERME, FIN_ANTICIPEE

### Community 77 - ".enregistrerPaiement"
Cohesion: 0.50
Nodes (4): RoleClient, CO_TITULAIRE, GARANT, TITULAIRE

## Knowledge Gaps
- **50 isolated node(s):** `A_JOUR`, `JOURS_120_149`, `JOURS_150_179`, `JOURS_180_PLUS`, `JOURS_1_29` (+45 more)
  These have ≤1 connection - possible missing edges. (Counts symbols only; 548 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **71 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Contrat` connect `MiseAJourDemandePage.tsx` to `ScoreService`, `SecurityConfig.java`, `ModificationContratDemande`, `localdate`, `lombok.RequiredArgsConstructor`, `.toDetailContrat`, `ImportPage.tsx`, `package.json`, `Contrat`, `dropdown-menu.tsx`, `SaisieHistoriquePage.tsx`, `ContratController`, `Contrat`, `clientSchema.ts`, `ClientRequest`, `BienLeasing`, `HistoriquePaiementMensuel`, `Emploi`, `ClientRepository`, `CategorieTiers`, `GrilleScore`?**
  _High betweenness centrality (0.081) - this node is a cross-community bridge._
- **What connects `A_JOUR`, `JOURS_120_149`, `JOURS_150_179` to the rest of the system?**
  _50 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `ScoreService` be split into smaller, more focused modules?**
  _Cohesion score 0.06010230179028133 - nodes in this community are weakly interconnected._
- **Why does `Client` connect `list` to `ScoreService`, `ContratForm.tsx`, `JournalAudit`, `ContratLien`, `ClientRequest`, `bigdecimal`, `ClientService`, `ContratRepository`, `ImportPage.tsx`, `components.json`, `.toScoreDto`?**
  _High betweenness centrality (0.033) - this node is a cross-community bridge._
- **Should `SecurityConfig.java` be split into smaller, more focused modules?**
  _Cohesion score 0.05711849957374254 - nodes in this community are weakly interconnected._
- **Why does `RapportService` connect `dropdown-menu.tsx` to `SecurityConfig.java`, `ModificationContratDemande`, `ContratController`, `Contrat`, `MiseAJourDemandePage.tsx`, `JournalAudit`, `GestionUtilisateursPage.tsx`, `ClientRequest`, `bigdecimal`, `BienLeasing`, `.toDetailContrat`, `HistoriquePaiementMensuel`, `Emploi`, `ContactDTO.java`, `ImportPage.tsx`, `Contrat`, `GrilleScore`, `SaisieHistoriquePage.tsx`?**
  _High betweenness centrality (0.032) - this node is a cross-community bridge._
- **Should `ModificationContratDemande` be split into smaller, more focused modules?**
  _Cohesion score 0.08246225319396051 - nodes in this community are weakly interconnected._