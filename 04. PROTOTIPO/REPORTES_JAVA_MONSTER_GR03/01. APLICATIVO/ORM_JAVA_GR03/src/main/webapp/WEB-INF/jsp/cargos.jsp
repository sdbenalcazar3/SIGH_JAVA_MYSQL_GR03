<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="activosIniciales" value="0"/>
<c:forEach var="cargoItem" items="${listaCargos}">
    <c:if test="${cargoItem.activo}">
        <c:set var="activosIniciales" value="${activosIniciales + 1}"/>
    </c:if>
</c:forEach>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mantenimiento de Cargos | SIGH GR03</title>
    <link rel="stylesheet" href="${ctx}/lib/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${ctx}/lib/bootstrap-icons/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${ctx}/lib/sweetalert2/sweetalert2.min.css">
    <link rel="stylesheet" href="${ctx}/css/sigh.css">
</head>
<body data-ctx="<c:out value='${ctx}'/>">
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
            <a class="btn btn-salir btn-sm mr-2" href="${ctx}/inicio"><i class="bi bi-house-door mr-1"></i>Inicio</a>
            <a class="btn btn-salir btn-sm" href="${ctx}/logout"><i class="bi bi-box-arrow-right mr-1"></i>Salir</a>
        </div>
    </div>
</header>

