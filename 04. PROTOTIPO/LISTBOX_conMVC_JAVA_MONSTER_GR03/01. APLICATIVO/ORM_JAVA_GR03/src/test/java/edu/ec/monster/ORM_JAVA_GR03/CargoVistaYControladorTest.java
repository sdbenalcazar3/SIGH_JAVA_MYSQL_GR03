package edu.ec.monster.ORM_JAVA_GR03;

import edu.ec.monster.ORM_JAVA_GR03.controlador.CargoVistaControlador;
import edu.ec.monster.ORM_JAVA_GR03.dto.CambioEstadoLoteRequest;
import edu.ec.monster.ORM_JAVA_GR03.dto.CargoResumen;
import edu.ec.monster.ORM_JAVA_GR03.dto.PersonalCargo;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Cargo;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Departamento;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Persona;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Usuario;
import edu.ec.monster.ORM_JAVA_GR03.seguridad.SesionInterceptor;
import edu.ec.monster.ORM_JAVA_GR03.servicio.CargoServicio;
import edu.ec.monster.ORM_JAVA_GR03.servicio.DepartamentoServicio;
import edu.ec.monster.ORM_JAVA_GR03.servicio.LoginServicio;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
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
class CargoVistaYControladorTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @Mock
    private LoginServicio loginServicio;

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

        CargoVistaControlador controlador = new CargoVistaControlador(
                loginServicio, departamentoServicio, cargoServicio);
        mockMvc = MockMvcBuilders.standaloneSetup(controlador)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void cargoResumenExponeTodosLosCamposDelContrato() {
        List<String> departamentos = List.of("D02 - Emergencia", "D03 - Consulta Externa");
        CargoResumen resumen = new CargoResumen("C03", "Medico general", true, 2L, departamentos);

        assertEquals("C03", resumen.codigoCargo());
        assertEquals("Medico general", resumen.nombre());
        assertEquals(Boolean.TRUE, resumen.activo());
        assertEquals(2L, resumen.personal());
        assertEquals(departamentos, resumen.departamentos());
    }

    @Test
    void personalCargoExponeTodosLosCamposDelContrato() {
        PersonalCargo persona = new PersonalCargo(
                "P00003", "Carla", "Benitez", "Emergencia",
                "Medico de guardia", "carla.benitez@sigh.local", true);

        assertEquals("P00003", persona.codigoPersona());
        assertEquals("Carla", persona.nombres());
        assertEquals("Benitez", persona.apellidos());
        assertEquals("Emergencia", persona.departamento());
        assertEquals("Medico de guardia", persona.funcion());
        assertEquals("carla.benitez@sigh.local", persona.correo());
        assertEquals(Boolean.TRUE, persona.activo());
    }

    @Test
    void cambioEstadoLoteRequestValidaRestriccionesBeanValidation() {
        CambioEstadoLoteRequest valido = new CambioEstadoLoteRequest(List.of("C04", "C07"), true);
        assertTrue(validator.validate(valido).isEmpty());

        CambioEstadoLoteRequest sinCodigos = new CambioEstadoLoteRequest(List.of(), false);
        Set<ConstraintViolation<CambioEstadoLoteRequest>> violacionesVacio = validator.validate(sinCodigos);
        assertFalse(violacionesVacio.isEmpty());

        CambioEstadoLoteRequest sinActivo = new CambioEstadoLoteRequest(List.of("C04"), null);
        Set<ConstraintViolation<CambioEstadoLoteRequest>> violacionesActivo = validator.validate(sinActivo);
        assertFalse(violacionesActivo.isEmpty());

        CambioEstadoLoteRequest codigoLargo = new CambioEstadoLoteRequest(List.of("C004"), true);
        Set<ConstraintViolation<CambioEstadoLoteRequest>> violacionesLargo = validator.validate(codigoLargo);
        assertFalse(violacionesLargo.isEmpty());

        CambioEstadoLoteRequest codigoBlanco = new CambioEstadoLoteRequest(List.of("   "), true);
        Set<ConstraintViolation<CambioEstadoLoteRequest>> violacionesBlanco = validator.validate(codigoBlanco);
        assertFalse(violacionesBlanco.isEmpty());
    }

    @Test
    void getCargosSinUsuarioEnSesionRedirigeALoginExpirada() throws Exception {
        MockHttpSession session = new MockHttpSession();

        mockMvc.perform(get("/cargos").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?expirada"));

        assertTrue(session.isInvalid());
        verifyNoInteractions(loginServicio, departamentoServicio, cargoServicio);
    }

    @Test
    void getCargosConUsuarioInexistenteEnRepositorioRedirigeALoginExpirada() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute(SesionInterceptor.ATRIBUTO_USUARIO, "U99999");
        when(loginServicio.buscarPorCodigo("U99999")).thenReturn(Optional.empty());

        mockMvc.perform(get("/cargos").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?expirada"));

        assertTrue(session.isInvalid());
        verify(loginServicio).buscarPorCodigo("U99999");
        verifyNoInteractions(departamentoServicio, cargoServicio);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getCargosConSesionValidaDespachaVistaCargosConModeloOrdenado() throws Exception {
        Persona persona = new Persona();
        persona.setCodigoPersona("P00001");
        persona.setNombres("Ana");
        persona.setApellidos("Ejemplo");

        Usuario usuario = new Usuario();
        usuario.setCodigoUsuario("U00001");
        usuario.setUsuario("demo.sigh");
        usuario.setPersona(persona);

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
        when(departamentoServicio.listar()).thenReturn(List.of(dep2, dep1));
        when(cargoServicio.listar()).thenReturn(List.of(cargo3, cargo1));

        MvcResult result = mockMvc.perform(get("/cargos").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("cargos"))
                .andExpect(forwardedUrl("/WEB-INF/jsp/cargos.jsp"))
                .andExpect(model().attributeExists("usuario", "persona", "listaDepartamentos", "listaCargos"))
                .andReturn();

        assertSame(usuario, result.getModelAndView().getModel().get("usuario"));
        assertSame(persona, result.getModelAndView().getModel().get("persona"));

        List<Departamento> deps = (List<Departamento>) result.getModelAndView().getModel().get("listaDepartamentos");
        assertEquals(List.of("D01", "D02"), deps.stream().map(Departamento::getCodigoDepartamento).toList());

        List<Cargo> cargos = (List<Cargo>) result.getModelAndView().getModel().get("listaCargos");
        assertEquals(List.of("C01", "C03"), cargos.stream().map(Cargo::getCodigoCargo).toList());

        verify(loginServicio).buscarPorCodigo("U00001");
        verify(departamentoServicio).listar();
        verify(cargoServicio).listar();
    }
}
