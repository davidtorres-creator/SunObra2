package com.example.service.report;

import com.example.model.usuarios;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Implementación de {@link ReportStrategy} que genera reportes en formato HTML.
 *
 * <p>Utiliza un {@link StringBuilder} para construir un documento HTML con
 * estilos embebidos. Este formato es adecuado para visualizarse en el
 * navegador y también permite su conversión posterior a PDF.</p>
 */
public class HtmlReportStrategy implements ReportStrategy {

    @Override
    public byte[] generateReport(List<usuarios> usuarios, String title) throws IOException {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head><meta charset='UTF-8'><title>").append(title).append("</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 20px; }");
        html.append("h1 { color: #333; }");
        html.append("table { border-collapse: collapse; width: 100%; margin-top: 20px; }");
        html.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
        html.append("th { background-color: #f2f2f2; font-weight: bold; }");
        html.append("tr:nth-child(even) { background-color: #f9f9f9; }");
        html.append("</style></head><body>");
        html.append("<h1>").append(title).append("</h1>");
        html.append("<p><strong>Fecha de generación:</strong> ")
            .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
            .append("</p>");
        html.append("<p><strong>Total de usuarios:</strong> ").append(usuarios.size()).append("</p>");

        html.append("<table>");
        html.append("<tr>");
        html.append("<th>ID</th>");
        html.append("<th>Nombre</th>");
        html.append("<th>Email</th>");
        html.append("<th>Rol</th>");
        html.append("<th>Especialidades</th>");
        html.append("<th>Experiencia</th>");
        html.append("<th>Tarifa/Hora</th>");
        html.append("<th>Teléfono</th>");
        html.append("</tr>");

        for (usuarios usuario : usuarios) {
            html.append("<tr>");
            html.append("<td>").append(usuario.getId()).append("</td>");
            html.append("<td>").append(usuario.getNombre()).append(" ")
                .append(usuario.getApellido()).append("</td>");
            html.append("<td>").append(usuario.getEmail()).append("</td>");
            html.append("<td>").append(usuario.getUserType()).append("</td>");
            html.append("<td>").append(usuario.getEspecialidades() != null ? usuario.getEspecialidades() : "-").append("</td>");
            html.append("<td>")
                .append(usuario.getExperiencia() != null ? usuario.getExperiencia() + " años" : "-")
                .append("</td>");
            html.append("<td>")
                .append(usuario.getTarifaHora() != null ? "$" + usuario.getTarifaHora() : "-")
                .append("</td>");
            html.append("<td>")
                .append(usuario.getTelefono() != null ? usuario.getTelefono() : "-")
                .append("</td>");
            html.append("</tr>");
        }

        html.append("</table>");
        html.append("</body></html>");
        return html.toString().getBytes("UTF-8");
    }
}