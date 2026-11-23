package edu.ec.monster.ORM_JAVA_GR03.repository;

import edu.ec.monster.ORM_JAVA_GR03.modelo.Usuario;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, String> {

    Optional<Usuario> findByUsuarioIgnoreCase(String usuario);

    Optional<Usuario> findFirstByPersonaCorreoIgnoreCase(String correo);
}
