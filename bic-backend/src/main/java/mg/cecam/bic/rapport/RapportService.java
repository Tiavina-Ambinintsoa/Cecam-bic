package mg.cecam.bic.rapport;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.client.Adresse;
import mg.cecam.bic.client.Client;
import mg.cecam.bic.client.EmploiRepository;
import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.RoleClient;
import mg.cecam.bic.common.enums.StatutEcheance;
import mg.cecam.bic.common.util.LabelMapper;
import mg.cecam.bic.common.util.ScoreColorMapper;
import mg.cecam.bic.contrat.Contrat;
import mg.cecam.bic.contrat.ContratRepository;
import mg.cecam.bic.contrat.Echeance;
import mg.cecam.bic.contrat.EcheanceRepository;
import mg.cecam.bic.contrat.GarantieRepository;
import mg.cecam.bic.rapport.dto.*;
import mg.cecam.bic.referentiel.GrilleScore;
import mg.cecam.bic.referentiel.GrilleScoreRepository;
import mg.cecam.bic.score.ScoreResult;
import mg.cecam.bic.score.ScoreService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RapportService {

    private static final Set<PhaseDemande> PHASES_DECAISSEES = EnumSet.of(PhaseDemande.ACTIF, PhaseDemande.FERME);
    private static final String[] MOIS_LABELS_LONG =
            {"Janvier","Février","Mars","Avril","Mai","Juin","Juillet","Août","Septembre","Octobre","Novembre","Décembre"};

    private final ContratRepository contratRepository;
    private final EcheanceRepository echeanceRepository;
    private final EmploiRepository emploiRepository;
    private final GarantieRepository garantieRepository;
    private final ScoreService scoreService;
    private final CalendrierService calendrierService;
    private final GrilleScoreRepository grilleScoreRepository;

    public RapportSolvabiliteResponse construire(Long contratId) {
        Contrat contrat = contratRepository.findById(contratId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrat introuvable : " + contratId));
        Client client = contrat.getClient();

        List<Contrat> tousContrats = contratRepository.findByClient_Id(client.getId());
        boolean clientTrouve = tousContrats.size() > 1;
        List<Contrat> historique = tousContrats.stream().filter(c -> !c.getId().equals(contratId)).toList();

        ScoreResult scoreResult = scoreService.calculer(client, contrat);

        List<AdresseDTO> actuelles = client.getAdresses().stream().filter(Adresse::getActuelle).map(this::toAdresseDto).toList();
        List<AdresseDTO> historiques = client.getAdresses().stream().filter(a -> !a.getActuelle()).map(this::toAdresseDto).toList();
        List<IdentifiantDTO> identifiants = client.getIdentifiants().stream()
                .map(i -> new IdentifiantDTO(i.getTypeIdentifiant(), i.getNumero())).toList();

        List<CalendrierCreditDTO> calendriers = historique.stream()
                .filter(c -> PHASES_DECAISSEES.contains(c.getPhaseDemande()))
                .map(calendrierService::construire)
                .filter(c -> !c.lignes().isEmpty())
                .toList();

        List<DetailContratDTO> detailContrats = historique.stream().map(this::toDetailContratDto).toList();

        List<GrilleScoreDTO> grille = grilleScoreRepository.findAll().stream()
                .sorted(Comparator.comparing(GrilleScore::getIntervalle).reversed())
                .map(g -> new GrilleScoreDTO(g.getIntervalle(), g.getCategorieRisque(), ScoreColorMapper.toHex(g.getCouleur())))
                .toList();

        EmploiDTO emploiDto = emploiRepository.findByClient_Id(client.getId())
                .map(e -> new EmploiDTO(e.getStatutEmploi(), e.getNomEmployeur(), e.getProfession(),
                        e.getDateEmbauche(), e.getRevenuAnnuelTotal(), e.getDevise()))
                .orElse(null);

        return new RapportSolvabiliteResponse(
                UUID.randomUUID().toString(), LocalDateTime.now(),
                clientTrouve ? "Client trouvé" : "Client Introuvable, Client Nouvellement Créé",
                client.getCodeClientCb(), toClientInfoDto(client), actuelles, historiques, identifiants,
                toDetailDemandeDto(contrat), emploiDto, List.of(),
                toScoreDto(scoreResult), grille, construireSynthese(historique, contrat), calendriers, detailContrats
        );
    }

    private ClientInfoDTO toClientInfoDto(Client c) {
        return new ClientInfoDTO(
                blankToDash(c.getTitre()), (c.getPrenom() + " " + c.getNom()).trim(),
                c.getPrenom(), blankToDash(c.getDeuxiemePrenom()), c.getNom(),
                c.getDateNaissance(), blankToDash(c.getVilleNaissance()), blankToDash(c.getPaysNaissance()),
                c.getGenre().name(), c.getNationalite(), blankToDash(c.getEtatCivil()), blankToDash(c.getTelephone()),
                c.getCategorieTiersCode(), c.getDateDerniereModification()
        );
    }

    private AdresseDTO toAdresseDto(Adresse a) {
        return new AdresseDTO(
                a.getTypeAdresse(), a.getAdresseComplete(), blankToDash(a.getNumeroRue()), blankToDash(a.getCodePostal()),
                blankToDash(a.getVille()), blankToDash(a.getCommune()), blankToDash(a.getRegion()), blankToDash(a.getPays()),
                a.getDateDerniereModification()
        );
    }

    private DetailDemandeDTO toDetailDemandeDto(Contrat c) {
        return new DetailDemandeDTO(
                c.getCodeContratCb(), LabelMapper.role(c.getRoleClient()), blankToDash(c.getTypeRelationEntreprise()),
                c.getTypeContrat(), LabelMapper.phase(c.getPhaseDemande()), c.getDevise(),
                blankToDash(c.getPeriodicitePaiement()), c.getMontantFinance(), c.getMontantEcheanceMensuelle(),
                c.getNombreTotalEcheances(), c.getDateDemande()
        );
    }

    private ScoreDTO toScoreDto(ScoreResult r) {
        if (!r.calculable()) return new ScoreDTO(false, null, null, null, null, null, r.message());
        return new ScoreDTO(true, r.valeur(), r.intervalle(), r.categorieRisque(), r.couleur(), ScoreColorMapper.toHex(r.couleur()), null);
    }

    private DetailContratDTO toDetailContratDto(Contrat c) {
        List<Echeance> echeances = echeanceRepository.findByContrat_IdOrderByNumeroEcheance(c.getId());
        LocalDate aujourdHui = LocalDate.now();

        List<HistoriquePaiementLigneDTO> historiquePaiement = echeances.stream()
                .filter(e -> e.getStatut() != StatutEcheance.A_VENIR)
                .map(e -> new HistoriquePaiementLigneDTO(
                        e.getDateEcheance().getYear(),
                        MOIS_LABELS_LONG[e.getDateEcheance().getMonthValue() - 1],
                        e.getStatut() == StatutEcheance.IMPAYE ? 1 : 0,
                        LabelMapper.statutEcheance(e.getStatut())
                ))
                .toList();

        int nbRestantes = (int) echeances.stream().filter(e -> e.getMontantPaye() == null).count();
        BigDecimal restantDu = echeances.stream()
                .filter(e -> e.getMontantPaye() == null && !e.getDateEcheance().isBefore(aujourdHui))
                .map(Echeance::getMontantDu).reduce(BigDecimal.ZERO, BigDecimal::add);
        int nbImpayees = (int) echeances.stream()
                .filter(e -> e.getMontantPaye() == null && e.getDateEcheance().isBefore(aujourdHui)).count();
        BigDecimal montantImpayesContrat = echeances.stream()
                .filter(e -> e.getMontantPaye() == null && e.getDateEcheance().isBefore(aujourdHui))
                .map(Echeance::getMontantDu).reduce(BigDecimal.ZERO, BigDecimal::add);

        String pireStatut = echeances.stream().anyMatch(e -> e.getStatut() == StatutEcheance.IMPAYE) ? "Impayé"
                : echeances.stream().anyMatch(e -> e.getStatut() == StatutEcheance.EN_RETARD) ? "Retard"
                : "Payé à temps";

        List<GarantieDTO> garanties = garantieRepository.findByContrat_Id(c.getId()).stream()
                .map(g -> new GarantieDTO(g.getTypeGarantie(), g.getNomGarant(), g.getCodeClientCbGarant(),
                        g.getMontantCouvert(), g.getDateDebutValidite(), g.getDateFinValidite()))
                .toList();

        LocalDate dateFin = echeances.isEmpty() ? null : echeances.get(echeances.size() - 1).getDateEcheance();

        return new DetailContratDTO(
                c.getCodeContratCb(), c.getTypeContrat(), LabelMapper.phase(c.getPhaseDemande()), LabelMapper.role(c.getRoleClient()),
                c.getDateDemande(), dateFin, c.getDevise(), c.getMontantFinance(), c.getMontantEcheanceMensuelle(),
                c.getNombreTotalEcheances(), historiquePaiement, nbRestantes, restantDu, nbImpayees, montantImpayesContrat,
                pireStatut, garanties
        );
    }

    private SyntheseDTO construireSynthese(List<Contrat> historique, Contrat contratEnCours) {
        Map<PhaseDemande, Long> parPhase = historique.stream()
                .collect(Collectors.groupingBy(Contrat::getPhaseDemande, Collectors.counting()));

        RepartitionLigneDTO financementsAvecEcheancier = new RepartitionLigneDTO(
                "Financements avec Échéancier",
                parPhase.getOrDefault(PhaseDemande.DEMANDE_EN_COURS, 0L),
                parPhase.getOrDefault(PhaseDemande.REFUSE, 0L),
                parPhase.getOrDefault(PhaseDemande.ABANDONNE, 0L),
                parPhase.getOrDefault(PhaseDemande.ACTIF, 0L),
                parPhase.getOrDefault(PhaseDemande.FERME, 0L)
        );

        BigDecimal montantRestantDu = BigDecimal.ZERO;
        BigDecimal montantImpayes = BigDecimal.ZERO;
        BigDecimal montantTotalDemandes = contratEnCours.getMontantFinance();
        LocalDate aujourdHui = LocalDate.now();

        for (Contrat c : historique) {
            montantTotalDemandes = montantTotalDemandes.add(c.getMontantFinance());
            if (!PHASES_DECAISSEES.contains(c.getPhaseDemande())) continue;

            for (Echeance e : echeanceRepository.findByContrat_IdOrderByNumeroEcheance(c.getId())) {
                if (e.getMontantPaye() != null) continue;
                if (e.getDateEcheance().isBefore(aujourdHui)) {
                    montantImpayes = montantImpayes.add(e.getMontantDu());
                } else {
                    montantRestantDu = montantRestantDu.add(e.getMontantDu());
                }
            }
        }

        BigDecimal totalGarantieSignature = historique.stream()
                .filter(c -> c.getRoleClient() == RoleClient.GARANT)
                .map(Contrat::getMontantFinance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new SyntheseDTO(
                historique.size(),
                historique.isEmpty() ? 0 : 1,
                "Non", // Contrat manquant pour réciprocité : "Non" tant qu'il n'y a qu'un seul déclarant (CECAM)
                "Ariary malgache",
                montantRestantDu, // Exposition Potentielle = Montant Total Restant dû (même formule, confirmé)
                montantRestantDu,
                montantImpayes,
                montantTotalDemandes,
                totalGarantieSignature,
                List.of(
                        financementsAvecEcheancier,
                        new RepartitionLigneDTO("Financements sans Échéancier", 0, 0, 0, 0, 0),
                        new RepartitionLigneDTO("Cartes de Crédit", 0, 0, 0, 0, 0),
                        new RepartitionLigneDTO("Services", 0, 0, 0, 0, 0)
                )
        );
    }

    private String blankToDash(String v) { return (v == null || v.isBlank()) ? "-" : v; }
}