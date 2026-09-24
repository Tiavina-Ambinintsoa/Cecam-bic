package mg.cecam.bic.contrat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContratLienRepository extends JpaRepository<ContratLien, Long> {
    List<ContratLien> findByContrat_IdIn(List<Long> contratIds);
}