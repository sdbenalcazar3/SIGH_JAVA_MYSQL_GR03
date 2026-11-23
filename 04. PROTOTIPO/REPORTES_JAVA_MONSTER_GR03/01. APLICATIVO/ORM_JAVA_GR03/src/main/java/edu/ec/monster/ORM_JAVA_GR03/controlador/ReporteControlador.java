package edu.ec.monster.ORM_JAVA_GR03.controlador;

import edu.ec.monster.ORM_JAVA_GR03.dto.reporte.FormatoReporte;
import edu.ec.monster.ORM_JAVA_GR03.dto.reporte.ResumenReportesDto;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Persona;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Usuario;
import edu.ec.monster.ORM_JAVA_GR03.seguridad.SesionInterceptor;
import edu.ec.monster.ORM_JAVA_GR03.servicio.LoginServicio;
import edu.ec.monster.ORM_JAVA_GR03.servicio.ReporteServicio;
import edu.ec.monster.ORM_JAVA_GR03.servicio.ReporteServicio.ResultadoReporte;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * API REST de reportes. La sesion y la opcion REP las valida {@code SesionInterceptor}.
 *
 *   GET /api/reportes/resumen        KPIs en JSON
 *   GET /api/reportes/departamentos  PDF (inline/attachment) o XLSX (attachment)
 *   GET /api/reportes/cargos         PDF o XLSX
 *   GET /api/reportes/personal       PDF o XLSX
 */
@RestController
@RequestMapping("/api/reportes")
public class ReporteControlador {

    private final ReporteServicio servicio;
    private final LoginServicio loginServicio;

    public ReporteControlador(ReporteServicio servicio, LoginServicio loginServicio) {
        this.servicio = servicio;
        this.loginServicio = loginServicio;
    }

    @GetMapping("/resumen")
    public ResumenReportesDto resumen() {
        return servicio.obtenerResumen();
    }

    @GetMapping("/departamentos")
    public ResponseEntity<byte[]> departamentos(@RequestParam(required = false) String formato,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(defaultValue = "false") boolean descarga,
            HttpSession session) {
        FormatoReporte tipo = servicio.resolverFormato(formato);
        return responder(servicio.generarReporteDepartamentos(q, activo, tipo, usuarioGenera(session)), descarga);
    }

    @GetMapping("/cargos")
    public ResponseEntity<byte[]> cargos(@RequestParam(required = false) String formato,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(required = false) String departamento,
            @RequestParam(defaultValue = "false") boolean descarga,
            HttpSession session) {
        FormatoReporte tipo = servicio.resolverFormato(formato);
        return responder(servicio.generarReporteCargos(q, activo, departamento, tipo, usuarioGenera(session)),
                descarga);
    }

    @GetMapping("/personal")
    public ResponseEntity<byte[]> personal(@RequestParam(required = false) String formato,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(required = false) String departamento,
            @RequestParam(required = false) String cargo,
            @RequestParam(defaultValue = "false") boolean descarga,
            HttpSession session) {
        FormatoReporte tipo = servicio.resolverFormato(formato);
        return responder(servicio.generarReportePersonal(q, activo, departamento, cargo, tipo,
                usuarioGenera(session)), descarga);
    }

    /** PDF inline solo cuando no se pide descarga; XLSX siempre como adjunto. */
    private ResponseEntity<byte[]> responder(ResultadoReporte resultado, boolean descarga) {
        boolean adjunto = descarga || resultado.formato() != FormatoReporte.PDF;
        ContentDisposition disposicion = (adjunto ? ContentDisposition.attachment() : ContentDisposition.inline())
                .filename(resultado.nombreArchivo()).build();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resultado.formato().getMediaType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposicion.toString())
                .header("X-Content-Type-Options", "nosniff")
                .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate")
                .contentLength(resultado.contenido().length)
                .body(resultado.contenido());
    }

    /** "usuario - Nombres Apellidos" del usuario autenticado en la sesion. */
    private String usuarioGenera(HttpSession session) {
        String codigo = session == null ? null : (String) session.getAttribute(SesionInterceptor.ATRIBUTO_USUARIO);
        if (codigo == null) {
            return "sistema";
        }
        Usuario usuario = loginServicio.buscarPorCodigo(codigo).orElse(null);
        if (usuario == null) {
            return "sistema";
        }
        Persona persona = usuario.getPersona();
        String nombre = persona == null ? "" : ((persona.getNombres() == null ? "" : persona.getNombres()) + " "
                + (persona.getApellidos() == null ? "" : persona.getApellidos())).trim();
        return nombre.isEmpty() ? usuario.getUsuario() : usuario.getUsuario() + " - " + nombre;
    }
}
