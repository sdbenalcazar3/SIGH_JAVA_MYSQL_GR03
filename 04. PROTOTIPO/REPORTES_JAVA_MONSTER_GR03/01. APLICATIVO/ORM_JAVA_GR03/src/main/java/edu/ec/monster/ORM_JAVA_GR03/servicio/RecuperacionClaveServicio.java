package edu.ec.monster.ORM_JAVA_GR03.servicio;

import edu.ec.monster.ORM_JAVA_GR03.modelo.TokenRecuperacion;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Usuario;
import edu.ec.monster.ORM_JAVA_GR03.repository.TokenRecuperacionRepository;
import edu.ec.monster.ORM_JAVA_GR03.repository.UsuarioRepository;
import edu.ec.monster.ORM_JAVA_GR03.utils.SeguridadUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Recuperacion de clave: genera un enlace de un solo uso y lo envia al correo de la persona
 * (PEPER_CORREO). Si no hay servidor SMTP configurado, el enlace se muestra en la consola.
 */
@Service
public class RecuperacionClaveServicio {

    private static final Logger LOG = LoggerFactory.getLogger(RecuperacionClaveServicio.class);
    private static final int MINUTOS_VIGENCIA = 30;
    private static final int MAX_SOLICITUDES = 3;
    private static final int HORAS_VENTANA = 1;

    private final UsuarioRepository usuarioRepository;
    private final TokenRecuperacionRepository tokenRepository;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String correoEmisor;

    public RecuperacionClaveServicio(UsuarioRepository usuarioRepository,
            TokenRecuperacionRepository tokenRepository,
            ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.usuarioRepository = usuarioRepository;
        this.tokenRepository = tokenRepository;
        this.mailSender = mailSenderProvider.getIfAvailable();
    }

    /**
     * @return false solo cuando se supero el limite de solicitudes. Si el correo no existe
     * tambien devuelve true para no revelar que cuentas estan registradas.
     */
    @Transactional
    public boolean solicitar(String correo, String urlBase) {
        if (correo == null || correo.isBlank()) {
            return true;
        }
        Optional<Usuario> encontrado = usuarioRepository.findFirstByPersonaCorreoIgnoreCase(correo.trim());
        if (encontrado.isEmpty()) {
            return true;
        }
        Usuario usuario = encontrado.get();
        if (usuario.getEstado() == null
                || !LoginServicio.ESTADO_ACTIVO.equalsIgnoreCase(usuario.getEstado().getCodigoEstado())) {
            return true;
        }

        long recientes = tokenRepository.countByUsuarioCodigoUsuarioAndFechaCreacionAfter(
                usuario.getCodigoUsuario(), LocalDateTime.now().minusHours(HORAS_VENTANA));
        if (recientes >= MAX_SOLICITUDES) {
            return false;
        }

        TokenRecuperacion token = new TokenRecuperacion();
        token.setToken(SeguridadUtil.generarToken());
        token.setUsuario(usuario);
        token.setFechaCreacion(LocalDateTime.now());
        token.setExpira(LocalDateTime.now().plusMinutes(MINUTOS_VIGENCIA));
        token.setUsado(false);
        tokenRepository.save(token);

        String enlace = urlBase + "/restablecer-clave?token=" + token.getToken();
        enviarCorreo(usuario, enlace);
        return true;
    }

    @Transactional(readOnly = true)
    public boolean tokenValido(String token) {
        return buscarVigente(token).isPresent();
    }

    @Transactional
    public boolean restablecer(String token, String clave, String confirmacion) {
        if (!claveValida(clave) || !clave.equals(confirmacion)) {
            return false;
        }
        Optional<TokenRecuperacion> vigente = buscarVigente(token);
        if (vigente.isEmpty()) {
            return false;
        }
        TokenRecuperacion recuperacion = vigente.get();
        Usuario usuario = recuperacion.getUsuario();
        usuario.setHashClave(SeguridadUtil.sha256(clave));
        usuario.setIntentosFallidos(0);
        usuario.setBloqueoHasta(null);
        usuario.setFechaModificacion(LocalDateTime.now());
        recuperacion.setUsado(true);
        usuarioRepository.save(usuario);
        tokenRepository.save(recuperacion);
        return true;
    }

    /** Minimo 8 caracteres, con al menos una letra y un numero. */
    public static boolean claveValida(String clave) {
        return clave != null && clave.length() >= 8 && clave.matches(".*[A-Za-z].*") && clave.matches(".*\\d.*");
    }

    private Optional<TokenRecuperacion> buscarVigente(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        return tokenRepository.findByToken(token.trim())
                .filter(item -> !Boolean.TRUE.equals(item.getUsado()))
                .filter(item -> item.getExpira() != null && item.getExpira().isAfter(LocalDateTime.now()));
    }

    private void enviarCorreo(Usuario usuario, String enlace) {
        String destino = usuario.getPersona().getCorreo();
        if (mailSender == null || correoEmisor == null || correoEmisor.isBlank()) {
            LOG.info("SMTP no configurado. Enlace de recuperacion para {}: {}", usuario.getUsuario(), enlace);
            return;
        }
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, false, "UTF-8");
            helper.setFrom(correoEmisor);
            helper.setTo(destino);
            helper.setSubject("SIGH - Recuperacion de clave");
            helper.setText(cuerpoCorreo(usuario, enlace), true);
            mailSender.send(mensaje);
        } catch (MailException | MessagingException ex) {
            LOG.warn("No se pudo enviar el correo ({}). Enlace para {}: {}", ex.getMessage(), usuario.getUsuario(), enlace);
        }
    }

    private String cuerpoCorreo(Usuario usuario, String enlace) {
        String nombre = usuario.getPersona().getNombres();
        return """
                <div style="font-family:Arial,sans-serif;max-width:520px;margin:auto;border:1px solid #d9e2ec;border-radius:8px;overflow:hidden">
                  <div style="background:#0f2a43;color:#fff;padding:18px 24px;font-size:18px;font-weight:bold">MONSTER &middot; SIGH</div>
                  <div style="padding:24px;color:#243b53;line-height:1.6">
                    <p>Hola %s,</p>
                    <p>Se solicito restablecer la clave del usuario <b>%s</b>. El enlace vence en %d minutos y solo puede usarse una vez.</p>
                    <p style="text-align:center;margin:28px 0">
                      <a href="%s" style="background:#127c78;color:#fff;padding:12px 22px;border-radius:6px;text-decoration:none;font-weight:bold">Restablecer clave</a>
                    </p>
                    <p style="font-size:12px;color:#627d98">Si no hiciste esta solicitud, ignora este mensaje.</p>
                  </div>
                </div>
                """.formatted(nombre, usuario.getUsuario(), MINUTOS_VIGENCIA, enlace);
    }
}
