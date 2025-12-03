package com.example.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    // Enviar a una lista de correos
    public void sendMassEmail(List<String> toEmails, String subject, String messageBody) throws Exception {
        for (String to : toEmails) {
            sendEmail(to.trim(), subject, messageBody);
        }
    }

    // Enviar un correo individual
    private void sendEmail(String to, String subject, String messageBody) throws Exception {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        Context context = new Context();
        context.setVariable("message", messageBody);

        String htmlContent = templateEngine.process("email-template", context);

        helper.setText(htmlContent, true);   // enviar como HTML
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom("davideltorres2@gmail.com");  // o el remitente configurado

        mailSender.send(mimeMessage);
    }
}

