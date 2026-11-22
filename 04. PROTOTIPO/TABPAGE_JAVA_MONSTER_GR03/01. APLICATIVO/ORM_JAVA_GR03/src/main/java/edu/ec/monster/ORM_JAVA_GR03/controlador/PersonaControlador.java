package edu.ec.monster.ORM_JAVA_GR03.controlador;
import edu.ec.monster.ORM_JAVA_GR03.dto.DatoAcademicoDto;
import edu.ec.monster.ORM_JAVA_GR03.dto.DatoFamiliarDto;
import edu.ec.monster.ORM_JAVA_GR03.dto.EmpleadoDetalle;
import edu.ec.monster.ORM_JAVA_GR03.dto.EmpleadoGuardarRequest;
import edu.ec.monster.ORM_JAVA_GR03.dto.EmpleadoResumen;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Persona;
import edu.ec.monster.ORM_JAVA_GR03.servicio.PersonaServicio;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * API de empleados. Conserva el CRUD generico (GET/POST/DELETE /api/empleados) y agrega los
 * endpoints del TabPage: resumen filtrable, correlativo, ficha maestro-detalle, foto y
 * registros academicos/familiares.
 */
@RestController
@RequestMapping("/api/empleados")
public class PersonaControlador extends CrudControlador<Persona> {

    private final PersonaServicio servicio;

    public PersonaControlador(PersonaServicio servicio) {
        super(servicio);
        this.servicio = servicio;
    }

    @GetMapping("/resumen")
    public List<EmpleadoResumen> resumen(@RequestParam(required = false) String q,
            @RequestParam(required = false) String departamento,
            @RequestParam(required = false) String cargo,
            @RequestParam(required = false) Boolean activo) {
        return servicio.listarResumen(q, departamento, cargo, activo);
    }

    @GetMapping("/siguiente-codigo")
    public Map<String, String> siguienteCodigo() {
        return Map.of("codigo", servicio.generarSiguienteCodigo());
    }

    @GetMapping("/{codigo}/detalle")
    public EmpleadoDetalle detalle(@PathVariable String codigo) {
        return servicio.obtenerDetalle(codigo);
    }

    @PostMapping("/ficha")
    public EmpleadoDetalle registrarFicha(@Valid @RequestBody EmpleadoGuardarRequest solicitud) {
        return servicio.registrarFicha(solicitud);
    }

    /** El codigo de la ruta prevalece sobre el del cuerpo. */
    @PutMapping("/{codigo}/ficha")
    public EmpleadoDetalle actualizarFicha(@PathVariable String codigo,
            @Valid @RequestBody EmpleadoGuardarRequest solicitud) {
        return servicio.actualizarFicha(codigo, solicitud);
    }

    @PostMapping("/{codigo}/foto")
    public Map<String, String> subirFoto(@PathVariable String codigo, @RequestParam("archivo") MultipartFile archivo) {
        return Map.of("foto", servicio.guardarFoto(codigo, archivo));
    }

    @DeleteMapping("/{codigo}/foto")
    public ResponseEntity<Void> quitarFoto(@PathVariable String codigo) {
        servicio.eliminarFoto(codigo);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{codigo}/academicos")
    public DatoAcademicoDto agregarAcademico(@PathVariable String codigo, @Valid @RequestBody DatoAcademicoDto dto) {
        return servicio.agregarAcademico(codigo, dto);
    }

    @DeleteMapping("/{codigo}/academicos/{id}")
    public ResponseEntity<Void> quitarAcademico(@PathVariable String codigo, @PathVariable Long id) {
        servicio.eliminarAcademico(codigo, id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{codigo}/familiares")
    public DatoFamiliarDto agregarFamiliar(@PathVariable String codigo, @Valid @RequestBody DatoFamiliarDto dto) {
        return servicio.agregarFamiliar(codigo, dto);
    }

    @DeleteMapping("/{codigo}/familiares/{id}")
    public ResponseEntity<Void> quitarFamiliar(@PathVariable String codigo, @PathVariable Long id) {
        servicio.eliminarFamiliar(codigo, id);
        return ResponseEntity.noContent().build();
    }
}
