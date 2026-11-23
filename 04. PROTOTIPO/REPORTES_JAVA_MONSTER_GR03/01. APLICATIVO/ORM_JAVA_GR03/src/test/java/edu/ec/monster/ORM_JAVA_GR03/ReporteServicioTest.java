package edu.ec.monster.ORM_JAVA_GR03;

import edu.ec.monster.ORM_JAVA_GR03.controlador.ErroresControlador;
import edu.ec.monster.ORM_JAVA_GR03.controlador.ReporteControlador;
import edu.ec.monster.ORM_JAVA_GR03.controlador.ReporteVistaControlador;
import edu.ec.monster.ORM_JAVA_GR03.dto.CargoResumen;
import edu.ec.monster.ORM_JAVA_GR03.dto.DepartamentoResumen;
import edu.ec.monster.ORM_JAVA_GR03.dto.EmpleadoResumen;
import edu.ec.monster.ORM_JAVA_GR03.dto.reporte.FormatoReporte;
import edu.ec.monster.ORM_JAVA_GR03.dto.reporte.ResumenReportesDto;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Cargo;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Departamento;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Persona;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Usuario;
import edu.ec.monster.ORM_JAVA_GR03.seguridad.SesionInterceptor;
import edu.ec.monster.ORM_JAVA_GR03.servicio.CargoServicio;
import edu.ec.monster.ORM_JAVA_GR03.servicio.DepartamentoServicio;
import edu.ec.monster.ORM_JAVA_GR03.servicio.LoginServicio;
import edu.ec.monster.ORM_JAVA_GR03.servicio.PersonaServicio;
import edu.ec.monster.ORM_JAVA_GR03.servicio.ReporteServicio;
import edu.ec.monster.ORM_JAVA_GR03.servicio.ReporteServicio.ResultadoReporte;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Pruebas del modulo de reportes: Mockito para las fuentes de datos y compilacion/exportacion
 * reales con JasperReports 7.0.8 (sin base de datos).
 */
@ExtendWith(MockitoExtension.class)
class ReporteServicioTest {

    private static final String XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    @Mock
    private DepartamentoServicio departamentoServicio;
    @Mock
    private CargoServicio cargoServicio;
    @Mock
    private PersonaServicio personaServicio;
    @Mock
    private LoginServicio loginServicio;

    private ReporteServicio servicio;

    @BeforeEach
    void preparar() {
        servicio = new ReporteServicio(departamentoServicio, cargoServicio, personaServicio);
        lenient().when(departamentoServicio.listarResumen()).thenReturn(List.of(
                new DepartamentoResumen("D01", "Administración", true, 2L),
                new DepartamentoResumen("D02", "Emergencia", true, 3L),
                new DepartamentoResumen("D03", "Pediatría Ñandú", false, 0L)));
        lenient().when(cargoServicio.listarResumen(any(), any(), any())).thenReturn(List.of(
                new CargoResumen("C01", "Analista de sistemas", true, 2L, List.of("D01 - Administración")),
                new CargoResumen("C02", "Médico Ñ", true, 3L, List.of("D02 - Emergencia", "D01 - Administración")),
                new CargoResumen("C03", "Auxiliar", false, 0L, List.of())));
        lenient().when(personaServicio.listarResumen(any(), any(), any(), any())).thenReturn(List.of(
                new EmpleadoResumen("P00001", "EXT-1", "Pérez Núñez", "José Ángel", "Médico de guardia",
                        "1712345678", "jose@sigh.local", null, "D02", "Emergencia", "C02", "Médico Ñ", true, 2L, 1L),
                new EmpleadoResumen("P00002", null, "Álvarez", "Óscar", null, null, null, null,
                        "D01", "Administración", "C01", "Analista de sistemas", false, 0L, 0L)));
    }

    // ------------------------------------------------------------ exportacion real

