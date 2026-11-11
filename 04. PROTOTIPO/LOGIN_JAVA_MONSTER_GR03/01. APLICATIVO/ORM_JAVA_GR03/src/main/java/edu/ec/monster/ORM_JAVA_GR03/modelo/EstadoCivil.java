package edu.ec.monster.ORM_JAVA_GR03.modelo;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "PEEST_ESTADO")
public class EstadoCivil {
    @Id
    @Column(name = "PEEST_CODIGO", nullable = false, length = 1)
    @NotBlank
    @Size(max = 1)
    private String codigoEstadoCivil;
    @Column(name = "PEEST_NOMBRE", nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    private String nombre;
    @Column(name = "PEEST_ACTIVO", nullable = false)
    @NotNull
    private Boolean activo;
    public EstadoCivil() {}
    public String getCodigoEstadoCivil() { return codigoEstadoCivil; }
    public void setCodigoEstadoCivil(String value) { this.codigoEstadoCivil = value; }
    public String getNombre() { return nombre; }
    public void setNombre(String value) { this.nombre = value; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean value) { this.activo = value; }
}
