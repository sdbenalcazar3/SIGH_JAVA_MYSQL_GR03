(() => {
    "use strict";

    const ctx = document.body.dataset.ctx || "";
    const API = ctx + "/api/cargos";

    const filtroTexto = document.getElementById("filtroTexto");
    const filtroEstado = document.getElementById("filtroEstado");
    const filtroDepartamento = document.getElementById("filtroDepartamento");
    const btnLimpiarFiltros = document.getElementById("btnLimpiarFiltros");
    const chkSeleccionMultiple = document.getElementById("chkSeleccionMultiple");
    const lstCargos = document.getElementById("lstCargos");
    const contadorResultados = document.getElementById("contadorResultados");

    const btnNuevoCargo = document.getElementById("btnNuevoCargo");
    const btnSugerirCodigo = document.getElementById("btnSugerirCodigo");
    const btnVerPersonal = document.getElementById("btnVerPersonal");
    const btnEliminarCargo = document.getElementById("btnEliminarCargo");

    const barraLote = document.getElementById("barraLote");
    const contadorLote = document.getElementById("contadorLote");
    const btnActivarLote = document.getElementById("btnActivarLote");
    const btnInactivarLote = document.getElementById("btnInactivarLote");

    const formCargo = document.getElementById("formCargo");
    const codigoCargo = document.getElementById("codigoCargo");
    const nombreCargo = document.getElementById("nombreCargo");
    const activoCargo = document.getElementById("activoCargo");
    const tituloFormCargo = document.getElementById("tituloFormCargo");
    const btnGuardarCargo = document.getElementById("btnGuardarCargo");
    const btnLimpiarForm = document.getElementById("btnLimpiarForm");

    const totalCargos = document.getElementById("totalCargos");
    const totalCargosActivos = document.getElementById("totalCargosActivos");
    const totalPersonalCargos = document.getElementById("totalPersonalCargos");
    const detalleCargo = document.getElementById("detalleCargo");
    const detalleDepartamentos = document.getElementById("detalleDepartamentos");

    let cargosVisibles = [];
    let totalGlobalCargos = 0;
    let editando = false;
    let debounceTimer = null;

    function normalizarCodigo(valor) {
        return (valor || "").toUpperCase().replace(/[^A-Z0-9]/g, "").slice(0, 3);
    }

    function normalizarNombre(valor) {
        return (valor || "").trim().replace(/\s+/g, " ");
    }

    function tieneFiltrosActivos() {
        return filtroTexto.value.trim() !== ""
            || filtroEstado.value !== ""
            || filtroDepartamento.value !== "";
    }

    function construirQueryFiltros() {
        const params = new URLSearchParams();
        const texto = filtroTexto.value.trim();
        if (texto !== "") {
            params.set("q", texto);
        }
        if (filtroEstado.value !== "") {
            params.set("activo", filtroEstado.value);
        }
        if (filtroDepartamento.value !== "") {
            params.set("departamento", filtroDepartamento.value);
        }
        const query = params.toString();
        return query ? "?" + query : "";
    }

    function formatearOpcion(cargo) {
        const estadoTexto = cargo.activo ? "Activo" : "Inactivo";
        const personal = typeof cargo.personal === "number" ? cargo.personal : 0;
        return cargo.codigoCargo + " — " + cargo.nombre + " [" + estadoTexto + " · " + personal + " pers.]";
    }

    function actualizarIndicadores(listaCompleta) {
        totalGlobalCargos = listaCompleta.length;
        totalCargos.textContent = String(listaCompleta.length);
        totalCargosActivos.textContent = String(listaCompleta.filter(c => Boolean(c.activo)).length);
        totalPersonalCargos.textContent = String(
            listaCompleta.reduce((acum, c) => acum + (typeof c.personal === "number" ? c.personal : 0), 0)
        );
    }

    async function cargarTotalesGlobales() {
        try {
            const respuesta = await fetch(API + "/resumen");
            if (respuesta.status === 401) {
                redirigirLogin();
                return false;
            }
            if (!respuesta.ok) {
                return false;
            }
            const lista = await respuesta.json();
            if (Array.isArray(lista)) {
                actualizarIndicadores(lista);
                return true;
            }
        } catch (e) {
            return false;
        }
        return false;
    }

    function obtenerCodigosSeleccionados() {
        return Array.from(lstCargos.selectedOptions)
            .filter(opt => !opt.disabled && opt.value)
            .map(opt => opt.value);
    }

    function buscarCargoVisible(codigo) {
        return cargosVisibles.find(c => c.codigoCargo === codigo) || null;
    }

    function renderizarDepartamentos(cargo) {
        detalleDepartamentos.innerHTML = "";
        if (!cargo) {
            detalleCargo.classList.add("d-none");
            return;
        }
        detalleCargo.classList.remove("d-none");
        const deps = Array.isArray(cargo.departamentos) ? cargo.departamentos : [];
        if (deps.length === 0) {
            const vacio = document.createElement("span");
            vacio.className = "ayuda";
            vacio.textContent = "Sin personal asignado en ningún departamento.";
            detalleDepartamentos.appendChild(vacio);
            return;
        }
        deps.forEach(dep => {
            const badge = document.createElement("span");
            badge.className = "badge-departamento";
            badge.textContent = dep;
            detalleDepartamentos.appendChild(badge);
        });
    }

    function establecerModoRegistro() {
        editando = false;
        formCargo.reset();
        codigoCargo.readOnly = false;
        btnSugerirCodigo.disabled = false;
        activoCargo.checked = true;
        tituloFormCargo.textContent = "Registrar cargo";
        btnGuardarCargo.innerHTML = '<i class="bi bi-save mr-1"></i>Guardar';
        renderizarDepartamentos(null);
    }

    function cargarCargoEnFormulario(cargo) {
        editando = true;
        codigoCargo.value = cargo.codigoCargo;
        codigoCargo.readOnly = true;
        btnSugerirCodigo.disabled = true;
        nombreCargo.value = cargo.nombre;
        activoCargo.checked = Boolean(cargo.activo);
        tituloFormCargo.textContent = "Editar cargo " + cargo.codigoCargo;
        btnGuardarCargo.innerHTML = '<i class="bi bi-arrow-repeat mr-1"></i>Actualizar';
        renderizarDepartamentos(cargo);
    }

    function sincronizarSeleccion() {
        const seleccionados = obtenerCodigosSeleccionados();

        if (chkSeleccionMultiple.checked) {
            btnVerPersonal.disabled = true;
            btnEliminarCargo.disabled = true;
            contadorLote.textContent = seleccionados.length + " cargo(s) seleccionado(s)";
            const haySeleccion = seleccionados.length >= 1;
            btnActivarLote.disabled = !haySeleccion;
            btnInactivarLote.disabled = !haySeleccion;
            renderizarDepartamentos(null);
            return;
        }

        const codigoSel = seleccionados.length === 1 ? seleccionados[0] : null;
        const cargo = codigoSel ? buscarCargoVisible(codigoSel) : null;
        if (cargo) {
            cargarCargoEnFormulario(cargo);
            btnVerPersonal.disabled = false;
            btnEliminarCargo.disabled = false;
        } else {
            btnVerPersonal.disabled = true;
            btnEliminarCargo.disabled = true;
            renderizarDepartamentos(null);
            if (editando) {
                establecerModoRegistro();
            }
        }
    }

    function renderizarListBox(preservarCodigos) {
        const seleccionPrevia = Array.isArray(preservarCodigos)
            ? new Set(preservarCodigos)
            : new Set(obtenerCodigosSeleccionados());

        lstCargos.innerHTML = "";

        if (cargosVisibles.length === 0) {
            const optVacio = document.createElement("option");
            optVacio.disabled = true;
            optVacio.textContent = "No hay cargos que coincidan con los filtros.";
            lstCargos.appendChild(optVacio);
            contadorResultados.textContent = "Mostrando 0 de " + totalGlobalCargos + " cargos";
            btnVerPersonal.disabled = true;
            btnEliminarCargo.disabled = true;
            btnActivarLote.disabled = true;
            btnInactivarLote.disabled = true;
            if (contadorLote) {
                contadorLote.textContent = "0 cargo(s) seleccionado(s)";
            }
            renderizarDepartamentos(null);
            if (editando) {
                establecerModoRegistro();
            }
            return;
        }

        cargosVisibles.forEach(cargo => {
            const opt = document.createElement("option");
            opt.value = cargo.codigoCargo;
            opt.textContent = formatearOpcion(cargo);
            if (!cargo.activo) {
                opt.className = "opcion-inactiva";
            }
            if (seleccionPrevia.has(cargo.codigoCargo)) {
                opt.selected = true;
            }
            lstCargos.appendChild(opt);
        });

        const totalMostrar = Math.max(totalGlobalCargos, cargosVisibles.length);
        contadorResultados.textContent = "Mostrando " + cargosVisibles.length + " de " + totalMostrar + " cargos";
        sincronizarSeleccion();
    }

    async function cargarResumen(preservarCodigos, actualizarGlobales) {
        const seleccionActual = Array.isArray(preservarCodigos)
            ? preservarCodigos
            : obtenerCodigosSeleccionados();

        try {
            const sinFiltros = !tieneFiltrosActivos();
            if (actualizarGlobales && !sinFiltros) {
                await cargarTotalesGlobales();
            }

            const respuesta = await fetch(API + "/resumen" + construirQueryFiltros());
            if (respuesta.status === 401) {
                redirigirLogin();
                return;
            }
            if (!respuesta.ok) {
                const mensaje = await leerMensaje(respuesta, "No se pudo consultar el listado de cargos.");
                mostrarError(mensaje);
                return;
            }

            const datos = await respuesta.json();
            cargosVisibles = Array.isArray(datos) ? datos : [];
            if (sinFiltros) {
                actualizarIndicadores(cargosVisibles);
            }
            renderizarListBox(seleccionActual);
        } catch (e) {
            mostrarError("No fue posible comunicarse con el servidor para cargar los cargos.");
        }
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
                codigoCargo.value = datos.codigo;
            }
            codigoCargo.readOnly = false;
            nombreCargo.focus();
        } catch (e) {
            mostrarError("No fue posible consultar el siguiente código disponible.");
        }
    }

    function validarCargo(cargo) {
        if (!/^[A-Z0-9]{3}$/.test(cargo.codigoCargo)) {
            return "El código debe tener exactamente 3 caracteres alfanuméricos, por ejemplo C08.";
        }
        if (cargo.nombre.length < 3 || cargo.nombre.length > 100) {
            return "El nombre del cargo debe tener entre 3 y 100 caracteres.";
        }
        if (/[0-9]/.test(cargo.nombre)) {
            return "El nombre del cargo no debe contener números.";
        }
        return null;
    }

    async function guardarCargo(evento) {
        evento.preventDefault();

        const cargo = {
            codigoCargo: normalizarCodigo(codigoCargo.value),
            nombre: normalizarNombre(nombreCargo.value),
            activo: activoCargo.checked
        };
        codigoCargo.value = cargo.codigoCargo;
        nombreCargo.value = cargo.nombre;

        const error = validarCargo(cargo);
        if (error) {
            mostrarError(error);
            return;
        }

        const url = editando ? API + "/" + encodeURIComponent(cargo.codigoCargo) : API;
        const metodo = editando ? "PUT" : "POST";

        try {
            const respuesta = await fetch(url, {
                method: metodo,
                headers: {"Content-Type": "application/json"},
                body: JSON.stringify(cargo)
            });
            if (respuesta.status === 401) {
                redirigirLogin();
                return;
            }
            if (!respuesta.ok) {
                mostrarError(await leerMensaje(respuesta, "No se pudo guardar el cargo."));
                return;
            }

            const guardado = await respuesta.json();
            const titulo = editando ? "Cargo actualizado" : "Cargo registrado";
            establecerModoRegistro();
            await cargarResumen(guardado && guardado.codigoCargo ? [guardado.codigoCargo] : [], true);
            Swal.fire({icon: "success", titleText: titulo, confirmButtonColor: "#127c78"});
        } catch (e) {
            mostrarError("Error de conexión al guardar el cargo.");
        }
    }

    async function verPersonalSeleccionado() {
        const codigo = lstCargos.value;
        const cargo = buscarCargoVisible(codigo);
        if (!cargo) {
            return;
        }

        try {
            const respuesta = await fetch(API + "/" + encodeURIComponent(cargo.codigoCargo) + "/personal");
            if (respuesta.status === 401) {
                redirigirLogin();
                return;
            }
            if (!respuesta.ok) {
                mostrarError(await leerMensaje(respuesta, "No se pudo consultar el personal del cargo."));
                return;
            }

            const personas = await respuesta.json();
            const contenedor = document.createElement("div");
            contenedor.className = "table-responsive";

            const tabla = document.createElement("table");
            tabla.className = "table table-sm tabla-sigh text-left mb-0";

            const thead = document.createElement("thead");
            const filaHead = document.createElement("tr");
            ["Código", "Nombre", "Departamento", "Función", "Correo"].forEach(col => {
                const th = document.createElement("th");
                th.textContent = col;
                filaHead.appendChild(th);
            });
            thead.appendChild(filaHead);
            tabla.appendChild(thead);

            const tbody = document.createElement("tbody");
            if (!Array.isArray(personas) || personas.length === 0) {
                const trVacio = document.createElement("tr");
                const tdVacio = document.createElement("td");
                tdVacio.colSpan = 5;
                tdVacio.className = "text-center text-muted";
                tdVacio.textContent = "El cargo no tiene personal asignado.";
                trVacio.appendChild(tdVacio);
                tbody.appendChild(trVacio);
            } else {
                personas.forEach(p => {
                    const tr = document.createElement("tr");
                    const valores = [
                        p.codigoPersona || "-",
                        ((p.nombres || "") + " " + (p.apellidos || "")).trim() || "-",
                        p.departamento || "-",
                        p.funcion || "-",
                        p.correo || "Sin registro"
                    ];
                    valores.forEach(val => {
                        const td = document.createElement("td");
                        td.textContent = val;
                        tr.appendChild(td);
                    });
                    tbody.appendChild(tr);
                });
            }
            tabla.appendChild(tbody);
            contenedor.appendChild(tabla);

            Swal.fire({
                titleText: "Personal con cargo " + cargo.nombre,
                html: contenedor,
                width: 780,
                confirmButtonText: "Cerrar",
                confirmButtonColor: "#127c78"
            });
        } catch (e) {
            mostrarError("Error de conexión al consultar el personal.");
        }
    }

    function eliminarCargoSeleccionado() {
        const codigo = lstCargos.value;
        const cargo = buscarCargoVisible(codigo);
        if (!cargo) {
            return;
        }

        Swal.fire({
            icon: "warning",
            titleText: "Eliminar cargo",
            text: "Se eliminará " + cargo.codigoCargo + " - " + cargo.nombre + ".",
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
                const respuesta = await fetch(API + "/" + encodeURIComponent(cargo.codigoCargo), {
                    method: "DELETE"
                });
                if (respuesta.status === 401) {
                    redirigirLogin();
                    return;
                }
                if (!respuesta.ok) {
                    mostrarError(await leerMensaje(respuesta, "No se pudo eliminar el cargo."));
                    return;
                }
                establecerModoRegistro();
                await cargarResumen([], true);
                Swal.fire({icon: "success", titleText: "Cargo eliminado", confirmButtonColor: "#127c78"});
            } catch (e) {
                mostrarError("Error de conexión al eliminar el cargo.");
            }
        });
    }

    function cambiarEstadoEnLote(activo) {
        const codigos = obtenerCodigosSeleccionados();
        if (codigos.length === 0) {
            return;
        }
        const accion = activo ? "activar" : "inactivar";
        const tituloAccion = activo ? "Activar cargos seleccionados" : "Inactivar cargos seleccionados";

        Swal.fire({
            icon: "question",
            titleText: tituloAccion,
            text: "Se procederá a " + accion + " " + codigos.length + " cargo(s): " + codigos.join(", ") + ".",
            showCancelButton: true,
            confirmButtonText: activo ? "Activar" : "Inactivar",
            cancelButtonText: "Cancelar",
            confirmButtonColor: activo ? "#127c78" : "#b54747",
            cancelButtonColor: "#627d98"
        }).then(async resultado => {
            if (!resultado.isConfirmed) {
                return;
            }
            try {
                const respuesta = await fetch(API + "/estado-lote", {
                    method: "PATCH",
                    headers: {"Content-Type": "application/json"},
                    body: JSON.stringify({codigos, activo})
                });
                if (respuesta.status === 401) {
                    redirigirLogin();
                    return;
                }
                if (!respuesta.ok) {
                    mostrarError(await leerMensaje(respuesta, "No se pudo actualizar el estado en lote."));
                    return;
                }
                await cargarResumen(codigos, true);
                Swal.fire({
                    icon: "success",
                    titleText: activo ? "Cargos activados" : "Cargos inactivados",
                    confirmButtonColor: "#127c78"
                });
            } catch (e) {
                mostrarError("Error de conexión al actualizar el estado en lote.");
            }
        });
    }

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

    async function leerMensaje(respuesta, defecto) {
        if (respuesta.status === 401) {
            redirigirLogin();
            return "Debe iniciar sesión.";
        }
        try {
            const datos = await respuesta.json();
            return datos && datos.mensaje ? datos.mensaje : defecto;
        } catch (e) {
            return defecto;
        }
    }

    // Eventos de sanitización de entradas
    codigoCargo.addEventListener("input", () => {
        codigoCargo.value = normalizarCodigo(codigoCargo.value);
    });
    nombreCargo.addEventListener("input", () => {
        nombreCargo.value = nombreCargo.value.replace(/[0-9]/g, "");
    });

    // Eventos de filtros
    filtroTexto.addEventListener("input", () => {
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(() => cargarResumen(), 200);
    });
    filtroEstado.addEventListener("change", () => cargarResumen());
    filtroDepartamento.addEventListener("change", () => cargarResumen());
    btnLimpiarFiltros.addEventListener("click", () => {
        clearTimeout(debounceTimer);
        filtroTexto.value = "";
        filtroEstado.value = "";
        filtroDepartamento.value = "";
        cargarResumen();
    });

    // Conmutador selección única / múltiple
    chkSeleccionMultiple.addEventListener("change", () => {
        const multiple = chkSeleccionMultiple.checked;
        lstCargos.multiple = multiple;
        lstCargos.classList.toggle("modo-multiple", multiple);

        if (multiple) {
            barraLote.classList.remove("d-none");
            establecerModoRegistro();
        } else {
            barraLote.classList.add("d-none");
            const seleccionados = obtenerCodigosSeleccionados();
            if (seleccionados.length > 1) {
                Array.from(lstCargos.options).forEach(opt => {
                    opt.selected = opt.value === seleccionados[0];
                });
            }
        }
        sincronizarSeleccion();
    });

    // Eventos del ListBox
    lstCargos.addEventListener("change", sincronizarSeleccion);
    lstCargos.addEventListener("click", () => {
        if (!chkSeleccionMultiple.checked) {
            sincronizarSeleccion();
        }
    });

    // Acciones CRUD y lote
    btnNuevoCargo.addEventListener("click", async () => {
        if (chkSeleccionMultiple.checked) {
            chkSeleccionMultiple.checked = false;
            lstCargos.multiple = false;
            lstCargos.classList.remove("modo-multiple");
            barraLote.classList.add("d-none");
        }
        lstCargos.selectedIndex = -1;
        btnVerPersonal.disabled = true;
        btnEliminarCargo.disabled = true;
        establecerModoRegistro();
        await sugerirSiguienteCodigo();
    });

    btnSugerirCodigo.addEventListener("click", async () => {
        if (editando) {
            return;
        }
        await sugerirSiguienteCodigo();
    });

    btnLimpiarForm.addEventListener("click", () => {
        if (!chkSeleccionMultiple.checked) {
            lstCargos.selectedIndex = -1;
            btnVerPersonal.disabled = true;
            btnEliminarCargo.disabled = true;
        }
        establecerModoRegistro();
    });

    formCargo.addEventListener("submit", guardarCargo);
    btnVerPersonal.addEventListener("click", verPersonalSeleccionado);
    btnEliminarCargo.addEventListener("click", eliminarCargoSeleccionado);
    btnActivarLote.addEventListener("click", () => cambiarEstadoEnLote(true));
    btnInactivarLote.addEventListener("click", () => cambiarEstadoEnLote(false));

    // Carga inicial del resumen vía API
    cargarResumen();
})();
