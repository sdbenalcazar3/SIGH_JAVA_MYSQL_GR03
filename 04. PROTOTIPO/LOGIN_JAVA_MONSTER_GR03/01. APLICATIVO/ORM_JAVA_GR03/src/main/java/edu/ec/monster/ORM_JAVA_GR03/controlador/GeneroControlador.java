package edu.ec.monster.ORM_JAVA_GR03.controlador;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Genero;
import edu.ec.monster.ORM_JAVA_GR03.servicio.GeneroServicio;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/generos")
public class GeneroControlador extends CrudControlador<Genero> {
    public GeneroControlador(GeneroServicio servicio) { super(servicio); }
}
