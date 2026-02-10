package com.example.SunObra.marketplace.controller;

import com.example.SunObra.marketplace.enums.SolicitudStatus;
import com.example.SunObra.marketplace.model.Cotizacion;
import com.example.SunObra.marketplace.model.Solicitud;
import com.example.SunObra.marketplace.repository.CotizacionRepository;
import com.example.SunObra.marketplace.repository.SolicitudRepository;
import com.example.SunObra.marketplace.service.CotizacionCreateRequest;
import com.example.SunObra.marketplace.service.CotizacionService;
import com.example.SunObra.marketplace.service.ServicioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Objects;

@Controller
@RequestMapping("/obrero/marketplace")
public class ObreroMarketplaceController {

    private final SolicitudRepository solicitudRepo;
    private final CotizacionService cotizacionService;
    private final CotizacionRepository cotizacionRepo;
    private final ServicioService servicioService;

    public ObreroMarketplaceController(SolicitudRepository solicitudRepo,
                                       CotizacionService cotizacionService,
                                       CotizacionRepository cotizacionRepo,
                                       ServicioService servicioService) {
        this.solicitudRepo = solicitudRepo;
        this.cotizacionService = cotizacionService;
        this.cotizacionRepo = cotizacionRepo;
        this.servicioService = servicioService;
    }

    private boolean isObrero(HttpSession session) {
        Object role = session.getAttribute("user_role");
        return role != null && Objects.equals(role.toString(), "obrero");
    }

    private Long getUserId(HttpSession session) {
        Object id = session.getAttribute("user_id");
        if (id == null) return null;
        if (id instanceof Long) return (Long) id;
        return Long.valueOf(id.toString());
    }

    @GetMapping("/solicitudes")
    public String solicitudesAbiertas(HttpSession session, Model model) {
        if (!isObrero(session)) return "redirect:/auth/login";

        model.addAttribute("solicitudes",
                solicitudRepo.findByEstadoOrderByIdDesc(SolicitudStatus.ABIERTA)
        );

        return "obrero/marketplace/solicitudes_list";
    }

    @GetMapping("/solicitudes/{id}")
    public String detalleSolicitud(HttpSession session, @PathVariable Long id, Model model) {
        if (!isObrero(session)) return "redirect:/auth/login";

        Long obreroId = getUserId(session);

        Solicitud solicitud = cotizacionService.obtenerSolicitud(id);
        if (solicitud == null) return "redirect:/obrero/marketplace/solicitudes";

        Cotizacion miCotizacion = cotizacionService.buscarCotizacion(id, obreroId);

        model.addAttribute("solicitud", solicitud);
        model.addAttribute("miCotizacion", miCotizacion);
        model.addAttribute("form", new CotizacionCreateRequest());

        return "obrero/marketplace/solicitud_detail";
    }

    @PostMapping("/solicitudes/{id}/cotizar")
    public String cotizar(HttpSession session,
                          @PathVariable Long id,
                          @ModelAttribute("form") CotizacionCreateRequest form,
                          RedirectAttributes ra) {
        if (!isObrero(session)) return "redirect:/auth/login";

        Long obreroId = getUserId(session);

        try {
            cotizacionService.crearCotizacion(id, obreroId, form);
            ra.addFlashAttribute("success", "✅ Cotización enviada.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/obrero/marketplace/solicitudes/" + id;
    }

    @GetMapping("/cotizaciones")
    public String misCotizaciones(HttpSession session, Model model) {
        if (!isObrero(session)) return "redirect:/auth/login";

        Long obreroId = getUserId(session);
        model.addAttribute("cotizaciones", cotizacionRepo.findByObreroIdOrderByIdDesc(obreroId));
        return "obrero/marketplace/mis_cotizaciones";
    }

    @GetMapping("/servicios")
    public String misServicios(HttpSession session, Model model) {
        if (!isObrero(session)) return "redirect:/auth/login";

        Long obreroId = getUserId(session);
        model.addAttribute("servicios", servicioService.listarPorObrero(obreroId));
        return "obrero/marketplace/servicios_list";
    }

    @PostMapping("/servicios/{id}/iniciar")
    public String iniciarServicio(HttpSession session, @PathVariable Long id, RedirectAttributes ra) {
        if (!isObrero(session)) return "redirect:/auth/login";

        Long obreroId = getUserId(session);
        try {
            servicioService.iniciarServicio(id, obreroId);
            ra.addFlashAttribute("success", "✅ Servicio iniciado.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/obrero/marketplace/servicios";
    }

    @PostMapping("/servicios/{id}/finalizar")
    public String finalizarServicio(HttpSession session, @PathVariable Long id, RedirectAttributes ra) {
        if (!isObrero(session)) return "redirect:/auth/login";

        Long obreroId = getUserId(session);
        try {
            servicioService.finalizarServicio(id, obreroId);
            ra.addFlashAttribute("success", "✅ Servicio finalizado.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/obrero/marketplace/servicios";
    }

}


