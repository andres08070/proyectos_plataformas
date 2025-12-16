package punto3;

public class CuentaCorriente extends Cliente {
    private long numeroCuenta;
    private String fechaApertura;
    private double saldo;
    private double porcentajeInteres;
    private double valorSobregiro;

    public CuentaCorriente(long documento, String nombre, String correo, int celular, String direccion,
                           long numeroCuenta, String fechaApertura, double saldo, double valorSobregiro) {
        super(documento, nombre, correo, celular, direccion);
        this.numeroCuenta = numeroCuenta;
        this.fechaApertura = fechaApertura;
        this.saldo = saldo;
        this.valorSobregiro = valorSobregiro;
        this.porcentajeInteres = 0.008;
    }

    public double calcularInteres() {
        return (saldo - valorSobregiro) * porcentajeInteres;
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

    public double getValorSobregiro() {
        return valorSobregiro;
    }
}
