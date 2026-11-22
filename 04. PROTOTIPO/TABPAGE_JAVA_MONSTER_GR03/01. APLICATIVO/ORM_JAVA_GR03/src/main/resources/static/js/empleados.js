(() => {
    "use strict";

    const ctx = document.body.dataset.ctx || "";
    const API = ctx + "/api/empleados";
    const MAX_FOTO_BYTES = 2 * 1024 * 1024;
    const EXTENSIONES_FOTO = new Set(["png", "jpg", "jpeg", "webp"]);
    const TIPOS_SANGRE_VALIDOS = new Set(["O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-"]);

    // Cabecera fija
    const formEmpleado = document.getElementById("formEmpleado");
    const codigoPersona = document.getElementById("codigoPersona");
    const codigoExterno = document.getElementById("codigoExterno");
    const apellidos = document.getElementById("apellidos");
    const nombres = document.getElementById("nombres");
    const btnSugerirCodigo = document.getElementById("btnSugerirCodigo");
    const badgeModoEmpleado = document.getElementById("badgeModoEmpleado");
    const badgeTieneUsuario = document.getElementById("badgeTieneUsuario");

    // Foto
    const previewFoto = document.getElementById("previewFoto");
    const avatarIniciales = document.getElementById("avatarIniciales");
    const inputFoto = document.getElementById("inputFoto");
    const btnSeleccionarFoto = document.getElementById("btnSeleccionarFoto");
    const btnQuitarFoto = document.getElementById("btnQuitarFoto");
    const estadoFoto = document.getElementById("estadoFoto");

    // Pestaña 1: Datos Generales
    const cbxSexo = document.getElementById("cbxSexo");
    const cbxEstadoCivil = document.getElementById("cbxEstadoCivil");
    const cbxSangre = document.getElementById("cbxSangre");
    const peso = document.getElementById("peso");
    const estatura = document.getElementById("estatura");
    const fechaNacimiento = document.getElementById("fechaNacimiento");
    const activo = document.getElementById("activo");
    const codigoDepartamento = document.getElementById("codigoDepartamento");
    const codigoCargo = document.getElementById("codigoCargo");
    const funcion = document.getElementById("funcion");
    const tipoDocumento = document.getElementById("tipoDocumento");
    const numeroDocumento = document.getElementById("numeroDocumento");
    const telefono = document.getElementById("telefono");
    const correo = document.getElementById("correo");
    const habilitacion = document.getElementById("habilitacion");

    // Pestaña 2: Datos Académicos
    const badgeAcademicos = document.getElementById("badgeAcademicos");
    const tituloSubformAcademico = document.getElementById("tituloSubformAcademico");
    const acaNivel = document.getElementById("acaNivel");
    const acaInstitucion = document.getElementById("acaInstitucion");
    const acaTitulo = document.getElementById("acaTitulo");
    const btnGuardarAcademico = document.getElementById("btnGuardarAcademico");
    const btnCancelarAcademico = document.getElementById("btnCancelarAcademico");
    const cuerpoAcademicos = document.getElementById("cuerpoAcademicos");

    // Pestaña 3: Datos Familiares
    const badgeFamiliares = document.getElementById("badgeFamiliares");
    const tituloSubformFamiliar = document.getElementById("tituloSubformFamiliar");
    const famParentesco = document.getElementById("famParentesco");
    const famApellidos = document.getElementById("famApellidos");
    const famNombres = document.getElementById("famNombres");
    const famFechaNacimiento = document.getElementById("famFechaNacimiento");
    const btnGuardarFamiliar = document.getElementById("btnGuardarFamiliar");
    const btnCancelarFamiliar = document.getElementById("btnCancelarFamiliar");
    const cuerpoFamiliares = document.getElementById("cuerpoFamiliares");

    // Barra inferior de 5 acciones
    const btnNuevoEmpleado = document.getElementById("btnNuevoEmpleado");
    const btnListarEmpleados = document.getElementById("btnListarEmpleados");
    const btnGuardarEmpleado = document.getElementById("btnGuardarEmpleado");
    const textoBtnGuardar = document.getElementById("textoBtnGuardar");
    const btnBorrarEmpleado = document.getElementById("btnBorrarEmpleado");
    const btnImprimirEmpleado = document.getElementById("btnImprimirEmpleado");

    // Modal Listar con paginación
    const modalListar = document.getElementById("modalListarEmpleados");
    const filtroModalTexto = document.getElementById("filtroModalTexto");
    const filtroModalDepartamento = document.getElementById("filtroModalDepartamento");
    const filtroModalCargo = document.getElementById("filtroModalCargo");
    const filtroModalEstado = document.getElementById("filtroModalEstado");
    const tamanoPaginaModal = document.getElementById("tamanoPaginaModal");
    const btnLimpiarFiltrosModal = document.getElementById("btnLimpiarFiltrosModal");
    const contadorModalResultados = document.getElementById("contadorModalResultados");
    const cuerpoModalEmpleados = document.getElementById("cuerpoModalEmpleados");
    const infoPaginaModal = document.getElementById("infoPaginaModal");
    const paginacionModal = document.getElementById("paginacionModal");

    // Estado interno
    let listaResumen = [];
    let listaModalFiltrada = [];
    let paginaModalActual = 1;
    let modoNuevo = false;
    let empleadoActual = null;
    let academicos = [];
    let familiares = [];
    let indiceEditAcademico = -1;
    let indiceEditFamiliar = -1;
    let archivoFotoPendiente = null;
    let urlFotoTemporal = null;
    let quitarFotoPendiente = false;
    let modalBackdrop = null;
    let modalAbierto = false;
    let debounceModal = null;

    // ------------------------------------------------------------------
    // Controlador Vanilla JS de pestañas y modal Bootstrap 4 (sin jQuery)
    // ------------------------------------------------------------------
    function activarPestana(selectorPane) {
        const enlaces = document.querySelectorAll('#tabEmpleados [data-toggle="tab"]');
        const paneles = document.querySelectorAll("#tabEmpleadosContent .tab-pane");
        enlaces.forEach(enlace => {
            const activoEnlace = enlace.getAttribute("href") === selectorPane;
            enlace.classList.toggle("active", activoEnlace);
            enlace.setAttribute("aria-selected", activoEnlace ? "true" : "false");
        });
        paneles.forEach(panel => {
            const activoPanel = ("#" + panel.id) === selectorPane;
            panel.classList.toggle("active", activoPanel);
            panel.classList.toggle("show", activoPanel);
        });
    }

    document.querySelectorAll('#tabEmpleados [data-toggle="tab"]').forEach(enlace => {
        enlace.addEventListener("click", evento => {
            evento.preventDefault();
            const destino = enlace.getAttribute("href");
            if (destino) {
                activarPestana(destino);
            }
        });
    });

    function abrirModalListar() {
        if (modalAbierto) {
            return;
        }
        modalAbierto = true;
        modalBackdrop = document.createElement("div");
        modalBackdrop.className = "modal-backdrop fade show";
        document.body.appendChild(modalBackdrop);
        document.body.classList.add("modal-open");
        modalListar.style.display = "block";
        modalListar.removeAttribute("aria-hidden");
        modalListar.classList.add("show");
        cargarModalEmpleados();
        filtroModalTexto.focus();
    }

    function cerrarModalListar() {
        if (!modalAbierto) {
            return;
        }
        modalAbierto = false;
        modalListar.classList.remove("show");
        modalListar.style.display = "none";
        modalListar.setAttribute("aria-hidden", "true");
        document.body.classList.remove("modal-open");
        if (modalBackdrop && modalBackdrop.parentNode) {
            modalBackdrop.parentNode.removeChild(modalBackdrop);
        }
        modalBackdrop = null;
    }

    modalListar.querySelectorAll('[data-dismiss="modal"]').forEach(btn => {
        btn.addEventListener("click", cerrarModalListar);
    });

    modalListar.addEventListener("click", evento => {
        if (evento.target === modalListar) {
            cerrarModalListar();
        }
    });

    document.addEventListener("keydown", evento => {
        if (evento.key === "Escape" && modalAbierto) {
            cerrarModalListar();
        }
    });

    // ------------------------------------------------------------------
    // Utilidades de formato, saneamiento y mensajes
    // ------------------------------------------------------------------
    function normalizarCodigo(valor, maxLen) {
        return (valor || "").toUpperCase().replace(/[^A-Z0-9]/g, "").slice(0, maxLen);
    }

    function colapsarEspacios(valor) {
        return (valor || "").trim().replace(/\s+/g, " ");
    }

    function valorONull(valor) {
        const limpio = colapsarEspacios(valor);
        return limpio === "" ? null : limpio;
    }

    function hoyIso() {
        const ahora = new Date();
        const y = ahora.getFullYear();
        const m = String(ahora.getMonth() + 1).padStart(2, "0");
        const d = String(ahora.getDate()).padStart(2, "0");
        return y + "-" + m + "-" + d;
    }

    function construirUrlFoto(rutaFoto) {
        if (!rutaFoto) {
            return null;
        }
        if (rutaFoto.startsWith("http://") || rutaFoto.startsWith("https://") || rutaFoto.startsWith("blob:")) {
            return rutaFoto;
        }
        if (rutaFoto.startsWith("/")) {
            return ctx + rutaFoto;
        }
        return ctx + "/" + rutaFoto;
    }

    function calcularIniciales(nom, ape) {
        const n = colapsarEspacios(nom);
        const a = colapsarEspacios(ape);
        const i1 = n ? n.charAt(0).toUpperCase() : "";
        const i2 = a ? a.charAt(0).toUpperCase() : "";
        return (i1 + i2) || "EM";
    }

    function liberarUrlTemporal() {
        if (urlFotoTemporal) {
            URL.revokeObjectURL(urlFotoTemporal);
            urlFotoTemporal = null;
        }
    }

    function actualizarVistaFoto() {
        avatarIniciales.textContent = calcularIniciales(nombres.value, apellidos.value);

        if (archivoFotoPendiente && urlFotoTemporal) {
            previewFoto.src = urlFotoTemporal;
            previewFoto.classList.remove("d-none");
            avatarIniciales.classList.add("d-none");
            btnQuitarFoto.disabled = false;
            estadoFoto.textContent = "Foto lista para subir al guardar";
            return;
        }

        const fotoGuardada = (!modoNuevo && !quitarFotoPendiente && empleadoActual) ? empleadoActual.foto : null;
        if (fotoGuardada) {
            previewFoto.src = construirUrlFoto(fotoGuardada);
            previewFoto.classList.remove("d-none");
            avatarIniciales.classList.add("d-none");
            btnQuitarFoto.disabled = false;
            estadoFoto.textContent = "PNG, JPG o WEBP · Máx. 2 MB";
            return;
        }

        previewFoto.removeAttribute("src");
        previewFoto.classList.add("d-none");
        avatarIniciales.classList.remove("d-none");
        btnQuitarFoto.disabled = true;
        estadoFoto.textContent = quitarFotoPendiente
            ? "La foto se quitará al actualizar"
            : "PNG, JPG o WEBP · Máx. 2 MB";
    }

    previewFoto.addEventListener("error", () => {
        previewFoto.classList.add("d-none");
        avatarIniciales.classList.remove("d-none");
    });

    function redirigirLogin() {
        window.location.href = ctx + "/login?expirada";
    }

    function mostrarError(texto) {
        Swal.fire({
            icon: "error",
            titleText: "Operación no completada",
            text: texto,
            confirmButtonColor: "#127c78"
        });
    }

    function mostrarExito(titulo, texto) {
        Swal.fire({
            icon: "success",
            titleText: titulo,
            text: texto || "",
            confirmButtonColor: "#127c78"
        });
    }

    async function leerMensaje(respuesta, defecto) {
        if (respuesta.status === 401) {
            redirigirLogin();
            return "Sesión expirada.";
        }
        if (respuesta.status === 403) {
            return "Su perfil no tiene permisos para realizar esta operación.";
        }
        try {
            const datos = await respuesta.json();
            return datos && datos.mensaje ? datos.mensaje : defecto;
        } catch (e) {
            return defecto;
        }
    }

    function textoOpcionSeleccionada(selectEl) {
        if (!selectEl || selectEl.selectedIndex < 0) {
            return "-";
        }
        const opt = selectEl.options[selectEl.selectedIndex];
        return opt && opt.value ? opt.textContent : "-";
    }

    // ------------------------------------------------------------------
    // Estado del botón Guardar / Actualizar y modo
    // ------------------------------------------------------------------
    function actualizarBotonGuardar() {
        const icono = btnGuardarEmpleado.querySelector("i");
        if (modoNuevo) {
            textoBtnGuardar.textContent = "Guardar";
            if (icono) {
                icono.className = "bi bi-save mr-1";
            }
            badgeModoEmpleado.textContent = "Nuevo registro";
            btnBorrarEmpleado.disabled = true;
            badgeTieneUsuario.classList.add("d-none");
        } else {
            textoBtnGuardar.textContent = "Actualizar";
            if (icono) {
                icono.className = "bi bi-arrow-repeat mr-1";
            }
            badgeModoEmpleado.textContent = empleadoActual
                ? "Editando " + empleadoActual.codigoPersona
                : "Consultando registro";
            btnBorrarEmpleado.disabled = !empleadoActual;
            if (empleadoActual && empleadoActual.tieneUsuario) {
                badgeTieneUsuario.classList.remove("d-none");
            } else {
                badgeTieneUsuario.classList.add("d-none");
            }
        }
    }

    // ------------------------------------------------------------------
    // Grilla Pestaña 2: Datos Académicos (0 innerHTML con datos usuario)
    // ------------------------------------------------------------------
    function limpiarSubformAcademico() {
        indiceEditAcademico = -1;
        acaNivel.value = "";
        acaInstitucion.value = "";
        acaTitulo.value = "";
        tituloSubformAcademico.textContent = "Agregar formación académica";
        btnGuardarAcademico.textContent = "";
        const icono = document.createElement("i");
        icono.className = "bi bi-plus-lg mr-1";
        btnGuardarAcademico.appendChild(icono);
        btnGuardarAcademico.appendChild(document.createTextNode("Agregar"));
        btnCancelarAcademico.classList.add("d-none");
    }

    function renderizarAcademicos() {
        badgeAcademicos.textContent = String(academicos.length);
        cuerpoAcademicos.textContent = "";

        if (academicos.length === 0) {
            const tr = document.createElement("tr");
            const td = document.createElement("td");
            td.colSpan = 4;
            td.className = "text-center text-muted py-3";
            td.textContent = "Sin registros académicos.";
            tr.appendChild(td);
            cuerpoAcademicos.appendChild(tr);
            return;
        }

        academicos.forEach((item, idx) => {
            const tr = document.createElement("tr");
            if (idx === indiceEditAcademico) {
                tr.className = "fila-en-edicion";
            }

            const tdNivel = document.createElement("td");
            tdNivel.textContent = item.nivel || "-";

            const tdInst = document.createElement("td");
            tdInst.textContent = item.institucion || "-";

            const tdTit = document.createElement("td");
            tdTit.textContent = item.titulo || "-";

            const tdAcc = document.createElement("td");
            tdAcc.className = "text-right text-nowrap";

            const btnEd = document.createElement("button");
            btnEd.type = "button";
            btnEd.className = "btn btn-icono btn-editar mr-1";
            btnEd.title = "Editar fila";
            const icoEd = document.createElement("i");
            icoEd.className = "bi bi-pencil-square";
            btnEd.appendChild(icoEd);
            btnEd.addEventListener("click", () => editarFilaAcademico(idx));

            const btnDel = document.createElement("button");
            btnDel.type = "button";
            btnDel.className = "btn btn-icono btn-eliminar";
            btnDel.title = "Quitar fila";
            const icoDel = document.createElement("i");
            icoDel.className = "bi bi-trash3";
            btnDel.appendChild(icoDel);
            btnDel.addEventListener("click", () => quitarFilaAcademico(idx));

            tdAcc.appendChild(btnEd);
            tdAcc.appendChild(btnDel);

            tr.appendChild(tdNivel);
            tr.appendChild(tdInst);
            tr.appendChild(tdTit);
            tr.appendChild(tdAcc);
            cuerpoAcademicos.appendChild(tr);
        });
    }

    function editarFilaAcademico(idx) {
        const item = academicos[idx];
        if (!item) {
            return;
        }
        indiceEditAcademico = idx;
        asegurarOpcionEnSelect(acaNivel, item.nivel);
        acaNivel.value = item.nivel || "";
        acaInstitucion.value = item.institucion || "";
        acaTitulo.value = item.titulo || "";
        tituloSubformAcademico.textContent = "Editando formación académica #" + (idx + 1);
        btnGuardarAcademico.textContent = "";
        const icono = document.createElement("i");
        icono.className = "bi bi-check-lg mr-1";
        btnGuardarAcademico.appendChild(icono);
        btnGuardarAcademico.appendChild(document.createTextNode("Aplicar"));
        btnCancelarAcademico.classList.remove("d-none");
        renderizarAcademicos();
        acaInstitucion.focus();
    }

    function quitarFilaAcademico(idx) {
        academicos.splice(idx, 1);
        if (indiceEditAcademico === idx) {
            limpiarSubformAcademico();
        } else if (indiceEditAcademico > idx) {
            indiceEditAcademico -= 1;
        }
        renderizarAcademicos();
    }

    function guardarFilaAcademico() {
        const nivel = colapsarEspacios(acaNivel.value);
        const institucion = colapsarEspacios(acaInstitucion.value);
        const titulo = colapsarEspacios(acaTitulo.value);

        if (!nivel) {
            mostrarError("Seleccione el nivel académico.");
            acaNivel.focus();
            return;
        }
        if (institucion.length < 2 || institucion.length > 150) {
            mostrarError("La institución académica debe tener entre 2 y 150 caracteres.");
            acaInstitucion.focus();
            return;
        }
        if (titulo.length < 2 || titulo.length > 150) {
            mostrarError("El título obtenido debe tener entre 2 y 150 caracteres.");
            acaTitulo.focus();
            return;
        }

        const registro = {
            codigoAcademico: indiceEditAcademico >= 0 ? academicos[indiceEditAcademico].codigoAcademico : null,
            nivel,
            institucion,
            titulo
        };

        if (indiceEditAcademico >= 0) {
            academicos[indiceEditAcademico] = registro;
        } else {
            academicos.push(registro);
        }
        limpiarSubformAcademico();
        renderizarAcademicos();
    }

    // ------------------------------------------------------------------
    // Grilla Pestaña 3: Datos Familiares (0 innerHTML con datos usuario)
    // ------------------------------------------------------------------
    function limpiarSubformFamiliar() {
        indiceEditFamiliar = -1;
        famParentesco.value = "";
        famApellidos.value = "";
        famNombres.value = "";
        famFechaNacimiento.value = "";
        tituloSubformFamiliar.textContent = "Agregar registro familiar";
        btnGuardarFamiliar.textContent = "";
        const icono = document.createElement("i");
        icono.className = "bi bi-plus-lg mr-1";
        btnGuardarFamiliar.appendChild(icono);
        btnGuardarFamiliar.appendChild(document.createTextNode("Agregar"));
        btnCancelarFamiliar.classList.add("d-none");
    }

    function renderizarFamiliares() {
        badgeFamiliares.textContent = String(familiares.length);
        cuerpoFamiliares.textContent = "";

        if (familiares.length === 0) {
            const tr = document.createElement("tr");
            const td = document.createElement("td");
            td.colSpan = 5;
            td.className = "text-center text-muted py-3";
            td.textContent = "Sin registros familiares.";
            tr.appendChild(td);
            cuerpoFamiliares.appendChild(tr);
            return;
        }

        familiares.forEach((item, idx) => {
            const tr = document.createElement("tr");
            if (idx === indiceEditFamiliar) {
                tr.className = "fila-en-edicion";
            }

            const tdPar = document.createElement("td");
            tdPar.textContent = item.parentesco || "-";

            const tdApe = document.createElement("td");
            tdApe.textContent = item.apellidos || "-";

            const tdNom = document.createElement("td");
            tdNom.textContent = item.nombres || "-";

            const tdFec = document.createElement("td");
            tdFec.textContent = item.fechaNacimiento || "-";

            const tdAcc = document.createElement("td");
            tdAcc.className = "text-right text-nowrap";

            const btnEd = document.createElement("button");
            btnEd.type = "button";
            btnEd.className = "btn btn-icono btn-editar mr-1";
            btnEd.title = "Editar fila";
            const icoEd = document.createElement("i");
            icoEd.className = "bi bi-pencil-square";
            btnEd.appendChild(icoEd);
            btnEd.addEventListener("click", () => editarFilaFamiliar(idx));

            const btnDel = document.createElement("button");
            btnDel.type = "button";
            btnDel.className = "btn btn-icono btn-eliminar";
            btnDel.title = "Quitar fila";
            const icoDel = document.createElement("i");
            icoDel.className = "bi bi-trash3";
            btnDel.appendChild(icoDel);
            btnDel.addEventListener("click", () => quitarFilaFamiliar(idx));

            tdAcc.appendChild(btnEd);
            tdAcc.appendChild(btnDel);

            tr.appendChild(tdPar);
            tr.appendChild(tdApe);
            tr.appendChild(tdNom);
            tr.appendChild(tdFec);
            tr.appendChild(tdAcc);
            cuerpoFamiliares.appendChild(tr);
        });
    }

    function editarFilaFamiliar(idx) {
        const item = familiares[idx];
        if (!item) {
            return;
        }
        indiceEditFamiliar = idx;
        asegurarOpcionEnSelect(famParentesco, item.parentesco);
        famParentesco.value = item.parentesco || "";
        famApellidos.value = item.apellidos || "";
        famNombres.value = item.nombres || "";
        famFechaNacimiento.value = item.fechaNacimiento || "";
        tituloSubformFamiliar.textContent = "Editando registro familiar #" + (idx + 1);
        btnGuardarFamiliar.textContent = "";
        const icono = document.createElement("i");
        icono.className = "bi bi-check-lg mr-1";
        btnGuardarFamiliar.appendChild(icono);
        btnGuardarFamiliar.appendChild(document.createTextNode("Aplicar"));
        btnCancelarFamiliar.classList.remove("d-none");
        renderizarFamiliares();
        famApellidos.focus();
    }

    function quitarFilaFamiliar(idx) {
        familiares.splice(idx, 1);
        if (indiceEditFamiliar === idx) {
            limpiarSubformFamiliar();
        } else if (indiceEditFamiliar > idx) {
            indiceEditFamiliar -= 1;
        }
        renderizarFamiliares();
    }

    function guardarFilaFamiliar() {
        const parentesco = colapsarEspacios(famParentesco.value);
        const ape = colapsarEspacios(famApellidos.value);
        const nom = colapsarEspacios(famNombres.value);
        const fecNac = famFechaNacimiento.value ? famFechaNacimiento.value.trim() : null;

        if (!parentesco) {
            mostrarError("Seleccione el parentesco del familiar.");
            famParentesco.focus();
            return;
        }
        if (ape.length < 2 || ape.length > 150 || /[0-9]/.test(ape)) {
            mostrarError("Los apellidos del familiar deben tener entre 2 y 150 caracteres y no contener números.");
            famApellidos.focus();
            return;
        }
        if (nom.length < 2 || nom.length > 150 || /[0-9]/.test(nom)) {
            mostrarError("Los nombres del familiar deben tener entre 2 y 150 caracteres y no contener números.");
            famNombres.focus();
            return;
        }
        if (fecNac && fecNac > hoyIso()) {
            mostrarError("La fecha de nacimiento del familiar no puede ser futura.");
            famFechaNacimiento.focus();
            return;
        }

        const registro = {
            codigoFamiliar: indiceEditFamiliar >= 0 ? familiares[indiceEditFamiliar].codigoFamiliar : null,
            parentesco,
            apellidos: ape,
            nombres: nom,
            fechaNacimiento: fecNac
        };

        if (indiceEditFamiliar >= 0) {
            familiares[indiceEditFamiliar] = registro;
        } else {
            familiares.push(registro);
        }
        limpiarSubformFamiliar();
        renderizarFamiliares();
    }

    function asegurarOpcionEnSelect(selectEl, valor) {
        if (!valor) {
            return;
        }
        const existe = Array.from(selectEl.options).some(opt => opt.value === valor);
        if (!existe) {
            const opt = document.createElement("option");
            opt.value = valor;
            opt.textContent = valor;
            selectEl.appendChild(opt);
        }
    }

    // ------------------------------------------------------------------
    // Carga y limpieza de ficha de empleado
    // ------------------------------------------------------------------
    function poblarFormulario(detalle) {
        modoNuevo = false;
        empleadoActual = detalle;
        liberarUrlTemporal();
        archivoFotoPendiente = null;
        quitarFotoPendiente = false;
        inputFoto.value = "";

        codigoPersona.value = detalle.codigoPersona || "";
        codigoPersona.readOnly = true;
        btnSugerirCodigo.disabled = true;
        codigoExterno.value = detalle.codigoExterno || "";
        apellidos.value = detalle.apellidos || "";
        nombres.value = detalle.nombres || "";

        cbxSexo.value = detalle.codigoGenero || "";
        cbxEstadoCivil.value = detalle.codigoEstadoCivil || "";
        cbxSangre.value = detalle.tipoSangre || "";
        peso.value = detalle.peso !== null && detalle.peso !== undefined ? String(detalle.peso) : "";
        estatura.value = detalle.estatura !== null && detalle.estatura !== undefined ? String(detalle.estatura) : "";
        fechaNacimiento.value = detalle.fechaNacimiento || "";
        activo.checked = detalle.activo !== false;

        codigoDepartamento.value = detalle.codigoDepartamento || "";
        codigoCargo.value = detalle.codigoCargo || "";
        funcion.value = detalle.funcion || "";
        tipoDocumento.value = detalle.tipoDocumento || "";
        numeroDocumento.value = detalle.numeroDocumento || "";
        telefono.value = detalle.telefono || "";
        correo.value = detalle.correo || "";
        habilitacion.value = detalle.habilitacion || "";

        academicos = Array.isArray(detalle.academicos)
            ? detalle.academicos.map(a => ({
                codigoAcademico: a.codigoAcademico ?? null,
                nivel: a.nivel || "",
                institucion: a.institucion || "",
                titulo: a.titulo || ""
            }))
            : [];
        familiares = Array.isArray(detalle.familiares)
            ? detalle.familiares.map(f => ({
                codigoFamiliar: f.codigoFamiliar ?? null,
                parentesco: f.parentesco || "",
                apellidos: f.apellidos || "",
                nombres: f.nombres || "",
                fechaNacimiento: f.fechaNacimiento || null
            }))
            : [];

        limpiarSubformAcademico();
        limpiarSubformFamiliar();
        renderizarAcademicos();
        renderizarFamiliares();
        actualizarVistaFoto();
        actualizarBotonGuardar();
    }

    async function prepararNuevoRegistro() {
        modoNuevo = true;
        empleadoActual = null;
        liberarUrlTemporal();
        archivoFotoPendiente = null;
        quitarFotoPendiente = false;
        inputFoto.value = "";

        formEmpleado.reset();
        codigoPersona.readOnly = false;
        btnSugerirCodigo.disabled = false;
        activo.checked = true;
        academicos = [];
        familiares = [];

        limpiarSubformAcademico();
        limpiarSubformFamiliar();
        renderizarAcademicos();
        renderizarFamiliares();
        actualizarVistaFoto();
        actualizarBotonGuardar();
        activarPestana("#paneGenerales");

        await sugerirSiguienteCodigo();
        apellidos.focus();
    }

    async function sugerirSiguienteCodigo() {
        try {
            const respuesta = await fetch(API + "/siguiente-codigo");
            if (respuesta.status === 401) {
                redirigirLogin();
                return;
            }
            if (!respuesta.ok) {
                mostrarError(await leerMensaje(respuesta, "No se pudo sugerir el siguiente código."));
                return;
            }
            const datos = await respuesta.json();
            if (datos && datos.codigo) {
                codigoPersona.value = datos.codigo;
            }
        } catch (e) {
            mostrarError("No fue posible consultar el siguiente código disponible.");
        }
    }

    async function cargarDetallePorCodigo(codigo) {
        try {
            const respuesta = await fetch(API + "/" + encodeURIComponent(codigo) + "/detalle");
            if (respuesta.status === 401) {
                redirigirLogin();
                return false;
            }
            if (!respuesta.ok) {
                mostrarError(await leerMensaje(respuesta, "No se pudo cargar la ficha del empleado."));
                return false;
            }
            const detalle = await respuesta.json();
            poblarFormulario(detalle);
            return true;
        } catch (e) {
            mostrarError("Error de conexión al cargar la ficha del empleado.");
            return false;
        }
    }

    async function cargarListaYNavegar(codigoPreferido) {
        try {
            const respuesta = await fetch(API + "/resumen");
            if (respuesta.status === 401) {
                redirigirLogin();
                return;
            }
            if (!respuesta.ok) {
                mostrarError(await leerMensaje(respuesta, "No se pudo consultar la lista de empleados."));
                return;
            }
            const datos = await respuesta.json();
            listaResumen = Array.isArray(datos) ? datos : [];

            if (listaResumen.length === 0) {
                await prepararNuevoRegistro();
                return;
            }

            let codigoDestino = listaResumen[0].codigoPersona;
            if (codigoPreferido && listaResumen.some(e => e.codigoPersona === codigoPreferido)) {
                codigoDestino = codigoPreferido;
            }

            await cargarDetallePorCodigo(codigoDestino);
        } catch (e) {
            mostrarError("No fue posible comunicarse con el servidor para cargar los empleados.");
        }
    }

    // ------------------------------------------------------------------
    // Validación y persistencia atómica (POST /ficha o PUT /{codigo}/ficha)
    // ------------------------------------------------------------------
    function construirPayloadYValidar() {
        const cod = normalizarCodigo(codigoPersona.value, 6);
        const codExt = normalizarCodigo(codigoExterno.value, 10);
        const ape = colapsarEspacios(apellidos.value);
        const nom = colapsarEspacios(nombres.value);
        const func = colapsarEspacios(funcion.value);
        const hab = valorONull(habilitacion.value);
        const tipDoc = valorONull(tipoDocumento.value);
        const numDoc = valorONull(numeroDocumento.value);
        const fecNac = fechaNacimiento.value ? fechaNacimiento.value.trim() : null;
        const tel = valorONull(telefono.value);
        const email = valorONull(correo.value);
        const gen = valorONull(cbxSexo.value);
        const estCiv = valorONull(cbxEstadoCivil.value);
        const sangre = valorONull(cbxSangre.value);
        const pesoTexto = peso.value.trim();
        const estaturaTexto = estatura.value.trim();
        const dep = valorONull(codigoDepartamento.value);
        const car = valorONull(codigoCargo.value);

        codigoPersona.value = cod;
        codigoExterno.value = codExt;
        apellidos.value = ape;
        nombres.value = nom;
        funcion.value = func;

        if (!/^[A-Z0-9]{1,6}$/.test(cod)) {
            return {error: "El código del empleado es obligatorio (1 a 6 caracteres alfanuméricos, ej. P00007)."};
        }
        if (ape.length < 2 || ape.length > 150 || /[0-9]/.test(ape)) {
            return {error: "Los apellidos deben tener entre 2 y 150 caracteres y no contener números."};
        }
        if (nom.length < 2 || nom.length > 255 || /[0-9]/.test(nom)) {
            return {error: "Los nombres deben tener entre 2 y 255 caracteres y no contener números."};
        }
        if (!dep) {
            return {error: "Seleccione un departamento en la pestaña Datos Generales.", pestana: "#paneGenerales"};
        }
        if (!car) {
            return {error: "Seleccione un cargo en la pestaña Datos Generales.", pestana: "#paneGenerales"};
        }
        if (func.length < 2 || func.length > 255) {
            return {error: "La función del empleado es obligatoria (2 a 255 caracteres).", pestana: "#paneGenerales"};
        }
        if (sangre && !TIPOS_SANGRE_VALIDOS.has(sangre)) {
            return {error: "Seleccione un tipo de sangre válido.", pestana: "#paneGenerales"};
        }

        let pesoNum = null;
        if (pesoTexto !== "") {
            pesoNum = Number(pesoTexto);
            if (Number.isNaN(pesoNum) || pesoNum < 20 || pesoNum > 300) {
                return {error: "El peso debe estar entre 20.00 y 300.00 kg.", pestana: "#paneGenerales"};
            }
        }

        let estaturaNum = null;
        if (estaturaTexto !== "") {
            estaturaNum = Number(estaturaTexto);
            if (Number.isNaN(estaturaNum) || estaturaNum < 0.50 || estaturaNum > 2.50) {
                return {error: "La estatura debe estar entre 0.50 y 2.50 m.", pestana: "#paneGenerales"};
            }
        }

        if (fecNac && fecNac > hoyIso()) {
            return {error: "La fecha de nacimiento no puede ser una fecha futura.", pestana: "#paneGenerales"};
        }
        if (tipDoc === "CEDULA" && numDoc && !/^\d{10}$/.test(numDoc)) {
            return {error: "Para tipo de documento Cédula ingrese exactamente 10 dígitos numéricos.", pestana: "#paneGenerales"};
        }
        if (tel && !/^[0-9+\-\s]{7,20}$/.test(tel)) {
            return {error: "El teléfono debe contener entre 7 y 20 caracteres numéricos válidos.", pestana: "#paneGenerales"};
        }
        if (email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
            return {error: "Ingrese un correo electrónico válido.", pestana: "#paneGenerales"};
        }

        return {
            payload: {
                codigoPersona: cod,
                codigoExterno: codExt || null,
                apellidos: ape,
                nombres: nom,
                funcion: func,
                habilitacion: hab,
                activo: activo.checked,
                tipoDocumento: tipDoc,
                numeroDocumento: numDoc,
                fechaNacimiento: fecNac,
                telefono: tel,
                correo: email,
                codigoGenero: gen,
                codigoEstadoCivil: estCiv,
                tipoSangre: sangre,
                peso: pesoNum,
                estatura: estaturaNum,
                codigoDepartamento: dep,
                codigoCargo: car,
                academicos: academicos.map(a => ({
                    codigoAcademico: a.codigoAcademico,
                    nivel: a.nivel,
                    institucion: a.institucion,
                    titulo: a.titulo
                })),
                familiares: familiares.map(f => ({
                    codigoFamiliar: f.codigoFamiliar,
                    parentesco: f.parentesco,
                    apellidos: f.apellidos,
                    nombres: f.nombres,
                    fechaNacimiento: f.fechaNacimiento
                }))
            }
        };
    }

    async function guardarEmpleado() {
        const resultadoValidacion = construirPayloadYValidar();
        if (resultadoValidacion.error) {
            if (resultadoValidacion.pestana) {
                activarPestana(resultadoValidacion.pestana);
            }
            mostrarError(resultadoValidacion.error);
            return;
        }

        const payload = resultadoValidacion.payload;
        const esCreacion = modoNuevo;
        const url = esCreacion
            ? API + "/ficha"
            : API + "/" + encodeURIComponent(empleadoActual.codigoPersona) + "/ficha";
        const metodo = esCreacion ? "POST" : "PUT";

        btnGuardarEmpleado.disabled = true;
        try {
            const respuesta = await fetch(url, {
                method: metodo,
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(payload)
            });
            if (respuesta.status === 401) {
                redirigirLogin();
                return;
            }
            if (!respuesta.ok) {
                mostrarError(await leerMensaje(respuesta, "No se pudo guardar la ficha del empleado."));
                return;
            }

            const guardado = await respuesta.json();
            const codigoGuardado = guardado && guardado.codigoPersona ? guardado.codigoPersona : payload.codigoPersona;

            let avisoFoto = "";
            if (archivoFotoPendiente) {
                const formData = new FormData();
                formData.append("archivo", archivoFotoPendiente);
                const respFoto = await fetch(API + "/" + encodeURIComponent(codigoGuardado) + "/foto", {
                    method: "POST",
                    body: formData
                });
                if (respFoto.status === 401) {
                    redirigirLogin();
                    return;
                }
                if (!respFoto.ok) {
                    avisoFoto = await leerMensaje(respFoto, "La ficha se guardó, pero la foto no pudo subirse.");
                }
            } else if (quitarFotoPendiente && !esCreacion) {
                const respDelFoto = await fetch(API + "/" + encodeURIComponent(codigoGuardado) + "/foto", {
                    method: "DELETE"
                });
                if (respDelFoto.status === 401) {
                    redirigirLogin();
                    return;
                }
                if (!respDelFoto.ok && respDelFoto.status !== 204) {
                    avisoFoto = await leerMensaje(respDelFoto, "La ficha se guardó, pero no se pudo quitar la foto anterior.");
                }
            }

            await cargarListaYNavegar(codigoGuardado);

            if (avisoFoto) {
                Swal.fire({
                    icon: "warning",
                    titleText: esCreacion ? "Empleado registrado con aviso" : "Empleado actualizado con aviso",
                    text: avisoFoto,
                    confirmButtonColor: "#127c78"
                });
            } else {
                mostrarExito(
                    esCreacion ? "Empleado registrado" : "Empleado actualizado",
                    "Se guardaron correctamente la cabecera, los datos generales, académicos y familiares."
                );
            }
        } catch (e) {
            mostrarError("Error de conexión al guardar la ficha del empleado.");
        } finally {
            btnGuardarEmpleado.disabled = false;
        }
    }

    function borrarEmpleadoActual() {
        if (modoNuevo || !empleadoActual) {
            return;
        }
        const codigo = empleadoActual.codigoPersona;
        const nombreCompleto = (empleadoActual.apellidos + ", " + empleadoActual.nombres).trim();

        Swal.fire({
            icon: "warning",
            titleText: "Eliminar empleado",
            text: "Se eliminará a " + codigo + " - " + nombreCompleto + " junto con sus datos académicos, familiares y foto.",
            showCancelButton: true,
            confirmButtonText: "Eliminar",
            cancelButtonText: "Cancelar",
            confirmButtonColor: "#b54747",
            cancelButtonColor: "#627d98"
        }).then(async resultado => {
            if (!resultado.isConfirmed) {
                return;
            }
            try {
                const respuesta = await fetch(API + "/" + encodeURIComponent(codigo), {
                    method: "DELETE"
                });
                if (respuesta.status === 401) {
                    redirigirLogin();
                    return;
                }
                if (!respuesta.ok) {
                    mostrarError(await leerMensaje(respuesta, "No se pudo eliminar el empleado."));
                    return;
                }
                await cargarListaYNavegar(null);
                mostrarExito("Empleado eliminado", "El registro fue eliminado correctamente.");
            } catch (e) {
                mostrarError("Error de conexión al eliminar el empleado.");
            }
        });
    }

    // ------------------------------------------------------------------
    // Modal Listar con filtros combinables y paginación en cliente
    // ------------------------------------------------------------------
    function construirQueryModal() {
        const params = new URLSearchParams();
        const texto = filtroModalTexto.value.trim();
        if (texto !== "") {
            params.set("q", texto);
        }
        if (filtroModalDepartamento.value !== "") {
            params.set("departamento", filtroModalDepartamento.value);
        }
        if (filtroModalCargo.value !== "") {
            params.set("cargo", filtroModalCargo.value);
        }
        if (filtroModalEstado.value !== "") {
            params.set("activo", filtroModalEstado.value);
        }
        const qs = params.toString();
        return qs ? "?" + qs : "";
    }

    function obtenerTamanoPaginaModal() {
        const val = Number(tamanoPaginaModal ? tamanoPaginaModal.value : 5);
        return Number.isInteger(val) && val > 0 ? val : 5;
    }

    async function cargarModalEmpleados() {
        cuerpoModalEmpleados.textContent = "";
        const trLoad = document.createElement("tr");
        const tdLoad = document.createElement("td");
        tdLoad.colSpan = 7;
        tdLoad.className = "text-center text-muted py-3";
        tdLoad.textContent = "Consultando empleados...";
        trLoad.appendChild(tdLoad);
        cuerpoModalEmpleados.appendChild(trLoad);

        try {
            const respuesta = await fetch(API + "/resumen" + construirQueryModal());
            if (respuesta.status === 401) {
                redirigirLogin();
                return;
            }
            if (!respuesta.ok) {
                mostrarError(await leerMensaje(respuesta, "No se pudo consultar el listado de empleados."));
                return;
            }
            const lista = await respuesta.json();
            listaModalFiltrada = Array.isArray(lista) ? lista : [];
            paginaModalActual = 1;
            renderizarPaginaModal();
        } catch (e) {
            mostrarError("Error de conexión al consultar el listado de empleados.");
        }
    }

    function crearItemPaginacion(etiqueta, titulo, paginaDestino, deshabilitado, activoItem) {
        const li = document.createElement("li");
        li.className = "page-item";
        if (deshabilitado) {
            li.classList.add("disabled");
        }
        if (activoItem) {
            li.classList.add("active");
        }

        const btn = document.createElement("button");
        btn.type = "button";
        btn.className = "page-link";
        btn.textContent = etiqueta;
        btn.title = titulo;
        btn.disabled = Boolean(deshabilitado);
        if (!deshabilitado && !activoItem) {
            btn.addEventListener("click", () => {
                paginaModalActual = paginaDestino;
                renderizarPaginaModal();
            });
        }
        li.appendChild(btn);
        return li;
    }

    function renderizarControlesPaginacion(totalPaginas) {
        paginacionModal.textContent = "";
        infoPaginaModal.textContent = "Página " + paginaModalActual + " de " + totalPaginas;

        const enPrimera = paginaModalActual <= 1;
        const enUltima = paginaModalActual >= totalPaginas;

        paginacionModal.appendChild(crearItemPaginacion("|<", "Primera página", 1, enPrimera, false));
        paginacionModal.appendChild(crearItemPaginacion("<", "Página anterior", paginaModalActual - 1, enPrimera, false));

        const maxBotones = 5;
        let inicioPag = Math.max(1, paginaModalActual - Math.floor(maxBotones / 2));
        let finPag = Math.min(totalPaginas, inicioPag + maxBotones - 1);
        inicioPag = Math.max(1, finPag - maxBotones + 1);

        for (let p = inicioPag; p <= finPag; p++) {
            paginacionModal.appendChild(
                crearItemPaginacion(String(p), "Ir a página " + p, p, false, p === paginaModalActual)
            );
        }

        paginacionModal.appendChild(crearItemPaginacion(">", "Página siguiente", paginaModalActual + 1, enUltima, false));
        paginacionModal.appendChild(crearItemPaginacion(">|", "Última página", totalPaginas, enUltima, false));
    }

    function renderizarPaginaModal() {
        const total = listaModalFiltrada.length;
        const porPagina = obtenerTamanoPaginaModal();
        const totalPaginas = Math.max(1, Math.ceil(total / porPagina));
        if (paginaModalActual > totalPaginas) {
            paginaModalActual = totalPaginas;
        }
        if (paginaModalActual < 1) {
            paginaModalActual = 1;
        }

        cuerpoModalEmpleados.textContent = "";

        if (total === 0) {
            contadorModalResultados.textContent = "0 empleado(s) encontrado(s)";
            const tr = document.createElement("tr");
            const td = document.createElement("td");
            td.colSpan = 7;
            td.className = "text-center text-muted py-3";
            td.textContent = "No hay empleados que coincidan con los filtros.";
            tr.appendChild(td);
            cuerpoModalEmpleados.appendChild(tr);
            renderizarControlesPaginacion(1);
            return;
        }

        const inicio = (paginaModalActual - 1) * porPagina;
        const fin = Math.min(inicio + porPagina, total);
        const paginaItems = listaModalFiltrada.slice(inicio, fin);
        contadorModalResultados.textContent =
            "Mostrando " + (inicio + 1) + "–" + fin + " de " + total + " empleado(s)";

        paginaItems.forEach(emp => {
            const tr = document.createElement("tr");
            tr.className = "fila-modal-seleccionable";
            if (empleadoActual && !modoNuevo && empleadoActual.codigoPersona === emp.codigoPersona) {
                tr.classList.add("fila-en-edicion");
            }

            const tdCod = document.createElement("td");
            const spanCod = document.createElement("span");
            spanCod.className = "codigo";
            spanCod.textContent = emp.codigoPersona || "-";
            tdCod.appendChild(spanCod);

            const tdNom = document.createElement("td");
            const strongNom = document.createElement("strong");
            strongNom.textContent = ((emp.apellidos || "") + ", " + (emp.nombres || "")).trim();
            const smallDoc = document.createElement("small");
            smallDoc.className = "d-block text-muted";
            smallDoc.textContent = emp.numeroDocumento ? "Doc: " + emp.numeroDocumento : (emp.funcion || "");
            tdNom.appendChild(strongNom);
            tdNom.appendChild(smallDoc);

            const tdDep = document.createElement("td");
            tdDep.textContent = emp.departamento || emp.codigoDepartamento || "-";

            const tdCar = document.createElement("td");
            tdCar.textContent = emp.cargo || emp.codigoCargo || "-";

            const tdEst = document.createElement("td");
            const spanEst = document.createElement("span");
            spanEst.className = emp.activo ? "estado-activo" : "estado-inactivo";
            spanEst.textContent = emp.activo ? "Activo" : "Inactivo";
            tdEst.appendChild(spanEst);

            const tdCont = document.createElement("td");
            tdCont.textContent = (emp.totalAcademicos ?? 0) + " / " + (emp.totalFamiliares ?? 0);

            const tdAcc = document.createElement("td");
            tdAcc.className = "text-right";
            const btnSel = document.createElement("button");
            btnSel.type = "button";
            btnSel.className = "btn btn-sigh btn-sm py-1 px-2";
            btnSel.style.height = "auto";
            btnSel.textContent = "Seleccionar";
            btnSel.addEventListener("click", async () => {
                cerrarModalListar();
                await cargarDetallePorCodigo(emp.codigoPersona);
            });
            tdAcc.appendChild(btnSel);

            tr.addEventListener("dblclick", async () => {
                cerrarModalListar();
                await cargarDetallePorCodigo(emp.codigoPersona);
            });

            tr.appendChild(tdCod);
            tr.appendChild(tdNom);
            tr.appendChild(tdDep);
            tr.appendChild(tdCar);
            tr.appendChild(tdEst);
            tr.appendChild(tdCont);
            tr.appendChild(tdAcc);
            cuerpoModalEmpleados.appendChild(tr);
        });

        renderizarControlesPaginacion(totalPaginas);
    }

    // ------------------------------------------------------------------
    // Impresión limpia de ficha (@media print)
    // ------------------------------------------------------------------
    function prepararFichaImprimible() {
        document.getElementById("printCodigo").textContent = codigoPersona.value.trim() || "-";
        document.getElementById("printEstado").textContent = activo.checked ? "Activo" : "Inactivo";
        document.getElementById("printNombreCompleto").textContent =
            (colapsarEspacios(apellidos.value) + ", " + colapsarEspacios(nombres.value)).replace(/^,\s*|,\s*$/g, "") || "Sin nombre";
        document.getElementById("printCargoDepto").textContent =
            textoOpcionSeleccionada(codigoCargo) + " · " + textoOpcionSeleccionada(codigoDepartamento);
        document.getElementById("printFuncion").textContent = funcion.value.trim() || "-";

        document.getElementById("printCodigoExterno").textContent = codigoExterno.value.trim() || "-";
        const docTexto = [tipoDocumento.value, numeroDocumento.value.trim()].filter(Boolean).join(" ");
        document.getElementById("printDocumento").textContent = docTexto || "-";
        document.getElementById("printSexo").textContent = textoOpcionSeleccionada(cbxSexo);
        document.getElementById("printEstadoCivil").textContent = textoOpcionSeleccionada(cbxEstadoCivil);
        document.getElementById("printTipoSangre").textContent = cbxSangre.value || "-";
        document.getElementById("printFechaNacimiento").textContent = fechaNacimiento.value || "-";
        document.getElementById("printPeso").textContent = peso.value ? peso.value + " kg" : "-";
        document.getElementById("printEstatura").textContent = estatura.value ? estatura.value + " m" : "-";
        document.getElementById("printTelefono").textContent = telefono.value.trim() || "-";
        document.getElementById("printCorreo").textContent = correo.value.trim() || "-";
        document.getElementById("printHabilitacion").textContent = habilitacion.value.trim() || "-";

        const printFotoImg = document.getElementById("printFotoImg");
        const printAvatar = document.getElementById("printAvatar");
        printAvatar.textContent = calcularIniciales(nombres.value, apellidos.value);
        if (!previewFoto.classList.contains("d-none") && previewFoto.getAttribute("src")) {
            printFotoImg.src = previewFoto.getAttribute("src");
            printFotoImg.classList.remove("d-none");
            printAvatar.classList.add("d-none");
        } else {
            printFotoImg.removeAttribute("src");
            printFotoImg.classList.add("d-none");
            printAvatar.classList.remove("d-none");
        }

        const printAca = document.getElementById("printCuerpoAcademicos");
        printAca.textContent = "";
        if (academicos.length === 0) {
            const tr = document.createElement("tr");
            const td = document.createElement("td");
            td.colSpan = 3;
            td.textContent = "Sin registros académicos.";
            tr.appendChild(td);
            printAca.appendChild(tr);
        } else {
            academicos.forEach(a => {
                const tr = document.createElement("tr");
                [a.nivel || "-", a.institucion || "-", a.titulo || "-"].forEach(val => {
                    const td = document.createElement("td");
                    td.textContent = val;
                    tr.appendChild(td);
                });
                printAca.appendChild(tr);
            });
        }

        const printFam = document.getElementById("printCuerpoFamiliares");
        printFam.textContent = "";
        if (familiares.length === 0) {
            const tr = document.createElement("tr");
            const td = document.createElement("td");
            td.colSpan = 4;
            td.textContent = "Sin registros familiares.";
            tr.appendChild(td);
            printFam.appendChild(tr);
        } else {
            familiares.forEach(f => {
                const tr = document.createElement("tr");
                [f.parentesco || "-", f.apellidos || "-", f.nombres || "-", f.fechaNacimiento || "-"].forEach(val => {
                    const td = document.createElement("td");
                    td.textContent = val;
                    tr.appendChild(td);
                });
                printFam.appendChild(tr);
            });
        }

        document.getElementById("printFechaEmision").textContent = "Impreso: " + new Date().toLocaleString("es-EC");
    }

    // ------------------------------------------------------------------
    // Eventos de entradas, foto, subformularios, navegador y acciones
    // ------------------------------------------------------------------
    codigoPersona.addEventListener("input", () => {
        codigoPersona.value = normalizarCodigo(codigoPersona.value, 6);
    });
    codigoExterno.addEventListener("input", () => {
        codigoExterno.value = normalizarCodigo(codigoExterno.value, 10);
    });
    apellidos.addEventListener("input", () => {
        apellidos.value = apellidos.value.replace(/[0-9]/g, "");
        actualizarVistaFoto();
    });
    nombres.addEventListener("input", () => {
        nombres.value = nombres.value.replace(/[0-9]/g, "");
        actualizarVistaFoto();
    });
    famApellidos.addEventListener("input", () => {
        famApellidos.value = famApellidos.value.replace(/[0-9]/g, "");
    });
    famNombres.addEventListener("input", () => {
        famNombres.value = famNombres.value.replace(/[0-9]/g, "");
    });

    btnSugerirCodigo.addEventListener("click", async () => {
        if (!modoNuevo) {
            return;
        }
        await sugerirSiguienteCodigo();
    });

    btnSeleccionarFoto.addEventListener("click", () => inputFoto.click());

    inputFoto.addEventListener("change", () => {
        const archivo = inputFoto.files && inputFoto.files[0];
        if (!archivo) {
            return;
        }
        const partes = archivo.name.split(".");
        const ext = partes.length > 1 ? partes[partes.length - 1].toLowerCase() : "";
        if (!EXTENSIONES_FOTO.has(ext)) {
            inputFoto.value = "";
            mostrarError("Formato no permitido. Seleccione una imagen PNG, JPG, JPEG o WEBP.");
            return;
        }
        if (archivo.size === 0 || archivo.size > MAX_FOTO_BYTES) {
            inputFoto.value = "";
            mostrarError("La imagen debe pesar más de 0 bytes y como máximo 2 MB.");
            return;
        }
        liberarUrlTemporal();
        archivoFotoPendiente = archivo;
        quitarFotoPendiente = false;
        urlFotoTemporal = URL.createObjectURL(archivo);
        actualizarVistaFoto();
    });

    btnQuitarFoto.addEventListener("click", () => {
        liberarUrlTemporal();
        inputFoto.value = "";
        if (archivoFotoPendiente) {
            archivoFotoPendiente = null;
        } else if (!modoNuevo && empleadoActual && empleadoActual.foto) {
            quitarFotoPendiente = true;
        }
        actualizarVistaFoto();
    });

    btnGuardarAcademico.addEventListener("click", guardarFilaAcademico);
    btnCancelarAcademico.addEventListener("click", () => {
        limpiarSubformAcademico();
        renderizarAcademicos();
    });

    btnGuardarFamiliar.addEventListener("click", guardarFilaFamiliar);
    btnCancelarFamiliar.addEventListener("click", () => {
        limpiarSubformFamiliar();
        renderizarFamiliares();
    });

    // Barra inferior de 5 acciones
    btnNuevoEmpleado.addEventListener("click", prepararNuevoRegistro);
    btnListarEmpleados.addEventListener("click", abrirModalListar);
    btnGuardarEmpleado.addEventListener("click", guardarEmpleado);
    formEmpleado.addEventListener("submit", evento => {
        evento.preventDefault();
        guardarEmpleado();
    });
    btnBorrarEmpleado.addEventListener("click", borrarEmpleadoActual);
    btnImprimirEmpleado.addEventListener("click", () => {
        prepararFichaImprimible();
        window.print();
    });

    // Filtros y paginación del modal Listar
    filtroModalTexto.addEventListener("input", () => {
        clearTimeout(debounceModal);
        debounceModal = setTimeout(cargarModalEmpleados, 220);
    });
    filtroModalDepartamento.addEventListener("change", cargarModalEmpleados);
    filtroModalCargo.addEventListener("change", cargarModalEmpleados);
    filtroModalEstado.addEventListener("change", cargarModalEmpleados);
    tamanoPaginaModal.addEventListener("change", () => {
        paginaModalActual = 1;
        renderizarPaginaModal();
    });
    btnLimpiarFiltrosModal.addEventListener("click", () => {
        clearTimeout(debounceModal);
        filtroModalTexto.value = "";
        filtroModalDepartamento.value = "";
        filtroModalCargo.value = "";
        filtroModalEstado.value = "";
        tamanoPaginaModal.value = "5";
        paginaModalActual = 1;
        cargarModalEmpleados();
    });

    // Carga inicial
    cargarListaYNavegar(null);
})();
