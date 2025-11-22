package com.example.service.report;

/**
 * Fábrica simple para obtener instancias de {@link ReportStrategy} según el
 * formato deseado.
 *
 * <p>Centraliza la lógica de selección de estrategias y evita la dispersión
 * de condicionales por todo el código. Si se requieren nuevos formatos,
 * basta con añadir un nuevo caso y su clase correspondiente.</p>
 */
public class ReportStrategyFactory {

    /**
     * Devuelve la estrategia adecuada según el identificador de formato.
     *
     * @param format nombre del formato (por ejemplo: "html", "csv", "txt")
     * @return estrategia para generar el reporte
     * @throws IllegalArgumentException si el formato no está soportado
     */
    public static ReportStrategy getStrategy(String format) {
        if (format == null) {
            throw new IllegalArgumentException("El formato no puede ser nulo");
        }
        switch (format.toLowerCase()) {
            case "html":
                return new HtmlReportStrategy();
            case "csv":
                return new CsvReportStrategy();
            case "txt":
            case "text":
                return new TextReportStrategy();
            default:
                throw new IllegalArgumentException("Formato no soportado: " + format);
        }
    }
}