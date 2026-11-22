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

    /** GET de una pagina MVC (fuera de /api), sin seguir redirecciones. */
    HttpResponse<String> pagina(String path) throws Exception {
        var request=HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+port+path)).GET().build();
        return client.send(request,HttpResponse.BodyHandlers.ofString());
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

    @Test void cargosExigenSesion() throws Exception {
        var vista=pagina("/cargos");
        assertEquals(302,vista.statusCode());
        assertTrue(vista.headers().firstValue("Location").orElse("").contains("/login?expirada"));
        for (String ruta : new String[]{"cargos","cargos/resumen","cargos/siguiente-codigo","cargos/C01","cargos/C01/personal"})
            assertEquals(401,call("GET",ruta,null).statusCode(),ruta);
        assertEquals(401,call("POST","cargos","{\"codigoCargo\":\"ZT1\",\"nombre\":\"Temporal\",\"activo\":true}").statusCode());
        assertEquals(401,call("PATCH","cargos/estado-lote","{\"codigos\":[\"C01\"],\"activo\":true}").statusCode());
        assertEquals(401,call("DELETE","cargos/C07",null).statusCode());
    }

    @Test void cargosRbacPerfilEnfermeriaNoTieneLaOpcionCAR() throws Exception {
        assertEquals(302,login("luis.mora",clavePrueba).statusCode(),"No se pudo iniciar sesion con luis.mora");
        var vista=pagina("/cargos");
        assertEquals(302,vista.statusCode());
        assertTrue(vista.headers().firstValue("Location").orElse("").contains("/inicio?sinPermiso"));
        assertEquals(403,call("GET","cargos",null).statusCode());
        assertEquals(403,call("GET","cargos/resumen",null).statusCode());
        assertEquals(403,call("GET","cargos/siguiente-codigo",null).statusCode());
        assertEquals(403,call("GET","cargos/C01/personal",null).statusCode());
        assertEquals(403,call("POST","cargos","{\"codigoCargo\":\"ZT1\",\"nombre\":\"Temporal\",\"activo\":true}").statusCode());
        assertEquals(403,call("PATCH","cargos/estado-lote","{\"codigos\":[\"C01\"],\"activo\":false}").statusCode());
        assertEquals(403,call("DELETE","cargos/C07",null).statusCode());
        var inicio=pagina("/inicio");
        assertEquals(200,inicio.statusCode());
        assertFalse(inicio.body().contains("/cargos\""),"luis.mora no debe ver el acceso a Cargos");
    }

    @Test void cargosPerfilAdministradorAccedeAlListBox() throws Exception {
        assertEquals(302,login(usuarioPrueba,clavePrueba).statusCode());
        var vista=pagina("/cargos");
        assertEquals(200,vista.statusCode());
        assertTrue(vista.body().contains("id=\"lstCargos\""));
        assertTrue(vista.body().contains("id=\"filtroDepartamento\""));
        var inicio=pagina("/inicio");
        assertEquals(200,inicio.statusCode());
        assertTrue(inicio.body().contains("/cargos\""),"demo.sigh debe ver el acceso a Cargos");
        assertEquals(200,call("GET","cargos/resumen",null).statusCode());
        assertEquals(200,pagina("/inicio").statusCode(),"El CRUD de departamentos sigue accesible");
        assertEquals(200,call("GET","departamentos/resumen",null).statusCode());
    }

    @Test void cargosFiltrosYCrudCompletoSobreMariaDB() throws Exception {
        assertEquals(302,login(usuarioPrueba,clavePrueba).statusCode());
        assertEquals(404,call("GET","cargos/ZT1",null).statusCode(),"El codigo de prueba ZT1 debe estar libre");
        assertEquals(404,call("GET","cargos/ZT2",null).statusCode(),"El codigo de prueba ZT2 debe estar libre");
        var siguiente=call("GET","cargos/siguiente-codigo",null);
        assertEquals(200,siguiente.statusCode());
        assertTrue(siguiente.body().matches("(?s).*\"codigo\"\\s*:\\s*\"C\\d{2}\".*"),siguiente.body());

        // Filtros sobre los datos del MER (C01 y C02 tienen personal en D01 y D02).
        var porDepartamento=call("GET","cargos/resumen?departamento=D02",null);
        assertEquals(200,porDepartamento.statusCode());
        assertTrue(porDepartamento.body().contains("\"C02\""));
        assertFalse(porDepartamento.body().contains("\"C01\""),"C01 solo tiene personal en D01");
        var porTexto=call("GET","cargos/resumen?q=ENFERMERA&activo=true",null);
        assertTrue(porTexto.body().contains("\"C02\""));
        assertFalse(porTexto.body().contains("\"C01\""));
        assertEquals("[]",call("GET","cargos/resumen?q=zzzzzz&activo=false&departamento=D01",null).body().trim());
        assertEquals(200,call("GET","cargos/resumen?q=%25_!",null).statusCode(),"Los comodines LIKE se escapan");

        try {
            String nuevo="{\"codigoCargo\":\"zt1\",\"nombre\":\"  Cargo   temporal \",\"activo\":true}";
            var creado=call("POST","cargos",nuevo);
            assertEquals(200,creado.statusCode(),creado.body());
            assertTrue(creado.body().contains("\"ZT1\""));
            assertTrue(creado.body().contains("Cargo temporal"));
            assertEquals(409,call("POST","cargos",nuevo).statusCode(),"Codigo duplicado");
            assertEquals(409,call("POST","cargos","{\"codigoCargo\":\"ZT2\",\"nombre\":\"CARGO TEMPORAL\",\"activo\":true}").statusCode(),"Nombre duplicado sin importar mayusculas");
            assertEquals(400,call("POST","cargos","{\"codigoCargo\":\"ZT2\",\"nombre\":\"Cargo 2\",\"activo\":true}").statusCode(),"Nombre con digitos");
            assertEquals(400,call("POST","cargos","{\"codigoCargo\":\"ZT\",\"nombre\":\"Cargo corto\",\"activo\":true}").statusCode(),"Codigo de 2 caracteres");
            assertEquals(400,call("POST","cargos","{\"codigoCargo\":\"ZT2\",\"nombre\":\"ab\",\"activo\":true}").statusCode(),"Nombre menor a 3 caracteres");

            assertEquals(200,call("PUT","cargos/ZT1","{\"codigoCargo\":\"C01\",\"nombre\":\"Cargo temporal editado\",\"activo\":true}").statusCode(),"El codigo de la ruta prevalece");
            assertTrue(call("GET","cargos/ZT1",null).body().contains("Cargo temporal editado"));
            assertTrue(call("GET","cargos/C01",null).body().contains("Analista de sistemas"),"C01 no debe cambiar");
            assertEquals(404,call("PUT","cargos/ZT2","{\"codigoCargo\":\"ZT2\",\"nombre\":\"Inexistente\",\"activo\":true}").statusCode());
            assertEquals(409,call("PUT","cargos/ZT1","{\"codigoCargo\":\"ZT1\",\"nombre\":\"enfermera\",\"activo\":true}").statusCode());

            var sinPersonal=call("GET","cargos/resumen?departamento=SIN_PERSONAL",null);
            assertTrue(sinPersonal.body().contains("\"ZT1\""));
            assertFalse(sinPersonal.body().contains("\"C01\""));
            assertFalse(call("GET","cargos/resumen?departamento=D01",null).body().contains("\"ZT1\""));
            assertEquals("[]",call("GET","cargos/ZT1/personal",null).body().trim());
            assertTrue(call("GET","cargos/C01/personal",null).body().contains("Ejemplo"));
            assertEquals(404,call("GET","cargos/ZT2/personal",null).statusCode());

            // Atomicidad del cambio en lote: un codigo inexistente no deja cambios parciales.
            assertEquals(404,call("PATCH","cargos/estado-lote","{\"codigos\":[\"ZT1\",\"NOE\"],\"activo\":false}").statusCode());
            assertTrue(call("GET","cargos/ZT1",null).body().contains("\"activo\":true"),"ZT1 no debe cambiar tras el rollback");
            assertEquals(400,call("PATCH","cargos/estado-lote","{\"codigos\":[],\"activo\":false}").statusCode());
            assertEquals(400,call("PATCH","cargos/estado-lote","{\"codigos\":[\"ZT1\"]}").statusCode());
            var lote=call("PATCH","cargos/estado-lote","{\"codigos\":[\"zt1\",\"ZT1\"],\"activo\":false}");
            assertEquals(200,lote.statusCode(),lote.body());
            assertTrue(call("GET","cargos/ZT1",null).body().contains("\"activo\":false"));
            assertTrue(call("GET","cargos/resumen?q=zt1&activo=false",null).body().contains("\"ZT1\""));
            assertFalse(call("GET","cargos/resumen?q=zt1&activo=true",null).body().contains("\"ZT1\""));

            assertEquals(409,call("DELETE","cargos/C01",null).statusCode(),"Cargo con personal asignado");
        } finally {
            call("DELETE","cargos/ZT1",null);
            call("DELETE","cargos/ZT2",null);
        }
        assertEquals(404,call("GET","cargos/ZT1",null).statusCode());
        assertEquals(404,call("DELETE","cargos/ZT1",null).statusCode());
    }
}
