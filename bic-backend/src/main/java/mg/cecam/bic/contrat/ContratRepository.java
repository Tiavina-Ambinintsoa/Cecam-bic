package mg.cecam.bic.contrat;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContratRepository extends JpaRepository<Contrat, Long> {
    List<Contrat> findByClient_Id(Long clientId);
    List<Contrat> findAllByOrderByDateDemandeDesc();
}