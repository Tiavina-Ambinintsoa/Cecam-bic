package mg.cecam.bic.rapport;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.client.Client;
import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.contrat.Contrat;
import mg.cecam.bic.contrat.ContratRepository;
import mg.cecam.bic.contrat.Echeance;
import mg.cecam.bic.contrat.EcheanceRepository;
import mg.cecam.bic.rapport.dto.AlerteDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AlerteService {

    private final ContratRepository contratRepository;
    private final EcheanceRepository echeanceRepository;

    /** Alertes sur l'ensemble des clients — page "Alertes". */
    @Transactional(readOnly = true)
    public List<AlerteDTO> lister() {
        LocalDate reference = LocalDate.now();
        return contratRepository.findAll().stream()
                .filter(c -> c.getPhaseDemande() == PhaseDemande.ACTIF)
                .map(c -> toAlerte(c, reference))
                .filter(Objects::nonNull)
                .toList();
    }

    /** Alertes d'un seul client — affichées dans son rapport de solvabilité. */
    @Transactional(readOnly = true)
    public List<AlerteDTO> listerParClient(Long clientId, LocalDate reference) {
        return contratRepository.findByClient_Id(clientId).stream()
                .filter(c -> c.getPhaseDemande() == PhaseDemande.ACTIF)
                .map(c -> toAlerte(c, reference))
                .filter(Objects::nonNull)
                .toList();
    }

    private AlerteDTO toAlerte(Contrat c, LocalDate reference) {
        List<Echeance> echeances = echeanceRepository.findByContrat_IdOrderByNumeroEcheance(c.getId());
        long impayees = 0, enRetard = 0;
        long[] buckets = new long[6];
        int maxJoursRetard = 0;
        for (Echeance e : echeances) {
            if (e.getDateEcheance() == null || e.estPayee() || e.getDateEcheance().isAfter(reference)) continue;
            int jours = e.joursDeRetard(reference);
            if (jours <= 0) continue;
            maxJoursRetard = Math.max(maxJoursRetard, jours);
            if (jours < 30) { enRetard++; continue; }
            impayees++;
            if (jours < 60) buckets[0]++;
            else if (jours < 90) buckets[1]++;
            else if (jours < 120) buckets[2]++;
            else if (jours < 150) buckets[3]++;
            else if (jours < 180) buckets[4]++;
            else buckets[5]++;
        }

        // Keep source-reported overdue fields when no exact dated schedule was supplied.
        if (echeances.isEmpty()) {
            impayees = Math.max(impayees, c.getNombreEcheancesImpayees() == null ? 0 : c.getNombreEcheancesImpayees());
            enRetard = Math.max(enRetard, c.getNombreJoursRetard() != null && c.getNombreJoursRetard() > 0
                    && c.getNombreJoursRetard() < 30 ? 1 : 0);
            maxJoursRetard = c.getNombreJoursRetard() == null ? 0 : c.getNombreJoursRetard();
            if (impayees > 0 && maxJoursRetard >= 30) {
                if (maxJoursRetard < 60) buckets[0] = impayees;
                else if (maxJoursRetard < 90) buckets[1] = impayees;
                else if (maxJoursRetard < 120) buckets[2] = impayees;
                else if (maxJoursRetard < 150) buckets[3] = impayees;
                else if (maxJoursRetard < 180) buckets[4] = impayees;
                else buckets[5] = impayees;
            }
        }
        if (impayees == 0 && enRetard == 0) return null;

        Client client = c.getClient();
        String description = impayees + " échéance(s) impayée(s), " + enRetard + " en retard";
        if (maxJoursRetard > 0) description += " — retard maximal : " + maxJoursRetard + " jours";
        return new AlerteDTO(client.getId(), client.getCodeClientCb(),
                (client.getPrenom() + " " + client.getNom()).trim(), c.getId(), c.getCodeContratCb(),
                c.getTypeContrat(), reference, description, impayees > 0 ? "CRITIQUE" : "VIGILANCE",
                client.getAgence(), c.getUtilisateurDeclarant(), impayees, enRetard,
                buckets[0], buckets[1], buckets[2], buckets[3], buckets[4], buckets[5], maxJoursRetard);
    }
}
