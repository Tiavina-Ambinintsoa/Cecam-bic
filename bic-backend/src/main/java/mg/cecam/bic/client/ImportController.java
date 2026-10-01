package mg.cecam.bic.client;

import lombok.RequiredArgsConstructor;
import mg.cecam.bic.client.dto.ImportResultatDTO;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
public class ImportController {

    private final ImportService importService;

    @GetMapping("/template")
    public ResponseEntity<Resource> telechargerTemplate() {
        Resource fichier = new ClassPathResource("templates-import/template-import-clients.csv");
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv"))
                .header("Content-Disposition", "attachment; filename=template-import-clients.csv")
                .body(fichier);
    }

    @PostMapping(value = "/clients", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ImportResultatDTO> importer(@RequestParam("fichier") MultipartFile fichier) {
        return ResponseEntity.ok(importService.importer(fichier));
    }
}