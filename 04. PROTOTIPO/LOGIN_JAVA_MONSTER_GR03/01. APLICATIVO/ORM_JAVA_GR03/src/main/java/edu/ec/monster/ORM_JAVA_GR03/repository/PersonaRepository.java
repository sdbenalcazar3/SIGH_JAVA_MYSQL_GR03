package edu.ec.monster.ORM_JAVA_GR03.repository;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PersonaRepository extends JpaRepository<Persona, String> {}
