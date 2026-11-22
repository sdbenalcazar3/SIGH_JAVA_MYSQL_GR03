package edu.ec.monster.ORM_JAVA_GR03.repository;

import edu.ec.monster.ORM_JAVA_GR03.modelo.DatoFamiliar;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DatoFamiliarRepository extends JpaRepository<DatoFamiliar, Long> {

    List<DatoFamiliar> findByPersonaCodigoPersonaOrderByCodigoFamiliarAsc(String codigoPersona);

    void deleteByPersonaCodigoPersona(String codigoPersona);

    long countByPersonaCodigoPersona(String codigoPersona);

    /** Total de registros familiares por empleado: filas [codigoPersona, total]. */
    @Query("SELECT f.persona.codigoPersona, COUNT(f) FROM DatoFamiliar f GROUP BY f.persona.codigoPersona")
    List<Object[]> contarPorPersona();
}
