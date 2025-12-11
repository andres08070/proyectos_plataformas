/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.cr.Ejemplo1;

/**
 *
 * @author andre
 */
public class usuarios {
    protected int ID_documento;
    protected String nombreC;
    protected String cinturon_rango;
    protected int edad;
    protected String contraseña;
    protected String correo;
    protected String sexo;
    protected String nacionalidad;

    public usuarios(int ID_documento, String nombreC, String cinturon_rango, int edad, String contraseña,String correo, String sexo, String nacionalidad) {
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

    public String getNombreC() {
        return nombreC;
    }

    public String getCinturon_rango() {
        return cinturon_rango;
    }

    public int getEdad() {
        return edad;
    }

    public String getContraseña() {
        return contraseña;
    }

    public String getCorreo() {
        return correo;
    }

    public String getSexo() {
        return sexo;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }
    
            
}
