package edu.ec.monster.ORM_JAVA_GR03.controlador;

import edu.ec.monster.ORM_JAVA_GR03.modelo.Cargo;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Departamento;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Usuario;
import edu.ec.monster.ORM_JAVA_GR03.seguridad.SesionInterceptor;
import edu.ec.monster.ORM_JAVA_GR03.servicio.CargoServicio;
import edu.ec.monster.ORM_JAVA_GR03.servicio.DepartamentoServicio;
import edu.ec.monster.ORM_JAVA_GR03.servicio.LoginServicio;
import jakarta.servlet.http.HttpSession;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador MVC para la vista protegida /WEB-INF/jsp/reportes.jsp.
 * Carga los catalogos SSR de departamentos y cargos usados por los filtros de los reportes.
 */
@Controller
public class ReporteVistaControlador {

    private final LoginServicio loginServicio;
    private final DepartamentoServicio departamentoServicio;
    private final CargoServicio cargoServicio;

    public ReporteVistaControlador(LoginServicio loginServicio, DepartamentoServicio departamentoServicio,
            CargoServicio cargoServicio) {
        this.loginServicio = loginServicio;
        this.departamentoServicio = departamentoServicio;
        this.cargoServicio = cargoServicio;
    }

    @GetMapping("/reportes")
    public String mostrarReportes(HttpSession session, Model model) {
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
        model.addAttribute("listaDepartamentos", listaDepartamentos);
        model.addAttribute("listaCargos", listaCargos);
        return "reportes";
    }
}
