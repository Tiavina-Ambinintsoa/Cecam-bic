package mg.cecam.bic.contrat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BienLeasingRepository extends JpaRepository<BienLeasing, Long> {
    List<BienLeasing> findByContrat_IdIn(List<Long> contratIds);
}