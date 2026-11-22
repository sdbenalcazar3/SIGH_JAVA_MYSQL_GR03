package edu.ec.monster.ORM_JAVA_GR03;

import edu.ec.monster.ORM_JAVA_GR03.dto.CargoResumen;
import edu.ec.monster.ORM_JAVA_GR03.dto.PersonalCargo;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Cargo;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Departamento;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Persona;
import edu.ec.monster.ORM_JAVA_GR03.repository.CargoRepository;
import edu.ec.monster.ORM_JAVA_GR03.repository.PersonaRepository;
import edu.ec.monster.ORM_JAVA_GR03.servicio.CargoServicio;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pruebas unitarias de CargoServicio sin base de datos (repositorios simulados con Mockito). */
@ExtendWith(MockitoExtension.class)
class CargoServicioTest {

    @Mock
    private CargoRepository cargoRepository;

    @Mock
    private PersonaRepository personaRepository;

    private CargoServicio servicio;

    @BeforeEach
    void preparar() {
        servicio = new CargoServicio(cargoRepository, personaRepository);
    }

    private static Cargo cargo(String codigo, String nombre, boolean activo) {
        Cargo cargo = new Cargo();
        cargo.setCodigoCargo(codigo);
        cargo.setNombre(nombre);
        cargo.setActivo(activo);
        return cargo;
    }

    private static HttpStatus estado(ResponseStatusException ex) {
        return HttpStatus.valueOf(ex.getStatusCode().value());
    }

    private static List<String> codigos(String... codigos) {
        return List.of(codigos);
    }

    // ---------------------------------------------------------------- listarResumen

    @Test
    void listarResumenSinFiltrosNoRestringeNada() {
        Cargo c01 = cargo("C01", "Administrador", true);
        Cargo c04 = cargo("C04", "Medico especialista", true);
        when(cargoRepository.filtrar("%", true, false, "")).thenReturn(List.of(c04, c01));
        when(personaRepository.contarPersonalPorCargo()).thenReturn(List.<Object[]>of(new Object[] {"C01", 3L}));
        when(personaRepository.departamentosDistintosPorCargo()).thenReturn(List.<Object[]>of(
                new Object[] {"C01", "D01", "Administracion"},
                new Object[] {"C01", "D02", "Emergencia"}));

        List<CargoResumen> resumen = servicio.listarResumen(null, null, null);

        assertEquals(2, resumen.size());
        assertEquals("C01", resumen.get(0).codigoCargo(), "Ordenado por codigo ascendente");
        assertEquals(3L, resumen.get(0).personal());
        assertEquals(List.of("D01 - Administracion", "D02 - Emergencia"), resumen.get(0).departamentos());
        assertEquals("C04", resumen.get(1).codigoCargo());
        assertEquals(0L, resumen.get(1).personal());
        assertEquals(List.of(), resumen.get(1).departamentos());
    }

    @Test
    void listarResumenCombinaTextoEstadoYDepartamento() {
        when(cargoRepository.filtrar("%medico%", false, true, "D02")).thenReturn(List.of(cargo("C03", "Medico general", true)));
        when(personaRepository.contarPersonalPorCargo()).thenReturn(List.<Object[]>of(new Object[] {"C03", 2L}));
        when(personaRepository.departamentosDistintosPorCargo()).thenReturn(List.of());

        List<CargoResumen> resumen = servicio.listarResumen("  MEDICO ", true, " d02 ");

        assertEquals(1, resumen.size());
        assertEquals("C03", resumen.get(0).codigoCargo());
        verify(cargoRepository).filtrar("%medico%", false, true, "D02");
    }

    @Test
    void listarResumenInactivosYSinPersonal() {
        when(cargoRepository.filtrar("%", false, false, "SIN_PERSONAL")).thenReturn(List.of(cargo("C07", "Auxiliar administrativo", false)));
        when(personaRepository.contarPersonalPorCargo()).thenReturn(List.of());
        when(personaRepository.departamentosDistintosPorCargo()).thenReturn(List.of());

        List<CargoResumen> resumen = servicio.listarResumen("", false, "SIN_PERSONAL");

        assertEquals(1, resumen.size());
        assertEquals(0L, resumen.get(0).personal());
        assertEquals(false, resumen.get(0).activo());
    }

