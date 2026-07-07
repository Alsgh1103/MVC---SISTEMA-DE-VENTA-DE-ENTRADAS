package controlador;

import coleccion.ColeccionConciertos;
import coleccion.ColeccionVentas;
import modelo.Concierto;
import modelo.Venta;
import modelo.Zona;
import vista.FrmAdmin;
import vista.FrmConcierto;
import vista.FrmZona;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

public class ControladorAdmin implements ActionListener, ItemListener {
    private FrmAdmin vistaAdmin;
    private ControladorPrincipal contextoCentral;

    public ControladorAdmin(FrmAdmin vistaAdmin, ControladorPrincipal contextoCentral) {
        this.vistaAdmin = vistaAdmin;
        this.contextoCentral = contextoCentral;

        this.vistaAdmin.btnRefrescar.addActionListener(this);
        this.vistaAdmin.btnCerrarSesion.addActionListener(this);
        this.vistaAdmin.btnRegistrarConcierto.addActionListener(this);
        this.vistaAdmin.btnEditarZonas.addActionListener(this);
        this.vistaAdmin.btnEliminarSeleccionados.addActionListener(this);
        this.vistaAdmin.btnAnadirZona.addActionListener(this);
        if (this.vistaAdmin.btnTransacciones != null) {
            this.vistaAdmin.btnTransacciones.addActionListener(this);
        }

        this.vistaAdmin.cbmConciertos.addItemListener(this);

        poblarComboBox();
        refrescarTabla();
    }

    public FrmAdmin getVista() {
        return this.vistaAdmin;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vistaAdmin.btnRefrescar) {
            refrescarTabla();
        } else if (e.getSource() == vistaAdmin.btnCerrarSesion) {
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

    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getStateChange() == ItemEvent.SELECTED) {
            refrescarTabla();
        }
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
        FrmConcierto ventanaConcierto = new FrmConcierto();
        new ControladorConcierto(ventanaConcierto, contextoCentral, this);
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
        contextoCentral.cerrarSesion();
        vista.FrmLogin login = new vista.FrmLogin();
        new ControladorLogin(login, this.contextoCentral);
        login.setVisible(true);
        vistaAdmin.dispose();
    }
}