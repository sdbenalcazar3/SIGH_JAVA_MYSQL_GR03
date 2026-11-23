<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Módulo de Reportes | SIGH GR03</title>
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

<main class="container contenido" id="moduloReportes">
    <div class="d-flex flex-wrap justify-content-between align-items-end mb-4">
        <div>
            <div class="titulo-seccion mb-1">Personal &middot; JasperReports 7.0.8</div>
            <h1 class="h3 mb-1" style="color: var(--navy)">Módulo de reportes</h1>
            <p class="ayuda mb-0">
                Generación de reportes institucionales en <strong>PDF</strong> (vista previa y descarga) y
                <strong>Excel (.xlsx)</strong> con filtros combinables sobre <code>PEDEP_DEPART</code>,
                <code>PECAR_CARGO</code> y <code>PEPER_PERSON</code>.
            </p>
        </div>
        <div class="mt-2 d-flex align-items-center">
            <span class="badge-formato badge-formato-pdf mr-2"><i class="bi bi-file-earmark-pdf mr-1"></i>PDF</span>
            <span class="badge-formato badge-formato-xlsx mr-2"><i class="bi bi-file-earmark-excel mr-1"></i>XLSX</span>
            <button class="btn btn-light border btn-sm" type="button" id="btnActualizarResumen" title="Actualizar indicadores">
                <i class="bi bi-arrow-clockwise mr-1"></i>Actualizar KPIs
            </button>
        </div>
    </div>

    <!-- 4 KPIs SUPERIORES (GET /api/reportes/resumen) -->
    <div class="row mb-4">
        <div class="col-sm-6 col-lg-3 mb-3 mb-lg-0">
            <div class="indicador indicador-reporte indicador-navy">
                <div class="d-flex justify-content-between align-items-start">
                    <span>Departamentos</span>
                    <i class="bi bi-building indicador-icono"></i>
                </div>
                <strong id="kpiTotalDepartamentos">--</strong>
                <small class="indicador-sub" id="kpiSubDepartamentos">Cargando activos...</small>
            </div>
        </div>
        <div class="col-sm-6 col-lg-3 mb-3 mb-lg-0">
            <div class="indicador indicador-reporte indicador-teal">
                <div class="d-flex justify-content-between align-items-start">
                    <span>Cargos registrados</span>
                    <i class="bi bi-briefcase indicador-icono"></i>
                </div>
                <strong id="kpiTotalCargos">--</strong>
                <small class="indicador-sub" id="kpiSubCargos">Cargando activos...</small>
            </div>
        </div>
        <div class="col-sm-6 col-lg-3 mb-3 mb-sm-0">
            <div class="indicador indicador-reporte indicador-amber">
                <div class="d-flex justify-content-between align-items-start">
                    <span>Personal total</span>
                    <i class="bi bi-people indicador-icono"></i>
                </div>
                <strong id="kpiTotalPersonal">--</strong>
                <small class="indicador-sub" id="kpiSubPersonal">Directorio PEPER_PERSON</small>
            </div>
        </div>
        <div class="col-sm-6 col-lg-3">
            <div class="indicador indicador-reporte indicador-activo">
                <div class="d-flex justify-content-between align-items-start">
                    <span>Personal activo</span>
                    <i class="bi bi-person-check indicador-icono"></i>
                </div>
                <strong id="kpiPersonalActivo">--</strong>
                <small class="indicador-sub" id="kpiSubPersonalActivo">Calculando cobertura...</small>
            </div>
        </div>
    </div>

    <!-- TARJETA 1: REPORTE DE DEPARTAMENTOS -->
    <section class="panel tarjeta-reporte" data-tarjeta="departamentos">
        <div class="tarjeta-reporte-cabecera">
            <div class="d-flex align-items-start">
                <div class="reporte-icono-wrap mr-3">
                    <i class="bi bi-building"></i>
                </div>
                <div>
                    <div class="titulo-seccion mb-1">A4 Vertical &middot; PEDEP_DEPART</div>
                    <h2 class="mb-1">1. Reporte de Departamentos</h2>
                    <p class="ayuda mb-0">
                        Listado oficial de departamentos con estado y conteo consolidado de personal asignado en <code>PEPER_PERSON</code>.
                    </p>
                </div>
            </div>
            <button type="button" class="btn btn-light border btn-sm mt-2 mt-md-0" id="depBtnLimpiar">
                <i class="bi bi-eraser mr-1"></i>Limpiar filtros
            </button>
        </div>

        <form id="formReporteDepartamentos" class="tarjeta-reporte-filtros" autocomplete="off">
            <div class="form-row align-items-end">
                <div class="col-md-7 form-group mb-2">
                    <label class="small font-weight-bold" for="depFiltroTexto">Buscar por código o nombre</label>
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <span class="input-group-text"><i class="bi bi-search"></i></span>
                        </div>
                        <input type="text" class="form-control" id="depFiltroTexto"
                               placeholder="Ej. D02 o Emergencia">
                    </div>
                </div>
                <div class="col-md-5 form-group mb-2">
                    <label class="small font-weight-bold" for="depFiltroEstado">Estado</label>
                    <select class="custom-select" id="depFiltroEstado">
                        <option value="">Todos los estados</option>
                        <option value="true">Solo activos</option>
                        <option value="false">Solo inactivos</option>
                    </select>
                </div>
            </div>
        </form>

        <div class="tarjeta-reporte-acciones">
            <button type="button" class="btn btn-sigh btn-sm btn-reporte-accion"
                    data-reporte="departamentos" data-accion="preview">
                <i class="bi bi-eye mr-1"></i>Vista previa PDF
            </button>
            <button type="button" class="btn btn-reporte-pdf btn-sm btn-reporte-accion"
                    data-reporte="departamentos" data-accion="pdf">
                <i class="bi bi-file-earmark-pdf mr-1"></i>Descargar PDF
            </button>
            <button type="button" class="btn btn-reporte-xlsx btn-sm btn-reporte-accion"
                    data-reporte="departamentos" data-accion="xlsx">
                <i class="bi bi-file-earmark-excel mr-1"></i>Exportar Excel (.xlsx)
            </button>
        </div>
    </section>

    <!-- TARJETA 2: REPORTE DE CARGOS -->
    <section class="panel tarjeta-reporte" data-tarjeta="cargos">
        <div class="tarjeta-reporte-cabecera">
            <div class="d-flex align-items-start">
                <div class="reporte-icono-wrap reporte-icono-teal mr-3">
                    <i class="bi bi-briefcase"></i>
                </div>
                <div>
                    <div class="titulo-seccion mb-1">A4 Vertical &middot; PECAR_CARGO</div>
                    <h2 class="mb-1">2. Reporte de Cargos</h2>
                    <p class="ayuda mb-0">
                        Catálogo de cargos con total de personal y detalle de departamentos donde se ocupa el cargo (incluye filtro <code>SIN_PERSONAL</code>).
                    </p>
                </div>
            </div>
            <button type="button" class="btn btn-light border btn-sm mt-2 mt-md-0" id="carBtnLimpiar">
                <i class="bi bi-eraser mr-1"></i>Limpiar filtros
            </button>
        </div>

        <form id="formReporteCargos" class="tarjeta-reporte-filtros" autocomplete="off">
            <div class="form-row align-items-end">
                <div class="col-md-4 form-group mb-2">
                    <label class="small font-weight-bold" for="carFiltroTexto">Buscar por código o nombre</label>
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <span class="input-group-text"><i class="bi bi-search"></i></span>
                        </div>
                        <input type="text" class="form-control" id="carFiltroTexto"
                               placeholder="Ej. C03 o Médico">
                    </div>
                </div>
                <div class="col-md-3 form-group mb-2">
                    <label class="small font-weight-bold" for="carFiltroEstado">Estado</label>
                    <select class="custom-select" id="carFiltroEstado">
                        <option value="">Todos los estados</option>
                        <option value="true">Solo activos</option>
                        <option value="false">Solo inactivos</option>
                    </select>
                </div>
                <div class="col-md-5 form-group mb-2">
                    <label class="small font-weight-bold" for="carFiltroDepartamento">Departamento</label>
                    <select class="custom-select" id="carFiltroDepartamento">
                        <option value="">Todos los departamentos</option>
                        <option value="SIN_PERSONAL">Sin personal asignado</option>
                        <c:forEach var="dep" items="${listaDepartamentos}">
                            <option value="<c:out value='${dep.codigoDepartamento}'/>"><c:out value="${dep.codigoDepartamento} - ${dep.nombre}"/></option>
                        </c:forEach>
                    </select>
                </div>
            </div>
        </form>

        <div class="tarjeta-reporte-acciones">
            <button type="button" class="btn btn-sigh btn-sm btn-reporte-accion"
                    data-reporte="cargos" data-accion="preview">
                <i class="bi bi-eye mr-1"></i>Vista previa PDF
            </button>
            <button type="button" class="btn btn-reporte-pdf btn-sm btn-reporte-accion"
                    data-reporte="cargos" data-accion="pdf">
                <i class="bi bi-file-earmark-pdf mr-1"></i>Descargar PDF
            </button>
            <button type="button" class="btn btn-reporte-xlsx btn-sm btn-reporte-accion"
                    data-reporte="cargos" data-accion="xlsx">
                <i class="bi bi-file-earmark-excel mr-1"></i>Exportar Excel (.xlsx)
            </button>
        </div>
    </section>

    <!-- TARJETA 3: REPORTE DE PERSONAL / EMPLEADOS -->
    <section class="panel tarjeta-reporte" data-tarjeta="personal">
        <div class="tarjeta-reporte-cabecera">
            <div class="d-flex align-items-start">
                <div class="reporte-icono-wrap reporte-icono-amber mr-3">
                    <i class="bi bi-person-vcard"></i>
                </div>
                <div>
                    <div class="titulo-seccion mb-1">A4 Horizontal &middot; PEPER_PERSON</div>
                    <h2 class="mb-1">3. Reporte de Personal / Empleados</h2>
                    <p class="ayuda mb-0">
                        Directorio completo de personal enriquecido con departamento, cargo, función, correo y totales de registros académicos (<code>PEACA_ACADEM</code>) y familiares (<code>PEFAM_FAMILI</code>).
                    </p>
                </div>
            </div>
            <button type="button" class="btn btn-light border btn-sm mt-2 mt-md-0" id="perBtnLimpiar">
                <i class="bi bi-eraser mr-1"></i>Limpiar filtros
            </button>
        </div>

        <form id="formReportePersonal" class="tarjeta-reporte-filtros" autocomplete="off">
            <div class="form-row align-items-end">
                <div class="col-md-4 form-group mb-2">
                    <label class="small font-weight-bold" for="perFiltroTexto">Buscar empleado</label>
                    <div class="input-group">
                        <div class="input-group-prepend">
                            <span class="input-group-text"><i class="bi bi-search"></i></span>
                        </div>
                        <input type="text" class="form-control" id="perFiltroTexto"
                               placeholder="Código, apellidos, nombres o documento">
                    </div>
                </div>
                <div class="col-md-2 form-group mb-2">
                    <label class="small font-weight-bold" for="perFiltroEstado">Estado</label>
                    <select class="custom-select" id="perFiltroEstado">
                        <option value="">Todos</option>
                        <option value="true">Activos</option>
                        <option value="false">Inactivos</option>
                    </select>
                </div>
                <div class="col-md-3 form-group mb-2">
                    <label class="small font-weight-bold" for="perFiltroDepartamento">Departamento</label>
                    <select class="custom-select" id="perFiltroDepartamento">
                        <option value="">Todos los departamentos</option>
                        <c:forEach var="dep" items="${listaDepartamentos}">
                            <option value="<c:out value='${dep.codigoDepartamento}'/>"><c:out value="${dep.codigoDepartamento} - ${dep.nombre}"/></option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-3 form-group mb-2">
                    <label class="small font-weight-bold" for="perFiltroCargo">Cargo</label>
                    <select class="custom-select" id="perFiltroCargo">
                        <option value="">Todos los cargos</option>
                        <c:forEach var="car" items="${listaCargos}">
                            <option value="<c:out value='${car.codigoCargo}'/>"><c:out value="${car.codigoCargo} - ${car.nombre}"/></option>
                        </c:forEach>
                    </select>
                </div>
            </div>
        </form>

        <div class="tarjeta-reporte-acciones">
            <button type="button" class="btn btn-sigh btn-sm btn-reporte-accion"
                    data-reporte="personal" data-accion="preview">
                <i class="bi bi-eye mr-1"></i>Vista previa PDF
            </button>
            <button type="button" class="btn btn-reporte-pdf btn-sm btn-reporte-accion"
                    data-reporte="personal" data-accion="pdf">
                <i class="bi bi-file-earmark-pdf mr-1"></i>Descargar PDF
            </button>
            <button type="button" class="btn btn-reporte-xlsx btn-sm btn-reporte-accion"
                    data-reporte="personal" data-accion="xlsx">
                <i class="bi bi-file-earmark-excel mr-1"></i>Exportar Excel (.xlsx)
            </button>
        </div>
    </section>
