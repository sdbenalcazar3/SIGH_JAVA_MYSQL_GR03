package edu.ec.monster.ORM_JAVA_GR03.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/** DTO de un registro familiar del empleado (PEFAM_FAMILI). */
public record DatoFamiliarDto(
        Long codigoFamiliar,
        @NotBlank @Size(max = 60) String parentesco,
        @NotBlank @Size(max = 150) String apellidos,
        @NotBlank @Size(max = 150) String nombres,
        LocalDate fechaNacimiento) {}
