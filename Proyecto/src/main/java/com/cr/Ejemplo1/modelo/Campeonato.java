package com.cr.Ejemplo1.modelo;

// Solo necesitamos el ID y el nombre para la lista
public class Campeonato {
    private int id; // Asume que el ID en tu BD es int o Long
    private String nombre;
    private String fechaInicio;
    private String ubicacion;
    // Constructor
    public Campeonato(int id, String nombre, String fechaInicio,String ubicacion) {
        this.id = id;
        this.nombre = nombre;
        this.fechaInicio = fechaInicio;
        this.ubicacion = ubicacion;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public String getUbicacion() {
        return ubicacion;
    }
    
    // (Opcional) Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
    
}