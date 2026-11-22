package edu.ec.monster.ORM_JAVA_GR03.config;

import edu.ec.monster.ORM_JAVA_GR03.modelo.Estado;
import edu.ec.monster.ORM_JAVA_GR03.modelo.OpcPer;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Opcion;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Sistema;
import edu.ec.monster.ORM_JAVA_GR03.modelo.UsuPer;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Usuario;
import edu.ec.monster.ORM_JAVA_GR03.repository.EstadoRepository;
import edu.ec.monster.ORM_JAVA_GR03.repository.OpcPerRepository;
import edu.ec.monster.ORM_JAVA_GR03.repository.OpcionRepository;
import edu.ec.monster.ORM_JAVA_GR03.repository.SistemaRepository;
import edu.ec.monster.ORM_JAVA_GR03.repository.UsuPerRepository;
import edu.ec.monster.ORM_JAVA_GR03.repository.UsuarioRepository;
import edu.ec.monster.ORM_JAVA_GR03.servicio.LoginServicio;
import edu.ec.monster.ORM_JAVA_GR03.utils.SeguridadUtil;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * Prepara la base SIGH_GR3 para el login al iniciar la aplicacion. Todas las operaciones
 * son idempotentes: si el dato ya existe no se vuelve a crear.
 * 1. Crea la tabla SETOK_RECUPE para los enlaces de recuperacion de clave.
 * 2. Asigna la clave inicial (SHA-256) a los usuarios cuyo hash no es valido, por ejemplo
 *    los usuarios de demostracion con DEMO_NO_AUTENTICABLE.
 * 3. Registra la opcion DEP (mantenimiento de departamentos) y la concede al perfil
 *    del primer usuario con una asignacion vigente, si ningun perfil la tiene.
 * 4. Hace lo mismo con la opcion CAR (mantenimiento de cargos con ListBox).
 */
