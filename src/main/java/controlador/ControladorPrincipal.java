/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.Concierto;
import modelo.Venta;
import modelo.Cliente;

/**
 *
 * @author alex_
 */
public class ControladorPrincipal {
    private Concierto concierto;

    public ControladorPrincipal() {
        this.concierto = new Concierto("Gran Concierto", java.time.LocalDate.now());
    }

    public void registrarNuevaVenta(Venta v) {
        concierto.registrarVenta(v);
    }

    public Concierto getConcierto() {
        return concierto;
    }

    public void registrarNuevaPersona(String dni, String nombre, String apellido, String correo, String contrasena,
            boolean esSocio) {
        Cliente nuevo = new Cliente(dni, nombre, apellido, correo, contrasena, false);
        this.getConcierto().agregarPersona(nuevo);
        javax.swing.JOptionPane.showInputDialog("Registrado con éxito");
    }
}
