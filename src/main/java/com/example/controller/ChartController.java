package com.example.controller;

import com.example.model.usuarios;
import com.example.service.ChartService;
import com.example.service.UsuarioService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * Controlador para la generación y visualización de gráficos en el panel de administración.
 */
@Controller
@RequestMapping("/admin/charts")
public class ChartController {

    @Autowired
    private ChartService chartService;

    @Autowired
    private UsuarioService usuarioService;

    /** Verifica rol del usuario */
    private boolean hasRole(HttpSession session, String role) {
        Object r = session.getAttribute("user_role");
        return r != null && Objects.equals(r.toString(), role);
    }

    // ============================================================
    //            VISTA PRINCIPAL DE GRÁFICOS (HTML)
    // ============================================================
    @GetMapping("")
    public String chartsView(HttpSession session, Model model) {
        if (!hasRole(session, "admin")) {
            return "redirect:/auth/login";
        }

        model.addAttribute("title", "Gráficos del Sistema");
        return "admin/charts"; // correspondencia con charts.html
    }

    // ============================================================
    //         GRÁFICO PNG: USUARIOS POR ROL
    // ============================================================
    @GetMapping(value = "/roles", produces = MediaType.IMAGE_PNG_VALUE)
    @ResponseBody
    public byte[] rolesChart(HttpSession session) throws Exception {
        if (!hasRole(session, "admin")) {
            return new byte[0];
        }

        List<usuarios> usuarios = usuarioService.listarUsuarios();
        return chartService.toPng(
                chartService.createRoleChart(usuarios),
                900, 450
        );
    }

    // ============================================================
    //         GRÁFICO PDF: USUARIOS POR ROL
    // ============================================================
    @GetMapping(value = "/roles/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public void rolesChartPdf(HttpSession session, HttpServletResponse response) throws Exception {
        if (!hasRole(session, "admin")) {
            response.sendRedirect("/auth/login");
            return;
        }

        List<usuarios> usuarios = usuarioService.listarUsuarios();

        response.setContentType("application/pdf");
        chartService.writeChartToPdf(
                chartService.createRoleChart(usuarios),
                response.getOutputStream()
        );
    }

    // ============================================================
    //         Alias opcional para redirigir desde /admin/reports
    // ============================================================
    @GetMapping("/go")
    public String goToCharts(HttpSession session) {
        if (!hasRole(session, "admin")) {
            return "redirect:/auth/login";
        }
        return "redirect:/admin/charts";
    }
}
