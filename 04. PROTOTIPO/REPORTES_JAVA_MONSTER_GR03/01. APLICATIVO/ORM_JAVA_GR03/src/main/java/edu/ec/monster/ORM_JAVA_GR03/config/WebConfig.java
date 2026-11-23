package edu.ec.monster.ORM_JAVA_GR03.config;

import edu.ec.monster.ORM_JAVA_GR03.seguridad.SesionInterceptor;
import java.nio.file.Path;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final SesionInterceptor sesionInterceptor;
    private final String directorioFotos;

    public WebConfig(SesionInterceptor sesionInterceptor,
            @Value("${sigh.uploads.dir:uploads/empleados}") String directorioFotos) {
        this.sesionInterceptor = sesionInterceptor;
        this.directorioFotos = directorioFotos;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sesionInterceptor)
                .addPathPatterns("/inicio", "/cargos", "/empleados", "/reportes", "/api/**");
    }

    /** Publica las fotos de empleados guardadas en disco bajo /uploads/empleados/**. */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String ubicacion = Path.of(directorioFotos).toAbsolutePath().normalize().toUri().toString();
        if (!ubicacion.endsWith("/")) {
            ubicacion += "/";
        }
        registry.addResourceHandler("/uploads/empleados/**").addResourceLocations(ubicacion);
    }
}
