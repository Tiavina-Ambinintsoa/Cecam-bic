# Graph Report - BIC  (2026-10-21)

## Corpus Check
- cluster-only mode — file stats not available

## Summary
- 1377 nodes · 3177 edges · 125 communities (49 shown, 76 thin omitted)
- Extraction: 96% EXTRACTED · 4% INFERRED · 0% AMBIGUOUS · INFERRED: 139 edges (avg confidence: 0.85)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `6af1cf8d`
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
- 20261008_link_contract_clients.sql

## God Nodes (most connected - your core abstractions)
1. `Contrat` - 85 edges
2. `RapportService` - 56 edges
3. `Client` - 45 edges
4. `Echeance` - 44 edges
5. `ScoreService` - 37 edges
6. `ContratService` - 31 edges
7. `react` - 30 edges
8. `Button()` - 28 edges
9. `RapportSolvabiliteResponse` - 26 edges
10. `ClientForm()` - 26 edges

## Surprising Connections (you probably didn't know these)
- `BienLeasing` --references--> `Contrat`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/contrat/BienLeasing.java → bic-backend/src/main/java/mg/cecam/bic/contrat/Contrat.java
- `Contrat` --references--> `Client`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/contrat/Contrat.java → bic-backend/src/main/java/mg/cecam/bic/client/Client.java
- `Contrat` --references--> `DernierContrat`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/contrat/Contrat.java → bic-backend/src/main/java/mg/cecam/bic/contrat/DernierContrat.java
- `ContratLien` --references--> `Contrat`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/contrat/ContratLien.java → bic-backend/src/main/java/mg/cecam/bic/contrat/Contrat.java
- `ContratRepository` --references--> `Contrat`  [EXTRACTED]
  bic-backend/src/main/java/mg/cecam/bic/contrat/ContratRepository.java → bic-backend/src/main/java/mg/cecam/bic/contrat/Contrat.java

## Import Cycles
- None detected.

## Communities (125 total, 76 thin omitted)

### Community 0 - "ScoreService"
Cohesion: 0.11
Nodes (26): App(), AppShell(), pageTransition, RapportPageRoute(), AdminRoute(), ProtectedRoute(), HomeMenu(), Tache (+18 more)

### Community 1 - "SecurityConfig.java"
Cohesion: 0.10
Nodes (29): ChampsObligatoiresNote(), affichageVersIso(), DateInput, DateInputProps, isoVersAffichage(), Input(), formatAffichage(), MontantInput (+21 more)

### Community 2 - "App.tsx"
Cohesion: 0.06
Nodes (31): rapportApi, ProblemDetail, RapportPage(), telechargerEtVisualiserPdf(), Adresse, AffaireIndividuelle, Alerte, BienLeasing (+23 more)

### Community 3 - "ModificationContratDemande"
Cohesion: 0.13
Nodes (31): Button(), Card(), CardContent(), CardHeader(), CardTitle(), Label(), useToast(), CreerAgentPage() (+23 more)

### Community 4 - "JwtAuthenticationFilter.java"
Cohesion: 0.10
Nodes (5): AuditController, JournalAudit, JournalAuditRepository, AlerteController, AlerteService

### Community 5 - "ContratForm.tsx"
Cohesion: 0.15
Nodes (5): BienLeasingDTO, DetailContratDTO, GarantieDTO, LienClientDTO, RapportService

### Community 6 - "RapportSolvabiliteResponse"
Cohesion: 0.06
Nodes (15): AdresseDTO, AffaireIndividuelleDTO, AlerteDTO, ConsultationDTO, ContactDTO, DernierContratDTO, DetailDemandeDTO, EmploiDTO (+7 more)

### Community 7 - "MiseAJourDemandePage.tsx"
Cohesion: 0.17
Nodes (4): ModificationContratResponse, ModificationContratController, RefusRequest, ModificationContratService

### Community 8 - "JournalAudit"
Cohesion: 0.15
Nodes (6): CalendrierService, CalendrierCreditDTO, CelluleMoisDTO, EncoursCategorieDTO, LigneAnneeDTO, LigneFinancementDTO

### Community 9 - "rapport.ts"
Cohesion: 0.15
Nodes (3): UtilisateurRequest, UtilisateurResponse, UtilisateurController

### Community 11 - "lombok.RequiredArgsConstructor"
Cohesion: 0.16
Nodes (3): AuditService, ModificationContratRequest, EcheanceController

### Community 12 - "GestionUtilisateursPage.tsx"
Cohesion: 0.10
Nodes (13): PhaseDemande, ABANDONNE, ACTIF, DEMANDE_EN_COURS, FERME, REFUSE, StatutEcheance, A_VENIR (+5 more)

### Community 13 - "org.springframework.http.ResponseEntity"
Cohesion: 0.15
Nodes (5): BienLeasingHistoriqueRequest, ClientLieHistoriqueRequest, GarantieHistoriqueRequest, HistoriqueContratRequest, HistoriquePaiementMensuelRequest

### Community 14 - "bigdecimal"
Cohesion: 0.19
Nodes (18): buttonVariants, SelectContent(), SelectItem(), SelectScrollDownButton(), SelectScrollUpButton(), SelectTrigger(), SelectValue(), formatDateFr() (+10 more)

