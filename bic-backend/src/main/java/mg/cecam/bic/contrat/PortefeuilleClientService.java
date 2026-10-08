package mg.cecam.bic.contrat;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.client.Client;
import mg.cecam.bic.common.enums.RoleClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PortefeuilleClientService {

    public record ContratDuClient(Contrat contrat, RoleClient role, boolean lie) {}

    private final ContratRepository contratRepository;
    private final ContratLienRepository contratLienRepository;

    @Transactional(readOnly = true)
    public List<ContratDuClient> contrats(Client client) {
        List<ContratDuClient> resultat = new ArrayList<>();
        for (Contrat c : contratRepository.findByClient_Id(client.getId())) {
            resultat.add(new ContratDuClient(c, c.getRoleClient(), false));
        }
        if (client.getCodeClientCb() != null) {
            for (ContratLien lien : contratLienRepository.findByCodeClientCb(client.getCodeClientCb())) {
                Contrat c = lien.getContrat();
                if (c.getClient().getId().equals(client.getId())) continue;
                resultat.add(new ContratDuClient(c, lien.getRoleClient(), true));
            }
        }
        return resultat;
    }
}