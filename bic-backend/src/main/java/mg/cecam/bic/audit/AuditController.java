package mg.cecam.bic.audit;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final JournalAuditRepository journalAuditRepository;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<JournalAudit> lister(@RequestParam(defaultValue = "200") int limite) {
        return journalAuditRepository.findAll(PageRequest.of(0, limite, Sort.by(Sort.Direction.DESC, "dateEvenement"))).getContent();
    }
}