package com.example.service.report;

import com.example.model.usuarios;
import java.io.IOException;
import java.util.List;

/**
 * Estrategia para generación de reportes.
 *
 * <p>Define un método que todas las estrategias concretas deben implementar.
 * Cada estrategia se encarga de generar un reporte en un formato particular
 * (por ejemplo, HTML, CSV o texto plano). Al encapsular esta lógica en
 * implementaciones separadas, se facilita la extensión de la aplicación
 * con nuevos formatos sin modificar el código existente.</p>
 */
public interface ReportStrategy {

    /**
     * Genera un reporte para la lista de usuarios recibida.
     *
     * @param usuarios lista de usuarios a incluir en el reporte
     * @param title    título del reporte
     * @return un arreglo de bytes que representa el contenido del reporte
     * @throws IOException en caso de error al generar el contenido
     */
    byte[] generateReport(List<usuarios> usuarios, String title) throws IOException;
}