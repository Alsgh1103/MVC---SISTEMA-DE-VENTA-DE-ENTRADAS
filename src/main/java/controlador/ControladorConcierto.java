package controlador;

import vista.FrmConcierto;
import modelo.Concierto;
import coleccion.ColeccionConciertos;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.JOptionPane;

public class ControladorConcierto {
    private FrmConcierto vistaConcierto;
    private ControladorPrincipal contextoCentral;
    private Concierto conciertoActual;

    public ControladorConcierto(FrmConcierto vistaConcierto, ControladorPrincipal contextoCentral) {
        this.vistaConcierto = vistaConcierto;
        this.contextoCentral = contextoCentral;
    }

    public boolean registrarNuevoConcierto(String nombre, String fechaStr) {
        try {
            if (nombre == null || nombre.trim().isEmpty() || fechaStr == null || fechaStr.trim().isEmpty()) {
                JOptionPane.showMessageDialog(vistaConcierto, "Por favor, complete todos los campos.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                return false;
            }
            LocalDate fecha = LocalDate.parse(fechaStr);
            ColeccionConciertos coleccion = contextoCentral.getColeccionConciertos();
            this.conciertoActual = coleccion.registrarConcierto(nombre, fecha);
            contextoCentral.setConciertoSeleccionado(this.conciertoActual);
            
            JOptionPane.showMessageDialog(vistaConcierto, "¡Concierto '" + nombre + "' registrado exitosamente!");
            return true;
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(vistaConcierto, "Formato de fecha inválido. Por favor use el formato YYYY-MM-DD.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(vistaConcierto, e.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean agregarZonaAlConcierto(String nombreZona, String capacidadStr, String precioStr) {
        try {
            if (conciertoActual == null) {
                JOptionPane.showMessageDialog(vistaConcierto, "No hay ningún concierto seleccionado para agregar zonas.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            if (nombreZona == null || nombreZona.trim().isEmpty() || capacidadStr == null || capacidadStr.trim().isEmpty() || precioStr == null || precioStr.trim().isEmpty()) {
                JOptionPane.showMessageDialog(vistaConcierto, "Por favor, complete todos los campos de la zona.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            int capacidad;
            int precio;
            try {
                capacidad = Integer.parseInt(capacidadStr.trim());
                precio = Integer.parseInt(precioStr.trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(vistaConcierto, "La capacidad y el precio deben ser números enteros válidos.", "Error de Tipado", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            conciertoActual.registrarZona(nombreZona, capacidad, precio);
            JOptionPane.showMessageDialog(vistaConcierto, "Zona agregada con éxito.");
            return true;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(vistaConcierto, e.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public void prepararReconfiguracionZonas(Concierto concierto) throws IllegalStateException, IllegalArgumentException {
        if (concierto == null) {
            throw new IllegalArgumentException("No se ha seleccionado un concierto válido.");
        }
        this.conciertoActual = concierto;
        this.conciertoActual.limpiarZonas(); // Vaciamos las zonas previas (N) para recibir las nuevas (M)
    }
}
