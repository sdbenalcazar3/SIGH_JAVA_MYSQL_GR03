package edu.ec.monster.ORM_JAVA_GR03.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Ficha completa del empleado para las 3 pestañas del TabPage (PEPER_PERSON + hijos). */
public record EmpleadoDetalle(
        String codigoPersona,
        String codigoExterno,
        String apellidos,
        String nombres,
        String foto,
        String funcion,
        String habilitacion,
        Boolean activo,
        String tipoDocumento,
        String numeroDocumento,
        LocalDate fechaNacimiento,
        String telefono,
        String correo,
        String codigoGenero,
        String genero,
        String codigoEstadoCivil,
        String estadoCivil,
        String tipoSangre,
        BigDecimal peso,
        BigDecimal estatura,
        String codigoDepartamento,
        String departamento,
        String codigoCargo,
        String cargo,
        boolean tieneUsuario,
        List<DatoAcademicoDto> academicos,
        List<DatoFamiliarDto> familiares) {}
