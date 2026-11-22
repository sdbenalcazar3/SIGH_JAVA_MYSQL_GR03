package edu.ec.monster.ORM_JAVA_GR03.controlador;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Persona;
import edu.ec.monster.ORM_JAVA_GR03.servicio.PersonaServicio;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/empleados")
public class PersonaControlador extends CrudControlador<Persona> {
    public PersonaControlador(PersonaServicio servicio) { super(servicio); }
}
