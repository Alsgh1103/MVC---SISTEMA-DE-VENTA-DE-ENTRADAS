package modelo;

import java.io.Serializable;

/**
 * @author alex_
 */
public class Entrada implements Serializable {
    private static final long serialVersionUID = 1L;
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