package controlador;

import vista.FrmConcierto;
import vista.FrmAdmin;
import modelo.Concierto;
import coleccion.ColeccionConciertos;
import java.time.LocalDate;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class ControladorConcierto implements ActionListener { 
    private FrmConcierto vista;
    private ControladorPrincipal contextoCentral;

    public ControladorConcierto(FrmConcierto vista, ControladorPrincipal contextoCentral) {
        this.vista = vista;
        this.contextoCentral = contextoCentral;
        
        // Conectamos los botones de tu pantalla
        this.vista.getBtnGuardar().addActionListener(this);
        this.vista.getBtnVolver().addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.getBtnGuardar()) {
            guardarConcierto();
        } else if (e.getSource() == vista.getBtnVolver()) {
            volver();
        }
    }

    private void guardarConcierto() {
        try {
            Concierto concierto = vista.getConciertoAEditar();

            if (concierto != null) { // Si estamos editando
                concierto.limpiarZonas(); 
            } else { // Si es un concierto nuevo
                String nombre = vista.getNombre();
                
                int anio = vista.getAnio();
                int mes = vista.getMes();
                int dia = vista.getDia();
                LocalDate fecha = LocalDate.of(anio, mes, dia);
                
                ColeccionConciertos coleccion = contextoCentral.getColeccionConciertos();
                concierto = coleccion.registrarConcierto(nombre, fecha);
                contextoCentral.setConciertoSeleccionado(concierto);
            }

            // Preguntamos las zonas con ventanas flotantes
            pedirZonas(concierto);

            vista.mostrarMensaje("Operación exitosa", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            volver();

        } catch (IllegalArgumentException | IllegalStateException ex) {
            vista.mostrarMensaje(ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            vista.mostrarMensaje("Fecha inválida", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void pedirZonas(Concierto c) {
        String numZonasStr = vista.pedirDato("¿Cuántas zonas tendrá?");
        if (numZonasStr == null || numZonasStr.isEmpty()) return;

        try {
            int numZonas = Integer.parseInt(numZonasStr);
            for (int i = 0; i < numZonas; i++) {
                String nombreZona = vista.pedirDato("Nombre de la zona " + (i + 1) + ":");
                if (nombreZona == null) break;
                String capStr = vista.pedirDato("Capacidad para " + nombreZona + ":");
                if (capStr == null) break;
                String precStr = vista.pedirDato("Precio para " + nombreZona + ":");
                if (precStr == null) break;

                try {
                    c.registrarZona(nombreZona, Integer.parseInt(capStr), Integer.parseInt(precStr));
                } catch (Exception e) {
                    vista.mostrarMensaje("Datos inválidos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    i--; // Reintenta
                }
            }
        } catch (Exception e) {
            vista.mostrarMensaje("Número inválido", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void volver() {
        if (vista.getVistaAnterior() != null) {
            vista.getVistaAnterior().setVisible(true);
            if (vista.getVistaAnterior() instanceof FrmAdmin) {
                ((FrmAdmin) vista.getVistaAnterior()).refrescarTabla();
            }
        }
        vista.dispose();
    }
}
