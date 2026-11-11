package edu.ec.monster.ORM_JAVA_GR03.servicio;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Persona;
import edu.ec.monster.ORM_JAVA_GR03.repository.PersonaRepository;
import org.springframework.stereotype.Service;
@Service
public class PersonaServicio extends CrudServicio<Persona> {
    public PersonaServicio(PersonaRepository repository) { super(repository, Persona::getCodigoPersona); }
}
