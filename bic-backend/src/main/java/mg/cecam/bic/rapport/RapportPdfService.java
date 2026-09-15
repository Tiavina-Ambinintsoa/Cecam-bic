package mg.cecam.bic.rapport;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import mg.cecam.bic.rapport.dto.RapportSolvabiliteResponse;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class RapportPdfService {

    private final SpringTemplateEngine templateEngine;

    public String genererHtml(RapportSolvabiliteResponse rapport) {
        Context context = new Context();
        context.setVariable("r", rapport);
        return templateEngine.process("rapport-solvabilite", context);
    }

    public byte[] genererPdf(RapportSolvabiliteResponse rapport) {
        String html = genererHtml(rapport);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.withHtmlContent(html, null);
            builder.toStream(out);
            builder.run();
        } catch (Exception e) {
            throw new IllegalStateException("Erreur lors de la génération du PDF", e);
        }
        return out.toByteArray();
    }
}