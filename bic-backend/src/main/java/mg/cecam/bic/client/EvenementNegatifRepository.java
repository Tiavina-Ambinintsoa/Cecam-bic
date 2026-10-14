package mg.cecam.bic.client;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvenementNegatifRepository extends JpaRepository<EvenementNegatif, Long> {
    List<EvenementNegatif> findByClient_IdAndActifTrue(Long clientId);
}