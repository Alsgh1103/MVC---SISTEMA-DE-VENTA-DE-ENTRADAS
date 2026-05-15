/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author alex_
 */
public class Cliente extends Persona {
    private boolean esSocio;
    private int puntos;
    private Tarjeta tarjeta;

    public Cliente(String dni, String nombres, String apellidos, String correo, String contrasena, boolean esSocio) {
        super(dni, nombres, apellidos, correo, contrasena);
        this.esSocio = esSocio;
        this.puntos = 0;
    }

    public boolean ingresar(String usuario, String clave) {
        return true;
    }

    public int getPuntos() { return puntos; }
    public void setPuntos(int puntos) { this.puntos = puntos; }

    public Tarjeta getTarjeta() { return tarjeta; }
    public void setTarjeta(Tarjeta tarjeta) { this.tarjeta = tarjeta; }

    public boolean isSocio() {
        return esSocio;
    }

    public void setEsSocio(boolean esSocio) {
        this.esSocio = esSocio;
    }
}
