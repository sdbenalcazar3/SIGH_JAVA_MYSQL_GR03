package edu.ec.monster.ORM_JAVA_GR03.controlador;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Estado;
import edu.ec.monster.ORM_JAVA_GR03.servicio.EstadoServicio;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/estados")
public class EstadoControlador extends CrudControlador<Estado> {
    public EstadoControlador(EstadoServicio servicio) { super(servicio); }
}
