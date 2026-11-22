package edu.ec.monster.ORM_JAVA_GR03.servicio;
import java.util.*;
import java.util.function.Function;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
public abstract class CrudServicio<T> {
    private final JpaRepository<T,String> repository;
    private final Function<T,String> id;
    protected CrudServicio(JpaRepository<T,String> repository, Function<T,String> id) { this.repository=repository; this.id=id; }
    @Transactional(readOnly=true)
    public List<T> listar() { return repository.findAll(); }
    @Transactional(readOnly=true)
    public T buscar(String codigo) { return repository.findById(codigo).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Registro no encontrado")); }
    @Transactional
    public T guardar(T entity) {
        if (entity instanceof edu.ec.monster.ORM_JAVA_GR03.modelo.Usuario user) {
            if (user.getHashClave()==null) repository.findById(id.apply(entity)).ifPresent(existing -> user.setHashClave(((edu.ec.monster.ORM_JAVA_GR03.modelo.Usuario)existing).getHashClave()));
            if (user.getHashClave()==null || user.getHashClave().isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"hashClave es obligatorio y no puede estar vacío");
        }
        T saved=repository.saveAndFlush(entity);
        return saved;
    }
    @Transactional
    public void eliminar(String codigo) { T entity=buscar(codigo); repository.delete(entity); repository.flush(); }
}
