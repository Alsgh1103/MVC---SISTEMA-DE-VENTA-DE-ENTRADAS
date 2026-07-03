package controlador;

import coleccion.ColeccionConciertos;
import coleccion.ColeccionVentas;
import modelo.Concierto;
import modelo.Venta;
import modelo.Zona;
import vista.FrmAdmin;
import vista.FrmConcierto;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ControladorAdmin implements ActionListener {
    private FrmAdmin vistaAdmin;
    private ControladorPrincipal contextoCentral;

    public ControladorAdmin(FrmAdmin vistaAdmin, ControladorPrincipal contextoCentral) {
        this.vistaAdmin = vistaAdmin;
        this.contextoCentral = contextoCentral;
        
        this.vistaAdmin.btnRefrescar.addActionListener(this);
        this.vistaAdmin.btnCerrarSesion.addActionListener(this);
        this.vistaAdmin.btnCrearConcierto.addActionListener(this);
        this.vistaAdmin.btnEditarZonas.addActionListener(this);
        this.vistaAdmin.btnEliminarFila.addActionListener(this);
        this.vistaAdmin.btnAnadirZonaIndividual.addActionListener(this);
        
        //refrescarTabla();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vistaAdmin.btnRefrescar) {
            refrescarTabla();
        } else if (e.getSource() == vistaAdmin.btnCerrarSesion) {
            cerrarSesion();
        } else if (e.getSource() == vistaAdmin.btnCrearConcierto) {
            abrirCrearConcierto();
        } else if (e.getSource() == vistaAdmin.btnEditarZonas) {
            abrirEditarZonas();
        } else if (e.getSource() == vistaAdmin.btnEliminarFila) {
            procesarEliminarFila();
        } else if (e.getSource() == vistaAdmin.btnAnadirZonaIndividual) {
            procesarAnadirZona();
        }
    }

    public void refrescarTabla() {
        vistaAdmin.refrescarTabla(obtenerDatosAuditoria());
    }

    public Object[][] obtenerDatosAuditoria() {
        ColeccionConciertos cc = contextoCentral.getColeccionConciertos();
        ArrayList<Concierto> todosConciertos = cc.getTodosLosConciertos();
        ColeccionVentas cv = contextoCentral.getColeccionVentas();
        ArrayList<Venta> todasLasVentas = cv.getTodasLasVentas();

        int totalFilas = 0;
        for (Concierto c : todosConciertos) {
            int cantidadZonas = c.getTodasLasZonas().size();
            totalFilas += (cantidadZonas == 0) ? 1 : cantidadZonas;
        }

        if (totalFilas == 0) return new Object[0][5];
        Object[][] matriz = new Object[totalFilas][5]; 
        int index = 0;

        for (Concierto c : todosConciertos) {
            ArrayList<Zona> zonasDelConcierto = c.getTodasLasZonas();
            if (zonasDelConcierto.isEmpty()) {
                matriz[index][0] = c.getNombre(); 
                matriz[index][1] = c.getFecha().toString();
                matriz[index][2] = "Sin zonas";
                matriz[index][3] = "-";
                matriz[index][4] = 0; 
                index++;
            } else {
                for (Zona z : zonasDelConcierto) {
                    matriz[index][0] = c.getNombre();
                    matriz[index][1] = c.getFecha().toString();
                    matriz[index][2] = z.getNombre();
                    matriz[index][3] = z.getCapacidadDisponible();

                    int contadorEntradasVendidas = 0;
                    for (Venta v : todasLasVentas) {
                        if (v.getZona() == z) {
                            contadorEntradasVendidas += v.getCantidadEntradas();
                        }
                    }
                    matriz[index][4] = contadorEntradasVendidas; 
                    index++;
                }
            }
        }
        return matriz;
    }

    private void abrirCrearConcierto() {
        FrmConcierto ventanaConcierto = new FrmConcierto(vistaAdmin);
        new ControladorConcierto(ventanaConcierto, contextoCentral, this);
        ventanaConcierto.setVisible(true);
        vistaAdmin.setVisible(false);
    }

    private void abrirEditarZonas() {
        int filaSeleccionada = vistaAdmin.tblVentas.getSelectedRow();
        if (filaSeleccionada < 0) {
            javax.swing.JOptionPane.showMessageDialog(vistaAdmin, "Por favor, seleccione una fila de la tabla.");
            return;
        }
        String nombreConcierto = vistaAdmin.tblVentas.getValueAt(filaSeleccionada, 0).toString();
        String fechaStr = vistaAdmin.tblVentas.getValueAt(filaSeleccionada, 1).toString();
        try {
            java.time.LocalDate fecha = java.time.LocalDate.parse(fechaStr);
            Concierto seleccionado = contextoCentral.getColeccionConciertos().buscarPorNombreYFecha(nombreConcierto, fecha);

            if (seleccionado != null) {
                FrmConcierto frmEdit = new FrmConcierto(vistaAdmin);
                new ControladorConcierto(frmEdit, contextoCentral, this, seleccionado);
                frmEdit.setVisible(true);
                vistaAdmin.setVisible(false);
            } else {
                javax.swing.JOptionPane.showMessageDialog(vistaAdmin, "No se encontró el concierto especificado.", "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            javax.swing.JOptionPane.showMessageDialog(vistaAdmin, "Error al procesar el concierto: " + e.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private void procesarEliminarFila() {
        int fila = vistaAdmin.tblVentas.getSelectedRow();
        if (fila < 0) {
            javax.swing.JOptionPane.showMessageDialog(vistaAdmin, "Seleccione una fila primero.");
            return;
        }
        String nombreConc = vistaAdmin.tblVentas.getValueAt(fila, 0).toString();
        String fechaStr = vistaAdmin.tblVentas.getValueAt(fila, 1).toString();
        String nombreZona = vistaAdmin.tblVentas.getValueAt(fila, 2).toString();
        
        if (nombreZona.equals("Sin zonas") || nombreZona.equals("-")) {
            int conf = javax.swing.JOptionPane.showConfirmDialog(vistaAdmin, "¿Eliminar por completo el concierto vacío?", "Confirmar", javax.swing.JOptionPane.YES_NO_OPTION);
            if (conf == javax.swing.JOptionPane.YES_OPTION) {
                eliminarConciertoVacio(nombreConc, fechaStr);
                refrescarTabla();
            }
        } else {
            String[] opciones = {"Borrar SOLO esta Zona", "Borrar TODO el Concierto", "Cancelar"};
            int seleccion = javax.swing.JOptionPane.showOptionDialog(vistaAdmin, "¿Qué desea eliminar?", "Opciones de Eliminación", 
                javax.swing.JOptionPane.DEFAULT_OPTION, javax.swing.JOptionPane.WARNING_MESSAGE, null, opciones, opciones[0]);

            if (seleccion == 0) {
                eliminarZonaEspecifica(nombreConc, fechaStr, nombreZona);
                refrescarTabla();
            } else if (seleccion == 1) {
                eliminarConciertoCompleto(nombreConc, fechaStr);
                refrescarTabla();
            }
        }
    }

    private void procesarAnadirZona() {
        int fila = vistaAdmin.tblVentas.getSelectedRow();
        if (fila < 0) {
            javax.swing.JOptionPane.showMessageDialog(vistaAdmin, "Seleccione en la tabla el concierto al que desea añadir una zona.");
            return;
        }

        String nombreConc = vistaAdmin.tblVentas.getValueAt(fila, 0).toString();
        String fechaStr = vistaAdmin.tblVentas.getValueAt(fila, 1).toString();

        String nombreZona = javax.swing.JOptionPane.showInputDialog(vistaAdmin, "Nombre de la NUEVA zona (Ej: Platinum):");
        if (nombreZona == null || nombreZona.trim().isEmpty()) return;

        String capStr = javax.swing.JOptionPane.showInputDialog(vistaAdmin, "Capacidad total para " + nombreZona + ":");
        if (capStr == null) return;

        String precStr = javax.swing.JOptionPane.showInputDialog(vistaAdmin, "Precio (S/) para " + nombreZona + ":");
        if (precStr == null) return;

        try {
            agregarZonaAConcierto(nombreConc, fechaStr, nombreZona, capStr, precStr);
            refrescarTabla();
            javax.swing.JOptionPane.showMessageDialog(vistaAdmin, "Zona añadida exitosamente sin borrar las anteriores.");
        } catch (NumberFormatException ex) {
            javax.swing.JOptionPane.showMessageDialog(vistaAdmin, "La capacidad y el precio deben ser números enteros.");
        } catch (IllegalArgumentException ex) {
            javax.swing.JOptionPane.showMessageDialog(vistaAdmin, ex.getLocalizedMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    public void eliminarConciertoVacio(String nombreConc, String fechaStr) {
        java.time.LocalDate fecha = java.time.LocalDate.parse(fechaStr);
        Concierto concierto = contextoCentral.getColeccionConciertos().buscarPorNombreYFecha(nombreConc, fecha);
        if (concierto != null) {
            contextoCentral.eliminarConciertoGlobal(concierto);
        }
    }

    public void eliminarZonaEspecifica(String nombreConc, String fechaStr, String nombreZona) {
        java.time.LocalDate fecha = java.time.LocalDate.parse(fechaStr);
        Concierto concierto = contextoCentral.getColeccionConciertos().buscarPorNombreYFecha(nombreConc, fecha);
        if (concierto != null) {
            contextoCentral.eliminarZonaDeConcierto(concierto, nombreZona);
        }
    }

    public void eliminarConciertoCompleto(String nombreConc, String fechaStr) {
        java.time.LocalDate fecha = java.time.LocalDate.parse(fechaStr);
        Concierto concierto = contextoCentral.getColeccionConciertos().buscarPorNombreYFecha(nombreConc, fecha);
        if (concierto != null) {
            contextoCentral.eliminarConciertoGlobal(concierto);
        }
    }

    public void agregarZonaAConcierto(String nombreConc, String fechaStr, String nombreZona, String capStr, String precStr) throws NumberFormatException, IllegalArgumentException {
        java.time.LocalDate fecha = java.time.LocalDate.parse(fechaStr);
        Concierto concierto = contextoCentral.getColeccionConciertos().buscarPorNombreYFecha(nombreConc, fecha);

        if (concierto == null) {
            throw new IllegalArgumentException("El concierto especificado no existe.");
        }
        int capacidad = Integer.parseInt(capStr);
        int precio = Integer.parseInt(precStr);
        concierto.registrarZona(nombreZona, capacidad, precio);
    }

    public void cerrarSesion() {
        contextoCentral.cerrarSesion();
        vista.FrmLogin login = new vista.FrmLogin();
        new ControladorLogin(login, this.contextoCentral);
        login.setVisible(true);
        vistaAdmin.dispose();
    }

    public ColeccionConciertos getColeccionConciertos() {
        return contextoCentral.getColeccionConciertos();
    }
}