package edu.ec.monster.ORM_JAVA_GR03.repository;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Persona;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PersonaRepository extends JpaRepository<Persona, String> {

    /** Personal asignado a un departamento, ordenado por apellidos. */
    List<Persona> findByDepartamentoCodigoDepartamentoOrderByApellidosAsc(String codigoDepartamento);

    long countByDepartamentoCodigoDepartamento(String codigoDepartamento);

    /** Personal asignado a un cargo, ordenado por apellidos. */
    List<Persona> findByCargoCodigoCargoOrderByApellidosAsc(String codigoCargo);

    long countByCargoCodigoCargo(String codigoCargo);

    /** Total de personal por cargo: filas [codigoCargo, total]. Evita una consulta por cargo. */
    @Query("SELECT p.cargo.codigoCargo, COUNT(p) FROM Persona p GROUP BY p.cargo.codigoCargo")
    List<Object[]> contarPersonalPorCargo();

    /** Departamentos distintos donde cada cargo tiene personal: filas [codigoCargo, codigoDepartamento, nombre]. */
    @Query("""
            SELECT DISTINCT p.cargo.codigoCargo, p.departamento.codigoDepartamento, p.departamento.nombre
            FROM Persona p
            ORDER BY p.cargo.codigoCargo, p.departamento.codigoDepartamento
            """)
    List<Object[]> departamentosDistintosPorCargo();
}
