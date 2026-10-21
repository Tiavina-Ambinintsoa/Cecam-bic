package mg.cecam.bic.contrat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContratRepository extends JpaRepository<Contrat, Long> {

    List<Contrat> findByClient_Id(Long clientId);

    @Query("""
            select distinct c from Contrat c join fetch c.client
            where c.client.id = :clientId
               or exists (
                    select l.id from ContratLien l
                    where l.contrat = c and l.clientLie.id = :clientId
               )
            order by c.id desc
            """)
    List<Contrat> findPourClient(@Param("clientId") Long clientId);

    @Query("select c from Contrat c join fetch c.client order by c.id desc")
    List<Contrat> findAllByOrderByIdDesc();

    List<Contrat> findAllByOrderByDateDemandeDesc();

    Optional<Contrat> findByCodeContratEtablissement(String codeContratEtablissement);

    @Query("select c from Contrat c where lower(c.codeContratEtablissement) = lower(:code) or lower(c.codeContratCb) = lower(:code)")
    Optional<Contrat> findByIdentifiantContrat(@Param("code") String code);

    @Query(value = "SELECT nextval('seq_code_contrat_cb')", nativeQuery = true)
    Long prochainCodeContratCb();
}
