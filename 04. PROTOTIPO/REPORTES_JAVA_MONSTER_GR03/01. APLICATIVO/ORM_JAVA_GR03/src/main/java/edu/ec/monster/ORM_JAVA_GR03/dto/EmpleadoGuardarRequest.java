package edu.ec.monster.ORM_JAVA_GR03.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/** Solicitud de alta o actualización atómica de ficha completa de empleado (maestro + detalles). */
public record EmpleadoGuardarRequest(
        @NotBlank @Size(max = 6) String codigoPersona,
        @Size(max = 10) String codigoExterno,
        @NotBlank @Size(max = 150) String apellidos,
        @NotBlank @Size(max = 255) String nombres,
        @NotBlank @Size(max = 255) String funcion,
        @Size(max = 255) String habilitacion,
        @NotNull Boolean activo,
        @Size(max = 30) String tipoDocumento,
        @Size(max = 30) String numeroDocumento,
        LocalDate fechaNacimiento,
        @Size(max = 30) String telefono,
        @Size(max = 150) String correo,
        @Size(max = 1) String codigoGenero,
        @Size(max = 1) String codigoEstadoCivil,
        @Size(max = 5) String tipoSangre,
        @DecimalMin("20.00") @DecimalMax("300.00") BigDecimal peso,
        @DecimalMin("0.50") @DecimalMax("2.50") BigDecimal estatura,
        @NotBlank @Size(max = 3) String codigoDepartamento,
        @NotBlank @Size(max = 3) String codigoCargo,
        @Valid List<DatoAcademicoDto> academicos,
        @Valid List<DatoFamiliarDto> familiares) {}
