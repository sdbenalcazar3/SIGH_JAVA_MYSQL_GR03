package edu.ec.monster.ORM_JAVA_GR03.controlador;
import edu.ec.monster.ORM_JAVA_GR03.servicio.CrudServicio;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
public abstract class CrudControlador<T> {
    private final CrudServicio<T> servicio;
    protected CrudControlador(CrudServicio<T> servicio) { this.servicio=servicio; }
    @GetMapping public List<T> listar() { return servicio.listar(); }
    @GetMapping("/{codigo}") public T buscar(@PathVariable String codigo) { return servicio.buscar(codigo); }
    @PostMapping public T guardar(@Valid @RequestBody T entity) { return servicio.guardar(entity); }
    @DeleteMapping("/{codigo}") public ResponseEntity<Void> eliminar(@PathVariable String codigo) { servicio.eliminar(codigo); return ResponseEntity.noContent().build(); }
}
