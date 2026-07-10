package controlador;

import coleccion.ColeccionPersonas;
import coleccion.ColeccionConciertos;
import coleccion.ColeccionVentas;
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
    private ColeccionVentas coleccionVentas;
    private Persona usuarioLogueado;
    private Concierto conciertoSeleccionado;

    public ControladorPrincipal() {
        persistencia.DatosSistema datosGuardados = persistencia.GestorSerializacion.cargar();

        if (datosGuardados != null) {
            this.coleccionPersonas = datosGuardados.personas;
            this.coleccionConciertos = datosGuardados.conciertos;
            this.coleccionVentas = datosGuardados.ventas;
        } else {
            this.coleccionPersonas = new ColeccionPersonas();
            this.coleccionConciertos = new ColeccionConciertos();
            this.coleccionVentas = new ColeccionVentas();

            Usuario admin = new Usuario("99999999", "Super", "Administrador", "admin", "admin", "ADM-001");
            this.coleccionPersonas.guardarPersona(admin);
            this.guardarEstado();
        }
    }

    public void guardarEstado() {
        persistencia.DatosSistema datosActuales = new persistencia.DatosSistema(
                this.coleccionPersonas,
                this.coleccionConciertos,
                this.coleccionVentas);
        persistencia.GestorSerializacion.guardar(datosActuales);
    }

    public void guardarPersonas() {
        persistencia.GestorSerializacion.guardarPersonas(this.coleccionPersonas);
    }

    public void guardarConciertos() {
        persistencia.GestorSerializacion.guardarConciertos(this.coleccionConciertos);
    }

    public void guardarVentas() {
        persistencia.GestorSerializacion.guardarVentas(this.coleccionVentas);
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

    public void registrarNuevoCliente(String dni, String nombre, String apellido, String correo, String contrasena) {
        Cliente nuevoCliente = new Cliente(dni, nombre, apellido, correo, contrasena);
        coleccionPersonas.guardarPersona(nuevoCliente);
        guardarPersonas();
    }

    public void registrarNuevaPersona(String dni, String nombre, String apellido, String correo, String contrasena) {
        registrarNuevoCliente(dni, nombre, apellido, correo, contrasena);
    }

    public void registrarNuevaVenta(Venta v) {
        this.coleccionVentas.registrarVenta(v);
        guardarVentas();
        guardarConciertos();
        guardarPersonas();
    }

    public Object[][] getDatosZonasParaTabla() {
        ArrayList<Concierto> todos = coleccionConciertos.getTodosLosConciertos();

        int totalRows = 0;
        for (Concierto c : todos) {
            int zonasSize = c.getTodasLasZonas().size();
            totalRows += (zonasSize == 0) ? 1 : zonasSize;
        }

        if (totalRows == 0)
            return new Object[0][0];

        Object[][] datos = new Object[totalRows][5];
        int index = 0;

        for (Concierto c : todos) {
            ArrayList<Zona> zonas = c.getTodasLasZonas();
            if (zonas.isEmpty()) {
                datos[index][0] = c.getNombre();
                datos[index][1] = c.getFecha().toString();
                datos[index][2] = "Sin zonas";
                datos[index][3] = "-";
                datos[index][4] = "-";
                index++;
            } else {
                for (Zona z : zonas) {
                    datos[index][0] = c.getNombre();
                    datos[index][1] = c.getFecha().toString();
                    datos[index][2] = z.getNombre();
                    datos[index][3] = z.getCapacidadDisponible();
                    datos[index][4] = z.getCapacidadTotal() - z.getCapacidadDisponible();
                    index++;
                }
            }
        }
        return datos;
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

    public ColeccionConciertos getColeccionConciertos() {
        return coleccionConciertos;
    }

    public ColeccionPersonas getColeccionPersonas() {
        return coleccionPersonas;
    }

    public ColeccionVentas getColeccionVentas() {
        return coleccionVentas;
    }

    public Persona getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public void setUsuarioLogueado(Persona usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
    }

    public boolean eliminarConciertoGlobal(Concierto concierto) {
        try {
            this.coleccionConciertos.eliminarConcierto(concierto);
            guardarConciertos();
            return true;
        } catch (IllegalStateException e) {
            javax.swing.JOptionPane.showMessageDialog(null, e.getMessage(), "Operación denegada",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean eliminarZonaDeConcierto(Concierto concierto, String nombreZona) {
        try {
            concierto.eliminarZona(nombreZona);
            guardarConciertos();
            return true;
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(null, e.getMessage(), "Error al eliminar",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
