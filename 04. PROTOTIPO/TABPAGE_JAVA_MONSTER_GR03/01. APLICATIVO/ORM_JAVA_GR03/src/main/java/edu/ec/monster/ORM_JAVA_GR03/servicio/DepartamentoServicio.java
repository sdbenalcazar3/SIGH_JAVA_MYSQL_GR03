package edu.ec.monster.ORM_JAVA_GR03.servicio;
import edu.ec.monster.ORM_JAVA_GR03.dto.DepartamentoResumen;
import edu.ec.monster.ORM_JAVA_GR03.dto.PersonalDepartamento;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Departamento;
import edu.ec.monster.ORM_JAVA_GR03.repository.DepartamentoRepository;
import edu.ec.monster.ORM_JAVA_GR03.repository.PersonaRepository;
import java.util.Comparator;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service
public class DepartamentoServicio extends CrudServicio<Departamento> {
    private final DepartamentoRepository repository;
    private final PersonaRepository personaRepository;
    public DepartamentoServicio(DepartamentoRepository repository, PersonaRepository personaRepository) {
        super(repository, Departamento::getCodigoDepartamento);
        this.repository = repository;
        this.personaRepository = personaRepository;
    }

    /** Listado para la tabla del CRUD con el numero de personas de cada departamento. */
    @Transactional(readOnly = true)
    public List<DepartamentoResumen> listarResumen() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(Departamento::getCodigoDepartamento))
                .map(d -> new DepartamentoResumen(d.getCodigoDepartamento(), d.getNombre(), d.getActivo(),
                        personaRepository.countByDepartamentoCodigoDepartamento(d.getCodigoDepartamento())))
                .toList();
    }

    /** Personal asignado (PEPER_PERSON) al departamento indicado. */
    @Transactional(readOnly = true)
    public List<PersonalDepartamento> personal(String codigo) {
        buscar(codigo);
        return personaRepository.findByDepartamentoCodigoDepartamentoOrderByApellidosAsc(codigo).stream()
                .map(p -> new PersonalDepartamento(p.getCodigoPersona(), p.getNombres(), p.getApellidos(),
                        p.getCargo() == null ? null : p.getCargo().getNombre(), p.getFuncion(), p.getCorreo(), p.getActivo()))
                .toList();
    }

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

    /** No se elimina un departamento con personal: se sugiere marcarlo como inactivo. */
    @Override
    @Transactional
    public void eliminar(String codigo) {
        buscar(codigo);
        long personal = personaRepository.countByDepartamentoCodigoDepartamento(codigo);
        if (personal > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "El departamento tiene " + personal + " persona(s) asignada(s). Puede marcarlo como inactivo.");
        }
        super.eliminar(codigo);
    }

    private void normalizar(Departamento departamento) {
        String codigo = departamento.getCodigoDepartamento().trim().toUpperCase();
        if (!codigo.matches("[A-Z0-9]{3}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El codigo debe tener 3 letras o numeros, por ejemplo D04.");
        }
        departamento.setCodigoDepartamento(codigo);
        departamento.setNombre(departamento.getNombre().trim().replaceAll("\\s+", " "));
        if (departamento.getNombre().length() < 3) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre debe tener al menos 3 caracteres.");
        }
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
