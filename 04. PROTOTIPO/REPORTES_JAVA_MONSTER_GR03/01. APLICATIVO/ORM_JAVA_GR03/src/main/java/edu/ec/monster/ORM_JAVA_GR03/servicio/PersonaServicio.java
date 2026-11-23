package edu.ec.monster.ORM_JAVA_GR03.servicio;

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
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/**
 * Mantenimiento de empleados (PEPER_PERSON) con TabPage: ficha maestro-detalle (datos academicos
 * PEACA_ACADEM y familiares PEFAM_FAMILI), correlativo Pnnnnn y foto de perfil en disco.
 * Todas las operaciones de escritura son atomicas: ante cualquier error (400/404/409) se revierte
 * la transaccion completa.
 */
@Service
public class PersonaServicio extends CrudServicio<Persona> {

    private static final Logger LOG = LoggerFactory.getLogger(PersonaServicio.class);

    /** Prefijo publico bajo el que WebConfig expone el directorio de fotos. */
    public static final String PREFIJO_URL_FOTO = "/uploads/empleados/";
    public static final long TAMANO_MAXIMO_FOTO = 2L * 1024 * 1024;
    public static final String MENSAJE_FOTO_GRANDE = "La imagen excede el tamaño máximo permitido de 2 MB.";

    private static final Pattern CODIGO = Pattern.compile("^[A-Z0-9]{1,6}$");
    private static final Pattern CODIGO_CORRELATIVO = Pattern.compile("^P(\\d{5})$");
    private static final int MAXIMO_CORRELATIVO = 99999;
    private static final Pattern TELEFONO = Pattern.compile("^[0-9+\\-\\s]{7,20}$");
    private static final Pattern CORREO = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    private static final Pattern CEDULA = Pattern.compile("^\\d{10}$");
    private static final Pattern CON_DIGITOS = Pattern.compile(".*\\d.*");
    private static final Set<String> TIPOS_SANGRE = Set.of("O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-");
    private static final BigDecimal PESO_MIN = new BigDecimal("20.00");
    private static final BigDecimal PESO_MAX = new BigDecimal("300.00");
    private static final BigDecimal ESTATURA_MIN = new BigDecimal("0.50");
    private static final BigDecimal ESTATURA_MAX = new BigDecimal("2.50");
    /** extension permitida -> tipo real de imagen (PNG, JPEG o WEBP). */
    private static final Map<String, String> TIPO_POR_EXTENSION =
            Map.of("png", "png", "jpg", "jpeg", "jpeg", "jpeg", "webp", "webp");
    private static final Map<String, String> TIPO_POR_MIME =
            Map.of("image/png", "png", "image/jpeg", "jpeg", "image/webp", "webp");

    private final PersonaRepository repository;
    private final DepartamentoRepository departamentoRepository;
    private final CargoRepository cargoRepository;
    private final GeneroRepository generoRepository;
    private final EstadoCivilRepository estadoCivilRepository;
    private final DatoAcademicoRepository academicoRepository;
    private final DatoFamiliarRepository familiarRepository;
    private final Path directorioFotos;

    public PersonaServicio(PersonaRepository repository,
            DepartamentoRepository departamentoRepository,
            CargoRepository cargoRepository,
            GeneroRepository generoRepository,
            EstadoCivilRepository estadoCivilRepository,
            DatoAcademicoRepository academicoRepository,
            DatoFamiliarRepository familiarRepository,
            @Value("${sigh.uploads.dir:uploads/empleados}") String directorioFotos) {
        super(repository, Persona::getCodigoPersona);
        this.repository = repository;
        this.departamentoRepository = departamentoRepository;
        this.cargoRepository = cargoRepository;
        this.generoRepository = generoRepository;
        this.estadoCivilRepository = estadoCivilRepository;
        this.academicoRepository = academicoRepository;
        this.familiarRepository = familiarRepository;
        this.directorioFotos = Path.of(directorioFotos).toAbsolutePath().normalize();
    }

    // ------------------------------------------------------------------ consultas

