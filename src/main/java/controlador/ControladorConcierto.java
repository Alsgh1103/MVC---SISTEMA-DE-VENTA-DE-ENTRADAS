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
    private Concierto conciertoAEditar;

    /**
     * Constructor para registrar un concierto nuevo.
     */
    public ControladorConcierto(FrmConcierto vista, ControladorPrincipal contextoCentral) {
        this(vista, contextoCentral, null);
    }

    /**
     * Constructor para editar un concierto existente.
     * Configura la vista en modo edición a través de prepararParaEdicion().
     */
    public ControladorConcierto(FrmConcierto vista, ControladorPrincipal contextoCentral, Concierto conciertoAEditar) {
        this.vista = vista;
        this.contextoCentral = contextoCentral;
        this.conciertoAEditar = conciertoAEditar;
        this.vista.getBtnGuardar().addActionListener(this);
        this.vista.getBtnVolver().addActionListener(this);

        // Llenar combos de fecha
        int anioActual = LocalDate.now().getYear();
        vista.llenarComboDias(1, 31);
        vista.llenarComboMeses(1, 12);
        vista.llenarComboAnios(anioActual, anioActual + 5);

        // Si se proveyó un concierto, el controlador configura la vista para edición
        if (conciertoAEditar != null) {
            LocalDate fecha = conciertoAEditar.getFecha();
            vista.cargarDatosParaEdicion(
                    conciertoAEditar.getNombre(),
                    fecha.getDayOfMonth(),
                    fecha.getMonthValue(),
                    fecha.getYear());
            vista.bloquearCamposEdicion();
            vista.setBtnGuardarTexto("Reconfigurar Zonas");
        }
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
            Concierto concierto = this.conciertoAEditar;

            if (concierto != null) { 
                concierto.limpiarZonas(); 
            } else { 
                String nombre = vista.getNombre();
                int anio = Integer.parseInt(vista.getAnio());
                int mes = Integer.parseInt(vista.getMes());
                int dia = Integer.parseInt(vista.getDia());
                LocalDate fecha = LocalDate.of(anio, mes, dia);

                ColeccionConciertos coleccion = contextoCentral.getColeccionConciertos();
                concierto = coleccion.registrarConcierto(nombre, fecha);
                contextoCentral.setConciertoSeleccionado(concierto);
            }

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
        if (numZonasStr == null || numZonasStr.isEmpty())
            return;

        try {
            int numZonas = Integer.parseInt(numZonasStr);
            for (int i = 0; i < numZonas; i++) {
                String nombreZona = vista.pedirDato("Nombre de la zona " + (i + 1) + ":");
                if (nombreZona == null)
                    break;
                String capStr = vista.pedirDato("Capacidad para " + nombreZona + ":");
                if (capStr == null)
                    break;
                String precStr = vista.pedirDato("Precio para " + nombreZona + ":");
                if (capStr == null) break;

                try {
                    c.registrarZona(nombreZona, Integer.parseInt(capStr), Integer.parseInt(precStr));
                } catch (Exception e) {
                    vista.mostrarMensaje("Datos inválidos: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    i--; 
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
                // REFACTORIZADO: Llama al método puente para que se refresque mediante el ControladorAdmin
                ((FrmAdmin) vista.getVistaAnterior()).invocarRefrescoDesdeControladorExterno();
            }
        }
        vista.dispose();
    }
}