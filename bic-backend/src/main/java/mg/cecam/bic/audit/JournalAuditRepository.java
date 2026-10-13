package mg.cecam.bic.audit;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JournalAuditRepository extends JpaRepository<JournalAudit, Long> {
    List<JournalAudit> findByEntiteAndEntiteIdOrderByDateEvenementDesc(String entite, Long entiteId);
    List<JournalAudit> findAllByOrderByDateEvenementDesc(Pageable pageable);
}
