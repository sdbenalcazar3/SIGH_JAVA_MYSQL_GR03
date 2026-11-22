package edu.ec.monster.ORM_JAVA_GR03.servicio;
import edu.ec.monster.ORM_JAVA_GR03.dto.CargoResumen;
import edu.ec.monster.ORM_JAVA_GR03.dto.PersonalCargo;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Cargo;
import edu.ec.monster.ORM_JAVA_GR03.repository.CargoRepository;
import edu.ec.monster.ORM_JAVA_GR03.repository.PersonaRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

/**
 * Mantenimiento de cargos (PECAR_CARGO). PECAR_CARGO es un catalogo independiente: la relacion con
 * los departamentos es indirecta a traves de PEPER_PERSON (cargo y departamento de cada persona).
 */
@Service
public class CargoServicio extends CrudServicio<Cargo> {

    private static final Pattern CODIGO = Pattern.compile("^[A-Z0-9]{3}$");
    private static final Pattern CODIGO_CORRELATIVO = Pattern.compile("^C(\\d{2})$");
    private static final int MAXIMO_CORRELATIVO = 99;

    private final CargoRepository repository;
    private final PersonaRepository personaRepository;

    public CargoServicio(CargoRepository repository, PersonaRepository personaRepository) {
        super(repository, Cargo::getCodigoCargo);
        this.repository = repository;
        this.personaRepository = personaRepository;
    }

    /**
     * Listado del ListBox con filtros combinables.
     * @param q texto a buscar en codigo o nombre (sin distinguir mayusculas); nulo o vacio no filtra
     * @param activo estado; nulo no filtra
     * @param departamento PEDEP_CODIGO, SIN_PERSONAL o nulo/vacio para todos
     */
    @Transactional(readOnly = true)
    public List<CargoResumen> listarResumen(String q, Boolean activo, String departamento) {
        String depto = departamento == null ? "" : departamento.trim().toUpperCase(Locale.ROOT);
        List<Cargo> cargos = repository.filtrar(patronBusqueda(q), activo == null,
                activo != null && activo, depto);

        Map<String, Long> totales = new HashMap<>();
        for (Object[] fila : personaRepository.contarPersonalPorCargo()) {
            totales.put((String) fila[0], ((Number) fila[1]).longValue());
        }
        Map<String, List<String>> departamentos = new HashMap<>();
        for (Object[] fila : personaRepository.departamentosDistintosPorCargo()) {
            departamentos.computeIfAbsent((String) fila[0], clave -> new ArrayList<>())
                    .add(fila[1] + " - " + fila[2]);
        }
        return cargos.stream()
                .sorted(Comparator.comparing(Cargo::getCodigoCargo))
                .map(c -> new CargoResumen(c.getCodigoCargo(), c.getNombre(), c.getActivo(),
                        totales.getOrDefault(c.getCodigoCargo(), 0L),
                        departamentos.getOrDefault(c.getCodigoCargo(), List.of())))
                .toList();
    }

    /** Personal asignado (PEPER_PERSON) al cargo indicado, ordenado por apellidos. */
    @Transactional(readOnly = true)
    public List<PersonalCargo> personal(String codigo) {
        String normalizado = normalizarCodigo(codigo);
        buscar(normalizado);
        return personaRepository.findByCargoCodigoCargoOrderByApellidosAsc(normalizado).stream()
                .map(p -> new PersonalCargo(p.getCodigoPersona(), p.getNombres(), p.getApellidos(),
                        p.getDepartamento() == null ? null : p.getDepartamento().getNombre(),
                        p.getFuncion(), p.getCorreo(), p.getActivo()))
                .toList();
    }

