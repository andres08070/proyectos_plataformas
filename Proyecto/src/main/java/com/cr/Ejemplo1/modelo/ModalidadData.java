package com.cr.Ejemplo1.modelo;

import java.util.ArrayList;
import java.util.List;

// Clase para mapear la estructura interna de cada modalidad en el JSON de la BD
public class ModalidadData {
    private String idModalidad = "";
    private String name = "";
    private String desc = "";
    private List<String> peso = new ArrayList<>();
    private List<String> rango = new ArrayList<>();
    private List<String> edad = new ArrayList<>();
    private String genero = "";

    // Constructor vacío (obligatorio para Jackson)
    public ModalidadData() {
        // Necesario para la deserialización
    }

    // --- Getters y Setters ---

    public String getIdModalidad() {
        return idModalidad;
    }

    public void setIdModalidad(String idModalidad) {
        this.idModalidad = idModalidad;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<String> getPeso() {
        return peso;
    }

    public void setPeso(List<String> peso) {
        this.peso = peso;
    }

    public List<String> getRango() {
        return rango;
    }

    public void setRango(List<String> rango) {
        this.rango = rango;
    }

    public List<String> getEdad() {
        return edad;
    }

    public void setEdad(List<String> edad) {
        this.edad = edad;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    @Override
    public String toString() {
        return "ModalidadData [idModalidad=" + idModalidad +
               ", name=" + name +
               ", desc=" + desc +
               ", peso=" + peso +
               ", rango=" + rango +
               ", edad=" + edad +
               ", genero=" + genero + "]";
    }
}
