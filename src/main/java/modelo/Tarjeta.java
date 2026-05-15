/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author alex_
 */
public class Tarjeta {
    private int numero;
    private String nombre;
    private String fecha;
    private int CVV;
    private int cantidadComprada;

    public Tarjeta(int numero, String nombre, String fecha, int CVV) {
        this.numero = numero;
        this.nombre = nombre;
        this.fecha = fecha;
        this.CVV = CVV;
        this.cantidadComprada = 0;
    }

    public int getNumero() {
        return numero;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public int getCVV() {
        return CVV;
    }

    public int getCantidadComprada() {
        return cantidadComprada;
    }

    public boolean validarTarjeta(int cvvIngresado) {
        return cvvIngresado == CVV;
    }

    public boolean puedeComprar(int cantidadNueva) {
        return (this.cantidadComprada + cantidadNueva) <= 4;
    }

    public boolean registrarCompra(int cantidadNueva, int cvvIngresado) {
        if (!puedeComprar(cantidadNueva)) {
            return false;
        }
        if (!validarTarjeta(cvvIngresado)) {
            return false;
        }
        cantidadComprada += cantidadNueva;
        return true;
    }
}
