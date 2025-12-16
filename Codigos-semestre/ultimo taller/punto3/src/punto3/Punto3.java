package punto3;

import java.util.ArrayList;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.text.NumberFormat;
import java.util.Locale;

public class Punto3 {

    public static void main(String[] args) {
//----------------------------------------creando caracteristicas-----------------------------------------//
        Scanner sc = new Scanner(System.in);
        int N;
        long documento;
        String nombre;
        String correo;
        int celular;
        String direccion;
        long numeroCuenta;
        double saldo;
        double valorSobregiro;
        int tipoCuenta;
        double totalIntereses = 0.0;
        double totalSaldos = 0.0;
        NumberFormat formatoNumero = NumberFormat.getNumberInstance(new Locale("es", "CO"));
        formatoNumero.setMinimumFractionDigits(2);
        formatoNumero.setMaximumFractionDigits(2);
        ArrayList<Cliente> clientes = new ArrayList<>();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        String fechaActual = LocalDate.now().format(formato);
//------------------------------------------------------------------------//
        System.out.print("Ingrese el numero de cuentas a procesar: ");
        N = sc.nextInt();

        for (int i = 0; i < N; i++) {
            System.out.println("\n--- Registro del cliente #" + (i + 1) + " ---");

            System.out.print("Documento: ");
            documento = sc.nextLong();
            sc.nextLine();
            System.out.print("Nombre: ");
            nombre = sc.nextLine();
            System.out.print("Correo: ");
            correo = sc.nextLine();
            System.out.print("Celular: ");
            celular = sc.nextInt();
            sc.nextLine();
            System.out.print("Direccion: ");
            direccion = sc.nextLine();

            System.out.print("Numero de cuenta: ");
            numeroCuenta = sc.nextLong();

            System.out.println("===============");
            System.out.print("""
                             Tipo de cuenta:
                             1 = Ahorros
                             2 = Corriente
                             Seleccione una opcion: """);
            tipoCuenta = sc.nextInt();

            System.out.println("===============");
            System.out.print("Saldo actual: ");
            saldo = sc.nextDouble();

            Cliente cliente;

            if (tipoCuenta == 1) {
                cliente = new Cuenta(documento, nombre, correo, celular, direccion,
                        numeroCuenta, fechaActual, saldo);
            } else {
                System.out.print("Valor de sobregiro permitido: ");
                valorSobregiro = sc.nextDouble();

                cliente = new CuentaCorriente(documento, nombre, correo, celular, direccion,
                        numeroCuenta, fechaActual, saldo, valorSobregiro);
            }

            clientes.add(cliente);
        }

        System.out.println("\n===== DATOS DE TODAS LAS CUENTAS =====");

        for (Cliente c : clientes) {
            System.out.println("===============");
            System.out.printf("Cliente: %s\n", c.nombre);
            System.out.printf("Documento: %d\n", c.documento);
            System.out.printf("Correo: %s\n", c.correo);
            System.out.printf("Celular: %d\n", c.celular);
            System.out.printf("Direccion: %s\n", c.direccion);

            if (c instanceof Cuenta) {
                Cuenta cuenta = (Cuenta) c;
                double interes = cuenta.calcularInteres();
                double saldoNuevo = cuenta.saldoConInteres();

                System.out.println("--- CUENTA DE AHORROS ---");
                System.out.printf("Numero de cuenta: %d\n", cuenta.getNumeroCuenta());
                System.out.printf("Fecha de apertura: %s\n", cuenta.getFechaApertura());
                System.out.printf("Saldo anterior: %s\n", formatoNumero.format(cuenta.getSaldo()));
                System.out.printf("Tasa aplicada: %.2f%%\n", cuenta.getPorcentajeInteres() * 100);
                System.out.printf("Interes generado: %s\n", formatoNumero.format(interes));
                System.out.printf("Saldo nuevo: %s\n", formatoNumero.format(saldoNuevo));

                totalIntereses += interes;
                totalSaldos += saldoNuevo;

            } else if (c instanceof CuentaCorriente) {
                CuentaCorriente cc = (CuentaCorriente) c;
                double interes = cc.calcularInteres();
                double saldoNuevo = cc.saldoConInteres();

                System.out.println("--- CUENTA CORRIENTE ---");
                System.out.printf("Numero de cuenta: %d\n", cc.getNumeroCuenta());
                System.out.printf("Fecha de apertura: %s\n", cc.getFechaApertura());
                System.out.printf("Saldo anterior: %s\n", formatoNumero.format(cc.getSaldo()));
                System.out.printf("Sobregiro permitido: %s\n", formatoNumero.format(cc.getValorSobregiro()));
                System.out.printf("Tasa aplicada: %.2f%%\n", cc.getPorcentajeInteres() * 100);
                System.out.printf("Interes generado: %s\n", formatoNumero.format(interes));
                System.out.printf("Saldo nuevo: %s\n", formatoNumero.format(saldoNuevo));

                totalIntereses += interes;
                totalSaldos += saldoNuevo;
            }
        }

        System.out.println("\n===== RESUMEN GENERAL =====");
        System.out.printf("Total intereses generados: %s\n", formatoNumero.format(totalIntereses));
        System.out.printf("Total saldos nuevos: %s\n", formatoNumero.format(totalSaldos));

        sc.close();
    }
}
