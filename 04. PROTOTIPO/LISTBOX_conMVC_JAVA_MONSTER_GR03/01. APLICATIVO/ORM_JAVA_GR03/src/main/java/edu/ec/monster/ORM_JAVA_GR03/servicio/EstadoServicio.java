package edu.ec.monster.ORM_JAVA_GR03.servicio;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Estado;
import edu.ec.monster.ORM_JAVA_GR03.repository.EstadoRepository;
import org.springframework.stereotype.Service;
@Service
public class EstadoServicio extends CrudServicio<Estado> {
    public EstadoServicio(EstadoRepository repository) { super(repository, Estado::getCodigoEstado); }
}
