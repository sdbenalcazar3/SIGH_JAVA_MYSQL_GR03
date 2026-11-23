package edu.ec.monster.ORM_JAVA_GR03.dto.reporte;

import java.util.Objects;

/**
 * POJO JavaBean que representa una fila del reporte de personal / empleados
 * ({@code reporte_personal.jrxml}) para {@code JRBeanCollectionDataSource}.
 */
public class ReportePersonalFila {

    private String codigoPersona;
    private String codigoExterno;
    private String nombreCompleto;
    private String numeroDocumento;
    private String departamento;
    private String cargo;
    private String funcion;
    private String correo;
    private String estado;
    private Long totalAcademicos;
    private Long totalFamiliares;

    public ReportePersonalFila() {
    }

    public ReportePersonalFila(
            String codigoPersona,
            String codigoExterno,
            String nombreCompleto,
            String numeroDocumento,
            String departamento,
            String cargo,
            String funcion,
            String correo,
            String estado,
            Long totalAcademicos,
            Long totalFamiliares) {
        this.codigoPersona = codigoPersona;
        this.codigoExterno = codigoExterno;
        this.nombreCompleto = nombreCompleto;
        this.numeroDocumento = numeroDocumento;
        this.departamento = departamento;
        this.cargo = cargo;
        this.funcion = funcion;
        this.correo = correo;
        this.estado = estado;
        this.totalAcademicos = totalAcademicos;
        this.totalFamiliares = totalFamiliares;
    }

    public String getCodigoPersona() {
        return codigoPersona;
    }

    public void setCodigoPersona(String codigoPersona) {
        this.codigoPersona = codigoPersona;
    }

    public String getCodigoExterno() {
        return codigoExterno;
    }

    public void setCodigoExterno(String codigoExterno) {
        this.codigoExterno = codigoExterno;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public String getFuncion() {
        return funcion;
    }

    public void setFuncion(String funcion) {
        this.funcion = funcion;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Long getTotalAcademicos() {
        return totalAcademicos;
    }

    public void setTotalAcademicos(Long totalAcademicos) {
        this.totalAcademicos = totalAcademicos;
    }

    public Long getTotalFamiliares() {
        return totalFamiliares;
    }

    public void setTotalFamiliares(Long totalFamiliares) {
        this.totalFamiliares = totalFamiliares;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ReportePersonalFila that)) {
            return false;
        }
        return Objects.equals(codigoPersona, that.codigoPersona)
                && Objects.equals(codigoExterno, that.codigoExterno)
                && Objects.equals(nombreCompleto, that.nombreCompleto)
                && Objects.equals(numeroDocumento, that.numeroDocumento)
                && Objects.equals(departamento, that.departamento)
                && Objects.equals(cargo, that.cargo)
                && Objects.equals(funcion, that.funcion)
                && Objects.equals(correo, that.correo)
                && Objects.equals(estado, that.estado)
                && Objects.equals(totalAcademicos, that.totalAcademicos)
                && Objects.equals(totalFamiliares, that.totalFamiliares);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
                codigoPersona,
                codigoExterno,
                nombreCompleto,
                numeroDocumento,
                departamento,
                cargo,
                funcion,
                correo,
                estado,
                totalAcademicos,
                totalFamiliares);
    }

    @Override
    public String toString() {
        return "ReportePersonalFila{"
                + "codigoPersona='" + codigoPersona + '\''
                + ", codigoExterno='" + codigoExterno + '\''
                + ", nombreCompleto='" + nombreCompleto + '\''
                + ", numeroDocumento='" + numeroDocumento + '\''
                + ", departamento='" + departamento + '\''
                + ", cargo='" + cargo + '\''
                + ", funcion='" + funcion + '\''
                + ", correo='" + correo + '\''
                + ", estado='" + estado + '\''
                + ", totalAcademicos=" + totalAcademicos
                + ", totalFamiliares=" + totalFamiliares
                + '}';
    }
}
