package edu.ec.monster.ORM_JAVA_GR03.dto;

import java.util.List;

/** Fila del ListBox de cargos: datos de PECAR_CARGO, total de personal y departamentos donde se ocupa el cargo. */
public record CargoResumen(
        String codigoCargo,
        String nombre,
        Boolean activo,
        long personal,
        List<String> departamentos
) {}
