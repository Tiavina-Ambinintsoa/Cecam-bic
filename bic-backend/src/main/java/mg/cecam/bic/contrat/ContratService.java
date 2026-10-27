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
import mg.cecam.bic.score.ScoreService;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Service métier pour la gestion du cycle de vie des contrats de crédit et des demandes.
 * <p>
 * Responsabilités principales :
 * <ul>
 *   <li><b>Origination de crédit (créerDemande)</b> : Calcul des échéances, des intérêts conventionnels
 *       (taux mensuel paramétré par type de crédit RFR, PRO, GCV...), ratios de couverture de garantie
 *       et de patrimoine, et enregistrement des antécédents déclarés vérifiés.</li>
 *   <li><b>Cycle de vie & Transitions de phases (changerPhase)</b> : Passage de DEMANDE_EN_COURS à ACTIF,
 *       génération automatique de l'échéancier selon la périodicité (mensuelle, trimestrielle...),
 *       clôture (FERME, ABANDONNE) avec motif réglementaire.</li>
 *   <li><b>Administration & Historique (saisirHistorique, modifierHistorique)</b> : Gestion sous
 *       contrôle d'accès strict (ADMIN) des dossiers historiques importés ou régularisés.</li>
 * </ul>
 * </p>
 *
 * @author CECAM / Direction du Crédit & des Engagements
 * @version 2.1 (Octobre 2026)
 */
@Service
@RequiredArgsConstructor
public class ContratService {

    private final ContratRepository contratRepository;
    private final ClientRepository clientRepository;
    private final EcheanceRepository echeanceRepository;
    private final GarantieRepository garantieRepository;
    private final RevenuDemandeRepository revenuDemandeRepository;
    private final BienLeasingRepository bienLeasingRepository;
    private final ContratLienRepository contratLienRepository;
    private final HistoriquePaiementMensuelRepository historiquePaiementMensuelRepository;
    private final ModificationContratDemandeRepository modificationContratDemandeRepository;
    private final DernierContratRepository dernierContratRepository;
    private final TauxMensuelProperties tauxMensuelProperties;
    private final AuditService auditService;

