package edu.ec.monster.ORM_JAVA_GR03.modelo;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "PECAR_CARGO")
public class Cargo {
    @Id
    @Column(name = "PECAR_CODIGO", nullable = false, length = 3)
    @NotBlank
    @Size(max = 3)
    private String codigoCargo;
    @Column(name = "PECAR_NOMBRE", nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    private String nombre;
    @Column(name = "PECAR_ACTIVO", nullable = false)
    @NotNull
    private Boolean activo;
    public Cargo() {}
    public String getCodigoCargo() { return codigoCargo; }
    public void setCodigoCargo(String value) { this.codigoCargo = value; }
    public String getNombre() { return nombre; }
    public void setNombre(String value) { this.nombre = value; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean value) { this.activo = value; }
}
