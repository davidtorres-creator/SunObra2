package com.example.service.report;

import com.example.model.usuarios;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Implementación de {@link ReportStrategy} que genera reportes en formato CSV.
 *
 * <p>El formato CSV es compatible con hojas de cálculo como Excel y se basa
 * en comas para separar los campos. Este generador escribe un bloque de
 * encabezados seguido de cada registro en una línea separada.</p>
 */
public class CsvReportStrategy implements ReportStrategy {

    @Override
    public byte[] generateReport(List<usuarios> usuarios, String title) throws IOException {
        StringBuilder csv = new StringBuilder();
        // Agregar información del reporte como comentarios
        csv.append("# ").append(title).append("\n");
        csv.append("# Fecha de generación: ")
            .append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")))
            .append("\n");
        csv.append("# Total de usuarios: ").append(usuarios.size()).append("\n\n");

        // Encabezados
        csv.append("ID,Nombre,Apellido,Email,Rol,Especialidades,Experiencia,Tarifa/Hora,Teléfono,Dirección\n");

        // Datos
        for (usuarios usuario : usuarios) {
            csv.append(usuario.getId()).append(",");
            csv.append("\"").append(usuario.getNombre()).append("\",");
            csv.append("\"").append(usuario.getApellido()).append("\",");
            csv.append("\"").append(usuario.getEmail()).append("\",");
            csv.append("\"").append(usuario.getUserType()).append("\",");
            csv.append("\"").append(usuario.getEspecialidades() != null ? usuario.getEspecialidades() : "").append("\",");
            csv.append(usuario.getExperiencia() != null ? usuario.getExperiencia() : "").append(",");
            csv.append(usuario.getTarifaHora() != null ? usuario.getTarifaHora() : "").append(",");
            csv.append("\"").append(usuario.getTelefono() != null ? usuario.getTelefono() : "").append("\",");
            csv.append("\"").append(usuario.getDireccion() != null ? usuario.getDireccion() : "").append("\"");
            csv.append("\n");
        }
        return csv.toString().getBytes("UTF-8");
    }
}
