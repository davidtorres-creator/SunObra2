package com.example.SunObra.marketplace.controller;

import com.example.SunObra.marketplace.service.SolicitudCreateRequest;
import com.example.SunObra.marketplace.service.SolicitudService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
        import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
@RequestMapping("/cliente/marketplace")
public class ClienteMarketplaceController {

    private final SolicitudService solicitudService;

    public ClienteMarketplaceController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }

    // ✅ Reutilizamos sesión: en tu app guardas rol e id
    private boolean isCliente(HttpSession session) {
        Object role = session.getAttribute("user_role");
        return role != null && Objects.equals(role.toString(), "cliente");
    }

    private Long getUserId(HttpSession session) {
        Object id = session.getAttribute("user_id");
        if (id == null) return null;
        if (id instanceof Long) return (Long) id;
        return Long.valueOf(id.toString());
    }

    @GetMapping("/solicitudes")
    public String listar(HttpSession session, Model model) {
        if (!isCliente(session)) return "redirect:/auth/login";

        Long clienteId = getUserId(session);
        model.addAttribute("solicitudes", solicitudService.listarPorCliente(clienteId));
        return "cliente/marketplace/solicitudes_list";
    }

    @GetMapping("/solicitudes/new")
    public String formNueva(HttpSession session, Model model) {
        if (!isCliente(session)) return "redirect:/auth/login";

        model.addAttribute("form", new SolicitudCreateRequest());
        return "cliente/marketplace/solicitud_form";
    }

    @PostMapping("/solicitudes")
    public String crear(HttpSession session,
                        @ModelAttribute("form") SolicitudCreateRequest form,
                        RedirectAttributes ra) {
        if (!isCliente(session)) return "redirect:/auth/login";

        Long clienteId = getUserId(session);

        try {
            solicitudService.crear(clienteId, form);
            ra.addFlashAttribute("success", "✅ Solicitud creada correctamente.");
            return "redirect:/cliente/marketplace/solicitudes";
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/cliente/marketplace/solicitudes/new";
        }
    }
}

