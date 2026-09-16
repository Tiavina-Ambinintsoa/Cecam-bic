// mg/cecam/bic/rapport/RapportService.java  — RÉÉCRIT
package mg.cecam.bic.rapport;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.client.Adresse;
import mg.cecam.bic.client.Client;
import mg.cecam.bic.client.Emploi;
import mg.cecam.bic.client.EmploiRepository;
import mg.cecam.bic.common.enums.NatureGarantie;
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
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RapportService {

    private static final Set<PhaseDemande> DECAISSEES = EnumSet.of(PhaseDemande.ACTIF, PhaseDemande.FERME);
    private static final String[] MOIS_LONG = {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
            "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"};

    private static final String CAT_AVEC_ECHEANCIER  = "Financements avec Échéancier";
    private static final String CAT_SANS_ECHEANCIER  = "Financements sans Échéancier";
    private static final String CAT_CARTES           = "Cartes de Crédit";
    private static final String CAT_SERVICES         = "Services";

    private final ContratRepository contratRepository;
    private final EcheanceRepository echeanceRepository;
    private final EmploiRepository emploiRepository;
    private final GarantieRepository garantieRepository;
    private final ScoreService scoreService;
    private final CalendrierService calendrierService;
    private final AlerteService alerteService;
    private final GrilleScoreRepository grilleScoreRepository;

    @Transactional(readOnly = true)
    public RapportSolvabiliteResponse construire(Long contratId) {
        Contrat contrat = contratRepository.findById(contratId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Contrat introuvable : " + contratId));
        Client client = contrat.getClient();
        LocalDate reference = LocalDate.now();

        List<Contrat> tous = contratRepository.findByClient_Id(client.getId());
        List<Contrat> historique = tous.stream()
                .filter(c -> !c.getId().equals(contratId))
                .sorted(Comparator.comparing(Contrat::getDateDemande).reversed())
                .toList();

        // --- Un seul chargement des échéances : supprime le N+1 et garantit
        //     que le score et le corps du rapport voient le même instantané.
        Map<Long, List<Echeance>> echeances = chargerEcheances(tous);
        Map<Long, List<Garantie>> garanties = chargerGaranties(tous);

        Emploi emploi = emploiRepository.findByClient_Id(client.getId()).orElse(null);
        BigDecimal revenuAnnuel = emploi != null ? emploi.getRevenuAnnuelTotal() : null;

        ScoreResult score = scoreService.calculer(
                client, contrat, historique, echeances, revenuAnnuel, reference);

        return new RapportSolvabiliteResponse(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                statutClient(client, contrat, historique),
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
                liens(historique, garanties, client),
                alerteService.listerParClient(client.getId(), reference),
                toScoreDto(score),
                grille(),
                synthese(historique, echeances, garanties, reference),
                syntheseParCategorie(historique, echeances, reference),
                encoursParCategorie(historique, echeances),
                calendriers(historique, echeances, reference),
                detailContrats(historique, echeances, garanties, reference)
        );
    }

    // ==================================================================
    // Chargement
    // ==================================================================

    private Map<Long, List<Echeance>> chargerEcheances(List<Contrat> contrats) {
        Map<Long, List<Echeance>> map = new HashMap<>();
        for (Contrat c : contrats) {
            map.put(c.getId(), echeanceRepository.findByContrat_IdOrderByNumeroEcheance(c.getId()));
        }
        return map;
    }

    private Map<Long, List<Garantie>> chargerGaranties(List<Contrat> contrats) {
        Map<Long, List<Garantie>> map = new HashMap<>();
        for (Contrat c : contrats) {
            map.put(c.getId(), garantieRepository.findByContrat_Id(c.getId()));
        }
        return map;
    }

    // ==================================================================
    // En-tête
    // ==================================================================

    /**
     * Libellés repris mot pour mot des deux rapports CRIF de référence.
     *
     * Le critère n'est plus « le client a plus d'un contrat » — un client
     * inscrit depuis deux ans dont c'est la première demande était déclaré
     * « nouvellement créé ». On regarde s'il préexistait à la demande saisie.
     */
    private String statutClient(Client client, Contrat saisi, List<Contrat> historique) {
        boolean preexistant = !historique.isEmpty()
                || client.getDateAdhesion() == null
                || client.getDateAdhesion().isBefore(saisi.getDateDemande());
        return preexistant ? "Client Trouvé" : "Client Introuvable, Client Nouvellement Créé";
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

    private List<ContactDTO> contacts(Client c) {
        List<ContactDTO> liste = new ArrayList<>();
        if (c.getTelephone() != null && !c.getTelephone().isBlank()) {
            liste.add(new ContactDTO("Téléphone Portable", c.getTelephone()));
        }
        return liste;
    }

    /** Boolean.TRUE.equals : une colonne actuelle à NULL provoquait un NPE au déballage. */
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
                .sorted(Comparator.comparing(GrilleScore::getScoreMin))   // E -> A, sens CRIF
                .map(g -> new GrilleScoreDTO(g.getIntervalle(), g.getCategorieRisque(),
                        ScoreColorMapper.toHex(g.getCouleur())))
                .toList();
    }

    /**
     * Clients liés, déduits des garanties (le garant d'un contrat est un client lié).
     * Le lien de co-titularité affiché par CRIF (GEORGETTE RAVAORISOA sur L00730033)
     * suppose une table de liaison contrat <-> clients qui reste à créer.
     */
    private List<LienClientDTO> liens(List<Contrat> historique, Map<Long, List<Garantie>> garanties, Client client) {
        Map<String, LienClientDTO> parCode = new LinkedHashMap<>();
        for (Contrat c : historique) {
            for (Garantie g : garanties.getOrDefault(c.getId(), List.of())) {
                String code = g.getCodeClientCbGarant();
                if (code == null || code.equals(client.getCodeClientCb())) continue;  // pas soi-même
                parCode.merge(code,
                        new LienClientDTO(code, g.getNomGarant(), "Garant", 1,
                                c.getCodeEtablissementDeclarant(), null, false),
                        (a, b) -> new LienClientDTO(a.codeClientCb(), a.nom(), a.typeRelation(),
                                a.occurrences() + 1, a.etablissement(), a.dateDerniereModification(),
                                a.flagParent()));
            }
        }
        return List.copyOf(parCode.values());
    }

    // ==================================================================
    // Synthèse — Chiffres Clés
    // ==================================================================

    /**
     * Définitions alignées sur les deux rapports CRIF de référence :
     *
     *  Montant Total Restant dû   = TOUTES les échéances non soldées des contrats
     *                               actifs, échues comprises. Les impayés en sont
     *                               un sous-ensemble, pas un complément.
     *  Montant Total Impayés      = la part échue et non soldée de ce restant dû.
     *  Montant Total Demandes     = somme des demandes EN COURS DÉJÀ EN BASE.
     *                               La demande en cours de saisie n'y entre pas
     *                               (modèle Juliette : 1 500 000 saisis, CRIF affiche 0).
     *  Exposition Potentielle     = restant dû + encours sans échéancier + garanties
     *                               par signature. Les demandes en cours n'y entrent
     *                               pas (Radera : exposition = restant dû = 1 717 875).
     *  Garantie par Signature     = montant COUVERT par les garanties PERSONNELLES.
     *                               Les nantissements de Radera sont des garanties
     *                               réelles, d'où le 0 affiché par CRIF.
     *
     * Sans aucun contrat en base, les montants sont null pour que le rendu
     * affiche « - » et non « 0 », sauf Demandes et Garantie par Signature que
     * CRIF affiche bien à 0.
     */
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
                restantDu = restantDu.add(e.getMontantDu());
                if (e.estEnSouffrance(reference)) {
                    impayes = impayes.add(e.getMontantDu());
                }
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
                historique.size(),
                etablissements,
                vide ? "-" : "Non",
                "Ariary malgache",
                vide ? null : exposition,
                vide ? null : restantDu,
                vide ? null : impayes,
                demandes,              // 0 et non « - », conformément au modèle Juliette
                garantieSignature,     // idem
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

    /**
     * Point d'extension : tous les produits CECAM connus aujourd'hui sont des
     * financements amortissables. Découverts, cartes et services devront être
     * rattachés ici quand ils entreront au catalogue.
     */
    private String categorieDe(Contrat c) {
        String t = c.getTypeContrat() == null ? "" : c.getTypeContrat().toLowerCase();
        if (t.contains("découvert") || t.contains("decouvert")) return CAT_SANS_ECHEANCIER;
        if (t.contains("carte")) return CAT_CARTES;
        if (t.contains("service") || t.contains("abonnement")) return CAT_SERVICES;
        return CAT_AVEC_ECHEANCIER;
    }

    // ==================================================================
    // Synthèse par catégorie
    // ==================================================================

    private List<SyntheseCategorieDTO> syntheseParCategorie(List<Contrat> historique,
                                                            Map<Long, List<Echeance>> echeances,
                                                            LocalDate reference) {
        List<Contrat> actifs = historique.stream()
                .filter(c -> c.getPhaseDemande() == PhaseDemande.ACTIF)
                .filter(c -> CAT_AVEC_ECHEANCIER.equals(categorieDe(c)))
                .toList();

        SyntheseCategorieDTO avecEcheancier = new SyntheseCategorieDTO(
                "Financements avec échéanciers", actifs.size(), "Montant Échéance Mensuelle",
                somme(actifs, false, c -> c.getMontantEcheanceMensuelle()),
                somme(actifs, true,  c -> c.getMontantEcheanceMensuelle()),
                sommeEcheances(actifs, false, echeances, e -> !e.estPayee()),
                sommeEcheances(actifs, true,  echeances, e -> !e.estPayee()),
                sommeEcheances(actifs, false, echeances, e -> e.estEnSouffrance(reference)),
                sommeEcheances(actifs, true,  echeances, e -> e.estEnSouffrance(reference)));

        // Les trois autres catégories sont nommées, plus « (non applicable) » x3.
        return List.of(avecEcheancier,
                SyntheseCategorieDTO.vide("Financements sans échéanciers", "Plafond de Crédit"),
                SyntheseCategorieDTO.vide("Cartes de Crédit", "Plafond de Crédit"),
                SyntheseCategorieDTO.vide("Services", "Montant Facture"));
    }

    private BigDecimal somme(List<Contrat> contrats, boolean garant,
                             java.util.function.Function<Contrat, BigDecimal> extracteur) {
        return contrats.stream()
                .filter(c -> (c.getRoleClient() == RoleClient.GARANT) == garant)
                .map(c -> extracteur.apply(c) == null ? BigDecimal.ZERO : extracteur.apply(c))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sommeEcheances(List<Contrat> contrats, boolean garant,
                                      Map<Long, List<Echeance>> echeances,
                                      java.util.function.Predicate<Echeance> filtre) {
        return contrats.stream()
                .filter(c -> (c.getRoleClient() == RoleClient.GARANT) == garant)
                .flatMap(c -> echeances.getOrDefault(c.getId(), List.<Echeance>of()).stream())
                .filter(filtre)
                .map(Echeance::getMontantDu)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ==================================================================
    // Grilles mensuelles
    // ==================================================================

    private List<EncoursCategorieDTO> encoursParCategorie(List<Contrat> historique,
                                                          Map<Long, List<Echeance>> echeances) {
        List<Contrat> avecEcheancier = historique.stream()
                .filter(c -> DECAISSEES.contains(c.getPhaseDemande()))
                .filter(c -> CAT_AVEC_ECHEANCIER.equals(categorieDe(c)))
                .toList();
        if (avecEcheancier.isEmpty()) return List.of();

        String etablissement = avecEcheancier.get(0).getCodeEtablissementDeclarant();
        EncoursCategorieDTO dto = calendrierService.encours(
                "Financements avec échéanciers", etablissement, avecEcheancier, echeances);
        return dto.lignes().isEmpty() ? List.of() : List.of(dto);
    }

    private List<CalendrierCreditDTO> calendriers(List<Contrat> historique,
                                                  Map<Long, List<Echeance>> echeances,
                                                  LocalDate reference) {
        return historique.stream()
                .filter(c -> DECAISSEES.contains(c.getPhaseDemande()))
                .map(c -> calendrierService.grilleStatut(c, echeances.getOrDefault(c.getId(), List.of()), reference))
                .filter(c -> !c.lignes().isEmpty())
                .toList();
    }

    // ==================================================================
    // Détail des contrats
    // ==================================================================

    private List<DetailContratDTO> detailContrats(List<Contrat> historique,
                                                  Map<Long, List<Echeance>> echeances,
                                                  Map<Long, List<Garantie>> garanties,
                                                  LocalDate reference) {
        return historique.stream()
                .sorted(Comparator
                        .comparingInt((Contrat c) -> ordrePhase(c.getPhaseDemande()))
                        .thenComparing(Contrat::getDateDemande, Comparator.reverseOrder()))
                .map(c -> toDetailContrat(c, echeances.getOrDefault(c.getId(), List.of()),
                        garanties.getOrDefault(c.getId(), List.of()), reference))
                .toList();
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

    private DetailContratDTO toDetailContrat(Contrat c, List<Echeance> echeances,
                                             List<Garantie> garanties, LocalDate reference) {

        // Historique de paiement, du plus récent au plus ancien comme CRIF.
        List<HistoriquePaiementLigneDTO> historiquePaiement = echeances.stream()
                .filter(e -> !e.getDateEcheance().isAfter(reference))
                .sorted(Comparator.comparing(Echeance::getDateEcheance).reversed())
                .map(e -> {
                    StatutEcheance s = e.statutEffectif(reference);
                    return new HistoriquePaiementLigneDTO(
                            e.getDateEcheance().getYear(),
                            MOIS_LONG[e.getDateEcheance().getMonthValue() - 1],
                            s == StatutEcheance.IMPAYE ? 1 : 0,
                            e.joursDeRetard(reference),
                            LabelMapper.statutEcheance(s));
                })
                .toList();

        List<Echeance> nonSoldees = echeances.stream().filter(e -> !e.estPayee()).toList();
        List<Echeance> enSouffrance = nonSoldees.stream().filter(e -> e.estEnSouffrance(reference)).toList();

        // Même périmètre pour le nombre et pour le montant : la version initiale
        // comptait toutes les non payées mais ne sommait que les non échues.
        int nbRestantes = nonSoldees.size();
        BigDecimal restantDu = nonSoldees.stream().map(Echeance::getMontantDu)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal montantImpayes = enSouffrance.stream().map(Echeance::getMontantDu)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Echeance prochaine = nonSoldees.stream()
                .min(Comparator.comparing(Echeance::getDateEcheance)).orElse(null);

        LocalDate dernierReglement = echeances.stream().filter(Echeance::estPayee)
                .map(Echeance::getDatePaiement).filter(Objects::nonNull)
                .max(Comparator.naturalOrder()).orElse(null);

        int maxJours = echeances.stream().mapToInt(e -> e.joursDeRetard(reference)).max().orElse(0);
        LocalDate dateMaxJours = echeances.stream()
                .filter(e -> e.joursDeRetard(reference) == maxJours && maxJours > 0)
                .map(Echeance::getDateEcheance).max(Comparator.naturalOrder()).orElse(null);
        int joursCourants = nonSoldees.stream().mapToInt(e -> e.joursDeRetard(reference)).max().orElse(0);

        // Pire statut : « No info » tant qu'aucune échéance n'est échue, comme CRIF.
        String pireStatut;
        LocalDate datePireStatut = null;
        if (historiquePaiement.isEmpty()) {
            pireStatut = "No info";
        } else {
            Optional<Echeance> pire = echeances.stream()
                    .filter(e -> !e.getDateEcheance().isAfter(reference))
                    .max(Comparator.comparingInt(e -> rangStatut(e.statutEffectif(reference))));
            StatutEcheance s = pire.map(e -> e.statutEffectif(reference)).orElse(StatutEcheance.PAYE_A_TEMPS);
            pireStatut = LabelMapper.statutEcheance(s);
            datePireStatut = s == StatutEcheance.PAYE_A_TEMPS ? null
                    : pire.map(Echeance::getDateEcheance).orElse(null);
        }

        return new DetailContratDTO(
                c.getCodeContratCb(), c.getCodeContratEtablissement(), c.getCodeEtablissementDeclarant(),
                c.getTypeContrat(), LabelMapper.phase(c.getPhaseDemande(), c.getMotifCloture()),
                LabelMapper.role(c.getRoleClient()), c.getDevise(),
                c.getDateDemande(), c.getDateDebutContrat(), c.getDateFinContrat(),
                c.getDatePremiereEcheance(), c.getDateDerniereModification(),
                c.getMontantFinance(), c.totalDu(), c.getMontantEcheanceMensuelle(),
                c.getNombreTotalEcheances(), tiret(c.getPeriodicitePaiement()),
                prochaine == null ? null : prochaine.getMontantDu(),
                prochaine == null ? null : prochaine.getDateEcheance(),
                dernierReglement,
                historiquePaiement,
                nbRestantes, restantDu, enSouffrance.size(), montantImpayes,
                montantImpayes, enSouffrance.size(),
                joursCourants, maxJours, dateMaxJours,
                pireStatut, datePireStatut,
                tiret(c.getCodeRestructuration()), tiret(c.getContratOrigine()), tiret(c.getNouveauContrat()),
                garanties.stream().map(this::toGarantieDto).toList());
    }

    private int rangStatut(StatutEcheance s) {
        return switch (s) {
            case A_VENIR -> 0;
            case PAYE_A_TEMPS -> 1;
            case EN_RETARD -> 2;
            case IMPAYE -> 3;
        };
    }

    private GarantieDTO toGarantieDto(Garantie g) {
        return new GarantieDTO(
                g.getNature() == NatureGarantie.PERSONNELLE ? "Garantie Personnelle" : "Garantie Réelle",
                g.getTypeGarantie(), tiret(g.getCodeEtablissementGarantie()), g.getNomGarant(),
                g.getCodeClientCbGarant(), g.getMontantCouvert(),
                g.getDateDebutValidite(), g.getDateFinValidite());
    }

    private String tiret(String v) {
        return (v == null || v.isBlank()) ? "-" : v;
    }
}