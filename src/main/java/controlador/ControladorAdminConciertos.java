package controlador;

import coleccion.ColeccionConciertos;
import coleccion.ColeccionVentas;
import modelo.Concierto;
import modelo.Venta;
import modelo.Zona;
import vista.FrmAdminConciertos;
import vista.FrmAdminRegistrarConcierto;
import vista.FrmZona;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

public class ControladorAdminConciertos implements ActionListener, ItemListener {
    private FrmAdminConciertos vistaAdmin;
    private ControladorPrincipal contextoCentral;

    public ControladorAdminConciertos(FrmAdminConciertos vistaAdmin, ControladorPrincipal contextoCentral) {
        this.vistaAdmin = vistaAdmin;
        this.contextoCentral = contextoCentral;
        this.vistaAdmin.btnVolver.addActionListener(this);
        this.vistaAdmin.btnRegistrarConcierto.addActionListener(this);
        this.vistaAdmin.btnEditarZonas.addActionListener(this);
        this.vistaAdmin.btnEliminarSeleccionados.addActionListener(this);
        this.vistaAdmin.btnAnadirZona.addActionListener(this);
        if (this.vistaAdmin.btnTransacciones != null) {
            this.vistaAdmin.btnTransacciones.addActionListener(this);
        }

        if (this.vistaAdmin.btnEliminarConcierto1 != null) {
            this.vistaAdmin.btnEliminarConcierto1.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    eliminarConcierto();
                }
            });
        }
        
        if (this.vistaAdmin.btnEditarConcierto != null) {
            this.vistaAdmin.btnEditarConcierto.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    editarConcierto();
                }
            });
        }

        this.vistaAdmin.cbmConciertos.addItemListener(this);

        poblarComboBox();
        refrescarTabla();
        actualizarBanner();

        modelo.Persona p = contextoCentral.getUsuarioLogueado();
        if (p != null && !p.getCorreo().equals("admin")) {
            this.vistaAdmin.btnVolver.setText("Cerrar Sesión");
            this.vistaAdmin.btnVolver
                    .setIcon(new javax.swing.ImageIcon(getClass().getResource("/img/cerrar-sesion.png")));
        }
    }

    public FrmAdminConciertos getVista() {
        return this.vistaAdmin;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vistaAdmin.btnVolver) {
            cerrarSesion();
        } else if (e.getSource() == vistaAdmin.btnRegistrarConcierto) {
            abrirCrearConcierto();
        } else if (e.getSource() == vistaAdmin.btnEditarZonas) {
            abrirEditarZona();
        } else if (e.getSource() == vistaAdmin.btnEliminarSeleccionados) {
            procesarEliminarSeleccionados();
        } else if (e.getSource() == vistaAdmin.btnAnadirZona) {
            abrirAnadirZona();
        } else if (vistaAdmin.btnTransacciones != null && e.getSource() == vistaAdmin.btnTransacciones) {
            abrirTransaccionesAdmin();
        }
    }

    private void eliminarConcierto() {
        int idxCombo = vistaAdmin.cbmConciertos.getSelectedIndex();
        if (idxCombo <= 0) {
            javax.swing.JOptionPane.showMessageDialog(vistaAdmin, "Por favor seleccione un concierto específico para eliminar.", "Aviso", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        Concierto c = contextoCentral.getColeccionConciertos().getTodosLosConciertos().get(idxCombo - 1);
        int resp = javax.swing.JOptionPane.showConfirmDialog(vistaAdmin, "¿Está seguro de eliminar el concierto: " + c.getNombre() + " y todas sus zonas?", "Confirmar Eliminación", javax.swing.JOptionPane.YES_NO_OPTION);
        if (resp == javax.swing.JOptionPane.YES_OPTION) {
            boolean exito = contextoCentral.eliminarConciertoGlobal(c);
            if (exito) {
                javax.swing.JOptionPane.showMessageDialog(vistaAdmin, "Concierto eliminado exitosamente.");
                contextoCentral.setConciertoSeleccionado(null);
                poblarComboBox();
                refrescarTabla();
                actualizarBanner();
            }
        }
    }

    private void editarConcierto() {
        int idxCombo = vistaAdmin.cbmConciertos.getSelectedIndex();
        if (idxCombo <= 0) {
            javax.swing.JOptionPane.showMessageDialog(vistaAdmin, "Por favor seleccione un concierto específico para editar.", "Aviso", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        Concierto c = contextoCentral.getColeccionConciertos().getTodosLosConciertos().get(idxCombo - 1);
        contextoCentral.setConciertoSeleccionado(c);
        vista.FrmAdminRegistrarConcierto ventanaEdicion = new vista.FrmAdminRegistrarConcierto();
        new ControladorAdminRegistrarConcierto(ventanaEdicion, contextoCentral, this);
        ventanaEdicion.setVisible(true);
        vistaAdmin.dispose();
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            refrescarTabla();
            actualizarBanner();
        }
    }

    private void actualizarBanner() {
        int idxCombo = vistaAdmin.cbmConciertos.getSelectedIndex();
        boolean cambioVisibilidad = false;
        if (idxCombo > 0) {
            Concierto c = contextoCentral.getColeccionConciertos().getTodosLosConciertos().get(idxCombo - 1);
            String ruta = c.getRutaImagen();
            if (ruta != null && !ruta.isEmpty()) {
                java.net.URL imgUrl = getClass().getResource(ruta);
                if (imgUrl != null) {
                    javax.swing.ImageIcon iconoOriginal = new javax.swing.ImageIcon(imgUrl);
                    java.awt.Image imagenEscalada = iconoOriginal.getImage().getScaledInstance(600, 136, java.awt.Image.SCALE_SMOOTH);
                    try {
                        java.lang.reflect.Field field = vistaAdmin.getClass().getDeclaredField("lblBanner");
                        field.setAccessible(true);
                        javax.swing.JLabel lblBanner = (javax.swing.JLabel) field.get(vistaAdmin);
                        lblBanner.setIcon(new javax.swing.ImageIcon(imagenEscalada));
                        if (!lblBanner.isVisible()) {
                            lblBanner.setVisible(true);
                            cambioVisibilidad = true;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    cambioVisibilidad = borrarBanner();
                }
            } else {
                cambioVisibilidad = borrarBanner();
            }
        } else {
            cambioVisibilidad = borrarBanner();
        }
        
        if (cambioVisibilidad) {
            vistaAdmin.pack();
        }
    }

    private boolean borrarBanner() {
        boolean cambio = false;
        try {
            java.lang.reflect.Field field = vistaAdmin.getClass().getDeclaredField("lblBanner");
            field.setAccessible(true);
            javax.swing.JLabel lblBanner = (javax.swing.JLabel) field.get(vistaAdmin);
            lblBanner.setIcon(null);
            if (lblBanner.isVisible()) {
                lblBanner.setVisible(false);
                cambio = true;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cambio;
    }

    public void poblarComboBox() {
        vistaAdmin.cbmConciertos.removeItemListener(this);
        vistaAdmin.cbmConciertos.removeAllItems();
        vistaAdmin.cbmConciertos.addItem("Todos los Conciertos");

        for (Concierto c : contextoCentral.getColeccionConciertos().getTodosLosConciertos()) {
            vistaAdmin.cbmConciertos.addItem(c.getNombre() + " (" + c.getFecha().toString() + ")");
        }

        Concierto ultimo = contextoCentral.getConciertoSeleccionado();
        if (ultimo != null) {
            vistaAdmin.cbmConciertos
                    .setSelectedItem(ultimo.getNombre() + " (" + ultimo.getFecha().toString() + ")");
        }
        vistaAdmin.cbmConciertos.addItemListener(this);
    }

    public void refrescarTabla() {
        boolean mostrarConcierto = vistaAdmin.cbmConciertos.getSelectedIndex() <= 0;
        Object[][] datos = obtenerDatosAuditoria(mostrarConcierto);

        String[] columnas;
        if (mostrarConcierto) {
            columnas = new String[] { "✓", "Concierto", "Zona", "Capacidad Restante", "Entradas Vendidas", "Precio" };
        } else {
            columnas = new String[] { "✓", "Zona", "Capacidad Restante", "Entradas Vendidas", "Precio" };
        }

        javax.swing.table.DefaultTableModel modeloTabla = new javax.swing.table.DefaultTableModel(datos, columnas) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Boolean.class : super.getColumnClass(columnIndex);
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; // Solo los checkbox son editables
            }
        };
        vistaAdmin.tblVentas.setModel(modeloTabla);

        // Ajustar ancho de la columna de check
        if (vistaAdmin.tblVentas.getColumnModel().getColumnCount() > 0) {
            javax.swing.table.TableColumn colCheck = vistaAdmin.tblVentas.getColumnModel().getColumn(0);
            colCheck.setMaxWidth(40);
            colCheck.setMinWidth(40);
            colCheck.setPreferredWidth(40);
        }
    }

    public Object[][] obtenerDatosAuditoria(boolean mostrarConcierto) {
        ColeccionConciertos cc = contextoCentral.getColeccionConciertos();
        ArrayList<Concierto> todosConciertos = cc.getTodosLosConciertos();
        ColeccionVentas cv = contextoCentral.getColeccionVentas();
        ArrayList<Venta> todasLasVentas = cv.getTodasLasVentas();

        int idxCombo = vistaAdmin.cbmConciertos.getSelectedIndex();
        Concierto filtro = null;
        if (idxCombo > 0) {
            filtro = todosConciertos.get(idxCombo - 1);
        }

        ArrayList<Zona> zonasAMostrar = new ArrayList<>();
        ArrayList<Concierto> conciertosAsociados = new ArrayList<>();

        for (Concierto c : todosConciertos) {
            if (filtro == null || c == filtro) {
                for (Zona z : c.getTodasLasZonas()) {
                    conciertosAsociados.add(c);
                    zonasAMostrar.add(z);
                }
            }
        }

        int numColumnas = mostrarConcierto ? 6 : 5;
        if (zonasAMostrar.isEmpty())
            return new Object[0][numColumnas];

        Object[][] matriz = new Object[zonasAMostrar.size()][numColumnas];

        for (int i = 0; i < zonasAMostrar.size(); i++) {
            Concierto c = conciertosAsociados.get(i);
            Zona z = zonasAMostrar.get(i);

            matriz[i][0] = false;

            int colIdx = 1;
            if (mostrarConcierto) {
                matriz[i][colIdx++] = c.getNombre();
            }

            matriz[i][colIdx++] = z.getNombre();
            matriz[i][colIdx++] = z.getCapacidadDisponible();

            int contadorEntradasVendidas = 0;
            for (Venta v : todasLasVentas) {
                if (v.getZona() == z) {
                    contadorEntradasVendidas += v.getCantidadEntradas();
                }
            }
            matriz[i][colIdx++] = contadorEntradasVendidas;
            matriz[i][colIdx] = "S/ " + z.getPrecio();
        }
        return matriz;
    }

    private void abrirCrearConcierto() {
        contextoCentral.setConciertoSeleccionado(null);
        FrmAdminRegistrarConcierto ventanaConcierto = new FrmAdminRegistrarConcierto();
        new ControladorAdminRegistrarConcierto(ventanaConcierto, contextoCentral, this);
        ventanaConcierto.setVisible(true);
        vistaAdmin.setVisible(false);
    }

    private void abrirAnadirZona() {
        int idxCombo = vistaAdmin.cbmConciertos.getSelectedIndex();
        if (idxCombo <= 0) {
            javax.swing.JOptionPane.showMessageDialog(vistaAdmin,
                    "Debe seleccionar un concierto específico en el desplegable de arriba para añadirle zonas.");
            return;
        }
        Concierto c = contextoCentral.getColeccionConciertos().getTodosLosConciertos().get(idxCombo - 1);

        FrmZona frm = new FrmZona();
        new ControladorZona(frm, contextoCentral, this, c.getNombre(), c.getFecha(), null);
        frm.setVisible(true);
    }

    public void abrirTransaccionesAdmin() {
        int idxCombo = vistaAdmin.cbmConciertos.getSelectedIndex();
        Concierto c = null;
        if (idxCombo > 0) {
            c = contextoCentral.getColeccionConciertos().getTodosLosConciertos().get(idxCombo - 1);
        }

        vista.FrmTransaccionesAdmin frm = new vista.FrmTransaccionesAdmin();
        new ControladorTransaccionesAdmin(frm, contextoCentral, c, this);
        frm.setVisible(true);
        vistaAdmin.setVisible(false);
    }

    private void abrirEditarZona() {
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) vistaAdmin.tblVentas
                .getModel();
        int rowCount = model.getRowCount();

        int selectedRows = 0;
        int lastSelectedRow = -1;
        for (int i = 0; i < rowCount; i++) {
            Boolean checked = (Boolean) model.getValueAt(i, 0);
            if (checked != null && checked) {
                selectedRows++;
                lastSelectedRow = i;
            }
        }

        if (selectedRows != 1) {
            javax.swing.JOptionPane.showMessageDialog(vistaAdmin,
                    "Por favor seleccione exactamente UNA zona (marcando el cuadro) para editarla.");
            return;
        }

        int idxCombo = vistaAdmin.cbmConciertos.getSelectedIndex();
        Concierto filtro = null;
        if (idxCombo > 0) {
            filtro = contextoCentral.getColeccionConciertos().getTodosLosConciertos().get(idxCombo - 1);
        } else {
            javax.swing.JOptionPane.showMessageDialog(vistaAdmin,
                    "Por favor seleccione un concierto individual en el desplegable primero.");
            return;
        }

        String cell1 = model.getValueAt(lastSelectedRow, 1).toString();
        String[] parts = cell1.split(" \\(");
        String zonaName = parts[0];

        Zona zonaAEditar = null;
        for (Zona z : filtro.getTodasLasZonas()) {
            if (z.getNombre().equals(zonaName)) {
                zonaAEditar = z;
                break;
            }
        }

        if (zonaAEditar != null) {
            FrmZona frm = new FrmZona();
            new ControladorZona(frm, contextoCentral, this, filtro.getNombre(), filtro.getFecha(), zonaAEditar);
            frm.setVisible(true);
        }
    }

    private void procesarEliminarSeleccionados() {
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) vistaAdmin.tblVentas
                .getModel();
        int rowCount = model.getRowCount();

        boolean eliminoAlgo = false;

        int idxCombo = vistaAdmin.cbmConciertos.getSelectedIndex();
        Concierto filtro = null;

        if (idxCombo > 0) {
            filtro = contextoCentral.getColeccionConciertos().getTodosLosConciertos().get(idxCombo - 1);
        }

        for (int i = rowCount - 1; i >= 0; i--) {
            Boolean checked = (Boolean) model.getValueAt(i, 0);
            if (checked != null && checked) {
                String cell1 = model.getValueAt(i, 1).toString();
                if (filtro != null) {
                    String[] parts = cell1.split(" \\(");
                    String zonaName = parts[0];
                    if (!zonaName.equals("Sin zonas -")) {
                        contextoCentral.eliminarZonaDeConcierto(filtro, zonaName);
                        eliminoAlgo = true;
                    }
                } else {
                    javax.swing.JOptionPane.showMessageDialog(vistaAdmin,
                            "Por favor seleccione un concierto individual en el desplegable para eliminar múltiples zonas.");
                    return;
                }
            }
        }

        if (eliminoAlgo) {
            javax.swing.JOptionPane.showMessageDialog(vistaAdmin, "Zonas seleccionadas eliminadas exitosamente.");
            refrescarTabla();
        } else {
            javax.swing.JOptionPane.showMessageDialog(vistaAdmin, "No se seleccionó ninguna zona válida.");
        }
    }

    public void cerrarSesion() {
        modelo.Persona p = contextoCentral.getUsuarioLogueado();
        if (p != null && p.getCorreo().equals("admin")) {
            vista.FrmAdmin ventanaSuperAdmin = new vista.FrmAdmin();
            new ControladorAdmin(ventanaSuperAdmin, contextoCentral);
            ventanaSuperAdmin.setVisible(true);
            vistaAdmin.dispose();
        } else {
            contextoCentral.cerrarSesion();
            vista.FrmLogin login = new vista.FrmLogin();
            new ControladorLogin(login, this.contextoCentral);
            login.setVisible(true);
            vistaAdmin.dispose();
        }
    }
}