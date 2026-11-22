package edu.ec.monster.ORM_JAVA_GR03.servicio;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Perfil;
import edu.ec.monster.ORM_JAVA_GR03.repository.PerfilRepository;
import org.springframework.stereotype.Service;
@Service
public class PerfilServicio extends CrudServicio<Perfil> {
    public PerfilServicio(PerfilRepository repository) { super(repository, Perfil::getCodigoPerfil); }
}
