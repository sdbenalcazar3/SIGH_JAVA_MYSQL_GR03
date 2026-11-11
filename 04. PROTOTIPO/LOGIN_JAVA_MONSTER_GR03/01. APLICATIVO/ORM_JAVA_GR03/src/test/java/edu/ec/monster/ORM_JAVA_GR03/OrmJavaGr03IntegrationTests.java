package edu.ec.monster.ORM_JAVA_GR03;

import java.net.CookieManager;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba real contra la base preparada por los scripts del taller.
 * Desde el login la API requiere sesion, por eso cada prueba inicia sesion primero.
 * Usuario y clave se pueden cambiar con SIGH_TEST_USER y SIGH_TEST_CLAVE.
 */
@EnabledIfEnvironmentVariable(named="SIGH_INTEGRATION", matches="true")
@SpringBootTest(webEnvironment=SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrmJavaGr03IntegrationTests {
    @LocalServerPort int port;
    HttpClient client;
    final String usuarioPrueba=System.getenv().getOrDefault("SIGH_TEST_USER","demo.sigh");
    final String clavePrueba=System.getenv().getOrDefault("SIGH_TEST_CLAVE","Sigh2026");

    @BeforeEach void nuevoCliente() {
        client=HttpClient.newBuilder().cookieHandler(new CookieManager()).followRedirects(HttpClient.Redirect.NEVER).build();
    }

    HttpResponse<String> call(String method,String path,String body) throws Exception {
        var builder=HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+port+"/api/"+path));
        builder.header("Content-Type","application/json");
        return client.send(builder.method(method,body==null?HttpRequest.BodyPublishers.noBody():HttpRequest.BodyPublishers.ofString(body)).build(),HttpResponse.BodyHandlers.ofString());
    }

    HttpResponse<String> login(String usuario,String clave) throws Exception {
        String form="usuario="+URLEncoder.encode(usuario,StandardCharsets.UTF_8)+"&clave="+URLEncoder.encode(clave,StandardCharsets.UTF_8);
        var request=HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+port+"/login"))
                .header("Content-Type","application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(form)).build();
        return client.send(request,HttpResponse.BodyHandlers.ofString());
    }

    @Test void loginProtegeLaApi() throws Exception {
        assertEquals(401,call("GET","usuarios",null).statusCode(),"Sin sesion la API debe responder 401");
        var fallido=login(usuarioPrueba,"clave-incorrecta");
        assertEquals(200,fallido.statusCode());
        assertTrue(fallido.body().contains("incorrectos"));
        var correcto=login(usuarioPrueba,clavePrueba);
        assertEquals(302,correcto.statusCode());
        assertTrue(correcto.headers().firstValue("Location").orElse("").contains("/inicio"));
        assertEquals(200,call("GET","usuarios",null).statusCode());
    }

    @Test void operacionesYRelacionesSobreMariaDB() throws Exception {
        assertEquals(302,login(usuarioPrueba,clavePrueba).statusCode(),"No se pudo iniciar sesion con el usuario de prueba");
        for (String route : new String[]{"usuarios","empleados","perfiles","opciones","estados","sistemas","generos","estados-civiles","departamentos","cargos"})
            assertEquals(200,call("GET",route,null).statusCode(),route);
        var usuario=call("GET","usuarios/U00001",null);
        assertEquals(200,usuario.statusCode());
        assertTrue(usuario.body().contains("P00001"));
        assertTrue(usuario.body().contains("UP0001"));
        assertTrue(usuario.body().contains("OP0001"));
        assertFalse(usuario.body().contains("hashClave"));
        assertFalse(usuario.body().contains("DEMO_NO_AUTENTICABLE"));
        assertEquals(404,call("GET","usuarios/NOEXIS",null).statusCode());
        assertEquals(404,call("DELETE","estados/Z",null).statusCode());
        assertEquals(404,call("GET","empleados/PT0001",null).statusCode(),"El código de prueba debe estar libre");
        assertEquals(404,call("GET","usuarios/UT0001",null).statusCode(),"El código de prueba debe estar libre");
        assertEquals(400,call("POST","estados","{\"codigoEstado\":\"XX\",\"nombre\":\"Inválido\",\"activo\":true}").statusCode());
        assertEquals(409,call("DELETE","estados/A",null).statusCode());
        String persona="{\"codigoPersona\":\"PT0001\",\"codigoExterno\":\"TEST000001\",\"nombres\":\"Prueba\",\"apellidos\":\"Temporal\",\"funcion\":\"Taller\",\"activo\":true,\"departamento\":{\"codigoDepartamento\":\"D01\"},\"cargo\":{\"codigoCargo\":\"C01\"}}";
        try {
            assertEquals(200,call("POST","estados","{\"codigoEstado\":\"Z\",\"nombre\":\"Temporal\",\"activo\":true}").statusCode());
            assertEquals(200,call("POST","estados","{\"codigoEstado\":\"Z\",\"nombre\":\"Actualizado\",\"activo\":false}").statusCode());
            assertTrue(call("GET","estados/Z",null).body().contains("Actualizado"));
            assertEquals(409,call("POST","empleados",persona.replace("D01","ZZZ")).statusCode());
            assertEquals(200,call("POST","empleados",persona).statusCode());
            String user="{\"codigoUsuario\":\"UT0001\",\"usuario\":\"test.temporal\",\"hashClave\":\"TEST_NO_AUTENTICABLE\",\"intentosFallidos\":0,\"fechaCreacion\":\"2026-11-04T18:30:00\",\"persona\":{\"codigoPersona\":\"PT0001\"},\"estado\":{\"codigoEstado\":\"Z\"}}";
            var created=call("POST","usuarios",user);
            assertEquals(200,created.statusCode(),created.body());
            assertFalse(created.body().contains("TEST_NO_AUTENTICABLE"));
            assertEquals(200,call("POST","usuarios",user.replace("\"hashClave\":\"TEST_NO_AUTENTICABLE\",","").replace("test.temporal","test.actualizado")).statusCode());
            assertTrue(call("GET","usuarios/UT0001",null).body().contains("test.actualizado"));
            assertEquals(409,call("DELETE","empleados/PT0001",null).statusCode());
        } finally {
            call("DELETE","usuarios/UT0001",null);
            call("DELETE","empleados/PT0001",null);
            call("DELETE","estados/Z",null);
        }
        assertEquals(404,call("GET","estados/Z",null).statusCode());
        assertEquals(404,call("GET","empleados/PT0001",null).statusCode());
    }
}
