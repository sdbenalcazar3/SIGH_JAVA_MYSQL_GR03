<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Restablecer clave | SIGH GR03</title>
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
            <h1>Nueva clave</h1>

            <c:if test="${not empty error}">
                <div class="alert alert-danger"><i class="bi bi-exclamation-triangle mr-1"></i><c:out value="${error}"/></div>
            </c:if>

            <c:choose>
                <c:when test="${tokenValido}">
                    <p class="sub">La clave debe tener mínimo 8 caracteres e incluir letras y números.</p>
                    <form action="${ctx}/restablecer-clave" method="post" autocomplete="off">
                        <input type="hidden" name="token" value="<c:out value='${token}'/>">
                        <div class="form-group">
                            <label for="clave">Nueva clave</label>
                            <input type="password" class="form-control" id="clave" name="clave" minlength="8" required autofocus>
                        </div>
                        <div class="form-group">
                            <label for="confirmacion">Confirmar clave</label>
                            <input type="password" class="form-control" id="confirmacion" name="confirmacion" minlength="8" required>
                        </div>
                        <button type="submit" class="btn btn-sigh btn-block">Guardar clave</button>
                    </form>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-warning">
                        <i class="bi bi-clock-history mr-1"></i>
                        El enlace no es válido, ya fue utilizado o expiró.
                    </div>
                    <a class="btn btn-sigh btn-block" href="${ctx}/recuperar-clave">Solicitar un nuevo enlace</a>
                </c:otherwise>
            </c:choose>

            <div class="text-center mt-4">
                <a class="enlace-sigh" href="${ctx}/login"><i class="bi bi-arrow-left mr-1"></i>Volver al inicio de sesión</a>
            </div>
        </div>
    </section>
</main>
</body>
</html>
