package edu.ec.monster.ORM_JAVA_GR03.modelo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Entidad JPA para los registros académicos de un empleado (tabla PEACA_ACADEM).
 */
@Entity
@Table(name = "PEACA_ACADEM")
public class DatoAcademico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PEACA_CODIGO")
    private Long codigoAcademico;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PEPER_CODIGO", nullable = false)
    @JsonIgnore
    private Persona persona;

    @Column(name = "PEACA_NIVEL", nullable = false, length = 60)
    @NotBlank
    @Size(max = 60)
    private String nivel;

    @Column(name = "PEACA_INSTIT", nullable = false, length = 150)
    @NotBlank
    @Size(max = 150)
    private String institucion;

    @Column(name = "PEACA_TITULO", nullable = false, length = 150)
    @NotBlank
    @Size(max = 150)
    private String titulo;

    public DatoAcademico() {}

    public DatoAcademico(Persona persona, String nivel, String institucion, String titulo) {
        this.persona = persona;
        this.nivel = nivel;
        this.institucion = institucion;
        this.titulo = titulo;
    }

    public Long getCodigoAcademico() { return codigoAcademico; }
    public void setCodigoAcademico(Long codigoAcademico) { this.codigoAcademico = codigoAcademico; }

    public Persona getPersona() { return persona; }
    public void setPersona(Persona persona) { this.persona = persona; }

    public String getNivel() { return nivel; }
    public void setNivel(String nivel) { this.nivel = nivel; }

    public String getInstitucion() { return institucion; }
    public void setInstitucion(String institucion) { this.institucion = institucion; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
}
