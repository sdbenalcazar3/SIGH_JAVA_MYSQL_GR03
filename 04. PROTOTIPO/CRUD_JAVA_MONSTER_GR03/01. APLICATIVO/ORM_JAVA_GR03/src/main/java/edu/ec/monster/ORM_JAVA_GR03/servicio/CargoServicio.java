package edu.ec.monster.ORM_JAVA_GR03.servicio;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Cargo;
import edu.ec.monster.ORM_JAVA_GR03.repository.CargoRepository;
import org.springframework.stereotype.Service;
@Service
public class CargoServicio extends CrudServicio<Cargo> {
    public CargoServicio(CargoRepository repository) { super(repository, Cargo::getCodigoCargo); }
}
