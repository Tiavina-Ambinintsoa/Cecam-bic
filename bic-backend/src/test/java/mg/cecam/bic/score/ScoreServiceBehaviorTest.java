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
        LocalDate echeanceDate = LocalDate.now().minusDays(10);
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

        ScoreResult result = service.calculer(client, demande, true);

        double attendu = properties.getPoidsPaiement() * properties.getQualiteRetardCourt()
                * (properties.getPlancherConfiance()
                + (1 - properties.getPlancherConfiance()) / properties.getObservationsPleineConfiance());
        assertEquals(attendu, result.composantes().get("paiement"), 0.1);
    }

    private Contrat contratHistorique(Long id) {
        return Contrat.builder()
                .id(id)
                .client(client)
                .roleClient(RoleClient.TITULAIRE)
                .phaseDemande(PhaseDemande.FERME)
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
