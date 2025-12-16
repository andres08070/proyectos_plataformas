package punto3;

public class Cliente {
    public long documento;
    public String nombre;
    public String correo;
    public int celular;
    public String direccion;

    public Cliente(long documento, String nombre, String correo, int celular, String direccion) {
        this.documento = documento;
        this.nombre = nombre;
        this.correo = correo;
        this.celular = celular;
        this.direccion = direccion;
    }
}
