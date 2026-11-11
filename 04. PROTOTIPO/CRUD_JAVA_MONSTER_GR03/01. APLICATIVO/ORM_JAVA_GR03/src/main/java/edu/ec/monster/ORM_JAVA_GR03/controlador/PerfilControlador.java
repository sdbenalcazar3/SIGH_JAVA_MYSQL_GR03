package edu.ec.monster.ORM_JAVA_GR03.controlador;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Perfil;
import edu.ec.monster.ORM_JAVA_GR03.servicio.PerfilServicio;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/perfiles")
public class PerfilControlador extends CrudControlador<Perfil> {
    public PerfilControlador(PerfilServicio servicio) { super(servicio); }
}
