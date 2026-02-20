package com.example.controller;

import com.example.SunObra.marketplace.enums.ServicioStatus;
import com.example.SunObra.marketplace.enums.SolicitudStatus;
import com.example.SunObra.marketplace.repository.ReviewRepository;
import com.example.SunObra.marketplace.repository.ServicioRepository;
import com.example.SunObra.marketplace.repository.SolicitudRepository;
import com.example.model.usuarios;
import com.example.service.AdminAlertService;
import com.example.service.ImportResult;
import com.example.service.ReportService;
import com.example.service.UsuarioService;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controlador para las funcionalidades administrativas.
 *
 * Incluye CRUD de usuarios, reportes y carga masiva desde CSV/Excel.
 * + Dashboard con métricas marketplace (solicitudes, servicios, reviews)
 * + Alertas simples (AdminAlertService)
 * + Endpoint de salud (Actuator)
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ReportService reportService;

    @Autowired
    private SolicitudRepository solicitudRepository;

    @Autowired
    private ServicioRepository servicioRepository;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private AdminAlertService adminAlertService;

    private boolean hasRole(HttpSession session, String role) {
        Object r = session.getAttribute("user_role");
        return r != null && Objects.equals(r.toString(), role);
    }

    // =======================
    // DASHBOARD
    // =======================
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!hasRole(session, "admin")) {
            return "redirect:/auth/login";
        }
        model.addAttribute("title", "Panel de Administración");
        model.addAttribute("totalUsuarios", usuarioService.listarUsuarios().size());
        model.addAttribute("fecha", LocalDateTime.now());

        // Métricas de solicitudes
        long solicitudesAbiertas = solicitudRepository.countByEstado(SolicitudStatus.ABIERTA);
        long solicitudesCerradas = solicitudRepository.countByEstado(SolicitudStatus.CERRADA);
        long solicitudesCanceladas = solicitudRepository.countByEstado(SolicitudStatus.CANCELADA);

        // Métricas de servicios
        long serviciosProgramados = servicioRepository.countByEstado(ServicioStatus.PROGRAMADO);
        long serviciosEnProceso = servicioRepository.countByEstado(ServicioStatus.EN_PROCESO);
        long serviciosFinalizados = servicioRepository.countByEstado(ServicioStatus.FINALIZADO);
        long serviciosCancelados = servicioRepository.countByEstado(ServicioStatus.CANCELADO);

        long totalReviews = reviewRepository.count();

        model.addAttribute("solicitudesAbiertas", solicitudesAbiertas);
        model.addAttribute("solicitudesCerradas", solicitudesCerradas);
        model.addAttribute("solicitudesCanceladas", solicitudesCanceladas);
        model.addAttribute("serviciosProgramados", serviciosProgramados);
        model.addAttribute("serviciosEnProceso", serviciosEnProceso);
        model.addAttribute("serviciosFinalizados", serviciosFinalizados);
        model.addAttribute("serviciosCancelados", serviciosCancelados);
        model.addAttribute("totalReviews", totalReviews);

        // Alertas
        Map<String, List<?>> alerts = adminAlertService.getAlerts();
        model.addAttribute("alertSolicitudes", alerts.getOrDefault("solicitudes", Collections.emptyList()).size());
        model.addAttribute("alertServicios", alerts.getOrDefault("servicios", Collections.emptyList()).size());

        return "admin/dashboard";
    }

    // =======================
    // CRUD USUARIOS
    // =======================
    @GetMapping("/users")
    public String listUsers(HttpSession session, Model model,
                            @RequestParam(required = false) String role,
                            @RequestParam(required = false) String q) {
        if (!hasRole(session, "admin")) return "redirect:/auth/login";

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
        if (!hasRole(session, "admin")) return "redirect:/auth/login";
        model.addAttribute("usuario", new usuarios());
        model.addAttribute("isEdit", false);
        return "admin/user_form";
    }

    @PostMapping("/users")
    public String createUser(HttpSession session, @ModelAttribute("usuario") usuarios user,
                             BindingResult result, RedirectAttributes ra) {
        if (!hasRole(session, "admin")) return "redirect:/auth/login";

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
            ra.addFlashAttribute("success", "Usuario creado correctamente.");
            return "redirect:/admin/users";
        } catch (Exception e) {
            result.rejectValue("email", "email.duplicate", "Error al crear usuario: " + e.getMessage());
            return "admin/user_form";
        }
    }

    @GetMapping("/users/{id}/edit")
    public String editUser(HttpSession session, @PathVariable Long id, Model model) {
        if (!hasRole(session, "admin")) return "redirect:/auth/login";
        usuarios u = usuarioService.buscarPorId(id);
        if (u == null) return "redirect:/admin/users";
        model.addAttribute("usuario", u);
        model.addAttribute("isEdit", true);
        return "admin/user_form";
    }

    @PostMapping("/users/{id}")
    public String updateUser(HttpSession session, @PathVariable Long id,
                             @ModelAttribute("usuario") usuarios user, BindingResult result,
                             RedirectAttributes ra) {
        if (!hasRole(session, "admin")) return "redirect:/auth/login";
        user.setId(id);
        usuarioService.actualizarUsuario(user);
        ra.addFlashAttribute("success", "Usuario actualizado correctamente.");
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/delete")
    public String deleteUser(HttpSession session, @PathVariable Long id, RedirectAttributes ra) {
        if (!hasRole(session, "admin")) return "redirect:/auth/login";
        usuarioService.eliminarUsuario(id);
        ra.addFlashAttribute("success", "Usuario eliminado.");
        return "redirect:/admin/users";
    }

    // =======================
    // CARGA MASIVA USUARIOS
    // =======================

    /** Vista con el formulario de importación */
    @GetMapping("/users/import")
    public String importForm(HttpSession session, Model model) {
        if (!hasRole(session, "admin")) return "redirect:/auth/login";
        model.addAttribute("title", "Importar usuarios (CSV / Excel)");
        return "admin/users_import";
    }

    /** Procesa el archivo CSV/XLS/XLSX */
    @PostMapping(value = "/users/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String importUsers(HttpSession session,
                              @RequestParam("file") MultipartFile file,
                              RedirectAttributes ra) {
        if (!hasRole(session, "admin")) return "redirect:/auth/login";
        try {
            ImportResult result = usuarioService.importarUsuarios(file);

            String msg = "Importación completada. Registros nuevos: " + result.getInserted();
            if (!result.getDuplicates().isEmpty()) {
                msg += ". Duplicados: " + result.getDuplicates();
            }
            ra.addFlashAttribute("success", msg);

        } catch (Exception e) {
            ra.addFlashAttribute("error", "No se pudo importar: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    /** Descarga plantilla CSV */
    @GetMapping("/users/template.csv")
    public ResponseEntity<ByteArrayResource> downloadTemplate() {
        String header = "nombre,apellido,email,userType,especialidades,experiencia,telefono,direccion,password\n";
        String sample =
                "Juan,Perez,juan.perez@example.com,cliente,,0,3000000000,Cra 1 #2-3,SunObra123*\n" +
                        "Ana,Gomez,ana.gomez@example.com,obrero,Albañileria;Pintura,5,3200000000,Calle 45 #67-89,\n" +
                        "Admin,Sistema,admin@sunobra.com,admin,,0,3138385779,Bogotá,Admin*2025\n";
        byte[] bytes = (header + sample).getBytes(StandardCharsets.UTF_8);
        ByteArrayResource res = new ByteArrayResource(bytes);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=plantilla_usuarios.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .contentLength(bytes.length)
                .body(res);
    }

    // =======================
    // REPORTES
    // =======================
    @GetMapping("/reports")
    public String reports(HttpSession session, Model model,
                          @RequestParam(required = false) String role,
                          @RequestParam(required = false) String especialidad,
                          @RequestParam(required = false) String texto) {
        if (!hasRole(session, "admin")) return "redirect:/auth/login";

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

    @GetMapping("/reports/export/html")
    public ResponseEntity<ByteArrayResource> exportHtml(HttpSession session,
                                                        @RequestParam(required = false) String role,
                                                        @RequestParam(required = false) String especialidad,
                                                        @RequestParam(required = false) String texto) {
        if (!hasRole(session, "admin")) return ResponseEntity.status(403).build();
        try {
            List<usuarios> data = getFilteredUsers(role, especialidad, texto);
            String title = "Reporte de Usuarios - " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            byte[] report = reportService.generateReport(data, title, "html");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_usuarios.html")
                    .contentType(MediaType.TEXT_HTML)
                    .contentLength(report.length)
                    .body(new ByteArrayResource(report));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/reports/export/csv")
    public ResponseEntity<ByteArrayResource> exportCsv(HttpSession session,
                                                       @RequestParam(required = false) String role,
                                                       @RequestParam(required = false) String especialidad,
                                                       @RequestParam(required = false) String texto) {
        if (!hasRole(session, "admin")) return ResponseEntity.status(403).build();
        try {
            List<usuarios> data = getFilteredUsers(role, especialidad, texto);
            String title = "Reporte de Usuarios - " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            byte[] report = reportService.generateReport(data, title, "csv");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_usuarios.csv")
                    .contentType(MediaType.TEXT_PLAIN)
                    .contentLength(report.length)
                    .body(new ByteArrayResource(report));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/reports/export/txt")
    public ResponseEntity<ByteArrayResource> exportTxt(HttpSession session,
                                                       @RequestParam(required = false) String role,
                                                       @RequestParam(required = false) String especialidad,
                                                       @RequestParam(required = false) String texto) {
        if (!hasRole(session, "admin")) return ResponseEntity.status(403).build();
        try {
            List<usuarios> data = getFilteredUsers(role, especialidad, texto);
            String title = "Reporte de Usuarios - " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            byte[] report = reportService.generateReport(data, title, "txt");
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reporte_usuarios.txt")
                    .contentType(MediaType.TEXT_PLAIN)
                    .contentLength(report.length)
                    .body(new ByteArrayResource(report));
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    // Auxiliar de filtros
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

    // =======================
    // ACTUATOR / HEALTH
    // =======================
    /** Endpoint para ver salud de la aplicación (usa Actuator) */
    @GetMapping("/health")
    public String health(HttpSession session) {
        if (!hasRole(session, "admin")) {
            return "redirect:/auth/login";
        }
        return "forward:/actuator/health";
    }
}
