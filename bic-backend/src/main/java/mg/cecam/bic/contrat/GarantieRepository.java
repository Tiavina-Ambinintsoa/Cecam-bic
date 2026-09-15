package mg.cecam.bic.contrat;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GarantieRepository extends JpaRepository<Garantie, Long> {
    List<Garantie> findByContrat_Id(Long contratId);
}