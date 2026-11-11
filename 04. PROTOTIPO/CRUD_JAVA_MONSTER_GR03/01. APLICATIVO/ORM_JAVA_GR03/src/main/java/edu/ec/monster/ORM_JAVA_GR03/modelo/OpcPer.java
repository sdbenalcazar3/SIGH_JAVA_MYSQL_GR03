package edu.ec.monster.ORM_JAVA_GR03.modelo;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "SEOXP_OPCPER")
public class OpcPer {
    @Id
    @Column(name = "SEOXP_CODIGO", nullable = false, length = 6)
    @NotBlank
    @Size(max = 6)
    private String codigoPermiso;
    @Column(name = "SEOXP_AMBITO", nullable = false, length = 255)
    @NotBlank
    @Size(max = 255)
    private String ambito;
    @Column(name = "SEOXP_VIGENT", nullable = false)
    @NotNull
    private Boolean vigente;
    @Column(name = "SEOXP_DESDE", nullable = false)
    @NotNull
    private LocalDateTime desde;
    @Column(name = "SEOXP_HASTA", nullable = true)
    private LocalDateTime hasta;
@JsonIgnore
        @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SEPER_CODIGO", nullable = false)
    @NotNull
    private Perfil perfil;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SEOPC_CODIGO", nullable = false)
    @NotNull
    private Opcion opcion;
@JsonIgnore
        @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SEUSU_CODIGO", nullable = false)
    @NotNull
    private Usuario concedidoPor;
    public OpcPer() {}
    public String getCodigoPermiso() { return codigoPermiso; }
    public void setCodigoPermiso(String value) { this.codigoPermiso = value; }
    public String getAmbito() { return ambito; }
    public void setAmbito(String value) { this.ambito = value; }
    public Boolean getVigente() { return vigente; }
    public void setVigente(Boolean value) { this.vigente = value; }
    public LocalDateTime getDesde() { return desde; }
    public void setDesde(LocalDateTime value) { this.desde = value; }
    public LocalDateTime getHasta() { return hasta; }
    public void setHasta(LocalDateTime value) { this.hasta = value; }
    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil value) { this.perfil = value; }
    public Opcion getOpcion() { return opcion; }
    public void setOpcion(Opcion value) { this.opcion = value; }
    public Usuario getConcedidoPor() { return concedidoPor; }
    public void setConcedidoPor(Usuario value) { this.concedidoPor = value; }
}
