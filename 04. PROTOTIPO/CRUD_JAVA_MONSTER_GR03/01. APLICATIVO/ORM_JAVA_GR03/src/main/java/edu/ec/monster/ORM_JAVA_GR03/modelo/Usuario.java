package edu.ec.monster.ORM_JAVA_GR03.modelo;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "SEUSU_USUARI")
public class Usuario {
    @Id
    @Column(name = "SEUSU_CODIGO", nullable = false, length = 6)
    @NotBlank
    @Size(max = 6)
    private String codigoUsuario;
    @Column(name = "SEUSU_USUARI", nullable = false, length = 255)
    @NotBlank
    @Size(max = 255)
    private String usuario;
    @Column(name = "SEUSU_HASHCL", nullable = false, length = 255)
    @Size(max = 255)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String hashClave;
    @Column(name = "SEUSU_INTFAL", nullable = false)
    @NotNull
    private Integer intentosFallidos;
    @Column(name = "SEUSU_BLOHAS", nullable = true)
    private LocalDateTime bloqueoHasta;
    @Column(name = "SEUSU_FECCRE", nullable = false)
    @NotNull
    private LocalDateTime fechaCreacion;
    @Column(name = "SEUSU_FECMOD", nullable = true)
    private LocalDateTime fechaModificacion;
    @Column(name = "SEUSU_PIEFIR", nullable = true, length = 200)
    @Size(max = 200)
    private String pieFirma;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PEPER_CODIGO", nullable = false, unique = true)
    @NotNull
    private Persona persona;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SEEST_CODIGO", nullable = false)
    @NotNull
    private Estado estado;
    @OneToMany(mappedBy = "usuario", fetch = FetchType.EAGER)
    private java.util.List<UsuPer> asignaciones = new java.util.ArrayList<>();
    public Usuario() {}
    public String getCodigoUsuario() { return codigoUsuario; }
    public void setCodigoUsuario(String value) { this.codigoUsuario = value; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String value) { this.usuario = value; }
    public String getHashClave() { return hashClave; }
    public void setHashClave(String value) { this.hashClave = value; }
    public Integer getIntentosFallidos() { return intentosFallidos; }
    public void setIntentosFallidos(Integer value) { this.intentosFallidos = value; }
    public LocalDateTime getBloqueoHasta() { return bloqueoHasta; }
    public void setBloqueoHasta(LocalDateTime value) { this.bloqueoHasta = value; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime value) { this.fechaCreacion = value; }
    public LocalDateTime getFechaModificacion() { return fechaModificacion; }
    public void setFechaModificacion(LocalDateTime value) { this.fechaModificacion = value; }
    public String getPieFirma() { return pieFirma; }
    public void setPieFirma(String value) { this.pieFirma = value; }
    public Persona getPersona() { return persona; }
    public void setPersona(Persona value) { this.persona = value; }
    public Estado getEstado() { return estado; }
    public void setEstado(Estado value) { this.estado = value; }
    public java.util.List<UsuPer> getAsignaciones() { return asignaciones; }
}
