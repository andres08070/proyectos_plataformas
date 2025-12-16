package com.cr.Ejemplo1.modelo;

import java.time.LocalDate;

public class Campeonato {

   
    private long id;
    private String nombre;
    private String descripcion; 
    private LocalDate fechaInicio; 
    private LocalDate fechaFin;
    private String ubicacion;
    private String jsonModalidades; 
    private int numAreas;
    private String nombreCreador;

   
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
   
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
    
    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
    public String getJsonModalidades() {
        return jsonModalidades;
    }
    public void setJsonModalidades(String jsonModalidades) {
        this.jsonModalidades = jsonModalidades;
    }

    public int getNumAreas() {
        return numAreas;
    }

    public void setNumAreas(int numAreas) {
        this.numAreas = numAreas;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombreCreador() {
        return nombreCreador;
    }

    public void setNombreCreador(String nombreCreador) {
        this.nombreCreador = nombreCreador;
    }
    
    
    

   
    @Override
    public String toString() {
        return "Campeonato{" +
                "nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", ubicacion='" + ubicacion + '\'' +
                ", numAreas=" + numAreas +
                '}';
    }
}