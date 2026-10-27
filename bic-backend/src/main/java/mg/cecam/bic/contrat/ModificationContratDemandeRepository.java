package mg.cecam.bic.contrat;

import mg.cecam.bic.common.enums.StatutDemandeModification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModificationContratDemandeRepository extends JpaRepository<ModificationContratDemande, Long> {
    List<ModificationContratDemande> findAllByOrderByDateSoumissionDesc();
    List<ModificationContratDemande> findByStatutOrderByDateSoumissionAsc(StatutDemandeModification statut);
    List<ModificationContratDemande> findBySoumisParOrderByDateSoumissionDesc(String soumisPar);
    boolean existsByContrat_IdAndStatut(Long contratId, StatutDemandeModification statut);
    /** Toutes les demandes de modification liées à un contrat donné. */
    List<ModificationContratDemande> findByContrat_Id(Long contratId);
}
