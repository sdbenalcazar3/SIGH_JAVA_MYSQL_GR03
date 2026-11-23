<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mantenimiento de Empleados | SIGH GR03</title>
    <link rel="stylesheet" href="${ctx}/lib/bootstrap/css/bootstrap.min.css">
    <link rel="stylesheet" href="${ctx}/lib/bootstrap-icons/bootstrap-icons.min.css">
    <link rel="stylesheet" href="${ctx}/lib/sweetalert2/sweetalert2.min.css">
    <link rel="stylesheet" href="${ctx}/css/sigh.css">
</head>
<body data-ctx="<c:out value='${ctx}'/>">
<header class="barra no-print">
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

<main class="container contenido" id="moduloEmpleados">
    <div class="d-flex flex-wrap justify-content-between align-items-end mb-3 no-print">
        <div>
            <div class="titulo-seccion mb-1">Personal &middot; TabPage MVC</div>
            <h1 class="h3 mb-1" style="color: var(--navy)">Mantenimiento de empleados</h1>
            <p class="ayuda mb-0">
                Ficha maestro-detalle de <code>PEPER_PERSON</code> con pestañas de
                <strong>Datos Generales</strong>, <strong>Datos Académicos</strong> (<code>PEACA_ACADEM</code>)
                y <strong>Datos Familiares</strong> (<code>PEFAM_FAMILI</code>).
            </p>
        </div>
        <div class="mt-2 d-flex align-items-center">
            <span class="etiqueta mb-0 mr-2" id="badgeModoEmpleado">Consultando registro</span>
            <span class="badge-usuario-asociado d-none" id="badgeTieneUsuario"
                  title="Este empleado tiene cuenta de usuario activa en SEUSU_USUARI">
                <i class="bi bi-shield-lock mr-1"></i>Con cuenta de usuario
            </span>
        </div>
    </div>

    <section class="panel tabpage-sigh no-print">
        <div class="tabpage-principal">
            <form id="formEmpleado" autocomplete="off" novalidate>
                    <!-- CABECERA FIJA FUERA DEL CONTENEDOR DE PESTAÑAS (Identificadores generales) -->
                    <div class="cabecera-empleado mb-3">
                        <div class="d-flex justify-content-between align-items-center mb-2">
                            <span class="titulo-seccion">Identificadores generales del empleado</span>
                            <small class="text-muted">Visibles en todas las pestañas</small>
                        </div>
                        <div class="row align-items-start">
                            <div class="col-md-8">
                                <div class="form-row">
                                    <div class="col-sm-6 form-group mb-2">
                                        <label class="small font-weight-bold" for="codigoPersona">Código <span class="text-danger">*</span></label>
                                        <div class="input-group">
                                            <input type="text" class="form-control" id="codigoPersona"
                                                   maxlength="6" placeholder="P00007" required>
                                            <div class="input-group-append">
                                                <button class="btn btn-light border" type="button" id="btnSugerirCodigo"
                                                        title="Sugerir siguiente código correlativo P000xx">
                                                    <i class="bi bi-magic mr-1"></i>Sugerir
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-sm-6 form-group mb-2">
                                        <label class="small font-weight-bold" for="codigoExterno">Código externo</label>
                                        <input type="text" class="form-control" id="codigoExterno"
                                               maxlength="10" placeholder="Autogenerado si se omite">
                                    </div>
                                </div>
                                <div class="form-row">
                                    <div class="col-sm-6 form-group mb-2">
                                        <label class="small font-weight-bold" for="apellidos">Apellidos <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="apellidos"
                                               maxlength="150" placeholder="Ej. Morales Pérez" required>
                                    </div>
                                    <div class="col-sm-6 form-group mb-2">
                                        <label class="small font-weight-bold" for="nombres">Nombres <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control" id="nombres"
                                               maxlength="255" placeholder="Ej. Camila Andrea" required>
                                    </div>
                                </div>
                            </div>

                            <div class="col-md-4">
                                <div class="foto-empleado-box">
                                    <label class="small font-weight-bold d-block text-center mb-2">Foto</label>
                                    <div class="foto-preview-marco mx-auto mb-2">
                                        <img id="previewFoto" class="foto-preview-img d-none" alt="Foto del empleado">
                                        <div id="avatarIniciales" class="foto-avatar-placeholder" aria-hidden="true">EM</div>
                                    </div>
                                    <input type="file" id="inputFoto" class="d-none"
                                           accept=".png,.jpg,.jpeg,.webp,image/png,image/jpeg,image/webp">
                                    <div class="d-flex justify-content-center">
                                        <button type="button" class="btn btn-light border btn-sm mr-1" id="btnSeleccionarFoto">
                                            <i class="bi bi-camera mr-1"></i>Subir
                                        </button>
                                        <button type="button" class="btn btn-eliminar btn-sm" id="btnQuitarFoto" disabled>
                                            <i class="bi bi-x-circle mr-1"></i>Quitar
                                        </button>
                                    </div>
                                    <small id="estadoFoto" class="d-block text-center text-muted mt-1">
                                        PNG, JPG o WEBP &middot; Máx. 2 MB
                                    </small>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- CONTENEDOR TABPAGE (3 PESTAÑAS BOOTSTRAP 4) -->
                    <ul class="nav nav-tabs nav-tabs-sigh" id="tabEmpleados" role="tablist">
                        <li class="nav-item">
                            <a class="nav-link active" id="tab-generales" data-toggle="tab" href="#paneGenerales"
                               role="tab" aria-controls="paneGenerales" aria-selected="true">
                                <i class="bi bi-person-lines-fill mr-1"></i>Datos Generales
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" id="tab-academicos" data-toggle="tab" href="#paneAcademicos"
                               role="tab" aria-controls="paneAcademicos" aria-selected="false">
                                <i class="bi bi-mortarboard mr-1"></i>Datos Académicos
                                <span class="badge badge-pill badge-tab ml-1" id="badgeAcademicos">0</span>
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" id="tab-familiares" data-toggle="tab" href="#paneFamiliares"
                               role="tab" aria-controls="paneFamiliares" aria-selected="false">
                                <i class="bi bi-people mr-1"></i>Datos Familiares
                                <span class="badge badge-pill badge-tab ml-1" id="badgeFamiliares">0</span>
                            </a>
                        </li>
                    </ul>

                    <div class="tab-content tab-content-sigh" id="tabEmpleadosContent">
                        <!-- PESTAÑA 1: DATOS GENERALES -->
                        <div class="tab-pane fade show active" id="paneGenerales" role="tabpanel" aria-labelledby="tab-generales">
                            <div class="subtitulo-tab mb-2">Datos personales y biométricos</div>
                            <div class="form-row">
                                <div class="col-md-4 col-sm-6 form-group">
                                    <label class="small font-weight-bold" for="cbxSexo">Sexo</label>
                                    <select class="custom-select" id="cbxSexo">
                                        <option value="">-- Seleccione --</option>
                                        <c:forEach var="gen" items="${listaGeneros}">
                                            <option value="<c:out value='${gen.codigoGenero}'/>"><c:out value="${gen.nombre}"/></option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-md-4 col-sm-6 form-group">
                                    <label class="small font-weight-bold" for="cbxEstadoCivil">Estado Civil</label>
                                    <select class="custom-select" id="cbxEstadoCivil">
                                        <option value="">-- Seleccione --</option>
                                        <c:forEach var="est" items="${listaEstadosCiviles}">
                                            <option value="<c:out value='${est.codigoEstadoCivil}'/>"><c:out value="${est.nombre}"/></option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-md-4 col-sm-4 form-group">
                                    <label class="small font-weight-bold" for="cbxSangre">Tipo Sangre</label>
                                    <select class="custom-select" id="cbxSangre">
                                        <option value="">-- Seleccione --</option>
                                        <c:forEach var="ts" items="${tiposSangre}">
                                            <option value="<c:out value='${ts}'/>"><c:out value="${ts}"/></option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-md-3 col-sm-4 form-group">
                                    <label class="small font-weight-bold" for="peso">Peso (kg)</label>
                                    <input type="number" class="form-control" id="peso"
                                           step="0.01" min="20.00" max="300.00" placeholder="Ej. 68.50">
                                </div>
                                <div class="col-md-3 col-sm-4 form-group">
                                    <label class="small font-weight-bold" for="estatura">Estatura (m)</label>
                                    <input type="number" class="form-control" id="estatura"
                                           step="0.01" min="0.50" max="2.50" placeholder="Ej. 1.72">
                                </div>
                                <div class="col-md-3 col-sm-6 form-group">
                                    <label class="small font-weight-bold" for="fechaNacimiento">Fecha Nacimiento</label>
                                    <input type="date" class="form-control" id="fechaNacimiento">
                                </div>
                                <div class="col-md-3 col-sm-6 form-group d-flex align-items-end">
                                    <div class="custom-control custom-checkbox mb-2">
                                        <input type="checkbox" class="custom-control-input" id="activo" checked>
                                        <label class="custom-control-label small font-weight-bold" for="activo">Empleado activo</label>
                                    </div>
                                </div>
                            </div>

                            <hr class="my-3">
                            <div class="subtitulo-tab mb-2">Datos laborales y de contacto</div>
                            <div class="form-row">
                                <div class="col-md-4 form-group">
                                    <label class="small font-weight-bold" for="codigoDepartamento">Departamento <span class="text-danger">*</span></label>
                                    <select class="custom-select" id="codigoDepartamento" required>
                                        <option value="">-- Seleccione --</option>
                                        <c:forEach var="dep" items="${listaDepartamentos}">
                                            <option value="<c:out value='${dep.codigoDepartamento}'/>"><c:out value="${dep.codigoDepartamento} - ${dep.nombre}"/></option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-md-4 form-group">
                                    <label class="small font-weight-bold" for="codigoCargo">Cargo <span class="text-danger">*</span></label>
                                    <select class="custom-select" id="codigoCargo" required>
                                        <option value="">-- Seleccione --</option>
                                        <c:forEach var="car" items="${listaCargos}">
                                            <option value="<c:out value='${car.codigoCargo}'/>"><c:out value="${car.codigoCargo} - ${car.nombre}"/></option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="col-md-4 form-group">
                                    <label class="small font-weight-bold" for="funcion">Función <span class="text-danger">*</span></label>
                                    <input type="text" class="form-control" id="funcion"
                                           maxlength="255" placeholder="Ej. Médico residente de guardia" required>
                                </div>
                            </div>

                            <div class="form-row">
                                <div class="col-md-3 col-sm-6 form-group">
                                    <label class="small font-weight-bold" for="tipoDocumento">Tipo Documento</label>
                                    <select class="custom-select" id="tipoDocumento">
                                        <option value="">-- Seleccione --</option>
                                        <option value="CEDULA">Cédula</option>
                                        <option value="PASAPORTE">Pasaporte</option>
                                        <option value="RUC">RUC</option>
                                        <option value="OTRO">Otro</option>
                                    </select>
                                </div>
                                <div class="col-md-3 col-sm-6 form-group">
                                    <label class="small font-weight-bold" for="numeroDocumento">Número Documento</label>
                                    <input type="text" class="form-control" id="numeroDocumento"
                                           maxlength="30" placeholder="Ej. 1700000007">
                                </div>
                                <div class="col-md-3 col-sm-6 form-group">
                                    <label class="small font-weight-bold" for="telefono">Teléfono</label>
                                    <input type="text" class="form-control" id="telefono"
                                           maxlength="30" placeholder="Ej. 0990000007">
                                </div>
                                <div class="col-md-3 col-sm-6 form-group">
                                    <label class="small font-weight-bold" for="correo">Correo electrónico</label>
                                    <input type="email" class="form-control" id="correo"
                                           maxlength="150" placeholder="nombre@sigh.local">
                                </div>
                            </div>

                            <div class="form-row">
                                <div class="col-12 form-group mb-0">
                                    <label class="small font-weight-bold" for="habilitacion">Habilitación / Registro profesional</label>
                                    <input type="text" class="form-control" id="habilitacion"
                                           maxlength="255" placeholder="Ej. MSP-SENESCYT-1005-2024 (opcional)">
                                </div>
                            </div>
                        </div>

                        <!-- PESTAÑA 2: DATOS ACADÉMICOS (GRILLA MAESTRO-DETALLE) -->
                        <div class="tab-pane fade" id="paneAcademicos" role="tabpanel" aria-labelledby="tab-academicos">
                            <div class="barra-subform mb-3">
                                <div class="small font-weight-bold text-uppercase mb-2" style="color: var(--teal-2)" id="tituloSubformAcademico">
                                    Agregar formación académica
                                </div>
                                <div class="form-row align-items-end">
                                    <div class="col-md-3 form-group mb-2">
                                        <label class="small font-weight-bold" for="acaNivel">Nivel <span class="text-danger">*</span></label>
                                        <select class="custom-select custom-select-sm" id="acaNivel">
                                            <option value="">-- Nivel --</option>
                                            <c:forEach var="niv" items="${nivelesAcademicos}">
                                                <option value="<c:out value='${niv}'/>"><c:out value="${niv}"/></option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="col-md-4 form-group mb-2">
                                        <label class="small font-weight-bold" for="acaInstitucion">Institución <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control form-control-sm" id="acaInstitucion"
                                               maxlength="150" placeholder="Ej. Universidad Central del Ecuador">
                                    </div>
                                    <div class="col-md-3 form-group mb-2">
                                        <label class="small font-weight-bold" for="acaTitulo">Título <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control form-control-sm" id="acaTitulo"
                                               maxlength="150" placeholder="Ej. Médico Cirujano">
                                    </div>
                                    <div class="col-md-2 form-group mb-2 d-flex">
                                        <button type="button" class="btn btn-sigh btn-sm flex-fill" id="btnGuardarAcademico">
                                            <i class="bi bi-plus-lg mr-1"></i>Agregar
                                        </button>
                                        <button type="button" class="btn btn-light border btn-sm ml-1 d-none" id="btnCancelarAcademico" title="Cancelar edición">
                                            <i class="bi bi-x-lg"></i>
                                        </button>
                                    </div>
                                </div>
                            </div>

                            <div class="table-responsive">
                                <table class="table table-sm tabla-sigh grilla-tabpage mb-0">
                                    <thead>
                                    <tr>
                                        <th>Nivel</th>
                                        <th>Institución</th>
                                        <th>Título</th>
                                        <th class="text-right">Acciones</th>
                                    </tr>
                                    </thead>
                                    <tbody id="cuerpoAcademicos">
                                    <tr>
                                        <td colspan="4" class="text-center text-muted py-3">Sin registros académicos.</td>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>

                        <!-- PESTAÑA 3: DATOS FAMILIARES (GRILLA MAESTRO-DETALLE) -->
                        <div class="tab-pane fade" id="paneFamiliares" role="tabpanel" aria-labelledby="tab-familiares">
                            <div class="barra-subform mb-3">
                                <div class="small font-weight-bold text-uppercase mb-2" style="color: var(--teal-2)" id="tituloSubformFamiliar">
                                    Agregar registro familiar
                                </div>
                                <div class="form-row align-items-end">
                                    <div class="col-md-2 col-sm-6 form-group mb-2">
                                        <label class="small font-weight-bold" for="famParentesco">Parentesco <span class="text-danger">*</span></label>
                                        <select class="custom-select custom-select-sm" id="famParentesco">
                                            <option value="">-- Seleccione --</option>
                                            <c:forEach var="par" items="${parentescos}">
                                                <option value="<c:out value='${par}'/>"><c:out value="${par}"/></option>
                                            </c:forEach>
                                        </select>
                                    </div>
                                    <div class="col-md-3 col-sm-6 form-group mb-2">
                                        <label class="small font-weight-bold" for="famApellidos">Apellidos <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control form-control-sm" id="famApellidos"
                                               maxlength="150" placeholder="Ej. Mora Cárdenas">
                                    </div>
                                    <div class="col-md-3 col-sm-6 form-group mb-2">
                                        <label class="small font-weight-bold" for="famNombres">Nombres <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control form-control-sm" id="famNombres"
                                               maxlength="150" placeholder="Ej. Emilia">
                                    </div>
                                    <div class="col-md-2 col-sm-6 form-group mb-2">
                                        <label class="small font-weight-bold" for="famFechaNacimiento">Fec. Nac.</label>
                                        <input type="date" class="form-control form-control-sm" id="famFechaNacimiento">
                                    </div>
                                    <div class="col-md-2 form-group mb-2 d-flex">
                                        <button type="button" class="btn btn-sigh btn-sm flex-fill" id="btnGuardarFamiliar">
                                            <i class="bi bi-plus-lg mr-1"></i>Agregar
                                        </button>
                                        <button type="button" class="btn btn-light border btn-sm ml-1 d-none" id="btnCancelarFamiliar" title="Cancelar edición">
                                            <i class="bi bi-x-lg"></i>
                                        </button>
                                    </div>
                                </div>
                            </div>

                            <div class="table-responsive">
                                <table class="table table-sm tabla-sigh grilla-tabpage mb-0">
                                    <thead>
                                    <tr>
                                        <th>Parentesco</th>
                                        <th>Apellidos</th>
                                        <th>Nombres</th>
                                        <th>Fec. Nac.</th>
                                        <th class="text-right">Acciones</th>
                                    </tr>
                                    </thead>
                                    <tbody id="cuerpoFamiliares">
                                    <tr>
                                        <td colspan="5" class="text-center text-muted py-3">Sin registros familiares.</td>
                                    </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </form>
        </div>

        <!-- BARRA INFERIOR DE 5 ACCIONES EXACTAS -->
        <div class="barra-acciones-tabpage">
            <button type="button" class="btn btn-accion-secundaria mr-2 mb-2" id="btnNuevoEmpleado">
                <i class="bi bi-file-earmark-plus mr-1"></i>Nuevo
            </button>
            <button type="button" class="btn btn-accion-secundaria mr-2 mb-2" id="btnListarEmpleados">
                <i class="bi bi-list-ul mr-1"></i>Listar
            </button>
            <button type="button" class="btn btn-sigh btn-accion-principal mr-2 mb-2" id="btnGuardarEmpleado">
                <i class="bi bi-arrow-repeat mr-1"></i><span id="textoBtnGuardar">Actualizar</span>
            </button>
            <button type="button" class="btn btn-eliminar btn-accion-peligro mr-2 mb-2" id="btnBorrarEmpleado" disabled>
                <i class="bi bi-trash3 mr-1"></i>Borrar
            </button>
            <button type="button" class="btn btn-accion-secundaria mb-2" id="btnImprimirEmpleado">
                <i class="bi bi-printer mr-1"></i>Imprimir
            </button>
        </div>
    </section>

    <!-- SECCIÓN DEDICADA PARA IMPRESIÓN (@media print) -->
    <section class="ficha-imprimible" id="fichaImprimible" aria-label="Ficha imprimible del empleado">
        <div class="print-encabezado">
            <div>
                <div class="print-sub">Sistema de Gestión Hospitalaria &middot; SIGH GR03</div>
                <h2 class="print-titulo">Ficha de Mantenimiento de Empleado</h2>
            </div>
            <div class="print-meta">
                <div><strong>Código:</strong> <span id="printCodigo">-</span></div>
                <div><strong>Estado:</strong> <span id="printEstado">-</span></div>
            </div>
        </div>

        <div class="print-cabecera-emp">
            <div class="print-foto-wrap">
                <img id="printFotoImg" class="print-foto d-none" alt="Foto empleado">
                <div id="printAvatar" class="print-avatar">EM</div>
            </div>
            <div class="print-identidad">
                <h3 id="printNombreCompleto">-</h3>
                <p id="printCargoDepto" class="mb-1">-</p>
                <p id="printFuncion" class="mb-0 text-muted">-</p>
            </div>
        </div>

        <div class="print-bloque">
            <h4>1. Datos Generales</h4>
            <table class="print-tabla-datos">
                <tbody>
                <tr>
                    <th>Código Externo</th><td id="printCodigoExterno">-</td>
                    <th>Documento</th><td id="printDocumento">-</td>
                </tr>
                <tr>
                    <th>Sexo</th><td id="printSexo">-</td>
                    <th>Estado Civil</th><td id="printEstadoCivil">-</td>
                </tr>
                <tr>
                    <th>Tipo Sangre</th><td id="printTipoSangre">-</td>
                    <th>Fecha Nacimiento</th><td id="printFechaNacimiento">-</td>
                </tr>
                <tr>
                    <th>Peso</th><td id="printPeso">-</td>
                    <th>Estatura</th><td id="printEstatura">-</td>
                </tr>
                <tr>
                    <th>Teléfono</th><td id="printTelefono">-</td>
                    <th>Correo</th><td id="printCorreo">-</td>
                </tr>
                <tr>
                    <th>Habilitación</th><td colspan="3" id="printHabilitacion">-</td>
                </tr>
                </tbody>
            </table>
        </div>

        <div class="print-bloque">
            <h4>2. Datos Académicos</h4>
            <table class="print-tabla-grilla">
                <thead>
                <tr>
                    <th>Nivel</th>
                    <th>Institución</th>
                    <th>Título</th>
                </tr>
                </thead>
                <tbody id="printCuerpoAcademicos">
                <tr><td colspan="3">Sin registros académicos.</td></tr>
                </tbody>
            </table>
        </div>

        <div class="print-bloque">
            <h4>3. Datos Familiares</h4>
            <table class="print-tabla-grilla">
                <thead>
                <tr>
                    <th>Parentesco</th>
                    <th>Apellidos</th>
                    <th>Nombres</th>
                    <th>Fec. Nac.</th>
                </tr>
                </thead>
                <tbody id="printCuerpoFamiliares">
                <tr><td colspan="4">Sin registros familiares.</td></tr>
                </tbody>
            </table>
        </div>

        <div class="print-pie">
            <span>Generado desde SIGH GR03 &middot; ORM_JAVA_GR03</span>
            <span id="printFechaEmision"></span>
        </div>
    </section>
