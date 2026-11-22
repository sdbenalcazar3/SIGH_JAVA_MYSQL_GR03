package edu.ec.monster.ORM_JAVA_GR03.dto;

/** Datos basicos de una persona asignada a un cargo (ventana "Ver personal"). */
public record PersonalCargo(
        String codigoPersona,
        String nombres,
        String apellidos,
        String departamento,
        String funcion,
        String correo,
        Boolean activo
) {}
