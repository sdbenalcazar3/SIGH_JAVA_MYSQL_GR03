package edu.ec.monster.ORM_JAVA_GR03.servicio;
import edu.ec.monster.ORM_JAVA_GR03.modelo.EstadoCivil;
import edu.ec.monster.ORM_JAVA_GR03.repository.EstadoCivilRepository;
import org.springframework.stereotype.Service;
@Service
public class EstadoCivilServicio extends CrudServicio<EstadoCivil> {
    public EstadoCivilServicio(EstadoCivilRepository repository) { super(repository, EstadoCivil::getCodigoEstadoCivil); }
}
