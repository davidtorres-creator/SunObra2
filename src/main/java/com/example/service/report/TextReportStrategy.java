package com.example.service.report;

import com.example.model.usuarios;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Implementación de {@link ReportStrategy} que genera reportes en formato de
 * texto plano.
 *
 * <p>Este generador utiliza {@link StringBuilder} junto con format
 * strings para alinear las columnas. Es apropiado para imprimirse o pegarse
 * en documentos de texto y soporta lectores como Microsoft Word.</p>
 */
public class TextReportStrategy implements ReportStrategy {

    @Override
    public byte[] generateReport(List<usuarios> usuarios, String title) throws IOException {
        StringBuilder text = new StringBuilder();
        text.append("=".repeat(80)).append("\n");
        text.append(" ").append(title).append("\n");
        text.append("=".repeat(80)).append("\n");
        text.append("Fecha de generación: ")
            .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
            .append("\n");
        text.append("Total de usuarios: ").append(usuarios.size()).append("\n");
        text.append("=".repeat(80)).append("\n\n");

        // Encabezados
        text.append(String.format("%-5s %-20s %-25s %-10s %-15s %-10s %-10s %-15s\n",
            "ID", "Nombre", "Email", "Rol", "Especialidades", "Experiencia", "Tarifa", "Teléfono"));
        text.append("-".repeat(120)).append("\n");

        // Datos
        for (usuarios usuario : usuarios) {
            String nombreCompleto = usuario.getNombre() + " " + usuario.getApellido();
            String especialidad = usuario.getEspecialidades() != null ? usuario.getEspecialidades() : "-";
            String experiencia = usuario.getExperiencia() != null ? usuario.getExperiencia() + " años" : "-";
            String tarifa = usuario.getTarifaHora() != null ? "$" + usuario.getTarifaHora() : "-";
            String telefono = usuario.getTelefono() != null ? usuario.getTelefono() : "-";
            // Asegurarse de que las columnas no excedan su ancho
            String nombreCol = nombreCompleto.length() > 20 ? nombreCompleto.substring(0, 20) : nombreCompleto;
            String emailCol = usuario.getEmail().length() > 25 ? usuario.getEmail().substring(0, 25) : usuario.getEmail();
            String espCol = especialidad.length() > 15 ? especialidad.substring(0, 15) : especialidad;
            text.append(String.format("%-5s %-20s %-25s %-10s %-15s %-10s %-10s %-15s\n",
                usuario.getId(),
                nombreCol,
                emailCol,
                usuario.getUserType(),
                espCol,
                experiencia,
                tarifa,
                telefono));
        }
        text.append("\n").append("=".repeat(80)).append("\n");
        text.append("Fin del reporte\n");
        return text.toString().getBytes("UTF-8");
    }
}
