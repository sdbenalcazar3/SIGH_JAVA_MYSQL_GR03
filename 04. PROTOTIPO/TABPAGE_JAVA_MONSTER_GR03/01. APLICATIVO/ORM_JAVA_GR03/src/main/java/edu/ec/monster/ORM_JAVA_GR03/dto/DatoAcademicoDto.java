package edu.ec.monster.ORM_JAVA_GR03.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** DTO de un registro académico del empleado (PEACA_ACADEM). */
public record DatoAcademicoDto(
        Long codigoAcademico,
        @NotBlank @Size(max = 60) String nivel,
        @NotBlank @Size(max = 150) String institucion,
        @NotBlank @Size(max = 150) String titulo) {}
