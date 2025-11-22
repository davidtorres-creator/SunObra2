package com.example.service;

import com.example.model.usuarios;
import com.example.service.report.ReportStrategy;
import com.example.service.report.ReportStrategyFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * Servicio de alto nivel para la generación de reportes.
 *
 * <p>Recibe la lista de usuarios, el título y el formato deseado,
 * selecciona la estrategia adecuada a través de {@link ReportStrategyFactory}
 * y delega la generación a la estrategia. Mantener esta lógica en un servicio
 * facilita su uso desde controladores o desde otras capas de la aplicación.</p>
 */
@Service
public class ReportService {

    /**
     * Genera un reporte según el formato indicado.
     *
     * @param usuarios lista de usuarios a incluir en el reporte
     * @param title    título del reporte
     * @param format   identificador del formato (ej. "html", "csv", "txt")
     * @return un arreglo de bytes con el contenido del reporte
     * @throws IOException en caso de error al generar el contenido
     * @throws IllegalArgumentException si el formato no es válido
     */
    public byte[] generateReport(List<usuarios> usuarios, String title, String format) throws IOException {
        ReportStrategy strategy = ReportStrategyFactory.getStrategy(format);
        return strategy.generateReport(usuarios, title);
    }
}