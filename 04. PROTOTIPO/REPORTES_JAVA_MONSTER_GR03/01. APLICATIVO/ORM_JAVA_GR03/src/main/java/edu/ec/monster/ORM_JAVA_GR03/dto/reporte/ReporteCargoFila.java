package edu.ec.monster.ORM_JAVA_GR03.dto.reporte;

import java.util.Objects;

/**
 * POJO JavaBean que representa una fila del reporte de cargos
 * ({@code reporte_cargos.jrxml}) para {@code JRBeanCollectionDataSource}.
 */
public class ReporteCargoFila {

    private String codigoCargo;
    private String nombre;
    private String estado;
    private Long totalPersonal;
    private String departamentos;

    public ReporteCargoFila() {
    }

    public ReporteCargoFila(
            String codigoCargo,
            String nombre,
            String estado,
            Long totalPersonal,
            String departamentos) {
        this.codigoCargo = codigoCargo;
        this.nombre = nombre;
        this.estado = estado;
        this.totalPersonal = totalPersonal;
        this.departamentos = departamentos;
    }

    public String getCodigoCargo() {
        return codigoCargo;
    }

    public void setCodigoCargo(String codigoCargo) {
        this.codigoCargo = codigoCargo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getTotalPersonal() {
        return totalPersonal;
    }

    public void setTotalPersonal(Long totalPersonal) {
        this.totalPersonal = totalPersonal;
    }

    public String getDepartamentos() {
        return departamentos;
    }

    public void setDepartamentos(String departamentos) {
        this.departamentos = departamentos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReporteCargoFila that)) {
            return false;
        }
        return Objects.equals(codigoCargo, that.codigoCargo)
                && Objects.equals(nombre, that.nombre)
                && Objects.equals(estado, that.estado)
                && Objects.equals(totalPersonal, that.totalPersonal)
                && Objects.equals(departamentos, that.departamentos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoCargo, nombre, estado, totalPersonal, departamentos);
    }

    @Override
    public String toString() {
        return "ReporteCargoFila{"
                + "codigoCargo='" + codigoCargo + '\''
                + ", nombre='" + nombre + '\''
                + ", estado='" + estado + '\''
                + ", totalPersonal=" + totalPersonal
                + ", departamentos='" + departamentos + '\''
                + '}';
    }
}
