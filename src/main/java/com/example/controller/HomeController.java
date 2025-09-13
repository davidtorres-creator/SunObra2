package com.example.controller;

import com.example.model.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/")
public class HomeController {
    
    /**
     * Página principal - Punto de entrada por defecto
     * Equivalente al método index() del IndexController PHP
     */
    @GetMapping
    public String index(Model model, HttpSession session) {
        // Obtener usuario actual de la sesión
        User user = getCurrentUser(session);
        
        // Configuración del sistema (equivalente a getSystemSettings() del PHP)
        Settings settings = Settings.getDefaultSettings();
        
        // Datos para las secciones de la página
        Project[] projects = Project.getSampleProjects();
        TeamMember[] teamMembers = TeamMember.getSampleTeamMembers();
        Testimonial[] testimonials = Testimonial.getSampleTestimonials();
        
        model.addAttribute("title", "SunObra - Plataforma de Servicios de Construcción");
        model.addAttribute("user", user);
        model.addAttribute("isHome", true);
        model.addAttribute("settings", settings);
        model.addAttribute("projects", projects);
        model.addAttribute("teamMembers", teamMembers);
        model.addAttribute("testimonials", testimonials);
        
        return "home";
    }
    
    /**
     * Página de contacto
     */
    @GetMapping("/contacto")
    public String contact(Model model) {
        model.addAttribute("title", "Contacto - SunObra");
        return "contact";
    }
    
    /**
     * Página sobre nosotros
     */
    @GetMapping("/nosotros")
    public String about(Model model) {
        model.addAttribute("title", "Sobre Nosotros - SunObra");
        return "about";
    }
    
    /**
     * Página de servicios
     */
    @GetMapping("/servicios")
    public String services(Model model) {
        model.addAttribute("title", "Servicios - SunObra");
        return "services";
    }
    
    /**
     * Redirección a login
     */
    @GetMapping("/login")
    public String login() {
        return "redirect:/auth/login";
    }
    
    /**
     * Redirección a logout
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
    
    /**
     * Página de bienvenida
     * Equivalente al método welcome() del IndexController PHP
     */
    @GetMapping("/welcome")
    public String welcome(Model model, HttpSession session) {
        User user = getCurrentUser(session);
        
        model.addAttribute("title", "Bienvenido a SunObra");
        model.addAttribute("user", user);
        
        return "welcome";
    }
    
    /**
     * Página de inicio para usuarios autenticados
     * Equivalente al método dashboard() del IndexController PHP
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        if (!isAuthenticated(session)) {
            return "redirect:/login";
        }
        
        return redirectToDashboard(session);
    }
    
    /**
     * Redirigir al dashboard según el rol
     * Equivalente al método redirectToDashboard() del IndexController PHP
     */
    private String redirectToDashboard(HttpSession session) {
        String role = (String) session.getAttribute("user_role");
        
        if (role == null) {
            return "redirect:/";
        }
        
        switch (role) {
            case "admin":
                return "redirect:/admin/dashboard";
            case "cliente":
                return "redirect:/cliente/dashboard";
            case "obrero":
                return "redirect:/obrero/dashboard";
            default:
                return "redirect:/";
        }
    }
    
    /**
     * Página de error 404 personalizada
     * Equivalente al método notFound() del IndexController PHP
     */
    @GetMapping("/404")
    public String notFound(Model model) {
        model.addAttribute("title", "Página No Encontrada - SunObra");
        return "errors/404";
    }
    
    /**
     * Página de error 500 personalizada
     * Equivalente al método serverError() del IndexController PHP
     */
    @GetMapping("/500")
    public String serverError(Model model) {
        model.addAttribute("title", "Error del Servidor - SunObra");
        return "errors/500";
    }
    
    /**
     * Manejar parámetros GET para compatibilidad
     * Equivalente al método handleGetParams() del IndexController PHP
     */
    @GetMapping("/handle")
    public String handleGetParams(@RequestParam(required = false) String view,
                                  @RequestParam(required = false) String action,
                                  HttpSession session) {
        
        if (view == null) {
            return "redirect:/";
        }
        
        // Mapear parámetros GET a rutas
        switch (view) {
            case "root":
                if ("dashboard".equals(action)) {
                    return dashboard(session);
                } else {
                    return index(null, session);
                }
            case "auth":
                switch (action) {
                    case "login":
                        return "redirect:/login";
                    case "register":
                        return "redirect:/register";
                    case "logout":
                        return "redirect:/logout";
                    default:
                        return index(null, session);
                }
            case "admin":
                if (isAuthenticated(session) && "admin".equals(session.getAttribute("user_role"))) {
                    switch (action) {
                        case "dashboard":
                            return "redirect:/admin/dashboard";
                        case "users":
                            return "redirect:/admin/users";
                        default:
                            return "redirect:/admin/dashboard";
                    }
                } else {
                    return "redirect:/login";
                }
            case "cliente":
                if (isAuthenticated(session) && "cliente".equals(session.getAttribute("user_role"))) {
                    switch (action) {
                        case "dashboard":
                            return "redirect:/cliente/dashboard";
                        case "profile":
                            return "redirect:/cliente/profile";
                        default:
                            return "redirect:/cliente/dashboard";
                    }
                } else {
                    return "redirect:/login";
                }
            case "obrero":
                if (isAuthenticated(session) && "obrero".equals(session.getAttribute("user_role"))) {
                    switch (action) {
                        case "dashboard":
                            return "redirect:/obrero/dashboard";
                        case "profile":
                            return "redirect:/obrero/profile";
                        default:
                            return "redirect:/obrero/dashboard";
                    }
                } else {
                    return "redirect:/login";
                }
            default:
                return index(null, session);
        }
    }
    
    /**
     * Obtiene el usuario actual de la sesión
     * Equivalente al método getCurrentUser() del IndexController PHP
     */
    private User getCurrentUser(HttpSession session) {
        Long userId = (Long) session.getAttribute("user_id");
        if (userId != null) {
            // En una aplicación real, esto vendría de la base de datos
            // Por ahora, retornamos un usuario de ejemplo basado en el rol
            String role = (String) session.getAttribute("user_role");
            if ("admin".equals(role)) {
                return User.getSampleAdminUser();
            } else if ("obrero".equals(role)) {
                return User.getSampleObreroUser();
            } else {
                return User.getSampleUser();
            }
        }
        return null;
    }
    
    /**
     * Verifica si el usuario está autenticado
     * Equivalente al método isAuthenticated() del IndexController PHP
     */
    private boolean isAuthenticated(HttpSession session) {
        return session.getAttribute("user_id") != null;
    }
    
    /**
     * Obtiene la configuración del sistema
     * Equivalente al método getSystemSettings() del controlador PHP
     * @deprecated Usar Settings.getDefaultSettings() en su lugar
     */
    @Deprecated
    private Map<String, String> getSystemSettings() {
        Map<String, String> settings = new HashMap<>();
        
        // Configuraciones por defecto (en una aplicación real vendrían de la base de datos)
        settings.put("logo_url", "/images/logo.png");
        settings.put("site_name", "SunObra");
        settings.put("site_description", "Plataforma de Servicios de Construcción");
        settings.put("contact_phone", "3138385779");
        settings.put("contact_email", "sunobra69@gmail.com");
        settings.put("contact_address", "Bogotá, Colombia");
        
        return settings;
    }
}