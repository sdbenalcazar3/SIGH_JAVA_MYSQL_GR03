<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Recuperar clave | SIGH GR03</title>
    <link rel="stylesheet" href="${ctx}/lib/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${ctx}/lib/bootstrap-icons/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${ctx}/css/sigh.css">
</head>
<body>
<main class="acceso">
    <section class="acceso-card" style="max-width: 560px">
        <div class="acceso-form">
            <div class="marca mb-4">
                <img src="${ctx}/img/monster-logo.png" alt="Monster" style="background: var(--navy)">
                <div>
                    <strong style="color: var(--navy)">MONSTER</strong>
                    <span style="color: var(--muted)">SIGH &middot; ORM Java GR03</span>
                </div>
            </div>
            <h1>Recuperar clave</h1>
            <p class="sub">Escriba el correo registrado en sus datos personales (PEPER_CORREO). Se enviará un enlace válido por 30 minutos.</p>

            <c:if test="${not empty mensaje}">
                <div class="alert alert-success"><i class="bi bi-envelope-check mr-1"></i><c:out value="${mensaje}"/></div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-danger"><i class="bi bi-exclamation-triangle mr-1"></i><c:out value="${error}"/></div>
            </c:if>

            <form action="${ctx}/recuperar-clave" method="post" autocomplete="off">
                <div class="form-group">
                    <label for="correo">Correo electrónico</label>
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <span class="input-group-text"><i class="bi bi-envelope"></i></span>
                        </div>
                        <input type="email" class="form-control" id="correo" name="correo" maxlength="150"
                               placeholder="usuario@correo.com" required autofocus>
                    </div>
                </div>
                <button type="submit" class="btn btn-sigh btn-block">Enviar enlace</button>
            </form>

            <div class="text-center mt-4">
                <a class="enlace-sigh" href="${ctx}/login"><i class="bi bi-arrow-left mr-1"></i>Volver al inicio de sesión</a>
            </div>
        </div>
    </section>
</main>
</body>
</html>
