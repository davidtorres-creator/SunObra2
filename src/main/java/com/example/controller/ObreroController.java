                               package com.example.controller;

import com.example.model.usuarios;
import com.example.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.List;

@Controller
@RequestMapping("/obrero")
public class ObreroController {

    @Autowired
    private UsuarioService usuarioService;

    private boolean hasRole(HttpSession session, String role) {
        Object r = session.getAttribute("user_role");
        return r != null && Objects.equals(r.toString(), role);
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        System.out.println("=== OBRERO DASHBOARD ===");
        System.out.println("Session user_id: " + session.getAttribute("user_id"));
        System.out.println("Session user_role: " + session.getAttribute("user_role"));
        
        if (!hasRole(session, "obrero")) {
            System.out.println("ERROR: Usuario no tiene rol de obrero");
            return "redirect:/auth/login";
        }
        
        Long id = (Long) session.getAttribute("user_id");
        usuarios u = usuarioService.buscarPorId(id);
        System.out.println("Usuario cargado: " + (u != null ? u.getEmail() : "null"));
        
        model.addAttribute("title", "Dashboard Obrero");
        model.addAttribute("user", u);
        System.out.println("=== REDIRIGIENDO A OBRERO DASHBOARD ===");
        return "obrero/dashboard";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        if (!hasRole(session, "obrero")) {
            return "redirect:/auth/login";
        }
        Long id = (Long) session.getAttribute("user_id");
        usuarios u = usuarioService.buscarPorId(id);
        model.addAttribute("usuario", u);
        return "obrero/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(HttpSession session, @ModelAttribute("usuario") usuarios user) {
        if (!hasRole(session, "obrero")) {
            return "redirect:/auth/login";
        }
        user.setId((Long) session.getAttribute("user_id"));
        user.setUserType("obrero");
        usuarioService.actualizarUsuario(user);
        return "redirect:/obrero/dashboard";
    }

    // Reportes con filtros multicriterio para Obrero
    @GetMapping("/reports")
    public String reports(HttpSession session, Model model,
                          @RequestParam(required = false) String especialidad,
                          @RequestParam(required = false) String experiencia,
                          @RequestParam(required = false) String tarifaMin,
                          @RequestParam(required = false) String tarifaMax,
                          @RequestParam(required = false) String texto) {
        if (!hasRole(session, "obrero")) {
            return "redirect:/auth/login";
        }
        
        // Obtener solo otros obreros para comparación
        List<usuarios> data = usuarioService.listarUsuarios().stream()
                .filter(u -> "obrero".equals(u.getUserType()))
                .collect(Collectors.toList());
        
        // Aplicar filtros multicriterio
        if (especialidad != null && !especialidad.isBlank()) {
            data = data.stream()
                    .filter(u -> u.getEspecialidades() != null && 
                               u.getEspecialidades().toLowerCase().contains(especialidad.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        if (experiencia != null && !experiencia.isBlank()) {
            try {
                int exp = Integer.parseInt(experiencia);
                data = data.stream()
                        .filter(u -> u.getExperiencia() != null && u.getExperiencia() >= exp)
                        .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                // Ignorar filtro si no es un número válido
            }
        }
        
        if (tarifaMin != null && !tarifaMin.isBlank()) {
            try {
                double min = Double.parseDouble(tarifaMin);
                data = data.stream()
                        .filter(u -> u.getTarifaHora() != null && u.getTarifaHora() >= min)
                        .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                // Ignorar filtro si no es un número válido
            }
        }
        
        if (tarifaMax != null && !tarifaMax.isBlank()) {
            try {
                double max = Double.parseDouble(tarifaMax);
                data = data.stream()
                        .filter(u -> u.getTarifaHora() != null && u.getTarifaHora() <= max)
                        .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                // Ignorar filtro si no es un número válido
            }
        }
        
        if (texto != null && !texto.isBlank()) {
            String t = texto.toLowerCase();
            data = data.stream()
                    .filter(u ->
                        (u.getNombre() != null && u.getNombre().toLowerCase().contains(t)) ||
                        (u.getApellido() != null && u.getApellido().toLowerCase().contains(t)) ||
                        (u.getEmail() != null && u.getEmail().toLowerCase().contains(t)) ||
                        (u.getEspecialidades() != null && u.getEspecialidades().toLowerCase().contains(t))
                    )
                    .collect(Collectors.toList());
        }
        
        model.addAttribute("title", "Reportes de Obreros");
        model.addAttribute("obreros", data);
        model.addAttribute("especialidad", especialidad);
        model.addAttribute("experiencia", experiencia);
        model.addAttribute("tarifaMin", tarifaMin);
        model.addAttribute("tarifaMax", tarifaMax);
        model.addAttribute("texto", texto);
        return "obrero/reports";
    }
}


