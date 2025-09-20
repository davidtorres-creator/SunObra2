package com.example.service;

import com.example.model.usuarios;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DocumentService {

    /**
     * Generar reporte en HTML (se puede convertir a PDF desde el navegador)
     */
    public byte[] generateHtmlReport(List<usuarios> usuarios, String title) throws IOException {
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
        html.append("<p><strong>Fecha de generación:</strong> ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("</p>");
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
            html.append("<td>").append(usuario.getNombre()).append(" ").append(usuario.getApellido()).append("</td>");
            html.append("<td>").append(usuario.getEmail()).append("</td>");
            html.append("<td>").append(usuario.getUserType()).append("</td>");
            html.append("<td>").append(usuario.getEspecialidades() != null ? usuario.getEspecialidades() : "-").append("</td>");
            html.append("<td>").append(usuario.getExperiencia() != null ? usuario.getExperiencia() + " años" : "-").append("</td>");
            html.append("<td>").append(usuario.getTarifaHora() != null ? "$" + usuario.getTarifaHora() : "-").append("</td>");
            html.append("<td>").append(usuario.getTelefono() != null ? usuario.getTelefono() : "-").append("</td>");
            html.append("</tr>");
        }
        
        html.append("</table>");
        html.append("</body></html>");
        
        return html.toString().getBytes("UTF-8");
    }

    /**
     * Generar reporte en CSV (compatible con Excel)
     */
    public byte[] generateCsvReport(List<usuarios> usuarios, String title) throws IOException {
        StringBuilder csv = new StringBuilder();
        
        // Agregar información del reporte
        csv.append("# ").append(title).append("\n");
        csv.append("# Fecha de generación: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        csv.append("# Total de usuarios: ").append(usuarios.size()).append("\n");
        csv.append("\n");
        
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

    /**
     * Generar reporte en formato de texto plano (compatible con Word)
     */
    public byte[] generateTextReport(List<usuarios> usuarios, String title) throws IOException {
        StringBuilder text = new StringBuilder();
        
        text.append("=".repeat(80)).append("\n");
        text.append(" ").append(title).append("\n");
        text.append("=".repeat(80)).append("\n");
        text.append("Fecha de generación: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
        text.append("Total de usuarios: ").append(usuarios.size()).append("\n");
        text.append("=".repeat(80)).append("\n\n");
        
        // Encabezados
        text.append(String.format("%-5s %-20s %-25s %-10s %-15s %-10s %-10s %-15s\n", 
            "ID", "Nombre", "Email", "Rol", "Especialidades", "Experiencia", "Tarifa", "Teléfono"));
        text.append("-".repeat(120)).append("\n");
        
        // Datos
        for (usuarios usuario : usuarios) {
            text.append(String.format("%-5s %-20s %-25s %-10s %-15s %-10s %-10s %-15s\n",
                usuario.getId(),
                (usuario.getNombre() + " " + usuario.getApellido()).substring(0, Math.min(20, (usuario.getNombre() + " " + usuario.getApellido()).length())),
                usuario.getEmail().substring(0, Math.min(25, usuario.getEmail().length())),
                usuario.getUserType(),
                (usuario.getEspecialidades() != null ? usuario.getEspecialidades() : "-").substring(0, Math.min(15, (usuario.getEspecialidades() != null ? usuario.getEspecialidades() : "-").length())),
                usuario.getExperiencia() != null ? usuario.getExperiencia() + " años" : "-",
                usuario.getTarifaHora() != null ? "$" + usuario.getTarifaHora() : "-",
                usuario.getTelefono() != null ? usuario.getTelefono() : "-"
            ));
        }
        
        text.append("\n").append("=".repeat(80)).append("\n");
        text.append("Fin del reporte\n");
        
        return text.toString().getBytes("UTF-8");
    }
}
