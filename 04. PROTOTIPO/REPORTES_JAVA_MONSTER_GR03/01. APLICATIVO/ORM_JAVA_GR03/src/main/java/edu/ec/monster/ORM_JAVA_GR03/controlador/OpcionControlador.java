package edu.ec.monster.ORM_JAVA_GR03.controlador;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Opcion;
import edu.ec.monster.ORM_JAVA_GR03.servicio.OpcionServicio;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/opciones")
public class OpcionControlador extends CrudControlador<Opcion> {
    public OpcionControlador(OpcionServicio servicio) { super(servicio); }
}
