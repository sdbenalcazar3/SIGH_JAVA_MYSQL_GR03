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

    // ------------------------------------------------------------------ TabPage de empleados (EMP)

    static final byte[] PNG_1X1=java.util.Base64.getDecoder().decode(
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==");

    /** POST multipart/form-data con el campo "archivo" a /api/empleados/{codigo}/foto. */
    HttpResponse<String> subirFoto(String codigo,String nombre,String mime,byte[] contenido) throws Exception {
        String limite="----SighLimite"+System.nanoTime();
        var cuerpo=new java.io.ByteArrayOutputStream();
        cuerpo.write(("--"+limite+"\r\nContent-Disposition: form-data; name=\"archivo\"; filename=\""+nombre+"\"\r\nContent-Type: "+mime+"\r\n\r\n").getBytes(StandardCharsets.UTF_8));
        cuerpo.write(contenido);
        cuerpo.write(("\r\n--"+limite+"--\r\n").getBytes(StandardCharsets.UTF_8));
        var request=HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+port+"/api/empleados/"+codigo+"/foto"))
                .header("Content-Type","multipart/form-data; boundary="+limite)
                .POST(HttpRequest.BodyPublishers.ofByteArray(cuerpo.toByteArray())).build();
        return client.send(request,HttpResponse.BodyHandlers.ofString());
    }

    HttpResponse<String> publico(String url) throws Exception {
        return client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+port+url)).GET().build(),HttpResponse.BodyHandlers.ofString());
    }

    static String ficha(String codigo,String correo,String documento,String academicos,String familiares) {
        return "{\"codigoPersona\":\""+codigo+"\",\"apellidos\":\"Prueba  Temporal\",\"nombres\":\"Ficha Taller\",\"funcion\":\"Taller TabPage\","
                +"\"activo\":true,\"tipoDocumento\":\"CEDULA\",\"numeroDocumento\":\""+documento+"\",\"fechaNacimiento\":\"1990-02-03\","
                +"\"telefono\":\"0991234567\",\"correo\":\""+correo+"\",\"codigoGenero\":\"M\",\"codigoEstadoCivil\":\"S\",\"tipoSangre\":\"O+\","
                +"\"peso\":70.5,\"estatura\":1.75,\"codigoDepartamento\":\"D01\",\"codigoCargo\":\"C01\","
                +"\"academicos\":"+academicos+",\"familiares\":"+familiares+"}";
    }

    @Test void empleadosExigenSesion() throws Exception {
        var vista=pagina("/empleados");
        assertEquals(302,vista.statusCode());
        assertTrue(vista.headers().firstValue("Location").orElse("").contains("/login?expirada"));
        for (String ruta : new String[]{"empleados","empleados/resumen","empleados/siguiente-codigo","empleados/P00001","empleados/P00001/detalle"})
            assertEquals(401,call("GET",ruta,null).statusCode(),ruta);
        assertEquals(401,call("POST","empleados/ficha",ficha("PT9002","a@b.ec","1700000099","[]","[]")).statusCode());
        assertEquals(401,call("PUT","empleados/P00001/ficha",ficha("P00001","a@b.ec","1700000099","[]","[]")).statusCode());
        assertEquals(401,call("DELETE","empleados/P00001",null).statusCode());
        assertEquals(401,call("DELETE","empleados/P00001/foto",null).statusCode());
        assertEquals(401,call("POST","empleados/P00001/academicos","{}").statusCode());
        assertEquals(401,call("DELETE","empleados/P00001/familiares/1",null).statusCode());
        assertEquals(401,subirFoto("P00001","a.png","image/png",PNG_1X1).statusCode());
    }

    @Test void empleadosRbacPerfilEnfermeriaNoTieneLaOpcionEMP() throws Exception {
        assertEquals(302,login("luis.mora",clavePrueba).statusCode(),"No se pudo iniciar sesion con luis.mora");
        var vista=pagina("/empleados");
        assertEquals(302,vista.statusCode());
        assertTrue(vista.headers().firstValue("Location").orElse("").contains("/inicio?sinPermiso"));
        for (String ruta : new String[]{"empleados","empleados/resumen","empleados/siguiente-codigo","empleados/P00001","empleados/P00001/detalle"})
            assertEquals(403,call("GET",ruta,null).statusCode(),ruta);
        assertEquals(403,call("POST","empleados/ficha",ficha("PT9002","a@b.ec","1700000099","[]","[]")).statusCode());
        assertEquals(403,call("PUT","empleados/P00001/ficha",ficha("P00001","a@b.ec","1700000099","[]","[]")).statusCode());
        assertEquals(403,call("DELETE","empleados/P00001",null).statusCode());
        assertEquals(403,call("DELETE","empleados/P00001/foto",null).statusCode());
        assertEquals(403,subirFoto("P00001","a.png","image/png",PNG_1X1).statusCode());
        var inicio=pagina("/inicio");
        assertEquals(200,inicio.statusCode());
        assertFalse(inicio.body().contains("/empleados\""),"luis.mora no debe ver el acceso a Empleados");
    }

    @Test void empleadosPerfilAdministradorAccedeAlTabPage() throws Exception {
        assertEquals(302,login(usuarioPrueba,clavePrueba).statusCode());
        var vista=pagina("/empleados");
        assertEquals(200,vista.statusCode());
        assertTrue(vista.body().contains("id=\"formEmpleado\""));
        assertTrue(vista.body().contains("id=\"tabEmpleados\""));
        var inicio=pagina("/inicio");
        assertEquals(200,inicio.statusCode());
        assertTrue(inicio.body().contains("/empleados\""),"demo.sigh debe ver el acceso a Empleados");
        var siguiente=call("GET","empleados/siguiente-codigo",null);
        assertEquals(200,siguiente.statusCode());
        assertTrue(siguiente.body().matches("(?s).*\"codigo\"\\s*:\\s*\"P\\d{5}\".*"),siguiente.body());
        var resumen=call("GET","empleados/resumen",null);
        assertEquals(200,resumen.statusCode());
        assertTrue(resumen.body().contains("\"P00001\""));
        assertTrue(call("GET","empleados/resumen?q=ejemplo",null).body().contains("\"P00001\""));
        assertFalse(call("GET","empleados/resumen?departamento=ZZZ",null).body().contains("\"P00001\""));
        assertEquals("[]",call("GET","empleados/resumen?q=zzzzzz&activo=false",null).body().trim());
        assertEquals(200,call("GET","empleados/resumen?q=%25_!",null).statusCode(),"Los comodines LIKE se escapan");
        var detalle=call("GET","empleados/P00001/detalle",null);
        assertEquals(200,detalle.statusCode());
        assertTrue(detalle.body().contains("\"tieneUsuario\":true"),detalle.body());
        assertEquals(404,call("GET","empleados/P99998/detalle",null).statusCode());
        assertEquals(400,call("GET","empleados/P-1/detalle",null).statusCode());
        assertEquals(200,pagina("/cargos").statusCode(),"El ListBox de cargos sigue accesible");
        assertEquals(200,call("GET","departamentos/resumen",null).statusCode(),"El CRUD de departamentos sigue accesible");
    }

    @Test void empleadosFichaMaestroDetalleYFotoSobreMariaDB() throws Exception {
        assertEquals(302,login(usuarioPrueba,clavePrueba).statusCode());
        assertEquals(404,call("GET","empleados/PT9002",null).statusCode(),"El codigo de prueba PT9002 debe estar libre");
        assertEquals(404,call("GET","empleados/PT9003",null).statusCode(),"El codigo de prueba PT9003 debe estar libre");
        String academicos="[{\"nivel\":\"Tercer Nivel\",\"institucion\":\"UCE\",\"titulo\":\"Ingeniero\"}]";
        String familiares="[{\"parentesco\":\"Hijo/a\",\"apellidos\":\"Prueba\",\"nombres\":\"Ana\",\"fechaNacimiento\":\"2015-05-05\"}]";
        try {
            // Validaciones y atomicidad: un error no deja nada guardado.
            assertEquals(400,call("POST","empleados/ficha",ficha("PT9002","pt2@sigh.test","1700000092","[{\"nivel\":\"\",\"institucion\":\"UCE\",\"titulo\":\"X\"}]","[]")).statusCode());
            assertEquals(404,call("GET","empleados/PT9002",null).statusCode(),"Sin cambios parciales tras el error");
            assertEquals(400,call("POST","empleados/ficha",ficha("PT9002","pt2@sigh.test","1712","[]","[]")).statusCode(),"Cedula de 4 digitos");
            assertEquals(400,call("POST","empleados/ficha",ficha("PT9002","pt2@sigh.test","1700000092","[]","[]").replace("O+","Z+")).statusCode());
            assertEquals(404,call("POST","empleados/ficha",ficha("PT9002","pt2@sigh.test","1700000092","[]","[]").replace("\"D01\"","\"ZZZ\"")).statusCode());

            var creado=call("POST","empleados/ficha",ficha("pt9002","pt2@sigh.test","1700000092",academicos,familiares));
            assertEquals(200,creado.statusCode(),creado.body());
            assertTrue(creado.body().contains("\"PT9002\""));
            assertTrue(creado.body().contains("\"codigoExterno\":\"EXT0009002\""),creado.body());
            assertTrue(creado.body().contains("\"apellidos\":\"Prueba Temporal\""),"Espacios colapsados");
            assertTrue(creado.body().contains("\"institucion\":\"UCE\""));
            assertTrue(creado.body().contains("\"tieneUsuario\":false"));
            assertEquals(409,call("POST","empleados/ficha",ficha("PT9002","otro@sigh.test","1700000093","[]","[]")).statusCode(),"Codigo duplicado");
            assertEquals(409,call("POST","empleados/ficha",ficha("PT9003","pt2@sigh.test","1700000093","[]","[]")).statusCode(),"Correo duplicado");
            assertEquals(409,call("POST","empleados/ficha",ficha("PT9003","otro@sigh.test","1700000092","[]","[]")).statusCode(),"Documento duplicado");
            assertEquals(404,call("GET","empleados/PT9003",null).statusCode());

            var resumen=call("GET","empleados/resumen?q=pt9002&activo=true&departamento=D01&cargo=C01",null);
            assertTrue(resumen.body().contains("\"PT9002\""));
            assertTrue(resumen.body().contains("\"totalAcademicos\":1"));
            assertTrue(resumen.body().contains("\"totalFamiliares\":1"));

            // Actualizacion: el codigo de la ruta prevalece y las listas quedan exactamente como se envian.
            var detalle=call("GET","empleados/PT9002/detalle",null);
            String idAcademico=detalle.body().replaceAll("(?s).*\"academicos\":\\[\\{\"codigoAcademico\":(\\d+).*","$1");
            String nuevosAcademicos="[{\"codigoAcademico\":"+idAcademico+",\"nivel\":\"Maestría\",\"institucion\":\"EPN\",\"titulo\":\"Magister\"},"
                    +"{\"nivel\":\"Doctorado\",\"institucion\":\"UCE\",\"titulo\":\"PhD\"}]";
            var actualizado=call("PUT","empleados/PT9002/ficha",ficha("P00001","pt2@sigh.test","1700000092",nuevosAcademicos,"[]").replace("Taller TabPage","Funcion editada"));
            assertEquals(200,actualizado.statusCode(),actualizado.body());
            assertTrue(actualizado.body().contains("\"codigoPersona\":\"PT9002\""));
            assertTrue(actualizado.body().contains("\"funcion\":\"Funcion editada\""));
            assertTrue(actualizado.body().contains("\"codigoAcademico\":"+idAcademico+",\"nivel\":\"Maestría\""),"Se conserva el id recibido");
            assertTrue(actualizado.body().contains("\"titulo\":\"PhD\""));
            assertTrue(actualizado.body().contains("\"familiares\":[]"),"Los familiares omitidos se eliminan");
            assertTrue(call("GET","empleados/P00001/detalle",null).body().contains("\"codigoPersona\":\"P00001\""));
            assertFalse(call("GET","empleados/P00001/detalle",null).body().contains("Funcion editada"),"P00001 no debe cambiar");
            assertEquals(404,call("PUT","empleados/PT9003/ficha",ficha("PT9003","x@sigh.test","1700000094","[]","[]")).statusCode());
            assertEquals(400,call("PUT","empleados/PT9002/ficha",ficha("PT9002","pt2@sigh.test","1700000092","[]","[{\"parentesco\":\"Madre\",\"apellidos\":\"A\",\"nombres\":\"B\",\"fechaNacimiento\":\"2999-01-01\"}]")).statusCode());
            assertTrue(call("GET","empleados/PT9002/detalle",null).body().contains("\"titulo\":\"PhD\""),"El rollback conserva los datos anteriores");

            // CRUD granular de hijos.
            var familiar=call("POST","empleados/PT9002/familiares","{\"parentesco\":\"Madre\",\"apellidos\":\"Prueba\",\"nombres\":\"Rosa\"}");
            assertEquals(200,familiar.statusCode(),familiar.body());
            String idFamiliar=familiar.body().replaceAll("(?s).*\"codigoFamiliar\":(\\d+).*","$1");
            assertEquals(400,call("POST","empleados/PT9002/familiares","{\"parentesco\":\"Madre\",\"apellidos\":\"\",\"nombres\":\"Rosa\"}").statusCode());
            assertEquals(404,call("DELETE","empleados/P00001/familiares/"+idFamiliar,null).statusCode(),"El registro pertenece a otro empleado");
            assertEquals(204,call("DELETE","empleados/PT9002/familiares/"+idFamiliar,null).statusCode());
            assertEquals(404,call("DELETE","empleados/PT9002/familiares/"+idFamiliar,null).statusCode());
            var academico=call("POST","empleados/PT9002/academicos","{\"nivel\":\"Bachiller\",\"institucion\":\"Colegio\",\"titulo\":\"Bachiller\"}");
            assertEquals(200,academico.statusCode(),academico.body());
            String idNuevoAcademico=academico.body().replaceAll("(?s).*\"codigoAcademico\":(\\d+).*","$1");
            assertEquals(204,call("DELETE","empleados/PT9002/academicos/"+idNuevoAcademico,null).statusCode());
            assertEquals(404,call("POST","empleados/PT9003/academicos","{\"nivel\":\"B\",\"institucion\":\"C\",\"titulo\":\"T\"}").statusCode());

            // Foto: validaciones sin asignar nada, carga correcta, publicacion y borrado.
            assertEquals(400,subirFoto("PT9002","virus.png","image/png","MZ no soy una imagen".getBytes(StandardCharsets.UTF_8)).statusCode(),"Magic bytes falsos");
            assertEquals(400,subirFoto("PT9002","nota.txt","text/plain",PNG_1X1).statusCode());
            assertEquals(400,subirFoto("PT9002","../../evil.png","image/png",PNG_1X1).statusCode(),"Path traversal");
            assertEquals(404,subirFoto("PT9003","a.png","image/png",PNG_1X1).statusCode());
            assertEquals(413,subirFoto("PT9002","grande.png","image/png",new byte[3*1024*1024]).statusCode(),"Mayor a 2 MB");
            assertTrue(call("GET","empleados/PT9002/detalle",null).body().contains("\"foto\":null"),"Ningun intento fallido asigna foto");
            var foto=subirFoto("PT9002","mi foto.png","image/png",PNG_1X1);
            assertEquals(200,foto.statusCode(),foto.body());
            String url=foto.body().replaceAll("(?s).*\"foto\"\\s*:\\s*\"([^\"]+)\".*","$1");
            assertTrue(url.matches("/uploads/empleados/PT9002_\\d+\\.png"),url);
            var imagen=client.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+port+url)).GET().build(),HttpResponse.BodyHandlers.ofByteArray());
            assertEquals(200,imagen.statusCode(),"La foto se publica bajo /uploads/empleados/**");
            assertArrayEquals(PNG_1X1,imagen.body());
            assertTrue(call("GET","empleados/PT9002/detalle",null).body().contains(url));
            assertEquals(200,call("PUT","empleados/PT9002/ficha",ficha("PT9002","pt2@sigh.test","1700000092","[]","[]")).statusCode());
            assertTrue(call("GET","empleados/PT9002/detalle",null).body().contains(url),"Actualizar la ficha no borra la foto");
            assertEquals(204,call("DELETE","empleados/PT9002/foto",null).statusCode());
            assertEquals(404,publico(url).statusCode(),"La foto eliminada deja de publicarse");
            assertEquals(404,call("DELETE","empleados/PT9003/foto",null).statusCode());

            // Borrado: P00001 tiene la cuenta demo.sigh (409); PT9002 se elimina con sus hijos y su foto fisica.
            assertEquals(409,call("DELETE","empleados/P00001",null).statusCode());
            assertEquals(200,call("GET","empleados/P00001/detalle",null).statusCode());
            var otraFoto=subirFoto("PT9002","otra.png","image/png",PNG_1X1);
            assertEquals(200,otraFoto.statusCode(),otraFoto.body());
            String urlOtra=otraFoto.body().replaceAll("(?s).*\"foto\"\\s*:\\s*\"([^\"]+)\".*","$1");
            assertEquals(200,publico(urlOtra).statusCode());
            assertEquals(204,call("DELETE","empleados/PT9002",null).statusCode());
            assertEquals(404,publico(urlOtra).statusCode(),"Al borrar el empleado se borra su foto");
        } finally {
            call("DELETE","empleados/PT9002",null);
            call("DELETE","empleados/PT9003",null);
        }
        assertEquals(404,call("GET","empleados/PT9002",null).statusCode());
        assertEquals(404,call("GET","empleados/PT9002/detalle",null).statusCode());
        assertEquals(404,call("DELETE","empleados/PT9002",null).statusCode());
    }

    // ---------------------------------------------------------------- Modulo de reportes (REP)

    static final String MIME_XLSX="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    /** GET binario de /api/reportes/{ruta} (PDF o XLSX). */
    HttpResponse<byte[]> reporte(String ruta) throws Exception {
        var request=HttpRequest.newBuilder(URI.create("http://127.0.0.1:"+port+"/api/reportes/"+ruta)).GET().build();
        return client.send(request,HttpResponse.BodyHandlers.ofByteArray());
    }

    static String firma(byte[] contenido,int largo) {
        return new String(contenido,0,Math.min(largo,contenido.length),StandardCharsets.ISO_8859_1);
    }

    @Test void reportesExigenSesion() throws Exception {
        var vista=pagina("/reportes");
        assertEquals(302,vista.statusCode());
        assertTrue(vista.headers().firstValue("Location").orElse("").contains("/login?expirada"));
        for (String ruta : new String[]{"reportes/resumen","reportes/departamentos","reportes/cargos","reportes/personal","reportes/personal?formato=xlsx"})
            assertEquals(401,call("GET",ruta,null).statusCode(),ruta);
    }

    @Test void reportesRbacPerfilEnfermeriaNoTieneLaOpcionREP() throws Exception {
        assertEquals(302,login("luis.mora",clavePrueba).statusCode(),"No se pudo iniciar sesion con luis.mora");
        var vista=pagina("/reportes");
        assertEquals(302,vista.statusCode());
        assertTrue(vista.headers().firstValue("Location").orElse("").contains("/inicio?sinPermiso"));
        for (String ruta : new String[]{"reportes/resumen","reportes/departamentos","reportes/cargos","reportes/personal","reportes/personal?formato=xlsx"})
            assertEquals(403,call("GET",ruta,null).statusCode(),ruta);
        var inicio=pagina("/inicio");
        assertEquals(200,inicio.statusCode());
        assertFalse(inicio.body().contains("/reportes\""),"luis.mora no debe ver el acceso a Reportes");
    }

    @Test void reportesPerfilAdministradorGeneraPdfYXlsxReales() throws Exception {
        assertEquals(302,login(usuarioPrueba,clavePrueba).statusCode());
        var vista=pagina("/reportes");
        assertEquals(200,vista.statusCode());
        var inicio=pagina("/inicio");
        assertEquals(200,inicio.statusCode());
        assertTrue(inicio.body().contains("/reportes\""),"demo.sigh debe ver el acceso a Reportes");

        var resumen=call("GET","reportes/resumen",null);
        assertEquals(200,resumen.statusCode());
        assertTrue(resumen.body().contains("totalDepartamentos")&&resumen.body().contains("personalActivo"));

        for (String reporte : new String[]{"departamentos","cargos","personal"}) {
            var pdf=reporte(reporte);
            assertEquals(200,pdf.statusCode(),reporte);
            assertEquals("application/pdf",pdf.headers().firstValue("Content-Type").orElse(""));
            assertTrue(pdf.headers().firstValue("Content-Disposition").orElse("").startsWith("inline; filename=\"reporte_"+reporte+"_"));
            assertEquals("nosniff",pdf.headers().firstValue("X-Content-Type-Options").orElse(""));
            assertEquals("no-store, no-cache, must-revalidate",pdf.headers().firstValue("Cache-Control").orElse(""));
            assertEquals("%PDF-",firma(pdf.body(),5));

            var descarga=reporte(reporte+"?descarga=true");
            assertEquals(200,descarga.statusCode());
            assertTrue(descarga.headers().firstValue("Content-Disposition").orElse("").startsWith("attachment;"));
            assertEquals("%PDF-",firma(descarga.body(),5));

            var xlsx=reporte(reporte+"?formato=xlsx");
            assertEquals(200,xlsx.statusCode(),reporte);
            assertEquals(MIME_XLSX,xlsx.headers().firstValue("Content-Type").orElse(""));
            assertTrue(xlsx.headers().firstValue("Content-Disposition").orElse("").startsWith("attachment; filename=\"reporte_"+reporte+"_"));
            assertEquals("PK",firma(xlsx.body(),2));
        }
    }

    @Test void reportesFiltrosCasosBordeYFormatoInvalido() throws Exception {
        assertEquals(302,login(usuarioPrueba,clavePrueba).statusCode());
        // Filtros combinados y comodines LIKE tratados literalmente (0 registros, documento valido).
        for (String consulta : new String[]{
                "departamentos?q=zzzzzz&activo=true","departamentos?q=%25_!&formato=xlsx",
                "cargos?q=zzzzzz&activo=false","cargos?departamento=SIN_PERSONAL&formato=xlsx","cargos?q=%25_!",
                "personal?q=zzzzzz&departamento=D01&cargo=C01&activo=true","personal?q=%25_!&formato=xlsx"}) {
            var respuesta=reporte(consulta);
            assertEquals(200,respuesta.statusCode(),consulta);
            assertEquals(consulta.contains("xlsx")?"PK":"%PDF-",firma(respuesta.body(),consulta.contains("xlsx")?2:5),consulta);
        }
        for (String ruta : new String[]{"departamentos","cargos","personal"}) {
            for (String invalido : new String[]{"docx","exe"}) {
                var rechazo=call("GET","reportes/"+ruta+"?formato="+invalido,null);
                assertEquals(400,rechazo.statusCode(),ruta+" "+invalido);
                assertTrue(rechazo.body().contains("\"mensaje\""));
                assertFalse(rechazo.body().contains("Exception"),"Sin stacktrace");
            }
        }
    }

    @Test void reportesNoRompenDepCarEmp() throws Exception {
        assertEquals(302,login(usuarioPrueba,clavePrueba).statusCode());
        assertEquals(200,call("GET","departamentos/resumen",null).statusCode());
        assertEquals(200,call("GET","cargos/resumen",null).statusCode());
        assertEquals(200,call("GET","empleados/resumen",null).statusCode());
        assertEquals(200,pagina("/cargos").statusCode());
        assertEquals(200,pagina("/empleados").statusCode());
        assertEquals(200,pagina("/reportes").statusCode());
    }
}
