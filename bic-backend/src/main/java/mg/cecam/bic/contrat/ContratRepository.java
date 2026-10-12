package mg.cecam.bic.contrat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContratRepository extends JpaRepository<Contrat, Long> {

    List<Contrat> findByClient_Id(Long clientId);

    List<Contrat> findAllByOrderByDateDemandeDesc();

    Optional<Contrat> findByCodeContratEtablissement(String codeContratEtablissement);

    @Query("select c from Contrat c where lower(c.codeContratEtablissement) = lower(:code) or lower(c.codeContratCb) = lower(:code)")
    Optional<Contrat> findByIdentifiantContrat(@Param("code") String code);

    @Query(value = "SELECT nextval('seq_code_contrat_cb')", nativeQuery = true)
    Long prochainCodeContratCb();
}