    /**
     * Listado del modal Listar y del navegador de registros, ordenado por codigo.
     * @param q texto a buscar (codigo, nombres, apellidos, documento); nulo o vacio no filtra
     * @param departamento PEDEP_CODIGO; nulo o vacio no filtra
     * @param cargo PECAR_CODIGO; nulo o vacio no filtra
     * @param activo estado; nulo no filtra
     */
    @Transactional(readOnly = true)
    public List<EmpleadoResumen> listarResumen(String q, String departamento, String cargo, Boolean activo) {
        List<Persona> personas = repository.filtrar(patronBusqueda(q), activo == null,
                activo != null && activo, sinEspacios(departamento), sinEspacios(cargo));
        Map<String, Long> academicos = totales(academicoRepository.contarPorPersona());
        Map<String, Long> familiares = totales(familiarRepository.contarPorPersona());
        return personas.stream()
                .map(p -> new EmpleadoResumen(p.getCodigoPersona(), p.getCodigoExterno(), p.getApellidos(),
                        p.getNombres(), p.getFuncion(), p.getNumeroDocumento(), p.getCorreo(), p.getFoto(),
                        p.getDepartamento() == null ? null : p.getDepartamento().getCodigoDepartamento(),
                        p.getDepartamento() == null ? null : p.getDepartamento().getNombre(),
                        p.getCargo() == null ? null : p.getCargo().getCodigoCargo(),
                        p.getCargo() == null ? null : p.getCargo().getNombre(),
                        p.getActivo(),
                        academicos.getOrDefault(p.getCodigoPersona(), 0L),
                        familiares.getOrDefault(p.getCodigoPersona(), 0L)))
                .toList();
    }

    /** Ficha completa del empleado: cabecera, datos generales, academicos y familiares. */
    @Transactional(readOnly = true)
    public EmpleadoDetalle obtenerDetalle(String codigo) {
        return detalle(buscar(normalizarCodigo(codigo)));
    }

    /**
     * Siguiente codigo Pnnnnn: maximo + 1 mientras el maximo sea menor que 99999; si ya existe
     * P99999, el primer hueco libre. Con los 99999 codigos ocupados responde 409.
     */
    @Transactional(readOnly = true)
    public String generarSiguienteCodigo() {
        Set<Integer> usados = new LinkedHashSet<>();
        for (String codigo : repository.codigosCorrelativos()) {
            var coincidencia = CODIGO_CORRELATIVO.matcher(codigo == null ? "" : codigo.toUpperCase(Locale.ROOT));
            if (coincidencia.matches()) {
                usados.add(Integer.parseInt(coincidencia.group(1)));
            }
        }
        int maximo = usados.stream().mapToInt(Integer::intValue).max().orElse(0);
        if (maximo < MAXIMO_CORRELATIVO) {
            return String.format("P%05d", maximo + 1);
        }
        for (int numero = 1; numero <= MAXIMO_CORRELATIVO; numero++) {
            if (!usados.contains(numero)) {
                return String.format("P%05d", numero);
            }
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT,
                "No hay codigos disponibles: el rango P00001..P99999 esta completo.");
    }

    // ------------------------------------------------------------------ ficha maestro-detalle

