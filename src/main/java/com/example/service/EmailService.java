package com.example.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${sunobra.mail.from:}")
    private String from;

    @Value("${spring.mail.username:}")
    private String smtpUser;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    /** Envio a un solo destinatario usando plantilla Thymeleaf */
    @Async("emailExecutor")
    public CompletableFuture<Boolean> sendToOne(
            String to, String subject, String templateName, Map<String, Object> vars, @Nullable String replyTo) {
        try {
            String html = render(templateName, vars);
            sendMime(to, subject, html, replyTo);
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            log.error("Failed sending email to {} subject={}.", to, subject, e);
            return CompletableFuture.failedFuture(e);
        }
    }

    /** Envío masivo simple (secuencial en lotes pequeños para Gmail personal) */
    public void sendBulk(List<String> recipients, String subject, String templateName, Map<String, Object> varsBase) {
        final int BATCH_SIZE = 75;
        final long PAUSE_MS = 5000;

        for (int i = 0; i < recipients.size(); i += BATCH_SIZE) {
            var slice = recipients.subList(i, Math.min(i + BATCH_SIZE, recipients.size()));
                var futures = slice.stream()
                        .map(to -> {
                            Map<String, Object> vars = varsBase == null ? Map.of() : varsBase;
                            return sendToOne(to, subject, templateName, vars, null);
                        })
                    .toList();
            // Esperar lote
            futures.forEach(f -> { try { f.join(); } catch (Exception ignore) {} });
            try { Thread.sleep(PAUSE_MS); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); }
        }
    }

    /* Helpers */
    private String render(String templateName, Map<String, Object> vars) {
        Context ctx = new Context(new Locale("es", "CO"));
        if (vars != null) vars.forEach(ctx::setVariable);
        // Busca en: src/main/resources/templates/email/<templateName>.html
        return templateEngine.process("email/" + templateName, ctx);
    }

    private void sendMime(String to, String subject, String html, @Nullable String replyTo) throws Exception {
        MimeMessage mime = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(
                mime, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

        String finalFrom = (from != null && !from.isBlank()) ? from : smtpUser;
        if (finalFrom == null || finalFrom.isBlank()) {
            throw new IllegalStateException("Remitente no configurado. Define sunobra.mail.from o spring.mail.username.");
        }

        helper.setFrom(finalFrom);
        helper.setTo(to);
        if (replyTo != null && !replyTo.isBlank()) helper.setReplyTo(replyTo);
        helper.setSubject(subject);
        helper.setText(html, true);
        // Use the injected JavaMailSender instance to send the mime message
        mailSender.send(mime);
    }
}
