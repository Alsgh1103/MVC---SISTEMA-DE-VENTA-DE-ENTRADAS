/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;
import java.io.Serializable;

/**
 *
 * @author alex_
 */
public class Cliente extends Persona implements java.io.Serializable{
    private static final long serialVersionUID = 1L;
    private int puntos;
    private Tarjeta tarjeta;

    public Cliente(String dni, String nombres, String apellidos, String correo, String contrasena) {
        super(dni, nombres, apellidos, correo, contrasena);
        this.puntos = 0;
    }

    public boolean ingresar(String usuario, String clave) {
        return true;
    }

    public int getPuntos() { return puntos; }
    public void setPuntos(int puntos) { this.puntos = puntos; }

    public Tarjeta getTarjeta() { return tarjeta; }
    public void setTarjeta(Tarjeta tarjeta) { this.tarjeta = tarjeta; }
    
    public void gastarPuntos(int cantidad) {
        if (cantidad <= this.puntos) {
            this.puntos -= cantidad;
        }
    }

    public void acumularPuntosPorCompra(int cantidadEntradas) {
        // Regla de negocio: 10 puntos por entrada
        this.puntos += (cantidadEntradas * 10);
    }
}
