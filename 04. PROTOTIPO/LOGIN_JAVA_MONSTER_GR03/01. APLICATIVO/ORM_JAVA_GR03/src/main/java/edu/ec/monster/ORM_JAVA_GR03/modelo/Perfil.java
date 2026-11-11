package edu.ec.monster.ORM_JAVA_GR03.modelo;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "SEPER_PERFIL")
public class Perfil {
    @Id
    @Column(name = "SEPER_CODIGO", nullable = false, length = 3)
    @NotBlank
    @Size(max = 3)
    private String codigoPerfil;
    @Column(name = "SEPER_NOMBRE", nullable = false, length = 255)
    @NotBlank
    @Size(max = 255)
    private String nombre;
    @Column(name = "SEPER_ACTIVO", nullable = false)
    @NotNull
    private Boolean activo;
    @Column(name = "SEPER_DESCRI", nullable = true, columnDefinition = "TEXT")
    private String descripcion;
    @Column(name = "SEPER_OBSERV", nullable = true, columnDefinition = "TEXT")
    private String observaciones;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SEEST_CODIGO", nullable = false)
    @NotNull
    private Estado estado;
    @OneToMany(mappedBy = "perfil", fetch = FetchType.EAGER)
    private java.util.List<OpcPer> permisos = new java.util.ArrayList<>();
    public Perfil() {}
    public String getCodigoPerfil() { return codigoPerfil; }
    public void setCodigoPerfil(String value) { this.codigoPerfil = value; }
    public String getNombre() { return nombre; }
    public void setNombre(String value) { this.nombre = value; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean value) { this.activo = value; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String value) { this.descripcion = value; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String value) { this.observaciones = value; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado value) { this.estado = value; }
    public java.util.List<OpcPer> getPermisos() { return permisos; }
}
