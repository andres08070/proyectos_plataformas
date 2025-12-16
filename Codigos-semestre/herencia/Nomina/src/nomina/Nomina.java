/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package nomina;

import java.util.Scanner;
import nomina.Constructores.EmpleadoAsalariado;
import nomina.Constructores.EmpleadoPorHoras;

/**
 *
 * @author andre
 */
public class Nomina {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String nombre;
        String idEmpleado;
        double salario;
        double tarifaHoras;
        double horasTrabajadas;
        System.out.println("Ingrese el nombre del Empleado Asalariado: ");
        nombre=sc.nextLine();
        System.out.println("Ingrese el ID: ");
        idEmpleado=sc.nextLine();
        System.out.println("Ingrese el salario base mensual: ");
        salario=sc.nextDouble();
        EmpleadoAsalariado Ea= new EmpleadoAsalariado(0.1,nombre,idEmpleado,salario);
        System.out.println("\n");
        
        
        System.out.println("Ingrese el nombre del Empleado por horas:");
        nombre=sc.nextLine();
        nombre=sc.nextLine();
        System.out.println("Ingrese el ID: ");
        idEmpleado=sc.nextLine();
        System.out.println("Ingrese la tarifa por hora: ");
        tarifaHoras=sc.nextDouble();
        System.out.println("Ingrese las horas trabajadas: ");
        horasTrabajadas=sc.nextDouble();
        EmpleadoPorHoras Eh = new EmpleadoPorHoras(horasTrabajadas,nombre,idEmpleado,0,tarifaHoras);
        
        System.out.println("---Informacion Inicial---");
        System.out.println(Ea.mostrarInformacion());
        System.out.println(Eh.mostrarInformacion());
        System.out.println("\n");
        System.out.println("---Aumento de salario 5%---");
        System.out.println("Salario con el aumento: "+Ea.aumentarSalarioBase(0.05));
        System.out.println("Horas con el aumento: "+Eh.aumentarSalarioBase(0.05));
        System.out.println("\n");
        System.out.println("---Datos con el aumento---");
        System.out.println(Ea.mostrarInformacion());
        System.out.println(Eh.mostrarInformacion());
        System.out.println("\n");
        System.out.println("---Calculo de nomina---");
        System.out.println("Nomina bruta del asalariado: ");
        System.out.println("Nomina del asalariado: "+Ea.calcularNomina());
        System.out.println("Nomina de el de horas: "+Eh.calcularNomina());
        
    }
    
}
