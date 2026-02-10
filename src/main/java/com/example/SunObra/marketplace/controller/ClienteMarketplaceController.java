package com.example.SunObra.marketplace.controller;

import com.example.SunObra.marketplace.model.Solicitud;
import com.example.SunObra.marketplace.service.MarketplaceClienteService;
import com.example.SunObra.marketplace.service.ServicioService;
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
    private final MarketplaceClienteService marketplaceClienteService;
    private final ServicioService servicioService;

    public ClienteMarketplaceController(SolicitudService solicitudService,
                                        MarketplaceClienteService marketplaceClienteService,
                                        ServicioService servicioService) {
        this.solicitudService = solicitudService;
        this.marketplaceClienteService = marketplaceClienteService;
        this.servicioService = servicioService;
    }

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

    @GetMapping("/solicitudes/{id}")
    public String detalle(HttpSession session, @PathVariable Long id, Model model) {
        if (!isCliente(session)) return "redirect:/auth/login";

        Long clienteId = getUserId(session);

        Solicitud solicitud = solicitudService.obtenerDetalle(id, clienteId);
        if (solicitud == null) return "redirect:/cliente/marketplace/solicitudes";

        model.addAttribute("solicitud", solicitud);
        model.addAttribute("cotizaciones", marketplaceClienteService.cotizacionesDeSolicitud(id));

        return "cliente/marketplace/solicitud_detail";
    }

    @PostMapping("/solicitudes/{solicitudId}/cotizaciones/{cotizacionId}/aceptar")
    public String aceptarCotizacion(HttpSession session,
                                    @PathVariable Long solicitudId,
                                    @PathVariable Long cotizacionId,
                                    RedirectAttributes ra) {
        if (!isCliente(session)) return "redirect:/auth/login";

        Long clienteId = getUserId(session);

        try {
            marketplaceClienteService.aceptarCotizacion(clienteId, solicitudId, cotizacionId);
            ra.addFlashAttribute("success", "✅ Cotización aceptada. Servicio creado.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/cliente/marketplace/solicitudes/" + solicitudId;
    }

    @GetMapping("/servicios")
    public String misServicios(HttpSession session, Model model) {
        if (!isCliente(session)) return "redirect:/auth/login";

        Long clienteId = getUserId(session);
        model.addAttribute("servicios", servicioService.listarPorCliente(clienteId));
        return "cliente/marketplace/servicios_list";
    }
}

