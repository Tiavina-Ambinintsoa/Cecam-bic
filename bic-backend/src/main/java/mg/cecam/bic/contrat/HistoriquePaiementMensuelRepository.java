package mg.cecam.bic.contrat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoriquePaiementMensuelRepository extends JpaRepository<HistoriquePaiementMensuel, Long> {
    List<HistoriquePaiementMensuel> findByContrat_IdOrderByMoisDesc(Long contratId);
}
