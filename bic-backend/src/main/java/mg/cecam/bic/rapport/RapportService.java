package mg.cecam.bic.rapport;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.audit.AuditService;
import mg.cecam.bic.audit.JournalAuditRepository;
import mg.cecam.bic.client.AffaireIndividuelle;
import mg.cecam.bic.client.AffaireIndividuelleRepository;
import mg.cecam.bic.client.Client;
import mg.cecam.bic.client.Emploi;
import mg.cecam.bic.client.EmploiRepository;
import mg.cecam.bic.common.enums.NatureGarantie;
import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.RoleClient;
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

    private static final String ETABLISSEMENT_PROPRE = "001";

    private static final Set<PhaseDemande> DECAISSEES = EnumSet.of(PhaseDemande.ACTIF, PhaseDemande.FERME);
    private static final Set<PhaseDemande> NON_DECAISSEES =
            EnumSet.of(PhaseDemande.DEMANDE_EN_COURS, PhaseDemande.REFUSE, PhaseDemande.ABANDONNE);

    private static final String[] MOIS_COURT = {"Jan", "Fév", "Mars", "Avr", "Mai", "Juin",
            "Juill", "Août", "Sep", "Oct", "Nov", "Déc"};

    private static final String CAT_AVEC_ECHEANCIER = "Financements avec Échéancier";
    private static final String CAT_SANS_ECHEANCIER = "Financements sans Échéancier";
    private static final String CAT_CARTES          = "Cartes de Crédit";
    private static final String CAT_SERVICES        = "Services";

    private final ContratRepository contratRepository;
    private final EcheanceRepository echeanceRepository;
    private final EmploiRepository emploiRepository;
    private final GarantieRepository garantieRepository;
    private final BienLeasingRepository bienLeasingRepository;
    private final ContratLienRepository contratLienRepository;
    private final PortefeuilleClientService portefeuilleService;
    private final AffaireIndividuelleRepository affaireRepository;
    private final ScoreService scoreService;
    private final CalendrierService calendrierService;
    private final AlerteService alerteService;
    private final GrilleScoreRepository grilleScoreRepository;
    private final AuditService auditService;
    private final JournalAuditRepository journalAuditRepository;

    // Pas de readOnly : la méthode écrit dans le journal d'audit (PostgreSQL refuse l'INSERT sinon).
    @Transactional
    public RapportSolvabiliteResponse construire(Long contratId) {
        Contrat contrat = contratRepository.findById(contratId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Contrat introuvable : " + contratId));
        Client client = contrat.getClient();
        LocalDate reference = LocalDate.now();

        // Consultations passées : lues AVANT d'enregistrer celle-ci, comme sur un rapport CRIF.
        List<ConsultationDTO> historiqueConsultations = journalAuditRepository
                .findByEntiteAndEntiteIdOrderByDateEvenementDesc("CLIENT", client.getId()).stream()
                .filter(a -> "CREATION".equals(a.getAction()) || "CONSULTATION".equals(a.getAction()))
                .map(a -> new ConsultationDTO(ETABLISSEMENT_PROPRE, a.getDateEvenement().toLocalDate(),
                        "CREATION".equals(a.getAction()) ? "Nouvelle Demande" : "Suivi Client"))
                .toList();
        auditService.enregistrer("CLIENT", client.getId(), "CONSULTATION", "Consultation du rapport de solvabilité");

        List<PortefeuilleClientService.ContratDuClient> portefeuille = portefeuilleService.contrats(client);
        Map<Long, RoleClient> roles = new HashMap<>();
        Map<Long, Boolean> lies = new HashMap<>();
        List<Contrat> tous = new ArrayList<>();
        for (PortefeuilleClientService.ContratDuClient v : portefeuille) {
            tous.add(v.contrat());
            roles.put(v.contrat().getId(), v.role());
            lies.put(v.contrat().getId(), v.lie());
        }

        List<Contrat> historique = tous.stream()
                .filter(c -> !c.getId().equals(contratId))
                .sorted(Comparator.comparing(Contrat::getDateDemande).reversed())
                .toList();

        List<Long> ids = tous.stream().map(Contrat::getId).toList();
        Map<Long, List<Echeance>> echeances = new HashMap<>();
        Map<Long, List<Garantie>> garanties = new HashMap<>();
        for (Contrat c : tous) {
            echeances.put(c.getId(), echeanceRepository.findByContrat_IdOrderByNumeroEcheance(c.getId()));
            garanties.put(c.getId(), garantieRepository.findByContrat_Id(c.getId()));
        }
        Map<Long, BienLeasing> leasings = bienLeasingRepository.findByContrat_IdIn(ids).stream()
                .collect(Collectors.toMap(b -> b.getContrat().getId(), Function.identity(), (a, b) -> a));
        Map<Long, List<ContratLien>> liensParContrat = contratLienRepository.findByContrat_IdIn(ids).stream()
                .collect(Collectors.groupingBy(l -> l.getContrat().getId()));

        Map<Long, List<LienClientDTO>> liensDto = new HashMap<>();
        for (Contrat c : tous) {
            liensDto.put(c.getId(), liensPour(c, lies.getOrDefault(c.getId(), false),
                    liensParContrat.getOrDefault(c.getId(), List.of()), client));
        }

        Emploi emploi = emploiRepository.findByClient_Id(client.getId()).orElse(null);

        boolean clientTrouve = clientPreexistant(client, contrat, historique);
        ScoreResult scoreResult = scoreService.calculer(client, contrat, clientTrouve);

        return new RapportSolvabiliteResponse(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                statutClient(clientTrouve),
                client.getCodeClientCb(),
                toClientInfo(client),
                contacts(client),
                adresses(client, true),
                adresses(client, false),
                client.getIdentifiants().stream()
                        .map(i -> new IdentifiantDTO(i.getTypeIdentifiant(), i.getNumero())).toList(),
                toDetailDemande(contrat),
                emploi == null ? null : new EmploiDTO(emploi.getStatutEmploi(), emploi.getNomEmployeur(),
                        emploi.getProfession(), emploi.getDateEmbauche(),
                        emploi.getRevenuAnnuelTotal(), emploi.getDevise()),
                affaireRepository.findByClient_Id(client.getId()).map(this::toAffaireDto).orElse(null),
                tousLesLiens(historique, liensDto, garanties, client),
                alerteService.listerParClient(client.getId(), reference),
                toScoreDto(scoreResult),
                grille(),
                synthese(historique, echeances, garanties, roles, clientTrouve, reference),
                syntheseParCategorie(historique, echeances, roles, reference),
                situationFinanciere(historique, echeances, roles, reference),
                financements(historique, echeances, liensDto, roles, NON_DECAISSEES, false, reference),
                financements(historique, echeances, liensDto, roles, DECAISSEES, true, reference),
                detailContrats(historique, echeances, garanties, leasings, liensDto, roles, reference),
                historiqueConsultations
        );
    }

    private RoleClient role(Contrat c, Map<Long, RoleClient> roles) {
        return roles.getOrDefault(c.getId(), c.getRoleClient());
    }

    private SyntheseDTO synthese(List<Contrat> historique,
                                 Map<Long, List<Echeance>> echeances,
                                 Map<Long, List<Garantie>> garanties,
                                 Map<Long, RoleClient> roles,
                                 boolean clientTrouve,
                                 LocalDate reference) {

        BigDecimal restantDu = BigDecimal.ZERO;
        BigDecimal impayes = BigDecimal.ZERO;

        for (Contrat c : historique) {
            if (c.getPhaseDemande() != PhaseDemande.ACTIF) continue;
            // Les contrats que le client garantit sans les rembourser sont comptés dans la colonne « Garant ».
            if (role(c, roles) == RoleClient.GARANT) continue;
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

        return new SyntheseDTO(
                historique.size(), etablissements,
                clientTrouve ? "Non" : "-", "Ariary malgache",
                vide ? null : exposition,
                vide ? null : restantDu,
                vide ? null : impayes,
                demandes, garantieSignature,
                repartition(historique));
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
                                                            Map<Long, RoleClient> roles,
                                                            LocalDate reference) {
        List<Contrat> actifs = historique.stream()
                .filter(c -> c.getPhaseDemande() == PhaseDemande.ACTIF)
                .filter(c -> CAT_AVEC_ECHEANCIER.equals(categorieDe(c)))
                .toList();

        Predicate<Echeance> aEchoir = e -> !e.estPayee() && !e.estEnSouffrance(reference);
        Predicate<Echeance> enSouffrance = e -> e.estEnSouffrance(reference);

        SyntheseCategorieDTO avecEcheancier = new SyntheseCategorieDTO(
                "Financements avec échéanciers", actifs.size(), "Montant Échéance Mensuelle",
                somme(actifs, roles, false, Contrat::getMontantEcheanceMensuelle),
                somme(actifs, roles, true,  Contrat::getMontantEcheanceMensuelle),
                sommeEcheances(actifs, roles, false, echeances, aEchoir),
                sommeEcheances(actifs, roles, true,  echeances, aEchoir),
                sommeEcheances(actifs, roles, false, echeances, enSouffrance),
                sommeEcheances(actifs, roles, true,  echeances, enSouffrance));

        return List.of(avecEcheancier,
                SyntheseCategorieDTO.vide("Financements sans échéanciers", "Plafond de Crédit"),
                SyntheseCategorieDTO.vide("Cartes de Crédit", "Plafond de Crédit"),
                SyntheseCategorieDTO.vide("Services", "Montant Facture"));
    }

    private BigDecimal somme(List<Contrat> contrats, Map<Long, RoleClient> roles, boolean garant,
                             Function<Contrat, BigDecimal> extracteur) {
        return contrats.stream()
                .filter(c -> (role(c, roles) == RoleClient.GARANT) == garant)
                .map(c -> extracteur.apply(c) == null ? BigDecimal.ZERO : extracteur.apply(c))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sommeEcheances(List<Contrat> contrats, Map<Long, RoleClient> roles, boolean garant,
                                      Map<Long, List<Echeance>> echeances, Predicate<Echeance> filtre) {
        return contrats.stream()
                .filter(c -> (role(c, roles) == RoleClient.GARANT) == garant)
                .flatMap(c -> echeances.getOrDefault(c.getId(), List.<Echeance>of()).stream())
                .filter(filtre)
                .map(Echeance::getMontantDu)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<EncoursCategorieDTO> situationFinanciere(List<Contrat> historique,
                                                          Map<Long, List<Echeance>> echeances,
                                                          Map<Long, RoleClient> roles,
                                                          LocalDate reference) {
        List<Contrat> avecEcheancier = historique.stream()
                .filter(c -> DECAISSEES.contains(c.getPhaseDemande()))
                .filter(c -> CAT_AVEC_ECHEANCIER.equals(categorieDe(c)))
                .filter(c -> role(c, roles) != RoleClient.GARANT)
                .toList();
        if (avecEcheancier.isEmpty()) return List.of();
        return calendrierService.situationFinanciere("Financements avec échéanciers",
                ETABLISSEMENT_PROPRE, avecEcheancier, echeances, reference);
    }

    private List<LigneFinancementDTO> financements(List<Contrat> historique,
                                                   Map<Long, List<Echeance>> echeances,
                                                   Map<Long, List<LienClientDTO>> liens,
                                                   Map<Long, RoleClient> roles,
                                                   Set<PhaseDemande> phases,
                                                   boolean avecGrille,
                                                   LocalDate reference) {
        List<Contrat> retenus = historique.stream()
                .filter(c -> phases.contains(c.getPhaseDemande()))
                .filter(c -> CAT_AVEC_ECHEANCIER.equals(categorieDe(c)))
                .sorted(Comparator.comparing(Contrat::getDateDemande).reversed())
                .toList();

        List<LigneFinancementDTO> lignes = new ArrayList<>();
        int i = 1;
        for (Contrat c : retenus) {
            lignes.add(new LigneFinancementDTO(
                    i++, c.getCodeContratCb(), tiret(c.getCodeContratEtablissement()),
                    c.getTypeContrat(),
                    LabelMapper.phase(c.getPhaseDemande(), c.getMotifCloture()),
                    LabelMapper.role(role(c, roles)),
                    c.getCodeEtablissementDeclarant(),
                    liens.getOrDefault(c.getId(), List.of()),
                    c.getDateDemande(), c.getMontantFinance(), c.getMontantEcheanceMensuelle(),
                    c.getNombreTotalEcheances(), tiret(c.getPeriodicitePaiement()),
                    c.getDateDerniereModification(),
                    c.getDateDebutContrat(), c.getDateFinContrat(), tiret(c.getNote()),
                    avecGrille
                            ? calendrierService.grilleStatut(c, echeances.getOrDefault(c.getId(), List.of()), reference)
                            : List.of()));
        }
        return lignes;
    }

    private List<DetailContratDTO> detailContrats(List<Contrat> historique,
                                                  Map<Long, List<Echeance>> echeances,
                                                  Map<Long, List<Garantie>> garanties,
                                                  Map<Long, BienLeasing> leasings,
                                                  Map<Long, List<LienClientDTO>> liens,
                                                  Map<Long, RoleClient> roles,
                                                  LocalDate reference) {
        // Comme chez CRIF : le détail n'existe que pour les contrats décaissés (Actif, Fin à terme, Fin anticipée).
        return historique.stream()
                .filter(c -> DECAISSEES.contains(c.getPhaseDemande()))
                .sorted(Comparator
                        .comparingInt((Contrat c) -> ordrePhase(c.getPhaseDemande()))
                        .thenComparing(Contrat::getDateDemande, Comparator.reverseOrder()))
                .map(c -> toDetailContrat(c, role(c, roles),
                        echeances.getOrDefault(c.getId(), List.of()),
                        garanties.getOrDefault(c.getId(), List.of()),
                        leasings.get(c.getId()),
                        liens.getOrDefault(c.getId(), List.of()),
                        reference))
                .toList();
    }

    private DetailContratDTO toDetailContrat(Contrat c, RoleClient role, List<Echeance> echeances,
                                             List<Garantie> garanties, BienLeasing leasing,
                                             List<LienClientDTO> liens, LocalDate reference) {

        List<CalendrierService.MoisSuivi> suivi = calendrierService.suivi(c, echeances, reference);
        List<HistoriquePaiementLigneDTO> historiquePaiement = historiquePaiement(suivi);

        List<Echeance> aEchoir = echeances.stream()
                .filter(e -> !e.estPayee() && !e.estEnSouffrance(reference)).toList();
        List<Echeance> enSouffrance = echeances.stream()
                .filter(e -> e.estEnSouffrance(reference)).toList();

        BigDecimal restantDu = somme(aEchoir);
        BigDecimal montantImpayes = somme(enSouffrance);
        int nombreImpayees = (int) enSouffrance.stream().map(Echeance::getDateEcheance).distinct().count();

        Echeance prochaine = echeances.stream()
                .filter(e -> !e.estPayee())
                .min(Comparator.comparing(Echeance::getDateEcheance)).orElse(null);

        LocalDate dernierReglement = echeances.stream().filter(Echeance::estPayee)
                .map(Echeance::getDatePaiement).filter(Objects::nonNull)
                .max(Comparator.naturalOrder()).orElse(null);

        // Maxima lus sur les photographies de fin de mois, comme sur les rapports CRIF.
        int maxJours = 0;
        LocalDate dateMaxJours = null;
        int maxNombre = 0;
        LocalDate dateMaxNombre = null;
        BigDecimal maxMontant = BigDecimal.ZERO;
        for (CalendrierService.MoisSuivi m : suivi) {
            if (m.jours() > 0 && m.jours() >= maxJours) {
                maxJours = m.jours();
                dateMaxJours = m.observation();
            }
            if (m.nombreEcheancesImpayees() > 0 && m.nombreEcheancesImpayees() >= maxNombre) {
                maxNombre = m.nombreEcheancesImpayees();
                dateMaxNombre = m.observation();
            }
            if (m.montantImpaye().compareTo(maxMontant) > 0) maxMontant = m.montantImpaye();
        }
        if (maxJours == 0) dateMaxJours = dateDerniereDeclaration(c, reference);

        int joursCourants = enSouffrance.stream().mapToInt(e -> e.joursDeRetard(reference)).max().orElse(0);

        return new DetailContratDTO(
                c.getCodeContratCb(), tiret(c.getCodeContratEtablissement()), c.getCodeEtablissementDeclarant(),
                c.getTypeContrat(), LabelMapper.phase(c.getPhaseDemande(), c.getMotifCloture()),
                LabelMapper.role(role), c.getDevise(),
                c.getDateDemande(), c.getDateDebutContrat(), c.getDateFinContrat(),
                c.getDatePremiereEcheance(), c.getDateDerniereModification(),
                c.getMontantFinance(), c.totalDu(), c.getMontantEcheanceMensuelle(),
                c.getNombreTotalEcheances(), tiret(c.getPeriodicitePaiement()), "-",
                prochaine == null ? BigDecimal.ZERO : prochaine.getMontantDu(),
                prochaine == null ? null : prochaine.getDateEcheance(),
                dernierReglement,
                historiquePaiement,
                aEchoir.size(), restantDu, nombreImpayees, montantImpayes,
                maxMontant, maxNombre, dateMaxNombre,
                joursCourants, maxJours, dateMaxJours,
                "No info", null,
                tiret(c.getCodeRestructuration()), c.getDateCodeRestructuration(),
                tiret(c.getContratOrigine()), tiret(c.getNouveauContrat()), tiret(c.getNote()),
                toLeasingDto(leasing),
                liens,
                garanties.stream().map(this::toGarantieDto).toList());
    }

    /** Un enregistrement par mois, du plus récent au plus ancien. */
    private List<HistoriquePaiementLigneDTO> historiquePaiement(List<CalendrierService.MoisSuivi> suivi) {
        List<HistoriquePaiementLigneDTO> lignes = new ArrayList<>();
        for (int i = suivi.size() - 1; i >= 0; i--) {
            CalendrierService.MoisSuivi m = suivi.get(i);
            lignes.add(new HistoriquePaiementLigneDTO(
                    m.mois().getYear(), MOIS_COURT[m.mois().getMonthValue() - 1],
                    m.nombreEcheancesImpayees(), m.tranche().getLibelle(), "-"));
        }
        return lignes;
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

    /**
     * Clients liés d'un contrat. Pour le titulaire : les co-titulaires et garants. Pour un client lié :
     * le titulaire du contrat, puis les autres liés (lui-même est exclu).
     */
    private List<LienClientDTO> liensPour(Contrat c, boolean lie, List<ContratLien> liens, Client client) {
        List<LienClientDTO> resultat = new ArrayList<>();
        if (lie) {
            Client titulaire = c.getClient();
            resultat.add(new LienClientDTO(titulaire.getCodeClientCb(), nomComplet(titulaire),
                    LabelMapper.role(c.getRoleClient()), 1, c.getCodeEtablissementDeclarant(), null, false));
        }
        for (ContratLien l : liens) {
            if (client.getCodeClientCb() != null && client.getCodeClientCb().equals(l.getCodeClientCb())) continue;
            resultat.add(new LienClientDTO(l.getCodeClientCb(), l.getNomClient(),
                    LabelMapper.role(l.getRoleClient()), 1, null, null, false));
        }
        return resultat;
    }

    private List<LienClientDTO> tousLesLiens(List<Contrat> historique,
                                             Map<Long, List<LienClientDTO>> liensParContrat,
                                             Map<Long, List<Garantie>> garanties,
                                             Client client) {
        Map<String, LienClientDTO> parCode = new LinkedHashMap<>();
        for (Contrat c : historique) {
            for (LienClientDTO l : liensParContrat.getOrDefault(c.getId(), List.of())) {
                fusionner(parCode, l.codeClientCb(), l.nom(), l.typeRelation(), c.getCodeEtablissementDeclarant());
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

    private AffaireIndividuelleDTO toAffaireDto(AffaireIndividuelle a) {
        return new AffaireIndividuelleDTO(a.getNomAffaire(), a.getDateDerniereModification(),
                new AdresseDTO("Entreprise - Adresse Principale", tiret(a.getAdresseComplete()),
                        tiret(a.getNumeroRue()), tiret(a.getCodePostal()), tiret(a.getVille()),
                        tiret(a.getCommune()), tiret(a.getRegion()), tiret(a.getPays()), null));
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

    private boolean clientPreexistant(Client client, Contrat saisi, List<Contrat> historique) {
        return !historique.isEmpty()
                || client.getDateAdhesion() == null
                || client.getDateAdhesion().isBefore(saisi.getDateDemande());
    }

    private String statutClient(boolean clientTrouve) {
        return clientTrouve ? "Client Trouvé" : "Client Introuvable, Client Nouvellement Créé";
    }

    private String nomComplet(Client c) {
        return ((c.getPrenom() == null ? "" : c.getPrenom()) + " "
                + (c.getNom() == null ? "" : c.getNom())).trim();
    }

    private ClientInfoDTO toClientInfo(Client c) {
        String nomComplet = nomComplet(c);
        return new ClientInfoDTO(
                c.getCodeClientCb(), tiret(c.getTitre()), nomComplet.isBlank() ? "-" : nomComplet,
                c.getPrenom(), tiret(c.getDeuxiemePrenom()), c.getNom(),
                c.getDateNaissance(), tiret(c.getVilleNaissance()), tiret(c.getPaysNaissance()),
                c.getGenre() == null ? "-" : c.getGenre().name(), c.getNationalite(),
                tiret(c.getEtatCivil()), tiret(c.getTelephone()),
                c.getCategorieTiersCode(), c.getDateDerniereModification());
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
                tiret(c.getPeriodicitePaiement()), c.getMontantFinance(),
                c.getMontantEcheanceMensuelle(), c.getNombreTotalEcheances(), c.getDateDemande());
    }

    private ScoreDTO toScoreDto(ScoreResult r) {
        if (!r.calculable()) {
            return new ScoreDTO(false, null, null, null, null, null, r.message(), null);
        }
        return new ScoreDTO(true, r.valeur(), r.intervalle(), r.categorieRisque(),
                r.couleur(), ScoreColorMapper.toHex(r.couleur()), null, r.detail());
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