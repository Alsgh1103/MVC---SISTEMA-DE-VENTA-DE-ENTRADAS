/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author alex_
 */
import java.util.ArrayList;

public class Usuario extends Persona {
    private String codigoUsuario;
    private boolean estado;

    public Usuario(String dni, String nombre, String apellido, String correo, String contrasena, String codigoAdmin) {
        super(dni, nombre, apellido, correo, contrasena);
        this.codigoUsuario = codigoAdmin;
        this.estado = true;
    }

    public boolean registrarZonas() {
        return true;
    }

    public boolean isEstado() { return estado; }
    public void setEstado(boolean estado) { this.estado = estado; }

    public boolean validarCodigoUsuario(String codigoIngresado) {
        return codigoIngresado.equals(codigoUsuario);
    }

    public void consultarVentas(ArrayList<Venta> todasLasVentas) {
        for (int i = 0; i < todasLasVentas.size(); i++) {
            Venta ventaActual = todasLasVentas.get(i);
            System.out.println("idTransaccion: " + ventaActual.getIdTransaccion() + "\n" +
                    "Fecha: " + ventaActual.getFecha() + "\n" +
                    "Monto: " + ventaActual.getMonto() + "\n");
        }
    }

    public void consultarZonas(ArrayList<Zona> todasLasZonas) {
        for (int i = 0; i < todasLasZonas.size(); i++) {
            Zona zonaActual = todasLasZonas.get(i);
            System.out.println("Nombre: " + zonaActual.getNombre() + "\n" +
                    "Capacidad Total: " + zonaActual.getCapacidadTotal() + "\n" +
                    "Cantidad Disponible: " + zonaActual.getCapacidadDisponible() + "\n" +
                    "Precio :" + zonaActual.getPrecio() + "\n");
        }
    }
}
