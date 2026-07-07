package controlador;

import vista.FrmZona;
import modelo.Concierto;
import modelo.Zona;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class ControladorZona implements ActionListener {
    private FrmZona vista;
    private ControladorPrincipal contextoCentral;
    private ControladorAdminConciertos ctrlAdmin;
    private String nombreConcierto;
    private java.time.LocalDate fechaConcierto;
    private Zona zonaAEditar;

    public ControladorZona(FrmZona vista, ControladorPrincipal contextoCentral, ControladorAdminConciertos ctrlAdmin, String nombreConcierto, java.time.LocalDate fechaConcierto, Zona zona) {
        this.vista = vista;
        this.contextoCentral = contextoCentral;
        this.ctrlAdmin = ctrlAdmin;
        this.nombreConcierto = nombreConcierto;
        this.fechaConcierto = fechaConcierto;
        this.zonaAEditar = zona;

        this.vista.btnGuardar.addActionListener(this);
        this.vista.btnCancelar.addActionListener(this);
        this.vista.setLocationRelativeTo(null);
        
        if (zona != null) {
            this.vista.setTitle("Editar Zona");
            this.vista.lblTitulo.setText("Editar Zona");
            this.vista.txtNombreZona.setText(zona.getNombre());
            this.vista.txtCapacidad.setText(String.valueOf(zona.getCapacidadTotal()));
            this.vista.txtPrecio.setText(String.valueOf(zona.getPrecio()));
        } else {
            this.vista.setTitle("Añadir Nueva Zona");
            this.vista.lblTitulo.setText("Añadir Nueva Zona");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.btnGuardar) {
            guardarZona();
        } else if (e.getSource() == vista.btnCancelar) {
            vista.dispose();
        }
    }

    private void guardarZona() {
        String nombreZona = vista.txtNombreZona.getText().trim();
        String capStr = vista.txtCapacidad.getText().trim();
        String precioStr = vista.txtPrecio.getText().trim();

        if (nombreZona.isEmpty() || capStr.isEmpty() || precioStr.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Complete todos los campos.", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int capacidad = Integer.parseInt(capStr);
            int precio = Integer.parseInt(precioStr);

            Concierto concierto = contextoCentral.getColeccionConciertos().buscarPorNombreYFecha(nombreConcierto, fechaConcierto);
            if (concierto != null) {
                if (zonaAEditar != null) {
                    zonaAEditar.setNombre(nombreZona);
                    int vendidas = zonaAEditar.getCapacidadTotal() - zonaAEditar.getCapacidadDisponible();
                    if (capacidad < vendidas) {
                        JOptionPane.showMessageDialog(vista, "La nueva capacidad no puede ser menor a las entradas ya vendidas (" + vendidas + ").", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    zonaAEditar.setCapacidadTotal(capacidad);
                    zonaAEditar.setCapacidadDisponible(capacidad - vendidas);
                    zonaAEditar.setPrecio(precio);
                    JOptionPane.showMessageDialog(vista, "Zona actualizada exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    concierto.registrarZona(nombreZona, capacidad, precio);
                    JOptionPane.showMessageDialog(vista, "Zona añadida exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
                contextoCentral.guardarEstado();
                ctrlAdmin.refrescarTabla();
                vista.dispose();
            } else {
                JOptionPane.showMessageDialog(vista, "No se encontró el concierto asociado.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "La capacidad y el precio deben ser valores numéricos.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(vista, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
