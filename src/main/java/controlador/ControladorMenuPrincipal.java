package controlador;

import vista.FrmMenuPrincipal;
import vista.FrmLogin;
import vista.FrmComprarEntradas;
import vista.FrmHistorial;
import modelo.Persona;
import modelo.Cliente;
import modelo.Concierto;
import modelo.Zona;
import javax.swing.JOptionPane;


  // Controlador para la vista del Menú Principal (FrmMenuPrincipal).
  
public class ControladorMenuPrincipal {
    private FrmMenuPrincipal vistaMenu;
    private ControladorPrincipal contextoCentral;

    
    public ControladorMenuPrincipal(FrmMenuPrincipal vistaMenu, ControladorPrincipal contextoCentral) {
        this.vistaMenu = vistaMenu;
        this.contextoCentral = contextoCentral;

        vistaMenu.btnComprar.addActionListener(e -> comprarEntradas());
        vistaMenu.btnCerrarSesion.addActionListener(e -> cerrarSesion());
        vistaMenu.btnMisCompras.addActionListener(e -> verMisCompras());
        vistaMenu.cbxConcierto.addActionListener(e -> {
            Object selected = vistaMenu.cbxConcierto.getSelectedItem();
            if (selected instanceof String) {
                String nombreConcierto = (String) selected;
                for (Concierto con : contextoCentral.getTodosLosConciertos()) {
                    if (con.getNombre().equals(nombreConcierto)) {
                        seleccionarConcierto(con);
                        break;
                    }
                }
            }
        });

    }

   
    public void cargarDatosCliente() {
        Persona usuario = contextoCentral.getUsuarioLogueado();
        if (usuario != null) {
            vistaMenu.lblBienvenida.setText("Bienvenido: " + usuario.getNombres() + " " + usuario.getApellidos());

            if (usuario instanceof Cliente) {
                Cliente c = (Cliente) usuario;
                vistaMenu.lblPuntos.setText("Puntos: " + c.getPuntos());
            } else {
                vistaMenu.lblPuntos.setText("Puntos: N/A");
                vistaMenu.lblConcierto.setText("Administración");
            }

            vistaMenu.cbxConcierto.removeAllItems();
            for (Concierto con : contextoCentral.getTodosLosConciertos()) {
                vistaMenu.cbxConcierto.addItem(con.getNombre());
            }

            if (contextoCentral.getConciertoSeleccionado() != null) {
                vistaMenu.cbxConcierto.setSelectedItem(contextoCentral.getConciertoSeleccionado().getNombre());
            }
        }
    }

    
     
     
    public void seleccionarConcierto(Concierto seleccion) {
        if (seleccion != null) {
            contextoCentral.setConciertoSeleccionado(seleccion);
        }
    }

    
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

    
     
    public void verMisCompras() {
        Persona usuario = contextoCentral.getUsuarioLogueado();
        if (usuario instanceof Cliente) {
            Cliente c = (Cliente) usuario;
            FrmHistorial frm = new FrmHistorial();
            new ControladorHistorial(frm, contextoCentral, c);
            frm.setVisible(true);
            vistaMenu.dispose();
        } else {
            JOptionPane.showMessageDialog(vistaMenu, "Solo los clientes tienen historial de compras.");
        }
    }

    
     
    public void comprarEntradas() {
        try {
            FrmComprarEntradas cliente = new FrmComprarEntradas();
            new ControladorComprarEntradas(contextoCentral, cliente, contextoCentral.getColeccionVentas());
            cliente.setVisible(true);
            vistaMenu.dispose();
        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(vistaMenu, 
                "Ocurrió un error al abrir la ventana:\n" + e.toString(), 
                "Error Crítico", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    
     
    public void cerrarSesion() {
        contextoCentral.cerrarSesion();
        FrmLogin login = new FrmLogin();
        new ControladorLogin(login, contextoCentral);
        login.setVisible(true);
        vistaMenu.dispose();
    }
}
