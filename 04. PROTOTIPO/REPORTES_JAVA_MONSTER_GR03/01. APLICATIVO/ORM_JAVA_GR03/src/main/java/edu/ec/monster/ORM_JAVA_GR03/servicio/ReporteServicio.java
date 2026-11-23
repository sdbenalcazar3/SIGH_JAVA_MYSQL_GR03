package edu.ec.monster.ORM_JAVA_GR03.servicio;

import edu.ec.monster.ORM_JAVA_GR03.dto.CargoResumen;
import edu.ec.monster.ORM_JAVA_GR03.dto.DepartamentoResumen;
import edu.ec.monster.ORM_JAVA_GR03.dto.EmpleadoResumen;
import edu.ec.monster.ORM_JAVA_GR03.dto.reporte.FormatoReporte;
import edu.ec.monster.ORM_JAVA_GR03.dto.reporte.ReporteCargoFila;
import edu.ec.monster.ORM_JAVA_GR03.dto.reporte.ReporteDepartamentoFila;
import edu.ec.monster.ORM_JAVA_GR03.dto.reporte.ReportePersonalFila;
import edu.ec.monster.ORM_JAVA_GR03.dto.reporte.ResumenReportesDto;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.pdf.JRPdfExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Motor de reportes del SIGH (JasperReports 7.0.8): consulta los servicios existentes de
 * departamentos, cargos y personal, compila (con cache) las plantillas .jrxml y exporta a PDF o XLSX.
 */
@Service
public class ReporteServicio {

    private static final Logger LOG = LoggerFactory.getLogger(ReporteServicio.class);

    static final String PLANTILLA_DEPARTAMENTOS = "reporte_departamentos";
    static final String PLANTILLA_CARGOS = "reporte_cargos";
    static final String PLANTILLA_PERSONAL = "reporte_personal";
    static final String RECURSO_LOGO = "static/img/monster-logo.png";
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final DateTimeFormatter FORMATO_ARCHIVO = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private static final String SIN_FILTROS = "Sin filtros (todos los registros)";

    /** Resultado de una exportacion: bytes del archivo, nombre sugerido y formato. */
    public record ResultadoReporte(byte[] contenido, String nombreArchivo, FormatoReporte formato) {}

    private final DepartamentoServicio departamentoServicio;
    private final CargoServicio cargoServicio;
    private final PersonaServicio personaServicio;
    private final Map<String, JasperReport> plantillas = new ConcurrentHashMap<>();

    public ReporteServicio(DepartamentoServicio departamentoServicio, CargoServicio cargoServicio,
            PersonaServicio personaServicio) {
        this.departamentoServicio = departamentoServicio;
        this.cargoServicio = cargoServicio;
        this.personaServicio = personaServicio;
    }

    /** Convierte el parametro "formato" de la URL; un valor distinto de pdf/xlsx produce 400. */
    public FormatoReporte resolverFormato(String valor) {
        try {
            return FormatoReporte.desde(valor);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Formato no soportado. Use pdf o xlsx.");
        }
    }

    public ResumenReportesDto obtenerResumen() {
        List<DepartamentoResumen> departamentos = departamentoServicio.listarResumen();
        List<CargoResumen> cargos = cargoServicio.listarResumen(null, null, null);
        List<EmpleadoResumen> personal = personaServicio.listarResumen(null, null, null, null);
        return new ResumenReportesDto(
                departamentos.size(), departamentos.stream().filter(d -> Boolean.TRUE.equals(d.activo())).count(),
                cargos.size(), cargos.stream().filter(c -> Boolean.TRUE.equals(c.activo())).count(),
                personal.size(), personal.stream().filter(p -> Boolean.TRUE.equals(p.activo())).count());
    }

