package mg.cecam.bic.rapport;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.rapport.dto.AlerteDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alertes")
@RequiredArgsConstructor
public class AlerteController {
    private final AlerteService alerteService;

    @GetMapping
    public List<AlerteDTO> lister(
            @RequestParam(required = false) String dateDebut,
            @RequestParam(required = false) String dateFin,
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String codeClientCb,
            @RequestParam(required = false) String codeContratCb,
            @RequestParam(required = false) String agence) {
        return alerteService.lister(dateDebut, dateFin, nom, codeClientCb, codeContratCb, agence);
    }
}