    private ResultadoReporte generar(String reporte, FormatoReporte formato) {
        return switch (reporte) {
            case "departamentos" -> servicio.generarReporteDepartamentos(null, null, formato, "demo.sigh - Ana");
            case "cargos" -> servicio.generarReporteCargos(null, null, null, formato, "demo.sigh - Ana");
            default -> servicio.generarReportePersonal(null, null, null, null, formato, "demo.sigh - Ana");
        };
    }

    @Test
    void losTresReportesSeExportanAPdfConFirmaValida() {
        for (String reporte : List.of("departamentos", "cargos", "personal")) {
            ResultadoReporte resultado = generar(reporte, FormatoReporte.PDF);
            assertTrue(resultado.contenido().length > 500, reporte);
            assertEquals("%PDF-", new String(resultado.contenido(), 0, 5, StandardCharsets.ISO_8859_1), reporte);
            assertEquals(FormatoReporte.PDF, resultado.formato());
            assertTrue(resultado.nombreArchivo().matches("reporte_" + reporte + "_\\d{8}_\\d{6}\\.pdf"),
                    resultado.nombreArchivo());
        }
    }

    @Test
    void losTresReportesSeExportanAXlsxConFirmaZip() {
        for (String reporte : List.of("departamentos", "cargos", "personal")) {
            ResultadoReporte resultado = generar(reporte, FormatoReporte.XLSX);
            assertEquals("PK", new String(resultado.contenido(), 0, 2, StandardCharsets.ISO_8859_1), reporte);
            assertEquals(FormatoReporte.XLSX, resultado.formato());
            assertTrue(resultado.nombreArchivo().matches("reporte_" + reporte + "_\\d{8}_\\d{6}\\.xlsx"),
                    resultado.nombreArchivo());
        }
    }

    @Test
    void logoNuloNoImpideGenerarLosTresReportes() {
        ReporteServicio sinLogo = new ReporteServicio(departamentoServicio, cargoServicio, personaServicio) {
            @Override
            protected java.io.InputStream abrirLogo() {
                return null;
            }
        };
        for (FormatoReporte formato : FormatoReporte.values()) {
            String firma = formato == FormatoReporte.PDF ? "%PDF-" : "PK";
            for (ResultadoReporte r : List.of(
                    sinLogo.generarReporteDepartamentos(null, null, formato, "u"),
                    sinLogo.generarReporteCargos(null, null, null, formato, "u"),
                    sinLogo.generarReportePersonal(null, null, null, null, formato, "u"))) {
                assertEquals(firma, new String(r.contenido(), 0, firma.length(), StandardCharsets.ISO_8859_1));
            }
        }
    }

    @Test
    void formatoNuloSeTrataComoPdf() {
        ResultadoReporte resultado = servicio.generarReporteDepartamentos(null, null, null, null);
        assertEquals(FormatoReporte.PDF, resultado.formato());
        assertEquals("%PDF-", new String(resultado.contenido(), 0, 5, StandardCharsets.ISO_8859_1));
    }

    @Test
    void xlsxConservaTildesYSustituyeCamposNulos() throws Exception {
        String texto = textoXlsx(generar("personal", FormatoReporte.XLSX).contenido());
        assertTrue(texto.contains("Pérez Núñez, José Ángel"), texto);
        assertTrue(texto.contains("Álvarez, Óscar"), texto);
        assertTrue(texto.contains("Administración"), texto);
        assertTrue(texto.contains("Sin registro"), texto);
        assertTrue(texto.contains("—"), texto);

        String cargos = textoXlsx(generar("cargos", FormatoReporte.XLSX).contenido());
        assertTrue(cargos.contains("Médico Ñ"), cargos);
        assertTrue(cargos.contains("D02 - Emergencia, D01 - Administración"), cargos);
        assertTrue(cargos.contains("Sin personal asignado"), cargos);

        String departamentos = textoXlsx(generar("departamentos", FormatoReporte.XLSX).contenido());
        assertTrue(departamentos.contains("Pediatría Ñandú"), departamentos);
        assertTrue(departamentos.contains("Inactivo"), departamentos);
    }

