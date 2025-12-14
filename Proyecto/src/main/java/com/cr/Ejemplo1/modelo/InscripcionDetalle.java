package com.cr.Ejemplo1.modelo;

import java.time.LocalDateTime;

/**
 * Clase para transportar la información combinada de la inscripción 
 * y los detalles del participante a la capa de vista.
 */
public class InscripcionDetalle {

    // Datos de campeonatos_inscripcion
    private Long idInscripcion;
    private String idModalidad;    
    private LocalDateTime fechaInscripcion;
    
    // Datos de usuarios (JOIN)
    private String nombreParticipante;
    private String sexo;
    private int edad;
    private String cinturonRango;
    
    // Dato calculado/mapeado (usando json_modalidades)
    private String nombreModalidad; 
    
    // Constructor vacío
    public InscripcionDetalle() {
    }

    // --- Getters y Setters ---

    public Long getIdInscripcion() {
        return idInscripcion;
    }

    public void setIdInscripcion(Long idInscripcion) {
        this.idInscripcion = idInscripcion;
    }

    public String getNombreParticipante() {
        return nombreParticipante;
    }

    public void setNombreParticipante(String nombreParticipante) {
        this.nombreParticipante = nombreParticipante;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getCinturonRango() {
        return cinturonRango;
    }

    public void setCinturonRango(String cinturonRango) {
        this.cinturonRango = cinturonRango;
    }

    public String getIdModalidad() {
        return idModalidad;
    }

    public void setIdModalidad(String idModalidad) {
        this.idModalidad = idModalidad;
    }
    
    // Nuevo Getter para el nombre legible de la modalidad
    public String getNombreModalidad() {
        return nombreModalidad;
    }
    
    // Nuevo Setter para el nombre legible de la modalidad
    public void setNombreModalidad(String nombreModalidad) {
        this.nombreModalidad = nombreModalidad;
    }

    public LocalDateTime getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(LocalDateTime fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    // Opcional: Para facilitar la depuración
    @Override
    public String toString() {
        return "InscripcionDetalle{" +
                "idInscripcion=" + idInscripcion +
                ", nombreParticipante='" + nombreParticipante + '\'' +
                ", idModalidad='" + idModalidad + '\'' +
                ", nombreModalidad='" + nombreModalidad + '\'' +
                '}';
    }
}