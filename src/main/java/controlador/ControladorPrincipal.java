/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.Sistema;
import modelo.Venta;
import modelo.Cliente;

/**
 *
 * @author alex_
 */
public class ControladorPrincipal {
    private Sistema sistema;

    public ControladorPrincipal() {
        this.sistema = new Sistema();
    }

    public void registrarNuevaVenta(Venta v) {
        sistema.registrarVenta(v);
    }

    public Sistema getSistema() {
        return sistema;
    }

    public void registrarNuevaPersona(String dni, String nombre, String apellido, String correo, String contrasena,
            boolean esSocio) {
        Cliente nuevo = new Cliente(dni, nombre, apellido, correo, contrasena, false);
        this.getSistema().agregarPersona(nuevo);
        javax.swing.JOptionPane.showInputDialog("Registrado con éxito");
    }
}
