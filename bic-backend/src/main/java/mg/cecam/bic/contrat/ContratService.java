package mg.cecam.bic.contrat;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.audit.AuditService;
import mg.cecam.bic.client.Client;
import mg.cecam.bic.client.ClientRepository;
import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.StatutEcheance;
import mg.cecam.bic.contrat.dto.ContratRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContratService {

    private final ContratRepository contratRepository;
    private final ClientRepository clientRepository;
    private final EcheanceRepository echeanceRepository;
    private final AuditService auditService;

    @Transactional
    public Contrat changerPhase(Long contratId, PhaseDemande nouvellePhase) {
        Contrat c = contratRepository.findById(contratId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrat introuvable"));
        c.setPhaseDemande(nouvellePhase);
        Contrat enregistre = contratRepository.save(c);
        auditService.enregistrer("CONTRAT", contratId, "CHANGEMENT_PHASE", "Nouvelle phase : " + nouvellePhase);
        return enregistre;
    }

    @Transactional
    public Contrat creerDemande(ContratRequest request) {
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client introuvable : " + request.clientId()));

        Contrat contrat = Contrat.builder()
                .codeContratCb(genererCodeContratCb())
                .client(client)
                .modeRattachement(request.modeRattachement())
                .typeContrat(request.typeContrat())
                .typeRelationEntreprise(request.typeRelationEntreprise())
                .roleClient(request.roleClient())
                .dateDemande(request.dateDemande())
                .montantFinance(request.montantFinance())
                .montantEcheanceMensuelle(request.montantEcheanceMensuelle())
                .nombreTotalEcheances(request.nombreTotalEcheances())
                .devise(request.devise())
                .periodicitePaiement(request.periodicitePaiement())
                .phaseDemande(PhaseDemande.DEMANDE_EN_COURS)
                .build();

        contrat = contratRepository.save(contrat);
        genererEcheances(contrat);
        auditService.enregistrer("CONTRAT", contrat.getId(), "CREATION", "Montant " + contrat.getMontantFinance());
        return contrat;
    }

    public List<Contrat> listerParClient(Long clientId) {
        return contratRepository.findByClient_Id(clientId);
    }

    public List<Contrat> listerTous() {
        return contratRepository.findAllByOrderByDateDemandeDesc();
    }

    private void genererEcheances(Contrat contrat) {
        BigDecimal montantParEcheance = contrat.getMontantEcheanceMensuelle() != null
                ? contrat.getMontantEcheanceMensuelle()
                : contrat.getMontantFinance().divide(BigDecimal.valueOf(contrat.getNombreTotalEcheances()), 2, RoundingMode.HALF_UP);

        List<Echeance> echeances = new ArrayList<>();
        for (int i = 1; i <= contrat.getNombreTotalEcheances(); i++) {
            echeances.add(Echeance.builder()
                    .contrat(contrat)
                    .numeroEcheance(i)
                    .dateEcheance(contrat.getDateDemande().plusMonths(i))
                    .montantDu(montantParEcheance)
                    .statut(StatutEcheance.A_VENIR)
                    .build());
        }
        echeanceRepository.saveAll(echeances);
    }

    private String genererCodeContratCb() {
        return String.valueOf(600_000_000L + (long) (Math.random() * 99_999_999L));
    }
}