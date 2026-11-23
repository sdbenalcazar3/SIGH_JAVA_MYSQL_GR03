package edu.ec.monster.ORM_JAVA_GR03.dto;

/** Datos basicos de una persona que se muestran en la ventana "Ver personal". */
public record PersonalDepartamento(String codigoPersona, String nombres, String apellidos,
        String cargo, String funcion, String correo, Boolean activo) {
}
