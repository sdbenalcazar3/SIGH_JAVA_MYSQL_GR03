package edu.ec.monster.ORM_JAVA_GR03.modelo;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "SEUXP_USUPER")
public class UsuPer {
    @Id
    @Column(name = "SEUXP_CODIGO", nullable = false, length = 6)
    @NotBlank
    @Size(max = 6)
    private String codigoAsignacion;
    @Column(name = "SEUXP_DESDE", nullable = false)
    @NotNull
    private LocalDateTime desde;
    @Column(name = "SEUXP_HASTA", nullable = true)
    private LocalDateTime hasta;
    @Column(name = "SEUXP_AMBITO", nullable = false, length = 255)
    @NotBlank
    @Size(max = 255)
    private String ambito;
    @Column(name = "SEUXP_VIGENT", nullable = false)
    @NotNull
    private Boolean vigente;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SEUSU_CODIGO", nullable = false)
    @JsonIgnore
    @NotNull
    private Usuario usuario;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SEPER_CODIGO", nullable = false)
    @NotNull
    private Perfil perfil;
@JsonIgnore
        @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SEUSU_CODIGO_ASIGNA", nullable = false)
    @NotNull
    private Usuario asignadoPor;
    public UsuPer() {}
    public String getCodigoAsignacion() { return codigoAsignacion; }
    public void setCodigoAsignacion(String value) { this.codigoAsignacion = value; }
    public LocalDateTime getDesde() { return desde; }
    public void setDesde(LocalDateTime value) { this.desde = value; }
    public LocalDateTime getHasta() { return hasta; }
    public void setHasta(LocalDateTime value) { this.hasta = value; }
    public String getAmbito() { return ambito; }
    public void setAmbito(String value) { this.ambito = value; }
    public Boolean getVigente() { return vigente; }
    public void setVigente(Boolean value) { this.vigente = value; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario value) { this.usuario = value; }
    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil value) { this.perfil = value; }
    public Usuario getAsignadoPor() { return asignadoPor; }
    public void setAsignadoPor(Usuario value) { this.asignadoPor = value; }
}