</main>

<!-- MODAL LISTAR EMPLEADOS (BOOTSTRAP 4 ESTRICTO) -->
<div class="modal fade modal-sigh no-print" id="modalListarEmpleados" tabindex="-1" role="dialog"
     aria-labelledby="tituloModalListar" aria-hidden="true">
    <div class="modal-dialog modal-lg modal-dialog-centered" role="document">
        <div class="modal-content">
            <div class="modal-header modal-header-sigh">
                <div>
                    <h5 class="modal-title mb-0" id="tituloModalListar">
                        <i class="bi bi-people mr-2"></i>Listado de empleados
                    </h5>
                    <small class="text-white-50">Seleccione un empleado para cargarlo en el formulario TabPage</small>
                </div>
                <button type="button" class="close text-white" data-dismiss="modal" aria-label="Cerrar">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
            <div class="modal-body">
                <div class="form-row align-items-end mb-2">
                    <div class="col-md-4 form-group mb-2">
                        <label class="small font-weight-bold" for="filtroModalTexto">Buscar</label>
                        <div class="input-group input-group-sm">
                            <div class="input-group-prepend">
                                <span class="input-group-text"><i class="bi bi-search"></i></span>
                            </div>
                            <input type="text" class="form-control" id="filtroModalTexto"
                                   placeholder="Código, apellidos, nombres o doc." autocomplete="off">
                        </div>
                    </div>
                    <div class="col-md-3 form-group mb-2">
                        <label class="small font-weight-bold" for="filtroModalDepartamento">Departamento</label>
                        <select class="custom-select custom-select-sm" id="filtroModalDepartamento">
                            <option value="">Todos</option>
                            <c:forEach var="dep" items="${listaDepartamentos}">
                                <option value="<c:out value='${dep.codigoDepartamento}'/>"><c:out value="${dep.codigoDepartamento} - ${dep.nombre}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-3 form-group mb-2">
                        <label class="small font-weight-bold" for="filtroModalCargo">Cargo</label>
                        <select class="custom-select custom-select-sm" id="filtroModalCargo">
                            <option value="">Todos</option>
                            <c:forEach var="car" items="${listaCargos}">
                                <option value="<c:out value='${car.codigoCargo}'/>"><c:out value="${car.codigoCargo} - ${car.nombre}"/></option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-2 form-group mb-2">
                        <label class="small font-weight-bold" for="filtroModalEstado">Estado</label>
                        <select class="custom-select custom-select-sm" id="filtroModalEstado">
                            <option value="">Todos</option>
                            <option value="true">Activos</option>
                            <option value="false">Inactivos</option>
                        </select>
                    </div>
                </div>

                <div class="d-flex flex-wrap justify-content-between align-items-center mb-2">
                    <span class="ayuda mb-1 mb-sm-0" id="contadorModalResultados">0 empleado(s) encontrado(s)</span>
                    <div class="d-flex align-items-center">
                        <label class="small text-muted mb-0 mr-2" for="tamanoPaginaModal">Por página:</label>
                        <select class="custom-select custom-select-sm mr-2" id="tamanoPaginaModal" style="width: auto;">
                            <option value="5" selected>5</option>
                            <option value="10">10</option>
                            <option value="20">20</option>
                        </select>
                        <button type="button" class="btn btn-light border btn-sm" id="btnLimpiarFiltrosModal">
                            <i class="bi bi-eraser mr-1"></i>Limpiar filtros
                        </button>
                    </div>
                </div>

                <div class="table-responsive" style="max-height: 360px; overflow-y: auto;">
                    <table class="table table-sm table-hover tabla-sigh mb-0" id="tablaModalEmpleados">
                        <thead>
                        <tr>
                            <th>Código</th>
                            <th>Empleado</th>
                            <th>Departamento</th>
                            <th>Cargo</th>
                            <th>Estado</th>
                            <th>Acad./Fam.</th>
                            <th class="text-right">Acción</th>
                        </tr>
                        </thead>
                        <tbody id="cuerpoModalEmpleados">
                        <tr>
                            <td colspan="7" class="text-center text-muted py-3">Cargando empleados...</td>
                        </tr>
                        </tbody>
                    </table>
                </div>

                <div class="paginacion-modal-barra d-flex flex-wrap justify-content-between align-items-center">
                    <span class="small text-muted mb-2 mb-sm-0" id="infoPaginaModal">Página 1 de 1</span>
                    <nav aria-label="Paginación de empleados">
                        <ul class="pagination pagination-sm paginacion-sigh mb-0" id="paginacionModal"></ul>
                    </nav>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-light border btn-sm" data-dismiss="modal">Cerrar</button>
            </div>
        </div>
    </div>
</div>

<script src="${ctx}/lib/bootstrap/js/bootstrap.bundle.min.js"></script>
<script src="${ctx}/lib/sweetalert2/sweetalert2.all.min.js"></script>
<script src="${ctx}/js/empleados.js"></script>
</body>
</html>
