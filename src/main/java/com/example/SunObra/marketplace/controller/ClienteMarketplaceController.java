package com.example.SunObra.marketplace.controller;

import com.example.SunObra.marketplace.model.Solicitud;
import com.example.SunObra.marketplace.service.MarketplaceClienteService;
import com.example.SunObra.marketplace.service.ReviewCreateRequest;
import com.example.SunObra.marketplace.service.ReviewService;
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
    private final ReviewService reviewService;

    public ClienteMarketplaceController(SolicitudService solicitudService,
                                        MarketplaceClienteService marketplaceClienteService,
                                        ServicioService servicioService,
                                        ReviewService reviewService) {
        this.solicitudService = solicitudService;
        this.marketplaceClienteService = marketplaceClienteService;
        this.servicioService = servicioService;
        this.reviewService = reviewService;
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

        var servicios = servicioService.listarPorCliente(clienteId);
        model.addAttribute("servicios", servicios);

        // Mapa: servicioId -> rating (para mostrar "Ya calificado ⭐X")
        var ids = servicios.stream().map(s -> s.getId()).toList();
        model.addAttribute("reviewRatings", reviewService.ratingsPorServicioIds(ids));

        return "cliente/marketplace/servicios_list";
    }


    @GetMapping("/servicios/{id}/review")
    public String formReview(HttpSession session, @PathVariable Long id, Model model) {
        if (!isCliente(session)) return "redirect:/auth/login";

        if (reviewService.buscarPorServicio(id) != null) {
            return "redirect:/cliente/marketplace/servicios";
        }

        model.addAttribute("servicioId", id);
        model.addAttribute("form", new ReviewCreateRequest());
        return "cliente/marketplace/review_form";
    }

    @PostMapping("/servicios/{id}/review")
    public String guardarReview(HttpSession session,
                                @PathVariable Long id,
                                @ModelAttribute("form") ReviewCreateRequest form,
                                RedirectAttributes ra) {
        if (!isCliente(session)) return "redirect:/auth/login";

        Long clienteId = getUserId(session);

        try {
            reviewService.crearReview(clienteId, id, form);
            ra.addFlashAttribute("success", "✅ Calificación enviada.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
            return "redirect:/cliente/marketplace/servicios/" + id + "/review";
        }

        return "redirect:/cliente/marketplace/servicios";
    }
}
