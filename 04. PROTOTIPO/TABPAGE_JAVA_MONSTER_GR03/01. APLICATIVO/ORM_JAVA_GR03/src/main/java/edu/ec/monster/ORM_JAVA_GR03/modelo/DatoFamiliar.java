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
import java.time.LocalDate;

/**
 * Entidad JPA para los registros familiares de un empleado (tabla PEFAM_FAMILI).
 */
@Entity
@Table(name = "PEFAM_FAMILI")
public class DatoFamiliar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PEFAM_CODIGO")
    private Long codigoFamiliar;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PEPER_CODIGO", nullable = false)
    @JsonIgnore
    private Persona persona;

    @Column(name = "PEFAM_PARENT", nullable = false, length = 60)
    @NotBlank
    @Size(max = 60)
    private String parentesco;

    @Column(name = "PEFAM_APELLI", nullable = false, length = 150)
    @NotBlank
    @Size(max = 150)
    private String apellidos;

    @Column(name = "PEFAM_NOMBRE", nullable = false, length = 150)
    @NotBlank
    @Size(max = 150)
    private String nombres;

    @Column(name = "PEFAM_FECNAC", nullable = true)
    private LocalDate fechaNacimiento;

    public DatoFamiliar() {}

    public DatoFamiliar(Persona persona, String parentesco, String apellidos, String nombres,
            LocalDate fechaNacimiento) {
        this.persona = persona;
        this.parentesco = parentesco;
        this.apellidos = apellidos;
        this.nombres = nombres;
        this.fechaNacimiento = fechaNacimiento;
    }

    public Long getCodigoFamiliar() { return codigoFamiliar; }
    public void setCodigoFamiliar(Long codigoFamiliar) { this.codigoFamiliar = codigoFamiliar; }

    public Persona getPersona() { return persona; }
    public void setPersona(Persona persona) { this.persona = persona; }

    public String getParentesco() { return parentesco; }
    public void setParentesco(String parentesco) { this.parentesco = parentesco; }

    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }

    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
}
