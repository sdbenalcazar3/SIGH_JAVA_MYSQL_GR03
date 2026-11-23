package edu.ec.monster.ORM_JAVA_GR03.modelo;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "SEEST_ESTADO")
public class Estado {
    @Id
    @Column(name = "SEEST_CODIGO", nullable = false, length = 1)
    @NotBlank
    @Size(max = 1)
    private String codigoEstado;
    @Column(name = "SEEST_NOMBRE", nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    private String nombre;
    @Column(name = "SEEST_ACTIVO", nullable = false)
    @NotNull
    private Boolean activo;
    public Estado() {}
    public String getCodigoEstado() { return codigoEstado; }
    public void setCodigoEstado(String value) { this.codigoEstado = value; }
    public String getNombre() { return nombre; }
    public void setNombre(String value) { this.nombre = value; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean value) { this.activo = value; }
}
