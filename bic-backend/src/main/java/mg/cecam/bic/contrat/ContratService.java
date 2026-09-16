// mg/cecam/bic/contrat/ContratService.java  — MODIFIÉ
package mg.cecam.bic.contrat;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.audit.AuditService;
import mg.cecam.bic.client.Client;
import mg.cecam.bic.client.ClientRepository;
import mg.cecam.bic.common.enums.MotifCloture;
import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.StatutEcheance;
import mg.cecam.bic.contrat.dto.ContratRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContratService {

    private final ContratRepository contratRepository;
    private final ClientRepository clientRepository;
    private final EcheanceRepository echeanceRepository;
    private final AuditService auditService;

    /**
     * Le passage à ACTIF déclenche le déblocage : c'est là que se fixent la
     * date de début de contrat et l'échéancier, et non à la saisie de la
     * demande. La version initiale générait l'échéancier dès la création,
     * y compris pour des demandes jamais accordées.
     */
    @Transactional
    public Contrat changerPhase(Long contratId, PhaseDemande nouvellePhase, MotifCloture motif) {
        Contrat c = contratRepository.findById(contratId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrat introuvable"));

        PhaseDemande ancienne = c.getPhaseDemande();
        c.setPhaseDemande(nouvellePhase);

        if (nouvellePhase == PhaseDemande.ACTIF && ancienne != PhaseDemande.ACTIF) {
            if (c.getDateDebutContrat() == null) c.setDateDebutContrat(LocalDate.now());
            if (echeanceRepository.findByContrat_IdOrderByNumeroEcheance(contratId).isEmpty()) {
                genererEcheances(c);
            }
        }
        if (nouvellePhase == PhaseDemande.FERME) {
            c.setMotifCloture(motif != null ? motif : MotifCloture.FIN_A_TERME);
            if (c.getDateFinContrat() == null) c.setDateFinContrat(LocalDate.now());
        }

        Contrat enregistre = contratRepository.save(c);
        auditService.enregistrer("CONTRAT", contratId, "CHANGEMENT_PHASE",
                ancienne + " -> " + nouvellePhase);
        return enregistre;
    }

    @Transactional
    public Contrat creerDemande(ContratRequest request) {
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Client introuvable : " + request.clientId()));

        BigDecimal totalDu = request.montantEcheanceMensuelle() != null
                ? request.montantEcheanceMensuelle()
                        .multiply(BigDecimal.valueOf(request.nombreTotalEcheances()))
                : request.montantFinance();

        Contrat contrat = Contrat.builder()
                .codeContratCb(genererCodeContratCb())
                .codeContratEtablissement(request.codeContratEtablissement())
                .client(client)
                .modeRattachement(request.modeRattachement())
                .typeContrat(request.typeContrat())
                .typeRelationEntreprise(request.typeRelationEntreprise())
                .roleClient(request.roleClient())
                .dateDemande(request.dateDemande())
                .montantFinance(request.montantFinance())
                .montantTotalDu(totalDu)
                .montantEcheanceMensuelle(request.montantEcheanceMensuelle())
                .nombreTotalEcheances(request.nombreTotalEcheances())
                .devise(request.devise())
                .periodicitePaiement(request.periodicitePaiement())
                .phaseDemande(PhaseDemande.DEMANDE_EN_COURS)
                .build();

        contrat = contratRepository.save(contrat);
        auditService.enregistrer("CONTRAT", contrat.getId(), "CREATION",
                "Montant " + contrat.getMontantFinance());
        return contrat;
    }

    public List<Contrat> listerParClient(Long clientId) {
        return contratRepository.findByClient_Id(clientId);
    }

    public List<Contrat> listerTous() {
        return contratRepository.findAllByOrderByDateDemandeDesc();
    }

    /**
     * Trois corrections par rapport à la version initiale :
     *
     *  1. La périodicité est respectée. Avant, plusMonths(i) imposait le
     *     mensuel même sur un contrat trimestriel.
     *  2. Le reste de division tombe sur la DERNIÈRE échéance. Avant,
     *     500 000 sur 6 à 83 333 donnait 499 998 : 2 Ar disparaissaient,
     *     et le restant dû ne pouvait jamais atteindre le montant financé.
     *  3. L'échéancier part de datePremiereEcheance (ou de la date de
     *     déblocage), pas de la date de demande : un différé de
     *     remboursement est désormais représentable.
     */
    private void genererEcheances(Contrat contrat) {
        int nb = contrat.getNombreTotalEcheances();
        int pas = contrat.pasEnMois();
        BigDecimal total = contrat.totalDu();

        LocalDate base = contrat.getDatePremiereEcheance() != null
                ? contrat.getDatePremiereEcheance()
                : (contrat.getDateDebutContrat() != null ? contrat.getDateDebutContrat() : contrat.getDateDemande())
                        .plusMonths(pas);

        BigDecimal unitaire = contrat.getMontantEcheanceMensuelle() != null
                ? contrat.getMontantEcheanceMensuelle()
                : total.divide(BigDecimal.valueOf(nb), 0, RoundingMode.DOWN);

        List<Echeance> echeances = new ArrayList<>(nb);
        BigDecimal cumul = BigDecimal.ZERO;

        for (int i = 1; i <= nb; i++) {
            BigDecimal montant = (i == nb) ? total.subtract(cumul) : unitaire;
            cumul = cumul.add(montant);
            echeances.add(Echeance.builder()
                    .contrat(contrat)
                    .numeroEcheance(i)
                    .dateEcheance(base.plusMonths((long) (i - 1) * pas))
                    .montantDu(montant)
                    .statut(StatutEcheance.A_VENIR)
                    .build());
        }

        if (contrat.getDatePremiereEcheance() == null) contrat.setDatePremiereEcheance(base);
        if (contrat.getDateFinContrat() == null) {
            contrat.setDateFinContrat(echeances.get(echeances.size() - 1).getDateEcheance());
        }
        echeanceRepository.saveAll(echeances);
    }

    /**
     * Séquence PostgreSQL au lieu de Math.random() sur une colonne unique.
     * À créer une fois :
     *   CREATE SEQUENCE IF NOT EXISTS seq_code_contrat_cb START WITH 700000001;
     */
    private String genererCodeContratCb() {
        Long n = contratRepository.prochainCodeContratCb();
        return String.valueOf(n);
    }
}