package edu.ec.monster.ORM_JAVA_GR03.modelo;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "SESIS_SISTEM")
public class Sistema {
    @Id
    @Column(name = "SESIS_CODIGO", nullable = false, length = 2)
    @NotBlank
    @Size(max = 2)
    private String codigoSistema;
    @Column(name = "SESIS_NOMBRE", nullable = false, length = 100)
    @NotBlank
    @Size(max = 100)
    private String nombre;
    @Column(name = "SESIS_ACTIVO", nullable = false)
    @NotNull
    private Boolean activo;
    public Sistema() {}
    public String getCodigoSistema() { return codigoSistema; }
    public void setCodigoSistema(String value) { this.codigoSistema = value; }
    public String getNombre() { return nombre; }
    public void setNombre(String value) { this.nombre = value; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean value) { this.activo = value; }
}
