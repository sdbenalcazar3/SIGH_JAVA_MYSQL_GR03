package edu.ec.monster.ORM_JAVA_GR03.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "SETOK_RECUPE")
public class TokenRecuperacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SETOK_CODIGO")
    private Long codigo;

    @Column(name = "SETOK_TOKEN", nullable = false, length = 64, unique = true)
    private String token;

    @Column(name = "SETOK_FECCRE", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "SETOK_EXPIRA", nullable = false)
    private LocalDateTime expira;

    @Column(name = "SETOK_USADO", nullable = false)
    private Boolean usado;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SEUSU_CODIGO", nullable = false)
    private Usuario usuario;

    public TokenRecuperacion() {}

    public Long getCodigo() { return codigo; }
    public void setCodigo(Long codigo) { this.codigo = codigo; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public LocalDateTime getExpira() { return expira; }
    public void setExpira(LocalDateTime expira) { this.expira = expira; }
    public Boolean getUsado() { return usado; }
    public void setUsado(Boolean usado) { this.usado = usado; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
}
