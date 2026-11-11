package edu.ec.monster.ORM_JAVA_GR03.repository;

import edu.ec.monster.ORM_JAVA_GR03.modelo.TokenRecuperacion;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRecuperacionRepository extends JpaRepository<TokenRecuperacion, Long> {

    Optional<TokenRecuperacion> findByToken(String token);

    long countByUsuarioCodigoUsuarioAndFechaCreacionAfter(String codigoUsuario, LocalDateTime desde);
}
