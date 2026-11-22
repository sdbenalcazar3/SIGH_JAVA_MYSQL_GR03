package edu.ec.monster.ORM_JAVA_GR03.repository;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Persona;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    /**
     * Listado filtrable de empleados (modal Listar y navegador de registros). Ningun parametro es
     * nulo, para evitar problemas de inferencia de tipos con MariaDBLegacyDialect:
     * - patron: patron LIKE en minusculas (escape '!'); "%" no filtra por texto. Busca en codigo,
     *   codigo externo, nombres, apellidos, "nombres apellidos", "apellidos nombres" y documento.
     * - todosEstados: true ignora el parametro activo.
     * - departamento / cargo: "" = todos; otro valor = codigo exacto.
     */
    @Query("""
            SELECT p FROM Persona p
            WHERE (:todosEstados = true OR p.activo = :activo)
              AND (:departamento = '' OR p.departamento.codigoDepartamento = :departamento)
              AND (:cargo = '' OR p.cargo.codigoCargo = :cargo)
              AND (LOWER(p.codigoPersona) LIKE :patron ESCAPE '!'
                   OR LOWER(p.codigoExterno) LIKE :patron ESCAPE '!'
                   OR LOWER(p.nombres) LIKE :patron ESCAPE '!'
                   OR LOWER(p.apellidos) LIKE :patron ESCAPE '!'
                   OR LOWER(CONCAT(p.nombres, ' ', p.apellidos)) LIKE :patron ESCAPE '!'
                   OR LOWER(CONCAT(p.apellidos, ' ', p.nombres)) LIKE :patron ESCAPE '!'
                   OR LOWER(p.numeroDocumento) LIKE :patron ESCAPE '!')
            ORDER BY p.codigoPersona ASC
            """)
    List<Persona> filtrar(@Param("patron") String patron,
            @Param("todosEstados") boolean todosEstados,
            @Param("activo") boolean activo,
            @Param("departamento") String departamento,
            @Param("cargo") String cargo);

    /** Codigos correlativos candidatos (Pnnnnn); el servicio valida el patron exacto ^P\d{5}$. */
    @Query("SELECT p.codigoPersona FROM Persona p WHERE p.codigoPersona LIKE 'P_____'")
    List<String> codigosCorrelativos();

    /** Cuentas de usuario (SEUSU_USUARI) asociadas al empleado: bloquea su eliminacion. */
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.persona.codigoPersona = :codigo")
    long contarUsuariosPorPersona(@Param("codigo") String codigo);

    /** Unicidad del codigo externo sin distinguir mayusculas, excluyendo al propio empleado. */
    boolean existsByCodigoExternoIgnoreCaseAndCodigoPersonaNot(String codigoExterno, String codigoPersona);

    /** Unicidad del numero de documento, excluyendo al propio empleado. */
    boolean existsByNumeroDocumentoAndCodigoPersonaNot(String numeroDocumento, String codigoPersona);

    /** Unicidad del correo sin distinguir mayusculas, excluyendo al propio empleado. */
    boolean existsByCorreoIgnoreCaseAndCodigoPersonaNot(String correo, String codigoPersona);
}
