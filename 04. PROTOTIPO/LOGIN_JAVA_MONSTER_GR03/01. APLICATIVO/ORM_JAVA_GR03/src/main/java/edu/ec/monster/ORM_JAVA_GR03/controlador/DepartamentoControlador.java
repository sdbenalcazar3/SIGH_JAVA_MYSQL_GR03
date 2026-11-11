package edu.ec.monster.ORM_JAVA_GR03.controlador;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Departamento;
import edu.ec.monster.ORM_JAVA_GR03.servicio.DepartamentoServicio;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoControlador extends CrudControlador<Departamento> {
    private final DepartamentoServicio servicio;
    public DepartamentoControlador(DepartamentoServicio servicio) { super(servicio); this.servicio = servicio; }

    /** Registro nuevo: responde 409 si el codigo ya existe. */
    @Override
    @PostMapping
    public Departamento guardar(@Valid @RequestBody Departamento departamento) { return servicio.registrar(departamento); }

    /** Edicion: el codigo de la ruta manda sobre el del cuerpo. */
    @PutMapping("/{codigo}")
    public Departamento actualizar(@PathVariable String codigo, @Valid @RequestBody Departamento departamento) {
        return servicio.actualizar(codigo, departamento);
    }
}