@Component
public class DatosInicialesConfig implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DatosInicialesConfig.class);

    private final JdbcTemplate jdbcTemplate;
    private final TransactionTemplate transaccion;
    private final UsuarioRepository usuarioRepository;
    private final UsuPerRepository usuPerRepository;
    private final OpcionRepository opcionRepository;
    private final OpcPerRepository opcPerRepository;
    private final SistemaRepository sistemaRepository;
    private final EstadoRepository estadoRepository;

    @Value("${login.datos-iniciales:true}")
    private boolean habilitado;

    @Value("${login.clave-inicial:Sigh2026}")
    private String claveInicial;

    public DatosInicialesConfig(JdbcTemplate jdbcTemplate, TransactionTemplate transaccion,
            UsuarioRepository usuarioRepository, UsuPerRepository usuPerRepository,
            OpcionRepository opcionRepository, OpcPerRepository opcPerRepository,
            SistemaRepository sistemaRepository, EstadoRepository estadoRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.transaccion = transaccion;
        this.usuarioRepository = usuarioRepository;
        this.usuPerRepository = usuPerRepository;
        this.opcionRepository = opcionRepository;
        this.opcPerRepository = opcPerRepository;
        this.sistemaRepository = sistemaRepository;
        this.estadoRepository = estadoRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!habilitado) {
            return;
        }
        crearTablaTokens();
        transaccion.executeWithoutResult(estado -> asignarClavesIniciales());
        transaccion.executeWithoutResult(estado -> registrarOpcionDepartamentos());
        transaccion.executeWithoutResult(estado -> registrarOpcionCargos());
    }

    private void crearTablaTokens() {
        String columnas = """
                SETOK_CODIGO BIGINT NOT NULL AUTO_INCREMENT,
                SETOK_TOKEN VARCHAR(64) NOT NULL,
                SETOK_FECCRE DATETIME NOT NULL,
                SETOK_EXPIRA DATETIME NOT NULL,
                SETOK_USADO BIT(1) NOT NULL DEFAULT b'0',
                SEUSU_CODIGO %s NOT NULL,
                PRIMARY KEY (SETOK_CODIGO),
                UNIQUE KEY UK_SETOK_TOKEN (SETOK_TOKEN),
                KEY IX_SETOK_USUARIO (SEUSU_CODIGO)""".formatted(tipoCodigoUsuario());
        try {
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS SETOK_RECUPE (" + columnas + """
                    ,
                    CONSTRAINT FK_SETOK_USUARIO FOREIGN KEY (SEUSU_CODIGO) REFERENCES SEUSU_USUARI (SEUSU_CODIGO)
                    )""");
        } catch (DataAccessException ex) {
            // Si la clave foranea no se puede crear (por ejemplo, distinto juego de caracteres),
            // la tabla se crea sin ella para no detener el arranque.
            LOG.warn("SETOK_RECUPE se crea sin clave foranea: {}", ex.getMostSpecificCause().getMessage());
            jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS SETOK_RECUPE (" + columnas + ")");
        }
    }

    /**
     * La clave foranea exige el mismo tipo, juego de caracteres y collation que SEUSU_CODIGO,
     * por eso se consulta en INFORMATION_SCHEMA en lugar de suponerlo.
     */
    private String tipoCodigoUsuario() {
        try {
            return jdbcTemplate.queryForObject("""
                    SELECT CONCAT(COLUMN_TYPE,
                                  IF(CHARACTER_SET_NAME IS NULL, '', CONCAT(' CHARACTER SET ', CHARACTER_SET_NAME)),
                                  IF(COLLATION_NAME IS NULL, '', CONCAT(' COLLATE ', COLLATION_NAME)))
                    FROM INFORMATION_SCHEMA.COLUMNS
                    WHERE TABLE_SCHEMA = DATABASE()
                      AND LOWER(TABLE_NAME) = 'seusu_usuari'
                      AND UPPER(COLUMN_NAME) = 'SEUSU_CODIGO'
                    """, String.class);
        } catch (DataAccessException ex) {
            return "VARCHAR(6)";
        }
    }

    private void asignarClavesIniciales() {
        String hashInicial = SeguridadUtil.sha256(claveInicial);
        for (Usuario usuario : usuarioRepository.findAll()) {
            if (!SeguridadUtil.esSha256(usuario.getHashClave())) {
                usuario.setHashClave(hashInicial);
                usuario.setIntentosFallidos(0);
                usuario.setBloqueoHasta(null);
                usuario.setFechaModificacion(LocalDateTime.now());
                usuarioRepository.save(usuario);
                LOG.info("Clave inicial asignada al usuario {}", usuario.getUsuario());
            }
        }
    }

    private void registrarOpcionDepartamentos() {
        registrarOpcion(LoginServicio.OPCION_DEPARTAMENTOS, "Departamentos",
                "Consulta, registro, edicion y eliminacion de departamentos",
                "Mantenimiento de departamentos desde el login");
    }

    private void registrarOpcionCargos() {
        registrarOpcion(LoginServicio.OPCION_CARGOS, "Cargos",
                "Consulta con ListBox, filtros y mantenimiento de cargos",
                "Mantenimiento de cargos con ListBox");
    }

    /** Registra la opcion si no existe y, si ningun perfil la tiene, la concede al perfil de la primera asignacion vigente. */
    private void registrarOpcion(String codigoOpcion, String nombre, String descripcion, String ambito) {
        Optional<Estado> activo = estadoRepository.findById(LoginServicio.ESTADO_ACTIVO);
        Optional<Sistema> sistema = sistemaRepository.findAll().stream()
                .filter(item -> Boolean.TRUE.equals(item.getActivo()))
                .min(Comparator.comparing(Sistema::getCodigoSistema));
        if (activo.isEmpty() || sistema.isEmpty()) {
            LOG.warn("No existe el estado A o un sistema activo; no se registra la opcion {}", codigoOpcion);
            return;
        }

        Opcion opcion = opcionRepository.findById(codigoOpcion).orElseGet(() -> {
            Opcion nueva = new Opcion();
            nueva.setCodigoOpcion(codigoOpcion);
            nueva.setNombre(nombre);
            nueva.setModulo("Personal");
            nueva.setOperacion("Mantenimiento");
            nueva.setDescripcion(descripcion);
            nueva.setActivo(true);
            nueva.setSistema(sistema.get());
            nueva.setEstado(activo.get());
            return opcionRepository.save(nueva);
        });

        boolean yaConcedida = opcPerRepository.findAll().stream()
                .anyMatch(permiso -> permiso.getOpcion() != null
                        && opcion.getCodigoOpcion().equals(permiso.getOpcion().getCodigoOpcion()));
        if (yaConcedida) {
            return;
        }

        Optional<UsuPer> asignacion = usuPerRepository.findAll().stream()
                .filter(item -> Boolean.TRUE.equals(item.getVigente()) && item.getHasta() == null)
                .min(Comparator.comparing(UsuPer::getCodigoAsignacion));
        if (asignacion.isEmpty()) {
            LOG.warn("No hay asignaciones usuario-perfil vigentes; la opcion {} queda sin conceder", codigoOpcion);
            return;
        }

        OpcPer permiso = new OpcPer();
        permiso.setCodigoPermiso(siguienteCodigoPermiso());
        permiso.setAmbito(ambito);
        permiso.setVigente(true);
        permiso.setDesde(LocalDateTime.now());
        permiso.setPerfil(asignacion.get().getPerfil());
        permiso.setOpcion(opcion);
        permiso.setConcedidoPor(asignacion.get().getUsuario());
        opcPerRepository.save(permiso);
        LOG.info("Opcion {} concedida al perfil {}", codigoOpcion, asignacion.get().getPerfil().getCodigoPerfil());
    }

    private String siguienteCodigoPermiso() {
        int numero = 1;
        String codigo;
        do {
            codigo = String.format("OPL%03d", numero++);
        } while (opcPerRepository.existsById(codigo));
        return codigo;
    }
}
