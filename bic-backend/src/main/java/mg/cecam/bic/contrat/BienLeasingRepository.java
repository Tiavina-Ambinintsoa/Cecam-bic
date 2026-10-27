package mg.cecam.bic.contrat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BienLeasingRepository extends JpaRepository<BienLeasing, Long> {
    Optional<BienLeasing> findByContrat_Id(Long contratId);
    List<BienLeasing> findByContrat_IdIn(List<Long> contratIds);
}
