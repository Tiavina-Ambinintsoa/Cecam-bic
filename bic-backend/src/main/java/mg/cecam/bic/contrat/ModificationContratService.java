package mg.cecam.bic.contrat;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.audit.AuditService;
import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.StatutDemandeModification;
import mg.cecam.bic.contrat.dto.ModificationContratRequest;
import mg.cecam.bic.contrat.dto.ModificationContratResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ModificationContratService {

    private final ModificationContratDemandeRepository demandeRepository;
    private final ContratRepository contratRepository;
    private final ContratService contratService;
    private final AuditService auditService;

    @Transactional
    public ModificationContratResponse soumettre(Long contratId, ModificationContratRequest request) {
        Contrat contrat = contratRepository.findById(contratId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contrat introuvable"));
        if (demandeRepository.existsByContrat_IdAndStatut(contratId, StatutDemandeModification.EN_ATTENTE)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Une demande de modification est déjà en attente pour ce contrat.");
        }
        boolean cloture = request.phaseDemande() == PhaseDemande.FERME || request.phaseDemande() == PhaseDemande.ABANDONNE;
        if (cloture && request.motifCloture() == null
                && (contrat.getPhaseDemande() != request.phaseDemande() || contrat.getMotifCloture() == null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Le motif de clôture ou d'annulation est obligatoire.");
        }

        ModificationContratDemande demande = ModificationContratDemande.builder()
                .contrat(contrat)
                .phaseInitiale(contrat.getPhaseDemande())
                .motifClotureInitial(contrat.getMotifCloture())
                .codeContratEtablissementInitial(contrat.getCodeContratEtablissement())
                .codeDemandeEtablissementInitial(contrat.getCodeDemandeEtablissement())
                .montantEcheanceInitial(contrat.getMontantEcheanceMensuelle())
                .deviseInitiale(contrat.getDevise())
                .codeContratEtablissement(normaliser(request.codeContratEtablissement()))
                .codeDemandeEtablissement(normaliser(request.codeDemandeEtablissement()))
                .phaseDemandee(request.phaseDemande())
                .motifCloture(request.motifCloture() != null ? request.motifCloture() : contrat.getMotifCloture())
                .montantEcheanceMensuelle(request.montantEcheanceMensuelle())
                .devise(request.devise().trim())
                .justification(request.justification().trim())
                .soumisPar(utilisateurConnecte())
                .build();
        demande = demandeRepository.save(demande);
        auditService.enregistrer("CONTRAT", contratId, "DEMANDE_MODIFICATION_SOUMISE",
                "Demande de modification " + demande.getId() + " soumise à validation admin");
        return versResponse(demande);
    }

    @Transactional(readOnly = true)
    public List<ModificationContratResponse> listerEnAttente() {
        return demandeRepository.findByStatutOrderByDateSoumissionAsc(StatutDemandeModification.EN_ATTENTE)
                .stream().map(this::versResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<ModificationContratResponse> listerMesDemandes() {
        return demandeRepository.findBySoumisParOrderByDateSoumissionDesc(utilisateurConnecte()).stream()
                .map(this::versResponse).toList();
    }

    @Transactional
    public ModificationContratResponse approuver(Long demandeId) {
        ModificationContratDemande demande = demandeEnAttente(demandeId);
        Contrat contrat = demande.getContrat();
        verifierInstantaneToujoursValide(demande, contrat);

        contratService.modifierCodesEtablissement(contrat.getId(), demande.getCodeContratEtablissement(),
                demande.getCodeDemandeEtablissement());
        contratService.changerEcheanceMensuelle(contrat.getId(), demande.getMontantEcheanceMensuelle(), demande.getDevise());
        contratService.changerPhase(contrat.getId(), demande.getPhaseDemandee(), demande.getMotifCloture());

        traiter(demande, StatutDemandeModification.APPROUVEE, null);
        auditService.enregistrer("CONTRAT", contrat.getId(), "DEMANDE_MODIFICATION_APPROUVEE",
                "Demande de modification " + demandeId + " approuvée par un administrateur");
        return versResponse(demande);
    }

    @Transactional
    public ModificationContratResponse refuser(Long demandeId, String commentaire) {
        ModificationContratDemande demande = demandeEnAttente(demandeId);
        traiter(demande, StatutDemandeModification.REFUSEE, normaliser(commentaire));
        auditService.enregistrer("CONTRAT", demande.getContrat().getId(), "DEMANDE_MODIFICATION_REFUSEE",
                "Demande de modification " + demandeId + " refusée par un administrateur");
        return versResponse(demande);
    }

    private ModificationContratDemande demandeEnAttente(Long id) {
        ModificationContratDemande demande = demandeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Demande de modification introuvable"));
        if (demande.getStatut() != StatutDemandeModification.EN_ATTENTE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cette demande a déjà été traitée.");
        }
        return demande;
    }

    private void traiter(ModificationContratDemande demande, StatutDemandeModification statut, String commentaire) {
        demande.setStatut(statut);
        demande.setTraitePar(utilisateurConnecte());
        demande.setDateTraitement(LocalDateTime.now());
        demande.setCommentaireAdmin(commentaire);
        demandeRepository.save(demande);
    }

    private ModificationContratResponse versResponse(ModificationContratDemande demande) {
        Contrat contrat = demande.getContrat();
        String nomComplet = (contrat.getClient().getPrenom() + " " + contrat.getClient().getNom()).trim();
        return new ModificationContratResponse(demande.getId(), contrat.getId(), contrat.getCodeContratCb(), nomComplet,
                contrat.getPhaseDemande(), contrat.getMotifCloture(), contrat.getCodeContratEtablissement(),
                contrat.getCodeDemandeEtablissement(), contrat.getMontantEcheanceMensuelle(), contrat.getDevise(),
                demande.getCodeContratEtablissement(), demande.getCodeDemandeEtablissement(), demande.getPhaseDemandee(),
                demande.getMotifCloture(), demande.getMontantEcheanceMensuelle(), demande.getDevise(),
                demande.getJustification(), demande.getStatut(), demande.getSoumisPar(), demande.getDateSoumission(),
                demande.getTraitePar(), demande.getDateTraitement(), demande.getCommentaireAdmin());
    }

    private void verifierInstantaneToujoursValide(ModificationContratDemande demande, Contrat contrat) {
        boolean montantInchange = montantsEgaux(contrat.getMontantEcheanceMensuelle(), demande.getMontantEcheanceInitial());
        boolean inchangé = contrat.getPhaseDemande() == demande.getPhaseInitiale()
                && contrat.getMotifCloture() == demande.getMotifClotureInitial()
                && Objects.equals(contrat.getCodeContratEtablissement(), demande.getCodeContratEtablissementInitial())
                && Objects.equals(contrat.getCodeDemandeEtablissement(), demande.getCodeDemandeEtablissementInitial())
                && montantInchange && Objects.equals(contrat.getDevise(), demande.getDeviseInitiale());
        if (!inchangé) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Le contrat a changé depuis la demande. Refusez-la et soumettez une nouvelle mise à jour.");
        }
    }

    private boolean montantsEgaux(BigDecimal a, BigDecimal b) {
        return a == null ? b == null : b != null && a.compareTo(b) == 0;
    }

    private String utilisateurConnecte() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())
                ? "system" : auth.getName();
    }

    private String normaliser(String valeur) {
        if (valeur == null || valeur.isBlank()) return null;
        return valeur.trim();
    }
}