</main>

<!-- MODAL VISOR PDF EMBEBIDO (BOOTSTRAP 4 + CONTROLADOR VANILLA JS) -->
<div class="modal fade modal-sigh" id="modalVistaPreviaPdf" tabindex="-1" role="dialog"
     aria-labelledby="tituloModalPdf" aria-hidden="true">
    <div class="modal-dialog modal-xl modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header modal-header-sigh">
                <div>
                    <h5 class="modal-title mb-0" id="tituloModalPdf">
                        <i class="bi bi-file-earmark-pdf mr-2"></i>Vista previa del reporte
                    </h5>
                    <small class="text-white-50" id="subtituloModalPdf">Cargando documento PDF...</small>
                </div>
                <button type="button" class="close text-white" data-dismiss="modal" aria-label="Cerrar">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body p-0">
                <div class="visor-pdf-contenedor">
                    <div id="cargandoVistaPrevia" class="visor-pdf-cargando">
                        <div class="spinner-border text-info mb-2" role="status">
                            <span class="sr-only">Generando PDF...</span>
                        </div>
                        <span class="small font-weight-bold text-muted">Generando vista previa PDF...</span>
                    </div>
                    <iframe id="iframeVistaPreviaPdf" class="visor-pdf-iframe d-none"
                            title="Vista previa del reporte PDF"></iframe>
                </div>
            </div>
            <div class="modal-footer justify-content-between">
                <span class="small text-muted d-none d-sm-inline">
                    <i class="bi bi-info-circle mr-1"></i>Si su navegador bloquea el visor integrado, utilice los botones de apertura o descarga.
                </span>
                <div class="d-flex flex-wrap">
                    <a id="btnAbrirPestanaPdf" class="btn btn-light border btn-sm mr-2 disabled"
                       href="#" target="_blank" rel="noopener noreferrer">
                        <i class="bi bi-box-arrow-up-right mr-1"></i>Abrir en pestaña
                    </a>
                    <button type="button" class="btn btn-reporte-pdf btn-sm mr-2" id="btnDescargarDesdeModal" disabled>
                        <i class="bi bi-download mr-1"></i>Descargar PDF
                    </button>
                    <button type="button" class="btn btn-light border btn-sm" data-dismiss="modal">Cerrar</button>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="${ctx}/lib/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${ctx}/lib/sweetalert2/sweetalert2.all.min.js"></script>
<script src="${ctx}/js/reportes.js"></script>
</body>
</html>
