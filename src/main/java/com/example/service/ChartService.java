package com.example.service;

import com.example.model.usuarios;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChartService {

    /* =======================
         GRÁFICO: USUARIOS POR ROL
       ======================= */
    public JFreeChart createRoleChart(List<usuarios> users) {
        // 1) Normalizar rol (trim, toLowerCase) y contar
        Map<String, Long> counts = users.stream()
                .map(u -> normalizeRole(u.getUserType()))
                .collect(Collectors.groupingBy(r -> r, Collectors.counting()));

        // 2) Orden deseado: Cliente, Obrero, Admin, Otros
        String[] order = {"cliente", "obrero", "admin", "otros"};
        Map<String, Long> sorted = new LinkedHashMap<>();
        for (String key : order) {
            if (counts.containsKey(key)) {
                sorted.put(key, counts.get(key));
            }
        }
        // cualquier otro rol no contemplado
        counts.entrySet().stream()
                .filter(e -> !sorted.containsKey(e.getKey()))
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .forEach(e -> sorted.put(e.getKey(), e.getValue()));

        // 3) Dataset con etiquetas bonitas
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        sorted.forEach((role, count) -> dataset.addValue(count, "Usuarios", prettyRole(role)));

        // 4) Crear gráfico
        JFreeChart chart = ChartFactory.createBarChart(
                "Usuarios por Rol",
                "Rol",
                "Cantidad",
                dataset,
                PlotOrientation.VERTICAL,
                false,  // sin leyenda (una sola serie)
                true,
                false
        );

        // 5) Estilo: fondo blanco, grilla suave, eje Y en enteros, etiquetas visibles
        applyDefaultBarStyle(chart);

        return chart;
    }

    /* =======================
         GRÁFICO: SOLICITUDES POR ESTADO
       ======================= */
    /**
     * Genera un gráfico de barras con el número de solicitudes por estado.
     *
     * @param abiertas   número de solicitudes ABIERTA
     * @param cerradas   número de solicitudes CERRADA
     * @param canceladas número de solicitudes CANCELADA
     */
    public JFreeChart createSolicitudStatusChart(long abiertas, long cerradas, long canceladas) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(abiertas, "Solicitudes", "Abiertas");
        dataset.addValue(cerradas, "Solicitudes", "Cerradas");
        dataset.addValue(canceladas, "Solicitudes", "Canceladas");

        JFreeChart chart = ChartFactory.createBarChart(
                "Solicitudes por Estado",
                "Estado",
                "Cantidad",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        // Aplica el mismo estilo que el gráfico de roles
        applyDefaultBarStyle(chart);

        return chart;
    }

    /* =======================
         GRÁFICO: SERVICIOS POR ESTADO
       ======================= */
    /**
     * Genera un gráfico de barras con el número de servicios por estado.
     *
     * @param programados número de servicios PROGRAMADO
     * @param enProceso   número de servicios EN_PROCESO
     * @param finalizados número de servicios FINALIZADO
     * @param cancelados  número de servicios CANCELADO
     */
    public JFreeChart createServicioStatusChart(long programados, long enProceso,
                                                long finalizados, long cancelados) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(programados, "Servicios", "Programados");
        dataset.addValue(enProceso, "Servicios", "En proceso");
        dataset.addValue(finalizados, "Servicios", "Finalizados");
        dataset.addValue(cancelados, "Servicios", "Cancelados");

        JFreeChart chart = ChartFactory.createBarChart(
                "Servicios por Estado",
                "Estado",
                "Cantidad",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        );

        // Aplica el mismo estilo que el gráfico de roles
        applyDefaultBarStyle(chart);

        return chart;
    }

    // =======================
    // ESTILO REUTILIZABLE
    // =======================
    /**
     * Aplica el mismo estilo visual usado en el gráfico de roles:
     * fondo blanco, grilla suave, etiquetas visibles, eje Y entero y color corporativo.
     */
    private void applyDefaultBarStyle(JFreeChart chart) {
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlineVisible(false);
        plot.setRangeGridlinePaint(new Color(230, 230, 230));

        NumberAxis y = (NumberAxis) plot.getRangeAxis();
        y.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        y.setAutoRangeIncludesZero(true);
        y.setUpperMargin(0.15); // espacio arriba para etiquetas

        BarRenderer r = (BarRenderer) plot.getRenderer();
        r.setDefaultItemLabelsVisible(true);
        r.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator("{2}", new DecimalFormat("0")));
        r.setDefaultItemLabelFont(new Font("SansSerif", Font.BOLD, 12));
        r.setBarPainter(new StandardBarPainter());
        r.setShadowVisible(false);
        // Color corporativo #eac72c
        r.setSeriesPaint(0, new Color(0xEA, 0xC7, 0x2C));
    }

    private String normalizeRole(String raw) {
        if (raw == null) return "otros";
        String s = raw.trim().toLowerCase(Locale.ROOT);
        if (s.isEmpty()) return "otros";
        // mapeos típicos
        if (s.equals("cliente") || s.equals("client")) return "cliente";
        if (s.equals("obrero") || s.equals("worker")) return "obrero";
        if (s.equals("admin") || s.equals("administrator")) return "admin";
        return s;
    }

    private String prettyRole(String norm) {
        switch (norm) {
            case "cliente": return "Cliente";
            case "obrero":  return "Obrero";
            case "admin":   return "Admin";
            default:        return capitalize(norm);
        }
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + (s.length() > 1 ? s.substring(1) : "");
    }

    /* =======================
         PNG
       ======================= */
    public byte[] toPng(JFreeChart chart, int width, int height) throws Exception {
        // Alta calidad
        BufferedImage img = chart.createBufferedImage(width, height, BufferedImage.TYPE_INT_ARGB, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        return baos.toByteArray();
    }

    /* =======================
         PDF
       ======================= */
    public void writeChartToPdf(JFreeChart chart, OutputStream out) throws Exception {
        BufferedImage image = chart.createBufferedImage(900, 450, BufferedImage.TYPE_INT_ARGB, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        ImageData data = ImageDataFactory.create(baos.toByteArray());

        PdfWriter writer = new PdfWriter(out);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);
        doc.add(new Image(data));
        doc.close();
    }
}