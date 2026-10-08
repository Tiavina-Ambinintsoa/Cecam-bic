package mg.cecam.bic.client;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AffaireIndividuelleRepository extends JpaRepository<AffaireIndividuelle, Long> {
    Optional<AffaireIndividuelle> findByClient_Id(Long clientId);
}