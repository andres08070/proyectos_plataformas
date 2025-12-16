/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package nomina.Constructores;

/**
 *
 * @author andre
 */
public class EmpleadoAsalariado extends Empleado {
    double deduccionImpuestos;

    public EmpleadoAsalariado(double deduccionImpuestos, String nombre, String idEmpleado, double salario) {
        super(nombre, idEmpleado, salario);
        this.deduccionImpuestos = deduccionImpuestos;
    }

    @Override
    public double calcularNomina() {
        double MontoDeduccion=salario*deduccionImpuestos;
        return salario-MontoDeduccion;
        
        
    }

    @Override
    public double aumentarSalarioBase(double porcentaje) { 
            double aumen=salario+(salario*porcentaje);
            salario=aumen;
            return aumen;
    }
    @Override
    public String mostrarInformacion() {
        return "Empleado asalariado nombre: " + nombre + ", idEmpleado: " + idEmpleado + ", salario: " + salario;
    }
}
