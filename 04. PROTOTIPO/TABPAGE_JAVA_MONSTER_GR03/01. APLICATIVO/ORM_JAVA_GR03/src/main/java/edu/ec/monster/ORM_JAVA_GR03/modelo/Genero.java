package edu.ec.monster.ORM_JAVA_GR03.modelo;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "PEGEN_GENERO")
public class Genero {
    @Id
    @Column(name = "PEGEN_CODIGO", nullable = false, length = 1)
    @NotBlank
    @Size(max = 1)
    private String codigoGenero;
    @Column(name = "PEGEN_NOMBRE", nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    private String nombre;
    @Column(name = "PEGEN_ACTIVO", nullable = false)
    @NotNull
    private Boolean activo;
    public Genero() {}
    public String getCodigoGenero() { return codigoGenero; }
    public void setCodigoGenero(String value) { this.codigoGenero = value; }
    public String getNombre() { return nombre; }
    public void setNombre(String value) { this.nombre = value; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean value) { this.activo = value; }
}
