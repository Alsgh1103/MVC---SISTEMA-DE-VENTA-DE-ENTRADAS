package controlador;

import vista.FrmConcierto;

import modelo.Concierto;
import coleccion.ColeccionConciertos;
import java.time.LocalDate;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class ControladorConcierto implements ActionListener {
    private FrmConcierto vista;
    private ControladorPrincipal contextoCentral;
    private ControladorAdmin ctrlAdminAnterior;

    public ControladorConcierto(FrmConcierto vista, ControladorPrincipal contextoCentral,
            ControladorAdmin ctrlAdminAnterior) {
        this.vista = vista;
        this.contextoCentral = contextoCentral;
        this.ctrlAdminAnterior = ctrlAdminAnterior;

        this.vista.btnGuardar.addActionListener(this);
        this.vista.btnVolver.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.btnGuardar) {
            guardarConcierto();
        } else if (e.getSource() == vista.btnVolver) {
            volver();
        }
    }

    private void guardarConcierto() {
        try {
            String nombre = vista.getTxtNombre().getText().trim();
            java.util.Date utilDate = vista.getDateFechaEvento().getDate();

            if (nombre.isEmpty() || utilDate == null) {
                JOptionPane.showMessageDialog(vista, "Por favor, complete todos los campos (Nombre y Fecha).", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate fecha = java.time.Instant.ofEpochMilli(utilDate.getTime()).atZone(java.time.ZoneId.systemDefault()).toLocalDate();

            ColeccionConciertos coleccion = contextoCentral.getColeccionConciertos();
            Concierto concierto = coleccion.registrarConcierto(nombre, fecha);
            contextoCentral.setConciertoSeleccionado(concierto);

            JOptionPane.showMessageDialog(vista, "Concierto creado. Redirigiendo al Panel de Control para añadir Zonas.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            
            volver();

        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(vista, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Ocurrió un error inesperado al guardar.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void volver() {
        if (ctrlAdminAnterior != null) {
            ctrlAdminAnterior.poblarComboBox();
            ctrlAdminAnterior.refrescarTabla();
            ctrlAdminAnterior.getVista().setVisible(true);
        }
        vista.dispose();
    }
}