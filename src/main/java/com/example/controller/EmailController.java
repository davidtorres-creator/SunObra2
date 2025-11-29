package com.example.controller;

import com.example.service.EmailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    /** == Como tu ejemplo: servir el formulario simple == */
    @GetMapping("/")
    public String home(Model model) {
        return "redirect:/index.html"; // si dejas index en static/
    }

    /** == Como tu ejemplo: enviar texto plano == */
    @PostMapping("/sendMail")
    @ResponseBody
    public String sendMail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String message
    ) {
        emailService.sendSimpleMail(to, subject, message, null);
        return "OK simple";
    }

    /** == Como tu ejemplo: enviar usando plantilla HTML == */
    @PostMapping("/sendMailTemplate")
    @ResponseBody
    public String sendMailTemplate(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String nombre,
            @RequestParam(required = false, defaultValue = "") String contenido
    ) throws Exception {
        var vars = Map.of(
                "nombre", nombre,
                "contenido", contenido
        );
        // plantilla: src/main/resources/templates/email-template.html
        emailService.sendHtmlTemplate(to, subject, "email-template", vars, null);
        return "OK html";
    }

    /** (Opcional) masivo simple tipo ejemplo: separados por coma */
    @PostMapping("/sendMailBulk")
    @ResponseBody
    public String sendMailBulk(
            @RequestParam String toList,
            @RequestParam String subject,
            @RequestParam String message
    ) {
        List<String> emails = Arrays.stream(toList.split(","))
                .map(String::trim).filter(s -> !s.isBlank()).toList();
        for (String to : emails) {
            emailService.sendSimpleMail(to, subject, message, null);
        }
        return "OK bulk " + emails.size();
    }
}

