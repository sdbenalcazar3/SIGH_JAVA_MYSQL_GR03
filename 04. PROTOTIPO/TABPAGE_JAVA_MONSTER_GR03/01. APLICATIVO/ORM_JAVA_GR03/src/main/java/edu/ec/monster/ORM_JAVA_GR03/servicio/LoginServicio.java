package edu.ec.monster.ORM_JAVA_GR03.servicio;

import edu.ec.monster.ORM_JAVA_GR03.modelo.OpcPer;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Opcion;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Perfil;
import edu.ec.monster.ORM_JAVA_GR03.modelo.UsuPer;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Usuario;
import edu.ec.monster.ORM_JAVA_GR03.repository.UsuarioRepository;
import edu.ec.monster.ORM_JAVA_GR03.utils.SeguridadUtil;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Logica del inicio de sesion sobre la tabla SEUSU_USUARI del SIGH.
 * Valida la clave con SHA-256, el estado del usuario y el bloqueo por intentos fallidos.
 */
@Service
public class LoginServicio {

    public static final int MAX_INTENTOS = 3;
    public static final int MINUTOS_BLOQUEO = 5;
    public static final String ESTADO_ACTIVO = "A";
    public static final String OPCION_DEPARTAMENTOS = "DEP";
    public static final String OPCION_CARGOS = "CAR";
    public static final String OPCION_EMPLEADOS = "EMP";

    private final UsuarioRepository usuarioRepository;

    public LoginServicio(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public enum Estado { CORRECTO, CREDENCIALES_INVALIDAS, BLOQUEADO, INACTIVO }

    public record Resultado(Estado estado, Usuario usuario, long minutosRestantes, int intentosRestantes) {
        static Resultado de(Estado estado) {
            return new Resultado(estado, null, 0, 0);
        }
    }

    @Transactional
    public Resultado autenticar(String nombreUsuario, String clave) {
        if (nombreUsuario == null || nombreUsuario.isBlank() || clave == null || clave.isEmpty()) {
            return Resultado.de(Estado.CREDENCIALES_INVALIDAS);
        }

        Optional<Usuario> encontrado = buscarUsuario(nombreUsuario.trim());
        if (encontrado.isEmpty()) {
            return Resultado.de(Estado.CREDENCIALES_INVALIDAS);
        }

        Usuario usuario = encontrado.get();
        LocalDateTime ahora = LocalDateTime.now();

        if (usuario.getBloqueoHasta() != null && usuario.getBloqueoHasta().isAfter(ahora)) {
            long minutos = Math.max(1, Duration.between(ahora, usuario.getBloqueoHasta()).toMinutes() + 1);
            return new Resultado(Estado.BLOQUEADO, null, minutos, 0);
        }

        if (!SeguridadUtil.mismoHash(SeguridadUtil.sha256(clave), usuario.getHashClave())) {
            int intentos = (usuario.getIntentosFallidos() == null ? 0 : usuario.getIntentosFallidos()) + 1;
            if (intentos >= MAX_INTENTOS) {
                usuario.setIntentosFallidos(0);
                usuario.setBloqueoHasta(ahora.plusMinutes(MINUTOS_BLOQUEO));
                usuarioRepository.save(usuario);
                return new Resultado(Estado.BLOQUEADO, null, MINUTOS_BLOQUEO, 0);
            }
            usuario.setIntentosFallidos(intentos);
            usuarioRepository.save(usuario);
            return new Resultado(Estado.CREDENCIALES_INVALIDAS, null, 0, MAX_INTENTOS - intentos);
        }

        if (usuario.getEstado() == null || !ESTADO_ACTIVO.equalsIgnoreCase(usuario.getEstado().getCodigoEstado())) {
            return Resultado.de(Estado.INACTIVO);
        }

        usuario.setIntentosFallidos(0);
        usuario.setBloqueoHasta(null);
        usuarioRepository.save(usuario);
        return new Resultado(Estado.CORRECTO, usuario, 0, MAX_INTENTOS);
    }

    /** Se acepta el nombre de usuario (SEUSU_USUARI) o el codigo (SEUSU_CODIGO). */
    private Optional<Usuario> buscarUsuario(String valor) {
        Optional<Usuario> porNombre = usuarioRepository.findByUsuarioIgnoreCase(valor);
        return porNombre.isPresent() ? porNombre : usuarioRepository.findById(valor.toUpperCase());
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorCodigo(String codigoUsuario) {
        return codigoUsuario == null ? Optional.empty() : usuarioRepository.findById(codigoUsuario);
    }

    /** Perfiles asignados al usuario que siguen vigentes. */
    @Transactional(readOnly = true)
    public List<Perfil> perfilesVigentes(String codigoUsuario) {
        return buscarPorCodigo(codigoUsuario)
                .map(usuario -> usuario.getAsignaciones().stream()
                        .filter(this::asignacionVigente)
                        .map(UsuPer::getPerfil)
                        .filter(perfil -> perfil != null && Boolean.TRUE.equals(perfil.getActivo()))
                        .distinct()
                        .toList())
                .orElse(List.of());
    }

    /**
     * Opciones habilitadas para el usuario: usuario -> perfiles vigentes -> permisos vigentes -> opcion activa.
     */
    @Transactional(readOnly = true)
    public List<Opcion> opcionesPermitidas(String codigoUsuario) {
        Map<String, Opcion> opciones = new LinkedHashMap<>();
        for (Perfil perfil : perfilesVigentes(codigoUsuario)) {
            for (OpcPer permiso : perfil.getPermisos()) {
                Opcion opcion = permiso.getOpcion();
                if (permisoVigente(permiso) && opcion != null && Boolean.TRUE.equals(opcion.getActivo())) {
                    opciones.putIfAbsent(opcion.getCodigoOpcion(), opcion);
                }
            }
        }
        return opciones.values().stream()
                .sorted(Comparator.comparing(Opcion::getModulo).thenComparing(Opcion::getNombre))
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean tieneOpcion(String codigoUsuario, String codigoOpcion) {
        return opcionesPermitidas(codigoUsuario).stream()
                .anyMatch(opcion -> codigoOpcion.equalsIgnoreCase(opcion.getCodigoOpcion()));
    }

    private boolean asignacionVigente(UsuPer asignacion) {
        return Boolean.TRUE.equals(asignacion.getVigente())
                && enRango(asignacion.getDesde(), asignacion.getHasta());
    }

    private boolean permisoVigente(OpcPer permiso) {
        return Boolean.TRUE.equals(permiso.getVigente())
                && enRango(permiso.getDesde(), permiso.getHasta());
    }

    private boolean enRango(LocalDateTime desde, LocalDateTime hasta) {
        LocalDateTime ahora = LocalDateTime.now();
        return (desde == null || !desde.isAfter(ahora)) && (hasta == null || hasta.isAfter(ahora));
    }
}
