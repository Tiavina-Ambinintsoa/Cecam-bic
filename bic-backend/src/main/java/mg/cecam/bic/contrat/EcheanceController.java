package mg.cecam.bic.contrat;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.audit.AuditService;
import mg.cecam.bic.common.enums.StatutEcheance;
import mg.cecam.bic.contrat.dto.PaiementRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/echeances")
@RequiredArgsConstructor
public class EcheanceController {

    private final EcheanceRepository echeanceRepository;
    private final AuditService auditService;

    @GetMapping
    public List<Echeance> listerParContrat(@RequestParam Long contratId) {
        return echeanceRepository.findByContrat_IdOrderByNumeroEcheance(contratId);
    }

    @PatchMapping("/{id}/paiement")
    public ResponseEntity<Echeance> enregistrerPaiement(@PathVariable Long id, @RequestBody PaiementRequest req) {
        Echeance e = echeanceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Échéance introuvable"));
        e.setMontantPaye(req.montantPaye());
        e.setDatePaiement(req.datePaiement());
        e.setStatut(req.datePaiement().isAfter(e.getDateEcheance()) ? StatutEcheance.EN_RETARD : StatutEcheance.PAYE_A_TEMPS);
        Echeance enregistree = echeanceRepository.save(e);
        auditService.enregistrer("ECHEANCE", id, "PAIEMENT", "Montant : " + req.montantPaye() + ", date : " + req.datePaiement());
        return ResponseEntity.ok(enregistree);
    }
}