    /** Alta atomica de empleado + academicos + familiares. 409 si el codigo, correo, documento o codigo externo existen. */
    @Transactional
    public EmpleadoDetalle registrarFicha(EmpleadoGuardarRequest solicitud) {
        if (solicitud == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La ficha del empleado es obligatoria.");
        }
        String codigo = normalizarCodigo(solicitud.codigoPersona());
        if (repository.existsById(codigo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un empleado con el codigo " + codigo + ".");
        }
        Persona persona = new Persona();
        persona.setCodigoPersona(codigo);
        aplicar(persona, solicitud, true);
        List<DatoAcademicoDto> academicos = validarAcademicos(solicitud.academicos());
        List<DatoFamiliarDto> familiares = validarFamiliares(solicitud.familiares());
        Persona guardada = repository.saveAndFlush(persona);
        sincronizarAcademicos(guardada, academicos);
        sincronizarFamiliares(guardada, familiares);
        return detalle(guardada);
    }

    /**
     * Actualizacion atomica: el codigo de la ruta prevalece sobre el del cuerpo; los registros
     * academicos y familiares quedan exactamente como en la solicitud (se conservan los id
     * recibidos, se crean los nuevos y se eliminan los omitidos). La foto no se modifica aqui.
     */
    @Transactional
    public EmpleadoDetalle actualizarFicha(String codigo, EmpleadoGuardarRequest solicitud) {
        String normalizado = normalizarCodigo(codigo);
        Persona persona = buscar(normalizado);
        if (solicitud == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La ficha del empleado es obligatoria.");
        }
        aplicar(persona, solicitud, false);
        List<DatoAcademicoDto> academicos = validarAcademicos(solicitud.academicos());
        List<DatoFamiliarDto> familiares = validarFamiliares(solicitud.familiares());
        Persona guardada = repository.saveAndFlush(persona);
        sincronizarAcademicos(guardada, academicos);
        sincronizarFamiliares(guardada, familiares);
        return detalle(guardada);
    }

    /**
     * Elimina el empleado, sus registros hijos y su foto fisica. Si tiene cuenta en SEUSU_USUARI
     * responde 409 sin modificar nada.
     */
    @Override
    @Transactional
    public void eliminar(String codigo) {
        String normalizado = normalizarCodigo(codigo);
        Persona persona = buscar(normalizado);
        if (repository.contarUsuariosPorPersona(normalizado) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El empleado tiene una cuenta de usuario asociada. Puede marcarlo como inactivo.");
        }
        String foto = persona.getFoto();
        academicoRepository.deleteByPersonaCodigoPersona(normalizado);
        familiarRepository.deleteByPersonaCodigoPersona(normalizado);
        super.eliminar(normalizado);
        borrarArchivo(foto);
    }

    // ------------------------------------------------------------------ CRUD granular de hijos

    @Transactional
    public DatoAcademicoDto agregarAcademico(String codigo, DatoAcademicoDto dto) {
        Persona persona = buscar(normalizarCodigo(codigo));
        DatoAcademicoDto valido = validarAcademico(dto);
        DatoAcademico guardado = academicoRepository.saveAndFlush(
                new DatoAcademico(persona, valido.nivel(), valido.institucion(), valido.titulo()));
        return aDto(guardado);
    }

    @Transactional
    public void eliminarAcademico(String codigo, Long id) {
        String normalizado = normalizarCodigo(codigo);
        buscar(normalizado);
        DatoAcademico academico = id == null ? null : academicoRepository.findById(id).orElse(null);
        if (academico == null || academico.getPersona() == null
                || !normalizado.equals(academico.getPersona().getCodigoPersona())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "El registro academico no existe para este empleado.");
        }
        academicoRepository.delete(academico);
        academicoRepository.flush();
    }

    @Transactional
    public DatoFamiliarDto agregarFamiliar(String codigo, DatoFamiliarDto dto) {
        Persona persona = buscar(normalizarCodigo(codigo));
        DatoFamiliarDto valido = validarFamiliar(dto);
        DatoFamiliar guardado = familiarRepository.saveAndFlush(new DatoFamiliar(persona, valido.parentesco(),
                valido.apellidos(), valido.nombres(), valido.fechaNacimiento()));
        return aDto(guardado);
    }