    @Test
    void departamentosFiltraPorTextoSinDistinguirMayusculasYPorEstado() throws Exception {
        String texto = textoXlsx(servicio.generarReporteDepartamentos("EMERG", true, FormatoReporte.XLSX,
                "demo.sigh").contenido());
        assertTrue(texto.contains("Emergencia"), texto);
        assertTrue(!texto.contains("Administración"), texto);
        assertTrue(!texto.contains("Pediatría"), texto);
        assertTrue(texto.contains("Estado: Activos"), texto);
        assertTrue(texto.contains("Búsqueda: EMERG"), texto);

        String porCodigo = textoXlsx(servicio.generarReporteDepartamentos("d03", false, FormatoReporte.XLSX,
                "demo.sigh").contenido());
        assertTrue(porCodigo.contains("Pediatría Ñandú"), porCodigo);
    }

    @Test
    void cargosYPersonalDelegaFiltrosEnLosServiciosExistentes() {
        servicio.generarReporteCargos("a%_!", true, "SIN_PERSONAL", FormatoReporte.PDF, "u");
        verify(cargoServicio).listarResumen("a%_!", true, "SIN_PERSONAL");

        servicio.generarReportePersonal("x", false, "D02", "C02", FormatoReporte.PDF, "u");
        verify(personaServicio).listarResumen("x", "D02", "C02", false);
    }

    @Test
    void sinRegistrosGeneraDocumentosValidosEnLosTresReportes() throws Exception {
        when(departamentoServicio.listarResumen()).thenReturn(List.of());
        when(cargoServicio.listarResumen(any(), any(), any())).thenReturn(List.of());
        when(personaServicio.listarResumen(any(), any(), any(), any())).thenReturn(List.of());

        for (String reporte : List.of("departamentos", "cargos", "personal")) {
            ResultadoReporte xlsx = generar(reporte, FormatoReporte.XLSX);
            assertEquals("PK", new String(xlsx.contenido(), 0, 2, StandardCharsets.ISO_8859_1), reporte);
            ResultadoReporte pdfReporte = generar(reporte, FormatoReporte.PDF);
            assertEquals("%PDF-", new String(pdfReporte.contenido(), 0, 5, StandardCharsets.ISO_8859_1), reporte);
        }
        String vacio = textoXlsx(generar("cargos", FormatoReporte.XLSX).contenido());
        assertTrue(vacio.contains("Sin filtros (todos los registros)"), vacio);
        assertTrue(vacio.contains("No se encontraron cargos"), vacio);
    }

    @Test
    void comodinesLikeSeTratanComoTextoLiteralEnDepartamentos() {
        ResultadoReporte resultado = servicio.generarReporteDepartamentos("%_!", null, FormatoReporte.XLSX, "u");
        assertEquals("PK", new String(resultado.contenido(), 0, 2, StandardCharsets.ISO_8859_1));
    }

