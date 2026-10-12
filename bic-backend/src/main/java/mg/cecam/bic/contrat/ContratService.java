package mg.cecam.bic.contrat;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.audit.AuditService;
import mg.cecam.bic.client.Client;
import mg.cecam.bic.client.ClientRepository;
import mg.cecam.bic.common.enums.ModeRattachement;
import mg.cecam.bic.common.enums.MotifCloture;
import mg.cecam.bic.common.enums.OrigineDonnee;
import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.StatutEcheance;
import mg.cecam.bic.contrat.dto.ContratRequest;
import mg.cecam.bic.contrat.dto.HistoriqueContratRequest;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContratService {

    private final ContratRepository contratRepository;
    private final ClientRepository clientRepository;
    private final EcheanceRepository echeanceRepository;
    private final GarantieRepository garantieRepository;
    private final BienLeasingRepository bienLeasingRepository;
    private final ContratLienRepository contratLienRepository;
    private final HistoriquePaiementMensuelRepository historiquePaiementMensuelRepository;
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
        if (nouvellePhase == PhaseDemande.FERME || nouvellePhase == PhaseDemande.ABANDONNE) {
            if (motif != null || ancienne != nouvellePhase) {
                c.setMotifCloture(motif != null ? motif : MotifCloture.FIN_A_TERME);
            }
            if (c.getDateFinContrat() == null) c.setDateFinContrat(LocalDate.now());
        }

        Contrat enregistre = contratRepository.save(c);
        auditService.enregistrer("CONTRAT", contratId, "CHANGEMENT_PHASE",
                ancienne + " -> " + nouvellePhase);
        return enregistre;
    }

    @Transactional
    public void modifierCodesEtablissement(Long id, String codeContratEtablissement, String codeDemandeEtablissement) {
        Contrat contrat = contratRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrat introuvable"));
        contrat.setCodeContratEtablissement(codeContratEtablissement);
        contrat.setCodeDemandeEtablissement(codeDemandeEtablissement);
        contratRepository.save(contrat);
        auditService.enregistrer("CONTRAT", id, "MODIFICATION_CODES_ETABLISSEMENT", "Codes établissement modifiés");
    }

    @Transactional
