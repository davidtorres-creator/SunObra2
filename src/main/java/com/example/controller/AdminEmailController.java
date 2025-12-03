package com.example.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.service.EmailService;

import jakarta.servlet.http.HttpSession;

/**
 * Controlador para la funcionalidad de envío de correos masivos
 * disponible únicamente para el rol de administrador.
 */
@Controller
@RequestMapping("/admin/email")
public class    AdminEmailController {

    @Autowired
    private EmailService emailService;

    /**
     * Verifica si el usuario autenticado tiene rol de administrador.
     *
     * @param session sesión HTTP actual
     * @return true si el usuario tiene rol "admin", false en caso contrario
     */
    private boolean isAdmin(HttpSession session) {
        Object role = session.getAttribute("user_role");
        return role != null && "admin".equals(role.toString());
    }

    /**
     * Muestra el formulario para enviar correos masivos.
     * Solo los administradores pueden acceder a esta vista.
     *
     * @param model   modelo utilizado por Thymeleaf
     * @param session sesión HTTP actual
     * @return nombre de la plantilla a renderizar
     */
    @GetMapping("/form")
    public String showEmailForm(Model model, HttpSession session) {
        if (!isAdmin(session)) {
            // Si no es admin, redirigir al login
            return "redirect:/auth/login";
        }
        // Inicializar campos vacíos y mensajes
        model.addAttribute("recipients", "");
        model.addAttribute("subject", "");
        model.addAttribute("message", "");
        return "admin/email_form";
    }

    /**
     * Procesa el formulario de envío de correos masivos.
     *
     * @param recipients lista de destinatarios separados por coma
     * @param subject    asunto del correo
     * @param message    cuerpo del mensaje (se usará en la plantilla)
     * @param session    sesión HTTP actual
     * @param redirectAttributes permite enviar mensajes de éxito/error al redirigir
     * @return redirección al formulario con mensajes de resultado
     */
    @PostMapping("/send-mass")
    public String sendMassEmail(@RequestParam("recipients") String recipients,
                                @RequestParam("subject") String subject,
                                @RequestParam("message") String message,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        if (!isAdmin(session)) {
            return "redirect:/auth/login";
        }
        try {
            // Convertir cadena de correos en lista
            List<String> emails = Arrays.asList(recipients.split(","));
            // Llamar al servicio para enviar correos
            emailService.sendMassEmail(emails, subject, message);
            redirectAttributes.addFlashAttribute("success",
                    "Correos enviados exitosamente a " + emails.size() + " destinatarios.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Error enviando correos: " + e.getMessage());
        }
        // Redirigir nuevamente al formulario para mostrar el mensaje
        return "redirect:/admin/email/form";
    }
}
