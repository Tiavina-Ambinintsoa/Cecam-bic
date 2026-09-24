package mg.cecam.bic.client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findAllByIdentifiants_NumeroAndIdentifiants_TypeIdentifiant(String numero, String typeIdentifiant);

    @Query(value = "SELECT nextval('seq_code_client_cb')", nativeQuery = true)
    Long prochainCodeClientCb();
}