public void changerEcheanceMensuelle(Long id, BigDecimal montant, String devise) {
    Contrat c = contratRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    c.setMontantEcheanceMensuelle(montant);
    c.setDevise(devise);
    contratRepository.save(c);
    auditService.enregistrer("CONTRAT", id, "MODIFICATION", "Échéance mensuelle modifiée");
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
                .codeClientCbCible(request.codeClientCbCible())
                .client(client)
                .modeRattachement(request.modeRattachement())
                .typeContrat(request.typeContrat())
                .roleClient(request.roleClient())
                .dateDemande(request.dateDemande())
                .montantFinance(request.montantFinance())
                .montantTotalDu(totalDu)
                .montantEcheanceMensuelle(request.montantEcheanceMensuelle())
                .nombreTotalEcheances(request.nombreTotalEcheances())
                .devise(request.devise())
                .origineDonnee(OrigineDonnee.NON_RENSEIGNEE)
                .periodicitePaiement(request.periodicitePaiement())
                .phaseDemande(PhaseDemande.DEMANDE_EN_COURS)
                .utilisateurDeclarant(utilisateurConnecte())
                .build();

        contrat = contratRepository.save(contrat);
        auditService.enregistrer("CONTRAT", contrat.getId(), "CREATION", "Nouvelle demande créée");
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
            .codeEtablissementDeclarant(req.codeEtablissementDeclarant())
            .utilisateurDeclarant(utilisateurConnecte())
            .codeEtablissementCrypte(req.codeEtablissementCrypte())
            .typeContrat(req.typeContrat())
            .typeRelationEntreprise(req.typeRelationEntreprise())
            .roleClient(req.roleClient())
            .modeRattachement(ModeRattachement.NOUVELLE_DEMANDE)
            .phaseDemande(req.phaseDemande())
            .dateDemande(req.dateDemande())
            .dateDebutContrat(req.dateDebutContrat())
            .dateFinContrat(req.dateFinContrat())
            .datePremiereEcheance(req.datePremiereEcheance())
            .dateDerniereModificationSource(req.dateDerniereModificationSource())
            .montantFinance(req.montantFinance())
            .montantTotalDu(req.montantTotalDu())
            .montantEcheanceMensuelle(req.montantEcheanceMensuelle())
            .nombreTotalEcheances(req.nombreTotalEcheances())
            .devise(req.devise())
            .origineDonnee(req.origineDonnee() != null ? req.origineDonnee() : OrigineDonnee.NON_RENSEIGNEE)
            .periodicitePaiement(req.periodicitePaiement())
            .modeReglement(req.modeReglement())
            .montantProchaineEcheance(req.montantProchaineEcheance())
            .dateProchaineEcheance(req.dateProchaineEcheance())
            .dateDernierReglement(req.dateDernierReglement())
            .nombreEcheancesRestantes(req.nombreEcheancesRestantes())
            .montantRestantDu(req.montantRestantDu())
            .nombreEcheancesImpayees(req.nombreEcheancesImpayees())
            .montantImpayes(req.montantImpayes())
            .maxMontantImpayes(req.maxMontantImpayes())
            .maxNombreEcheancesImpayees(req.maxNombreEcheancesImpayees())
            .dateMaxNombreEcheancesImpayees(req.dateMaxNombreEcheancesImpayees())
            .nombreJoursRetard(req.nombreJoursRetard())
            .maxNombreJoursRetard(req.maxNombreJoursRetard())
            .dateMaxNombreJoursRetard(req.dateMaxNombreJoursRetard())
            .pireStatut(req.pireStatut())
            .datePireStatut(req.datePireStatut())
            .motifCloture(req.motifCloture())
            .codeRestructuration(req.codeRestructuration())
            .dateCodeRestructuration(req.dateCodeRestructuration())
            .contratOrigine(req.contratOrigine())
            .nouveauContrat(req.nouveauContrat())
            .note(req.note())
            .build();

    Contrat sauvegarde = contratRepository.save(contrat);
    enregistrerHistoriqueLie(sauvegarde, req);

    auditService.enregistrer("CONTRAT", sauvegarde.getId(), "SAISIE_HISTORIQUE",
            "Contrat historique saisi par un administrateur");

    return sauvegarde;
}

    @Transactional
    public void modifierHistorique(Long contratId, HistoriqueContratRequest req) {
    Contrat contrat = contratRepository.findById(contratId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrat introuvable"));

    contrat.setCodeContratCb(req.codeContratCb());
    if (req.origineDonnee() != null) contrat.setOrigineDonnee(req.origineDonnee());
    contrat.setCodeContratEtablissement(req.codeContratEtablissement());
    contrat.setCodeEtablissementDeclarant(req.codeEtablissementDeclarant());
    contrat.setCodeEtablissementCrypte(req.codeEtablissementCrypte());
    contrat.setTypeContrat(req.typeContrat());
    contrat.setTypeRelationEntreprise(req.typeRelationEntreprise());
    contrat.setRoleClient(req.roleClient());
    contrat.setDateDemande(req.dateDemande());
    contrat.setDateDebutContrat(req.dateDebutContrat());
    contrat.setDateFinContrat(req.dateFinContrat());
    contrat.setDatePremiereEcheance(req.datePremiereEcheance());
    contrat.setDateDerniereModificationSource(req.dateDerniereModificationSource());
    contrat.setMontantFinance(req.montantFinance());
    contrat.setMontantTotalDu(req.montantTotalDu());
    contrat.setMontantEcheanceMensuelle(req.montantEcheanceMensuelle());
    contrat.setNombreTotalEcheances(req.nombreTotalEcheances());
    contrat.setDevise(req.devise());
    contrat.setPeriodicitePaiement(req.periodicitePaiement());
    contrat.setModeReglement(req.modeReglement());
    contrat.setMontantProchaineEcheance(req.montantProchaineEcheance());
    contrat.setDateProchaineEcheance(req.dateProchaineEcheance());
    contrat.setDateDernierReglement(req.dateDernierReglement());
    contrat.setNombreEcheancesRestantes(req.nombreEcheancesRestantes());
    contrat.setMontantRestantDu(req.montantRestantDu());
    contrat.setNombreEcheancesImpayees(req.nombreEcheancesImpayees());
    contrat.setMontantImpayes(req.montantImpayes());
    contrat.setMaxMontantImpayes(req.maxMontantImpayes());
    contrat.setMaxNombreEcheancesImpayees(req.maxNombreEcheancesImpayees());
    contrat.setDateMaxNombreEcheancesImpayees(req.dateMaxNombreEcheancesImpayees());
    contrat.setNombreJoursRetard(req.nombreJoursRetard());
    contrat.setMaxNombreJoursRetard(req.maxNombreJoursRetard());
    contrat.setDateMaxNombreJoursRetard(req.dateMaxNombreJoursRetard());
    contrat.setPireStatut(req.pireStatut());
    contrat.setDatePireStatut(req.datePireStatut());
    contrat.setPhaseDemande(req.phaseDemande());
    contrat.setMotifCloture(req.motifCloture());
    contrat.setCodeRestructuration(req.codeRestructuration());
    contrat.setDateCodeRestructuration(req.dateCodeRestructuration());
    contrat.setContratOrigine(req.contratOrigine());
    contrat.setNouveauContrat(req.nouveauContrat());
    contrat.setNote(req.note());
    contrat.setDateDerniereModification(LocalDateTime.now());
    contratRepository.save(contrat);

    supprimerHistoriqueLie(contrat);
    enregistrerHistoriqueLie(contrat, req);

    auditService.enregistrer("CONTRAT", contratId, "MODIFICATION_HISTORIQUE", "Historique modifié par admin");
}

    private String utilisateurConnecte() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) return null;
        return authentication.getName();
    }

    private void enregistrerHistoriqueLie(Contrat contrat, HistoriqueContratRequest req) {
        List<Echeance> echeances = (req.echeances() == null ? List.<HistoriqueContratRequest.EcheanceHistoriqueRequest>of() : req.echeances())
                .stream().map(e -> Echeance.builder()
                        .contrat(contrat).numeroEcheance(e.numeroEcheance()).dateEcheance(e.dateEcheance())
                        .montantDu(e.montantDu()).montantPaye(e.montantPaye()).datePaiement(e.datePaiement())
                        .statut(e.statut()).build()).toList();
        echeanceRepository.saveAll(echeances);

        List<HistoriquePaiementMensuel> mois = (req.historiquePaiementMensuel() == null
                ? List.<HistoriqueContratRequest.HistoriquePaiementMensuelRequest>of() : req.historiquePaiementMensuel())
                .stream().map(h -> HistoriquePaiementMensuel.builder()
                        .contrat(contrat).mois(h.mois().withDayOfMonth(1))
                        .nombreEcheances(h.nombreEcheances())
                        .nombreEcheancesImpayees(h.nombreEcheancesImpayees())
                        .joursDeRetard(h.joursDeRetard()).statut(h.statut())
                        .detailsSource(h.detailsSource()).build()).toList();
        historiquePaiementMensuelRepository.saveAll(mois);

        List<Garantie> garanties = (req.garanties() == null
                ? List.<HistoriqueContratRequest.GarantieHistoriqueRequest>of() : req.garanties())
                .stream().map(g -> Garantie.builder().contrat(contrat).nature(g.nature())
                        .typeGarantie(g.typeGarantie()).codeEtablissementGarantie(g.codeEtablissementGarantie())
                        .nomGarant(g.nomGarant()).codeClientCbGarant(g.codeClientCbGarant())
                        .montantCouvert(g.montantCouvert()).dateDebutValidite(g.dateDebutValidite())
                        .dateFinValidite(g.dateFinValidite()).build()).toList();
        garantieRepository.saveAll(garanties);

        (req.clientsLies() == null ? List.<HistoriqueContratRequest.ClientLieHistoriqueRequest>of() : req.clientsLies())
                .stream().map(l -> ContratLien.builder().contrat(contrat)
                .codeClientCb(l.codeClientCb()).nomClient(l.nomClient()).roleClient(l.roleClient())
                .clientLie(l.codeClientCb() == null ? null : clientRepository.findByCodeClientCb(l.codeClientCb()).orElse(null))
                .build()).forEach(contratLienRepository::save);

        if (req.bienLeasing() != null) {
            var b = req.bienLeasing();
            bienLeasingRepository.save(BienLeasing.builder().contrat(contrat).codeBien(b.codeBien())
                    .valeurBien(b.valeurBien()).etatBien(b.etatBien()).marqueBien(b.marqueBien())
                    .dateAcquisition(b.dateAcquisition()).immatriculation(b.immatriculation()).build());
        }
    }

    private void supprimerHistoriqueLie(Contrat contrat) {
        Long id = contrat.getId();
        echeanceRepository.deleteAll(echeanceRepository.findByContrat_IdOrderByNumeroEcheance(id));
        historiquePaiementMensuelRepository.deleteAll(historiquePaiementMensuelRepository.findByContrat_IdOrderByMoisDesc(id));
        garantieRepository.deleteAll(garantieRepository.findByContrat_Id(id));
        contratLienRepository.deleteAll(contratLienRepository.findByContrat_IdIn(List.of(id)));
        bienLeasingRepository.findByContrat_IdIn(List.of(id)).forEach(bienLeasingRepository::delete);
    }

    public List<Contrat> listerParClient(Long clientId) {
        return contratRepository.findByClient_Id(clientId);
    }

    public List<Contrat> listerTous() {
        return contratRepository.findAllByOrderByDateDemandeDesc();
    }

    private void genererEcheances(Contrat contrat) {
        if (contrat.getNombreTotalEcheances() == null || contrat.getNombreTotalEcheances() <= 0
                || contrat.totalDu() == null) return;
        int nb = contrat.getNombreTotalEcheances();
        int pas = contrat.pasEnMois();
        BigDecimal total = contrat.totalDu();

        LocalDate base = contrat.getDatePremiereEcheance() != null
                ? contrat.getDatePremiereEcheance()
                : (contrat.getDateDebutContrat() != null ? contrat.getDateDebutContrat()
                        : contrat.getDateDemande() != null ? contrat.getDateDemande() : LocalDate.now()).plusMonths(pas);

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
