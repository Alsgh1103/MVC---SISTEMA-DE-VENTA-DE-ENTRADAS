package controlador;

import coleccion.ColeccionPersonas;
import coleccion.ColeccionConciertos;
import modelo.Concierto;
import modelo.Venta;
import modelo.Cliente;
import modelo.Persona;
import modelo.Usuario;
import modelo.Zona;
import modelo.Tarjeta;
import java.util.ArrayList;

public class ControladorPrincipal {
    private ColeccionPersonas coleccionPersonas;
    private ColeccionConciertos coleccionConciertos;
    private Persona usuarioLogueado;
    private Concierto conciertoSeleccionado;

    public ControladorPrincipal() {
        this.coleccionPersonas = new ColeccionPersonas();
        this.coleccionConciertos = new ColeccionConciertos();
        Usuario admin = new Usuario("99999999", "Administrador", "Sistema", "admin@gmail.com", "admin123", "ADM001");
        this.coleccionPersonas.guardarPersona(admin);
        Usuario adminRapido = new Usuario("00000000", "Admin", "Pruebas", "admin", "admin", "ADM002");
        this.coleccionPersonas.guardarPersona(adminRapido);
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

    public void registrarNuevaVenta(Venta v) {
        if (conciertoSeleccionado != null) {
            conciertoSeleccionado.registrarVenta(v);
        }
    }
    
    public Object[][] getDatosZonasParaTabla() {
        ArrayList<Concierto> todos = coleccionConciertos.getTodosLosConciertos();
        
        int totalRows = 0;
        for (Concierto c : todos) {
            int zonasSize = c.getTodasLasZonas().size();
            totalRows += (zonasSize == 0) ? 1 : zonasSize;
        }

        if (totalRows == 0) return new Object[0][0];

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

    public Persona getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public void setUsuarioLogueado(Persona usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
    }
    
    public Venta procesarCompraConcierto(String nombreZona, int cantidad, Tarjeta tarjetaUsar) throws Exception {
        if (conciertoSeleccionado == null) throw new Exception("Seleccione un concierto primero.");
        
        Zona zonaEncontrada = conciertoSeleccionado.buscarZonaPorNombre(nombreZona);
        if (zonaEncontrada == null) throw new Exception("Zona no encontrada en el sistema.");
        
        if (cantidad <= 0) throw new Exception("La cantidad de entradas debe ser mayor a 0.");

        Persona usuario = this.usuarioLogueado;
        if (!(usuario instanceof Cliente)) throw new Exception("El administrador no puede realizar compras.");
        
        Cliente cliente = (Cliente) usuario;

        Venta nuevaVenta = new Venta(cantidad, cliente, zonaEncontrada, tarjetaUsar);
        
        if (nuevaVenta.procesarCompra(tarjetaUsar.getCVV())) {
            registrarNuevaVenta(nuevaVenta);
            return nuevaVenta; 
        } else {
            throw new Exception("La transacción fue rechazada. Verifique la disponibilidad, su límite de entradas o la tarjeta.");
        }
    }

    public boolean eliminarConciertoGlobal(Concierto concierto) {
        try {
            this.coleccionConciertos.eliminarConcierto(concierto);
            return true;
        } catch (IllegalStateException e) {
            javax.swing.JOptionPane.showMessageDialog(null, e.getMessage(), "Operación denegada", javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean eliminarZonaDeConcierto(Concierto concierto, String nombreZona) {
        try {
            concierto.eliminarZona(nombreZona);
            return true;
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(null, e.getMessage(), "Error al eliminar", javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
