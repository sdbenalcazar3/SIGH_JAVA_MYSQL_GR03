package edu.ec.monster.ORM_JAVA_GR03.controlador;

import edu.ec.monster.ORM_JAVA_GR03.modelo.Cargo;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Departamento;
import edu.ec.monster.ORM_JAVA_GR03.modelo.EstadoCivil;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Genero;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Usuario;
import edu.ec.monster.ORM_JAVA_GR03.seguridad.SesionInterceptor;
import edu.ec.monster.ORM_JAVA_GR03.servicio.CargoServicio;
import edu.ec.monster.ORM_JAVA_GR03.servicio.DepartamentoServicio;
import edu.ec.monster.ORM_JAVA_GR03.servicio.EstadoCivilServicio;
import edu.ec.monster.ORM_JAVA_GR03.servicio.GeneroServicio;
import edu.ec.monster.ORM_JAVA_GR03.servicio.LoginServicio;
import jakarta.servlet.http.HttpSession;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador MVC para la vista protegida /WEB-INF/jsp/empleados.jsp (Módulo TabPage).
 * Carga los catálogos iniciales SSR ordenados por código para los combos del formulario y modal.
 */
@Controller
public class EmpleadoVistaControlador {

    public static final List<String> TIPOS_SANGRE = List.of(
            "O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-");

    public static final List<String> NIVELES_ACADEMICOS = List.of(
            "Bachiller", "Tecnólogo", "Tercer Nivel", "Especialidad", "Maestría", "Doctorado");

    public static final List<String> PARENTESCOS = List.of(
            "Cónyuge", "Hijo/a", "Padre", "Madre", "Hermano/a", "Otro");

    private final LoginServicio loginServicio;
    private final GeneroServicio generoServicio;
    private final EstadoCivilServicio estadoCivilServicio;
    private final DepartamentoServicio departamentoServicio;
    private final CargoServicio cargoServicio;

    public EmpleadoVistaControlador(LoginServicio loginServicio,
            GeneroServicio generoServicio,
            EstadoCivilServicio estadoCivilServicio,
            DepartamentoServicio departamentoServicio,
            CargoServicio cargoServicio) {
        this.loginServicio = loginServicio;
        this.generoServicio = generoServicio;
        this.estadoCivilServicio = estadoCivilServicio;
        this.departamentoServicio = departamentoServicio;
        this.cargoServicio = cargoServicio;
    }

    @GetMapping("/empleados")
    public String mostrarEmpleados(HttpSession session, Model model) {
        String codigoUsuario = session == null
                ? null
                : (String) session.getAttribute(SesionInterceptor.ATRIBUTO_USUARIO);
        Usuario usuario = codigoUsuario == null
                ? null
                : loginServicio.buscarPorCodigo(codigoUsuario).orElse(null);
        if (usuario == null) {
            if (session != null) {
                session.invalidate();
            }
            return "redirect:/login?expirada";
        }

        List<Genero> listaGeneros = generoServicio.listar().stream()
                .filter(g -> Boolean.TRUE.equals(g.getActivo()))
                .sorted(Comparator.comparing(Genero::getCodigoGenero,
                        Comparator.nullsLast(String::compareTo)))
                .toList();
        List<EstadoCivil> listaEstadosCiviles = estadoCivilServicio.listar().stream()
                .filter(e -> Boolean.TRUE.equals(e.getActivo()))
                .sorted(Comparator.comparing(EstadoCivil::getCodigoEstadoCivil,
                        Comparator.nullsLast(String::compareTo)))
                .toList();
        List<Departamento> listaDepartamentos = departamentoServicio.listar().stream()
                .sorted(Comparator.comparing(Departamento::getCodigoDepartamento,
                        Comparator.nullsLast(String::compareTo)))
                .toList();
        List<Cargo> listaCargos = cargoServicio.listar().stream()
                .sorted(Comparator.comparing(Cargo::getCodigoCargo,
                        Comparator.nullsLast(String::compareTo)))
                .toList();

        model.addAttribute("usuario", usuario);
        model.addAttribute("persona", usuario.getPersona());
        model.addAttribute("listaGeneros", listaGeneros);
        model.addAttribute("listaEstadosCiviles", listaEstadosCiviles);
        model.addAttribute("listaDepartamentos", listaDepartamentos);
        model.addAttribute("listaCargos", listaCargos);
        model.addAttribute("tiposSangre", TIPOS_SANGRE);
        model.addAttribute("nivelesAcademicos", NIVELES_ACADEMICOS);
        model.addAttribute("parentescos", PARENTESCOS);
        return "empleados";
    }
}
