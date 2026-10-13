package mg.cecam.bic.rapport;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.audit.AuditService;
import mg.cecam.bic.audit.JournalAuditRepository;
import mg.cecam.bic.client.Client;
import mg.cecam.bic.client.AffaireIndividuelle;
import mg.cecam.bic.client.Emploi;
import mg.cecam.bic.client.EmploiRepository;
import mg.cecam.bic.common.enums.NatureGarantie;
import mg.cecam.bic.common.enums.BucketRetard;
import mg.cecam.bic.common.enums.OrigineDonnee;
import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.RoleClient;
import mg.cecam.bic.common.enums.StatutEcheance;
import mg.cecam.bic.common.util.LabelMapper;
import mg.cecam.bic.common.util.ScoreColorMapper;
import mg.cecam.bic.contrat.*;
import mg.cecam.bic.rapport.dto.*;
import mg.cecam.bic.referentiel.GrilleScore;
import mg.cecam.bic.referentiel.GrilleScoreRepository;
import mg.cecam.bic.score.ScoreResult;
import mg.cecam.bic.score.ScoreService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RapportService {
    private static final Set<PhaseDemande> DECAISSEES = EnumSet.of(PhaseDemande.ACTIF, PhaseDemande.FERME);
    private static final Set<PhaseDemande> NON_DECAISSEES =
            EnumSet.of(PhaseDemande.DEMANDE_EN_COURS, PhaseDemande.REFUSE, PhaseDemande.ABANDONNE);

    private static final String[] MOIS_LONG = {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
            "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"};

    private static final String CAT_AVEC_ECHEANCIER = "Financements avec Échéancier";
    private static final String CAT_SANS_ECHEANCIER = "Financements sans Échéancier";
    private static final String CAT_CARTES          = "Cartes de Crédit";
    private static final String CAT_SERVICES        = "Services";

    private final ContratRepository contratRepository;
    private final EcheanceRepository echeanceRepository;
    private final HistoriquePaiementMensuelRepository historiquePaiementMensuelRepository;
    private final EmploiRepository emploiRepository;
    private final GarantieRepository garantieRepository;
    private final BienLeasingRepository bienLeasingRepository;
    private final ContratLienRepository contratLienRepository;
    private final ScoreService scoreService;
    private final CalendrierService calendrierService;
    private final AlerteService alerteService;
    private final GrilleScoreRepository grilleScoreRepository;
    private final AuditService auditService;
    private final JournalAuditRepository journalAuditRepository;
    private final SituationFinanciereMensuelleService situationFinanciereMensuelleService;

    @Transactional
    public RapportSolvabiliteResponse construire(Long contratId) {
        Contrat contrat = contratRepository.findById(contratId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Contrat introuvable : " + contratId));
        Client client = contrat.getClient();
        LocalDate reference = LocalDate.now();

        auditService.enregistrer("CLIENT", client.getId(), "CONSULTATION", "Consultation du rapport de solvabilité");

        List<Contrat> tous = contratRepository.findPourClient(client.getId());
        List<Contrat> historique = tous.stream()
                .filter(c -> !c.getId().equals(contratId))
                .sorted(Comparator.comparing(Contrat::getDateDemande,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        List<Long> ids = tous.stream().map(Contrat::getId).toList();
        Map<Long, List<Echeance>> echeances = new HashMap<>();
        for (Contrat c : tous) {
            echeances.put(c.getId(), echeanceRepository.findByContrat_IdOrderByNumeroEcheance(c.getId()));
        }
        Map<Long, List<Garantie>> garanties = new HashMap<>();
        for (Contrat c : tous) {
            garanties.put(c.getId(), garantieRepository.findByContrat_Id(c.getId()));
        }
        Map<Long, BienLeasing> leasings = bienLeasingRepository.findByContrat_IdIn(ids).stream()
                .collect(Collectors.toMap(b -> b.getContrat().getId(), Function.identity(), (a, b) -> a));
        Map<Long, List<ContratLien>> liensParContrat = contratLienRepository.findByContrat_IdIn(ids).stream()
                .collect(Collectors.groupingBy(l -> l.getContrat().getId()));

        Emploi emploi = emploiRepository.findByClient_Id(client.getId()).orElse(null);
        if (emploi != null && !emploi.estRenseigne()) emploi = null;

        // [CORRIGÉ] "clientTrouve" n'existait nulle part : c'est le même booléen que celui déjà
        // utilisé par statutClient(...) plus bas, je l'ai factorisé dans clientPreexistant(...)
        // pour ne calculer la règle qu'à un seul endroit.
        boolean clientTrouve = clientPreexistant(client, contrat, historique);
        ScoreResult scoreResult = scoreService.calculer(client, contrat, clientTrouve);

        // [RÉINTÉGRÉ] ce bloc était à la fin du fichier collé, EN DEHORS de toute méthode
        // (après la fermeture de tiret()), et référençait client/journalAuditRepository qui
        // n'existent pas à portée de classe. Il a tout l'air d'une variable locale de
        // construire() oubliée en dehors : je l'ai remise ici, avec le DTO ConsultationDTO et
        // le champ RapportSolvabiliteResponse.historiqueConsultations créés en complément.
        List<ConsultationDTO> historiqueConsultations = journalAuditRepository
                .findByEntiteAndEntiteIdOrderByDateEvenementDesc("CLIENT", client.getId()).stream()
                .filter(a -> a.getAction().equals("CREATION") || a.getAction().equals("CONSULTATION"))
                .map(a -> new ConsultationDTO("001", a.getDateEvenement().toLocalDate(),
                        a.getAction().equals("CREATION") ? "Nouvelle Demande" : "Suivi Client"))
                .toList();

        return new RapportSolvabiliteResponse(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                statutClient(clientTrouve),
                client.getCodeClientCb(),
                toClientInfo(client),
                contacts(client),
                adresses(client, true),
                adresses(client, false),
                toAffaireIndividuelleDTO(client.getAffaireIndividuelle()),
                client.getIdentifiants().stream()
                        .map(i -> new IdentifiantDTO(i.getTypeIdentifiant(), i.getNumero())).toList(),
                toDetailDemande(contrat),
                emploi == null ? null : new EmploiDTO(emploi.getStatutEmploi(), emploi.getNomEmployeur(),
                        emploi.getProfession(), emploi.getDateEmbauche(),
                        emploi.getRevenuAnnuelTotal(), emploi.getDevise()),
                tousLesLiens(historique, liensParContrat, garanties, client),
                alerteService.listerParClient(client.getId(), reference),
                // [CORRIGÉ] c'était toScoreDto(score) alors que la variable s'appelle scoreResult
                toScoreDto(scoreResult),
                grille(),
                synthese(historique, echeances, garanties, reference),
                syntheseParCategorie(historique, echeances, reference),
                encoursParCategorie(historique, echeances, reference),
                impayesParCategorie(historique, echeances, reference),
                situationFinanciereMensuelleService.calculer(tous, echeances, reference),
                financements(historique, echeances, liensParContrat, NON_DECAISSEES, false, reference),
                financements(historique, echeances, liensParContrat, DECAISSEES, true, reference),
                detailContrats(historique, echeances, garanties, leasings, liensParContrat, reference),
                historiqueConsultations
        );
    }


    private SyntheseDTO synthese(List<Contrat> historique,
                                 Map<Long, List<Echeance>> echeances,
                                 Map<Long, List<Garantie>> garanties,
                                 LocalDate reference) {

        BigDecimal restantDu = BigDecimal.ZERO;  
        BigDecimal impayes = BigDecimal.ZERO;     

        for (Contrat c : historique) {
            if (c.getPhaseDemande() != PhaseDemande.ACTIF) continue;
            for (Echeance e : echeances.getOrDefault(c.getId(), List.of())) {
                if (e.estPayee()) continue;
                if (e.estEnSouffrance(reference)) impayes = impayes.add(e.getMontantDu());
                else restantDu = restantDu.add(e.getMontantDu());
            }
        }

        BigDecimal demandes = historique.stream()
                .filter(c -> c.getPhaseDemande() == PhaseDemande.DEMANDE_EN_COURS)
                .map(Contrat::totalDu)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal garantieSignature = historique.stream()
                .filter(c -> c.getPhaseDemande() != PhaseDemande.FERME)
                .flatMap(c -> garanties.getOrDefault(c.getId(), List.<Garantie>of()).stream())
                .filter(g -> g.getNature() == NatureGarantie.PERSONNELLE)
                .map(g -> g.getMontantCouvert() == null ? BigDecimal.ZERO : g.getMontantCouvert())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal exposition = restantDu.add(garantieSignature);

        int etablissements = (int) historique.stream()
                .map(Contrat::getCodeEtablissementDeclarant)
                .filter(Objects::nonNull).distinct().count();

        boolean vide = historique.isEmpty();
        Set<String> devises = historique.stream().map(Contrat::getDevise)
                .filter(this::deviseRenseignee).collect(Collectors.toCollection(TreeSet::new));
        long contratsSansDevise = historique.stream().filter(c -> !deviseRenseignee(c.getDevise())).count();
        boolean totauxCompatibles = devises.size() == 1 && contratsSansDevise == 0;
        String devise = totauxCompatibles ? devises.iterator().next()
                : vide ? "-" : "Devises multiples/incomplètes";

        return new SyntheseDTO(
                historique.size(), etablissements,
                vide ? "-" : "Non", devise,
                totauxCompatibles ? exposition : null,
                totauxCompatibles ? restantDu : null,
                totauxCompatibles ? impayes : null,
                totauxCompatibles ? demandes : null,
                totauxCompatibles ? garantieSignature : null,
                repartition(historique));
    }

    private boolean deviseRenseignee(String devise) {
        return devise != null && !devise.isBlank();
    }

    private List<RepartitionLigneDTO> repartition(List<Contrat> historique) {
        Map<String, Map<PhaseDemande, Long>> parCategorie = historique.stream()
                .collect(Collectors.groupingBy(this::categorieDe,
                        Collectors.groupingBy(Contrat::getPhaseDemande, Collectors.counting())));

        return Stream.of(CAT_AVEC_ECHEANCIER, CAT_SANS_ECHEANCIER, CAT_CARTES, CAT_SERVICES)
                .map(cat -> {
                    Map<PhaseDemande, Long> p = parCategorie.getOrDefault(cat, Map.of());
                    return new RepartitionLigneDTO(cat,
                            p.getOrDefault(PhaseDemande.DEMANDE_EN_COURS, 0L),
                            p.getOrDefault(PhaseDemande.REFUSE, 0L),
                            p.getOrDefault(PhaseDemande.ABANDONNE, 0L),
                            p.getOrDefault(PhaseDemande.ACTIF, 0L),
                            p.getOrDefault(PhaseDemande.FERME, 0L));
                })
                .toList();
    }

    private String categorieDe(Contrat c) {
        String t = c.getTypeContrat() == null ? "" : c.getTypeContrat().toLowerCase();
        if (t.contains("découvert") || t.contains("decouvert")) return CAT_SANS_ECHEANCIER;
        if (t.contains("carte")) return CAT_CARTES;
        if (t.contains("service") || t.contains("abonnement")) return CAT_SERVICES;
        return CAT_AVEC_ECHEANCIER;
    }

    private List<SyntheseCategorieDTO> syntheseParCategorie(List<Contrat> historique,
                                                            Map<Long, List<Echeance>> echeances,
                                                            LocalDate reference) {
        List<Contrat> actifs = historique.stream()
                .filter(c -> c.getPhaseDemande() == PhaseDemande.ACTIF)
                .filter(c -> CAT_AVEC_ECHEANCIER.equals(categorieDe(c)))
                .toList();

        Predicate<Echeance> aEchoir = e -> !e.estPayee() && !e.estEnSouffrance(reference);
        Predicate<Echeance> enSouffrance = e -> e.estEnSouffrance(reference);

        List<SyntheseCategorieDTO> parDevise = new ArrayList<>();
        List<String> devises = actifs.stream().map(Contrat::getDevise).filter(this::deviseRenseignee)
                .distinct().sorted().toList();
        for (String devise : devises) {
            List<Contrat> contratsDansDevise = actifs.stream()
                    .filter(c -> Objects.equals(c.getDevise(), devise)).toList();
            parDevise.add(new SyntheseCategorieDTO(
                    "Financements avec échéanciers", devise, contratsDansDevise.size(), "Montant Échéance Mensuelle",
                    somme(contratsDansDevise, false, Contrat::getMontantEcheanceMensuelle),
                    somme(contratsDansDevise, true,  Contrat::getMontantEcheanceMensuelle),
                    sommeEcheances(contratsDansDevise, false, echeances, aEchoir),
                    sommeEcheances(contratsDansDevise, true,  echeances, aEchoir),
                    sommeEcheances(contratsDansDevise, false, echeances, enSouffrance),
                    sommeEcheances(contratsDansDevise, true,  echeances, enSouffrance)));
        }
        long contratsSansDevise = actifs.stream().filter(c -> !deviseRenseignee(c.getDevise())).count();
        if (contratsSansDevise > 0) {
            parDevise.add(new SyntheseCategorieDTO("Financements avec échéanciers", "Devise non renseignée",
                    (int) contratsSansDevise, "Montant Échéance Mensuelle",
                    null, null, null, null, null, null));
        }
        if (parDevise.isEmpty()) {
            parDevise.add(SyntheseCategorieDTO.vide(
                    "Financements avec échéanciers", "Montant Échéance Mensuelle"));
        }

        parDevise.add(SyntheseCategorieDTO.vide("Financements sans échéanciers", "Plafond de Crédit"));
        parDevise.add(SyntheseCategorieDTO.vide("Cartes de Crédit", "Plafond de Crédit"));
        parDevise.add(SyntheseCategorieDTO.vide("Services", "Montant Facture"));
        return List.copyOf(parDevise);
    }

    private BigDecimal somme(List<Contrat> contrats, boolean garant, Function<Contrat, BigDecimal> extracteur) {
        return contrats.stream()
                .filter(c -> (c.getRoleClient() == RoleClient.GARANT) == garant)
                .map(c -> extracteur.apply(c) == null ? BigDecimal.ZERO : extracteur.apply(c))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sommeEcheances(List<Contrat> contrats, boolean garant,
                                      Map<Long, List<Echeance>> echeances, Predicate<Echeance> filtre) {
        return contrats.stream()
                .filter(c -> (c.getRoleClient() == RoleClient.GARANT) == garant)
                .flatMap(c -> echeances.getOrDefault(c.getId(), List.<Echeance>of()).stream())
                .filter(filtre)
                .map(Echeance::getMontantDu)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }


    private List<EncoursCategorieDTO> encoursParCategorie(List<Contrat> historique,
                                                          Map<Long, List<Echeance>> echeances,
                                                          LocalDate reference) {
        List<Contrat> avecEcheancier = historique.stream()
                .filter(c -> DECAISSEES.contains(c.getPhaseDemande()))
                .filter(c -> !echeances.getOrDefault(c.getId(), List.of()).isEmpty())
                .toList();
        if (avecEcheancier.isEmpty()) return List.of();

        List<EncoursCategorieDTO> resultats = new ArrayList<>();
        for (String categorie : List.of(CAT_AVEC_ECHEANCIER, CAT_SANS_ECHEANCIER, CAT_CARTES, CAT_SERVICES)) {
            List<Contrat> contratsCategorie = avecEcheancier.stream()
                    .filter(c -> categorie.equals(categorieDe(c)))
                    .toList();
            List<String> etablissements = contratsCategorie.stream()
                    .map(Contrat::getCodeEtablissementDeclarant)
                    .distinct()
                    .sorted(Comparator.nullsLast(Comparator.naturalOrder()))
                    .toList();

            for (String etablissement : etablissements) {
                List<Contrat> contratsEtablissement = contratsCategorie.stream()
                        .filter(c -> Objects.equals(c.getCodeEtablissementDeclarant(), etablissement))
                        .toList();
                List<String> devises = contratsEtablissement.stream().map(Contrat::getDevise).distinct()
                        .sorted(Comparator.nullsLast(Comparator.naturalOrder())).toList();
                for (String devise : devises) {
                    List<Contrat> contratsGroupe = contratsEtablissement.stream()
                            .filter(c -> Objects.equals(c.getDevise(), devise)).toList();
                    EncoursCategorieDTO dto = calendrierService.encours(
                            categorie, etablissement, devise, contratsGroupe, echeances, reference);
                    if (!dto.lignes().isEmpty()) resultats.add(dto);
                }
            }
        }
        return List.copyOf(resultats);
    }

    private List<EncoursCategorieDTO> impayesParCategorie(List<Contrat> historique,
                                                          Map<Long, List<Echeance>> echeances,
                                                          LocalDate reference) {
        List<Contrat> avecEcheancier = historique.stream()
                .filter(c -> DECAISSEES.contains(c.getPhaseDemande()))
                .filter(c -> !echeances.getOrDefault(c.getId(), List.of()).isEmpty())
                .toList();
        if (avecEcheancier.isEmpty()) return List.of();

        List<EncoursCategorieDTO> resultats = new ArrayList<>();
        for (String categorie : List.of(CAT_AVEC_ECHEANCIER, CAT_SANS_ECHEANCIER, CAT_CARTES, CAT_SERVICES)) {
            List<Contrat> contratsCategorie = avecEcheancier.stream()
                    .filter(c -> categorie.equals(categorieDe(c)))
                    .toList();
            List<String> etablissements = contratsCategorie.stream()
                    .map(Contrat::getCodeEtablissementDeclarant)
                    .distinct()
                    .sorted(Comparator.nullsLast(Comparator.naturalOrder()))
                    .toList();

            for (String etablissement : etablissements) {
                List<Contrat> contratsEtablissement = contratsCategorie.stream()
                        .filter(c -> Objects.equals(c.getCodeEtablissementDeclarant(), etablissement))
                        .toList();
                List<String> devises = contratsEtablissement.stream().map(Contrat::getDevise).distinct()
                        .sorted(Comparator.nullsLast(Comparator.naturalOrder())).toList();
                for (String devise : devises) {
                    List<Contrat> contratsGroupe = contratsEtablissement.stream()
                            .filter(c -> Objects.equals(c.getDevise(), devise)).toList();
                    EncoursCategorieDTO dto = calendrierService.impayes(
                            categorie, etablissement, devise, contratsGroupe, echeances, reference);
                    if (!dto.lignes().isEmpty()) resultats.add(dto);
                }
            }
        }
        return List.copyOf(resultats);
    }

    private List<LigneFinancementDTO> financements(List<Contrat> historique,
                                                   Map<Long, List<Echeance>> echeances,
                                                   Map<Long, List<ContratLien>> liens,
                                                   Set<PhaseDemande> phases,
                                                   boolean avecGrille,
                                                   LocalDate reference) {
        List<Contrat> retenus = historique.stream()
                .filter(c -> phases.contains(c.getPhaseDemande()))
                .filter(c -> CAT_AVEC_ECHEANCIER.equals(categorieDe(c)))
                .sorted(Comparator.comparing(Contrat::getDateDemande,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();

        List<LigneFinancementDTO> lignes = new ArrayList<>();
        int i = 1;
        for (Contrat c : retenus) {
            List<HistoriquePaiementMensuel> histo = avecGrille
                    ? historiquePaiementMensuelRepository.findByContrat_IdOrderByMoisDesc(c.getId())
                    : List.of();

            lignes.add(new LigneFinancementDTO(
                    i++, c.getCodeContratCb(), tiret(c.getCodeContratEtablissement()),
                    c.getTypeContrat(),
                    LabelMapper.phase(c.getPhaseDemande(), c.getMotifCloture()),
                    LabelMapper.role(c.getRoleClient()),
                    c.getCodeEtablissementDeclarant(),
                    liensDe(liens.getOrDefault(c.getId(), List.of())),
                    c.getDateDemande(), c.getMontantFinance(), c.getMontantEcheanceMensuelle(),
                    c.getNombreTotalEcheances(), tiret(c.getPeriodicitePaiement()),
                    c.getDateDerniereModification(),
                    c.getDateDebutContrat(), c.getDateFinContrat(), tiret(c.getNote()),
                    avecGrille
                            ? calendrierService.grilleStatut(c, echeances.getOrDefault(c.getId(), List.of()), histo, reference)
                            : List.of()));
        }
        return lignes;
    }

    private List<DetailContratDTO> detailContrats(List<Contrat> historique,
                                                  Map<Long, List<Echeance>> echeances,
                                                  Map<Long, List<Garantie>> garanties,
                                                  Map<Long, BienLeasing> leasings,
                                                  Map<Long, List<ContratLien>> liens,
                                                  LocalDate reference) {
        return historique.stream()
                .sorted(Comparator
                        .comparingInt((Contrat c) -> ordrePhase(c.getPhaseDemande()))
                        .thenComparing(Contrat::getDateDemande,
                                Comparator.nullsLast(Comparator.reverseOrder())))
                .map(c -> toDetailContrat(c,
                        echeances.getOrDefault(c.getId(), List.of()),
                        garanties.getOrDefault(c.getId(), List.of()),
                        leasings.get(c.getId()),
                        liens.getOrDefault(c.getId(), List.of()),
                        reference))
                .toList();
    }

    private DetailContratDTO toDetailContrat(Contrat c, List<Echeance> echeances,
                                             List<Garantie> garanties, BienLeasing leasing,
                                             List<ContratLien> liens, LocalDate reference) {

        List<HistoriquePaiementLigneDTO> historiquePaiement = historiquePaiement(c, echeances, reference);

        List<Echeance> aEchoir = echeances.stream()
                .filter(e -> !e.estPayee() && !e.estEnSouffrance(reference)).toList();
        List<Echeance> enSouffrance = echeances.stream()
                .filter(e -> e.estEnSouffrance(reference)).toList();

        BigDecimal restantDu = c.getMontantRestantDu() != null ? c.getMontantRestantDu() : somme(aEchoir);
        BigDecimal montantImpayes = c.getMontantImpayes() != null ? c.getMontantImpayes() : somme(enSouffrance);

        Echeance prochaine = echeances.stream()
                .filter(e -> !e.estPayee())
                .min(Comparator.comparing(Echeance::getDateEcheance)).orElse(null);

        LocalDate dernierReglementCalcule = echeances.stream().filter(Echeance::estPayee)
                .map(Echeance::getDatePaiement).filter(Objects::nonNull)
                .max(Comparator.naturalOrder()).orElse(null);

        int maxJours = echeances.stream().mapToInt(e -> e.joursDeRetard(reference)).max().orElse(0);
        LocalDate dateMaxJours = maxJours > 0
                ? echeances.stream().filter(e -> e.joursDeRetard(reference) == maxJours)
                        .map(Echeance::getDateEcheance).max(Comparator.naturalOrder()).orElse(null)
                : dateDerniereDeclaration(c, reference);
        int joursCourants = enSouffrance.stream().mapToInt(e -> e.joursDeRetard(reference)).max().orElse(0);

        String pireStatut;
        LocalDate datePireStatut = null;
        Optional<Echeance> pire = echeances.stream()
                .filter(e -> !e.getDateEcheance().isAfter(reference))
                .max(Comparator.comparingInt(e -> rangStatut(e.statutEffectif(reference))));
        if (c.getPireStatut() != null && !c.getPireStatut().isBlank()) {
            pireStatut = c.getPireStatut();
            datePireStatut = c.getDatePireStatut();
        } else if (pire.isEmpty()) {
            pireStatut = "No info";
        } else {
            StatutEcheance s = pire.get().statutEffectif(reference);
            if (s == StatutEcheance.PAYE_A_TEMPS) {
                pireStatut = "No info";
            } else {
                pireStatut = LabelMapper.statutEcheance(s);
                datePireStatut = pire.get().getDateEcheance();
            }
        }

        return new DetailContratDTO(
                c.getCodeContratCb(), tiret(c.getCodeContratEtablissement()), c.getCodeEtablissementDeclarant(),
                tiret(c.getCodeEtablissementCrypte() != null ? c.getCodeEtablissementCrypte() : c.getCodeEtablissementDeclarant()),
                c.getTypeContrat(), LabelMapper.phase(c.getPhaseDemande(), c.getMotifCloture()),
                LabelMapper.role(c.getRoleClient()), c.getDevise(),
                c.getDateDemande(), c.getDateDebutContrat(), c.getDateFinContrat(),
                c.getDatePremiereEcheance(), c.getDateDerniereModificationSource() != null
                        ? c.getDateDerniereModificationSource().atStartOfDay() : c.getDateDerniereModification(),
                c.getMontantFinance(), c.totalDu(), c.getMontantEcheanceMensuelle(),
                c.getNombreTotalEcheances(), tiret(c.getPeriodicitePaiement()), tiret(c.getModeReglement()),
                c.getMontantProchaineEcheance() != null ? c.getMontantProchaineEcheance()
                        : prochaine == null ? BigDecimal.ZERO : prochaine.getMontantDu(),
                c.getDateProchaineEcheance() != null ? c.getDateProchaineEcheance()
                        : prochaine == null ? null : prochaine.getDateEcheance(),
                c.getDateDernierReglement() != null ? c.getDateDernierReglement() : dernierReglementCalcule,
                historiquePaiement,
                c.getNombreEcheancesRestantes() != null ? c.getNombreEcheancesRestantes() : aEchoir.size(),
                restantDu,
                c.getNombreEcheancesImpayees() != null ? c.getNombreEcheancesImpayees() : enSouffrance.size(),
                montantImpayes,
                c.getMaxMontantImpayes() != null ? c.getMaxMontantImpayes() : montantImpayes,
                c.getMaxNombreEcheancesImpayees() != null ? c.getMaxNombreEcheancesImpayees() : enSouffrance.size(),
                c.getDateMaxNombreEcheancesImpayees() != null ? c.getDateMaxNombreEcheancesImpayees()
                        : enSouffrance.isEmpty() ? null : enSouffrance.get(0).getDateEcheance(),
                c.getNombreJoursRetard() != null ? c.getNombreJoursRetard() : joursCourants,
                c.getMaxNombreJoursRetard() != null ? c.getMaxNombreJoursRetard() : maxJours,
                c.getDateMaxNombreJoursRetard() != null ? c.getDateMaxNombreJoursRetard() : dateMaxJours,
                pireStatut, datePireStatut,
                tiret(c.getCodeRestructuration()), c.getDateCodeRestructuration(),
                tiret(c.getContratOrigine()), tiret(c.getNouveauContrat()), tiret(c.getNote()),
                toLeasingDto(leasing),
                liensDe(liens),
                garanties.stream().map(this::toGarantieDto).toList(),
                libelleOrigineDonnee(c.getOrigineDonnee()));
    }

    private List<HistoriquePaiementLigneDTO> historiquePaiement(Contrat c, List<Echeance> echeances,
                                                                LocalDate reference) {
        List<HistoriquePaiementMensuel> source = historiquePaiementMensuelRepository
                .findByContrat_IdOrderByMoisDesc(c.getId());
        if (!source.isEmpty()) {
            return source.stream().map(h -> {
                BucketRetard bucket = BucketRetard.depuisJours(h.getJoursDeRetard());
                if (bucket == null) bucket = BucketRetard.depuisStatut(h.getStatut());
                return new HistoriquePaiementLigneDTO(
                        h.getMois().getYear(), MOIS_LONG[h.getMois().getMonthValue() - 1],
                        h.getNombreEcheances(), h.getNombreEcheancesImpayees(), h.getJoursDeRetard(),
                        h.getStatut(), bucket == null ? null : bucket.libelle(), h.getDetailsSource());
            }).toList();
        }

        var debut = calendrierService.moisDebutDeclaration(c);
        var fin = calendrierService.moisFinDeclaration(c, reference);
        if (debut == null || fin == null || debut.isAfter(fin)) return List.of();

        List<HistoriquePaiementLigneDTO> lignes = new ArrayList<>();
        var courant = fin;
        while (!courant.isBefore(debut)) {
            LocalDate finDeMois = courant.atEndOfMonth();
            LocalDate observation = finDeMois.isAfter(reference) ? reference : finDeMois;

            List<Echeance> exigibles = echeances.stream()
                    .filter(e -> e != null && e.getDateEcheance() != null && !e.getDateEcheance().isAfter(finDeMois))
                    .toList();
            List<Integer> retardsObserves = exigibles.stream()
                    .map(e -> joursRetardObserve(e, observation)).toList();
            boolean historiqueIncomplet = retardsObserves.stream().anyMatch(Objects::isNull);
            Integer jours = exigibles.isEmpty() || historiqueIncomplet ? null
                    : retardsObserves.stream().mapToInt(Integer::intValue).max().orElse(0);
            List<StatutEcheance> statutsObserves = exigibles.stream()
                    .map(e -> statutEcheanceObserve(e, observation)).toList();
            StatutEcheance pire = historiqueIncomplet ? null : statutsObserves.stream()
                    .filter(Objects::nonNull)
                    .max(Comparator.comparingInt(this::rangStatut)).orElse(null);
            int impayees = (int) statutsObserves.stream().filter(s -> s == StatutEcheance.IMPAYE).count();

            lignes.add(new HistoriquePaiementLigneDTO(
                    courant.getYear(), MOIS_LONG[courant.getMonthValue() - 1],
                    null, impayees, jours, pire == null ? (exigibles.isEmpty() ? "NOUV" : "NDIS") : LabelMapper.statutEcheance(pire),
                    jours == null ? null : Optional.ofNullable(BucketRetard.depuisJours(jours))
                            .map(BucketRetard::libelle).orElse(null), null));
            courant = courant.minusMonths(1);
        }
        return lignes;
    }

    private Integer joursRetardObserve(Echeance echeance, LocalDate observation) {
        if (echeance.estPayee() && echeance.getDatePaiement() == null) return null;
        LocalDate fin = echeance.estPayee() && !echeance.getDatePaiement().isAfter(observation)
                ? echeance.getDatePaiement() : observation;
        if (!fin.isAfter(echeance.getDateEcheance())) return 0;
        return (int) java.time.temporal.ChronoUnit.DAYS.between(echeance.getDateEcheance(), fin);
    }

    private StatutEcheance statutEcheanceObserve(Echeance echeance, LocalDate observation) {
        Integer jours = joursRetardObserve(echeance, observation);
        if (jours == null) return null;
        if (echeance.estPayee() && echeance.getDatePaiement() != null
                && !echeance.getDatePaiement().isAfter(observation)) {
            return jours > 0 ? StatutEcheance.EN_RETARD : StatutEcheance.PAYE_A_TEMPS;
        }
        return jours > Echeance.SEUIL_IMPAYE_JOURS ? StatutEcheance.IMPAYE : StatutEcheance.EN_RETARD;
    }

    private LocalDate dateDerniereDeclaration(Contrat c, LocalDate reference) {
        var fin = calendrierService.moisFinDeclaration(c, reference);
        return fin == null ? null : fin.atEndOfMonth();
    }

    private BigDecimal somme(List<Echeance> echeances) {
        return echeances.stream().map(Echeance::getMontantDu)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BienLeasingDTO toLeasingDto(BienLeasing b) {
        if (b == null) return BienLeasingDTO.vide();
        return new BienLeasingDTO(b.getCodeBien(), b.getValeurBien(), b.getEtatBien(),
                b.getMarqueBien(), b.getDateAcquisition(), b.getImmatriculation());
    }

    private List<LienClientDTO> liensDe(List<ContratLien> liens) {
        return liens.stream()
                .map(l -> new LienClientDTO(l.getCodeClientCb(), l.getNomClient(),
                        LabelMapper.role(l.getRoleClient()), 1, null, null, false))
                .toList();
    }

    private List<LienClientDTO> tousLesLiens(List<Contrat> historique,
                                             Map<Long, List<ContratLien>> liens,
                                             Map<Long, List<Garantie>> garanties,
                                             Client client) {
        Map<String, LienClientDTO> parCode = new LinkedHashMap<>();
        for (Contrat c : historique) {
            for (ContratLien l : liens.getOrDefault(c.getId(), List.of())) {
                if (l.getCodeClientCb() != null
                        && l.getCodeClientCb().equals(client.getCodeClientCb())) continue;
                fusionner(parCode, l.getCodeClientCb(), l.getNomClient(),
                        LabelMapper.role(l.getRoleClient()), c.getCodeEtablissementDeclarant());
            }
            for (Garantie g : garanties.getOrDefault(c.getId(), List.of())) {
                if (g.getCodeClientCbGarant() == null
                        || g.getCodeClientCbGarant().equals(client.getCodeClientCb())) continue;
                fusionner(parCode, g.getCodeClientCbGarant(), g.getNomGarant(),
                        "Garant", c.getCodeEtablissementDeclarant());
            }
        }
        return List.copyOf(parCode.values());
    }

    private void fusionner(Map<String, LienClientDTO> cible, String code, String nom,
                           String relation, String etablissement) {
        if (code == null) return;
        cible.merge(code, new LienClientDTO(code, nom, relation, 1, etablissement, null, false),
                (a, b) -> new LienClientDTO(a.codeClientCb(), a.nom(), a.typeRelation(),
                        a.occurrences() + 1, a.etablissement(), a.dateDerniereModification(), a.flagParent()));
    }

    private GarantieDTO toGarantieDto(Garantie g) {
        return new GarantieDTO(
                g.getNature() == NatureGarantie.PERSONNELLE ? "Garantie Personnelle" : "Garantie Réelle",
                g.getTypeGarantie(), tiret(g.getCodeEtablissementGarantie()), g.getNomGarant(),
                g.getCodeClientCbGarant(), g.getMontantCouvert(),
                g.getDateDebutValidite(), g.getDateFinValidite());
    }

    private int ordrePhase(PhaseDemande p) {
        return switch (p) {
            case ACTIF -> 0;
            case DEMANDE_EN_COURS -> 1;
            case FERME -> 2;
            case REFUSE -> 3;
            case ABANDONNE -> 4;
        };
    }

    private int rangStatut(StatutEcheance s) {
        return switch (s) {
            case A_VENIR -> 0;
            case PAYE_A_TEMPS -> 1;
            case EN_RETARD -> 2;
            case IMPAYE -> 3;
        };
    }

    // [AJOUTÉ] factorise la règle déjà présente avant (comparaison dateAdhesion/historique) pour
    // qu'elle serve à la fois au libellé affiché (statutClient) et au message de score
    // (ScoreService.calculer reçoit maintenant ce booléen).
    private boolean clientPreexistant(Client client, Contrat saisi, List<Contrat> historique) {
        return !historique.isEmpty()
                || client.getDateAdhesion() == null
                || client.getDateAdhesion().isBefore(saisi.getDateDemande());
    }

    private String statutClient(boolean clientTrouve) {
        return clientTrouve ? "Client Trouvé" : "Client Introuvable, Client Nouvellement Créé";
    }

    private ClientInfoDTO toClientInfo(Client c) {
        String nomComplet = ((c.getPrenom() == null ? "" : c.getPrenom()) + " "
                + (c.getNom() == null ? "" : c.getNom())).trim();
        return new ClientInfoDTO(
                c.getCodeClientCb(), tiret(c.getTitre()), nomComplet.isBlank() ? "-" : nomComplet,
                c.getPrenom(), tiret(c.getDeuxiemePrenom()), c.getNom(),
                c.getDateNaissance(), tiret(c.getVilleNaissance()), tiret(c.getPaysNaissance()),
                c.getGenre() == null ? "-" : c.getGenre().name(), c.getNationalite(),
                tiret(c.getEtatCivil()), tiret(c.getTelephone()),
                c.getCategorieTiersCode(), c.getDateDerniereModification());
    }

    private AffaireIndividuelleDTO toAffaireIndividuelleDTO(AffaireIndividuelle affaire) {
        if (affaire == null) return null;
        List<AdresseDTO> adresses = affaire.getAdresses().stream()
                .filter(a -> Boolean.TRUE.equals(a.getActuelle()))
                .map(a -> new AdresseDTO(a.getTypeAdresse(), a.getAdresseComplete(),
                        tiret(a.getNumeroRue()), tiret(a.getCodePostal()), tiret(a.getVille()),
                        tiret(a.getCommune()), tiret(a.getRegion()), tiret(a.getPays()),
                        a.getDateDerniereModification()))
                .toList();
        return new AffaireIndividuelleDTO(affaire.getNom(), affaire.getDateDerniereModification(), adresses);
    }

    private String libelleOrigineDonnee(OrigineDonnee origine) {
        if (origine == null) return "Origine non renseignée";
        return switch (origine) {
            case NON_RENSEIGNEE -> "Origine non renseignée";
            case RAPPORT_CRIF -> "Rapport CRIF";
            case DONNEE_FICTIVE -> "Donnée fictive de démonstration";
        };
    }

    private List<ContactDTO> contacts(Client c) {
        List<ContactDTO> liste = new ArrayList<>();
        if (c.getTelephone() != null && !c.getTelephone().isBlank()) {
            liste.add(new ContactDTO("Téléphone Portable", c.getTelephone()));
        }
        return liste;
    }

    private List<AdresseDTO> adresses(Client c, boolean actuelles) {
        return c.getAdresses().stream()
                .filter(a -> Boolean.TRUE.equals(a.getActuelle()) == actuelles)
                .map(a -> new AdresseDTO(a.getTypeAdresse(), a.getAdresseComplete(),
                        tiret(a.getNumeroRue()), tiret(a.getCodePostal()), tiret(a.getVille()),
                        tiret(a.getCommune()), tiret(a.getRegion()), tiret(a.getPays()),
                        a.getDateDerniereModification()))
                .toList();
    }

    private DetailDemandeDTO toDetailDemande(Contrat c) {
        return new DetailDemandeDTO(
                c.getCodeContratCb(), LabelMapper.role(c.getRoleClient()),
                tiret(c.getTypeRelationEntreprise()), c.getTypeContrat(),
                LabelMapper.phase(c.getPhaseDemande(), c.getMotifCloture()), c.getDevise(),
                tiret(c.getPeriodicitePaiement()), libelleOrigineDonnee(c.getOrigineDonnee()),
                c.getMontantFinance(),
                c.getMontantEcheanceMensuelle(), c.getNombreTotalEcheances(), c.getDateDemande(),
                tiret(c.getPeriodiciteBudget()), c.getMontantRecette(), c.getMontantDepense(),
                c.getRatioRestePourcentage(),
                tiret(c.getTypeGarantie()), c.getValeurGarantie(),
                c.getMontantTotalDemandeInteret(), c.getRatioCouvertureGarantie());
    }

    private ScoreDTO toScoreDto(ScoreResult r) {
        String avertissement = "Score expérimental, non calibré sur des historiques de remboursement CECAM réels. "
                + "Indicateur d'analyse interne uniquement : il ne constitue pas une décision de crédit.";
        if (!r.calculable()) {
            return new ScoreDTO(false, null, null, null, null, null, r.message(), avertissement);
        }
        return new ScoreDTO(true, r.valeur(), r.intervalle(), r.categorieRisque(),
                r.couleur(), ScoreColorMapper.toHex(r.couleur()), r.message(), avertissement);
    }

    private List<GrilleScoreDTO> grille() {
        return grilleScoreRepository.findAll().stream()
                .sorted(Comparator.comparing(GrilleScore::getScoreMin))
                .map(g -> new GrilleScoreDTO(g.getIntervalle(), g.getCategorieRisque(),
                        ScoreColorMapper.toHex(g.getCouleur())))
                .toList();
    }

    private String tiret(String v) {
        return (v == null || v.isBlank()) ? "-" : v;
    }
}
