package edu.ec.monster.ORM_JAVA_GR03.dto.reporte;

import java.util.Objects;

/**
 * POJO JavaBean que representa una fila del reporte de departamentos
 * ({@code reporte_departamentos.jrxml}) para {@code JRBeanCollectionDataSource}.
 */
public class ReporteDepartamentoFila {

    private String codigoDepartamento;
    private String nombre;
    private String estado;
    private Long totalPersonal;

    public ReporteDepartamentoFila() {
    }

    public ReporteDepartamentoFila(String codigoDepartamento, String nombre, String estado, Long totalPersonal) {
        this.codigoDepartamento = codigoDepartamento;
        this.nombre = nombre;
        this.estado = estado;
        this.totalPersonal = totalPersonal;
    }

    public String getCodigoDepartamento() {
        return codigoDepartamento;
    }

    public void setCodigoDepartamento(String codigoDepartamento) {
        this.codigoDepartamento = codigoDepartamento;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReporteDepartamentoFila that)) {
            return false;
        }
        return Objects.equals(codigoDepartamento, that.codigoDepartamento)
                && Objects.equals(nombre, that.nombre)
                && Objects.equals(estado, that.estado)
                && Objects.equals(totalPersonal, that.totalPersonal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoDepartamento, nombre, estado, totalPersonal);
    }

    @Override
    public String toString() {
        return "ReporteDepartamentoFila{"
                + "codigoDepartamento='" + codigoDepartamento + '\''
                + ", nombre='" + nombre + '\''
                + ", estado='" + estado + '\''
                + ", totalPersonal=" + totalPersonal
                + '}';
    }
}
