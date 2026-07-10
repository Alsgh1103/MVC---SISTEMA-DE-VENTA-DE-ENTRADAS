package controlador;

import vista.FrmHistorial;
import vista.FrmMenuPrincipal;
import modelo.Cliente;
import modelo.Venta;
import coleccion.ColeccionVentas;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import javax.swing.JOptionPane;

public class ControladorHistorial {
    private FrmHistorial vista;
    private ControladorPrincipal contextoCentral;
    private Cliente cliente;
    private ArrayList<Venta> ventasCliente;

    public ControladorHistorial(FrmHistorial vista, ControladorPrincipal contextoCentral, Cliente cliente) {
        this.vista = vista;
        this.contextoCentral = contextoCentral;
        this.cliente = cliente;

        this.vista.btnVolver.addActionListener(e -> onVolver());
        this.vista.btnDevolver.addActionListener(e -> onDevolver());
        
        cargarHistorial();
    }

    private void cargarHistorial() {
        ColeccionVentas coleccionVentas = contextoCentral.getColeccionVentas();
        ventasCliente = coleccionVentas.buscarVentasPorCliente(cliente.getDni());
        
        String[] columnas = {"✔", "Fecha", "Concierto", "Artista", "Zona", "Cantidad", "Precio Unitario", "Descuento", "Total Pagado", "Tarjeta", "Puntos"};
        Object[][] datos = new Object[ventasCliente.size()][11];
        
        for (int i = 0; i < ventasCliente.size(); i++) {
            Venta v = ventasCliente.get(i);
            
            String nombreConcierto = "Desconocido";
            String artista = "Desconocido";
            if (v.getConcierto() != null) {
                nombreConcierto = v.getConcierto().getNombre();
                artista = v.getConcierto().getArtista();
            }
            
            double precioOriginal = v.getZona().getPrecio() * v.getCantidadEntradas();
            double montoFinal = v.getMonto();
            String descuentoText = "-";
            if (precioOriginal > montoFinal && precioOriginal > 0) {
                double diff = precioOriginal - montoFinal;
                int porcentaje = (int) Math.round((diff / precioOriginal) * 100);
                descuentoText = porcentaje + "%";
            }
            
            String tarjetaStr = "-";
            if (v.getTarjeta() != null) {
                tarjetaStr = v.getTarjeta().getEmisor().toString();
            }

            boolean esDevuelta = "DEVUELTA".equals(v.getEstado());
            String tachadoA = esDevuelta ? "<html><strike><font color='red'>" : "";
            String tachadoC = esDevuelta ? "</font></strike></html>" : "";
            
            datos[i][0] = false; 
            datos[i][1] = tachadoA + v.getFecha() + tachadoC;  
            datos[i][2] = tachadoA + nombreConcierto + tachadoC;
            datos[i][3] = tachadoA + artista + tachadoC;
            datos[i][4] = tachadoA + v.getZona().getNombre() + tachadoC;
            datos[i][5] = tachadoA + v.getCantidadEntradas() + tachadoC;
            datos[i][6] = tachadoA + "S/ " + v.getZona().getPrecio() + tachadoC;
            datos[i][7] = tachadoA + descuentoText + tachadoC;
            datos[i][8] = tachadoA + String.format("S/ %.2f", montoFinal) + tachadoC;
            datos[i][9] = tachadoA + tarjetaStr + tachadoC;
            datos[i][10] = tachadoA + "+" + (v.getCantidadEntradas() * 10) + " puntos" + tachadoC;
        }
        
        DefaultTableModel modelo = new DefaultTableModel(datos, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 0) {
                    return !"DEVUELTA".equals(ventasCliente.get(row).getEstado());
                }
                return false;
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Boolean.class;
                return Object.class;
            }
        };
        
        vista.tblHistorial.setModel(modelo);
        
        javax.swing.table.TableColumnModel columnModel = vista.tblHistorial.getColumnModel();
        if (columnModel.getColumnCount() > 8) {
            javax.swing.table.TableColumn colCheck = columnModel.getColumn(0);
            colCheck.setMaxWidth(30);
            colCheck.setMinWidth(30);
            colCheck.setPreferredWidth(30);

            javax.swing.table.TableColumn colConcierto = columnModel.getColumn(2);
            colConcierto.setPreferredWidth(200);
            
            javax.swing.table.TableColumn colArtista = columnModel.getColumn(3);
            colArtista.setPreferredWidth(150);
            
            javax.swing.table.TableColumn colZona = columnModel.getColumn(4);
            colZona.setMaxWidth(55);
            colZona.setMinWidth(55);
            colZona.setPreferredWidth(55);
            
            javax.swing.table.TableColumn colCant = columnModel.getColumn(5);
            colCant.setMaxWidth(65);
            colCant.setMinWidth(65);
            colCant.setPreferredWidth(65);
        }
    }

    private void onDevolver() {
        DefaultTableModel modelo = (DefaultTableModel) vista.tblHistorial.getModel();
        int devueltas = 0;
        boolean error = false;

        for (int i = 0; i < modelo.getRowCount(); i++) {
            Boolean isChecked = (Boolean) modelo.getValueAt(i, 0);
            if (Boolean.TRUE.equals(isChecked)) {
                Venta ventaSeleccionada = ventasCliente.get(i);
                try {
                    ventaSeleccionada.devolver();
                    devueltas++;
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(vista, "Error al devolver entrada: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    error = true;
                }
            }
        }

        if (devueltas > 0) {
            contextoCentral.guardarEstado();
            cargarHistorial();
            JOptionPane.showMessageDialog(vista, "Se devolvieron " + devueltas + " compra(s) exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else if (!error) {
            JOptionPane.showMessageDialog(vista, "Seleccione al menos una compra para devolver.", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onVolver() {
        FrmMenuPrincipal menu = new FrmMenuPrincipal();
        ControladorMenuPrincipal ctrlMenu = new ControladorMenuPrincipal(menu, this.contextoCentral);
        ctrlMenu.cargarDatosCliente();
        menu.setVisible(true);
        vista.dispose();
    }
}