    public ResultadoReporte generarReporteDepartamentos(String q, Boolean activo, FormatoReporte formato,
            String usuarioGenera) {
        String texto = q == null ? "" : q.trim().toLowerCase(Locale.ROOT);
        List<ReporteDepartamentoFila> filas = departamentoServicio.listarResumen().stream()
                .filter(d -> activo == null || activo.equals(Boolean.TRUE.equals(d.activo())))
                .filter(d -> texto.isEmpty()
                        || contiene(d.codigoDepartamento(), texto) || contiene(d.nombre(), texto))
                .map(d -> new ReporteDepartamentoFila(d.codigoDepartamento(), d.nombre(),
                        estado(d.activo()), d.personal()))
                .toList();
        long asignados = filas.stream().mapToLong(f -> valor(f.getTotalPersonal())).sum();
        long activos = filas.stream().filter(f -> "Activo".equals(f.getEstado())).count();
        String filtros = subtitulo(activo, q, null, null);
        return exportar(PLANTILLA_DEPARTAMENTOS, "REPORTE DE DEPARTAMENTOS", filtros, filas,
                activos, asignados, formato, usuarioGenera);
    }

    public ResultadoReporte generarReporteCargos(String q, Boolean activo, String departamento,
            FormatoReporte formato, String usuarioGenera) {
        List<ReporteCargoFila> filas = cargoServicio.listarResumen(q, activo, departamento).stream()
                .map(c -> new ReporteCargoFila(c.codigoCargo(), c.nombre(), estado(c.activo()), c.personal(),
                        c.departamentos() == null || c.departamentos().isEmpty()
                                ? "Sin personal asignado" : String.join(", ", c.departamentos())))
                .toList();
        long asignados = filas.stream().mapToLong(f -> valor(f.getTotalPersonal())).sum();
        long activos = filas.stream().filter(f -> "Activo".equals(f.getEstado())).count();
        String filtros = subtitulo(activo, q, departamento == null || departamento.isBlank() ? null
                : "Departamento: " + departamento.trim(), null);
        return exportar(PLANTILLA_CARGOS, "REPORTE DE CARGOS", filtros, filas, activos, asignados,
                formato, usuarioGenera);
    }

    public ResultadoReporte generarReportePersonal(String q, Boolean activo, String departamento, String cargo,
            FormatoReporte formato, String usuarioGenera) {
        List<ReportePersonalFila> filas = personaServicio.listarResumen(q, departamento, cargo, activo).stream()
                .map(p -> new ReportePersonalFila(p.codigoPersona(), p.codigoExterno(),
                        nombreCompleto(p.apellidos(), p.nombres()), vacioA(p.numeroDocumento(), "—"),
                        p.departamento(), p.cargo(), p.funcion(), vacioA(p.correo(), "Sin registro"),
                        estado(p.activo()), p.totalAcademicos(), p.totalFamiliares()))
                .toList();
        long activos = filas.stream().filter(f -> "Activo".equals(f.getEstado())).count();
        String filtros = subtitulo(activo, q,
                departamento == null || departamento.isBlank() ? null : "Departamento: " + departamento.trim(),
                cargo == null || cargo.isBlank() ? null : "Cargo: " + cargo.trim());
        return exportar(PLANTILLA_PERSONAL, "REPORTE DE PERSONAL", filtros, filas, activos, filas.size(),
                formato, usuarioGenera);
    }

    // ---------------------------------------------------------------- motor

