package edu.ec.monster.ORM_JAVA_GR03.controlador;
import edu.ec.monster.ORM_JAVA_GR03.modelo.EstadoCivil;
import edu.ec.monster.ORM_JAVA_GR03.servicio.EstadoCivilServicio;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/estados-civiles")
public class EstadoCivilControlador extends CrudControlador<EstadoCivil> {
    public EstadoCivilControlador(EstadoCivilServicio servicio) { super(servicio); }
}
