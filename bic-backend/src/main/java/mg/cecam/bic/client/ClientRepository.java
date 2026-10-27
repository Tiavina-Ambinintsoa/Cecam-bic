package mg.cecam.bic.client;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findAllByIdentifiants_NumeroAndIdentifiants_TypeIdentifiant(String numero, String typeIdentifiant);

    @Query(value = """
            SELECT DISTINCT c.*
            FROM client c
            JOIN identifiant i ON i.client_id = c.id
            WHERE upper(trim(i.type_identifiant)) = 'CIN'
              AND regexp_replace(i.numero, '[^0-9]', '', 'g') =
                  regexp_replace(:numero, '[^0-9]', '', 'g')
            """, nativeQuery = true)
    List<Client> findAllByCinNormalise(@Param("numero") String numero);

    @Query(value = "SELECT nextval('seq_code_client_cb')", nativeQuery = true)
    Long prochainCodeClientCb();

    Optional<Client> findByCodeClientCb(String codeClientCb);
    Optional<Client> findByCodeClientEtablissement(String codeClientEtablissement);

    /** Liste paginée des clients, triée par id décroissant par défaut. */
    Page<Client> findAllByOrderByIdDesc(Pageable pageable);

    /** Recherche paginée par nom, prénom ou code CB, triée par id décroissant. */
    @Query("SELECT c FROM Client c WHERE LOWER(c.nom) LIKE LOWER(CONCAT('%', :q, '%')) "
         + "OR LOWER(c.prenom) LIKE LOWER(CONCAT('%', :q, '%')) "
         + "OR c.codeClientCb LIKE CONCAT('%', :q, '%') "
         + "ORDER BY c.id DESC")
    Page<Client> rechercherParMotCle(@Param("q") String motCle, Pageable pageable);
}
