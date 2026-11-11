<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Inicio | SIGH GR03</title>
    <link rel="stylesheet" href="${ctx}/lib/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${ctx}/lib/bootstrap-icons/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${ctx}/lib/sweetalert2/sweetalert2.min.css">
    <link rel="stylesheet" href="${ctx}/css/sigh.css">
</head>
<body>
<header class="barra">
    <div class="container d-flex align-items-center justify-content-between">
        <div class="marca">
            <img src="${ctx}/img/monster-logo.png" alt="Monster">
            <div>
                <strong>SIGH</strong>
                <span>ORM_JAVA_GR03</span>
            </div>
        </div>
        <div class="d-flex align-items-center">
            <span class="mr-3 d-none d-md-inline"><i class="bi bi-person-circle mr-1"></i><c:out value="${usuario.usuario}"/></span>
            <a class="btn btn-salir btn-sm" href="${ctx}/logout"><i class="bi bi-box-arrow-right mr-1"></i>Salir</a>
        </div>
    </div>
</header>

<main class="container contenido">
    <div class="titulo-seccion mb-1">Panel principal</div>
    <h1 class="h3 mb-4" style="color: var(--navy)">Bienvenido, <c:out value="${persona.nombres}"/></h1>

    <div class="row">
        <div class="col-lg-4">
            <section class="panel">
                <div class="perfil-usuario mb-3">
                    <span class="avatar"><c:out value="${persona.nombres.substring(0,1)}${persona.apellidos.substring(0,1)}"/></span>
                    <div>
                        <h2 class="mb-0"><c:out value="${persona.nombres} ${persona.apellidos}"/></h2>
                        <span class="ayuda"><c:out value="${persona.funcion}"/></span>
                    </div>
                </div>
                <div class="dato"><span>Usuario</span><strong><c:out value="${usuario.usuario}"/></strong></div>
                <div class="dato"><span>Código</span><strong><c:out value="${usuario.codigoUsuario}"/></strong></div>
                <div class="dato"><span>Departamento</span><strong><c:out value="${persona.departamento.nombre}"/></strong></div>
                <div class="dato"><span>Cargo</span><strong><c:out value="${persona.cargo.nombre}"/></strong></div>
                <div class="dato"><span>Correo</span><strong><c:out value="${empty persona.correo ? 'Sin registro' : persona.correo}"/></strong></div>
            </section>

            <section class="panel">
                <h2>Perfiles vigentes</h2>
                <p class="ayuda">Asignados en SEUXP_USUPER.</p>
                <c:forEach var="perfil" items="${perfiles}">
                    <span class="etiqueta"><c:out value="${perfil.codigoPerfil} · ${perfil.nombre}"/></span>
                </c:forEach>
                <c:if test="${empty perfiles}">
                    <span class="ayuda">El usuario no tiene perfiles vigentes.</span>
                </c:if>
            </section>

            <section class="panel">
                <h2>Opciones habilitadas</h2>
                <p class="ayuda">Obtenidas de los permisos del perfil (SEOXP_OPCPER).</p>
                <c:forEach var="opcion" items="${opciones}">
                    <div class="opcion">
                        <i class="bi ${opcion.codigoOpcion == 'DEP' ? 'bi-building' : 'bi-grid'}"></i>
                        <div>
                            <strong><c:out value="${opcion.nombre}"/></strong>
                            <small><c:out value="${opcion.modulo} / ${opcion.operacion}"/></small>
                        </div>
                    </div>
                </c:forEach>
                <c:if test="${empty opciones}">
                    <span class="ayuda">No hay opciones asignadas a sus perfiles.</span>
                </c:if>
            </section>
        </div>

        <div class="col-lg-8">
            <c:choose>
                <c:when test="${puedeGestionarDepartamentos}">
                    <section class="panel">
                        <div class="d-flex flex-wrap justify-content-between align-items-start mb-3">
                            <div>
                                <div class="titulo-seccion">Personal</div>
                                <h2>Mantenimiento de departamentos</h2>
                                <p class="ayuda mb-0">Tabla PEDEP_DEPART. Opción DEP habilitada para su perfil.</p>
                            </div>
                            <button class="btn btn-sigh btn-sm mt-2" type="button" id="btnNuevo">
                                <i class="bi bi-plus-lg mr-1"></i>Nuevo departamento
                            </button>
                        </div>

                        <div class="row mb-3">
                            <div class="col-4"><div class="indicador"><span>Total</span><strong id="totalDep">0</strong></div></div>
                            <div class="col-4"><div class="indicador"><span>Activos</span><strong id="totalAct">0</strong></div></div>
                            <div class="col-4"><div class="indicador"><span>Personal asignado</span><strong id="totalPer">0</strong></div></div>
                        </div>

                        <form id="formDepartamento" class="form-row align-items-end mb-3 formulario-dep" autocomplete="off">
                            <div class="col-12 mb-2"><strong id="tituloForm" class="small text-uppercase" style="color: var(--teal-2)">Registrar departamento</strong></div>
                            <div class="col-md-2 mb-2">
                                <label class="small font-weight-bold" for="codigo">Código</label>
                                <input class="form-control" id="codigo" maxlength="3" placeholder="D04" required>
                            </div>
                            <div class="col-md-5 mb-2">
                                <label class="small font-weight-bold" for="nombre">Nombre</label>
                                <input class="form-control" id="nombre" maxlength="100" placeholder="Pediatría" required>
                            </div>
                            <div class="col-md-2 mb-2">
                                <div class="custom-control custom-checkbox mb-2">
                                    <input type="checkbox" class="custom-control-input" id="activo" checked>
                                    <label class="custom-control-label small font-weight-bold" for="activo">Activo</label>
                                </div>
                            </div>
                            <div class="col-md-3 mb-2 d-flex">
                                <button class="btn btn-sigh flex-fill mr-2" type="submit" id="btnGuardar">
                                    <i class="bi bi-save mr-1"></i>Guardar
                                </button>
                                <button class="btn btn-light border" type="button" id="btnLimpiar" title="Limpiar">
                                    <i class="bi bi-x-lg"></i>
                                </button>
                            </div>
                        </form>

                        <div class="d-flex flex-wrap mb-2">
                            <div class="input-group mr-2 mb-2" style="max-width: 260px">
                                <div class="input-group-prepend"><span class="input-group-text"><i class="bi bi-search"></i></span></div>
                                <input class="form-control" id="buscar" placeholder="Buscar por código o nombre">
                            </div>
                            <select class="custom-select mb-2" id="filtroEstado" style="max-width: 170px">
                                <option value="">Todos los estados</option>
                                <option value="true">Solo activos</option>
                                <option value="false">Solo inactivos</option>
                            </select>
                        </div>

                        <div class="table-responsive">
                            <table class="table tabla-sigh mb-0">
                                <thead>
                                <tr>
                                    <th>Código</th>
                                    <th>Departamento</th>
                                    <th>Estado</th>
                                    <th>Personal</th>
                                    <th class="text-right">Acciones</th>
                                </tr>
                                </thead>
                                <tbody id="cuerpoTabla">
                                <tr><td colspan="5" class="text-center text-muted">Cargando departamentos...</td></tr>
                                </tbody>
                            </table>
                        </div>
                    </section>
                </c:when>
                <c:otherwise>
                    <section class="panel">
                        <div class="sin-permiso">
                            <i class="bi bi-lock"></i>
                            <div>
                                <h2>Sesión validada</h2>
                                <p class="ayuda mb-0">Su perfil no tiene la opción DEP, por lo que el mantenimiento de
                                    departamentos no está disponible. Las opciones mostradas a la izquierda dependen
                                    de los permisos asignados a su perfil.</p>
                            </div>
                        </div>
                    </section>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</main>

