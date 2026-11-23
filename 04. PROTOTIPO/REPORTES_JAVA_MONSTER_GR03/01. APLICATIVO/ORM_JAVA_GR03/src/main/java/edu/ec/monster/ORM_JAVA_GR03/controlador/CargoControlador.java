package edu.ec.monster.ORM_JAVA_GR03.controlador;
import edu.ec.monster.ORM_JAVA_GR03.dto.CambioEstadoLoteRequest;
import edu.ec.monster.ORM_JAVA_GR03.dto.CargoResumen;
import edu.ec.monster.ORM_JAVA_GR03.dto.PersonalCargo;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Cargo;
import edu.ec.monster.ORM_JAVA_GR03.servicio.CargoServicio;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.*;

/**
 * Mantenimiento de cargos (PECAR_CARGO). Las rutas estan protegidas por SesionInterceptor:
 * sin sesion responden 401 y sin la opcion CAR responden 403.
 *   GET    /api/cargos                       listar
 *   GET    /api/cargos/resumen               listar con filtros (q, activo, departamento)
 *   GET    /api/cargos/siguiente-codigo      siguiente codigo Cxx disponible
 *   GET    /api/cargos/{codigo}              buscar
 *   GET    /api/cargos/{codigo}/personal     personal asignado
 *   POST   /api/cargos                       registrar
 *   PUT    /api/cargos/{codigo}              actualizar
 *   PATCH  /api/cargos/estado-lote           activar o inactivar varios cargos
 *   DELETE /api/cargos/{codigo}              eliminar
 */
@RestController
@RequestMapping("/api/cargos")
public class CargoControlador extends CrudControlador<Cargo> {
    private final CargoServicio servicio;
    public CargoControlador(CargoServicio servicio) { super(servicio); this.servicio = servicio; }

    @GetMapping("/resumen")
    public List<CargoResumen> resumen(@RequestParam(required = false) String q,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(required = false) String departamento) {
        return servicio.listarResumen(q, activo, departamento);
    }

    @GetMapping("/siguiente-codigo")
    public Map<String, String> siguienteCodigo() { return Map.of("codigo", servicio.generarSiguienteCodigo()); }

    @GetMapping("/{codigo}/personal")
    public List<PersonalCargo> personal(@PathVariable String codigo) { return servicio.personal(codigo); }

    /** Registro nuevo: responde 409 si el codigo o el nombre ya existen. */
    @Override
    @PostMapping
    public Cargo guardar(@Valid @RequestBody Cargo cargo) { return servicio.registrar(cargo); }

    /** Edicion: el codigo de la ruta manda sobre el del cuerpo. */
    @PutMapping("/{codigo}")
    public Cargo actualizar(@PathVariable String codigo, @Valid @RequestBody Cargo cargo) {
        return servicio.actualizar(codigo, cargo);
    }

    /** Cambio de estado atomico: si falta algun codigo responde 404 sin modificar ningun cargo. */
    @PatchMapping("/estado-lote")
    public List<Cargo> cambiarEstadoEnLote(@Valid @RequestBody CambioEstadoLoteRequest solicitud) {
        return servicio.cambiarEstadoEnLote(solicitud.codigos(), solicitud.activo());
    }
}
