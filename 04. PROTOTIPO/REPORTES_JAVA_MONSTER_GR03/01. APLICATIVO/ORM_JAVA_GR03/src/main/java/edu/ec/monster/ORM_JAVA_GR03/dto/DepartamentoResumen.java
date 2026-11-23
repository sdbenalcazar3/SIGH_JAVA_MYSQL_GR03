package edu.ec.monster.ORM_JAVA_GR03.dto;

/** Fila del listado de departamentos: datos de PEDEP_DEPART mas el total de personal asignado. */
public record DepartamentoResumen(String codigoDepartamento, String nombre, Boolean activo, long personal) {
}