    private ResultadoReporte exportar(String plantilla, String titulo, String filtros, Collection<?> filas,
            long activos, long asignados, FormatoReporte formato, String usuarioGenera) {
        FormatoReporte destino = formato == null ? FormatoReporte.PDF : formato;
        LocalDateTime ahora = LocalDateTime.now();
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("TITULO_REPORTE", titulo);
        parametros.put("SUBTITULO_FILTROS", filtros);
        parametros.put("USUARIO_GENERA", usuarioGenera == null || usuarioGenera.isBlank() ? "sistema"
                : usuarioGenera);
        parametros.put("FECHA_GENERACION", FORMATO_FECHA.format(ahora));
        parametros.put("TOTAL_REGISTROS", filas.size());
        parametros.put("TOTAL_ACTIVOS", (int) activos);
        parametros.put("TOTAL_PERSONAL_ASIGNADO", asignados);
        // Un InputStream nuevo por solicitud: nunca se comparte entre peticiones.
        try (InputStream logo = abrirLogo()) {
            parametros.put("LOGO_IMAGEN", logo);
            JasperPrint impresion = JasperFillManager.fillReport(obtenerPlantilla(plantilla), parametros,
                    new JRBeanCollectionDataSource(new ArrayList<>(filas)));
            byte[] contenido = destino == FormatoReporte.PDF ? exportarPdf(impresion) : exportarXlsx(impresion);
            String nombre = plantilla + "_" + FORMATO_ARCHIVO.format(ahora) + "." + destino.getExtension();
            return new ResultadoReporte(contenido, nombre, destino);
        } catch (JRException | IOException | RuntimeException ex) {
            LOG.error("No se pudo generar el reporte {}", plantilla, ex);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "No se pudo generar el reporte. Intente nuevamente.");
        }
    }

    /** Compila el .jrxml una sola vez y reutiliza el resultado (ConcurrentHashMap, thread-safe). */
    public JasperReport obtenerPlantilla(String nombre) {
        return plantillas.computeIfAbsent(nombre, clave -> {
            try (InputStream xml = new ClassPathResource("reportes/" + clave + ".jrxml").getInputStream()) {
                return JasperCompileManager.compileReport(xml);
            } catch (JRException | IOException ex) {
                throw new IllegalStateException("No se pudo compilar la plantilla " + clave, ex);
            }
        });
    }

    public int plantillasEnCache() {
        return plantillas.size();
    }

    protected InputStream abrirLogo() {
        ClassPathResource recurso = new ClassPathResource(RECURSO_LOGO);
        if (!recurso.exists()) {
            return null;
        }
        try {
            return recurso.getInputStream();
        } catch (IOException ex) {
            LOG.warn("No se pudo abrir el logo institucional; el reporte se genera sin logo", ex);
            return null;
        }
    }

    private byte[] exportarPdf(JasperPrint impresion) throws JRException {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        JRPdfExporter exportador = new JRPdfExporter();
        exportador.setExporterInput(new SimpleExporterInput(impresion));
        exportador.setExporterOutput(new SimpleOutputStreamExporterOutput(salida));
        exportador.exportReport();
        return salida.toByteArray();
    }

    private byte[] exportarXlsx(JasperPrint impresion) throws JRException {
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        JRXlsxExporter exportador = new JRXlsxExporter();
        exportador.setExporterInput(new SimpleExporterInput(impresion));
        exportador.setExporterOutput(new SimpleOutputStreamExporterOutput(salida));
        SimpleXlsxReportConfiguration configuracion = new SimpleXlsxReportConfiguration();
        configuracion.setOnePagePerSheet(false);
        configuracion.setDetectCellType(true);
        configuracion.setWhitePageBackground(false);
        exportador.setConfiguration(configuracion);
        exportador.exportReport();
        return salida.toByteArray();
    }

    // -------------------------------------------------------------- helpers

    private static String estado(Boolean activo) {
        return Boolean.TRUE.equals(activo) ? "Activo" : "Inactivo";
    }

    private static long valor(Long numero) {
        return numero == null ? 0L : numero;
    }

    private static boolean contiene(String texto, String buscado) {
        return texto != null && texto.toLowerCase(Locale.ROOT).contains(buscado);
    }

    private static String vacioA(String texto, String alternativa) {
        return texto == null || texto.isBlank() ? alternativa : texto;
    }

    private static String nombreCompleto(String apellidos, String nombres) {
        String a = apellidos == null ? "" : apellidos.trim();
        String n = nombres == null ? "" : nombres.trim();
        if (a.isEmpty() || n.isEmpty()) {
            return a.isEmpty() ? n : a;
        }
        return a + ", " + n;
    }

    private static String subtitulo(Boolean activo, String q, String extra1, String extra2) {
        List<String> partes = new ArrayList<>();
        if (activo != null) {
            partes.add("Estado: " + (activo ? "Activos" : "Inactivos"));
        }
        if (q != null && !q.isBlank()) {
            partes.add("Búsqueda: " + q.trim());
        }
        if (extra1 != null) {
            partes.add(extra1);
        }
        if (extra2 != null) {
            partes.add(extra2);
        }
        return partes.isEmpty() ? SIN_FILTROS : String.join(" · ", partes);
    }
}
