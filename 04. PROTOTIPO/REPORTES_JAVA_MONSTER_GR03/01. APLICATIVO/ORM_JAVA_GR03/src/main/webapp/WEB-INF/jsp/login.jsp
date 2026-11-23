<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Iniciar sesión | SIGH GR03</title>
    <link rel="stylesheet" href="${ctx}/lib/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${ctx}/lib/bootstrap-icons/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${ctx}/lib/sweetalert2/sweetalert2.min.css">
    <link rel="stylesheet" href="${ctx}/css/sigh.css">
</head>
<body>
<main class="acceso">
    <section class="acceso-card">
        <aside class="acceso-info">
            <div>
                <div class="marca">
                    <img src="${ctx}/img/monster-logo.png" alt="Monster">
                    <div>
                        <strong>MONSTER</strong>
                        <span>SIGH &middot; ORM Java GR03</span>
                    </div>
                </div>
                <h2>Sistema de Gestión Hospitalaria</h2>
                <p>Módulo de seguridad: el acceso se valida contra los usuarios, perfiles y opciones registrados en SIGH_GR3.</p>
                <ul class="lista-check">
                    <li><i class="bi bi-shield-lock"></i>Claves almacenadas con SHA-256</li>
                    <li><i class="bi bi-hourglass-split"></i>Bloqueo tras 3 intentos fallidos</li>
                    <li><i class="bi bi-diagram-3"></i>Menú según perfil y opciones</li>
                </ul>
            </div>
            <div class="acceso-pie">ESPE &middot; Grupo 03</div>
        </aside>

        <div class="acceso-form">
            <h1>Iniciar sesión</h1>
            <p class="sub">Ingrese sus credenciales para continuar.</p>

            <form action="${ctx}/login" method="post" autocomplete="off" id="formLogin">
                <div class="form-group">
                    <label for="usuario">Usuario</label>
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <span class="input-group-text"><i class="bi bi-person"></i></span>
                        </div>
                        <input type="text" class="form-control" id="usuario" name="usuario" maxlength="255"
                               value="<c:out value='${usuario}'/>" placeholder="Ejemplo: demo.sigh" required autofocus>
                    </div>
                </div>

                <div class="form-group">
                    <label for="clave">Clave</label>
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <span class="input-group-text"><i class="bi bi-key"></i></span>
                        </div>
                        <input type="password" class="form-control" id="clave" name="clave"
                               placeholder="Ingrese su clave" required>
                        <div class="input-group-append">
                            <button class="btn btn-ver" type="button" id="verClave" title="Mostrar clave">
                                <i class="bi bi-eye"></i>
                            </button>
                        </div>
                    </div>
                </div>

                <div class="d-flex justify-content-end mb-3">
                    <a class="enlace-sigh" href="${ctx}/recuperar-clave">¿Olvidó su clave?</a>
                </div>

                <button type="submit" class="btn btn-sigh btn-block">
                    Ingresar <i class="bi bi-box-arrow-in-right ml-1"></i>
                </button>
            </form>

            <div class="nota">
                <i class="bi bi-info-circle mr-1"></i>
                Puede ingresar con el nombre de usuario o con el código (por ejemplo U00001).
            </div>
        </div>
    </section>
</main>

<span id="mensajeError" hidden><c:out value="${error}"/></span>

<script src="${ctx}/lib/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${ctx}/lib/sweetalert2/sweetalert2.all.min.js"></script>
<script>
    const clave = document.getElementById("clave");
    document.getElementById("verClave").addEventListener("click", function () {
        const oculto = clave.type === "password";
        clave.type = oculto ? "text" : "password";
        this.innerHTML = oculto ? '<i class="bi bi-eye-slash"></i>' : '<i class="bi bi-eye"></i>';
    });

    const error = document.getElementById("mensajeError").textContent.trim();
    const parametros = new URLSearchParams(window.location.search);
    const alerta = {confirmButtonColor: "#127c78", confirmButtonText: "Aceptar"};

    if (error) {
        Swal.fire(Object.assign({icon: error.includes("bloqueado") ? "warning" : "error",
            title: "Acceso denegado", text: error}, alerta));
    } else if (parametros.has("salida")) {
        Swal.fire(Object.assign({icon: "success", title: "Sesión cerrada",
            text: "Ha salido correctamente del sistema."}, alerta));
    } else if (parametros.has("expirada")) {
        Swal.fire(Object.assign({icon: "info", title: "Sesión requerida",
            text: "Inicie sesión para acceder a esa página."}, alerta));
    } else if (parametros.has("restablecida")) {
        Swal.fire(Object.assign({icon: "success", title: "Clave actualizada",
            text: "Ya puede ingresar con su nueva clave."}, alerta));
    }
</script>
</body>
</html>
