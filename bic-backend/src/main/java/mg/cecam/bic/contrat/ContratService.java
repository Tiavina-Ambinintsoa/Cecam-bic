package mg.cecam.bic.contrat;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.audit.AuditService;
import mg.cecam.bic.client.Client;
import mg.cecam.bic.client.ClientRepository;
import mg.cecam.bic.common.enums.ModeRattachement;
import mg.cecam.bic.common.enums.MotifCloture;
import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.StatutEcheance;
import mg.cecam.bic.contrat.dto.ContratRequest;
import mg.cecam.bic.contrat.dto.HistoriqueContratRequest;

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
public void changerEcheanceMensuelle(Long id, BigDecimal montant, String devise) {
    Contrat c = contratRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    c.setMontantEcheanceMensuelle(montant);
    c.setDevise(devise);
    contratRepository.save(c);
    auditService.enregistrer("CONTRAT", id, "MODIFICATION", "Échéance mensuelle modifiée : " + montant);
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

    @Transactional
public Contrat saisirHistorique(HistoriqueContratRequest req) {
    Client client = clientRepository.findById(req.clientId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client introuvable"));

    Contrat contrat = Contrat.builder()
            .client(client)
            .codeContratCb(req.codeContratCb())
            .codeContratEtablissement(req.codeContratEtablissement())
            .typeContrat(req.typeContrat())
            .roleClient(req.roleClient())
            .modeRattachement(ModeRattachement.NOUVELLE_DEMANDE)
            .phaseDemande(req.phaseDemande())
            .dateDemande(req.dateDemande())
            .dateDebutContrat(req.dateDebutContrat())
            .dateFinContrat(req.dateFinContrat())
            .montantFinance(req.montantFinance())
            .montantTotalDu(req.montantTotalDu() != null ? req.montantTotalDu() : req.montantFinance())
            .montantEcheanceMensuelle(req.montantEcheanceMensuelle())
            .nombreTotalEcheances(req.nombreTotalEcheances())
            .devise(req.devise())
            .periodicitePaiement(req.periodicitePaiement())
            .build();

    Contrat sauvegarde = contratRepository.save(contrat);

    List<Echeance> echeances = req.echeances().stream()
            .map(e -> Echeance.builder()
                    .contrat(sauvegarde)
                    .numeroEcheance(e.numeroEcheance())
                    .dateEcheance(e.dateEcheance())
                    .montantDu(e.montantDu())
                    .montantPaye(e.montantPaye())
                    .datePaiement(e.datePaiement())
                    .statut(e.statut())
                    .build())
            .toList();
    echeanceRepository.saveAll(echeances);

    auditService.enregistrer("CONTRAT", sauvegarde.getId(), "SAISIE_HISTORIQUE",
            "Contrat historique saisi manuellement par admin : " + req.codeContratCb());

    return sauvegarde;
}

@Transactional
public void modifierHistorique(Long contratId, HistoriqueContratRequest req) {
    Contrat contrat = contratRepository.findById(contratId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrat introuvable"));

    contrat.setPhaseDemande(req.phaseDemande());
    contrat.setDateDebutContrat(req.dateDebutContrat());
    contrat.setDateFinContrat(req.dateFinContrat());
    contrat.setMontantFinance(req.montantFinance());
    contrat.setMontantTotalDu(req.montantTotalDu());
    contrat.setMontantEcheanceMensuelle(req.montantEcheanceMensuelle());
    contratRepository.save(contrat);

    echeanceRepository.deleteAll(echeanceRepository.findByContrat_IdOrderByNumeroEcheance(contratId));
    List<Echeance> echeances = req.echeances().stream()
            .map(e -> Echeance.builder()
                    .contrat(contrat).numeroEcheance(e.numeroEcheance()).dateEcheance(e.dateEcheance())
                    .montantDu(e.montantDu()).montantPaye(e.montantPaye()).datePaiement(e.datePaiement())
                    .statut(e.statut())
                    .build())
            .toList();
    echeanceRepository.saveAll(echeances);

    auditService.enregistrer("CONTRAT", contratId, "MODIFICATION_HISTORIQUE", "Historique modifié par admin");
}

    public List<Contrat> listerParClient(Long clientId) {
        return contratRepository.findByClient_Id(clientId);
    }

    public List<Contrat> listerTous() {
        return contratRepository.findAllByOrderByDateDemandeDesc();
    }

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

    private String genererCodeContratCb() {
        Long n = contratRepository.prochainCodeContratCb();
        return String.valueOf(n);
    }
}
