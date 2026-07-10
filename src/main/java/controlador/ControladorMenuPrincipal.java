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

import componentes.PanelConciertoTarjeta;

public class ControladorMenuPrincipal {
    private FrmMenuPrincipal vistaMenu;
    private ControladorPrincipal contextoCentral;

    public ControladorMenuPrincipal(FrmMenuPrincipal vistaMenu, ControladorPrincipal contextoCentral) {
        this.vistaMenu = vistaMenu;
        this.contextoCentral = contextoCentral;

        vistaMenu.btnCerrarSesion.addActionListener(e -> cerrarSesion());
        vistaMenu.btnMisCompras.addActionListener(e -> verMisCompras());
        vistaMenu.panelCartelera.setLayout(new java.awt.GridLayout(0, 3, 15, 15));

        if (vistaMenu.txtNombreConcierto != null) {
            vistaMenu.txtNombreConcierto.addKeyListener(new java.awt.event.KeyAdapter() {
                public void keyReleased(java.awt.event.KeyEvent e) {
                    actualizarCartelera(vistaMenu.txtNombreConcierto.getText());
                }
            });
        }
        
        // Inicializar cartelera al abrir el menú
        actualizarCartelera("");
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
                        .append(" (Disponibles: ").append(z.getCapacidadDisponible()).append("/")
                        .append(z.getCapacidadTotal()).append(")\n");
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

    public void cerrarSesion() {
        contextoCentral.cerrarSesion();
        FrmLogin login = new FrmLogin();
        new ControladorLogin(login, contextoCentral);
        login.setVisible(true);
        vistaMenu.dispose();
    }

    public void comprarEntradasConcierto(Concierto concierto) {
        contextoCentral.setConciertoSeleccionado(concierto);
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

    public void actualizarCartelera(String textoBuscador) {
        vistaMenu.panelCartelera.removeAll();

        for (Concierto c : contextoCentral.getTodosLosConciertos()) {
            if (textoBuscador.isEmpty() || c.getNombre().toLowerCase().contains(textoBuscador.toLowerCase())) {
                PanelConciertoTarjeta tarjeta = new PanelConciertoTarjeta(c, this);
                vistaMenu.panelCartelera.add(tarjeta);
            }
        }

        vistaMenu.panelCartelera.revalidate();
        vistaMenu.panelCartelera.repaint();
    }

}
