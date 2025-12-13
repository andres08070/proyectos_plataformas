package com.cr.Ejemplo1.modelo;

import java.util.List;

public class ModalidadData {
    // Estos nombres de campos DEBEN coincidir con las claves del JSON (name, desc, peso, rango, edad, genero)
    private int idCreador;
    private String name; 
    private String desc;
    private List<String> peso;
    private List<String> rango;
    private List<String> edad;
    private String genero;
    
    // NOTA: El campo 'name' es el nombre de la modalidad (Ej: "Combate").

    // --- Getters y Setters (Necesarios para la deserialización de Jackson) ---
    // (Tu IDE puede generarlos automáticamente)

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDesc() { return desc; }
    public void setDesc(String desc) { this.desc = desc; }

    public List<String> getPeso() { return peso; }
    public void setPeso(List<String> peso) { this.peso = peso; }

    public List<String> getRango() { return rango; }
    public void setRango(List<String> rango) { this.rango = rango; }

    public List<String> getEdad() { return edad; }
    public void setEdad(List<String> edad) { this.edad = edad; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public int getIdCreador() {return idCreador;}

    public void setIdCreador(int idCreador) {this.idCreador = idCreador;}
    
    @Override
    public String toString() {
        return "ModalidadData [name=" + name + ", peso=" + peso + ", genero=" + genero + "]";
    }
}