    /**
     * Siguiente codigo Cxx: maximo + 1 mientras el maximo sea menor que 99; si ya existe C99,
     * el primer hueco libre de C01..C99. Con los 99 codigos ocupados responde 409.
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
            return String.format("C%02d", maximo + 1);
        }
        for (int numero = 1; numero <= MAXIMO_CORRELATIVO; numero++) {
            if (!usados.contains(numero)) {
                return String.format("C%02d", numero);
            }
        }
        throw new ResponseStatusException(HttpStatus.CONFLICT,
                "No hay codigos disponibles: el rango C01..C99 esta completo.");
    }

    @Transactional
    public Cargo registrar(Cargo cargo) {
        normalizar(cargo);
        if (repository.existsById(cargo.getCodigoCargo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un cargo con el codigo " + cargo.getCodigoCargo() + ".");
        }
        validarNombreUnico(cargo);
        return guardar(cargo);
    }

    /** El codigo de la ruta manda sobre el del cuerpo. */
    @Transactional
    public Cargo actualizar(String codigo, Cargo cargo) {
        String normalizado = normalizarCodigo(codigo);
        buscar(normalizado);
        cargo.setCodigoCargo(normalizado);
        normalizar(cargo);
        validarNombreUnico(cargo);
        return guardar(cargo);
    }

    /** No se elimina un cargo con personal asignado: se sugiere marcarlo como inactivo. */
    @Override
    @Transactional
    public void eliminar(String codigo) {
        String normalizado = normalizarCodigo(codigo);
        buscar(normalizado);
        long personal = personaRepository.countByCargoCodigoCargo(normalizado);
        if (personal > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El cargo tiene " + personal + " persona(s) asignada(s). Puede marcarlo como inactivo.");
        }
        super.eliminar(normalizado);
    }

    /**
     * Activa o inactiva varios cargos en una sola transaccion. Todos los codigos se validan antes
     * de modificar nada: si alguno es invalido (400) o no existe (404) no se cambia ningun cargo.
     */
    @Transactional
    public List<Cargo> cambiarEstadoEnLote(List<String> codigos, boolean activo) {
        if (codigos == null || codigos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seleccione al menos un cargo.");
        }
        Set<String> normalizados = new LinkedHashSet<>();
        for (String codigo : codigos) {
            normalizados.add(normalizarCodigo(codigo));
        }
        List<Cargo> encontrados = repository.findAllById(normalizados);
        if (encontrados.size() != normalizados.size()) {
            Set<String> existentes = new LinkedHashSet<>();
            encontrados.forEach(c -> existentes.add(c.getCodigoCargo()));
            List<String> faltantes = normalizados.stream().filter(c -> !existentes.contains(c)).toList();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "No existe(n) el/los cargo(s): " + String.join(", ", faltantes) + ". No se modifico ningun cargo.");
        }
        encontrados.forEach(c -> c.setActivo(activo));
        List<Cargo> guardados = new ArrayList<>(repository.saveAllAndFlush(encontrados));
        guardados.sort(Comparator.comparing(Cargo::getCodigoCargo));
        return guardados;
    }

    /** Normaliza y valida codigo, nombre y estado del cargo (400 si incumple). */
    public void normalizar(Cargo cargo) {
        cargo.setCodigoCargo(normalizarCodigo(cargo.getCodigoCargo()));
        String nombre = cargo.getNombre() == null ? "" : cargo.getNombre().trim().replaceAll("\\s+", " ");
        if (nombre.length() < 3 || nombre.length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El nombre debe tener entre 3 y 100 caracteres.");
        }
        if (nombre.matches(".*\\d.*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del cargo no debe contener numeros.");
        }
        cargo.setNombre(nombre);
        if (cargo.getActivo() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El estado del cargo es obligatorio.");
        }
    }

    private String normalizarCodigo(String codigo) {
        String normalizado = codigo == null ? "" : codigo.trim().toUpperCase(Locale.ROOT);
        if (!CODIGO.matcher(normalizado).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El codigo debe tener 3 letras o numeros, por ejemplo C08.");
        }
        return normalizado;
    }

    private void validarNombreUnico(Cargo cargo) {
        if (repository.existsByNombreIgnoreCaseAndCodigoCargoNot(cargo.getNombre(), cargo.getCodigoCargo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un cargo con ese nombre.");
        }
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
