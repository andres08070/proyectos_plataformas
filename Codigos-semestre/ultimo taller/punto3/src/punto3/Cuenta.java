package punto3;

public class Cuenta extends Cliente {
    private long numeroCuenta;
    private String fechaApertura;
    private double saldo;
    private double porcentajeInteres;

    public Cuenta(long documento, String nombre, String correo, int celular, String direccion,
                  long numeroCuenta, String fechaApertura, double saldo) {
        super(documento, nombre, correo, celular, direccion);
        this.numeroCuenta = numeroCuenta;
        this.fechaApertura = fechaApertura;
        this.saldo = saldo;
        this.porcentajeInteres = 0.015;
    }

    public double calcularInteres() {
        return saldo * porcentajeInteres;
    }

    public double saldoConInteres() {
        return saldo + calcularInteres();
    }

    public long getNumeroCuenta() {
        return numeroCuenta;
    }

    public String getFechaApertura() {
        return fechaApertura;
    }

    public double getSaldo() {
        return saldo;
    }

    public double getPorcentajeInteres() {
        return porcentajeInteres;
    }
}
