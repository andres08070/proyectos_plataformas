/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package punto1;

import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Punto1 {
    public static double obtenerTasaPorTipo(int tipo) {
        switch (tipo) {
            case 1:
                return 0.015;
            case 2:
                return 0.017;
            case 3:
                return 0.016;
        }
        return 0;
    }

    public static String obtenerNombreTipo(int tipo) {
        switch (tipo) {
            case 1:
                return "Ahorro Diario";
            case 2:
                return "Cuenta Joven";
            case 3:
                return "Tradicional";
        }
        return "";
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        int N;
        long numeroCuenta;
        int tipoCuenta;
        double saldo;
        double tasa;
        double valorInteres;
        double saldoNuevo;
        double valorTotalIntereses = 0.0;
        double valorTotalSaldos = 0.0;

        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String fechaActual = LocalDate.now().format(formato);

        System.out.print("Ingrese el numero de cuentas a procesar: ");
        N = sc.nextInt();

        System.out.println("\n===== PROCESO DE LIQUIDACION DE INTERESES =====");

        for (int i = 0; i < N; i++) {
            System.out.println("\n--- Cuenta #" + (i + 1) + " ---");

            System.out.print("Numero de cuenta: ");
            numeroCuenta = sc.nextLong();

            do {               
                System.out.println("===============");
                System.out.print("""
                                 Tipo de cuenta:
                                 1 = Ahorro Diario
                                 2 = Cuenta Joven
                                 3 = Tradicional
                                 Seleccione una opcion: """);
                tipoCuenta = sc.nextInt();
                if(tipoCuenta > 3 || tipoCuenta < 1){
                    System.out.println("**********************");
                    System.out.println("Tipo de cuenta no valido");
                    System.out.println("**********************");
                }
            } while (tipoCuenta > 3 || tipoCuenta < 1);

            System.out.println("===============");
            System.out.print("Saldo actual: ");
            saldo = sc.nextDouble();

            tasa = obtenerTasaPorTipo(tipoCuenta);
            valorInteres = saldo * tasa;
            saldoNuevo = saldo + valorInteres;

            valorTotalIntereses += valorInteres;
            valorTotalSaldos += saldoNuevo;

            System.out.println("===============");
            System.out.println("\nDatos de la cuenta:");
            System.out.println("-------------------");
            System.out.printf("Numero de cuenta: %d\n", numeroCuenta);
            System.out.printf("Fecha de apertura: %s\n", fechaActual);
            System.out.printf("Tipo de cuenta: %s\n", obtenerNombreTipo(tipoCuenta));
            System.out.printf("Saldo anterior: %.2f\n", saldo);
            System.out.printf("Tasa aplicada: %.2f%%\n", tasa * 100);
            System.out.printf("Valor del interes: %.2f\n", valorInteres);
            System.out.printf("Saldo nuevo: %.2f\n", saldoNuevo);
            System.out.println("===============");
        }

        System.out.println("\n===== RESUMEN GENERAL =====");
        System.out.printf("Total intereses generados: %.2f\n", valorTotalIntereses);
        System.out.printf("Total saldos nuevos: %.2f\n", valorTotalSaldos);

        sc.close();
    }
}
