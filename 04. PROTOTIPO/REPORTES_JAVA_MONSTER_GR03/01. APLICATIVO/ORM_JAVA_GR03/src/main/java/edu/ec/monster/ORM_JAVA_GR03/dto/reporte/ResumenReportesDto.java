package edu.ec.monster.ORM_JAVA_GR03.dto.reporte;

/**
 * Indicadores agregados (KPIs) expuestos por {@code GET /api/reportes/resumen}
 * para las tarjetas superiores de la vista de reportes.
 */
public record ResumenReportesDto(
        long totalDepartamentos,
        long departamentosActivos,
        long totalCargos,
        long cargosActivos,
        long totalPersonal,
        long personalActivo) {}
