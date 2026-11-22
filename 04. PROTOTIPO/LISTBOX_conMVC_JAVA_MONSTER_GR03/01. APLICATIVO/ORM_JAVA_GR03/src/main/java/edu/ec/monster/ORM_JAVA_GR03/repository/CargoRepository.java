package edu.ec.monster.ORM_JAVA_GR03.repository;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Cargo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CargoRepository extends JpaRepository<Cargo, String> {

    /** Valor del filtro de departamento que selecciona los cargos sin personal en PEPER_PERSON. */
    String SIN_PERSONAL = "SIN_PERSONAL";

    /**
     * Filtrado combinado del ListBox. Todos los parametros son no nulos para evitar problemas de
     * inferencia de tipos con MariaDBLegacyDialect:
     * - patron: patron LIKE en minusculas (escape '!'); "%" no filtra por texto.
     * - todosEstados: true ignora el parametro activo.
     * - departamento: "" = todos, SIN_PERSONAL = cargos sin personal, otro valor = codigo PEDEP_CODIGO
     *   (cargos ocupados por personal de ese departamento).
     */
    @Query("""
            SELECT c FROM Cargo c
            WHERE (:todosEstados = true OR c.activo = :activo)
              AND (LOWER(c.codigoCargo) LIKE :patron ESCAPE '!' OR LOWER(c.nombre) LIKE :patron ESCAPE '!')
              AND (:departamento = ''
                   OR (:departamento = 'SIN_PERSONAL'
                       AND NOT EXISTS (SELECT 1 FROM Persona p WHERE p.cargo = c))
                   OR (:departamento <> 'SIN_PERSONAL'
                       AND EXISTS (SELECT 1 FROM Persona p
                                   WHERE p.cargo = c AND p.departamento.codigoDepartamento = :departamento)))
            ORDER BY c.codigoCargo ASC
            """)
    List<Cargo> filtrar(@Param("patron") String patron,
            @Param("todosEstados") boolean todosEstados,
            @Param("activo") boolean activo,
            @Param("departamento") String departamento);

    /** Unicidad de nombre sin distinguir mayusculas, excluyendo el propio cargo en una edicion. */
    boolean existsByNombreIgnoreCaseAndCodigoCargoNot(String nombre, String codigoCargo);

    /** Codigos correlativos candidatos (Cxx); el servicio valida el patron exacto ^C\d{2}$. */
    @Query("SELECT c.codigoCargo FROM Cargo c WHERE c.codigoCargo LIKE 'C__'")
    List<String> codigosCorrelativos();
}
