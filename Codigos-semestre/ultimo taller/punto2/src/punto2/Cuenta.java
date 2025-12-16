/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package punto2;

/**
 *
 * @author andre
 */
public class Cuenta {

    private long numeroCuenta;
    private String fechaApertura;
    private int tipoCuenta;
    private double saldo;

    public Cuenta(long numeroCuenta, String fechaApertura, int tipoCuenta, double saldo) {
        this.numeroCuenta = numeroCuenta;
        this.fechaApertura = fechaApertura;
        this.tipoCuenta = tipoCuenta;
        this.saldo = saldo;
    }

    public long getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(long numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public String getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(String fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public int getTipoCuenta() {
        return tipoCuenta;
    }

    public void setTipoCuenta(int tipoCuenta) {
        this.tipoCuenta = tipoCuenta;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public double calcularInteres(boolean tas) {
        double tasa;
        
        switch (tipoCuenta) {
            case 1:
                tasa = 0.015;
                break;
            case 2:
                tasa = 0.017;
                break;
            case 3:
                tasa = 0.016;
                break;
            default:
                tasa = 0.0;
        }
        if(tas){
            return tasa;
        }
        return saldo * tasa;
    }

    public double saldoConInteres() {
        return saldo + calcularInteres(false);
    }
}

