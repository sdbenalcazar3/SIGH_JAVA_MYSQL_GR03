package edu.ec.monster.ORM_JAVA_GR03.seguridad;

import edu.ec.monster.ORM_JAVA_GR03.servicio.LoginServicio;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Controla el acceso a las rutas privadas:
 * - Sin sesion: las paginas redirigen a /login y la API responde 401.
 * - /api/departamentos requiere la opcion DEP en alguno de los perfiles vigentes (403).
 * - /cargos requiere la opcion CAR (si falta redirige a /inicio?sinPermiso) y /api/cargos/** responde 403.
 */
@Component
public class SesionInterceptor implements HandlerInterceptor {

    public static final String ATRIBUTO_USUARIO = "codigoUsuario";

    private final LoginServicio loginServicio;

    public SesionInterceptor(LoginServicio loginServicio) {
        this.loginServicio = loginServicio;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        HttpSession sesion = request.getSession(false);
        String codigoUsuario = sesion == null ? null : (String) sesion.getAttribute(ATRIBUTO_USUARIO);
        String ruta = request.getRequestURI().substring(request.getContextPath().length());
        boolean esApi = ruta.startsWith("/api/");

        if (codigoUsuario == null) {
            if (esApi) {
                responderJson(response, HttpServletResponse.SC_UNAUTHORIZED, "Debe iniciar sesion.");
            } else {
                response.sendRedirect(request.getContextPath() + "/login?expirada");
            }
            return false;
        }

        if (ruta.startsWith("/api/departamentos")
                && !loginServicio.tieneOpcion(codigoUsuario, LoginServicio.OPCION_DEPARTAMENTOS)) {
            responderJson(response, HttpServletResponse.SC_FORBIDDEN,
                    "Su perfil no tiene asignada la opcion de departamentos.");
            return false;
        }

        if (esRuta(ruta, "/api/cargos") && !loginServicio.tieneOpcion(codigoUsuario, LoginServicio.OPCION_CARGOS)) {
            responderJson(response, HttpServletResponse.SC_FORBIDDEN,
                    "Su perfil no tiene asignada la opcion de cargos.");
            return false;
        }
        if (esRuta(ruta, "/cargos") && !loginServicio.tieneOpcion(codigoUsuario, LoginServicio.OPCION_CARGOS)) {
            response.sendRedirect(request.getContextPath() + "/inicio?sinPermiso");
            return false;
        }
        return true;
    }

    /** Coincide con la ruta exacta o con una subruta (evita confundir /cargos con /cargosX). */
    private boolean esRuta(String ruta, String base) {
        return ruta.equals(base) || ruta.startsWith(base + "/");
    }

    private void responderJson(HttpServletResponse response, int estado, String mensaje) throws IOException {
        response.setStatus(estado);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"mensaje\":\"" + mensaje + "\"}");
    }
}
