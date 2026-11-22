package edu.ec.monster.ORM_JAVA_GR03.controlador;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Sistema;
import edu.ec.monster.ORM_JAVA_GR03.servicio.SistemaServicio;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/sistemas")
public class SistemaControlador extends CrudControlador<Sistema> {
    public SistemaControlador(SistemaServicio servicio) { super(servicio); }
}