    @Transactional
    public Contrat changerPhase(Long contratId, PhaseDemande nouvellePhase, MotifCloture motif) {
        Contrat c = contratRepository.findById(contratId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrat introuvable"));

        PhaseDemande ancienne = c.getPhaseDemande();
        c.setPhaseDemande(nouvellePhase);

        if (nouvellePhase == PhaseDemande.ACTIF && ancienne != PhaseDemande.ACTIF) {
            if (c.getDateDebutContrat() == null) c.setDateDebutContrat(LocalDate.now());
            if (c.getMontantRestantDu() == null) {
                c.setMontantRestantDu(c.totalDu() != null ? c.totalDu() : c.getMontantFinance());
            }
            if (c.getMontantImpayes() == null) c.setMontantImpayes(BigDecimal.ZERO);
            if (c.getNombreEcheancesImpayees() == null) c.setNombreEcheancesImpayees(0);
            if (c.getNombreEcheancesRestantes() == null) {
                c.setNombreEcheancesRestantes(c.getNombreTotalEcheances());
            }
            if (echeanceRepository.findByContrat_IdOrderByNumeroEcheance(contratId).isEmpty()) {
                genererEcheances(c);
            }
        }
        if (nouvellePhase == PhaseDemande.FERME || nouvellePhase == PhaseDemande.ABANDONNE) {
            if (motif != null || ancienne != nouvellePhase) {
                c.setMotifCloture(motif != null ? motif : MotifCloture.FIN_A_TERME);
            }
            if (c.getDateFinContrat() == null) c.setDateFinContrat(LocalDate.now());
            c.setMontantRestantDu(BigDecimal.ZERO);
            c.setNombreEcheancesRestantes(0);
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

        BigDecimal ratio = null;
        if (request.montantRecette() != null && request.montantDepense() != null) {
            BigDecimal recette = request.montantRecette();
            BigDecimal depense = request.montantDepense();
            if (depense.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal reste = recette.subtract(depense);
                ratio = reste.multiply(BigDecimal.valueOf(100))
                        .divide(depense, 2, java.math.RoundingMode.HALF_UP);
            } else if (recette.compareTo(BigDecimal.ZERO) > 0) {
                ratio = BigDecimal.valueOf(100);
            } else {
                ratio = BigDecimal.ZERO;
            }
        }

        // Le taux est mensuel : convertir les échéances en durée réelle selon leur périodicité.
        String typeCredit = request.typeCredit() != null ? request.typeCredit() : "GCV";
        int nombreEcheances = request.nombreTotalEcheances() != null && request.nombreTotalEcheances() > 0
                ? request.nombreTotalEcheances() : 1;
        int moisEntreEcheances = Contrat.moisEntreEcheances(request.periodicitePaiement());
        if (moisEntreEcheances == 0) moisEntreEcheances = 1;
        BigDecimal moisDuree = BigDecimal.valueOf(nombreEcheances)
                .multiply(BigDecimal.valueOf(moisEntreEcheances));
        BigDecimal tauxMensuelPct = request.tauxInteretMensuel() != null && request.tauxInteretMensuel().signum() >= 0
                ? request.tauxInteretMensuel() : tauxMensuelProperties.pourType(typeCredit);
        BigDecimal tauxInteretGlobal = tauxMensuelPct.divide(BigDecimal.valueOf(100), 8, RoundingMode.HALF_UP)
                .multiply(moisDuree);
        BigDecimal montantInteret = request.montantFinance().multiply(tauxInteretGlobal);
        BigDecimal totalDemandeInteret = request.montantFinance().add(montantInteret);

        BigDecimal ratioGarantie = null;
        if (request.valeurGarantie() != null && request.valeurGarantie().compareTo(BigDecimal.ZERO) > 0) {
            ratioGarantie = request.valeurGarantie().multiply(BigDecimal.valueOf(100))
                    .divide(totalDemandeInteret, 2, java.math.RoundingMode.HALF_UP);
        }

        BigDecimal margePatrimoine = request.valeurPatrimoine() == null
                ? null : request.valeurPatrimoine().subtract(totalDemandeInteret);
        BigDecimal ratioPatrimoine = request.valeurPatrimoine() == null
                || request.valeurPatrimoine().signum() <= 0
                ? null
                : request.valeurPatrimoine().multiply(BigDecimal.valueOf(100))
                        .divide(totalDemandeInteret, 2, RoundingMode.HALF_UP);

        BigDecimal echeanceCalc = request.montantEcheanceMensuelle();
        if (echeanceCalc == null || echeanceCalc.signum() <= 0) {
            echeanceCalc = totalDemandeInteret.divide(BigDecimal.valueOf(nombreEcheances), 0, RoundingMode.HALF_UP);
        }
        String periodicite = request.periodicitePaiement() != null && !request.periodicitePaiement().isBlank()
                ? request.periodicitePaiement() : "Mensuelle";

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
                .montantTotalDu(totalDemandeInteret)
                .montantEcheanceMensuelle(echeanceCalc)
                .nombreTotalEcheances(request.nombreTotalEcheances())
                .devise(request.devise())
                .origineDonnee(OrigineDonnee.NON_RENSEIGNEE)
                .periodicitePaiement(periodicite)
                .periodiciteBudget(request.periodiciteBudget() != null ? request.periodiciteBudget() : "Mensuelle")
                .montantRecette(request.montantRecette())
                .montantDepense(request.montantDepense())
                .ratioRestePourcentage(ratio)
                .typeGarantie(request.typeGarantie() != null && !request.typeGarantie().isBlank() ? request.typeGarantie() : "Garantie réelle")
                .valeurGarantie(request.valeurGarantie())
                .typeCredit(typeCredit)
                .tauxInteretMensuel(tauxMensuelPct)
                .montantTotalDemandeInteret(totalDemandeInteret)
                .ratioCouvertureGarantie(ratioGarantie)
                .valeurPatrimoine(request.valeurPatrimoine())
                .margePatrimoine(margePatrimoine)
                .ratioCouverturePatrimoine(ratioPatrimoine)
                .totalDepot(request.totalDepot())
                .totalCreditActif(request.totalCreditActif())
                .phaseDemande(PhaseDemande.DEMANDE_EN_COURS)
                .utilisateurDeclarant(utilisateurConnecte())
                .build();

        contrat = contratRepository.save(contrat);

        enregistrerRevenusComplementaires(contrat, request);
        enregistrerDerniersContrats(contrat, request);

        if (request.valeurGarantie() != null && request.valeurGarantie().compareTo(BigDecimal.ZERO) > 0) {
            Garantie garantie = Garantie.builder()
                    .contrat(contrat)
                    .typeGarantie(request.typeGarantie() != null ? request.typeGarantie() : "Garantie réelle")
                    .montantCouvert(request.valeurGarantie())
                    .nature(mg.cecam.bic.common.enums.NatureGarantie.REELLE)
                    .dateDebutValidite(contrat.getDateDemande())
                    .build();
            garantieRepository.save(garantie);
        }

        auditService.enregistrer("CONTRAT", contrat.getId(), "CREATION", "Nouvelle demande créée");
        return contrat;
    }

    private void enregistrerRevenusComplementaires(Contrat contrat, ContratRequest request) {
        if (request.revenusComplementaires() == null) return;
        List<RevenuDemande> revenus = new ArrayList<>();
        for (ContratRequest.RevenuDemandeRequest revenu : request.revenusComplementaires()) {
            if (revenu == null || revenu.typeRevenu() == null || revenu.typeRevenu().isBlank()
                    || revenu.description() == null || revenu.description().isBlank()
                    || revenu.montantAnnuel() == null || revenu.montantAnnuel().signum() <= 0
                    || revenu.devise() == null || revenu.devise().isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Chaque revenu complémentaire doit avoir un type, une description, un montant annuel positif et une devise.");
            }
            revenus.add(RevenuDemande.builder()
                    .contrat(contrat)
                    .typeRevenu(revenu.typeRevenu().trim())
                    .description(revenu.description().trim())
                    .montantAnnuel(revenu.montantAnnuel())
                    .devise(revenu.devise().trim())
                    .build());
        }
        revenuDemandeRepository.saveAll(revenus);
    }

    private void enregistrerDerniersContrats(Contrat contrat, ContratRequest request) {
        if (request.derniersContrats() == null || request.derniersContrats().isEmpty()) return;
        List<DernierContrat> list = new ArrayList<>();
        LocalDate ref = LocalDate.now();
        for (ContratRequest.DernierContratRequest d : request.derniersContrats()) {
            if (d == null || (d.montant() == null && d.dateOctroi() == null && d.statut() == null)) continue;
            // Normaliser le statut via ScoreService (même logique que le moteur de score)
            String statut = ScoreService.normaliserStatutDernierContrat(d.statut());
            // Pas de défaut "A_TEMPS" : si le statut est inconnu, on laisse null pour que ScoreService l'exclue
            if (statut == null && d.statut() != null && !d.statut().isBlank()) {
                // statut non reconnu, mais non vide : on le conserve tel quel pour traçabilité
                statut = d.statut().toUpperCase(java.util.Locale.ROOT).trim();
            }

            // Calcul des jours de retard réels (à partir des dates fournies) — pas de valeurs inventées
            int joursRetard = 0;
            if (d.datePrevueRemboursement() != null && d.dateRemboursement() != null) {
                long delta = ChronoUnit.DAYS.between(d.datePrevueRemboursement(), d.dateRemboursement());
                joursRetard = (int) delta; // négatif si anticipation, positif si retard
                // Recalcul du statut à partir des dates si non fourni explicitement
                if (d.statut() == null || d.statut().isBlank()) {
                    if (delta > 5) statut = "EN_RETARD";
                    else if (delta < -25) statut = "ANTICIPATION";
                    else statut = "A_TEMPS";
                }
            } else if (d.datePrevueRemboursement() != null && d.dateRemboursement() == null) {
                if (d.datePrevueRemboursement().isBefore(ref)) {
                    joursRetard = (int) ChronoUnit.DAYS.between(d.datePrevueRemboursement(), ref);
                    if (d.statut() == null || d.statut().isBlank()) statut = "NON_REMBOURSE";
                } else {
                    if (d.statut() == null || d.statut().isBlank()) statut = "EN_COURS";
                }
            }
            list.add(DernierContrat.builder()
                    .contrat(contrat)
                    .typeCredit(d.typeCredit() != null ? d.typeCredit() : "RFR")
                    .dateOctroi(d.dateOctroi())
                    .montant(d.montant())
                    .datePrevueRemboursement(d.datePrevueRemboursement())
                    .dateRemboursement(d.dateRemboursement())
                    .joursRetard(joursRetard)
                    .statut(statut)
                    .verifie(d.verifie() != null ? d.verifie() : false)
                    .sourceVerification(d.sourceVerification() != null && !d.sourceVerification().isBlank()
                            ? d.sourceVerification() : "NON_VERIFIE")
                    .build());
        }
        if (!list.isEmpty()) {
            dernierContratRepository.saveAll(list);
        }
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

    @Transactional(readOnly = true)
    public mg.cecam.bic.contrat.dto.HistoriqueContratDetailResponse obtenirDetailHistorique(Long contratId) {
        Contrat c = contratRepository.findById(contratId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrat introuvable : " + contratId));

        Client cl = c.getClient();
        String cin = null;
        if (cl != null && cl.getIdentifiants() != null) {
            cin = cl.getIdentifiants().stream()
                    .filter(i -> "CIN".equalsIgnoreCase(i.getTypeIdentifiant()))
                    .map(mg.cecam.bic.client.Identifiant::getNumero)
                    .findFirst()
                    .orElse(null);
        }

        List<mg.cecam.bic.contrat.dto.HistoriqueContratDetailResponse.EcheanceDetailResponse> echeances =
                echeanceRepository.findByContrat_IdOrderByNumeroEcheance(contratId).stream()
                        .map(e -> new mg.cecam.bic.contrat.dto.HistoriqueContratDetailResponse.EcheanceDetailResponse(
                                e.getNumeroEcheance(), e.getDateEcheance(), e.getMontantDu(),
                                e.getMontantPaye(), e.getDatePaiement(), e.getStatut()
                        )).toList();

        List<mg.cecam.bic.contrat.dto.HistoriqueContratDetailResponse.MoisDetailResponse> mois =
                historiquePaiementMensuelRepository.findByContrat_IdOrderByMoisDesc(contratId).stream()
                        .map(m -> new mg.cecam.bic.contrat.dto.HistoriqueContratDetailResponse.MoisDetailResponse(
                                m.getMois(), m.getNombreEcheances(), m.getNombreEcheancesImpayees(),
                                m.getJoursDeRetard(), m.getStatut(), m.getDetailsSource()
                        )).toList();

        List<mg.cecam.bic.contrat.dto.HistoriqueContratDetailResponse.GarantieDetailResponse> garanties =
                garantieRepository.findByContrat_Id(contratId).stream()
                        .map(g -> new mg.cecam.bic.contrat.dto.HistoriqueContratDetailResponse.GarantieDetailResponse(
                                g.getNature(), g.getTypeGarantie(), g.getCodeEtablissementGarantie(),
                                g.getNomGarant(), g.getCodeClientCbGarant(), g.getMontantCouvert(),
                                g.getDateDebutValidite(), g.getDateFinValidite()
                        )).toList();

        List<mg.cecam.bic.contrat.dto.HistoriqueContratDetailResponse.ClientLieDetailResponse> liens =
                contratLienRepository.findByContrat_Id(contratId).stream()
                        .map(l -> new mg.cecam.bic.contrat.dto.HistoriqueContratDetailResponse.ClientLieDetailResponse(
                                l.getCodeClientCb(), l.getNomClient(), l.getRoleClient()
                        )).toList();

        mg.cecam.bic.contrat.dto.HistoriqueContratDetailResponse.BienLeasingDetailResponse leasing =
                bienLeasingRepository.findByContrat_Id(contratId)
                        .map(b -> new mg.cecam.bic.contrat.dto.HistoriqueContratDetailResponse.BienLeasingDetailResponse(
                                b.getCodeBien(), b.getValeurBien(), b.getEtatBien(),
                                b.getMarqueBien(), b.getDateAcquisition(), b.getImmatriculation()
                        )).orElse(null);

        return new mg.cecam.bic.contrat.dto.HistoriqueContratDetailResponse(
                c.getId(),
                cl != null ? cl.getId() : null,
                cl != null ? cl.getNom() : null,
                cl != null ? cl.getPrenom() : null,
                cin,
                cl != null ? cl.getCodeClientCb() : null,
                c.getCodeContratCb(),
                c.getCodeContratEtablissement(),
                c.getCodeEtablissementDeclarant(),
                c.getCodeEtablissementCrypte(),
                c.getTypeContrat(),
                c.getTypeRelationEntreprise(),
                c.getRoleClient(),
                c.getPhaseDemande(),
                c.getMotifCloture(),
                c.getDateDemande(),
                c.getDateDebutContrat(),
                c.getDateFinContrat(),
                c.getDatePremiereEcheance(),
                c.getDateDerniereModificationSource(),
                c.getMontantFinance(),
                c.getMontantTotalDu(),
                c.getMontantEcheanceMensuelle(),
                c.getNombreTotalEcheances(),
                c.getDevise(),
                c.getPeriodicitePaiement(),
                c.getModeReglement(),
                c.getMontantProchaineEcheance(),
                c.getDateProchaineEcheance(),
                c.getDateDernierReglement(),
                c.getNombreEcheancesRestantes(),
                c.getMontantRestantDu(),
                c.getNombreEcheancesImpayees(),
                c.getMontantImpayes(),
                c.getMaxMontantImpayes(),
                c.getMaxNombreEcheancesImpayees(),
                c.getDateMaxNombreEcheancesImpayees(),
                c.getNombreJoursRetard(),
                c.getMaxNombreJoursRetard(),
                c.getDateMaxNombreJoursRetard(),
                c.getPireStatut(),
                c.getDatePireStatut(),
                c.getCodeRestructuration(),
                c.getDateCodeRestructuration(),
                c.getContratOrigine(),
                c.getNouveauContrat(),
                c.getNote(),
                c.getOrigineDonnee(),
                echeances,
                mois,
                garanties,
                liens,
                leasing
        );
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
        revenuDemandeRepository.deleteAll(revenuDemandeRepository.findByContrat_Id(id));
        dernierContratRepository.deleteAll(dernierContratRepository.findByContrat_IdOrderByDateOctroiAsc(id));
        modificationContratDemandeRepository.deleteAll(modificationContratDemandeRepository.findByContrat_Id(id));
    }

    /**
     * Supprime un contrat et l'intégralité de son historique lié (échéances, garanties, déclarations mensuelles, etc.).
     * Réservé aux administrateurs.
     */
    @Transactional
    public void supprimerContrat(Long contratId) {
        Contrat contrat = contratRepository.findById(contratId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrat introuvable : " + contratId));
        supprimerHistoriqueLie(contrat);
        contratRepository.delete(contrat);
        auditService.enregistrer("CONTRAT", contratId, "SUPPRESSION", "Contrat supprimé par un administrateur");
    }

    public Contrat obtenirParId(Long contratId) {
        return contratRepository.findById(contratId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrat introuvable : " + contratId));
    }

    public List<Contrat> listerParClient(Long clientId) {
        return contratRepository.findPourClient(clientId);
    }

    public List<Contrat> listerTous() {
        return contratRepository.findAllByOrderByIdDesc();
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
        String code;
        do {
            Long n = contratRepository.prochainCodeContratCb();
            code = String.valueOf(n);
        } while (contratRepository.findByIdentifiantContrat(code).isPresent());
        return code;
    }
}
