/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author alex_
 */

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Venta {
    private static int contadorVentas = 1;
    private String idTransaccion;
    private String fecha;
    private int cantidadEntradas;
    private int monto;
    private Cliente cliente;
    private Zona zona;
    private Tarjeta tarjeta;

    public Venta(int cantidad, double montoT, Cliente c, Zona z, Tarjeta t) {
        this.cantidadEntradas = cantidad;
        this.cliente = c;
        this.zona = z;
        this.tarjeta = t;
        this.monto = (int) calcularTotal();
        LocalDate hoy = LocalDate.now();
        this.fecha = hoy.toString();
        DateTimeFormatter formatoId = DateTimeFormatter.ofPattern("ddMMyy");
        this.idTransaccion = hoy.format(formatoId) + String.format("%03d", contadorVentas);
        contadorVentas++;
    }

    public boolean anular() {
        return true;
    }

    public static int getContadorVentas() {
        return contadorVentas;
    }

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public String getFecha() {
        return fecha;
    }

    public int getCantidadEntradas() {
        return cantidadEntradas;
    }

    public int getMonto() {
        return monto;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Zona getZona() {
        return zona;
    }

    public Tarjeta getTarjeta() {
        return tarjeta;
    }

    public boolean validarLimiteEntradas() {
        return tarjeta.puedeComprar(this.cantidadEntradas);
    }

    public double calcularTotal() {
        double total = zona.getPrecio() * cantidadEntradas;
        if (cliente.isSocio()) {
            total *= 0.7;
        }
        return total;
    }

    public boolean procesarCompra(int cvvIngresadoUsuario) {
        if (!zona.verificarDisponibilidad(cantidadEntradas)) {
            return false;
        }
        if (!validarLimiteEntradas()) {
            return false;
        }
        if (tarjeta.registrarCompra(cantidadEntradas, cvvIngresadoUsuario)) {
            monto = (int) calcularTotal();
            return true;
        }
        return false;
    }

    public ArrayList<Entrada> generarEntradas() {
        ArrayList<Entrada> entradasGeneradas = new ArrayList<>();

        for (int i = 1; i <= this.cantidadEntradas; i++) {
            Entrada nueva = new Entrada(i, "VENDIDA");
            entradasGeneradas.add(nueva);
        }
        return entradasGeneradas;
    }
}
