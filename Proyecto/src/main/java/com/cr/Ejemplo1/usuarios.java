package com.cr.Ejemplo1;

public class usuarios {
    protected int ID_documento;
    protected String nombreC;
    protected String cinturon_rango;
    protected int edad;
    protected String contraseña;
    protected String correo;
    protected String sexo;
    protected String nacionalidad;

    
    public usuarios() {
        
    }

    
    public usuarios(int ID_documento, String nombreC, String cinturon_rango, int edad, 
                   String contraseña, String correo, String sexo, String nacionalidad) {
        this.ID_documento = ID_documento;
        this.nombreC = nombreC;
        this.cinturon_rango = cinturon_rango;
        this.edad = edad;
        this.contraseña = contraseña;
        this.correo = correo;
        this.sexo = sexo;
        this.nacionalidad = nacionalidad;
    }

    
    public int getID_documento() {
        return ID_documento;
    }

    public void setID_documento(int ID_documento) {
        this.ID_documento = ID_documento;
    }

    public String getNombreC() {
        return nombreC;
    }

    public void setNombreC(String nombreC) {
        this.nombreC = nombreC;
    }

    public String getCinturon_rango() {
        return cinturon_rango;
    }

    public void setCinturon_rango(String cinturon_rango) {
        this.cinturon_rango = cinturon_rango;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }
}