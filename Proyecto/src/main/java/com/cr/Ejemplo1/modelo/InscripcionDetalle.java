package com.cr.Ejemplo1.modelo;

import java.time.LocalDateTime;


public class InscripcionDetalle {

    
    private Long idInscripcion;
    private String idModalidad;    
    private LocalDateTime fechaInscripcion;
    
    
    private String nombreParticipante;
    private String sexo;
    private int edad;
    private String cinturonRango;
    
    
    private String nombreModalidad; 
    
    
    public InscripcionDetalle() {
    }

   

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
    
    
    public String getNombreModalidad() {
        return nombreModalidad;
    }
    
    
    public void setNombreModalidad(String nombreModalidad) {
        this.nombreModalidad = nombreModalidad;
    }

    public LocalDateTime getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(LocalDateTime fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    
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