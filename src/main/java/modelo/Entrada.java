package modelo;

/**
 * @author alex_
 */
public class Entrada {
    private int numero;
    private String estado;

    public Entrada(int numero, String estado) {
        this.numero = numero;
        this.estado = estado;
    }

    public boolean vender() {
        return true;
    }

    public boolean liberar() {
        return true;
    }

    public int getNumero() {
        return numero;
    }

    public String getEstado() {
        return estado;
    }
}