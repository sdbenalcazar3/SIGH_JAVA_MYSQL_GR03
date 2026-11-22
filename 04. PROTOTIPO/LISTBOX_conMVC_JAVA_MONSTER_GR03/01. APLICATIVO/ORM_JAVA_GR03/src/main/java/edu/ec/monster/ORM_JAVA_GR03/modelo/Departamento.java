package edu.ec.monster.ORM_JAVA_GR03.modelo;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "PEDEP_DEPART")
public class Departamento {
    @Id
    @Column(name = "PEDEP_CODIGO", nullable = false, length = 3)
    @NotBlank
    @Size(max = 3)
    private String codigoDepartamento;
    @Column(name = "PEDEP_NOMBRE", nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    private String nombre;
    @Column(name = "PEDEP_ACTIVO", nullable = false)
    @NotNull
    private Boolean activo;
    public Departamento() {}
    public String getCodigoDepartamento() { return codigoDepartamento; }
    public void setCodigoDepartamento(String value) { this.codigoDepartamento = value; }
    public String getNombre() { return nombre; }
    public void setNombre(String value) { this.nombre = value; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean value) { this.activo = value; }
}
