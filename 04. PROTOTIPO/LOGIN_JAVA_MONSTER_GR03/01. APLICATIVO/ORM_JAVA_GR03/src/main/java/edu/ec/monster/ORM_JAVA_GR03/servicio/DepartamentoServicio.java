package edu.ec.monster.ORM_JAVA_GR03.servicio;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Departamento;
import edu.ec.monster.ORM_JAVA_GR03.repository.DepartamentoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service
public class DepartamentoServicio extends CrudServicio<Departamento> {
    private final DepartamentoRepository repository;
    public DepartamentoServicio(DepartamentoRepository repository) { super(repository, Departamento::getCodigoDepartamento); this.repository = repository; }

    @Transactional
    public Departamento registrar(Departamento departamento) {
        normalizar(departamento);
        if (repository.existsById(departamento.getCodigoDepartamento())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un departamento con el codigo " + departamento.getCodigoDepartamento() + ".");
        }
        return guardar(departamento);
    }

    @Transactional
    public Departamento actualizar(String codigo, Departamento departamento) {
        buscar(codigo);
        departamento.setCodigoDepartamento(codigo);
        normalizar(departamento);
        return guardar(departamento);
    }

    private void normalizar(Departamento departamento) {
        departamento.setCodigoDepartamento(departamento.getCodigoDepartamento().trim().toUpperCase());
        departamento.setNombre(departamento.getNombre().trim());
        if (departamento.getNombre().matches(".*\\d.*")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre del departamento no debe contener numeros.");
        }
        boolean nombreRepetido = repository.findAll().stream()
                .anyMatch(item -> item.getNombre().equalsIgnoreCase(departamento.getNombre())
                        && !item.getCodigoDepartamento().equals(departamento.getCodigoDepartamento()));
        if (nombreRepetido) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe un departamento con ese nombre.");
        }
    }
}