    @Test
    void formatoInvalidoSeRechazaConEstado400() {
        for (String invalido : List.of("docx", "exe", "pdf;rm")) {
            ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                    () -> servicio.resolverFormato(invalido));
            assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        }
        assertEquals(FormatoReporte.PDF, servicio.resolverFormato(null));
        assertEquals(FormatoReporte.XLSX, servicio.resolverFormato("XLSX"));
    }

    @Test
    void obtenerResumenCalculaTotalesYActivos() {
        ResumenReportesDto resumen = servicio.obtenerResumen();
        assertEquals(new ResumenReportesDto(3, 2, 3, 2, 2, 1), resumen);
    }

    // ------------------------------------------------------------ cache y concurrencia

    @Test
    void plantillasSeCompilanUnaSolaVezYSeReutilizan() {
        assertSame(servicio.obtenerPlantilla("reporte_cargos"), servicio.obtenerPlantilla("reporte_cargos"));
        servicio.obtenerPlantilla("reporte_departamentos");
        servicio.obtenerPlantilla("reporte_personal");
        assertEquals(3, servicio.plantillasEnCache());
    }

    @Test
    void generacionConcurrenteEsSegura() throws Exception {
        ExecutorService pool = Executors.newFixedThreadPool(6);
        try {
            List<Callable<ResultadoReporte>> tareas = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                String reporte = List.of("departamentos", "cargos", "personal").get(i % 3);
                FormatoReporte formato = i % 2 == 0 ? FormatoReporte.PDF : FormatoReporte.XLSX;
                tareas.add(() -> generar(reporte, formato));
            }
            for (Future<ResultadoReporte> futuro : pool.invokeAll(tareas)) {
                ResultadoReporte resultado = futuro.get();
                String firma = new String(resultado.contenido(), 0, 2, StandardCharsets.ISO_8859_1);
                assertEquals(resultado.formato() == FormatoReporte.PDF ? "%P" : "PK", firma);
            }
            assertEquals(3, servicio.plantillasEnCache());
        } finally {
            pool.shutdownNow();
        }
    }

    // ------------------------------------------------------------ controladores (MockMvc)

    private MockMvc mockMvcApi() {
        return MockMvcBuilders.standaloneSetup(new ReporteControlador(servicio, loginServicio))
                .setControllerAdvice(new ErroresControlador())
                .build();
    }

    private Usuario usuarioDemo() {
        Persona persona = new Persona();
        persona.setCodigoPersona("P00001");
        persona.setNombres("Ana");
        persona.setApellidos("Ejemplo");
        Usuario usuario = new Usuario();
        usuario.setCodigoUsuario("U00001");
        usuario.setUsuario("demo.sigh");
        usuario.setPersona(persona);
        return usuario;
    }

    @Test
    void apiPdfInlineYDescargaConCabecerasSeguras() throws Exception {
        MockMvc mvc = mockMvcApi();
        MockHttpSession sesion = new MockHttpSession();
        sesion.setAttribute(SesionInterceptor.ATRIBUTO_USUARIO, "U00001");
        when(loginServicio.buscarPorCodigo("U00001")).thenReturn(Optional.of(usuarioDemo()));

        MvcResult inline = mvc.perform(get("/api/reportes/departamentos").session(sesion))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("Cache-Control", "no-store, no-cache, must-revalidate"))
                .andReturn();
        assertTrue(inline.getResponse().getHeader("Content-Disposition").startsWith("inline; filename=\"reporte_departamentos_"));
        assertEquals("%PDF-", new String(inline.getResponse().getContentAsByteArray(), 0, 5,
                StandardCharsets.ISO_8859_1));

        mvc.perform(get("/api/reportes/cargos").param("descarga", "true").session(sesion))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "application/pdf"))
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.startsWith("attachment; filename=\"reporte_cargos_")));

        MvcResult xlsx = mvc.perform(get("/api/reportes/personal").param("formato", "xlsx").session(sesion))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", XLSX))
                .andExpect(header().string("Content-Disposition",
                        org.hamcrest.Matchers.startsWith("attachment; filename=\"reporte_personal_")))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andReturn();
        assertEquals("PK", new String(xlsx.getResponse().getContentAsByteArray(), 0, 2,
                StandardCharsets.ISO_8859_1));
    }

    @Test
    void apiFormatoInvalidoDevuelve400JsonSinStacktrace() throws Exception {
        mockMvcApi().perform(get("/api/reportes/departamentos").param("formato", "docx"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Formato no soportado. Use pdf o xlsx."));
        mockMvcApi().perform(get("/api/reportes/personal").param("formato", "exe"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").exists());
        verifyNoInteractions(personaServicio);
    }

    @Test
    void apiResumenDevuelveKpisJson() throws Exception {
        mockMvcApi().perform(get("/api/reportes/resumen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDepartamentos").value(3))
                .andExpect(jsonPath("$.departamentosActivos").value(2))
                .andExpect(jsonPath("$.totalCargos").value(3))
                .andExpect(jsonPath("$.cargosActivos").value(2))
                .andExpect(jsonPath("$.totalPersonal").value(2))
                .andExpect(jsonPath("$.personalActivo").value(1));
    }

    @Test
    void reporteVistaControladorSinSesionRedirigeALoginExpirada(@Mock CargoServicio cargos,
            @Mock DepartamentoServicio departamentos) throws Exception {
        MockHttpSession sesion = new MockHttpSession();
        vistaMvc(departamentos, cargos).perform(get("/reportes").session(sesion))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?expirada"));
        assertTrue(sesion.isInvalid());
        verifyNoInteractions(loginServicio, departamentos, cargos);
    }

    @Test
    void reporteVistaControladorConSesionValidaCargaModeloOrdenadoPorCodigo(@Mock CargoServicio cargos,
            @Mock DepartamentoServicio departamentos) throws Exception {
        MockHttpSession sesion = new MockHttpSession();
        sesion.setAttribute(SesionInterceptor.ATRIBUTO_USUARIO, "U00001");
        Usuario usuario = usuarioDemo();
        when(loginServicio.buscarPorCodigo("U00001")).thenReturn(Optional.of(usuario));
        when(departamentos.listar()).thenReturn(List.of(departamento("D02", "Emergencia"),
                departamento("D01", "Administración")));
        when(cargos.listar()).thenReturn(List.of(cargo("C03", "Médico"), cargo("C01", "Analista")));

        MvcResult resultado = vistaMvc(departamentos, cargos).perform(get("/reportes").session(sesion))
                .andExpect(status().isOk())
                .andExpect(view().name("reportes"))
                .andExpect(model().attribute("usuario", usuario))
                .andExpect(model().attribute("persona", usuario.getPersona()))
                .andExpect(model().attributeExists("listaDepartamentos", "listaCargos"))
                .andReturn();

        @SuppressWarnings("unchecked")
        List<Departamento> listaDepartamentos =
                (List<Departamento>) resultado.getModelAndView().getModel().get("listaDepartamentos");
        @SuppressWarnings("unchecked")
        List<Cargo> listaCargos = (List<Cargo>) resultado.getModelAndView().getModel().get("listaCargos");
        assertEquals("D01", listaDepartamentos.get(0).getCodigoDepartamento());
        assertEquals("D02", listaDepartamentos.get(1).getCodigoDepartamento());
        assertEquals("C01", listaCargos.get(0).getCodigoCargo());
        assertEquals("C03", listaCargos.get(1).getCodigoCargo());
    }

    private MockMvc vistaMvc(DepartamentoServicio departamentos, CargoServicio cargos) {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix("/WEB-INF/jsp/");
        resolver.setSuffix(".jsp");
        return MockMvcBuilders.standaloneSetup(new ReporteVistaControlador(loginServicio, departamentos, cargos))
                .setViewResolvers(resolver)
                .build();
    }

    private static Departamento departamento(String codigo, String nombre) {
        Departamento d = new Departamento();
        d.setCodigoDepartamento(codigo);
        d.setNombre(nombre);
        d.setActivo(true);
        return d;
    }

    private static Cargo cargo(String codigo, String nombre) {
        Cargo c = new Cargo();
        c.setCodigoCargo(codigo);
        c.setNombre(nombre);
        c.setActivo(true);
        return c;
    }

    // ------------------------------------------------------------ utilidades

    /** Concatena el texto de todas las celdas de todas las hojas del XLSX. */
    private static String textoXlsx(byte[] contenido) throws Exception {
        StringBuilder texto = new StringBuilder();
        DataFormatter formateador = new DataFormatter();
        try (XSSFWorkbook libro = new XSSFWorkbook(new ByteArrayInputStream(contenido))) {
            assertNotNull(libro);
            for (Sheet hoja : libro) {
                for (Row fila : hoja) {
                    for (Cell celda : fila) {
                        texto.append(formateador.formatCellValue(celda)).append('\n');
                    }
                }
            }
        }
        return texto.toString();
    }
}
