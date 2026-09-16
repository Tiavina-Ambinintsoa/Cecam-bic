// mg/cecam/bic/contrat/ContratRepository.java  — MODIFIÉ
package mg.cecam.bic.contrat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ContratRepository extends JpaRepository<Contrat, Long> {

    List<Contrat> findByClient_Id(Long clientId);

    List<Contrat> findAllByOrderByDateDemandeDesc();

    /**
     * Remplace Math.random() : la colonne code_contrat_cb est UNIQUE et un
     * tirage aléatoire sans contrôle finit toujours par entrer en collision.
     *
     * Prérequis (à ajouter dans data.sql ou une migration) :
     *   CREATE SEQUENCE IF NOT EXISTS seq_code_contrat_cb START WITH 700000001;
     */
    @Query(value = "SELECT nextval('seq_code_contrat_cb')", nativeQuery = true)
    Long prochainCodeContratCb();
}