package com.cr.Ejemplo1.modelo;

import java.time.LocalDate;

public class Campeonato {

    // Campos del Step 1
    private long id;
    private String nombre;
    // --- Campo Faltante Añadido ---
    private String descripcion; 
    // -----------------------------
    private LocalDate fechaInicio; 
    private LocalDate fechaFin;
    private String ubicacion;
    private String jsonModalidades; 

    // Campo del Step 3
    private int numAreas;

    // --- Getters y Setters (Necesarios para el mapeo de Spring) ---

    // Getters para campos básicos
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // --- Getter y Setter para Descripcion Añadidos ---
    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    // ------------------------------------------------

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
    // ... (El resto de getters/setters y toString permanecen iguales) ...

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
    

    // Opcional: Para ver todos los datos en la consola
    @Override
    public String toString() {
        return "Campeonato{" +
                "nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' + // Añadido al toString
                ", fechaInicio=" + fechaInicio +
                ", fechaFin=" + fechaFin +
                ", ubicacion='" + ubicacion + '\'' +
                ", numAreas=" + numAreas +
                '}';
    }
}