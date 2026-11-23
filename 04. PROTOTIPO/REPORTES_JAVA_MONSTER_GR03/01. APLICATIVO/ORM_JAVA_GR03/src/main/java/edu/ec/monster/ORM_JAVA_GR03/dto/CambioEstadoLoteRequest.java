package edu.ec.monster.ORM_JAVA_GR03.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/** Solicitud para activar o inactivar varios cargos seleccionados en el ListBox. */
public record CambioEstadoLoteRequest(
        @NotEmpty List<@NotBlank @Size(max = 3) String> codigos,
        @NotNull Boolean activo
) {}
