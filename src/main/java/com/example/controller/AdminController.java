package com.example.controller;

import com.example.model.usuarios;
import com.example.service.UsuarioService;
import com.example.service.ReportService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Controlador para las funcionalidades administrativas.
 *
 * <p>Incluye operaciones de CRUD para usuarios y la generación de reportes
 * en distintos formatos. Este controlador utiliza {@link ReportService}
 * para delegar la generación de reportes, aplicando así el patrón de
 * estrategia en la capa de servicio.</p>
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ReportService reportService;

    private boolean hasRole(HttpSession session, String role) {
        Object r = session.getAttribute("user_role");
        return r != null && Objects.equals(r.toString(), role);
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        System.out.println("=== ADMIN DASHBOARD ===");
        System.out.println("Session user_id: " + session.getAttribute("user_id"));
        System.out.println("Session user_role: " + session.getAttribute("user_role"));
        if (!hasRole(session, "admin")) {
            System.out.println("ERROR: Usuario no tiene rol de admin");
            return "redirect:/auth/login";
        }
        model.addAttribute("title", "Panel de Administración");
        model.addAttribute("totalUsuarios", usuarioService.listarUsuarios().size());
        model.addAttribute("fecha", LocalDateTime.now());
        System.out.println("=== REDIRIGIENDO A ADMIN DASHBOARD ===");
        return "admin/dashboard";
    }

    // CRUD Usuarios
    @GetMapping("/users")
    public String listUsers(HttpSession session, Model model,
                            @RequestParam(required = false) String role,
                            @RequestParam(required = false) String q) {
        if (!hasRole(session, "admin")) {
            return "redirect:/auth/login";
        }
        List<usuarios> data = new ArrayList<>(usuarioService.listarUsuarios());
        if (role != null && !role.isBlank()) {
            data = data.stream()
                    .filter(u -> role.equalsIgnoreCase(u.getUserType()))
                    .collect(Collectors.toList());
        }
        if (q != null && !q.isBlank()) {
            String term = q.toLowerCase();
            data = data.stream()
                    .filter(u -> (u.getNombre() != null && u.getNombre().toLowerCase().contains(term)) ||
                            (u.getApellido() != null && u.getApellido().toLowerCase().contains(term)) ||
                            (u.getEmail() != null && u.getEmail().toLowerCase().contains(term)))
                    .collect(Collectors.toList());
        }
        model.addAttribute("title", "Usuarios");
        model.addAttribute("usuarios", data);
        model.addAttribute("filterRole", role);
        model.addAttribute("q", q);
        return "admin/users_list";
    }

    @GetMapping("/users/new")
    public String newUser(HttpSession session, Model model) {
        if (!hasRole(session, "admin")) {
            return "redirect:/auth/login";
        }
        model.addAttribute("usuario", new usuarios());
        model.addAttribute("isEdit", false);
        return "admin/user_form";
    }

    @PostMapping("/users")
    public String createUser(HttpSession session, @ModelAttribute("usuario") usuarios user,
                             BindingResult result) {
        if (!hasRole(session, "admin")) {
            return "redirect:/auth/login";
        }
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            result.rejectValue("email", "email.required", "El email es requerido");
        }
        if (user.getPassword() == null || user.getPassword().length() < 6) {
            result.rejectValue("password", "password.min", "Mínimo 6 caracteres");
        }
        if (result.hasErrors()) {
            return "admin/user_form";
        }
        try {
            usuarioService.registrarUsuario(user);
            return "redirect:/admin/users";
        } catch (Exception e) {
            result.rejectValue("email", "email.duplicate", "Error al crear usuario: " + e.getMessage());
            return "admin/user_form";
        }
    }

    @GetMapping("/users/{id}/edit")
    public String editUser(HttpSession session, @PathVariable Long id, Model model) {
        if (!hasRole(session, "admin")) {
            return "redirect:/auth/login";
        }
        usuarios u = usuarioService.buscarPorId(id);
        if (u == null) {
            return "redirect:/admin/users";
        }
        model.addAttribute("usuario", u);
        model.addAttribute("isEdit", true);
        return "admin/user_form";
    }

    @PostMapping("/users/{id}")
    public String updateUser(HttpSession session, @PathVariable Long id,
                             @ModelAttribute("usuario") usuarios user, BindingResult result) {
        if (!hasRole(session, "admin")) {
            return "redirect:/auth/login";
        }
        user.setId(id);
        usuarioService.actualizarUsuario(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(HttpSession session, @PathVariable Long id) {
        if (!hasRole(session, "admin")) {
            return "redirect:/auth/login";
        }
        usuarioService.eliminarUsuario(id);
        return "redirect:/admin/users";
    }

    // Reportes con filtros multicriterio (en memoria)
    @GetMapping("/reports")
    public String reports(HttpSession session, Model model,
                          @RequestParam(required = false) String role,
                          @RequestParam(required = false) String especialidad,
                          @RequestParam(required = false) String texto) {
        if (!hasRole(session, "admin")) {
            return "redirect:/auth/login";
        }
        List<usuarios> data = new ArrayList<>(usuarioService.listarUsuarios());
        if (role != null && !role.isBlank()) {
            data = data.stream()
                    .filter(u -> role.equalsIgnoreCase(u.getUserType()))
                    .collect(Collectors.toList());
        }
        if (especialidad != null && !especialidad.isBlank()) {
            data = data.stream()
                    .filter(u -> u.getEspecialidades() != null &&
                            u.getEspecialidades().toLowerCase().contains(especialidad.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (texto != null && !texto.isBlank()) {
            String t = texto.toLowerCase();
            data = data.stream()
                    .filter(u -> (u.getNombre() != null && u.getNombre().toLowerCase().contains(t)) ||
                            (u.getApellido() != null && u.getApellido().toLowerCase().contains(t)) ||
                            (u.getEmail() != null && u.getEmail().toLowerCase().contains(t)))
                    .collect(Collectors.toList());
        }
        model.addAttribute("title", "Reportes");
        model.addAttribute("usuarios", data);
        model.addAttribute("role", role);
        model.addAttribute("especialidad", especialidad);
        model.addAttribute("texto", texto);
        return "admin/reports";
    }

    // Endpoints para generar reportes en diferentes formatos
    @GetMapping("/reports/export/html")
    public ResponseEntity<ByteArrayResource> exportHtml(HttpSession session,
                                                        @RequestParam(required = false) String role,
                                                        @RequestParam(required = false) String especialidad,
                                                        @RequestParam(required = false) String texto) {
        if (!hasRole(session, "admin")) {
            return ResponseEntity.status(403).build();
        }
        try {
            List<usuarios> data = getFilteredUsers(role, especialidad, texto);
            String title = "Reporte de Usuarios - " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            byte[] report = reportService.generateReport(data, title, "html");
            ByteArrayResource resource = new ByteArrayResource(report);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_usuarios.html")
                    .contentType(MediaType.TEXT_HTML)
                    .contentLength(report.length)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/reports/export/csv")
    public ResponseEntity<ByteArrayResource> exportCsv(HttpSession session,
                                                      @RequestParam(required = false) String role,
                                                      @RequestParam(required = false) String especialidad,
                                                      @RequestParam(required = false) String texto) {
        if (!hasRole(session, "admin")) {
            return ResponseEntity.status(403).build();
        }
        try {
            List<usuarios> data = getFilteredUsers(role, especialidad, texto);
            String title = "Reporte de Usuarios - " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            byte[] report = reportService.generateReport(data, title, "csv");
            ByteArrayResource resource = new ByteArrayResource(report);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_usuarios.csv")
                    .contentType(MediaType.TEXT_PLAIN)
                    .contentLength(report.length)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/reports/export/txt")
    public ResponseEntity<ByteArrayResource> exportTxt(HttpSession session,
                                                      @RequestParam(required = false) String role,
                                                      @RequestParam(required = false) String especialidad,
                                                      @RequestParam(required = false) String texto) {
        if (!hasRole(session, "admin")) {
            return ResponseEntity.status(403).build();
        }
        try {
            List<usuarios> data = getFilteredUsers(role, especialidad, texto);
            String title = "Reporte de Usuarios - " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            byte[] report = reportService.generateReport(data, title, "txt");
            ByteArrayResource resource = new ByteArrayResource(report);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_usuarios.txt")
                    .contentType(MediaType.TEXT_PLAIN)
                    .contentLength(report.length)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Método auxiliar para obtener usuarios filtrados según los parámetros de reporte
    private List<usuarios> getFilteredUsers(String role, String especialidad, String texto) {
        List<usuarios> data = new ArrayList<>(usuarioService.listarUsuarios());
        if (role != null && !role.isBlank()) {
            data = data.stream()
                    .filter(u -> role.equalsIgnoreCase(u.getUserType()))
                    .collect(Collectors.toList());
        }
        if (especialidad != null && !especialidad.isBlank()) {
            data = data.stream()
                    .filter(u -> u.getEspecialidades() != null &&
                            u.getEspecialidades().toLowerCase().contains(especialidad.toLowerCase()))
                    .collect(Collectors.toList());
        }
        if (texto != null && !texto.isBlank()) {
            String t = texto.toLowerCase();
            data = data.stream()
                    .filter(u -> (u.getNombre() != null && u.getNombre().toLowerCase().contains(t)) ||
                            (u.getApellido() != null && u.getApellido().toLowerCase().contains(t)) ||
                            (u.getEmail() != null && u.getEmail().toLowerCase().contains(t)))
                    .collect(Collectors.toList());
        }
        return data;
    }
}