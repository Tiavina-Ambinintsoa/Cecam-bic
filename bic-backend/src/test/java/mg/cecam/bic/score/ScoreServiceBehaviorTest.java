package mg.cecam.bic.score;

import mg.cecam.bic.client.Client;
import mg.cecam.bic.client.EmploiRepository;
import mg.cecam.bic.client.EvenementNegatifRepository;
import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.RoleClient;
import mg.cecam.bic.common.enums.StatutEcheance;
import mg.cecam.bic.contrat.*;
import mg.cecam.bic.referentiel.GrilleScore;
import mg.cecam.bic.referentiel.GrilleScoreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScoreServiceBehaviorTest {
    @Mock private GrilleScoreRepository grilleScoreRepository;
    @Mock private ContratRepository contratRepository;
    @Mock private ContratLienRepository contratLienRepository;
    @Mock private EcheanceRepository echeanceRepository;
    @Mock private HistoriquePaiementMensuelRepository historiquePaiementMensuelRepository;
    @Mock private EmploiRepository emploiRepository;
    @Mock private RevenuDemandeRepository revenuDemandeRepository;
    @Mock private EvenementNegatifRepository evenementNegatifRepository;
    @Mock private DernierContratRepository dernierContratRepository;

    private ScoreService service;
    private final ScoreProperties properties = new ScoreProperties();
    private final Client client = Client.builder().id(7L).build();
    private final Contrat demande = Contrat.builder()
            .id(70L)
            .client(client)
            .roleClient(RoleClient.TITULAIRE)
            .phaseDemande(PhaseDemande.DEMANDE_EN_COURS)
            .devise("MGA")
            .ratioRestePourcentage(BigDecimal.valueOf(75))
            .build();

    @BeforeEach
    void setUp() {
        service = new ScoreService(grilleScoreRepository, contratRepository, contratLienRepository,
                echeanceRepository, historiquePaiementMensuelRepository, emploiRepository,
                revenuDemandeRepository, evenementNegatifRepository, dernierContratRepository, properties);
        when(grilleScoreRepository.findAll()).thenReturn(grille());
        when(contratRepository.findPourClient(client.getId())).thenReturn(List.of());
        when(evenementNegatifRepository.findByClient_IdAndActifTrue(client.getId())).thenReturn(List.of());
        when(dernierContratRepository.findByContrat_IdOrderByDateOctroiAsc(demande.getId())).thenReturn(List.of());
        when(emploiRepository.findByClient_Id(client.getId())).thenReturn(Optional.empty());
        when(revenuDemandeRepository.findByContrat_Id(demande.getId())).thenReturn(List.of());
    }

    @Test
    void statutInconnuEtAbsenceDeGarantieNeFabriquentPasUnBonHistoriqueNiUnPlafondDeGarantie() {
        when(dernierContratRepository.findByContrat_IdOrderByDateOctroiAsc(demande.getId()))
                .thenReturn(List.of(DernierContrat.builder().statut("statut inconnu").build()));

        ScoreResult result = service.calculer(client, demande, false);

        assertTrue(result.calculable());
        assertEquals(properties.getPlafondSansHistorique(), result.valeur());
        assertFalse(result.composantes().containsKey("paiement"));
        assertTrue(result.message().contains("statut absent ou inconnu"));
        assertFalse(result.plafonds().stream().anyMatch(p -> p.contains("garantie")));
    }

    @Test
    void contratDeclareNonRemboursePlafonneMemeAvecHistoriqueReel() {
        Contrat rembourse = contratHistorique(1L);
        when(contratRepository.findPourClient(client.getId())).thenReturn(List.of(rembourse));
        when(echeanceRepository.findByContrat_IdOrderByNumeroEcheance(rembourse.getId())).thenReturn(List.of());
        when(historiquePaiementMensuelRepository.findByContrat_IdOrderByMoisDesc(rembourse.getId()))
                .thenReturn(List.of(HistoriquePaiementMensuel.builder()
                        .mois(LocalDate.now().withDayOfMonth(1))
                        .joursDeRetard(0)
                        .statut("À jour")
                        .build()));
        when(dernierContratRepository.findByContrat_IdOrderByDateOctroiAsc(demande.getId()))
                .thenReturn(List.of(DernierContrat.builder().statut("NON_REMBOURSE").build()));

        ScoreResult result = service.calculer(client, demande, true);

        assertTrue(result.calculable());
        assertTrue(result.valeur() <= properties.getPlafondCreditNonRembourse());
        assertTrue(result.plafonds().stream().anyMatch(p -> p.startsWith("399|crédit antérieur déclaré")));
    }

    @Test
    void observationMensuelleEtEcheanceDuMemeMoisNeComptentQuUneFois() {
        Contrat rembourse = contratHistorique(2L);
        LocalDate reference = LocalDate.now(ZoneId.of("Indian/Antananarivo"));
        LocalDate echeanceDate = reference.minusDays(10);
        when(contratRepository.findPourClient(client.getId())).thenReturn(List.of(rembourse));
        when(echeanceRepository.findByContrat_IdOrderByNumeroEcheance(rembourse.getId()))
                .thenReturn(List.of(Echeance.builder()
                        .dateEcheance(echeanceDate)
                        .montantDu(BigDecimal.valueOf(100))
                        .montantPaye(BigDecimal.ZERO)
                        .statut(StatutEcheance.EN_RETARD)
                        .build()));
        when(historiquePaiementMensuelRepository.findByContrat_IdOrderByMoisDesc(rembourse.getId()))
                .thenReturn(List.of(HistoriquePaiementMensuel.builder()
                        .mois(echeanceDate.withDayOfMonth(1))
                        .nombreEcheances(1)
                        .joursDeRetard(0)
                        .statut("À jour")
                        .build()));

        ScoreResult result = service.calculer(client, demande, true, reference);

        double ratioRetard = (10.0 - properties.getToleranceRetardJours())
                / (30.0 - properties.getToleranceRetardJours());
        double qualiteRetard = properties.getQualitePayeATemps()
                - (properties.getQualitePayeATemps() - properties.getQualiteRetardCourt()) * ratioRetard;
        double facteurConfiance = properties.getPlancherConfiance()
                + (1 - properties.getPlancherConfiance()) / properties.getObservationsPleineConfiance();
        double attendu = properties.getPoidsPaiement() * qualiteRetard * facteurConfiance;
        assertEquals(attendu, result.composantes().get("paiement"), 0.1);
    }

    @Test
    void historiqueResumeDuDossierInterneNestPasPlafonneEnClasseCParDefaut() {
        LocalDate dateRemboursement = LocalDate.now().minusMonths(1);
        when(dernierContratRepository.findByContrat_IdOrderByDateOctroiAsc(demande.getId()))
                .thenReturn(List.of(
                        contratDeclare(dateRemboursement),
                        contratDeclare(dateRemboursement.minusMonths(2)),
                        contratDeclare(dateRemboursement.minusMonths(4))));

        ScoreResult result = service.calculer(client, demande, true);

        assertTrue(result.calculable());
        assertTrue(result.valeur() > properties.getPlafondClasseC());
        assertFalse(result.plafonds().stream().anyMatch(p -> p.contains("historique uniquement déclaré")));
        assertFalse(result.message().contains("Justificatif recommandé"));
    }

    @Test
    void couvertureInferieureA50PourcentPlafonneLeScoreGlobalEnD() {
        ScoreResult result = service.calculer(client, demandeAvecCouverture("49.99"), true,
                LocalDate.of(2026, 10, 23));

        assertEquals(459, result.valeur());
        assertEquals("D", result.intervalle());
        assertTrue(result.plafonds().stream().anyMatch(p -> p.contains("couverture de garantie 49.99 %")));
    }

    @Test
    void couvertureDe50AmoinsDe100PourcentPlafonneLeScoreGlobalEnC() {
        LocalDate reference = LocalDate.of(2026, 10, 23);
        preparerHistoriqueDeclare(reference);
        ScoreResult result = service.calculer(client, demandeAvecCouverture("83"), true, reference);
        ScoreResult borneBasse = service.calculer(client, demandeAvecCouverture("50"), true, reference);

        assertEquals(499, result.valeur());
        assertEquals("C", result.intervalle());
        assertTrue(result.plafonds().stream().anyMatch(p -> p.contains("couverture de garantie 83 %")));
        assertEquals(499, borneBasse.valeur());
        assertEquals("C", borneBasse.intervalle());
    }

    @Test
    void couvertureAuMoinsEgaleA100PourcentNePlafonnePasLeScoreDeBase() {
        LocalDate reference = LocalDate.of(2026, 10, 23);
        preparerHistoriqueDeclare(reference);
        ScoreResult sansGarantie = service.calculer(client, demande, true, reference);
        ScoreResult avecCouverture = service.calculer(client, demandeAvecCouverture("100"), true, reference);

        assertTrue(sansGarantie.valeur() > properties.getPlafondClasseC());
        assertEquals(sansGarantie.valeur(), avecCouverture.valeur());
        assertEquals(sansGarantie.intervalle(), avecCouverture.intervalle());
        assertFalse(avecCouverture.plafonds().stream().anyMatch(p -> p.contains("couverture de garantie")));
    }

    @Test
    void ratioDeCouvertureEstCalculeQuandLeChampNEstPasEnregistre() {
        Contrat contrat = Contrat.builder()
                .valeurGarantie(BigDecimal.ZERO)
                .montantTotalDemandeInteret(BigDecimal.valueOf(1000))
                .build();

        assertEquals(new BigDecimal("0.00"), CouvertureGarantie.ratioPour(contrat));
    }

    private Contrat contratHistorique(Long id) {
        return Contrat.builder()
                .id(id)
                .client(client)
                .roleClient(RoleClient.TITULAIRE)
                .phaseDemande(PhaseDemande.FERME)
                .build();
    }

    private Contrat demandeAvecCouverture(String ratio) {
        return Contrat.builder()
                .id(demande.getId())
                .client(client)
                .roleClient(RoleClient.TITULAIRE)
                .phaseDemande(PhaseDemande.DEMANDE_EN_COURS)
                .devise("MGA")
                .ratioRestePourcentage(BigDecimal.valueOf(75))
                .ratioCouvertureGarantie(new BigDecimal(ratio))
                .build();
    }

    private void preparerHistoriqueDeclare(LocalDate reference) {
        when(dernierContratRepository.findByContrat_IdOrderByDateOctroiAsc(demande.getId()))
                .thenReturn(List.of(
                        contratDeclare(reference.minusMonths(1)),
                        contratDeclare(reference.minusMonths(2)),
                        contratDeclare(reference.minusMonths(3))));
    }

    private DernierContrat contratDeclare(LocalDate dateRemboursement) {
        return DernierContrat.builder()
                .statut("A_TEMPS")
                .dateRemboursement(dateRemboursement)
                .build();
    }

    private List<GrilleScore> grille() {
        return List.of(
                grille("A", 530, 850),
                grille("B", 500, 529),
                grille("C", 460, 499),
                grille("D", 400, 459),
                grille("E", 300, 399));
    }

    private GrilleScore grille(String intervalle, int min, int max) {
        return GrilleScore.builder().intervalle(intervalle).scoreMin(min).scoreMax(max)
                .categorieRisque(intervalle).couleur("Rouge Vif").build();
    }
}
