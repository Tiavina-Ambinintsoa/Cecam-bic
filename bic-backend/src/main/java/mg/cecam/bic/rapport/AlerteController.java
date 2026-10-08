package mg.cecam.bic.rapport;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.rapport.dto.AlerteDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alertes")
@RequiredArgsConstructor
public class AlerteController {
    private final AlerteService alerteService;

    @GetMapping
    public List<AlerteDTO> lister() {
        return alerteService.lister();
    }
}