### Community 16 - ".toDetailContrat"
Cohesion: 0.10
Nodes (4): RoleClient, CO_TITULAIRE, GARANT, TITULAIRE

### Community 17 - "Contrat.java"
Cohesion: 0.11
Nodes (21): name, private, type, version, class-variance-authority, eslint, @eslint/js, eslint-plugin-react-hooks (+13 more)

### Community 18 - "ImportPage.tsx"
Cohesion: 0.13
Nodes (18): authApi, LoginResponse, axiosClient, clientApi, ClientEnregistrementResponse, ClientLieLigne, defaults, EcheanceLigne (+10 more)

### Community 19 - "components.json"
Cohesion: 0.20
Nodes (11): utilisateurApi, UtilisateurRequest, UtilisateurResponse, CardDescription(), ToastContext, ToastContextValue, ToastItem, @base-ui/react (+3 more)

### Community 21 - "package.json"
Cohesion: 0.17
Nodes (4): ScoreResult, Observation, RevenuEvaluation, ScoreService

### Community 22 - "compilerOptions"
Cohesion: 0.15
Nodes (3): Echeance, EcheanceRepository, HistoriquePaiementLigneDTO

### Community 23 - "RapportService"
Cohesion: 0.09
Nodes (21): aliases, components, hooks, lib, ui, utils, iconLibrary, menuAccent (+13 more)

### Community 25 - "dropdown-menu.tsx"
Cohesion: 0.10
Nodes (20): compilerOptions, allowArbitraryExtensions, allowImportingTsExtensions, erasableSyntaxOnly, jsx, lib, module, moduleDetection (+12 more)

### Community 26 - "Echeance"
Cohesion: 0.10
Nodes (18): adresseSchema, affaireIndividuelleAdresseSchema, affaireIndividuelleContactSchema, affaireIndividuelleIdentifiantSchema, affaireIndividuelleSchema, ClientFormInput, ClientFormValues, clientSchema (+10 more)

### Community 27 - "SaisieHistoriquePage.tsx"
Cohesion: 0.14
Nodes (6): StatutDemandeModification, APPROUVEE, EN_ATTENTE, REFUSEE, ModificationContratDemande, ModificationContratDemandeRepository

### Community 28 - ".construire"
Cohesion: 0.18
Nodes (4): ContratService, ContratRequest, DernierContratRequest, RevenuDemandeRequest

### Community 29 - "dependencies"
Cohesion: 0.25
Nodes (13): contratApi, modificationContratApi, DEVISES, PHASE_LABELS, PHASES, ContratRequest, ContratResponse, DernierContratRequest (+5 more)

### Community 30 - "compilerOptions"
Cohesion: 0.20
Nodes (9): AppBreadcrumb(), segmentsForPath(), DropdownMenu(), DropdownMenuContent(), DropdownMenuItem(), DropdownMenuSub(), DropdownMenuSubContent(), DropdownMenuSubTrigger() (+1 more)

### Community 32 - "EcheanceController.java"
Cohesion: 0.11
Nodes (18): dependencies, axios, @base-ui/react, class-variance-authority, cn, @fontsource-variable/geist, @hookform/resolvers, lucide-react (+10 more)

### Community 36 - "ContratController"
Cohesion: 0.12
Nodes (16): compilerOptions, allowImportingTsExtensions, erasableSyntaxOnly, lib, module, moduleDetection, noEmit, noFallthroughCasesInSwitch (+8 more)

### Community 38 - "devDependencies"
Cohesion: 0.20
Nodes (5): UtilisateurRepository, EvenementNegatifRepository, GarantieRepository, HistoriquePaiementMensuelRepository, GrilleScoreRepository

### Community 45 - "ClientRequest"
Cohesion: 0.23
Nodes (4): CodesEtablissementRequest, ContratController, EcheanceMensuelleRequest, PhaseRequest

### Community 46 - "BienLeasing"
Cohesion: 0.15
Nodes (13): devDependencies, eslint, @eslint/js, eslint-plugin-react-hooks, eslint-plugin-react-refresh, globals, @types/node, @types/react (+5 more)

### Community 47 - "ContratRepository"
Cohesion: 0.17
Nodes (4): Utilisateur, Role, ADMIN, AGENT_CREDIT

### Community 50 - "Emploi"
Cohesion: 0.17
Nodes (4): NatureGarantie, PERSONNELLE, REELLE, Garantie

### Community 52 - ".toScoreDto"
Cohesion: 0.18
Nodes (9): BucketRetard, A_JOUR, JOURS_120_149, JOURS_150_179, JOURS_180_PLUS, JOURS_1_29, JOURS_30_59, JOURS_60_89 (+1 more)

### Community 55 - "CategorieTiers"
Cohesion: 0.18
Nodes (10): AdresseRequest, AffaireIndividuelleAdresseRequest, AffaireIndividuelleContactRequest, AffaireIndividuelleIdentifiantRequest, AffaireIndividuelleRequest, ContactRequest, DocumentIdentificationRequest, EmploiRequest (+2 more)

