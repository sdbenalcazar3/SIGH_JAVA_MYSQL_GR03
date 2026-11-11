package edu.ec.monster.ORM_JAVA_GR03.servicio;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Genero;
import edu.ec.monster.ORM_JAVA_GR03.repository.GeneroRepository;
import org.springframework.stereotype.Service;
@Service
public class GeneroServicio extends CrudServicio<Genero> {
    public GeneroServicio(GeneroRepository repository) { super(repository, Genero::getCodigoGenero); }
}
