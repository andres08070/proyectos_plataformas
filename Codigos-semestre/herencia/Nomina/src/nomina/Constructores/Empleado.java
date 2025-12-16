/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nomina.Constructores;

/**
 *
 * @author andre
 */
abstract class Empleado {
    protected String nombre;
    protected String idEmpleado;
    protected double salario;

    public Empleado(String nombre, String idEmpleado, double salario) {
        this.nombre = nombre;
        this.idEmpleado = idEmpleado;
        this.salario = salario;
    }

    public String mostrarInformacion() {
        return "nombre=" + nombre + "\nidEmpleado=" + idEmpleado + "\nsalario=" + salario;
    }
    public double aumentarSalarioBase(double porcentaje){
        return porcentaje;
    }
    abstract double calcularNomina();
    
}