<main class="container contenido" id="moduloCargos">
    <div class="d-flex flex-wrap justify-content-between align-items-end mb-4">
        <div>
            <div class="titulo-seccion mb-1">Personal &middot; ListBox MVC</div>
            <h1 class="h3 mb-1" style="color: var(--navy)">Mantenimiento de cargos</h1>
            <p class="ayuda mb-0">
                Cat&aacute;logo <code>PECAR_CARGO</code> gestionado con ListBox, filtros combinables
                (texto, estado y departamento v&iacute;a <code>PEPER_PERSON</code>) y autogeneraci&oacute;n AJAX de c&oacute;digo.
            </p>
        </div>
        <div class="mt-2">
            <button class="btn btn-sigh btn-sm" type="button" id="btnNuevoCargo">
                <i class="bi bi-plus-lg mr-1"></i>Nuevo cargo
            </button>
        </div>
    </div>

    <div class="row mb-3">
        <div class="col-md-4 mb-2 mb-md-0">
            <div class="indicador">
                <span>Total de cargos</span>
                <strong id="totalCargos"><c:out value="${listaCargos.size()}"/></strong>
            </div>
        </div>
        <div class="col-md-4 mb-2 mb-md-0">
            <div class="indicador">
                <span>Cargos activos</span>
                <strong id="totalCargosActivos"><c:out value="${activosIniciales}"/></strong>
            </div>
        </div>
        <div class="col-md-4">
            <div class="indicador">
                <span>Personal asignado</span>
                <strong id="totalPersonalCargos">0</strong>
            </div>
        </div>
    </div>

    <section class="panel barra-filtros-cargos">
        <div class="d-flex flex-wrap justify-content-between align-items-center mb-2">
            <div>
                <h2 class="mb-0">Filtros de consulta</h2>
                <p class="ayuda mb-0">Combine b&uacute;squeda por texto, estado y ComboBox de departamento.</p>
            </div>
            <button class="btn btn-light border btn-sm mt-2 mt-sm-0" type="button" id="btnLimpiarFiltros">
                <i class="bi bi-eraser mr-1"></i>Limpiar filtros
            </button>
        </div>

        <div class="form-row align-items-end mt-2">
            <div class="col-md-4 mb-2">
                <label class="small font-weight-bold" for="filtroTexto">Buscar por c&oacute;digo o nombre</label>
                <div class="input-group">
                    <div class="input-group-prepend">
                        <span class="input-group-text"><i class="bi bi-search"></i></span>
                    </div>
                    <input type="text" class="form-control" id="filtroTexto"
                           placeholder="Ej. C03 o Medico" autocomplete="off">
                </div>
            </div>
            <div class="col-md-3 mb-2">
                <label class="small font-weight-bold" for="filtroEstado">Estado</label>
                <select class="custom-select" id="filtroEstado">
                    <option value="">Todos los estados</option>
                    <option value="true">Activos</option>
                    <option value="false">Inactivos</option>
                </select>
            </div>
            <div class="col-md-5 mb-2">
                <label class="small font-weight-bold" for="filtroDepartamento">Departamento (ComboBox PEDEP_DEPART)</label>
                <select class="custom-select" id="filtroDepartamento">
                    <option value="">Todos los departamentos</option>
                    <option value="SIN_PERSONAL">Sin personal asignado</option>
                    <c:forEach var="dep" items="${listaDepartamentos}">
                        <option value="<c:out value='${dep.codigoDepartamento}'/>"><c:out value="${dep.codigoDepartamento} - ${dep.nombre}"/></option>
                    </c:forEach>
                </select>
            </div>
        </div>
    </section>

    <div class="row">
        <div class="col-lg-7">
            <section class="panel">
                <div class="d-flex flex-wrap justify-content-between align-items-center mb-2">
                    <div>
                        <h2 class="mb-0">ListBox de cargos</h2>
                        <span id="contadorResultados" class="ayuda">Mostrando <c:out value="${listaCargos.size()}"/> de <c:out value="${listaCargos.size()}"/> cargos</span>
                    </div>
                    <div class="custom-control custom-checkbox mt-2 mt-sm-0">
                        <input type="checkbox" class="custom-control-input" id="chkSeleccionMultiple">
                        <label class="custom-control-label small font-weight-bold" for="chkSeleccionMultiple">
                            Selecci&oacute;n m&uacute;ltiple (lote)
                        </label>
                    </div>
                </div>

                <label class="sr-only" for="lstCargos">Listado de cargos</label>
                <select id="lstCargos" size="8" class="form-control listbox-sigh">
                    <c:choose>
                        <c:when test="${empty listaCargos}">
                            <option disabled>No hay cargos registrados.</option>
                        </c:when>
                        <c:otherwise>
                            <c:forEach var="cargo" items="${listaCargos}">
                                <option value="<c:out value='${cargo.codigoCargo}'/>"><c:out value="${cargo.codigoCargo} — ${cargo.nombre} [${cargo.activo ? 'Activo' : 'Inactivo'} · 0 pers.]"/></option>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </select>

                <p class="ayuda mt-2 mb-3">
                    En modo &uacute;nico seleccione un cargo para editarlo, ver su personal o eliminarlo.
                    En modo m&uacute;ltiple use <code>Ctrl</code> o <code>Shift</code> para cambiar el estado en lote.
                </p>

                <div class="d-flex flex-wrap align-items-center">
                    <button class="btn btn-ver-personal btn-sm mr-2 mb-2" type="button" id="btnVerPersonal" disabled>
                        <i class="bi bi-people mr-1"></i>Ver personal
                    </button>
                    <button class="btn btn-eliminar btn-sm mb-2" type="button" id="btnEliminarCargo" disabled>
                        <i class="bi bi-trash3 mr-1"></i>Eliminar cargo
                    </button>
                </div>

                <div id="barraLote" class="barra-lote d-none mt-2">
                    <div class="d-flex flex-wrap justify-content-between align-items-center">
                        <span class="small font-weight-bold mb-2 mb-sm-0" id="contadorLote">0 cargo(s) seleccionado(s)</span>
                        <div>
                            <button class="btn btn-sigh btn-sm mr-2" type="button" id="btnActivarLote" disabled>
                                <i class="bi bi-check-circle mr-1"></i>Activar seleccionados
                            </button>
                            <button class="btn btn-eliminar btn-sm" type="button" id="btnInactivarLote" disabled>
                                <i class="bi bi-x-circle mr-1"></i>Inactivar seleccionados
                            </button>
                        </div>
                    </div>
                </div>
            </section>
        </div>

        <div class="col-lg-5">
            <section class="panel">
                <div class="titulo-seccion mb-1">Formulario PECAR_CARGO</div>
                <h2 id="tituloFormCargo" class="mb-3">Registrar cargo</h2>

                <form id="formCargo" autocomplete="off">
                    <div class="form-group">
                        <label class="small font-weight-bold" for="codigoCargo">C&oacute;digo (3 caracteres)</label>
                        <div class="input-group">
                            <input type="text" class="form-control" id="codigoCargo" maxlength="3"
                                   placeholder="C08" required>
                            <div class="input-group-append">
                                <button class="btn btn-light border" type="button" id="btnSugerirCodigo"
                                        title="Sugerir siguiente c&oacute;digo correlativo v&iacute;a AJAX">
                                    <i class="bi bi-magic mr-1"></i>Sugerir
                                </button>
                            </div>
                        </div>
                        <small class="form-text text-muted">
                            Formato de 3 caracteres alfanum&eacute;ricos (ej. <code>C08</code>). Editable en registro nuevo.
                        </small>
                    </div>

                    <div class="form-group">
                        <label class="small font-weight-bold" for="nombreCargo">Nombre del cargo</label>
                        <input type="text" class="form-control" id="nombreCargo" maxlength="100"
                               placeholder="Ej. Medico pediatra" required>
                        <small class="form-text text-muted">Entre 3 y 100 caracteres, sin n&uacute;meros.</small>
                    </div>

                    <div class="form-group">
                        <div class="custom-control custom-checkbox">
                            <input type="checkbox" class="custom-control-input" id="activoCargo" checked>
                            <label class="custom-control-label small font-weight-bold" for="activoCargo">Cargo activo</label>
                        </div>
                    </div>

                    <div class="d-flex">
                        <button class="btn btn-sigh flex-fill mr-2" type="submit" id="btnGuardarCargo">
                            <i class="bi bi-save mr-1"></i>Guardar
                        </button>
                        <button class="btn btn-light border" type="button" id="btnLimpiarForm" title="Limpiar formulario">
                            <i class="bi bi-x-lg"></i>
                        </button>
                    </div>
                </form>

                <div id="detalleCargo" class="detalle-cargo d-none mt-4">
                    <div class="small font-weight-bold text-uppercase mb-2" style="color: var(--navy-2)">
                        Departamentos con personal en este cargo
                    </div>
                    <div id="detalleDepartamentos"></div>
                </div>
            </section>
        </div>
    </div>
</main>

<script src="${ctx}/lib/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${ctx}/lib/sweetalert2/sweetalert2.all.min.js"></script>
<script src="${ctx}/js/cargos.js"></script>
</body>
</html>
