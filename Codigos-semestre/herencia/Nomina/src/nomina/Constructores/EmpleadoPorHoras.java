/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nomina.Constructores;

/**
 *
 * @author andre
 */
public class EmpleadoPorHoras extends Empleado{
    double HorasTrabajadas,tarifaHora;

    public EmpleadoPorHoras(double HorasTrabajadas, String nombre, String idEmpleado, double salario,double tarifaHora) {
        super(nombre, idEmpleado, salario);
        this.HorasTrabajadas = HorasTrabajadas;
        this.tarifaHora = tarifaHora;
    }
    
    @Override
    public double aumentarSalarioBase(double porcentaje) {
        tarifaHora=tarifaHora+(tarifaHora*porcentaje);
        return tarifaHora;
    }

    @Override
    public double calcularNomina() {
        double apagar=HorasTrabajadas*tarifaHora;
        return apagar;
    }
    @Override
    public String mostrarInformacion() {
        return "Empleado por horas, nombre: " + nombre + ", idEmpleado: " + idEmpleado + ", Tarifa/Hora: " + tarifaHora+", Horas: "+HorasTrabajadas;
    }
}
