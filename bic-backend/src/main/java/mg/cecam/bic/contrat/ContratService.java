// bic-backend/src/main/java/mg/cecam/bic/contrat/ContratService.java (remplace)
package mg.cecam.bic.contrat;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.audit.AuditService;
import mg.cecam.bic.client.Client;
import mg.cecam.bic.client.ClientRepository;
import mg.cecam.bic.common.enums.ModeRattachement;
import mg.cecam.bic.common.enums.MotifCloture;
import mg.cecam.bic.common.enums.NatureGarantie;
import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.RoleClient;
import mg.cecam.bic.common.enums.StatutEcheance;
import mg.cecam.bic.contrat.dto.ContratRequest;
import mg.cecam.bic.contrat.dto.HistoriqueContratRequest;
import mg.cecam.bic.contrat.dto.HistoriqueContratRequest.EcheanceHistoriqueRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ContratService {

    private final ContratRepository contratRepository;
    private final ClientRepository clientRepository;
    private final EcheanceRepository echeanceRepository;
    private final GarantieRepository garantieRepository;
    private final ContratLienRepository contratLienRepository;
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
        } else if (motif != null
                && (nouvellePhase == PhaseDemande.ABANDONNE || nouvellePhase == PhaseDemande.REFUSE)) {
            c.setMotifCloture(motif);
        }

        Contrat enregistre = contratRepository.save(c);
        auditService.enregistrer("CONTRAT", contratId, "CHANGEMENT_PHASE", ancienne + " -> " + nouvellePhase);
        return enregistre;
    }

    @Transactional
    public void changerEcheanceMensuelle(Long id, BigDecimal montant, String devise) {
        Contrat c = contratRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrat introuvable"));
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

    // ------------------------------------------------------------------
    // Saisie d'historique (ADMIN)
    // ------------------------------------------------------------------

    @Transactional
    public Contrat saisirHistorique(HistoriqueContratRequest req) {
        Client client = clientRepository.findById(req.clientId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Client introuvable : " + req.clientId()));

        List<EcheanceHistoriqueRequest> saisies = verifierHistorique(req);

        Contrat contrat = Contrat.builder()
                .client(client)
                .modeRattachement(ModeRattachement.NOUVELLE_DEMANDE)
                .codeContratCb(req.codeContratCb() != null && !req.codeContratCb().isBlank()
                        ? req.codeContratCb().trim()
                        : genererCodeContratCb())
                .build();
        appliquer(contrat, req, saisies);

        Contrat sauvegarde;
        try {
            sauvegarde = contratRepository.saveAndFlush(contrat);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ce code contrat CB existe déjà : " + contrat.getCodeContratCb());
        }

        echeanceRepository.saveAll(construireEcheances(sauvegarde, saisies));
        enregistrerAnnexes(sauvegarde, req, false);
        auditService.enregistrer("CONTRAT", sauvegarde.getId(), "SAISIE_HISTORIQUE",
                "Contrat historique saisi par un administrateur : " + sauvegarde.getCodeContratCb());
        return sauvegarde;
    }

    @Transactional
    public void modifierHistorique(Long contratId, HistoriqueContratRequest req) {
        Contrat contrat = contratRepository.findById(contratId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrat introuvable"));

        if (!contrat.getClient().getId().equals(req.clientId())) {
            throw invalide("Ce contrat n'appartient pas au client indiqué.");
        }

        List<EcheanceHistoriqueRequest> saisies = verifierHistorique(req);
        appliquer(contrat, req, saisies);
        contratRepository.save(contrat);

        // Le code contrat CB est un identifiant : il n'est jamais modifié ici.
        echeanceRepository.deleteAll(echeanceRepository.findByContrat_IdOrderByNumeroEcheance(contratId));
        echeanceRepository.saveAll(construireEcheances(contrat, saisies));
        enregistrerAnnexes(contrat, req, true);

        auditService.enregistrer("CONTRAT", contratId, "MODIFICATION_HISTORIQUE",
                "Historique modifié par un administrateur");
    }

    /** Garanties et clients liés. Une liste null laisse l'existant intact. */
    private void enregistrerAnnexes(Contrat contrat, HistoriqueContratRequest req, boolean remplacer) {
        if (req.garanties() != null) {
            if (remplacer) garantieRepository.deleteAll(garantieRepository.findByContrat_Id(contrat.getId()));
            List<Garantie> garanties = req.garanties().stream()
                    .map(g -> Garantie.builder()
                            .contrat(contrat)
                            .nature(g.nature() != null ? g.nature() : NatureGarantie.REELLE)
                            .typeGarantie(g.typeGarantie().trim())
                            .codeEtablissementGarantie(vide(g.codeEtablissementGarantie()))
                            .nomGarant(vide(g.nomGarant()))
                            .codeClientCbGarant(vide(g.codeClientCbGarant()))
                            .montantCouvert(g.montantCouvert())
                            .dateDebutValidite(g.dateDebutValidite())
                            .dateFinValidite(g.dateFinValidite())
                            .build())
                    .toList();
            garantieRepository.saveAll(garanties);
        }

        if (req.liens() != null) {
            if (remplacer) {
                contratLienRepository.deleteAll(contratLienRepository.findByContrat_IdIn(List.of(contrat.getId())));
            }
            String codeTitulaire = contrat.getClient().getCodeClientCb();
            List<ContratLien> liens = new ArrayList<>();
            for (HistoriqueContratRequest.LienSaisie l : req.liens()) {
                String code = l.codeClientCb().trim();
                if (code.equals(codeTitulaire)) {
                    throw invalide("Un client ne peut pas être lié à son propre contrat : " + code);
                }
                if (l.roleClient() == RoleClient.TITULAIRE) {
                    throw invalide("Le titulaire est le client du contrat : liez un co-titulaire ou un garant.");
                }
                String nom = vide(l.nomClient());
                if (nom == null) {
                    nom = clientRepository.findByCodeClientCb(code).map(this::nomComplet).orElse(null);
                }
                liens.add(ContratLien.builder()
                        .contrat(contrat).codeClientCb(code).nomClient(nom).roleClient(l.roleClient()).build());
            }
            contratLienRepository.saveAll(liens);
        }
    }

    /** Règles d'intégrité de la saisie ; renvoie les échéances triées par numéro. */
    private List<EcheanceHistoriqueRequest> verifierHistorique(HistoriqueContratRequest req) {
        List<EcheanceHistoriqueRequest> saisies = req.echeances() == null
                ? List.of()
                : req.echeances().stream()
                        .sorted(Comparator.comparing(EcheanceHistoriqueRequest::numeroEcheance))
                        .toList();

        boolean decaisse = req.phaseDemande() == PhaseDemande.ACTIF || req.phaseDemande() == PhaseDemande.FERME;
        if (decaisse && saisies.isEmpty()) {
            throw invalide("Un contrat Actif ou Fermé doit avoir au moins une échéance.");
        }

        if (req.dateDebutContrat() != null && req.dateFinContrat() != null
                && req.dateFinContrat().isBefore(req.dateDebutContrat())) {
            throw invalide("La date de fin de contrat est antérieure à la date de début.");
        }

        LocalDate aujourdHui = LocalDate.now();
        Set<Integer> numeros = new HashSet<>();
        for (EcheanceHistoriqueRequest e : saisies) {
            int n = e.numeroEcheance();
            if (n <= 0 || !numeros.add(n)) {
                throw invalide("Numéro d'échéance invalide ou en double : " + n);
            }
            if (e.montantDu().signum() <= 0) {
                throw invalide("Échéance " + n + " : le montant dû doit être positif.");
            }
            if (e.montantPaye() != null && e.montantPaye().signum() < 0) {
                throw invalide("Échéance " + n + " : le montant payé ne peut pas être négatif.");
            }
            boolean payee = e.montantPaye() != null && e.montantPaye().signum() > 0;
            if (payee && e.datePaiement() == null) {
                throw invalide("Échéance " + n + " : la date de paiement est obligatoire quand un montant est payé.");
            }
            if (e.datePaiement() != null && e.datePaiement().isAfter(aujourdHui)) {
                throw invalide("Échéance " + n + " : la date de paiement est dans le futur.");
            }
        }
        return saisies;
    }

    private void appliquer(Contrat c, HistoriqueContratRequest req, List<EcheanceHistoriqueRequest> saisies) {
        c.setCodeContratEtablissement(vide(req.codeContratEtablissement()));
        if (vide(req.codeEtablissementDeclarant()) != null) {
            c.setCodeEtablissementDeclarant(req.codeEtablissementDeclarant().trim());
        }
        c.setTypeContrat(req.typeContrat());
        c.setTypeRelationEntreprise(vide(req.typeRelationEntreprise()));
        c.setRoleClient(req.roleClient());
        c.setPhaseDemande(req.phaseDemande());

        MotifCloture motif = req.motifCloture();
        if (motif == null && req.phaseDemande() == PhaseDemande.FERME) motif = MotifCloture.FIN_A_TERME;
        c.setMotifCloture(motif);

        c.setDateDemande(req.dateDemande());
        c.setDateDebutContrat(req.dateDebutContrat());

        LocalDate premiere = saisies.stream().map(EcheanceHistoriqueRequest::dateEcheance)
                .min(Comparator.naturalOrder()).orElse(null);
        LocalDate derniere = saisies.stream().map(EcheanceHistoriqueRequest::dateEcheance)
                .max(Comparator.naturalOrder()).orElse(null);
        c.setDatePremiereEcheance(req.datePremiereEcheance() != null ? req.datePremiereEcheance() : premiere);
        c.setDateFinContrat(req.dateFinContrat() != null ? req.dateFinContrat() : derniere);

        c.setMontantFinance(req.montantFinance());
        c.setMontantTotalDu(req.montantTotalDu() != null ? req.montantTotalDu() : req.montantFinance());
        c.setMontantEcheanceMensuelle(req.montantEcheanceMensuelle());
        c.setNombreTotalEcheances(req.nombreTotalEcheances());
        c.setDevise(req.devise());
        c.setPeriodicitePaiement(vide(req.periodicitePaiement()));
        c.setCodeRestructuration(vide(req.codeRestructuration()));
        c.setDateCodeRestructuration(req.dateCodeRestructuration());
        c.setNote(vide(req.note()));
    }

    /** Le statut stocké est dérivé des dates et du paiement : jamais saisi à la main. */
    private List<Echeance> construireEcheances(Contrat contrat, List<EcheanceHistoriqueRequest> saisies) {
        LocalDate reference = LocalDate.now();
        List<Echeance> resultat = new ArrayList<>(saisies.size());
        for (EcheanceHistoriqueRequest e : saisies) {
            Echeance echeance = Echeance.builder()
                    .contrat(contrat)
                    .numeroEcheance(e.numeroEcheance())
                    .dateEcheance(e.dateEcheance())
                    .montantDu(e.montantDu())
                    .montantPaye(e.montantPaye())
                    .datePaiement(e.datePaiement())
                    .statut(StatutEcheance.A_VENIR)
                    .build();
            echeance.setStatut(echeance.statutEffectif(reference));
            resultat.add(echeance);
        }
        return resultat;
    }

    private ResponseStatusException invalide(String message) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }

    private String vide(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    private String nomComplet(Client c) {
        return ((c.getPrenom() == null ? "" : c.getPrenom()) + " "
                + (c.getNom() == null ? "" : c.getNom())).trim();
    }

    // ------------------------------------------------------------------

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