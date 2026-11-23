package edu.ec.monster.ORM_JAVA_GR03;

import edu.ec.monster.ORM_JAVA_GR03.controlador.EmpleadoVistaControlador;
import edu.ec.monster.ORM_JAVA_GR03.dto.DatoAcademicoDto;
import edu.ec.monster.ORM_JAVA_GR03.dto.DatoFamiliarDto;
import edu.ec.monster.ORM_JAVA_GR03.dto.EmpleadoDetalle;
import edu.ec.monster.ORM_JAVA_GR03.dto.EmpleadoGuardarRequest;
import edu.ec.monster.ORM_JAVA_GR03.dto.EmpleadoResumen;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Cargo;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Departamento;
import edu.ec.monster.ORM_JAVA_GR03.modelo.EstadoCivil;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Genero;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Persona;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Usuario;
import edu.ec.monster.ORM_JAVA_GR03.seguridad.SesionInterceptor;
import edu.ec.monster.ORM_JAVA_GR03.servicio.CargoServicio;
import edu.ec.monster.ORM_JAVA_GR03.servicio.DepartamentoServicio;
import edu.ec.monster.ORM_JAVA_GR03.servicio.EstadoCivilServicio;
import edu.ec.monster.ORM_JAVA_GR03.servicio.GeneroServicio;
import edu.ec.monster.ORM_JAVA_GR03.servicio.LoginServicio;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class EmpleadoVistaYControladorTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @Mock
    private LoginServicio loginServicio;

    @Mock
    private GeneroServicio generoServicio;

    @Mock
    private EstadoCivilServicio estadoCivilServicio;

    @Mock
    private DepartamentoServicio departamentoServicio;

    @Mock
    private CargoServicio cargoServicio;

    private MockMvc mockMvc;

    @BeforeAll
    static void iniciarValidador() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void cerrarValidador() {
        if (validatorFactory != null) {
            validatorFactory.close();
        }
    }

    @BeforeEach
    void configurarMockMvc() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/jsp/");
        viewResolver.setSuffix(".jsp");

        EmpleadoVistaControlador controlador = new EmpleadoVistaControlador(
                loginServicio, generoServicio, estadoCivilServicio, departamentoServicio, cargoServicio);
        mockMvc = MockMvcBuilders.standaloneSetup(controlador)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void datoAcademicoDtoValidaRestriccionesBeanValidation() {
        DatoAcademicoDto valido = new DatoAcademicoDto(
                1L, "Maestría", "Escuela Politécnica Nacional", "Magíster en Salud Pública");
        assertTrue(validator.validate(valido).isEmpty());

        DatoAcademicoDto nivelBlanco = new DatoAcademicoDto(null, "  ", "EPN", "Ingeniero");
        assertFalse(validator.validate(nivelBlanco).isEmpty());

        DatoAcademicoDto institucionBlanca = new DatoAcademicoDto(null, "Tercer Nivel", "", "Ingeniero");
        assertFalse(validator.validate(institucionBlanca).isEmpty());

        DatoAcademicoDto tituloLargo = new DatoAcademicoDto(null, "Tercer Nivel", "UCE", "A".repeat(151));
        assertFalse(validator.validate(tituloLargo).isEmpty());
    }

    @Test
    void datoFamiliarDtoValidaRestriccionesBeanValidation() {
        DatoFamiliarDto valido = new DatoFamiliarDto(
                10L, "Cónyuge", "Cárdenas Vega", "Paola", LocalDate.of(1993, 11, 18));
        assertTrue(validator.validate(valido).isEmpty());

        DatoFamiliarDto sinFecha = new DatoFamiliarDto(
                null, "Madre", "Ejemplo Morales", "Carmen", null);
        assertTrue(validator.validate(sinFecha).isEmpty());

        DatoFamiliarDto parentescoBlanco = new DatoFamiliarDto(
                null, "", "Mora", "Luis", LocalDate.of(2000, 1, 1));
        assertFalse(validator.validate(parentescoBlanco).isEmpty());

        DatoFamiliarDto nombresLargo = new DatoFamiliarDto(
                null, "Hijo/a", "Mora", "N".repeat(151), null);
        assertFalse(validator.validate(nombresLargo).isEmpty());
    }

    @Test
    void empleadoResumenYEmpleadoDetalleExponenTodosLosCamposDelContrato() {
        EmpleadoResumen resumen = new EmpleadoResumen(
                "P00001", "EXT0000001", "Ejemplo", "Ana", "Administradora del sistema",
                "1700000001", "ana.ejemplo@sigh.local", "/uploads/empleados/P00001.png",
                "D01", "Administracion", "C01", "Analista de sistemas", true, 2L, 1L);

        assertEquals("P00001", resumen.codigoPersona());
        assertEquals("EXT0000001", resumen.codigoExterno());
        assertEquals("Ejemplo", resumen.apellidos());
        assertEquals("Ana", resumen.nombres());
        assertEquals("Administradora del sistema", resumen.funcion());
        assertEquals("1700000001", resumen.numeroDocumento());
        assertEquals("ana.ejemplo@sigh.local", resumen.correo());
        assertEquals("/uploads/empleados/P00001.png", resumen.foto());
        assertEquals("D01", resumen.codigoDepartamento());
        assertEquals("Administracion", resumen.departamento());
        assertEquals("C01", resumen.codigoCargo());
        assertEquals("Analista de sistemas", resumen.cargo());
        assertEquals(Boolean.TRUE, resumen.activo());
        assertEquals(2L, resumen.totalAcademicos());
        assertEquals(1L, resumen.totalFamiliares());

        List<DatoAcademicoDto> academicos = List.of(
                new DatoAcademicoDto(1L, "Tercer Nivel", "ESPE", "Ingeniera en Software"));
        List<DatoFamiliarDto> familiares = List.of(
                new DatoFamiliarDto(2L, "Madre", "Ejemplo Morales", "Carmen", LocalDate.of(1968, 6, 15)));

        EmpleadoDetalle detalle = new EmpleadoDetalle(
                "P00001", "EXT0000001", "Ejemplo", "Ana", "/uploads/empleados/P00001.png",
                "Administradora del sistema", "REG-001", true, "CEDULA", "1700000001",
                LocalDate.of(1995, 4, 12), "0990000001", "ana.ejemplo@sigh.local",
                "F", "Femenino", "S", "Soltero", "O+", new BigDecimal("61.50"), new BigDecimal("1.65"),
                "D01", "Administracion", "C01", "Analista de sistemas", true, academicos, familiares);

        assertEquals("P00001", detalle.codigoPersona());
        assertEquals("O+", detalle.tipoSangre());
        assertEquals(new BigDecimal("61.50"), detalle.peso());
        assertEquals(new BigDecimal("1.65"), detalle.estatura());
        assertTrue(detalle.tieneUsuario());
        assertEquals(academicos, detalle.academicos());
        assertEquals(familiares, detalle.familiares());
    }

    @Test
    void empleadoGuardarRequestValidaRestriccionesBeanValidationYListasHijas() {
        EmpleadoGuardarRequest valido = new EmpleadoGuardarRequest(
                "P00007", "EXT0000007", "Morales", "Camila", "Medico residente", "MSP-007", true,
                "CEDULA", "1700000007", LocalDate.of(1994, 5, 20), "0990000007", "camila.morales@sigh.local",
                "F", "S", "O+", new BigDecimal("59.50"), new BigDecimal("1.64"), "D02", "C03",
                List.of(new DatoAcademicoDto(null, "Tercer Nivel", "UCE", "Medico Cirujano")),
                List.of(new DatoFamiliarDto(null, "Padre", "Morales", "Marco", LocalDate.of(1964, 3, 10))));
        assertTrue(validator.validate(valido).isEmpty());

        EmpleadoGuardarRequest pesoFueraDeRango = new EmpleadoGuardarRequest(
                "P00007", null, "Morales", "Camila", "Medico residente", null, true,
                null, null, null, null, null, null, null, "O+",
                new BigDecimal("19.99"), new BigDecimal("1.64"), "D02", "C03", List.of(), List.of());
        Set<ConstraintViolation<EmpleadoGuardarRequest>> violacionesPeso = validator.validate(pesoFueraDeRango);
        assertFalse(violacionesPeso.isEmpty());

        EmpleadoGuardarRequest estaturaFueraDeRango = new EmpleadoGuardarRequest(
                "P00007", null, "Morales", "Camila", "Medico residente", null, true,
                null, null, null, null, null, null, null, "O+",
                new BigDecimal("60.00"), new BigDecimal("2.51"), "D02", "C03", List.of(), List.of());
        assertFalse(validator.validate(estaturaFueraDeRango).isEmpty());

        EmpleadoGuardarRequest hijoAcademicoInvalido = new EmpleadoGuardarRequest(
                "P00007", null, "Morales", "Camila", "Medico residente", null, true,
                null, null, null, null, null, null, null, null, null, null, "D02", "C03",
                List.of(new DatoAcademicoDto(null, "", "UCE", "Medico")),
                List.of());
        assertFalse(validator.validate(hijoAcademicoInvalido).isEmpty());

        EmpleadoGuardarRequest hijoFamiliarInvalido = new EmpleadoGuardarRequest(
                "P00007", null, "Morales", "Camila", "Medico residente", null, true,
                null, null, null, null, null, null, null, null, null, null, "D02", "C03",
                List.of(),
                List.of(new DatoFamiliarDto(null, "Padre", "", "Marco", null)));
        assertFalse(validator.validate(hijoFamiliarInvalido).isEmpty());
    }

    @Test
    void personaPermiteCamposBiometricosYFotoNulosYValidaRangosCuandoSeInforman() {
        Departamento dep = new Departamento();
        dep.setCodigoDepartamento("D01");
        dep.setNombre("Administracion");
        dep.setActivo(true);

        Cargo cargo = new Cargo();
        cargo.setCodigoCargo("C01");
        cargo.setNombre("Analista");
        cargo.setActivo(true);

        Persona minima = new Persona();
        minima.setCodigoPersona("PT0001");
        minima.setCodigoExterno("EXT9999");
        minima.setNombres("Prueba");
        minima.setApellidos("Retro");
        minima.setFuncion("Pruebas");
        minima.setActivo(true);
        minima.setDepartamento(dep);
        minima.setCargo(cargo);
        assertTrue(validator.validate(minima).isEmpty());

        minima.setFoto("/uploads/empleados/PT0001.png");
        minima.setTipoSangre("AB+");
        minima.setPeso(new BigDecimal("70.25"));
        minima.setEstatura(new BigDecimal("1.75"));
        assertTrue(validator.validate(minima).isEmpty());

        minima.setPeso(new BigDecimal("350.00"));
        assertFalse(validator.validate(minima).isEmpty());
    }

    @Test
    void getEmpleadosSinUsuarioEnSesionRedirigeALoginExpirada() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(get("/empleados").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?expirada"));

        assertTrue(session.isInvalid());
        verifyNoInteractions(loginServicio, generoServicio, estadoCivilServicio, departamentoServicio, cargoServicio);
    }

    @Test
    void getEmpleadosConUsuarioInexistenteEnRepositorioRedirigeALoginExpirada() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SesionInterceptor.ATRIBUTO_USUARIO, "U99999");
        when(loginServicio.buscarPorCodigo("U99999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/empleados").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?expirada"));

        assertTrue(session.isInvalid());
        verify(loginServicio).buscarPorCodigo("U99999");
        verifyNoInteractions(generoServicio, estadoCivilServicio, departamentoServicio, cargoServicio);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getEmpleadosConSesionValidaDespachaVistaEmpleadosConCatalogosOrdenados() throws Exception {
        Persona persona = new Persona();
        persona.setCodigoPersona("P00001");
        persona.setNombres("Ana");
        persona.setApellidos("Ejemplo");

        Usuario usuario = new Usuario();
        usuario.setCodigoUsuario("U00001");
        usuario.setUsuario("demo.sigh");
        usuario.setPersona(persona);

        Genero genM = new Genero();
        genM.setCodigoGenero("M");
        genM.setNombre("Masculino");
        genM.setActivo(true);
        Genero genF = new Genero();
        genF.setCodigoGenero("F");
        genF.setNombre("Femenino");
        genF.setActivo(true);
        Genero genInactivo = new Genero();
        genInactivo.setCodigoGenero("X");
        genInactivo.setNombre("Inactivo");
        genInactivo.setActivo(false);

        EstadoCivil estS = new EstadoCivil();
        estS.setCodigoEstadoCivil("S");
        estS.setNombre("Soltero");
        estS.setActivo(true);
        EstadoCivil estC = new EstadoCivil();
        estC.setCodigoEstadoCivil("C");
        estC.setNombre("Casado");
        estC.setActivo(true);
        EstadoCivil estInactivo = new EstadoCivil();
        estInactivo.setCodigoEstadoCivil("Z");
        estInactivo.setNombre("Otro");
        estInactivo.setActivo(false);

        Departamento dep2 = new Departamento();
        dep2.setCodigoDepartamento("D02");
        dep2.setNombre("Emergencia");
        dep2.setActivo(true);
        Departamento dep1 = new Departamento();
        dep1.setCodigoDepartamento("D01");
        dep1.setNombre("Administracion");
        dep1.setActivo(true);

        Cargo cargo3 = new Cargo();
        cargo3.setCodigoCargo("C03");
        cargo3.setNombre("Medico general");
        cargo3.setActivo(true);
        Cargo cargo1 = new Cargo();
        cargo1.setCodigoCargo("C01");
        cargo1.setNombre("Analista de sistemas");
        cargo1.setActivo(true);

        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SesionInterceptor.ATRIBUTO_USUARIO, "U00001");

        when(loginServicio.buscarPorCodigo("U00001")).thenReturn(Optional.of(usuario));
        when(generoServicio.listar()).thenReturn(List.of(genM, genInactivo, genF));
        when(estadoCivilServicio.listar()).thenReturn(List.of(estS, estInactivo, estC));
        when(departamentoServicio.listar()).thenReturn(List.of(dep2, dep1));
        when(cargoServicio.listar()).thenReturn(List.of(cargo3, cargo1));

        MvcResult result = mockMvc.perform(get("/empleados").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("empleados"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/empleados.jsp"))
                .andExpect(model().attributeExists(
                        "usuario", "persona", "listaGeneros", "listaEstadosCiviles",
                        "listaDepartamentos", "listaCargos", "tiposSangre",
                        "nivelesAcademicos", "parentescos"))
                .andReturn();

        assertSame(usuario, result.getModelAndView().getModel().get("usuario"));
        assertSame(persona, result.getModelAndView().getModel().get("persona"));

        List<Genero> generos = (List<Genero>) result.getModelAndView().getModel().get("listaGeneros");
        assertEquals(List.of("F", "M"), generos.stream().map(Genero::getCodigoGenero).toList());

        List<EstadoCivil> estados = (List<EstadoCivil>) result.getModelAndView().getModel().get("listaEstadosCiviles");
        assertEquals(List.of("C", "S"), estados.stream().map(EstadoCivil::getCodigoEstadoCivil).toList());

        List<Departamento> deps = (List<Departamento>) result.getModelAndView().getModel().get("listaDepartamentos");
        assertEquals(List.of("D01", "D02"), deps.stream().map(Departamento::getCodigoDepartamento).toList());

        List<Cargo> cargos = (List<Cargo>) result.getModelAndView().getModel().get("listaCargos");
        assertEquals(List.of("C01", "C03"), cargos.stream().map(Cargo::getCodigoCargo).toList());

        assertEquals(EmpleadoVistaControlador.TIPOS_SANGRE, result.getModelAndView().getModel().get("tiposSangre"));
        assertEquals(EmpleadoVistaControlador.NIVELES_ACADEMICOS,
                result.getModelAndView().getModel().get("nivelesAcademicos"));
        assertEquals(EmpleadoVistaControlador.PARENTESCOS, result.getModelAndView().getModel().get("parentescos"));

        verify(loginServicio).buscarPorCodigo("U00001");
        verify(generoServicio).listar();
        verify(estadoCivilServicio).listar();
        verify(departamentoServicio).listar();
        verify(cargoServicio).listar();
    }
}
