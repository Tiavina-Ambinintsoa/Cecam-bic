package mg.cecam.bic.rapport;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.client.Client;
import mg.cecam.bic.common.enums.PhaseDemande;
import mg.cecam.bic.common.enums.StatutEcheance;
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

    @Transactional(readOnly = true)
    public List<AlerteDTO> lister() {
        LocalDate reference = LocalDate.now();
        return contratRepository.findAll().stream()
                .filter(c -> c.getPhaseDemande() == PhaseDemande.ACTIF)
                .map(c -> toAlerte(c, reference))
                .filter(Objects::nonNull)
                .toList();
    }

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
        long impayees = 0;
        long enRetard = 0;
        for (Echeance e : echeances) {
            StatutEcheance s = e.statutEffectif(reference);
            if (s == StatutEcheance.IMPAYE) impayees++;
            else if (s == StatutEcheance.EN_RETARD && !e.estPayee()) enRetard++;
        }
        if (impayees == 0 && enRetard == 0) return null;

        Client client = c.getClient();
        return new AlerteDTO(client.getId(), client.getCodeClientCb(),
                (client.getPrenom() + " " + client.getNom()).trim(),
                c.getId(), c.getCodeContratCb(), impayees, enRetard);
    }
}