package controlador;

import coleccion.ColeccionPersonas;
import coleccion.ColeccionConciertos;
import modelo.Concierto;
import modelo.Venta;
import modelo.Cliente;
import modelo.Persona;
import modelo.Usuario;
import modelo.Zona;
import java.util.ArrayList;

public class ControladorPrincipal {
    private ColeccionPersonas coleccionPersonas;
    private ColeccionConciertos coleccionConciertos;
    private Persona usuarioLogueado;
    private Concierto conciertoSeleccionado;

    public ControladorPrincipal() {
        this.coleccionPersonas = new ColeccionPersonas();
        this.coleccionConciertos = new ColeccionConciertos();
        
    }

    public Persona login(String correo, String contrasena) {
        Persona p = coleccionPersonas.buscarPorCorreo(correo);
        if (p != null && p.validarContrasena(contrasena)) {
            this.usuarioLogueado = p;
            return p;
        }
        return null;
    }

    public void cerrarSesion() {
        if (this.usuarioLogueado != null) {
            this.usuarioLogueado.cerrarSesion();
            this.usuarioLogueado = null;
        }
    }

    public void registrarNuevoCliente(String dni, String nombre, String apellido, String correo, String contrasena, boolean esSocio) {
        Cliente nuevo = new Cliente(dni, nombre, apellido, correo, contrasena, esSocio);
        coleccionPersonas.guardarPersona(nuevo);
    }

    public void registrarNuevaPersona(String dni, String nombre, String apellido, String correo, String contrasena, boolean esSocio) {
        registrarNuevoCliente(dni, nombre, apellido, correo, contrasena, esSocio);
    }

    public void registrarNuevoConcierto(String nombre, java.time.LocalDate fecha) {
        Concierto nuevo = new Concierto(nombre, fecha);
        coleccionConciertos.guardarConcierto(nuevo);
        if (this.conciertoSeleccionado == null) {
            this.conciertoSeleccionado = nuevo;
        }
    }

    public void registrarNuevaVenta(Venta v) {
        if (conciertoSeleccionado != null) {
            conciertoSeleccionado.registrarVenta(v);
        }
    }

    public Concierto getConcierto() {
        return conciertoSeleccionado;
    }

    public Concierto getConciertoSeleccionado() {
        return conciertoSeleccionado;
    }

    public void setConciertoSeleccionado(Concierto c) {
        this.conciertoSeleccionado = c;
    }

    public ArrayList<Concierto> getTodosLosConciertos() {
        return coleccionConciertos.getTodosLosConciertos();
    }

    public ColeccionPersonas getColeccionPersonas() {
        return coleccionPersonas;
    }

    public Persona getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public void setUsuarioLogueado(Persona usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
    }
}