<script src="${ctx}/lib/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${ctx}/lib/sweetalert2/sweetalert2.all.min.js"></script>
<script>
    if (new URLSearchParams(window.location.search).has("bienvenida")) {
        Swal.fire({toast: true, position: "top-end", icon: "success", title: "Inicio de sesión correcto",
            showConfirmButton: false, timer: 2200});
        history.replaceState(null, "", window.location.pathname);
    }
</script>
<c:if test="${puedeGestionarDepartamentos}">
<script>
    const API = "${ctx}/api/departamentos";
    const form = document.getElementById("formDepartamento");
    const codigo = document.getElementById("codigo");
    const nombre = document.getElementById("nombre");
    const activo = document.getElementById("activo");
    const cuerpo = document.getElementById("cuerpoTabla");
    const buscar = document.getElementById("buscar");
    const filtroEstado = document.getElementById("filtroEstado");
    let departamentos = [];
    let editando = false;

    codigo.addEventListener("input", () => codigo.value = codigo.value.toUpperCase().replace(/[^A-Z0-9]/g, ""));
    nombre.addEventListener("input", () => nombre.value = nombre.value.replace(/[0-9]/g, ""));
    buscar.addEventListener("input", pintar);
    filtroEstado.addEventListener("change", pintar);
    document.getElementById("btnLimpiar").addEventListener("click", limpiar);
    document.getElementById("btnNuevo").addEventListener("click", () => { limpiar(); codigo.focus(); });

    form.addEventListener("submit", async (evento) => {
        evento.preventDefault();
        const departamento = {codigoDepartamento: codigo.value.trim(), nombre: nombre.value.trim(), activo: activo.checked};
        const error = validar(departamento);
        if (error) {
            return mostrarError(error);
        }
        const respuesta = await fetch(editando ? API + "/" + departamento.codigoDepartamento : API, {
            method: editando ? "PUT" : "POST",
            headers: {"Content-Type": "application/json"},
            body: JSON.stringify(departamento)
        });
        if (!respuesta.ok) {
            return mostrarError(await leerMensaje(respuesta, "No se pudo guardar el departamento."));
        }
        const titulo = editando ? "Departamento actualizado" : "Departamento registrado";
        limpiar();
        await cargar();
        Swal.fire({icon: "success", title: titulo, confirmButtonColor: "#127c78"});
    });

    function validar(d) {
        if (!/^[A-Z0-9]{3}$/.test(d.codigoDepartamento)) return "El código debe tener 3 caracteres, por ejemplo D04.";
        if (d.nombre.length < 3) return "El nombre debe tener al menos 3 caracteres.";
        if (/[0-9]/.test(d.nombre)) return "El nombre no debe contener números.";
        if (!editando && departamentos.some(x => x.codigoDepartamento === d.codigoDepartamento))
            return "Ya existe un departamento con ese código.";
        if (departamentos.some(x => x.nombre.toLowerCase() === d.nombre.toLowerCase()
                && x.codigoDepartamento !== d.codigoDepartamento))
            return "Ya existe un departamento con ese nombre.";
        return null;
    }

    async function cargar() {
        const respuesta = await fetch(API + "/resumen");
        if (!respuesta.ok) {
            return mostrarError(await leerMensaje(respuesta, "No se pudo cargar la lista de departamentos."));
        }
        departamentos = await respuesta.json();
        document.getElementById("totalDep").textContent = departamentos.length;
        document.getElementById("totalAct").textContent = departamentos.filter(d => d.activo).length;
        document.getElementById("totalPer").textContent = departamentos.reduce((s, d) => s + d.personal, 0);
        pintar();
    }

    function pintar() {
        const texto = buscar.value.trim().toLowerCase();
        const estado = filtroEstado.value;
        const lista = departamentos
            .filter(d => !texto || d.codigoDepartamento.toLowerCase().includes(texto) || d.nombre.toLowerCase().includes(texto))
            .filter(d => estado === "" || String(d.activo) === estado);
        if (!lista.length) {
            cuerpo.innerHTML = '<tr><td colspan="5" class="text-center text-muted">No hay departamentos para mostrar.</td></tr>';
            return;
        }
        cuerpo.innerHTML = "";
        lista.forEach(d => {
            const fila = document.createElement("tr");
            fila.innerHTML =
                '<td><span class="codigo"></span></td><td class="nombre"></td>' +
                '<td>' + (d.activo ? '<span class="estado-activo"><i class="bi bi-check-circle mr-1"></i>Activo</span>'
                                   : '<span class="estado-inactivo"><i class="bi bi-x-circle mr-1"></i>Inactivo</span>') + '</td>' +
                '<td>' + d.personal + (d.personal === 1 ? ' persona' : ' personas') + '</td>' +
                '<td class="text-right">' +
                '<button class="btn btn-icono btn-ver-personal mr-1" title="Ver personal"><i class="bi bi-people"></i></button>' +
                '<button class="btn btn-icono btn-editar mr-1" title="Editar"><i class="bi bi-pencil-square"></i></button>' +
                '<button class="btn btn-icono btn-eliminar" title="Eliminar"><i class="bi bi-trash3"></i></button></td>';
            fila.querySelector(".codigo").textContent = d.codigoDepartamento;
            fila.querySelector(".nombre").textContent = d.nombre;
            fila.querySelector(".btn-ver-personal").addEventListener("click", () => verPersonal(d));
            fila.querySelector(".btn-editar").addEventListener("click", () => editar(d));
            fila.querySelector(".btn-eliminar").addEventListener("click", () => eliminar(d));
            cuerpo.appendChild(fila);
        });
    }

    async function verPersonal(d) {
        const respuesta = await fetch(API + "/" + encodeURIComponent(d.codigoDepartamento) + "/personal");
        if (!respuesta.ok) {
            return mostrarError(await leerMensaje(respuesta, "No se pudo consultar el personal."));
        }
        const personas = await respuesta.json();
        const tabla = document.createElement("table");
        tabla.className = "table table-sm tabla-sigh text-left mb-0";
        tabla.innerHTML = "<thead><tr><th>Código</th><th>Nombre</th><th>Cargo</th><th>Correo</th></tr></thead>";
        const tbody = document.createElement("tbody");
        personas.forEach(p => {
            const tr = document.createElement("tr");
            [p.codigoPersona, p.nombres + " " + p.apellidos, p.cargo || "-", p.correo || "Sin registro"].forEach(v => {
                const td = document.createElement("td"); td.textContent = v; tr.appendChild(td);
            });
            tbody.appendChild(tr);
        });
        if (!personas.length) {
            tbody.innerHTML = '<tr><td colspan="4" class="text-center text-muted">El departamento no tiene personal asignado.</td></tr>';
        }
        tabla.appendChild(tbody);
        Swal.fire({
            title: "Personal de " + d.nombre,
            html: tabla.outerHTML,
            width: 720,
            confirmButtonText: "Cerrar",
            confirmButtonColor: "#127c78"
        });
    }

    function editar(d) {
        editando = true;
        codigo.value = d.codigoDepartamento;
        codigo.readOnly = true;
        nombre.value = d.nombre;
        activo.checked = d.activo;
        document.getElementById("tituloForm").textContent = "Editar departamento " + d.codigoDepartamento;
        document.getElementById("btnGuardar").innerHTML = '<i class="bi bi-arrow-repeat mr-1"></i>Actualizar';
        nombre.focus();
    }

    function eliminar(d) {
        Swal.fire({
            icon: "warning", title: "Eliminar departamento",
            text: "Se eliminará " + d.codigoDepartamento + " - " + d.nombre + ".",
            showCancelButton: true, confirmButtonText: "Eliminar", cancelButtonText: "Cancelar",
            confirmButtonColor: "#b54747", cancelButtonColor: "#627d98"
        }).then(async (r) => {
            if (!r.isConfirmed) return;
            const respuesta = await fetch(API + "/" + encodeURIComponent(d.codigoDepartamento), {method: "DELETE"});
            if (!respuesta.ok) {
                return mostrarError(await leerMensaje(respuesta, "No se pudo eliminar el departamento."));
            }
            limpiar();
            await cargar();
            Swal.fire({icon: "success", title: "Departamento eliminado", confirmButtonColor: "#127c78"});
        });
    }

    function limpiar() {
        editando = false;
        form.reset();
        codigo.readOnly = false;
        activo.checked = true;
        document.getElementById("tituloForm").textContent = "Registrar departamento";
        document.getElementById("btnGuardar").innerHTML = '<i class="bi bi-save mr-1"></i>Guardar';
    }

    function mostrarError(texto) {
        Swal.fire({icon: "error", title: "Operación no completada", text: texto, confirmButtonColor: "#127c78"});
    }

    async function leerMensaje(respuesta, defecto) {
        if (respuesta.status === 401) {
            window.location.href = "${ctx}/login?expirada";
        }
        try {
            const datos = await respuesta.json();
            return datos.mensaje || defecto;
        } catch (e) {
            return defecto;
        }
    }

    cargar();
</script>
</c:if>
</body>
</html>
