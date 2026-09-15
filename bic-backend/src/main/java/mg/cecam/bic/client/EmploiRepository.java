package mg.cecam.bic.client;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmploiRepository extends JpaRepository<Emploi, Long> {
    Optional<Emploi> findByClient_Id(Long clientId);
}