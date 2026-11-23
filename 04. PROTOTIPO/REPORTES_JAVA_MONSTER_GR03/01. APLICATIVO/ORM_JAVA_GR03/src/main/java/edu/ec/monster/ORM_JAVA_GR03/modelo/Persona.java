package edu.ec.monster.ORM_JAVA_GR03.modelo;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "PEPER_PERSON")
public class Persona {
    @Id
    @Column(name = "PEPER_CODIGO", nullable = false, length = 6)
    @NotBlank
    @Size(max = 6)
    private String codigoPersona;
    @Column(name = "PEPER_CODEXT", nullable = false, length = 10)
    @NotBlank
    @Size(max = 10)
    private String codigoExterno;
    @Column(name = "PEPER_NOMBRE", nullable = false, length = 255)
    @NotBlank
    @Size(max = 255)
    private String nombres;
    @Column(name = "PEPER_FUNCIO", nullable = false, length = 255)
    @NotBlank
    @Size(max = 255)
    private String funcion;
    @Column(name = "PEPER_HABILI", nullable = true, length = 255)
    @Size(max = 255)
    private String habilitacion;
    @Column(name = "PEPER_ACTIVO", nullable = false)
    @NotNull
    private Boolean activo;
    @Column(name = "PEPER_APELLI", nullable = false, length = 150)
    @NotBlank
    @Size(max = 150)
    private String apellidos;
    @Column(name = "PEPER_TIPDOC", nullable = true, length = 30)
    @Size(max = 30)
    private String tipoDocumento;
    @Column(name = "PEPER_NUMDOC", nullable = true, length = 30)
    @Size(max = 30)
    private String numeroDocumento;
    @Column(name = "PEPER_FECNAC", nullable = true)
    private LocalDate fechaNacimiento;
    @Column(name = "PEPER_TELEFO", nullable = true, length = 30)
    @Size(max = 30)
    private String telefono;
    @Column(name = "PEPER_CORREO", nullable = true, length = 150)
    @Size(max = 150)
    private String correo;
    @Column(name = "PEPER_FOTO", nullable = true, length = 255)
    @Size(max = 255)
    private String foto;
    @Column(name = "PEPER_TIPSAN", nullable = true, length = 5)
    @Size(max = 5)
    private String tipoSangre;
    @Column(name = "PEPER_PESO", nullable = true, precision = 5, scale = 2)
    @DecimalMin("20.00")
    @DecimalMax("300.00")
    private BigDecimal peso;
    @Column(name = "PEPER_ESTATU", nullable = true, precision = 4, scale = 2)
    @DecimalMin("0.50")
    @DecimalMax("2.50")
    private BigDecimal estatura;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PEGEN_CODIGO", nullable = true)
    private Genero genero;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PEEST_CODIGO", nullable = true)
    private EstadoCivil estadoCivil;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PEDEP_CODIGO", nullable = false)
    @NotNull
    private Departamento departamento;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "PECAR_CODIGO", nullable = false)
    @NotNull
    private Cargo cargo;
    public Persona() {}
    public String getCodigoPersona() { return codigoPersona; }
    public void setCodigoPersona(String value) { this.codigoPersona = value; }
    public String getCodigoExterno() { return codigoExterno; }
    public void setCodigoExterno(String value) { this.codigoExterno = value; }
    public String getNombres() { return nombres; }
    public void setNombres(String value) { this.nombres = value; }
    public String getFuncion() { return funcion; }
    public void setFuncion(String value) { this.funcion = value; }
    public String getHabilitacion() { return habilitacion; }
    public void setHabilitacion(String value) { this.habilitacion = value; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean value) { this.activo = value; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String value) { this.apellidos = value; }
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String value) { this.tipoDocumento = value; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String value) { this.numeroDocumento = value; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate value) { this.fechaNacimiento = value; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String value) { this.telefono = value; }
    public String getCorreo() { return correo; }
    public void setCorreo(String value) { this.correo = value; }
    public String getFoto() { return foto; }
    public void setFoto(String value) { this.foto = value; }
    public String getTipoSangre() { return tipoSangre; }
    public void setTipoSangre(String value) { this.tipoSangre = value; }
    public BigDecimal getPeso() { return peso; }
    public void setPeso(BigDecimal value) { this.peso = value; }
    public BigDecimal getEstatura() { return estatura; }
    public void setEstatura(BigDecimal value) { this.estatura = value; }
    public Genero getGenero() { return genero; }
    public void setGenero(Genero value) { this.genero = value; }
    public EstadoCivil getEstadoCivil() { return estadoCivil; }
    public void setEstadoCivil(EstadoCivil value) { this.estadoCivil = value; }
    public Departamento getDepartamento() { return departamento; }
    public void setDepartamento(Departamento value) { this.departamento = value; }
    public Cargo getCargo() { return cargo; }
    public void setCargo(Cargo value) { this.cargo = value; }
}
