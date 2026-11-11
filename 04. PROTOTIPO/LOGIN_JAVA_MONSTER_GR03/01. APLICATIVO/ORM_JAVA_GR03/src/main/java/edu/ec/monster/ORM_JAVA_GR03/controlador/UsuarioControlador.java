package edu.ec.monster.ORM_JAVA_GR03.controlador;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Usuario;
import edu.ec.monster.ORM_JAVA_GR03.servicio.UsuarioServicio;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioControlador extends CrudControlador<Usuario> {
    public UsuarioControlador(UsuarioServicio servicio) { super(servicio); }
}
