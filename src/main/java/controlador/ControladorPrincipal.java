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
import modelo.Tarjeta;
import java.util.ArrayList;

public class ControladorPrincipal {
    private ColeccionPersonas coleccionPersonas;
    private ColeccionConciertos coleccionConciertos;
    private ColeccionVentas coleccionVentas;
    private Persona usuarioLogueado;
    private Concierto conciertoSeleccionado;

    public ControladorPrincipal() {
        this.coleccionPersonas = new ColeccionPersonas();
        this.coleccionConciertos = new ColeccionConciertos();
        this.coleccionVentas = new ColeccionVentas();
        Usuario admin = new Usuario("99999999", "Administrador", "Sistema", "admin@gmail.com", "admin123", "ADM001");
        this.coleccionPersonas.guardarPersona(admin);
        Usuario adminRapido = new Usuario("00000000", "Admin", "Pruebas", "admin", "admin", "ADM002");
        this.coleccionPersonas.guardarPersona(adminRapido);

        // Cliente de prueba para saltarse la validación por correo
        Cliente clientePruebas = new Cliente("87654321", "Maria", "Pruebas", "maria@pruebas.com", "maria123");
        this.coleccionPersonas.guardarPersona(clientePruebas);
        
        // Cliente rápido de pruebas
        Cliente clienteRapido = new Cliente("11111111", "Cliente", "Pruebas", "cliente", "cliente");
        this.coleccionPersonas.guardarPersona(clienteRapido);

        Concierto conciertoPrueba = new Concierto("Megadeth en Lima", java.time.LocalDate.now().plusDays(30));
        conciertoPrueba.registrarZona("VIP",      50,  350);
        conciertoPrueba.registrarZona("Platinum", 100, 200);
        conciertoPrueba.registrarZona("General",  200, 100);
        this.coleccionConciertos.guardarConcierto(conciertoPrueba);
        this.conciertoSeleccionado = conciertoPrueba;
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
        Cliente nuevo = new Cliente(dni, nombre, apellido, correo, contrasena);
        coleccionPersonas.guardarPersona(nuevo);
    }

    public void registrarNuevaPersona(String dni, String nombre, String apellido, String correo, String contrasena) {
        registrarNuevoCliente(dni, nombre, apellido, correo, contrasena);
    }

    public void registrarNuevaVenta(Venta v) {
        if (conciertoSeleccionado != null) {
            conciertoSeleccionado.registrarVenta(v);
        }
        this.coleccionVentas.registrarVenta(v);
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

    public ColeccionVentas getColeccionVentas() {
        return coleccionVentas;
    }

    public Persona getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public void setUsuarioLogueado(Persona usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
    }
    
    // ==========================================================
    // INSTANCIACIÓN DE CONTROLADORES (MÓDULO CLIENTE / TARJETA)
    // ==========================================================
    // Se sustituye la antigua lógica de control directo (procesarCompraConcierto)
    // por la instanciación de los nuevos controladores, inyectando las dependencias
    // correctas (vistas y colecciones centralizadas).

    public ControladorComprarEntradas crearControladorCliente(vista.FrmComprarEntradas vista) {
        return new ControladorComprarEntradas(this, vista, this.coleccionVentas);
    }

    public ControladorTarjeta crearControladorTarjeta(vista.FrmTarjeta vista) {
        return new ControladorTarjeta(this, vista);
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
