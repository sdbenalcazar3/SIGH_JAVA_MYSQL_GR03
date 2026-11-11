package edu.ec.monster.ORM_JAVA_GR03.repository;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Persona;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PersonaRepository extends JpaRepository<Persona, String> {

    /** Personal asignado a un departamento, ordenado por apellidos. */
    List<Persona> findByDepartamentoCodigoDepartamentoOrderByApellidosAsc(String codigoDepartamento);

    long countByDepartamentoCodigoDepartamento(String codigoDepartamento);
}
