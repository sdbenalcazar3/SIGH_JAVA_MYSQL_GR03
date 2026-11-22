package edu.ec.monster.ORM_JAVA_GR03.repository;

import edu.ec.monster.ORM_JAVA_GR03.modelo.DatoAcademico;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DatoAcademicoRepository extends JpaRepository<DatoAcademico, Long> {

    List<DatoAcademico> findByPersonaCodigoPersonaOrderByCodigoAcademicoAsc(String codigoPersona);

    void deleteByPersonaCodigoPersona(String codigoPersona);

    long countByPersonaCodigoPersona(String codigoPersona);

    /** Total de registros académicos por empleado: filas [codigoPersona, total]. */
    @Query("SELECT a.persona.codigoPersona, COUNT(a) FROM DatoAcademico a GROUP BY a.persona.codigoPersona")
    List<Object[]> contarPorPersona();
}
