package edu.ec.monster.ORM_JAVA_GR03.servicio;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Sistema;
import edu.ec.monster.ORM_JAVA_GR03.repository.SistemaRepository;
import org.springframework.stereotype.Service;
@Service
public class SistemaServicio extends CrudServicio<Sistema> {
    public SistemaServicio(SistemaRepository repository) { super(repository, Sistema::getCodigoSistema); }
}
