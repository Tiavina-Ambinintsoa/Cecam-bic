package mg.cecam.bic.contrat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContratRepository extends JpaRepository<Contrat, Long> {

    List<Contrat> findByClient_Id(Long clientId);

    List<Contrat> findAllByOrderByDateDemandeDesc();

    @Query(value = "SELECT nextval('seq_code_contrat_cb')", nativeQuery = true)
    Long prochainCodeContratCb();
}