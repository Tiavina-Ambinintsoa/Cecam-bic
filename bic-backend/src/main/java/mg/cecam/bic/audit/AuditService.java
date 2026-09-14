package mg.cecam.bic.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final JournalAuditRepository journalAuditRepository;

    public void enregistrer(String entite, Long entiteId, String action, String detail) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String utilisateur = (auth != null && auth.isAuthenticated()) ? auth.getName() : "system";

        journalAuditRepository.save(JournalAudit.builder()
                .dateEvenement(LocalDateTime.now())
                .utilisateur(utilisateur)
                .entite(entite)
                .entiteId(entiteId)
                .action(action)
                .detail(detail)
                .build());
    }
}