package com.example.controller;

import com.example.model.usuarios;
import com.example.service.UsuarioService;
import com.example.SunObra.marketplace.repository.ReviewRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

    @Autowired
    private UsuarioService usuarioService;

    // ✅ para leer promedios de reviews
    @Autowired
    private ReviewRepository reviewRepo;

    private boolean hasRole(HttpSession session, String role) {
        Object r = session.getAttribute("user_role");
        return r != null && Objects.equals(r.toString(), role);
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        System.out.println("=== CLIENTE DASHBOARD ===");
        System.out.println("Session user_id: " + session.getAttribute("user_id"));
        System.out.println("Session user_role: " + session.getAttribute("user_role"));

        if (!hasRole(session, "cliente")) {
            System.out.println("ERROR: Usuario no tiene rol de cliente");
            return "redirect:/auth/login";
        }

        Long id = (Long) session.getAttribute("user_id");
        usuarios u = usuarioService.buscarPorId(id);
        System.out.println("Usuario cargado: " + (u != null ? u.getEmail() : "null"));

        model.addAttribute("title", "Dashboard Cliente");
        model.addAttribute("user", u);
        System.out.println("=== REDIRIGIENDO A CLIENTE DASHBOARD ===");
        return "cliente/dashboard";
    }

    @GetMapping("/profile")
    public String profile(HttpSession session, Model model) {
        if (!hasRole(session, "cliente")) {
            return "redirect:/auth/login";
        }
        Long id = (Long) session.getAttribute("user_id");
        usuarios u = usuarioService.buscarPorId(id);
        model.addAttribute("usuario", u);
        return "cliente/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(HttpSession session, @ModelAttribute("usuario") usuarios user) {
        if (!hasRole(session, "cliente")) {
            return "redirect:/auth/login";
        }
        user.setId((Long) session.getAttribute("user_id"));
        user.setUserType("cliente");
        usuarioService.actualizarUsuario(user);
        return "redirect:/cliente/dashboard";
    }

    // Reportes con filtros multicriterio para Cliente
    @GetMapping("/reports")
    public String reports(HttpSession session, Model model,
                          @RequestParam(required = false) String especialidad,
                          @RequestParam(required = false) String experiencia,
                          @RequestParam(required = false) String tarifaMin,
                          @RequestParam(required = false) String tarifaMax,
                          @RequestParam(required = false) String texto,
                          // ✅ NUEVO: ordenar por ranking
                          @RequestParam(required = false, defaultValue = "false") boolean ordenarPorRating) {

        if (!hasRole(session, "cliente")) {
            return "redirect:/auth/login";
        }

        // Obtener solo obreros para el cliente
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

        // ✅ ratingMap para mostrar ⭐ promedio y cantidad de reviews por obrero
        Map<Long, ReviewRepository.ObreroRatingRow> ratingMap = data.isEmpty()
                ? Map.of()
                : reviewRepo.statsByObreroIds(
                data.stream().map(usuarios::getId).toList()
        ).stream().collect(Collectors.toMap(
                ReviewRepository.ObreroRatingRow::getObreroId,
                r -> r
        ));

        // ✅ Ordenar por ranking (rating desc, luego total desc, luego experiencia desc)
        if (ordenarPorRating) {
            data = data.stream()
                    .sorted((a, b) -> {
                        double aAvg = (ratingMap.containsKey(a.getId()) && ratingMap.get(a.getId()).getAvgRating() != null)
                                ? ratingMap.get(a.getId()).getAvgRating() : 0.0;
                        double bAvg = (ratingMap.containsKey(b.getId()) && ratingMap.get(b.getId()).getAvgRating() != null)
                                ? ratingMap.get(b.getId()).getAvgRating() : 0.0;

                        long aTot = (ratingMap.containsKey(a.getId()) && ratingMap.get(a.getId()).getTotal() != null)
                                ? ratingMap.get(a.getId()).getTotal() : 0L;
                        long bTot = (ratingMap.containsKey(b.getId()) && ratingMap.get(b.getId()).getTotal() != null)
                                ? ratingMap.get(b.getId()).getTotal() : 0L;

                        int cmp = Double.compare(bAvg, aAvg); // mejor rating primero
                        if (cmp != 0) return cmp;

                        cmp = Long.compare(bTot, aTot); // más reviews primero
                        if (cmp != 0) return cmp;

                        Integer aExp = a.getExperiencia() != null ? a.getExperiencia() : 0;
                        Integer bExp = b.getExperiencia() != null ? b.getExperiencia() : 0;
                        return Integer.compare(bExp, aExp); // más experiencia primero
                    })
                    .collect(Collectors.toList());
        }

        model.addAttribute("title", "Reportes de Obreros");
        model.addAttribute("obreros", data);
        model.addAttribute("ratingMap", ratingMap);

        // ✅ para que el checkbox quede marcado al recargar
        model.addAttribute("ordenarPorRating", ordenarPorRating);

        model.addAttribute("especialidad", especialidad);
        model.addAttribute("experiencia", experiencia);
        model.addAttribute("tarifaMin", tarifaMin);
        model.addAttribute("tarifaMax", tarifaMax);
        model.addAttribute("texto", texto);

        return "cliente/reports";
    }
}


