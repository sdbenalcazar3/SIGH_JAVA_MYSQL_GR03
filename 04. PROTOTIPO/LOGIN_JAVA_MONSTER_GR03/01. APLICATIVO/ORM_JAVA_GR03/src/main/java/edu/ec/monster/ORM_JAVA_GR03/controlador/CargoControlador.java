package edu.ec.monster.ORM_JAVA_GR03.controlador;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Cargo;
import edu.ec.monster.ORM_JAVA_GR03.servicio.CargoServicio;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/cargos")
public class CargoControlador extends CrudControlador<Cargo> {
    public CargoControlador(CargoServicio servicio) { super(servicio); }
}
