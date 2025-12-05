package com.example.api.rest;

import com.example.model.usuarios;
import com.example.service.ChartService;
import com.example.service.UsuarioService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API REST para obtener gráficos (PNG/PDF).
 */
@RestController
@RequestMapping("/api/v1/charts")
public class ChartRestController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ChartService chartService;

    // Devuelve un PNG con usuarios por rol
    @GetMapping(value = "/users/by-role", produces = MediaType.IMAGE_PNG_VALUE)
    public @ResponseBody byte[] usersByRolePng() throws Exception {
        List<usuarios> users = usuarioService.listarUsuarios();
        return chartService.toPng(chartService.createRoleChart(users), 900, 450);
    }

    // Devuelve un PDF con el mismo gráfico
    @GetMapping(value = "/users/by-role/pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    public void usersByRolePdf(HttpServletResponse response) throws Exception {
        List<usuarios> users = usuarioService.listarUsuarios();
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        chartService.writeChartToPdf(
                chartService.createRoleChart(users),
                response.getOutputStream());
    }
}
