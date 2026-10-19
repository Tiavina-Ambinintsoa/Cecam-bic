package mg.cecam.bic.contrat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RevenuDemandeRepository extends JpaRepository<RevenuDemande, Long> {
    List<RevenuDemande> findByContrat_Id(Long contratId);
}
