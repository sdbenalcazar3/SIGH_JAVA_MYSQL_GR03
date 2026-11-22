package edu.ec.monster.ORM_JAVA_GR03.servicio;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Usuario;
import edu.ec.monster.ORM_JAVA_GR03.repository.UsuarioRepository;
import edu.ec.monster.ORM_JAVA_GR03.utils.SeguridadUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class UsuarioServicio extends CrudServicio<Usuario> {
    public UsuarioServicio(UsuarioRepository repository) { super(repository, Usuario::getCodigoUsuario); }

    /** Si la clave llega en texto plano se guarda su hash SHA-256 para que pueda usarse en el login. */
    @Override
    @Transactional
    public Usuario guardar(Usuario usuario) {
        if (usuario.getHashClave() != null && !usuario.getHashClave().isBlank()
                && !SeguridadUtil.esSha256(usuario.getHashClave())) {
            usuario.setHashClave(SeguridadUtil.sha256(usuario.getHashClave()));
        }
        return super.guardar(usuario);
    }
}
