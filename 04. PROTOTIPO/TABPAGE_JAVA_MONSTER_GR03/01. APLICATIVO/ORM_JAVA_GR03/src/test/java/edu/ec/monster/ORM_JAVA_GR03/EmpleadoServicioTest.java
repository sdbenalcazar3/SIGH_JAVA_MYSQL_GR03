package edu.ec.monster.ORM_JAVA_GR03;

import edu.ec.monster.ORM_JAVA_GR03.dto.DatoAcademicoDto;
import edu.ec.monster.ORM_JAVA_GR03.dto.DatoFamiliarDto;
import edu.ec.monster.ORM_JAVA_GR03.dto.EmpleadoDetalle;
import edu.ec.monster.ORM_JAVA_GR03.dto.EmpleadoGuardarRequest;
import edu.ec.monster.ORM_JAVA_GR03.dto.EmpleadoResumen;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Cargo;
import edu.ec.monster.ORM_JAVA_GR03.modelo.DatoAcademico;
import edu.ec.monster.ORM_JAVA_GR03.modelo.DatoFamiliar;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Departamento;
import edu.ec.monster.ORM_JAVA_GR03.modelo.EstadoCivil;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Genero;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Persona;
import edu.ec.monster.ORM_JAVA_GR03.repository.CargoRepository;
import edu.ec.monster.ORM_JAVA_GR03.repository.DatoAcademicoRepository;
import edu.ec.monster.ORM_JAVA_GR03.repository.DatoFamiliarRepository;
import edu.ec.monster.ORM_JAVA_GR03.repository.DepartamentoRepository;
import edu.ec.monster.ORM_JAVA_GR03.repository.EstadoCivilRepository;
import edu.ec.monster.ORM_JAVA_GR03.repository.GeneroRepository;
import edu.ec.monster.ORM_JAVA_GR03.repository.PersonaRepository;
import edu.ec.monster.ORM_JAVA_GR03.servicio.PersonaServicio;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pruebas unitarias de PersonaServicio (empleados) sin base de datos: repositorios simulados y fotos en un @TempDir. */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class EmpleadoServicioTest {

    private static final byte[] PNG = {(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0, 0, 0, 0};
    private static final byte[] JPEG = {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0, 0x10, 0x4A, 0x46};
    private static final byte[] WEBP = {0x52, 0x49, 0x46, 0x46, 0x24, 0, 0, 0, 0x57, 0x45, 0x42, 0x50, 0x56, 0x50};

    @Mock private PersonaRepository personaRepository;
    @Mock private DepartamentoRepository departamentoRepository;
    @Mock private CargoRepository cargoRepository;
    @Mock private GeneroRepository generoRepository;
    @Mock private EstadoCivilRepository estadoCivilRepository;
    @Mock private DatoAcademicoRepository academicoRepository;
    @Mock private DatoFamiliarRepository familiarRepository;
    @Captor private ArgumentCaptor<Iterable<DatoAcademico>> academicosCaptor;
    @Captor private ArgumentCaptor<Iterable<DatoFamiliar>> familiaresCaptor;
    @Captor private ArgumentCaptor<Persona> personaCaptor;

    @TempDir Path fotos;

    private PersonaServicio servicio;

    @BeforeEach
    void preparar() {
        servicio = new PersonaServicio(personaRepository, departamentoRepository, cargoRepository,
                generoRepository, estadoCivilRepository, academicoRepository, familiarRepository, fotos.toString());
        Departamento d01 = new Departamento();
        d01.setCodigoDepartamento("D01");
        d01.setNombre("Administracion");
        Cargo c01 = new Cargo();
        c01.setCodigoCargo("C01");
        c01.setNombre("Analista");
        Genero genero = new Genero();
        genero.setCodigoGenero("M");
        genero.setNombre("Masculino");
        EstadoCivil soltero = new EstadoCivil();
        soltero.setCodigoEstadoCivil("S");
        soltero.setNombre("Soltero");
        when(departamentoRepository.findById("D01")).thenReturn(Optional.of(d01));
        when(cargoRepository.findById("C01")).thenReturn(Optional.of(c01));
        when(generoRepository.findById("M")).thenReturn(Optional.of(genero));
        when(estadoCivilRepository.findById("S")).thenReturn(Optional.of(soltero));
        when(personaRepository.saveAndFlush(any(Persona.class))).then(returnsFirstArg());
        when(personaRepository.codigosCorrelativos()).thenReturn(List.of());
    }

    // ---------------------------------------------------------------- utilidades

    /** Solicitud valida por omision; cada prueba cambia solo el campo que quiere romper. */
    private static final class Datos {
        String codigo = "P00007";
        String codigoExterno = null;
        String apellidos = "Perez   Lopez";
        String nombres = "Juan Carlos";
        String correo = "juan@sigh.ec";
        String tipoDocumento = "CEDULA";
        String numeroDocumento = "1712345678";
        String telefono = "0991234567";
        String tipoSangre = "O+";
        BigDecimal peso = new BigDecimal("70.5");
        BigDecimal estatura = new BigDecimal("1.75");
        LocalDate fechaNacimiento = LocalDate.of(1990, 1, 1);
        String codigoGenero = "M";
        String codigoEstadoCivil = "S";
        String codigoDepartamento = "D01";
        String codigoCargo = "C01";
        List<DatoAcademicoDto> academicos = List.of(new DatoAcademicoDto(null, "Tercer Nivel", "UCE", "Ingeniero"));
        List<DatoFamiliarDto> familiares = List.of(
                new DatoFamiliarDto(null, "Hijo/a", "Perez", "Ana", LocalDate.of(2015, 5, 5)));

        EmpleadoGuardarRequest build() {
            return new EmpleadoGuardarRequest(codigo, codigoExterno, apellidos, nombres, "Analista", null, true,
                    tipoDocumento, numeroDocumento, fechaNacimiento, telefono, correo, codigoGenero,
                    codigoEstadoCivil, tipoSangre, peso, estatura, codigoDepartamento, codigoCargo,
                    academicos, familiares);
        }
    }

    private static HttpStatus estado(ResponseStatusException ex) {
        return HttpStatus.valueOf(ex.getStatusCode().value());
    }

    private static HttpStatus estadoDe(org.junit.jupiter.api.function.Executable accion) {
        return estado(assertThrows(ResponseStatusException.class, accion));
    }

    private static Persona persona(String codigo) {
        Persona p = new Persona();
        p.setCodigoPersona(codigo);
        p.setCodigoExterno("EXT0000001");
        p.setNombres("Ana");
        p.setApellidos("Ruiz");
        p.setFuncion("Medico");
        p.setActivo(true);
        return p;
    }

    private static <T> List<T> lista(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
    }

    private static DatoAcademico academico(Long id, Persona persona, String nivel) {
        DatoAcademico a = new DatoAcademico(persona, nivel, "Institucion", "Titulo");
        a.setCodigoAcademico(id);
        return a;
    }

    private static DatoFamiliar familiar(Long id, Persona persona, String nombres) {
        DatoFamiliar f = new DatoFamiliar(persona, "Hijo/a", "Ruiz", nombres, null);
        f.setCodigoFamiliar(id);
        return f;
    }

    private long archivosEnDirectorio() throws IOException {
        try (var flujo = Files.list(fotos)) {
            return flujo.count();
        }
    }

    private void sinEscrituras() {
        verify(personaRepository, never()).saveAndFlush(any(Persona.class));
        verify(academicoRepository, never()).saveAll(any());
        verify(familiarRepository, never()).saveAll(any());
    }

    // ---------------------------------------------------------------- correlativo

    @Test
    void siguienteCodigoSinEmpleadosCorrelativosEsP00001() {
        assertEquals("P00001", servicio.generarSiguienteCodigo());
    }

    @Test
    void siguienteCodigoEsMaximoMasUnoIgnorandoCodigosNoCorrelativos() {
        when(personaRepository.codigosCorrelativos()).thenReturn(List.of("P00001", "P00006", "PXXXXX", "P0001A"));
        assertEquals("P00007", servicio.generarSiguienteCodigo());
    }

    @Test
    void siguienteCodigoUsaElPrimerHuecoCuandoExisteP99999() {
        when(personaRepository.codigosCorrelativos()).thenReturn(List.of("P00001", "P00003", "P99999"));
        assertEquals("P00002", servicio.generarSiguienteCodigo());
    }

    @Test
    void siguienteCodigoConElRangoCompletoResponde409() {
        List<String> todos = IntStream.rangeClosed(1, 99999).mapToObj(n -> String.format("P%05d", n)).toList();
        when(personaRepository.codigosCorrelativos()).thenReturn(todos);
        assertEquals(HttpStatus.CONFLICT, estadoDe(() -> servicio.generarSiguienteCodigo()));
    }

    // ---------------------------------------------------------------- listado y detalle

    @Test
    void listarResumenSinFiltrosNoRestringeNadaYAgregaTotales() {
        Persona ana = persona("P00001");
        Departamento d = new Departamento();
        d.setCodigoDepartamento("D01");
        d.setNombre("Administracion");
        ana.setDepartamento(d);
        when(personaRepository.filtrar("%", true, false, "", "")).thenReturn(List.of(ana));
        when(academicoRepository.contarPorPersona()).thenReturn(List.<Object[]>of(new Object[] {"P00001", 2L}));
        when(familiarRepository.contarPorPersona()).thenReturn(List.<Object[]>of(new Object[] {"P00001", 1L}));

        List<EmpleadoResumen> resumen = servicio.listarResumen(null, null, "  ", null);

        assertEquals(1, resumen.size());
        assertEquals("P00001", resumen.get(0).codigoPersona());
        assertEquals("D01", resumen.get(0).codigoDepartamento());
        assertEquals(2L, resumen.get(0).totalAcademicos());
        assertEquals(1L, resumen.get(0).totalFamiliares());
    }

    @Test
    void listarResumenEscapaComodinesYNormalizaFiltros() {
        servicio.listarResumen("  50%_! ", "d01", "c02", true);
        verify(personaRepository).filtrar("%50!%!_!!%", false, true, "D01", "C02");
    }

    @Test
    void listarResumenInactivosUsaActivoFalso() {
        servicio.listarResumen("Ana", "", "", false);
        verify(personaRepository).filtrar("%ana%", false, false, "", "");
    }

    @Test
    void obtenerDetalleConCodigoInvalidoResponde400() {
        assertEquals(HttpStatus.BAD_REQUEST, estadoDe(() -> servicio.obtenerDetalle("P-001")));
        assertEquals(HttpStatus.BAD_REQUEST, estadoDe(() -> servicio.obtenerDetalle("../etc")));
        assertEquals(HttpStatus.BAD_REQUEST, estadoDe(() -> servicio.obtenerDetalle(null)));
    }

    @Test
    void obtenerDetalleInexistenteResponde404() {
        when(personaRepository.findById("P00009")).thenReturn(Optional.empty());
        assertEquals(HttpStatus.NOT_FOUND, estadoDe(() -> servicio.obtenerDetalle("p00009")));
    }

    @Test
    void obtenerDetalleIncluyeHijosYCuentaDeUsuario() {
        Persona ana = persona("P00001");
        when(personaRepository.findById("P00001")).thenReturn(Optional.of(ana));
        when(personaRepository.contarUsuariosPorPersona("P00001")).thenReturn(1L);
        when(academicoRepository.findByPersonaCodigoPersonaOrderByCodigoAcademicoAsc("P00001"))
                .thenReturn(List.of(academico(5L, ana, "Maestría")));
        when(familiarRepository.findByPersonaCodigoPersonaOrderByCodigoFamiliarAsc("P00001"))
                .thenReturn(List.of(familiar(8L, ana, "Luis")));

        EmpleadoDetalle detalle = servicio.obtenerDetalle(" p00001 ");

        assertEquals("P00001", detalle.codigoPersona());
        assertTrue(detalle.tieneUsuario());
        assertEquals(5L, detalle.academicos().get(0).codigoAcademico());
        assertEquals("Maestría", detalle.academicos().get(0).nivel());
        assertEquals("Luis", detalle.familiares().get(0).nombres());
    }

    // ---------------------------------------------------------------- registrarFicha

    @Test
    void registrarFichaNormalizaGuardaYAutogeneraElCodigoExterno() {
        EmpleadoDetalle detalle = servicio.registrarFicha(new Datos().build());

        verify(personaRepository).saveAndFlush(personaCaptor.capture());
        Persona guardada = personaCaptor.getValue();
        assertEquals("P00007", guardada.getCodigoPersona());
        assertEquals("EXT0000007", guardada.getCodigoExterno());
        assertEquals("Perez Lopez", guardada.getApellidos(), "Los espacios se colapsan");
        assertEquals(new BigDecimal("70.50"), guardada.getPeso());
        assertEquals("D01", guardada.getDepartamento().getCodigoDepartamento());
        assertEquals("M", guardada.getGenero().getCodigoGenero());
        verify(academicoRepository).saveAll(academicosCaptor.capture());
        assertEquals("UCE", lista(academicosCaptor.getValue()).get(0).getInstitucion());
        assertSame(guardada, lista(academicosCaptor.getValue()).get(0).getPersona());
        verify(familiarRepository).saveAll(familiaresCaptor.capture());
        assertEquals("Ana", lista(familiaresCaptor.getValue()).get(0).getNombres());
        assertEquals("P00007", detalle.codigoPersona());
    }

    @Test
    void registrarFichaConcodigoNormalizadoEnMayusculas() {
        Datos datos = new Datos();
        datos.codigo = " pt0001 ";
        servicio.registrarFicha(datos.build());
        verify(personaRepository).saveAndFlush(personaCaptor.capture());
        assertEquals("PT0001", personaCaptor.getValue().getCodigoPersona());
        assertEquals("EXT0000001", personaCaptor.getValue().getCodigoExterno());
    }

    @Test
    void registrarFichaConservaElCodigoExternoIndicado() {
        Datos datos = new Datos();
        datos.codigoExterno = "EXT0000099";
        servicio.registrarFicha(datos.build());
        verify(personaRepository).saveAndFlush(personaCaptor.capture());
        assertEquals("EXT0000099", personaCaptor.getValue().getCodigoExterno());
    }

    @Test
    void registrarFichaConCodigoExistenteResponde409() {
        when(personaRepository.existsById("P00007")).thenReturn(true);
        assertEquals(HttpStatus.CONFLICT, estadoDe(() -> servicio.registrarFicha(new Datos().build())));
        sinEscrituras();
    }

    @Test
    void registrarFichaNulaOCodigoInvalidoResponde400() {
        assertEquals(HttpStatus.BAD_REQUEST, estadoDe(() -> servicio.registrarFicha(null)));
        Datos datos = new Datos();
        datos.codigo = "P0000007";
        assertEquals(HttpStatus.BAD_REQUEST, estadoDe(() -> servicio.registrarFicha(datos.build())));
        sinEscrituras();
    }

    @Test
    void registrarFichaRechazaDatosInvalidosConError400() {
        List<Datos> invalidos = new ArrayList<>();
        Datos a = new Datos(); a.nombres = "Juan 2"; invalidos.add(a);
        Datos b = new Datos(); b.apellidos = "P"; invalidos.add(b);
        Datos c = new Datos(); c.tipoSangre = "Z+"; invalidos.add(c);
        Datos d = new Datos(); d.peso = new BigDecimal("10"); invalidos.add(d);
        Datos e = new Datos(); e.estatura = new BigDecimal("3.10"); invalidos.add(e);
        Datos f = new Datos(); f.numeroDocumento = "17123"; invalidos.add(f);
        Datos g = new Datos(); g.fechaNacimiento = LocalDate.now().plusDays(1); invalidos.add(g);
        Datos h = new Datos(); h.telefono = "abc"; invalidos.add(h);
        Datos i = new Datos(); i.correo = "sin-arroba"; invalidos.add(i);
        Datos j = new Datos(); j.codigoDepartamento = " "; invalidos.add(j);
        Datos k = new Datos(); k.codigoCargo = null; invalidos.add(k);
        for (Datos datos : invalidos) {
            assertEquals(HttpStatus.BAD_REQUEST, estadoDe(() -> servicio.registrarFicha(datos.build())));
        }
        sinEscrituras();
    }

    @Test
    void registrarFichaConReferenciasInexistentesResponde404() {
        Datos departamento = new Datos(); departamento.codigoDepartamento = "ZZZ";
        Datos cargo = new Datos(); cargo.codigoCargo = "ZZZ";
        Datos genero = new Datos(); genero.codigoGenero = "X";
        Datos civil = new Datos(); civil.codigoEstadoCivil = "X";
        for (Datos datos : List.of(departamento, cargo, genero, civil)) {
            assertEquals(HttpStatus.NOT_FOUND, estadoDe(() -> servicio.registrarFicha(datos.build())));
        }
        sinEscrituras();
    }

    @Test
    void registrarFichaConDatosUnicosRepetidosResponde409() {
        when(personaRepository.existsByCorreoIgnoreCaseAndCodigoPersonaNot("juan@sigh.ec", "P00007")).thenReturn(true);
        assertEquals(HttpStatus.CONFLICT, estadoDe(() -> servicio.registrarFicha(new Datos().build())));

        when(personaRepository.existsByCorreoIgnoreCaseAndCodigoPersonaNot("juan@sigh.ec", "P00007")).thenReturn(false);
        when(personaRepository.existsByNumeroDocumentoAndCodigoPersonaNot("1712345678", "P00007")).thenReturn(true);
        assertEquals(HttpStatus.CONFLICT, estadoDe(() -> servicio.registrarFicha(new Datos().build())));

        when(personaRepository.existsByNumeroDocumentoAndCodigoPersonaNot("1712345678", "P00007")).thenReturn(false);
        when(personaRepository.existsByCodigoExternoIgnoreCaseAndCodigoPersonaNot("EXT0000007", "P00007")).thenReturn(true);
        assertEquals(HttpStatus.CONFLICT, estadoDe(() -> servicio.registrarFicha(new Datos().build())));
        sinEscrituras();
    }

    @Test
    void registrarFichaConUnHijoInvalidoNoGuardaNada() {
        Datos academico = new Datos();
        academico.academicos = List.of(new DatoAcademicoDto(null, "Tercer Nivel", "UCE", "Ingeniero"),
                new DatoAcademicoDto(null, "Maestría", "UCE", "  "));
        assertEquals(HttpStatus.BAD_REQUEST, estadoDe(() -> servicio.registrarFicha(academico.build())));

        Datos familiar = new Datos();
        familiar.familiares = List.of(new DatoFamiliarDto(null, "Hijo/a", "Perez", "Ana", LocalDate.now().plusDays(2)));
        assertEquals(HttpStatus.BAD_REQUEST, estadoDe(() -> servicio.registrarFicha(familiar.build())));
        sinEscrituras();
    }

    @Test
    void registrarFichaSinListasGuardaSoloElEmpleado() {
        Datos datos = new Datos();
        datos.academicos = null;
        datos.familiares = null;
        EmpleadoDetalle detalle = servicio.registrarFicha(datos.build());
        assertTrue(detalle.academicos().isEmpty());
        assertTrue(detalle.familiares().isEmpty());
    }

    // ---------------------------------------------------------------- actualizarFicha

    @Test
    void actualizarFichaInexistenteResponde404() {
        when(personaRepository.findById("P00001")).thenReturn(Optional.empty());
        assertEquals(HttpStatus.NOT_FOUND, estadoDe(() -> servicio.actualizarFicha("P00001", new Datos().build())));
        sinEscrituras();
    }

    @Test
    void actualizarFichaElCodigoDeLaRutaPrevaleceYConservaLaFoto() {
        Persona existente = persona("P00001");
        existente.setFoto("/uploads/empleados/P00001_1.png");
        when(personaRepository.findById("P00001")).thenReturn(Optional.of(existente));

        Datos datos = new Datos();
        datos.codigo = "P00099";
        servicio.actualizarFicha("p00001", datos.build());

        verify(personaRepository).saveAndFlush(personaCaptor.capture());
        assertSame(existente, personaCaptor.getValue());
        assertEquals("P00001", existente.getCodigoPersona());
        assertEquals("Perez Lopez", existente.getApellidos());
        assertEquals("/uploads/empleados/P00001_1.png", existente.getFoto());
        assertEquals("EXT0000001", existente.getCodigoExterno(), "Sin codigo externo en la solicitud se conserva el actual");
    }

    @Test
    void actualizarFichaSincronizaAcademicosYFamiliaresExactamente() {
        Persona existente = persona("P00001");
        when(personaRepository.findById("P00001")).thenReturn(Optional.of(existente));
        DatoAcademico a1 = academico(1L, existente, "Bachiller");
        DatoAcademico a2 = academico(2L, existente, "Tercer Nivel");
        DatoFamiliar f1 = familiar(10L, existente, "Luis");
        when(academicoRepository.findByPersonaCodigoPersonaOrderByCodigoAcademicoAsc("P00001"))
                .thenReturn(List.of(a1, a2));
        when(familiarRepository.findByPersonaCodigoPersonaOrderByCodigoFamiliarAsc("P00001"))
                .thenReturn(List.of(f1));

        Datos datos = new Datos();
        datos.academicos = List.of(new DatoAcademicoDto(2L, "Maestría", "EPN", "Magister"),
                new DatoAcademicoDto(null, "Doctorado", "UCE", "PhD"),
                new DatoAcademicoDto(999L, "Especialidad", "UCE", "Esp"));
        datos.familiares = List.of();
        servicio.actualizarFicha("P00001", datos.build());

        verify(academicoRepository).deleteAll(academicosCaptor.capture());
        assertEquals(List.of(a1), lista(academicosCaptor.getValue()), "Se elimina el omitido");
        verify(academicoRepository).saveAll(academicosCaptor.capture());
        List<DatoAcademico> guardados = lista(academicosCaptor.getValue());
        assertEquals(3, guardados.size());
        assertSame(a2, guardados.get(0), "El id recibido actualiza el registro existente");
        assertEquals("Maestría", a2.getNivel());
        assertEquals("EPN", a2.getInstitucion());
        assertNull(guardados.get(1).getCodigoAcademico());
        assertNull(guardados.get(2).getCodigoAcademico(), "Un id ajeno al empleado se trata como registro nuevo");
        verify(familiarRepository).deleteAll(familiaresCaptor.capture());
        assertEquals(List.of(f1), lista(familiaresCaptor.getValue()));
    }

    @Test
    void actualizarFichaConHijoInvalidoNoModificaNingunaTabla() {
        Persona existente = persona("P00001");
        when(personaRepository.findById("P00001")).thenReturn(Optional.of(existente));
        Datos datos = new Datos();
        datos.familiares = List.of(new DatoFamiliarDto(null, " ", "Perez", "Ana", null));
        assertEquals(HttpStatus.BAD_REQUEST, estadoDe(() -> servicio.actualizarFicha("P00001", datos.build())));
        sinEscrituras();
        verify(academicoRepository, never()).deleteAll(any());
        verify(familiarRepository, never()).deleteAll(any());
    }

    // ---------------------------------------------------------------- eliminar

    @Test
    void eliminarEmpleadoConCuentaDeUsuarioResponde409SinTocarNada() {
        when(personaRepository.findById("P00001")).thenReturn(Optional.of(persona("P00001")));
        when(personaRepository.contarUsuariosPorPersona("P00001")).thenReturn(1L);
        assertEquals(HttpStatus.CONFLICT, estadoDe(() -> servicio.eliminar("P00001")));
        verify(academicoRepository, never()).deleteByPersonaCodigoPersona("P00001");
        verify(familiarRepository, never()).deleteByPersonaCodigoPersona("P00001");
        verify(personaRepository, never()).delete(any(Persona.class));
    }

    @Test
    void eliminarInexistenteOCodigoInvalido() {
        when(personaRepository.findById("P00009")).thenReturn(Optional.empty());
        assertEquals(HttpStatus.NOT_FOUND, estadoDe(() -> servicio.eliminar("P00009")));
        assertEquals(HttpStatus.BAD_REQUEST, estadoDe(() -> servicio.eliminar("..")));
    }

    @Test
    void eliminarBorraHijosRegistroYFotoFisica() throws IOException {
        Persona ana = persona("P00001");
        ana.setFoto("/uploads/empleados/P00001_1.png");
        Path archivo = Files.write(fotos.resolve("P00001_1.png"), PNG);
        Path ajeno = Files.write(fotos.resolve("P00002_1.png"), PNG);
        when(personaRepository.findById("P00001")).thenReturn(Optional.of(ana));

        servicio.eliminar("p00001");

        verify(academicoRepository).deleteByPersonaCodigoPersona("P00001");
        verify(familiarRepository).deleteByPersonaCodigoPersona("P00001");
        verify(personaRepository).delete(ana);
        assertFalse(Files.exists(archivo));
        assertTrue(Files.exists(ajeno), "Solo se borra la foto del empleado eliminado");
    }

    @Test
    void eliminarNuncaBorraArchivosFueraDelDirectorioDeFotos() throws IOException {
        Path externo = Files.createTempFile("sigh-externo", ".png");
        try {
            Persona ana = persona("P00001");
            ana.setFoto("/uploads/empleados/../../" + externo.getFileName());
            when(personaRepository.findById("P00001")).thenReturn(Optional.of(ana));
            servicio.eliminar("P00001");
            assertTrue(Files.exists(externo));
        } finally {
            Files.deleteIfExists(externo);
        }
    }

    // ---------------------------------------------------------------- CRUD granular

    @Test
    void agregarAcademicoValidaYDevuelveElRegistroGuardado() {
        Persona ana = persona("P00001");
        when(personaRepository.findById("P00001")).thenReturn(Optional.of(ana));
        when(academicoRepository.saveAndFlush(any(DatoAcademico.class))).then(invocacion -> {
            DatoAcademico a = invocacion.getArgument(0);
            a.setCodigoAcademico(33L);
            return a;
        });

        DatoAcademicoDto dto = servicio.agregarAcademico("P00001",
                new DatoAcademicoDto(null, " Tercer   Nivel ", "UCE", "Ingeniero"));

        assertEquals(33L, dto.codigoAcademico());
        assertEquals("Tercer Nivel", dto.nivel());
        assertEquals(HttpStatus.BAD_REQUEST, estadoDe(() -> servicio.agregarAcademico("P00001",
                new DatoAcademicoDto(null, "", "UCE", "Ingeniero"))));
        assertEquals(HttpStatus.BAD_REQUEST, estadoDe(() -> servicio.agregarAcademico("P00001", null)));
    }

    @Test
    void agregarFamiliarValidaFechaYEmpleado() {
        Persona ana = persona("P00001");
        when(personaRepository.findById("P00001")).thenReturn(Optional.of(ana));
        when(personaRepository.findById("P00009")).thenReturn(Optional.empty());
        when(familiarRepository.saveAndFlush(any(DatoFamiliar.class))).then(returnsFirstArg());

        DatoFamiliarDto dto = servicio.agregarFamiliar("P00001",
                new DatoFamiliarDto(null, "Madre", "Ruiz", "Rosa", LocalDate.of(1960, 3, 3)));
        assertEquals("Rosa", dto.nombres());
        assertEquals(HttpStatus.BAD_REQUEST, estadoDe(() -> servicio.agregarFamiliar("P00001",
                new DatoFamiliarDto(null, "Madre", "Ruiz", "Rosa", LocalDate.now().plusDays(1)))));
        assertEquals(HttpStatus.NOT_FOUND, estadoDe(() -> servicio.agregarFamiliar("P00009",
                new DatoFamiliarDto(null, "Madre", "Ruiz", "Rosa", null))));
    }

    @Test
    void eliminarAcademicoVerificaQuePertenezcaAlEmpleado() {
        Persona ana = persona("P00001");
        Persona otra = persona("P00002");
        DatoAcademico propio = academico(1L, ana, "Bachiller");
        DatoAcademico ajeno = academico(2L, otra, "Bachiller");
        when(personaRepository.findById("P00001")).thenReturn(Optional.of(ana));
        when(academicoRepository.findById(1L)).thenReturn(Optional.of(propio));
        when(academicoRepository.findById(2L)).thenReturn(Optional.of(ajeno));
        when(academicoRepository.findById(3L)).thenReturn(Optional.empty());

        assertEquals(HttpStatus.NOT_FOUND, estadoDe(() -> servicio.eliminarAcademico("P00001", 2L)));
        assertEquals(HttpStatus.NOT_FOUND, estadoDe(() -> servicio.eliminarAcademico("P00001", 3L)));
        assertEquals(HttpStatus.NOT_FOUND, estadoDe(() -> servicio.eliminarAcademico("P00001", null)));
        verify(academicoRepository, never()).delete(ajeno);

        servicio.eliminarAcademico("p00001", 1L);
        verify(academicoRepository).delete(propio);
    }

    @Test
    void eliminarFamiliarVerificaQuePertenezcaAlEmpleado() {
        Persona ana = persona("P00001");
        DatoFamiliar propio = familiar(1L, ana, "Luis");
        DatoFamiliar ajeno = familiar(2L, persona("P00002"), "Eva");
        when(personaRepository.findById("P00001")).thenReturn(Optional.of(ana));
        when(familiarRepository.findById(1L)).thenReturn(Optional.of(propio));
        when(familiarRepository.findById(2L)).thenReturn(Optional.of(ajeno));

        assertEquals(HttpStatus.NOT_FOUND, estadoDe(() -> servicio.eliminarFamiliar("P00001", 2L)));
        verify(familiarRepository, never()).delete(ajeno);
        servicio.eliminarFamiliar("P00001", 1L);
        verify(familiarRepository).delete(propio);
    }

    // ---------------------------------------------------------------- foto

    private void empleadoExistente(Persona p) {
        when(personaRepository.findById(p.getCodigoPersona())).thenReturn(Optional.of(p));
    }

    private static byte[] conRelleno(byte[] cabecera, int total) {
        byte[] bytes = new byte[total];
        System.arraycopy(cabecera, 0, bytes, 0, cabecera.length);
        return bytes;
    }

    @Test
    void guardarFotoPngValidaEscribeElArchivoYActualizaElEmpleado() throws IOException {
        Persona ana = persona("P00001");
        empleadoExistente(ana);

        String url = servicio.guardarFoto("p00001", new MockMultipartFile("archivo", "mi foto.PNG", "image/png", PNG));

        assertTrue(url.matches("/uploads/empleados/P00001_\\d+\\.png"), url);
        assertEquals(url, ana.getFoto());
        Path escrito = fotos.resolve(url.substring("/uploads/empleados/".length()));
        assertTrue(Files.exists(escrito));
        assertEquals(PNG.length, Files.size(escrito));
        verify(personaRepository).saveAndFlush(ana);
    }

    @Test
    void guardarFotoJpegYWebpSonValidas() throws IOException {
        empleadoExistente(persona("P00001"));
        String jpg = servicio.guardarFoto("P00001", new MockMultipartFile("archivo", "a.jpg", "image/jpeg", JPEG));
        String jpeg = servicio.guardarFoto("P00001", new MockMultipartFile("archivo", "a.jpeg", "image/jpeg", JPEG));
        String webp = servicio.guardarFoto("P00001", new MockMultipartFile("archivo", "a.webp", "image/webp", WEBP));
        assertTrue(jpg.endsWith(".jpg"));
        assertTrue(jpeg.endsWith(".jpeg"));
        assertTrue(webp.endsWith(".webp"));
    }

    @Test
    void guardarFotoReemplazaYBorraLaFotoAnterior() throws IOException {
        Persona ana = persona("P00001");
        ana.setFoto("/uploads/empleados/P00001_1.png");
        Path anterior = Files.write(fotos.resolve("P00001_1.png"), PNG);
        empleadoExistente(ana);

        String nueva = servicio.guardarFoto("P00001", new MockMultipartFile("archivo", "n.png", "image/png", PNG));

        assertFalse(Files.exists(anterior));
        assertTrue(Files.exists(fotos.resolve(nueva.substring("/uploads/empleados/".length()))));
        assertEquals(1, archivosEnDirectorio());
    }

    @Test
    void guardarFotoDeEmpleadoInexistenteResponde404() throws IOException {
        when(personaRepository.findById("P00009")).thenReturn(Optional.empty());
        assertEquals(HttpStatus.NOT_FOUND, estadoDe(() -> servicio.guardarFoto("P00009",
                new MockMultipartFile("archivo", "a.png", "image/png", PNG))));
        assertEquals(0, archivosEnDirectorio());
    }

    @Test
    void guardarFotoRechazaArchivoVacioONulo() throws IOException {
        empleadoExistente(persona("P00001"));
        assertEquals(HttpStatus.BAD_REQUEST, estadoDe(() -> servicio.guardarFoto("P00001", null)));
        assertEquals(HttpStatus.BAD_REQUEST, estadoDe(() -> servicio.guardarFoto("P00001",
                new MockMultipartFile("archivo", "a.png", "image/png", new byte[0]))));
        assertEquals(0, archivosEnDirectorio());
    }

    @Test
    void guardarFotoMayorADosMegabytesResponde413SinEscribirNada() throws IOException {
        empleadoExistente(persona("P00001"));
        byte[] grande = conRelleno(PNG, 2 * 1024 * 1024 + 1);
        assertEquals(HttpStatus.CONTENT_TOO_LARGE, estadoDe(() -> servicio.guardarFoto("P00001",
                new MockMultipartFile("archivo", "a.png", "image/png", grande))));
        assertEquals(0, archivosEnDirectorio());
        verify(personaRepository, never()).saveAndFlush(any(Persona.class));
    }

    @Test
    void guardarFotoExactamenteDeDosMegabytesEsValida() {
        empleadoExistente(persona("P00001"));
        byte[] limite = conRelleno(PNG, 2 * 1024 * 1024);
        assertTrue(servicio.guardarFoto("P00001", new MockMultipartFile("archivo", "a.png", "image/png", limite))
                .endsWith(".png"));
    }

    @Test
    void guardarFotoRechazaExtensionYMimeNoPermitidos() throws IOException {
        empleadoExistente(persona("P00001"));
        for (String nombre : new String[] {"virus.exe", "nota.txt", "sinextension", "imagen.gif", "doble.png.exe"}) {
            assertEquals(HttpStatus.BAD_REQUEST, estadoDe(() -> servicio.guardarFoto("P00001",
                    new MockMultipartFile("archivo", nombre, "image/png", PNG))), nombre);
        }
        assertEquals(HttpStatus.BAD_REQUEST, estadoDe(() -> servicio.guardarFoto("P00001",
                new MockMultipartFile("archivo", "a.png", "text/plain", PNG))));
        assertEquals(HttpStatus.BAD_REQUEST, estadoDe(() -> servicio.guardarFoto("P00001",
                new MockMultipartFile("archivo", "a.png", null, PNG))));
        assertEquals(0, archivosEnDirectorio());
    }

    @Test
    void guardarFotoRechazaMagicBytesFalsosSinEscribirArchivos() throws IOException {
        empleadoExistente(persona("P00001"));
        byte[] ejecutable = "MZ\u0090\u0000 programa renombrado".getBytes();
        byte[] texto = "esto no es una imagen".getBytes();
        byte[] riffSinWebp = {0x52, 0x49, 0x46, 0x46, 0, 0, 0, 0, 0x41, 0x56, 0x49, 0x20, 0};
        assertEquals(HttpStatus.BAD_REQUEST, estadoDe(() -> servicio.guardarFoto("P00001",
                new MockMultipartFile("archivo", "a.png", "image/png", ejecutable))));
        assertEquals(HttpStatus.BAD_REQUEST, estadoDe(() -> servicio.guardarFoto("P00001",
                new MockMultipartFile("archivo", "a.jpg", "image/jpeg", texto))));
        assertEquals(HttpStatus.BAD_REQUEST, estadoDe(() -> servicio.guardarFoto("P00001",
                new MockMultipartFile("archivo", "a.webp", "image/webp", riffSinWebp))));
        assertEquals(0, archivosEnDirectorio());
        verify(personaRepository, never()).saveAndFlush(any(Persona.class));
    }

    @Test
    void guardarFotoRechazaContenidoQueNoCoincideConLaExtensionOElMime() throws IOException {
        empleadoExistente(persona("P00001"));
        assertEquals(HttpStatus.BAD_REQUEST, estadoDe(() -> servicio.guardarFoto("P00001",
                new MockMultipartFile("archivo", "a.png", "image/png", JPEG))));
        assertEquals(HttpStatus.BAD_REQUEST, estadoDe(() -> servicio.guardarFoto("P00001",
                new MockMultipartFile("archivo", "a.png", "image/jpeg", PNG))));
        assertEquals(0, archivosEnDirectorio());
    }

    @Test
    void guardarFotoBloqueaPathTraversalEnNombreYEnCodigo() throws IOException {
        empleadoExistente(persona("P00001"));
        for (String nombre : new String[] {"../../evil.png", "..\\..\\evil.png", "/etc/passwd.png", "C:\\x\\a.png",
                "a/../b.png", "..png"}) {
            assertEquals(HttpStatus.BAD_REQUEST, estadoDe(() -> servicio.guardarFoto("P00001",
                    new MockMultipartFile("archivo", nombre, "image/png", PNG))), nombre);
        }
        for (String codigo : new String[] {"../x", "..\\x", "P0/001", "../../etc"}) {
            assertEquals(HttpStatus.BAD_REQUEST, estadoDe(() -> servicio.guardarFoto(codigo,
                    new MockMultipartFile("archivo", "a.png", "image/png", PNG))), codigo);
        }
        assertEquals(0, archivosEnDirectorio());
        assertFalse(Files.exists(fotos.getParent().resolve("evil.png")));
    }

    @Test
    void eliminarFotoBorraElArchivoYLimpiaElCampo() throws IOException {
        Persona ana = persona("P00001");
        ana.setFoto("/uploads/empleados/P00001_5.png");
        Path archivo = Files.write(fotos.resolve("P00001_5.png"), PNG);
        empleadoExistente(ana);

        servicio.eliminarFoto("P00001");

        assertNull(ana.getFoto());
        assertFalse(Files.exists(archivo));
        verify(personaRepository).saveAndFlush(ana);
    }

    @Test
    void eliminarFotoSinFotoNoHaceNadaYEmpleadoInexistenteResponde404() {
        Persona sinFoto = persona("P00001");
        empleadoExistente(sinFoto);
        servicio.eliminarFoto("P00001");
        verify(personaRepository, never()).saveAndFlush(any(Persona.class));

        when(personaRepository.findById("P00009")).thenReturn(Optional.empty());
        assertEquals(HttpStatus.NOT_FOUND, estadoDe(() -> servicio.eliminarFoto("P00009")));
    }
}
