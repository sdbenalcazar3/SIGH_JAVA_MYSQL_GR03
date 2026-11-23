(() => {
    "use strict";

    const ctx = document.body.dataset.ctx || "";
    const API = ctx + "/api/reportes";

    // KPIs superiores
    const kpiTotalDepartamentos = document.getElementById("kpiTotalDepartamentos");
    const kpiSubDepartamentos = document.getElementById("kpiSubDepartamentos");
    const kpiTotalCargos = document.getElementById("kpiTotalCargos");
    const kpiSubCargos = document.getElementById("kpiSubCargos");
    const kpiTotalPersonal = document.getElementById("kpiTotalPersonal");
    const kpiSubPersonal = document.getElementById("kpiSubPersonal");
    const kpiPersonalActivo = document.getElementById("kpiPersonalActivo");
    const kpiSubPersonalActivo = document.getElementById("kpiSubPersonalActivo");
    const btnActualizarResumen = document.getElementById("btnActualizarResumen");

    // Filtros: Departamentos
    const formDepartamentos = document.getElementById("formReporteDepartamentos");
    const depFiltroTexto = document.getElementById("depFiltroTexto");
    const depFiltroEstado = document.getElementById("depFiltroEstado");
    const depBtnLimpiar = document.getElementById("depBtnLimpiar");

    // Filtros: Cargos
    const formCargos = document.getElementById("formReporteCargos");
    const carFiltroTexto = document.getElementById("carFiltroTexto");
    const carFiltroEstado = document.getElementById("carFiltroEstado");
    const carFiltroDepartamento = document.getElementById("carFiltroDepartamento");
    const carBtnLimpiar = document.getElementById("carBtnLimpiar");

    // Filtros: Personal
    const formPersonal = document.getElementById("formReportePersonal");
    const perFiltroTexto = document.getElementById("perFiltroTexto");
    const perFiltroEstado = document.getElementById("perFiltroEstado");
    const perFiltroDepartamento = document.getElementById("perFiltroDepartamento");
    const perFiltroCargo = document.getElementById("perFiltroCargo");
    const perBtnLimpiar = document.getElementById("perBtnLimpiar");

    // Modal visor PDF
    const modalPdf = document.getElementById("modalVistaPreviaPdf");
    const tituloModalPdf = document.getElementById("tituloModalPdf");
    const subtituloModalPdf = document.getElementById("subtituloModalPdf");
    const cargandoVistaPrevia = document.getElementById("cargandoVistaPrevia");
    const iframeVistaPreviaPdf = document.getElementById("iframeVistaPreviaPdf");
    const btnAbrirPestanaPdf = document.getElementById("btnAbrirPestanaPdf");
    const btnDescargarDesdeModal = document.getElementById("btnDescargarDesdeModal");

    const TITULOS_REPORTE = {
        departamentos: "Reporte de Departamentos",
        cargos: "Reporte de Cargos",
        personal: "Reporte de Personal / Empleados"
    };

    let modalBackdrop = null;
    let modalAbierto = false;
    let urlBlobPdfActual = null;
    let tipoModalActual = null;

    // ------------------------------------------------------------------
    // Controlador Vanilla JS de modal Bootstrap 4 (sin dependencia jQuery)
    // ------------------------------------------------------------------
    function abrirModalPdf(tipo, resumenFiltros) {
        tipoModalActual = tipo;
        tituloModalPdf.innerHTML = '<i class="bi bi-file-earmark-pdf mr-2"></i>'
            + (TITULOS_REPORTE[tipo] || "Vista previa de reporte");
        subtituloModalPdf.textContent = resumenFiltros;

        liberarBlobActual();
        iframeVistaPreviaPdf.classList.add("d-none");
        iframeVistaPreviaPdf.removeAttribute("src");
        cargandoVistaPrevia.classList.remove("d-none");
        btnAbrirPestanaPdf.classList.add("disabled");
        btnAbrirPestanaPdf.setAttribute("href", "#");
        btnDescargarDesdeModal.disabled = true;

        if (!modalAbierto) {
            modalAbierto = true;
            modalBackdrop = document.createElement("div");
            modalBackdrop.className = "modal-backdrop fade show";
            document.body.appendChild(modalBackdrop);
            document.body.classList.add("modal-open");
            modalPdf.style.display = "block";
            modalPdf.removeAttribute("aria-hidden");
            modalPdf.classList.add("show");
        }
    }

    function cerrarModalPdf() {
        if (!modalAbierto) {
            return;
        }
        modalAbierto = false;
        modalPdf.classList.remove("show");
        modalPdf.style.display = "none";
        modalPdf.setAttribute("aria-hidden", "true");
        document.body.classList.remove("modal-open");
        if (modalBackdrop && modalBackdrop.parentNode) {
            modalBackdrop.parentNode.removeChild(modalBackdrop);
        }
        modalBackdrop = null;
        iframeVistaPreviaPdf.removeAttribute("src");
        liberarBlobActual();
        tipoModalActual = null;
    }

    function liberarBlobActual() {
        if (urlBlobPdfActual) {
            URL.revokeObjectURL(urlBlobPdfActual);
            urlBlobPdfActual = null;
        }
    }

    modalPdf.querySelectorAll('[data-dismiss="modal"]').forEach(btn => {
        btn.addEventListener("click", cerrarModalPdf);
    });

    modalPdf.addEventListener("click", evento => {
        if (evento.target === modalPdf) {
            cerrarModalPdf();
        }
    });

    document.addEventListener("keydown", evento => {
        if (evento.key === "Escape" && modalAbierto) {
            cerrarModalPdf();
        }
    });

    // ------------------------------------------------------------------
    // Construcción segura de URLs y lectura de filtros
    // ------------------------------------------------------------------
    function textoSeleccionado(selectEl) {
        if (!selectEl || !selectEl.value) {
            return "";
        }
        const opt = selectEl.options[selectEl.selectedIndex];
        return opt ? opt.textContent.trim() : selectEl.value;
    }

    function construirQueryReporte(tipo, formato, descarga) {
        const params = new URLSearchParams();
        params.set("formato", formato);
        params.set("descarga", descarga ? "true" : "false");

        if (tipo === "departamentos") {
            const q = depFiltroTexto.value.trim();
            if (q !== "") {
                params.set("q", q);
            }
            if (depFiltroEstado.value !== "") {
                params.set("activo", depFiltroEstado.value);
            }
        } else if (tipo === "cargos") {
            const q = carFiltroTexto.value.trim();
            if (q !== "") {
                params.set("q", q);
            }
            if (carFiltroEstado.value !== "") {
                params.set("activo", carFiltroEstado.value);
            }
            if (carFiltroDepartamento.value !== "") {
                params.set("departamento", carFiltroDepartamento.value);
            }
        } else if (tipo === "personal") {
            const q = perFiltroTexto.value.trim();
            if (q !== "") {
                params.set("q", q);
            }
            if (perFiltroEstado.value !== "") {
                params.set("activo", perFiltroEstado.value);
            }
            if (perFiltroDepartamento.value !== "") {
                params.set("departamento", perFiltroDepartamento.value);
            }
            if (perFiltroCargo.value !== "") {
                params.set("cargo", perFiltroCargo.value);
            }
        }

        return API + "/" + encodeURIComponent(tipo) + "?" + params.toString();
    }

    function describirFiltros(tipo) {
        const partes = [];
        if (tipo === "departamentos") {
            if (depFiltroTexto.value.trim() !== "") {
                partes.push("Búsqueda: " + depFiltroTexto.value.trim());
            }
            if (depFiltroEstado.value !== "") {
                partes.push("Estado: " + textoSeleccionado(depFiltroEstado));
            }
        } else if (tipo === "cargos") {
            if (carFiltroTexto.value.trim() !== "") {
                partes.push("Búsqueda: " + carFiltroTexto.value.trim());
            }
            if (carFiltroEstado.value !== "") {
                partes.push("Estado: " + textoSeleccionado(carFiltroEstado));
            }
            if (carFiltroDepartamento.value !== "") {
                partes.push("Departamento: " + textoSeleccionado(carFiltroDepartamento));
            }
        } else if (tipo === "personal") {
            if (perFiltroTexto.value.trim() !== "") {
                partes.push("Búsqueda: " + perFiltroTexto.value.trim());
            }
            if (perFiltroEstado.value !== "") {
                partes.push("Estado: " + textoSeleccionado(perFiltroEstado));
            }
            if (perFiltroDepartamento.value !== "") {
                partes.push("Departamento: " + textoSeleccionado(perFiltroDepartamento));
            }
            if (perFiltroCargo.value !== "") {
                partes.push("Cargo: " + textoSeleccionado(perFiltroCargo));
            }
        }
        return partes.length > 0 ? partes.join(" · ") : "Sin filtros (todos los registros)";
    }

    function extraerNombreArchivo(respuesta, tipo, formato) {
        const cabecera = respuesta.headers.get("Content-Disposition") || "";
        const matchUtf8 = cabecera.match(/filename\*\s*=\s*UTF-8''([^;]+)/i);
        if (matchUtf8 && matchUtf8[1]) {
            try {
                return decodeURIComponent(matchUtf8[1].trim());
            } catch (e) {
                return matchUtf8[1].trim();
            }
        }
        const matchSimple = cabecera.match(/filename\s*=\s*"?([^";]+)"?/i);
        if (matchSimple && matchSimple[1]) {
            return matchSimple[1].trim();
        }
        return "reporte_" + tipo + "." + formato;
    }

    // ------------------------------------------------------------------
    // Manejo centralizado de errores HTTP y mensajes SweetAlert2
    // ------------------------------------------------------------------
    function redirigirLogin() {
        window.location.href = ctx + "/login?expirada";
    }

    async function leerMensajeError(respuesta, defecto) {
        try {
            const datos = await respuesta.json();
            if (datos && datos.mensaje) {
                return datos.mensaje;
            }
        } catch (e) {
            // La respuesta no es JSON; se usa el mensaje por defecto
        }
        return defecto;
    }

    async function manejarErrorHttp(respuesta, accionDescripcion) {
        if (respuesta.status === 401) {
            redirigirLogin();
            return;
        }
        if (respuesta.status === 403) {
            const msg = await leerMensajeError(
                respuesta,
                "Su perfil no tiene asignada la opción REP para generar reportes."
            );
            await Swal.fire({
                icon: "warning",
                titleText: "Acceso restringido",
                text: msg,
                confirmButtonColor: "#127c78"
            });
            return;
        }
        if (respuesta.status === 400) {
            const msg = await leerMensajeError(
                respuesta,
                "Los parámetros enviados para el reporte no son válidos."
            );
            await Swal.fire({
                icon: "warning",
                titleText: "Solicitud inválida",
                text: msg,
                confirmButtonColor: "#127c78"
            });
            return;
        }
        const msg = await leerMensajeError(
            respuesta,
            "No fue posible " + accionDescripcion + ". Intente nuevamente."
        );
        await Swal.fire({
            icon: "error",
            titleText: "Error al generar reporte",
            text: msg,
            confirmButtonColor: "#127c78"
        });
    }

    function establecerEstadoBotones(deshabilitado) {
        document.querySelectorAll(".btn-reporte-accion").forEach(btn => {
            btn.disabled = deshabilitado;
        });
    }

    // ------------------------------------------------------------------
    // Carga de KPIs (GET /api/reportes/resumen)
    // ------------------------------------------------------------------
    function formatearNumero(valor) {
        return typeof valor === "number" && Number.isFinite(valor) ? String(valor) : "0";
    }

    async function cargarResumen() {
        try {
            if (btnActualizarResumen) {
                btnActualizarResumen.disabled = true;
            }
            const respuesta = await fetch(API + "/resumen", {
                headers: {Accept: "application/json"}
            });
            if (!respuesta.ok) {
                await manejarErrorHttp(respuesta, "consultar los indicadores de reportes");
                return;
            }
            const datos = await respuesta.json();
            const totalDep = Number(datos.totalDepartamentos) || 0;
            const depAct = Number(datos.departamentosActivos) || 0;
            const totalCar = Number(datos.totalCargos) || 0;
            const carAct = Number(datos.cargosActivos) || 0;
            const totalPer = Number(datos.totalPersonal) || 0;
            const perAct = Number(datos.personalActivo) || 0;
            const porcentaje = totalPer > 0 ? Math.round((perAct * 100) / totalPer) : 0;

            kpiTotalDepartamentos.textContent = formatearNumero(totalDep);
            kpiSubDepartamentos.textContent = depAct + " activo(s) · " + Math.max(0, totalDep - depAct) + " inactivo(s)";

            kpiTotalCargos.textContent = formatearNumero(totalCar);
            kpiSubCargos.textContent = carAct + " activo(s) · " + Math.max(0, totalCar - carAct) + " inactivo(s)";

            kpiTotalPersonal.textContent = formatearNumero(totalPer);
            kpiSubPersonal.textContent = Math.max(0, totalPer - perAct) + " inactivo(s) en catálogo";

            kpiPersonalActivo.textContent = formatearNumero(perAct);
            kpiSubPersonalActivo.textContent = porcentaje + "% del personal total";
        } catch (e) {
            Swal.fire({
                icon: "error",
                titleText: "Error de conexión",
                text: "No fue posible consultar el resumen de indicadores de reportes.",
                confirmButtonColor: "#127c78"
            });
        } finally {
            if (btnActualizarResumen) {
                btnActualizarResumen.disabled = false;
            }
        }
    }

    // ------------------------------------------------------------------
    // Generación de Vista Previa PDF y Descarga PDF / Excel (.xlsx)
    // ------------------------------------------------------------------
    async function vistaPreviaPdf(tipo) {
        const url = construirQueryReporte(tipo, "pdf", false);
        const resumenFiltros = describirFiltros(tipo);
        abrirModalPdf(tipo, resumenFiltros);
        establecerEstadoBotones(true);

        try {
            const respuesta = await fetch(url, {
                headers: {Accept: "application/pdf, application/json"}
            });
            if (!respuesta.ok) {
                cerrarModalPdf();
                await manejarErrorHttp(respuesta, "generar la vista previa PDF");
                return;
            }

            const blob = await respuesta.blob();
            const pdfBlob = blob.type === "application/pdf"
                ? blob
                : new Blob([blob], {type: "application/pdf"});
            urlBlobPdfActual = URL.createObjectURL(pdfBlob);

            iframeVistaPreviaPdf.src = urlBlobPdfActual;
            cargandoVistaPrevia.classList.add("d-none");
            iframeVistaPreviaPdf.classList.remove("d-none");

            btnAbrirPestanaPdf.href = urlBlobPdfActual;
            btnAbrirPestanaPdf.classList.remove("disabled");
            btnDescargarDesdeModal.disabled = false;
        } catch (e) {
            cerrarModalPdf();
            Swal.fire({
                icon: "error",
                titleText: "Error de conexión",
                text: "No se pudo establecer comunicación con el servidor para generar el PDF.",
                confirmButtonColor: "#127c78"
            });
        } finally {
            establecerEstadoBotones(false);
        }
    }

    async function descargarReporte(tipo, formato) {
        const url = construirQueryReporte(tipo, formato, true);
        const etiquetaFormato = formato === "xlsx" ? "Excel (.xlsx)" : "PDF";
        establecerEstadoBotones(true);

        try {
            const respuesta = await fetch(url);
            if (!respuesta.ok) {
                await manejarErrorHttp(respuesta, "exportar el reporte en " + etiquetaFormato);
                return;
            }

            const blob = await respuesta.blob();
            const nombreArchivo = extraerNombreArchivo(respuesta, tipo, formato);
            const urlTemporal = URL.createObjectURL(blob);

            const enlace = document.createElement("a");
            enlace.href = urlTemporal;
            enlace.download = nombreArchivo;
            enlace.style.display = "none";
            document.body.appendChild(enlace);
            enlace.click();
            document.body.removeChild(enlace);

            setTimeout(() => URL.revokeObjectURL(urlTemporal), 10000);

            Swal.fire({
                toast: true,
                position: "top-end",
                icon: "success",
                titleText: "Descarga iniciada: " + nombreArchivo,
                showConfirmButton: false,
                timer: 2600
            });
        } catch (e) {
            Swal.fire({
                icon: "error",
                titleText: "Error de conexión",
                text: "No fue posible descargar el reporte en formato " + etiquetaFormato + ".",
                confirmButtonColor: "#127c78"
            });
        } finally {
            establecerEstadoBotones(false);
        }
    }

    // ------------------------------------------------------------------
    // Registro de eventos
    // ------------------------------------------------------------------
    document.querySelectorAll(".btn-reporte-accion").forEach(boton => {
        boton.addEventListener("click", () => {
            const tipo = boton.dataset.reporte;
            const accion = boton.dataset.accion;
            if (!tipo || !accion) {
                return;
            }
            if (accion === "preview") {
                vistaPreviaPdf(tipo);
            } else if (accion === "pdf" || accion === "xlsx") {
                descargarReporte(tipo, accion);
            }
        });
    });

    if (formDepartamentos) {
        formDepartamentos.addEventListener("submit", evento => {
            evento.preventDefault();
            vistaPreviaPdf("departamentos");
        });
    }
    if (formCargos) {
        formCargos.addEventListener("submit", evento => {
            evento.preventDefault();
            vistaPreviaPdf("cargos");
        });
    }
    if (formPersonal) {
        formPersonal.addEventListener("submit", evento => {
            evento.preventDefault();
            vistaPreviaPdf("personal");
        });
    }

    if (depBtnLimpiar) {
        depBtnLimpiar.addEventListener("click", () => {
            depFiltroTexto.value = "";
            depFiltroEstado.value = "";
        });
    }
    if (carBtnLimpiar) {
        carBtnLimpiar.addEventListener("click", () => {
            carFiltroTexto.value = "";
            carFiltroEstado.value = "";
            carFiltroDepartamento.value = "";
        });
    }
    if (perBtnLimpiar) {
        perBtnLimpiar.addEventListener("click", () => {
            perFiltroTexto.value = "";
            perFiltroEstado.value = "";
            perFiltroDepartamento.value = "";
            perFiltroCargo.value = "";
        });
    }

    if (btnDescargarDesdeModal) {
        btnDescargarDesdeModal.addEventListener("click", () => {
            if (tipoModalActual) {
                descargarReporte(tipoModalActual, "pdf");
            }
        });
    }

    if (btnActualizarResumen) {
        btnActualizarResumen.addEventListener("click", cargarResumen);
    }

    window.addEventListener("beforeunload", liberarBlobActual);

    // Carga inicial de KPIs
    cargarResumen();
})();
