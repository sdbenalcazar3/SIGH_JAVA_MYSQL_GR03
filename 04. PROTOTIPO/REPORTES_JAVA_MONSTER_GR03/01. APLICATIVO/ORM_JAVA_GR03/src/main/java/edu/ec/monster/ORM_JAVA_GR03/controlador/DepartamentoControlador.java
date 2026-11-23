package edu.ec.monster.ORM_JAVA_GR03.controlador;
import edu.ec.monster.ORM_JAVA_GR03.dto.DepartamentoResumen;
import edu.ec.monster.ORM_JAVA_GR03.dto.PersonalDepartamento;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Departamento;
import edu.ec.monster.ORM_JAVA_GR03.servicio.DepartamentoServicio;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.*;

/**
 * CRUD de departamentos (PEDEP_DEPART). Las rutas estan protegidas por SesionInterceptor:
 * sin sesion responden 401 y sin la opcion DEP responden 403.
 *   GET    /api/departamentos                    listar
 *   GET    /api/departamentos/resumen            listar con total de personal
 *   GET    /api/departamentos/{codigo}           buscar
 *   GET    /api/departamentos/{codigo}/personal  personal asignado
 *   POST   /api/departamentos                    registrar
 *   PUT    /api/departamentos/{codigo}           actualizar
 *   DELETE /api/departamentos/{codigo}           eliminar
 */
@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoControlador extends CrudControlador<Departamento> {
    private final DepartamentoServicio servicio;
    public DepartamentoControlador(DepartamentoServicio servicio) { super(servicio); this.servicio = servicio; }

    @GetMapping("/resumen")
    public List<DepartamentoResumen> resumen() { return servicio.listarResumen(); }

    @GetMapping("/{codigo}/personal")
    public List<PersonalDepartamento> personal(@PathVariable String codigo) { return servicio.personal(codigo.toUpperCase()); }

    /** Registro nuevo: responde 409 si el codigo ya existe. */
    @Override
    @PostMapping
    public Departamento guardar(@Valid @RequestBody Departamento departamento) { return servicio.registrar(departamento); }

    /** Edicion: el codigo de la ruta manda sobre el del cuerpo. */
    @PutMapping("/{codigo}")
    public Departamento actualizar(@PathVariable String codigo, @Valid @RequestBody Departamento departamento) {
        return servicio.actualizar(codigo.toUpperCase(), departamento);
    }
}