    @Transactional
    public void eliminarFamiliar(String codigo, Long id) {
        String normalizado = normalizarCodigo(codigo);
        buscar(normalizado);
        DatoFamiliar familiar = id == null ? null : familiarRepository.findById(id).orElse(null);
        if (familiar == null || familiar.getPersona() == null
                || !normalizado.equals(familiar.getPersona().getCodigoPersona())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "El registro familiar no existe para este empleado.");
        }
        familiarRepository.delete(familiar);
        familiarRepository.flush();
    }

    // ------------------------------------------------------------------ foto

    /**
     * Guarda o reemplaza la foto del empleado. Valida (sin escribir nada en disco hasta que todo
     * sea correcto): archivo no vacio, tamano &lt;= 2 MB, extension png/jpg/jpeg/webp, MIME y
     * magic bytes coherentes entre si, y nombre sin separadores de ruta. El nombre en disco se
     * genera en el servidor a partir del codigo normalizado.
     *
     * @return URL publica de la foto, por ejemplo /uploads/empleados/P00001_1730000000000.png
     */
    @Transactional
    public String guardarFoto(String codigo, MultipartFile archivo) {
        String normalizado = normalizarCodigo(codigo);
        Persona persona = buscar(normalizado);
        if (archivo == null || archivo.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seleccione una imagen.");
        }
        if (archivo.getSize() > TAMANO_MAXIMO_FOTO) {
            throw new ResponseStatusException(HttpStatus.CONTENT_TOO_LARGE, MENSAJE_FOTO_GRANDE);
        }
        String extension = extensionPermitida(archivo.getOriginalFilename());
        String tipoMime = TIPO_POR_MIME.get(archivo.getContentType() == null
                ? "" : archivo.getContentType().trim().toLowerCase(Locale.ROOT));
        if (tipoMime == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El tipo de archivo no es una imagen PNG, JPG o WEBP.");
        }
        byte[] contenido;
        try {
            contenido = archivo.getBytes();
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo leer la imagen.");
        }
        if (contenido.length > TAMANO_MAXIMO_FOTO) {
            throw new ResponseStatusException(HttpStatus.CONTENT_TOO_LARGE, MENSAJE_FOTO_GRANDE);
        }
        String tipoReal = detectarTipo(contenido);
        String tipoExtension = TIPO_POR_EXTENSION.get(extension);
        if (tipoReal == null || !tipoReal.equals(tipoExtension) || !tipoReal.equals(tipoMime)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El contenido del archivo no corresponde a una imagen PNG, JPG o WEBP valida.");
        }

        String nombre = normalizado + "_" + System.currentTimeMillis() + "." + extension;
        Path destino = resolverEnDirectorio(nombre);
        if (destino == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre de archivo no permitido.");
        }
        String anterior = persona.getFoto();
        try {
            Files.createDirectories(directorioFotos);
            Files.write(destino, contenido);
        } catch (IOException ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No se pudo guardar la imagen.");
        }
        try {
            persona.setFoto(PREFIJO_URL_FOTO + nombre);
            repository.saveAndFlush(persona);
        } catch (RuntimeException ex) {
            borrarArchivo(PREFIJO_URL_FOTO + nombre);
            throw ex;
        }
        borrarArchivo(anterior);
        return PREFIJO_URL_FOTO + nombre;
    }

    /** Quita la foto del empleado y elimina su archivo. Si no tiene foto no hace nada. */
    @Transactional
    public void eliminarFoto(String codigo) {
        Persona persona = buscar(normalizarCodigo(codigo));
        String foto = persona.getFoto();
        if (foto == null || foto.isBlank()) {
            return;
        }
        persona.setFoto(null);
        repository.saveAndFlush(persona);
        borrarArchivo(foto);
    }

    /** Tipo real de imagen segun los primeros bytes (PNG, JPEG o WEBP) o null si no coincide. */
    static String detectarTipo(byte[] b) {
        if (b == null) {
            return null;
        }
        if (b.length >= 4 && (b[0] & 0xFF) == 0x89 && b[1] == 0x50 && b[2] == 0x4E && b[3] == 0x47) {
            return "png";
        }
        if (b.length >= 3 && (b[0] & 0xFF) == 0xFF && (b[1] & 0xFF) == 0xD8 && (b[2] & 0xFF) == 0xFF) {
            return "jpeg";
        }
        if (b.length >= 12 && b[0] == 0x52 && b[1] == 0x49 && b[2] == 0x46 && b[3] == 0x46
                && b[8] == 0x57 && b[9] == 0x45 && b[10] == 0x42 && b[11] == 0x50) {
            return "webp";
        }
        return null;
    }

    /** Extension en minusculas; 400 si el nombre trae separadores de ruta, ".." o una extension no permitida. */
    private String extensionPermitida(String nombreOriginal) {
        String nombre = nombreOriginal == null ? "" : nombreOriginal.trim();
        if (nombre.isEmpty() || nombre.contains("/") || nombre.contains("\\") || nombre.contains("..")
                || nombre.indexOf('\0') >= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nombre de archivo no permitido.");
        }
        int punto = nombre.lastIndexOf('.');
        String extension = punto < 0 ? "" : nombre.substring(punto + 1).toLowerCase(Locale.ROOT);
        if (!TIPO_POR_EXTENSION.containsKey(extension)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Solo se permiten imagenes PNG, JPG, JPEG o WEBP.");
        }
        return extension;
    }

    /** Resuelve el nombre dentro del directorio de fotos; null si la ruta resultante escapa de el. */
    private Path resolverEnDirectorio(String nombre) {
        Path ruta = directorioFotos.resolve(nombre).normalize();
        return ruta.startsWith(directorioFotos) && !ruta.equals(directorioFotos) ? ruta : null;
    }

    /** Elimina el archivo de una URL /uploads/empleados/...; nunca sale del directorio de fotos y no lanza errores. */
    private void borrarArchivo(String urlFoto) {
        if (urlFoto == null || !urlFoto.startsWith(PREFIJO_URL_FOTO)) {
            return;
        }
        Path ruta = resolverEnDirectorio(urlFoto.substring(PREFIJO_URL_FOTO.length()));
        if (ruta == null) {
            return;
        }
        try {
            Files.deleteIfExists(ruta);
        } catch (IOException ex) {
            LOG.warn("No se pudo eliminar la foto {}: {}", ruta, ex.getMessage());
        }
    }

    // ------------------------------------------------------------------ validacion y mapeo

    /** Valida los datos de la solicitud y los copia a la entidad (nueva o existente). */
    private void aplicar(Persona persona, EmpleadoGuardarRequest s, boolean nueva) {
        String codigo = persona.getCodigoPersona();
        String apellidos = nombrePersona(s.apellidos(), "Los apellidos", 2, 150);
        String nombres = nombrePersona(s.nombres(), "Los nombres", 2, 255);
        String funcion = textoObligatorio(s.funcion(), "La funcion", 255);
        String habilitacion = textoOpcional(s.habilitacion(), "La habilitacion", 255);
        if (s.activo() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado del empleado es obligatorio.");
        }
        String tipoDocumento = textoOpcional(s.tipoDocumento(), "El tipo de documento", 30);
        String numeroDocumento = textoOpcional(s.numeroDocumento(), "El numero de documento", 30);
        if (numeroDocumento != null && "CEDULA".equalsIgnoreCase(tipoDocumento)
                && !CEDULA.matcher(numeroDocumento).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La cedula debe tener 10 digitos numericos.");
        }
        validarFechaNoFutura(s.fechaNacimiento(), "La fecha de nacimiento");
        String telefono = textoOpcional(s.telefono(), "El telefono", 30);
        if (telefono != null && !TELEFONO.matcher(telefono).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El telefono debe tener entre 7 y 20 digitos (se permiten + - y espacios).");
        }
        String correo = textoOpcional(s.correo(), "El correo", 150);
        if (correo != null && !CORREO.matcher(correo).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El correo no tiene un formato valido.");
        }
        String tipoSangre = textoOpcional(s.tipoSangre(), "El tipo de sangre", 5);
        if (tipoSangre != null) {
            tipoSangre = tipoSangre.toUpperCase(Locale.ROOT);
            if (!TIPOS_SANGRE.contains(tipoSangre)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El tipo de sangre debe ser uno de: O+, O-, A+, A-, B+, B-, AB+, AB-.");
            }
        }
        BigDecimal peso = rango(s.peso(), PESO_MIN, PESO_MAX, "El peso", "kg");
        BigDecimal estatura = rango(s.estatura(), ESTATURA_MIN, ESTATURA_MAX, "La estatura", "m");

        String codigoDepartamento = codigoReferencia(s.codigoDepartamento(), "El departamento", 3, true);
        String codigoCargo = codigoReferencia(s.codigoCargo(), "El cargo", 3, true);
        String codigoGenero = codigoReferencia(s.codigoGenero(), "El genero", 1, false);
        String codigoEstadoCivil = codigoReferencia(s.codigoEstadoCivil(), "El estado civil", 1, false);
        Departamento departamento = departamentoRepository.findById(codigoDepartamento)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe el departamento " + codigoDepartamento + "."));
        Cargo cargo = cargoRepository.findById(codigoCargo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe el cargo " + codigoCargo + "."));
        Genero genero = codigoGenero == null ? null : generoRepository.findById(codigoGenero)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe el genero " + codigoGenero + "."));
        EstadoCivil estadoCivil = codigoEstadoCivil == null ? null : estadoCivilRepository.findById(codigoEstadoCivil)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe el estado civil " + codigoEstadoCivil + "."));

        String codigoExterno = textoOpcional(s.codigoExterno(), "El codigo externo", 10);
        if (codigoExterno == null) {
            codigoExterno = nueva ? codigoExternoAutomatico(codigo) : persona.getCodigoExterno();
        }
        if (repository.existsByCodigoExternoIgnoreCaseAndCodigoPersonaNot(codigoExterno, codigo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un empleado con ese codigo externo.");
        }
        if (numeroDocumento != null && repository.existsByNumeroDocumentoAndCodigoPersonaNot(numeroDocumento, codigo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un empleado con ese numero de documento.");
        }
        if (correo != null && repository.existsByCorreoIgnoreCaseAndCodigoPersonaNot(correo, codigo)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un empleado con ese correo.");
        }

        persona.setCodigoExterno(codigoExterno);
        persona.setApellidos(apellidos);
        persona.setNombres(nombres);
        persona.setFuncion(funcion);
        persona.setHabilitacion(habilitacion);
        persona.setActivo(s.activo());
        persona.setTipoDocumento(tipoDocumento);
        persona.setNumeroDocumento(numeroDocumento);
        persona.setFechaNacimiento(s.fechaNacimiento());
        persona.setTelefono(telefono);
        persona.setCorreo(correo);
        persona.setGenero(genero);
        persona.setEstadoCivil(estadoCivil);
        persona.setTipoSangre(tipoSangre);
        persona.setPeso(peso);
        persona.setEstatura(estatura);
        persona.setDepartamento(departamento);
        persona.setCargo(cargo);
    }

    /** EXT + 7 digitos: el numero final del codigo (P00007 -> EXT0000007) o, si no tiene, un derivado estable. */
    private String codigoExternoAutomatico(String codigo) {
        String digitos = codigo.replaceAll("\\D", "");
        if (digitos.isEmpty()) {
            digitos = String.valueOf(Math.abs(codigo.hashCode()) % 10_000_000);
        }
        if (digitos.length() > 7) {
            digitos = digitos.substring(digitos.length() - 7);
        }
        return "EXT" + "0".repeat(7 - digitos.length()) + digitos;
    }

    private List<DatoAcademicoDto> validarAcademicos(List<DatoAcademicoDto> lista) {
        List<DatoAcademicoDto> validos = new ArrayList<>();
        for (DatoAcademicoDto dto : lista == null ? List.<DatoAcademicoDto>of() : lista) {
            validos.add(validarAcademico(dto));
        }
        return validos;
    }

    private DatoAcademicoDto validarAcademico(DatoAcademicoDto dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El registro academico es obligatorio.");
        }
        return new DatoAcademicoDto(dto.codigoAcademico(),
                textoObligatorio(dto.nivel(), "El nivel academico", 60),
                textoObligatorio(dto.institucion(), "La institucion", 150),
                textoObligatorio(dto.titulo(), "El titulo", 150));
    }

    private List<DatoFamiliarDto> validarFamiliares(List<DatoFamiliarDto> lista) {
        List<DatoFamiliarDto> validos = new ArrayList<>();
        for (DatoFamiliarDto dto : lista == null ? List.<DatoFamiliarDto>of() : lista) {
            validos.add(validarFamiliar(dto));
        }
        return validos;
    }

    private DatoFamiliarDto validarFamiliar(DatoFamiliarDto dto) {
        if (dto == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El registro familiar es obligatorio.");
        }
        validarFechaNoFutura(dto.fechaNacimiento(), "La fecha de nacimiento del familiar");
        return new DatoFamiliarDto(dto.codigoFamiliar(),
                textoObligatorio(dto.parentesco(), "El parentesco", 60),
                textoObligatorio(dto.apellidos(), "Los apellidos del familiar", 150),
                textoObligatorio(dto.nombres(), "Los nombres del familiar", 150),
                dto.fechaNacimiento());
    }

    /** Deja los academicos exactamente como en la lista: actualiza por id, crea los nuevos y elimina los omitidos. */
    private void sincronizarAcademicos(Persona persona, List<DatoAcademicoDto> lista) {
        List<DatoAcademico> actuales = academicoRepository
                .findByPersonaCodigoPersonaOrderByCodigoAcademicoAsc(persona.getCodigoPersona());
        Map<Long, DatoAcademico> porId = new HashMap<>();
        actuales.forEach(a -> porId.put(a.getCodigoAcademico(), a));
        Set<Long> conservados = new HashSet<>();
        List<DatoAcademico> guardar = new ArrayList<>();
        for (DatoAcademicoDto dto : lista) {
            DatoAcademico entidad = dto.codigoAcademico() == null || conservados.contains(dto.codigoAcademico())
                    ? null : porId.get(dto.codigoAcademico());
            if (entidad == null) {
                entidad = new DatoAcademico();
                entidad.setPersona(persona);
            } else {
                conservados.add(entidad.getCodigoAcademico());
            }
            entidad.setNivel(dto.nivel());
            entidad.setInstitucion(dto.institucion());
            entidad.setTitulo(dto.titulo());
            guardar.add(entidad);
        }
        List<DatoAcademico> eliminar = actuales.stream()
                .filter(a -> !conservados.contains(a.getCodigoAcademico())).toList();
        if (!eliminar.isEmpty()) {
            academicoRepository.deleteAll(eliminar);
        }
        academicoRepository.saveAll(guardar);
        academicoRepository.flush();
    }

    private void sincronizarFamiliares(Persona persona, List<DatoFamiliarDto> lista) {
        List<DatoFamiliar> actuales = familiarRepository
                .findByPersonaCodigoPersonaOrderByCodigoFamiliarAsc(persona.getCodigoPersona());
        Map<Long, DatoFamiliar> porId = new HashMap<>();
        actuales.forEach(f -> porId.put(f.getCodigoFamiliar(), f));
        Set<Long> conservados = new HashSet<>();
        List<DatoFamiliar> guardar = new ArrayList<>();
        for (DatoFamiliarDto dto : lista) {
            DatoFamiliar entidad = dto.codigoFamiliar() == null || conservados.contains(dto.codigoFamiliar())
                    ? null : porId.get(dto.codigoFamiliar());
            if (entidad == null) {
                entidad = new DatoFamiliar();
                entidad.setPersona(persona);
            } else {
                conservados.add(entidad.getCodigoFamiliar());
            }
            entidad.setParentesco(dto.parentesco());
            entidad.setApellidos(dto.apellidos());
            entidad.setNombres(dto.nombres());
            entidad.setFechaNacimiento(dto.fechaNacimiento());
            guardar.add(entidad);
        }
        List<DatoFamiliar> eliminar = actuales.stream()
                .filter(f -> !conservados.contains(f.getCodigoFamiliar())).toList();
        if (!eliminar.isEmpty()) {
            familiarRepository.deleteAll(eliminar);
        }
        familiarRepository.saveAll(guardar);
        familiarRepository.flush();
    }

    private EmpleadoDetalle detalle(Persona p) {
        List<DatoAcademicoDto> academicos = academicoRepository
                .findByPersonaCodigoPersonaOrderByCodigoAcademicoAsc(p.getCodigoPersona())
                .stream().map(this::aDto).toList();
        List<DatoFamiliarDto> familiares = familiarRepository
                .findByPersonaCodigoPersonaOrderByCodigoFamiliarAsc(p.getCodigoPersona())
                .stream().map(this::aDto).toList();
        Genero genero = p.getGenero();
        EstadoCivil estadoCivil = p.getEstadoCivil();
        Departamento departamento = p.getDepartamento();
        Cargo cargo = p.getCargo();
        return new EmpleadoDetalle(p.getCodigoPersona(), p.getCodigoExterno(), p.getApellidos(), p.getNombres(),
                p.getFoto(), p.getFuncion(), p.getHabilitacion(), p.getActivo(), p.getTipoDocumento(),
                p.getNumeroDocumento(), p.getFechaNacimiento(), p.getTelefono(), p.getCorreo(),
                genero == null ? null : genero.getCodigoGenero(),
                genero == null ? null : genero.getNombre(),
                estadoCivil == null ? null : estadoCivil.getCodigoEstadoCivil(),
                estadoCivil == null ? null : estadoCivil.getNombre(),
                p.getTipoSangre(), p.getPeso(), p.getEstatura(),
                departamento == null ? null : departamento.getCodigoDepartamento(),
                departamento == null ? null : departamento.getNombre(),
                cargo == null ? null : cargo.getCodigoCargo(),
                cargo == null ? null : cargo.getNombre(),
                repository.contarUsuariosPorPersona(p.getCodigoPersona()) > 0,
                academicos, familiares);
    }

    private DatoAcademicoDto aDto(DatoAcademico a) {
        return new DatoAcademicoDto(a.getCodigoAcademico(), a.getNivel(), a.getInstitucion(), a.getTitulo());
    }

    private DatoFamiliarDto aDto(DatoFamiliar f) {
        return new DatoFamiliarDto(f.getCodigoFamiliar(), f.getParentesco(), f.getApellidos(), f.getNombres(),
                f.getFechaNacimiento());
    }

    // ------------------------------------------------------------------ utilidades

    private String normalizarCodigo(String codigo) {
        String normalizado = codigo == null ? "" : codigo.trim().toUpperCase(Locale.ROOT);
        if (!CODIGO.matcher(normalizado).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El codigo debe tener de 1 a 6 letras o numeros, por ejemplo P00007.");
        }
        return normalizado;
    }

    /** Codigo de una referencia (departamento, cargo, genero, estado civil) en mayusculas; 400 si falta o excede el largo. */
    private String codigoReferencia(String valor, String etiqueta, int maximo, boolean obligatorio) {
        String codigo = valor == null ? "" : valor.trim().toUpperCase(Locale.ROOT);
        if (codigo.isEmpty()) {
            if (obligatorio) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, etiqueta + " es obligatorio.");
            }
            return null;
        }
        if (codigo.length() > maximo) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    etiqueta + " debe tener maximo " + maximo + " caracter(es).");
        }
        return codigo;
    }

    /** Apellidos o nombres: espacios colapsados, largo dentro del rango y sin digitos. */
    private String nombrePersona(String valor, String etiqueta, int minimo, int maximo) {
        String texto = colapsar(valor);
        if (texto.length() < minimo || texto.length() > maximo) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    etiqueta + " deben tener entre " + minimo + " y " + maximo + " caracteres.");
        }
        if (CON_DIGITOS.matcher(texto).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, etiqueta + " no deben contener numeros.");
        }
        return texto;
    }

    private String textoObligatorio(String valor, String etiqueta, int maximo) {
        String texto = colapsar(valor);
        if (texto.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, etiqueta + " es obligatorio.");
        }
        if (texto.length() > maximo) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    etiqueta + " debe tener maximo " + maximo + " caracteres.");
        }
        return texto;
    }

    private String textoOpcional(String valor, String etiqueta, int maximo) {
        String texto = colapsar(valor);
        if (texto.isEmpty()) {
            return null;
        }
        if (texto.length() > maximo) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    etiqueta + " debe tener maximo " + maximo + " caracteres.");
        }
        return texto;
    }

    private String colapsar(String valor) {
        return valor == null ? "" : valor.trim().replaceAll("\\s+", " ");
    }

    private String sinEspacios(String valor) {
        return valor == null ? "" : valor.trim().toUpperCase(Locale.ROOT);
    }

    private void validarFechaNoFutura(LocalDate fecha, String etiqueta) {
        if (fecha != null && fecha.isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, etiqueta + " no puede ser futura.");
        }
    }

    private BigDecimal rango(BigDecimal valor, BigDecimal minimo, BigDecimal maximo, String etiqueta, String unidad) {
        if (valor == null) {
            return null;
        }
        if (valor.compareTo(minimo) < 0 || valor.compareTo(maximo) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, etiqueta + " debe estar entre "
                    + minimo.toPlainString() + " y " + maximo.toPlainString() + " " + unidad + ".");
        }
        return valor.setScale(2, RoundingMode.HALF_UP);
    }

    private Map<String, Long> totales(List<Object[]> filas) {
        Map<String, Long> totales = new HashMap<>();
        for (Object[] fila : filas) {
            totales.put((String) fila[0], ((Number) fila[1]).longValue());
        }
        return totales;
    }

    /** Patron LIKE en minusculas con los comodines del usuario escapados (ESCAPE '!'). */
    private String patronBusqueda(String q) {
        if (q == null || q.isBlank()) {
            return "%";
        }
        String escapado = q.trim().toLowerCase(Locale.ROOT)
                .replace("!", "!!").replace("%", "!%").replace("_", "!_");
        return "%" + escapado + "%";
    }
}