    @Test
    void listarResumenEscapaComodinesLikeDelUsuario() {
        when(cargoRepository.filtrar(anyString(), anyBoolean(), anyBoolean(), anyString())).thenReturn(List.of());
        when(personaRepository.contarPersonalPorCargo()).thenReturn(List.of());
        when(personaRepository.departamentosDistintosPorCargo()).thenReturn(List.of());

        servicio.listarResumen("50%_a!", null, null);

        verify(cargoRepository).filtrar("%50!%!_a!!%", true, false, "");
    }

    @Test
    void listarResumenSinCoincidenciasDevuelveListaVacia() {
        when(cargoRepository.filtrar("%zzz%", false, true, "D01")).thenReturn(List.of());
        when(personaRepository.contarPersonalPorCargo()).thenReturn(List.of());
        when(personaRepository.departamentosDistintosPorCargo()).thenReturn(List.of());

        assertEquals(List.of(), servicio.listarResumen("zzz", true, "D01"));
    }

    // ---------------------------------------------------------------- personal

    @Test
    void personalDevuelveDatosDelPersonalAsignado() {
        Departamento departamento = new Departamento();
        departamento.setCodigoDepartamento("D02");
        departamento.setNombre("Emergencia");
        Persona persona = new Persona();
        persona.setCodigoPersona("P00001");
        persona.setNombres("Ana");
        persona.setApellidos("Aguirre");
        persona.setFuncion("Guardia");
        persona.setCorreo("ana@sigh.ec");
        persona.setActivo(true);
        persona.setDepartamento(departamento);
        when(cargoRepository.findById("C03")).thenReturn(Optional.of(cargo("C03", "Medico general", true)));
        when(personaRepository.findByCargoCodigoCargoOrderByApellidosAsc("C03")).thenReturn(List.of(persona));

        List<PersonalCargo> personal = servicio.personal("c03");

        assertEquals(1, personal.size());
        assertEquals("P00001", personal.get(0).codigoPersona());
        assertEquals("Emergencia", personal.get(0).departamento());
        assertEquals("Aguirre", personal.get(0).apellidos());
    }

    @Test
    void personalDeCargoInexistenteResponde404() {
        when(cargoRepository.findById("C99")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> servicio.personal("C99"));

        assertEquals(HttpStatus.NOT_FOUND, estado(ex));
    }

    // ---------------------------------------------------------------- generarSiguienteCodigo

    @Test
    void siguienteCodigoSinCargosEsC01() {
        when(cargoRepository.codigosCorrelativos()).thenReturn(List.of());

        assertEquals("C01", servicio.generarSiguienteCodigo());
    }

    @Test
    void siguienteCodigoTomaElMaximoMasUnoIgnorandoHuecos() {
        when(cargoRepository.codigosCorrelativos()).thenReturn(List.of("C01", "C03", "C07", "CAB", "C1X"));

        assertEquals("C08", servicio.generarSiguienteCodigo());
    }

    @Test
    void siguienteCodigoConC99BuscaElPrimerHuecoLibre() {
        when(cargoRepository.codigosCorrelativos()).thenReturn(List.of("C01", "C02", "C04", "C99"));

        assertEquals("C03", servicio.generarSiguienteCodigo());
    }

    @Test
    void siguienteCodigoConRangoAgotadoResponde409() {
        List<String> todos = IntStream.rangeClosed(1, 99).mapToObj(n -> String.format("C%02d", n)).collect(Collectors.toList());
        when(cargoRepository.codigosCorrelativos()).thenReturn(todos);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> servicio.generarSiguienteCodigo());

