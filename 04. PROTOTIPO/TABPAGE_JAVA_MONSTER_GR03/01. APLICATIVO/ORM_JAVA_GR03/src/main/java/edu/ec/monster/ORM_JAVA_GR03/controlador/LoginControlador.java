package edu.ec.monster.ORM_JAVA_GR03.controlador;

import edu.ec.monster.ORM_JAVA_GR03.modelo.Opcion;
import edu.ec.monster.ORM_JAVA_GR03.modelo.Usuario;
import edu.ec.monster.ORM_JAVA_GR03.seguridad.SesionInterceptor;
import edu.ec.monster.ORM_JAVA_GR03.servicio.LoginServicio;
import edu.ec.monster.ORM_JAVA_GR03.servicio.LoginServicio.Resultado;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginControlador {

    private final LoginServicio loginServicio;

    public LoginControlador(LoginServicio loginServicio) {
        this.loginServicio = loginServicio;
    }

    @GetMapping({"/", "/login"})
    public String mostrarLogin(HttpSession session) {
        if (session.getAttribute(SesionInterceptor.ATRIBUTO_USUARIO) != null) {
            return "redirect:/inicio";
        }
        return "login";
    }

    @PostMapping("/login")
    public String iniciarSesion(@RequestParam("usuario") String usuario,
            @RequestParam("clave") String clave,
            HttpServletRequest request,
            Model model) {

        Resultado resultado = loginServicio.autenticar(usuario, clave);
        switch (resultado.estado()) {
            case CORRECTO -> {
                HttpSession session = request.getSession();
                request.changeSessionId();
                Usuario autenticado = resultado.usuario();
                session.setAttribute(SesionInterceptor.ATRIBUTO_USUARIO, autenticado.getCodigoUsuario());
                session.setAttribute("nombreUsuario", autenticado.getUsuario());
                return "redirect:/inicio?bienvenida";
            }
            case BLOQUEADO -> model.addAttribute("error",
                    "Usuario bloqueado por intentos fallidos. Intente nuevamente en "
                            + resultado.minutosRestantes() + " minuto(s).");
            case INACTIVO -> model.addAttribute("error", "El usuario no se encuentra activo.");
            default -> model.addAttribute("error", resultado.intentosRestantes() > 0
                    ? "Usuario o clave incorrectos. Intentos restantes: " + resultado.intentosRestantes() + "."
                    : "Usuario o clave incorrectos.");
        }
        model.addAttribute("usuario", usuario);
        return "login";
    }

    @GetMapping("/inicio")
    public String inicio(HttpSession session, Model model) {
        String codigoUsuario = (String) session.getAttribute(SesionInterceptor.ATRIBUTO_USUARIO);
        Usuario usuario = loginServicio.buscarPorCodigo(codigoUsuario).orElse(null);
        if (usuario == null) {
            session.invalidate();
            return "redirect:/login";
        }
        List<Opcion> opciones = loginServicio.opcionesPermitidas(codigoUsuario);
        model.addAttribute("usuario", usuario);
        model.addAttribute("persona", usuario.getPersona());
        model.addAttribute("perfiles", loginServicio.perfilesVigentes(codigoUsuario));
        model.addAttribute("opciones", opciones);
        model.addAttribute("puedeGestionarDepartamentos", opciones.stream()
                .anyMatch(opcion -> LoginServicio.OPCION_DEPARTAMENTOS.equals(opcion.getCodigoOpcion())));
        model.addAttribute("puedeGestionarCargos", opciones.stream()
                .anyMatch(opcion -> LoginServicio.OPCION_CARGOS.equals(opcion.getCodigoOpcion())));
        model.addAttribute("puedeGestionarEmpleados", opciones.stream()
                .anyMatch(opcion -> LoginServicio.OPCION_EMPLEADOS.equals(opcion.getCodigoOpcion())));
        return "inicio";
    }

    @GetMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/login?salida";
    }
}
