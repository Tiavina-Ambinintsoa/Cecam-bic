package mg.cecam.bic.rapport;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import lombok.RequiredArgsConstructor;
import mg.cecam.bic.rapport.dto.RapportSolvabiliteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayOutputStream;

@Service
@RequiredArgsConstructor
public class RapportPdfService {

    private static final Logger log = LoggerFactory.getLogger(RapportPdfService.class);

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
            log.error("Échec de génération du PDF du rapport de solvabilité", e);
            Throwable cause = e;
            while (cause.getCause() != null && cause.getCause() != cause) {
                cause = cause.getCause();
            }
            String causeMessage = cause.getMessage();
            String causeDetail = cause.getClass().getSimpleName()
                    + (causeMessage == null || causeMessage.isBlank() ? "" : ": " + causeMessage);
            throw new IllegalStateException("Erreur lors de la génération du PDF (" + causeDetail + ")", e);
        }
        return out.toByteArray();
    }
}