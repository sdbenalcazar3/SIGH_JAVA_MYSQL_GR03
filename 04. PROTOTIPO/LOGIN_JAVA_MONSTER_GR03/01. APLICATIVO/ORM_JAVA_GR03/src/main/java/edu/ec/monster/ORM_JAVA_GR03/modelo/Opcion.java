package edu.ec.monster.ORM_JAVA_GR03.modelo;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "SEOPC_OPCION")
public class Opcion {
    @Id
    @Column(name = "SEOPC_CODIGO", nullable = false, length = 3)
    @NotBlank
    @Size(max = 3)
    private String codigoOpcion;
    @Column(name = "SEOPC_MODULO", nullable = false, length = 255)
    @NotBlank
    @Size(max = 255)
    private String modulo;
    @Column(name = "SEOPC_OPERAC", nullable = false, length = 255)
    @NotBlank
    @Size(max = 255)
    private String operacion;
    @Column(name = "SEOPC_ACTIVO", nullable = false)
    @NotNull
    private Boolean activo;
    @Column(name = "SEOPC_NOMBRE", nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    private String nombre;
    @Column(name = "SEOPC_DESCRI", nullable = true, columnDefinition = "TEXT")
    private String descripcion;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SESIS_CODIGO", nullable = false)
    @NotNull
    private Sistema sistema;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SEEST_CODIGO", nullable = false)
    @NotNull
    private Estado estado;
    public Opcion() {}
    public String getCodigoOpcion() { return codigoOpcion; }
    public void setCodigoOpcion(String value) { this.codigoOpcion = value; }
    public String getModulo() { return modulo; }
    public void setModulo(String value) { this.modulo = value; }
    public String getOperacion() { return operacion; }
    public void setOperacion(String value) { this.operacion = value; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean value) { this.activo = value; }
    public String getNombre() { return nombre; }
    public void setNombre(String value) { this.nombre = value; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String value) { this.descripcion = value; }
    public Sistema getSistema() { return sistema; }
    public void setSistema(Sistema value) { this.sistema = value; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado value) { this.estado = value; }
}
