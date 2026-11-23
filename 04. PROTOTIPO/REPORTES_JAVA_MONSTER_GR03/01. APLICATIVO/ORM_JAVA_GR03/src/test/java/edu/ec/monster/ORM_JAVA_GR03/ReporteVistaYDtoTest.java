package edu.ec.monster.ORM_JAVA_GR03;

import edu.ec.monster.ORM_JAVA_GR03.dto.reporte.FormatoReporte;
import edu.ec.monster.ORM_JAVA_GR03.dto.reporte.ReporteCargoFila;
import edu.ec.monster.ORM_JAVA_GR03.dto.reporte.ReporteDepartamentoFila;
import edu.ec.monster.ORM_JAVA_GR03.dto.reporte.ReportePersonalFila;
import edu.ec.monster.ORM_JAVA_GR03.dto.reporte.ResumenReportesDto;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReporteVistaYDtoTest {

    private static final Map<String, String> PARAMETROS_COMUNES = Map.of(
            "TITULO_REPORTE", "java.lang.String",
            "SUBTITULO_FILTROS", "java.lang.String",
            "USUARIO_GENERA", "java.lang.String",
            "FECHA_GENERACION", "java.lang.String",
            "TOTAL_REGISTROS", "java.lang.Integer",
            "TOTAL_ACTIVOS", "java.lang.Integer",
            "TOTAL_PERSONAL_ASIGNADO", "java.lang.Long",
            "LOGO_IMAGEN", "java.io.InputStream");

    @Test
    void formatoReporteResuelvePdfXlsxYRechazaValoresInvalidos() {
        assertEquals("pdf", FormatoReporte.PDF.getExtension());
        assertEquals("application/pdf", FormatoReporte.PDF.getMediaType());
        assertEquals("xlsx", FormatoReporte.XLSX.getExtension());
        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                FormatoReporte.XLSX.getMediaType());

        assertEquals(FormatoReporte.PDF, FormatoReporte.desde(null));
        assertEquals(FormatoReporte.PDF, FormatoReporte.desde(""));
        assertEquals(FormatoReporte.PDF, FormatoReporte.desde("   "));
        assertEquals(FormatoReporte.PDF, FormatoReporte.desde("pdf"));
        assertEquals(FormatoReporte.PDF, FormatoReporte.desde("PDF"));
        assertEquals(FormatoReporte.XLSX, FormatoReporte.desde("xlsx"));
        assertEquals(FormatoReporte.XLSX, FormatoReporte.desde("  XLSX  "));

        assertThrows(IllegalArgumentException.class, () -> FormatoReporte.desde("docx"));
        assertThrows(IllegalArgumentException.class, () -> FormatoReporte.desde("exe"));
        assertThrows(IllegalArgumentException.class, () -> FormatoReporte.desde("csv"));
    }

    @Test
    void resumenReportesDtoExponeTodosLosIndicadoresDelContrato() {
        ResumenReportesDto resumen = new ResumenReportesDto(5L, 4L, 7L, 6L, 12L, 10L);

        assertTrue(ResumenReportesDto.class.isRecord());
        assertEquals(5L, resumen.totalDepartamentos());
        assertEquals(4L, resumen.departamentosActivos());
        assertEquals(7L, resumen.totalCargos());
        assertEquals(6L, resumen.cargosActivos());
        assertEquals(12L, resumen.totalPersonal());
        assertEquals(10L, resumen.personalActivo());
    }

    @Test
    void reporteDepartamentoFilaEsPojoJavaBeanConGettersCompatiblesConJasper() throws Exception {
        assertFalse(ReporteDepartamentoFila.class.isRecord());

        ReporteDepartamentoFila fila = new ReporteDepartamentoFila();
        fila.setCodigoDepartamento("D02");
        fila.setNombre("Emergencia");
        fila.setEstado("Activo");
        fila.setTotalPersonal(4L);

        assertEquals("D02", fila.getCodigoDepartamento());
        assertEquals("Emergencia", fila.getNombre());
        assertEquals("Activo", fila.getEstado());
        assertEquals(4L, fila.getTotalPersonal());

        ReporteDepartamentoFila copia = new ReporteDepartamentoFila("D02", "Emergencia", "Activo", 4L);
        assertEquals(copia, fila);
        assertEquals(copia.hashCode(), fila.hashCode());
        assertTrue(fila.toString().contains("D02"));

        validarPropiedadesJavaBean(ReporteDepartamentoFila.class, Map.of(
                "codigoDepartamento", String.class,
                "nombre", String.class,
                "estado", String.class,
                "totalPersonal", Long.class));
    }

    @Test
    void reporteCargoFilaEsPojoJavaBeanConGettersCompatiblesConJasper() throws Exception {
        assertFalse(ReporteCargoFila.class.isRecord());

        ReporteCargoFila fila = new ReporteCargoFila();
        fila.setCodigoCargo("C03");
        fila.setNombre("Médico general");
        fila.setEstado("Activo");
        fila.setTotalPersonal(3L);
        fila.setDepartamentos("D02 - Emergencia, D03 - Consulta Externa");

        assertEquals("C03", fila.getCodigoCargo());
        assertEquals("Médico general", fila.getNombre());
        assertEquals("Activo", fila.getEstado());
        assertEquals(3L, fila.getTotalPersonal());
        assertEquals("D02 - Emergencia, D03 - Consulta Externa", fila.getDepartamentos());

        ReporteCargoFila copia = new ReporteCargoFila(
                "C03", "Médico general", "Activo", 3L, "D02 - Emergencia, D03 - Consulta Externa");
        assertEquals(copia, fila);
        assertEquals(copia.hashCode(), fila.hashCode());
        assertTrue(fila.toString().contains("C03"));

        validarPropiedadesJavaBean(ReporteCargoFila.class, Map.of(
                "codigoCargo", String.class,
                "nombre", String.class,
                "estado", String.class,
                "totalPersonal", Long.class,
                "departamentos", String.class));
    }

    @Test
    void reportePersonalFilaEsPojoJavaBeanConGettersCompatiblesConJasper() throws Exception {
        assertFalse(ReportePersonalFila.class.isRecord());

        ReportePersonalFila fila = new ReportePersonalFila();
        fila.setCodigoPersona("P00001");
        fila.setCodigoExterno("EXT00001");
        fila.setNombreCompleto("Ejemplo, Ana");
        fila.setNumeroDocumento("1700000001");
        fila.setDepartamento("Administración");
        fila.setCargo("Analista de sistemas");
        fila.setFuncion("Administración de plataforma SIGH");
        fila.setCorreo("ana.ejemplo@sigh.local");
        fila.setEstado("Activo");
        fila.setTotalAcademicos(2L);
        fila.setTotalFamiliares(2L);

        assertEquals("P00001", fila.getCodigoPersona());
        assertEquals("EXT00001", fila.getCodigoExterno());
        assertEquals("Ejemplo, Ana", fila.getNombreCompleto());
        assertEquals("1700000001", fila.getNumeroDocumento());
        assertEquals("Administración", fila.getDepartamento());
        assertEquals("Analista de sistemas", fila.getCargo());
        assertEquals("Administración de plataforma SIGH", fila.getFuncion());
        assertEquals("ana.ejemplo@sigh.local", fila.getCorreo());
        assertEquals("Activo", fila.getEstado());
        assertEquals(2L, fila.getTotalAcademicos());
        assertEquals(2L, fila.getTotalFamiliares());

        ReportePersonalFila copia = new ReportePersonalFila(
                "P00001", "EXT00001", "Ejemplo, Ana", "1700000001",
                "Administración", "Analista de sistemas", "Administración de plataforma SIGH",
                "ana.ejemplo@sigh.local", "Activo", 2L, 2L);
        assertEquals(copia, fila);
        assertEquals(copia.hashCode(), fila.hashCode());
        assertTrue(fila.toString().contains("P00001"));

        Map<String, Class<?>> propiedadesEsperadas = new LinkedHashMap<>();
        propiedadesEsperadas.put("codigoPersona", String.class);
        propiedadesEsperadas.put("codigoExterno", String.class);
        propiedadesEsperadas.put("nombreCompleto", String.class);
        propiedadesEsperadas.put("numeroDocumento", String.class);
        propiedadesEsperadas.put("departamento", String.class);
        propiedadesEsperadas.put("cargo", String.class);
        propiedadesEsperadas.put("funcion", String.class);
        propiedadesEsperadas.put("correo", String.class);
        propiedadesEsperadas.put("estado", String.class);
        propiedadesEsperadas.put("totalAcademicos", Long.class);
        propiedadesEsperadas.put("totalFamiliares", Long.class);

        validarPropiedadesJavaBean(ReportePersonalFila.class, propiedadesEsperadas);
    }

    @Test
    void plantillaDepartamentosCumpleContratoJrxml708() throws Exception {
        Document doc = cargarXml("/reportes/reporte_departamentos.jrxml");
        Element root = doc.getDocumentElement();

        validarRaizComun(root, "reporte_departamentos", "595", "842", "555", null);
        validarParametrosComunes(doc);
        validarCamposExactos(doc, Map.of(
                "codigoDepartamento", "java.lang.String",
                "nombre", "java.lang.String",
                "estado", "java.lang.String",
                "totalPersonal", "java.lang.Long"));
        validarEstructuraJrxml7(doc, 555);
    }

    @Test
    void plantillaCargosCumpleContratoJrxml708() throws Exception {
        Document doc = cargarXml("/reportes/reporte_cargos.jrxml");
        Element root = doc.getDocumentElement();

        validarRaizComun(root, "reporte_cargos", "595", "842", "555", null);
        validarParametrosComunes(doc);
        validarCamposExactos(doc, Map.of(
                "codigoCargo", "java.lang.String",
                "nombre", "java.lang.String",
                "estado", "java.lang.String",
                "totalPersonal", "java.lang.Long",
                "departamentos", "java.lang.String"));
        validarEstructuraJrxml7(doc, 555);
    }

    @Test
    void plantillaPersonalCumpleContratoJrxml708Horizontal() throws Exception {
        Document doc = cargarXml("/reportes/reporte_personal.jrxml");
        Element root = doc.getDocumentElement();

        validarRaizComun(root, "reporte_personal", "842", "595", "802", "Landscape");
        validarParametrosComunes(doc);

        Map<String, String> camposEsperados = new LinkedHashMap<>();
        camposEsperados.put("codigoPersona", "java.lang.String");
        camposEsperados.put("codigoExterno", "java.lang.String");
        camposEsperados.put("nombreCompleto", "java.lang.String");
        camposEsperados.put("numeroDocumento", "java.lang.String");
        camposEsperados.put("departamento", "java.lang.String");
        camposEsperados.put("cargo", "java.lang.String");
        camposEsperados.put("funcion", "java.lang.String");
        camposEsperados.put("correo", "java.lang.String");
        camposEsperados.put("estado", "java.lang.String");
        camposEsperados.put("totalAcademicos", "java.lang.Long");
        camposEsperados.put("totalFamiliares", "java.lang.Long");

        validarCamposExactos(doc, camposEsperados);
        validarEstructuraJrxml7(doc, 802);
    }

    private void validarPropiedadesJavaBean(Class<?> clase, Map<String, Class<?>> esperadas) throws Exception {
        Map<String, PropertyDescriptor> descriptores = new LinkedHashMap<>();
        for (PropertyDescriptor pd : Introspector.getBeanInfo(clase, Object.class).getPropertyDescriptors()) {
            descriptores.put(pd.getName(), pd);
        }
        for (Map.Entry<String, Class<?>> entry : esperadas.entrySet()) {
            PropertyDescriptor pd = descriptores.get(entry.getKey());
            assertNotNull(pd, "Falta propiedad JavaBean '" + entry.getKey() + "' en " + clase.getSimpleName());
            assertNotNull(pd.getReadMethod(), "Falta getter getXxx() para '" + entry.getKey() + "'");
            assertTrue(pd.getReadMethod().getName().startsWith("get"),
                    "El getter debe seguir convencion JavaBean getXxx(): " + pd.getReadMethod().getName());
            assertEquals(entry.getValue(), pd.getPropertyType(),
                    "Tipo incorrecto para propiedad '" + entry.getKey() + "'");
        }
    }

    private Document cargarXml(String rutaClasspath) throws Exception {
        try (InputStream is = getClass().getResourceAsStream(rutaClasspath)) {
            assertNotNull(is, "No se encontro el archivo JRXML en el classpath: " + rutaClasspath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);
            doc.getDocumentElement().normalize();
            return doc;
        }
    }

    private void validarRaizComun(
            Element root,
            String nombreEsperado,
            String anchoPagina,
            String altoPagina,
            String anchoColumna,
            String orientacionEsperada) {
        assertEquals("jasperReport", root.getTagName());
        assertEquals(nombreEsperado, root.getAttribute("name"));
        assertEquals("java", root.getAttribute("language"));
        assertEquals("AllSectionsNoDetail", root.getAttribute("whenNoDataType"));
        assertEquals(anchoPagina, root.getAttribute("pageWidth"));
        assertEquals(altoPagina, root.getAttribute("pageHeight"));
        assertEquals(anchoColumna, root.getAttribute("columnWidth"));
        assertFalse(root.getAttribute("uuid").isBlank(), "El reporte debe incluir atributo uuid");
        if (orientacionEsperada != null) {
            assertEquals(orientacionEsperada, root.getAttribute("orientation"));
        }
    }

    private void validarParametrosComunes(Document doc) {
        NodeList nodos = doc.getElementsByTagName("parameter");
        Map<String, String> encontrados = new LinkedHashMap<>();
        for (int i = 0; i < nodos.getLength(); i++) {
            Element param = (Element) nodos.item(i);
            encontrados.put(param.getAttribute("name"), param.getAttribute("class"));
        }
        assertEquals(PARAMETROS_COMUNES, encontrados,
                "Los parametros declarados en el JRXML deben coincidir exactamente con el contrato");
    }

    private void validarCamposExactos(Document doc, Map<String, String> esperados) {
        NodeList nodos = doc.getElementsByTagName("field");
        Map<String, String> encontrados = new LinkedHashMap<>();
        for (int i = 0; i < nodos.getLength(); i++) {
            Element field = (Element) nodos.item(i);
            encontrados.put(field.getAttribute("name"), field.getAttribute("class"));
        }
        assertEquals(esperados, encontrados,
                "Los campos <field> declarados en el JRXML deben coincidir exactamente con el POJO");
    }

    private void validarEstructuraJrxml7(Document doc, int columnWidth) {
        assertEquals(0, doc.getElementsByTagName("reportElement").getLength(),
                "No debe usar etiquetas legacy <reportElement> de JRXML 6.x");
        assertEquals(0, doc.getElementsByTagName("textField").getLength(),
                "No debe usar etiquetas legacy <textField> de JRXML 6.x");
        assertEquals(0, doc.getElementsByTagName("staticText").getLength(),
                "No debe usar etiquetas legacy <staticText> de JRXML 6.x");
        assertEquals(0, doc.getElementsByTagName("image").getLength(),
                "No debe usar etiquetas legacy <image> de JRXML 6.x");

        NodeList elementos = doc.getElementsByTagName("element");
        assertTrue(elementos.getLength() > 0, "Debe contener nodos <element kind=\"...\"> de JasperReports 7");

        boolean logoValido = false;
        for (int i = 0; i < elementos.getLength(); i++) {
            Element el = (Element) elementos.item(i);
            String kind = el.getAttribute("kind");
            assertFalse(kind.isBlank(), "Todo <element> debe declarar atributo kind");

            int x = Integer.parseInt(el.getAttribute("x"));
            int width = Integer.parseInt(el.getAttribute("width"));
            assertTrue(x >= 0 && width > 0 && x + width <= columnWidth,
                    "Elemento fuera de columnWidth (" + columnWidth + "): kind=" + kind + " x=" + x + " width=" + width);

            if ("image".equals(kind)) {
                assertEquals("Blank", el.getAttribute("onErrorType"),
                        "El logo debe declarar onErrorType=\"Blank\" para tolerar LOGO_IMAGEN nulo");
                NodeList expresiones = el.getElementsByTagName("expression");
                assertEquals(1, expresiones.getLength());
                if (expresiones.item(0).getTextContent().contains("$P{LOGO_IMAGEN}")) {
                    logoValido = true;
                }
            }
        }
        assertTrue(logoValido, "Debe existir un elemento <element kind=\"image\" onErrorType=\"Blank\"> con $P{LOGO_IMAGEN}");
    }
}
