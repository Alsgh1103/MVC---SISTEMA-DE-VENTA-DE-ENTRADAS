package controlador;

import vista.FrmMenuPrincipal;
import vista.FrmLogin;
import vista.FrmCliente;
import modelo.Persona;
import modelo.Cliente;
import modelo.Concierto;
import modelo.Venta;
import modelo.Zona;
import coleccion.ColeccionVentas;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JLabel;
import javax.swing.JComboBox;


  // Controlador para la vista del Menú Principal (FrmMenuPrincipal).
  
public class ControladorMenuPrincipal {
    private FrmMenuPrincipal vistaMenu;
    private ControladorPrincipal contextoCentral;

    
    public ControladorMenuPrincipal(FrmMenuPrincipal vistaMenu, ControladorPrincipal contextoCentral) {
        this.vistaMenu = vistaMenu;
        this.contextoCentral = contextoCentral;
    }

   
    public void cargarDatosCliente(JLabel lblBienvenida, JLabel lblPuntos, JLabel lblConcierto, JComboBox<Concierto> cbxConcierto) {
        Persona usuario = contextoCentral.getUsuarioLogueado();
        if (usuario != null) {
            lblBienvenida.setText("Bienvenido: " + usuario.getNombres() + " " + usuario.getApellidos());
            
            if (usuario instanceof Cliente) {
                Cliente c = (Cliente) usuario;
                lblPuntos.setText("Puntos: " + c.getPuntos());
            } else {
                lblPuntos.setText("Puntos: N/A");
                lblConcierto.setText("Administración");
            }
            
            cbxConcierto.removeAllItems();
            for (Concierto con : contextoCentral.getTodosLosConciertos()) {
                cbxConcierto.addItem(con);
            }
            
            if (contextoCentral.getConciertoSeleccionado() != null) {
                cbxConcierto.setSelectedItem(contextoCentral.getConciertoSeleccionado());
            }
        }
    }

    
     // Guarda el concierto seleccionado por el usuario en el contexto central.
     
     
    public void seleccionarConcierto(Concierto seleccion) {
        if (seleccion != null) {
            contextoCentral.setConciertoSeleccionado(seleccion);
        }
    }

    
     // Muestra las zonas, precios y disponibilidad del concierto actualmente seleccionado.
     
    public void verZonas() {
        Concierto conciertoSeleccionado = contextoCentral.getConciertoSeleccionado();
        if (conciertoSeleccionado != null) {
            StringBuilder sb = new StringBuilder("Zonas del Concierto: " + conciertoSeleccionado.getNombre() + "\n\n");
            for (Zona z : conciertoSeleccionado.getTodasLasZonas()) {
                sb.append("- ").append(z.getNombre())
                  .append(": S/ ").append(z.getPrecio())
                  .append(" (Disponibles: ").append(z.getCapacidadDisponible()).append("/").append(z.getCapacidadTotal()).append(")\n");
            }
            JOptionPane.showMessageDialog(vistaMenu, sb.toString());
        } else {
            JOptionPane.showMessageDialog(vistaMenu, "No hay concierto seleccionado.");
        }
    }

    
     // Muestra el historial de compras y puntos acumulados del cliente activo.
     
    public void verMisCompras() {
        Persona usuario = contextoCentral.getUsuarioLogueado();
        if (usuario instanceof Cliente) {
            Cliente c = (Cliente) usuario;
            StringBuilder sb = new StringBuilder("Historial de Compras de " + c.getNombres() + "\n");
            sb.append("Puntos acumulados: ").append(c.getPuntos()).append("\n\n");
            sb.append("Compras realizadas:\n");
            
            ColeccionVentas coleccionVentas = contextoCentral.getColeccionVentas();
            ArrayList<Venta> ventasCliente = coleccionVentas.buscarVentasPorCliente(c.getDni());
            
            boolean tieneVentas = !ventasCliente.isEmpty();
            for (Venta v : ventasCliente) {
                String nombreConcierto = "Concierto Desconocido";
                // Buscamos el nombre del concierto correspondiente a la venta
                for (Concierto con : contextoCentral.getColeccionConciertos().getTodosLosConciertos()) {
                    if (con.getTodasLasVentas().contains(v)) {
                        nombreConcierto = con.getNombre();
                        break;
                    }
                }
                
                sb.append("- ").append(nombreConcierto)
                  .append(" | ").append(v.getZona().getNombre())
                  .append(" | Cantidad: ").append(v.getCantidadEntradas())
                  .append(" | Total: S/ ").append(v.getMonto())
                  .append(" | Transacción: ").append(v.getIdTransaccion()).append("\n");
            }
            
            if (!tieneVentas) {
                sb.append("No has realizado ninguna compra todavía.");
            }
            JOptionPane.showMessageDialog(vistaMenu, sb.toString());
        } else {
            JOptionPane.showMessageDialog(vistaMenu, "Esta opción solo está disponible para Clientes.");
        }
    }

    
     // Dirige al usuario al flujo de compra de entradas, abriendo FrmCliente y cerrando el menú.
     
    public void comprarEntradas() {
        FrmCliente cliente = new FrmCliente(contextoCentral);
        cliente.setVisible(true);
        vistaMenu.dispose();
    }

    
     // Cierra la sesión activa del usuario y regresa al portal de inicio de sesión (FrmLogin).
     
    public void cerrarSesion() {
        contextoCentral.cerrarSesion();
        FrmLogin login = new FrmLogin();
        ControladorLogin ctrlLogin = new ControladorLogin(login, contextoCentral);
        login.setVisible(true);
        vistaMenu.dispose();
    }
}
