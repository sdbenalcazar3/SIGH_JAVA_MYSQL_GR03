package edu.ec.monster.ORM_JAVA_GR03.servicio;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Opcion;
import edu.ec.monster.ORM_JAVA_GR03.repository.OpcionRepository;
import org.springframework.stereotype.Service;
@Service
public class OpcionServicio extends CrudServicio<Opcion> {
    public OpcionServicio(OpcionRepository repository) { super(repository, Opcion::getCodigoOpcion); }
}
