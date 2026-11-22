package edu.ec.monster.ORM_JAVA_GR03.controlador;

import edu.ec.monster.ORM_JAVA_GR03.servicio.RecuperacionClaveServicio;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
public class RecuperacionClaveControlador {

    private final RecuperacionClaveServicio recuperacionServicio;

    public RecuperacionClaveControlador(RecuperacionClaveServicio recuperacionServicio) {
        this.recuperacionServicio = recuperacionServicio;
    }

    @GetMapping("/recuperar-clave")
    public String mostrarSolicitud() {
        return "recuperar-clave";
    }

    @PostMapping("/recuperar-clave")
    public String solicitar(@RequestParam("correo") String correo, HttpServletRequest request, Model model) {
        String urlBase = ServletUriComponentsBuilder.fromContextPath(request).toUriString();
        if (!recuperacionServicio.solicitar(correo, urlBase)) {
            model.addAttribute("error", "Se alcanzo el limite de solicitudes. Intente nuevamente en una hora.");
        } else {
            model.addAttribute("mensaje",
                    "Si el correo pertenece a un usuario activo, recibira un enlace para restablecer la clave.");
        }
        return "recuperar-clave";
    }

    @GetMapping("/restablecer-clave")
    public String mostrarRestablecer(@RequestParam(value = "token", required = false) String token, Model model) {
        model.addAttribute("token", token);
        model.addAttribute("tokenValido", recuperacionServicio.tokenValido(token));
        return "restablecer-clave";
    }

    @PostMapping("/restablecer-clave")
    public String restablecer(@RequestParam("token") String token,
            @RequestParam("clave") String clave,
            @RequestParam("confirmacion") String confirmacion,
            Model model) {
        if (!RecuperacionClaveServicio.claveValida(clave)) {
            model.addAttribute("error", "La clave debe tener minimo 8 caracteres, con letras y numeros.");
        } else if (!clave.equals(confirmacion)) {
            model.addAttribute("error", "Las claves no coinciden.");
        } else if (recuperacionServicio.restablecer(token, clave, confirmacion)) {
            return "redirect:/login?restablecida";
        } else {
            model.addAttribute("error", "El enlace ya fue usado o vencio. Solicite uno nuevo.");
        }
        model.addAttribute("token", token);
        model.addAttribute("tokenValido", recuperacionServicio.tokenValido(token));
        return "restablecer-clave";
    }
}