### Community 63 - "demande_modification_contrat"
Cohesion: 0.25
Nodes (4): ModeRattachement, DEMANDE_EXISTANTE, ENTREPRISE, NOUVELLE_DEMANDE

### Community 66 - "scripts"
Cohesion: 0.39
Nodes (6): GestionUtilisateursPage(), chargerUtilisateurs(), fermerFormulaire(), handleSubmit(), handleSupprimer(), handleToggleActif()

### Community 68 - "tsconfig.json"
Cohesion: 0.48
Nodes (3): Cle, SituationFinanciereMensuelleService, Totaux

### Community 70 - "MotifCloture"
Cohesion: 0.40
Nodes (6): AdresseAffaireIndividuelleRequest, AdresseRequest, AffaireIndividuelleRequest, ClientRequest, EmploiRequest, IdentifiantRequest

### Community 71 - "RoleClient"
Cohesion: 0.40
Nodes (4): ClientResponse, Genre, FEMME, HOMME

### Community 74 - "PageShell.tsx"
Cohesion: 0.40
Nodes (4): OrigineDonnee, DONNEE_FICTIVE, NON_RENSEIGNEE, RAPPORT_CRIF

### Community 75 - "ImportLigneDTO.java"
Cohesion: 0.60
Nodes (3): demande_modification_contrat, idx_demande_modification_statut_date, uq_demande_modification_contrat_en_attente

### Community 76 - ".rechercher"
Cohesion: 0.60
Nodes (3): adresse_affaire_individuelle, affaire_individuelle, idx_adresse_affaire_individuelle_parent

### Community 77 - ".enregistrerPaiement"
Cohesion: 0.40
Nodes (5): scripts, build, dev, lint, preview

### Community 78 - "ContactDTO.java"
Cohesion: 0.70
Nodes (5): useIdleLogout(), effacerMinuteur(), enregistrerActivite(), programmerExpiration(), surChangementStockage()

### Community 79 - "GrilleScoreDTO.java"
Cohesion: 0.40
Nodes (4): compilerOptions, paths, files, references

### Community 80 - "RepartitionLigneDTO.java"
Cohesion: 0.40
Nodes (3): @tailwindcss/vite, vite, @vitejs/plugin-react

### Community 81 - "ScoreDTO.java"
Cohesion: 0.50
Nodes (3): MotifCloture, FIN_A_TERME, FIN_ANTICIPEE

## Knowledge Gaps
- **46 isolated node(s):** `A_VENIR`, `EN_RETARD`, `IMPAYE`, `PAYE_A_TEMPS`, `ABANDONNE` (+41 more)
  These have ≤1 connection - possible missing edges. (Counts symbols only; 557 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **76 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `Contrat` connect `localdate` to `ContratForm.tsx`, `MiseAJourDemandePage.tsx`, `JournalAudit`, `GestionUtilisateursPage.tsx`, `org.springframework.http.ResponseEntity`, `.toDetailContrat`, `package.json`, `compilerOptions`, `SaisieHistoriquePage.tsx`, `.construire`, `list`, `.historiquePaiement`, `AdresseAffaireIndividuelle`, `ClientRequest`, `org.springframework.data.jpa.repository.JpaRepository`, `Emploi`, `Identifiant`, `GrilleScore`, `GestionUtilisateursPage`, `tsconfig.json`?**
  _High betweenness centrality (0.067) - this node is a cross-community bridge._
- **What connects `A_VENIR`, `EN_RETARD`, `IMPAYE` to the rest of the system?**
  _46 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `ScoreService` be split into smaller, more focused modules?**
  _Cohesion score 0.1076923076923077 - nodes in this community are weakly interconnected._
- **Why does `Client` connect `list` to `.historiquePaiement`, `vite.config.ts`, `ContratForm.tsx`, `AffaireIndividuelle`, `Utilisateur`, `rapport.ts`, `localdate`, `org.springframework.http.ResponseEntity`, `ClientService`, `HistoriquePaiementMensuel`, `.toDetailContrat`, `demande_modification_contrat`, `axiosClient.ts`, `package.json`, `PhaseDemande`, `BicBackendApplication.java`, `Client`?**
  _High betweenness centrality (0.059) - this node is a cross-community bridge._
- **Should `SecurityConfig.java` be split into smaller, more focused modules?**
  _Cohesion score 0.10256410256410256 - nodes in this community are weakly interconnected._
- **Why does `RapportService` connect `ContratForm.tsx` to `.historiquePaiement`, `Garantie`, `JwtAuthenticationFilter.java`, `useIdleLogout`, `devDependencies`, `tsconfig.json`, `JournalAudit`, `localdate`, `lombok.RequiredArgsConstructor`, `AdresseAffaireIndividuelle`, `org.springframework.http.ResponseEntity`, `.toDetailContrat`, `ClientRepository`, `compilerOptions`, `package.json`, `GrilleScore`, `GestionUtilisateursPage`?**
  _High betweenness centrality (0.020) - this node is a cross-community bridge._
- **Should `App.tsx` be split into smaller, more focused modules?**
  _Cohesion score 0.05832147937411095 - nodes in this community are weakly interconnected._