        assertEquals(HttpStatus.CONFLICT, estado(ex));
    }

    // ---------------------------------------------------------------- normalizar / registrar

    @Test
    void normalizarAplicaMayusculasYColapsaEspacios() {
        Cargo cargo = cargo("  c08 ", "  Medico    pediatra  ", true);

        servicio.normalizar(cargo);

        assertEquals("C08", cargo.getCodigoCargo());
        assertEquals("Medico pediatra", cargo.getNombre());
    }

    @Test
    void registrarNormalizaYGuarda() {
        when(cargoRepository.existsById("C08")).thenReturn(false);
        when(cargoRepository.existsByNombreIgnoreCaseAndCodigoCargoNot("Medico pediatra", "C08")).thenReturn(false);
        when(cargoRepository.saveAndFlush(any(Cargo.class))).thenAnswer(inv -> inv.getArgument(0));

        Cargo guardado = servicio.registrar(cargo(" c08", " Medico   pediatra ", true));

        assertEquals("C08", guardado.getCodigoCargo());
        assertEquals("Medico pediatra", guardado.getNombre());
        verify(cargoRepository).saveAndFlush(any(Cargo.class));
    }

    @Test
    void registrarConCodigoDuplicadoResponde409() {
        when(cargoRepository.existsById("C01")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> servicio.registrar(cargo("C01", "Otro cargo", true)));

        assertEquals(HttpStatus.CONFLICT, estado(ex));
        verify(cargoRepository, never()).saveAndFlush(any());
    }

    @Test
    void registrarConNombreDuplicadoSinImportarCapitalizacionResponde409() {
        when(cargoRepository.existsById("C08")).thenReturn(false);
        when(cargoRepository.existsByNombreIgnoreCaseAndCodigoCargoNot("MEDICO GENERAL", "C08")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> servicio.registrar(cargo("C08", "  MEDICO   GENERAL ", true)));

        assertEquals(HttpStatus.CONFLICT, estado(ex));
        verify(cargoRepository, never()).saveAndFlush(any());
    }

    @Test
    void registrarValidaCodigoNombreYEstadoConError400() {
        Cargo sinEstado = cargo("C08", "Nombre valido", true);
        sinEstado.setActivo(null);
        for (Cargo invalido : List.of(
                cargo("AB", "Nombre valido", true),
                cargo("ABCD", "Nombre valido", true),
                cargo("A-1", "Nombre valido", true),
                cargo(null, "Nombre valido", true),
                cargo("C08", "ab", true),
                cargo("C08", "   ", true),
                cargo("C08", null, true),
                cargo("C08", "Medico 2", true),
                cargo("C08", "x".repeat(101), true),
                sinEstado)) {
            ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> servicio.registrar(invalido));
            assertEquals(HttpStatus.BAD_REQUEST, estado(ex), String.valueOf(invalido.getCodigoCargo()) + "/" + invalido.getNombre());
        }
        verify(cargoRepository, never()).saveAndFlush(any());
    }

    // ---------------------------------------------------------------- actualizar

    @Test
    void actualizarUsaElCodigoDeLaRutaSobreElDelCuerpo() {
        when(cargoRepository.findById("C03")).thenReturn(Optional.of(cargo("C03", "Medico general", true)));
        when(cargoRepository.existsByNombreIgnoreCaseAndCodigoCargoNot("Medico clinico", "C03")).thenReturn(false);
        when(cargoRepository.saveAndFlush(any(Cargo.class))).thenAnswer(inv -> inv.getArgument(0));

        Cargo actualizado = servicio.actualizar("c03", cargo("C99", "Medico clinico", false));

        assertEquals("C03", actualizado.getCodigoCargo());
        assertEquals("Medico clinico", actualizado.getNombre());
        assertEquals(false, actualizado.getActivo());
    }

    @Test
    void actualizarCargoInexistenteResponde404() {
        when(cargoRepository.findById("C50")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> servicio.actualizar("C50", cargo("C50", "Cargo nuevo", true)));

        assertEquals(HttpStatus.NOT_FOUND, estado(ex));
    }

    @Test
    void actualizarConNombreDeOtroCargoResponde409() {
        when(cargoRepository.findById("C03")).thenReturn(Optional.of(cargo("C03", "Medico general", true)));
        when(cargoRepository.existsByNombreIgnoreCaseAndCodigoCargoNot("Administrador", "C03")).thenReturn(true);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> servicio.actualizar("C03", cargo("C03", "Administrador", true)));

        assertEquals(HttpStatus.CONFLICT, estado(ex));
        verify(cargoRepository, never()).saveAndFlush(any());
    }

    // ---------------------------------------------------------------- eliminar

    @Test
    void eliminarCargoInexistenteResponde404() {
        when(cargoRepository.findById("C77")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> servicio.eliminar("c77"));

        assertEquals(HttpStatus.NOT_FOUND, estado(ex));
    }

    @Test
    void eliminarCargoConPersonalResponde409YNoBorra() {
        Cargo c01 = cargo("C01", "Administrador", true);
        when(cargoRepository.findById("C01")).thenReturn(Optional.of(c01));
        when(personaRepository.countByCargoCodigoCargo("C01")).thenReturn(2L);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> servicio.eliminar("C01"));

        assertEquals(HttpStatus.CONFLICT, estado(ex));
        assertEquals("El cargo tiene 2 persona(s) asignada(s). Puede marcarlo como inactivo.", ex.getReason());
        verify(cargoRepository, never()).delete(any());
    }

    @Test
    void eliminarCargoSinPersonalLoBorra() {
        Cargo c07 = cargo("C07", "Auxiliar administrativo", false);
        when(cargoRepository.findById("C07")).thenReturn(Optional.of(c07));
        when(personaRepository.countByCargoCodigoCargo("C07")).thenReturn(0L);

        servicio.eliminar(" c07 ");

        verify(cargoRepository).delete(c07);
        verify(cargoRepository).flush();
    }

    // ---------------------------------------------------------------- cambiarEstadoEnLote

    @Test
    void cambiarEstadoEnLoteDeduplicaActualizaYOrdena() {
        Cargo c05 = cargo("C05", "Tecnologo de laboratorio", true);
        Cargo c04 = cargo("C04", "Medico especialista", true);
        when(cargoRepository.findAllById(any())).thenReturn(List.of(c05, c04));
        when(cargoRepository.saveAllAndFlush(any())).thenAnswer(inv -> inv.getArgument(0));

        List<Cargo> resultado = servicio.cambiarEstadoEnLote(codigos("c05", " C04 ", "C05"), false);

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Iterable<String>> consulta = ArgumentCaptor.forClass(Iterable.class);
        verify(cargoRepository).findAllById(consulta.capture());
        List<String> consultados = new ArrayList<>();
        consulta.getValue().forEach(consultados::add);
        assertEquals(List.of("C05", "C04"), consultados, "Deduplicado preservando el orden");
        assertEquals(List.of("C04", "C05"), resultado.stream().map(Cargo::getCodigoCargo).toList());
        assertEquals(false, c04.getActivo());
        assertEquals(false, c05.getActivo());
    }

    @Test
    void cambiarEstadoEnLoteConCodigoInexistenteResponde404SinCambiosParciales() {
        Cargo c04 = cargo("C04", "Medico especialista", true);
        when(cargoRepository.findAllById(any())).thenReturn(List.of(c04));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> servicio.cambiarEstadoEnLote(codigos("C04", "ZZZ"), false));

        assertEquals(HttpStatus.NOT_FOUND, estado(ex));
        assertEquals(true, c04.getActivo(), "El cargo existente no debe cambiar");
        verify(cargoRepository, never()).saveAllAndFlush(any());
        verify(cargoRepository, never()).saveAll(any());
        verify(cargoRepository, never()).save(any());
    }

    @Test
    void cambiarEstadoEnLoteConCodigoInvalidoResponde400SinConsultarNiGuardar() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> servicio.cambiarEstadoEnLote(codigos("C04", "AB"), true));

        assertEquals(HttpStatus.BAD_REQUEST, estado(ex));
        verify(cargoRepository, never()).findAllById(any());
        verify(cargoRepository, never()).saveAllAndFlush(any());
    }

    @Test
    void cambiarEstadoEnLoteSinCodigosResponde400() {
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> servicio.cambiarEstadoEnLote(List.of(), true));

        assertEquals(HttpStatus.BAD_REQUEST, estado(ex));
    }

    @Test
    void registrarDevuelveLaMismaInstanciaGuardada() {
        Cargo nuevo = cargo("C09", "Odontologo", true);
        when(cargoRepository.existsById(eq("C09"))).thenReturn(false);
        when(cargoRepository.existsByNombreIgnoreCaseAndCodigoCargoNot("Odontologo", "C09")).thenReturn(false);
        when(cargoRepository.saveAndFlush(nuevo)).thenReturn(nuevo);

        assertSame(nuevo, servicio.registrar(nuevo));
    }
}
