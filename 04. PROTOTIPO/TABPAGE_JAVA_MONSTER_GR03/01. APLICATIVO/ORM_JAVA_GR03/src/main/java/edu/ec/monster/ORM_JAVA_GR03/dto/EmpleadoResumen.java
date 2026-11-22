package edu.ec.monster.ORM_JAVA_GR03.dto;

/** Resumen de empleado para el modal Listar y el navegador de registros del TabPage. */
public record EmpleadoResumen(
        String codigoPersona,
        String codigoExterno,
        String apellidos,
        String nombres,
        String funcion,
        String numeroDocumento,
        String correo,
        String foto,
        String codigoDepartamento,
        String departamento,
        String codigoCargo,
        String cargo,
        Boolean activo,
        long totalAcademicos,
        long totalFamiliares) {}
