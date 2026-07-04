package controlador;

import vista.FrmHistorial;
import vista.FrmMenuPrincipal;
import modelo.Cliente;
import modelo.Concierto;
import modelo.Venta;
import coleccion.ColeccionVentas;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class ControladorHistorial {
    private FrmHistorial vista;
    private ControladorPrincipal contextoCentral;
    private Cliente cliente;

    public ControladorHistorial(FrmHistorial vista, ControladorPrincipal contextoCentral, Cliente cliente) {
        this.vista = vista;
        this.contextoCentral = contextoCentral;
        this.cliente = cliente;

        this.vista.btnVolver.addActionListener(e -> onVolver());
        
        cargarHistorial();
    }

    private void cargarHistorial() {
        ColeccionVentas coleccionVentas = contextoCentral.getColeccionVentas();
        ArrayList<Venta> ventasCliente = coleccionVentas.buscarVentasPorCliente(cliente.getDni());
        
        String[] columnas = {"Fecha", "Concierto", "Zona", "Cantidad", "Precio Unitario", "Descuento", "Total Pagado", "Tarjeta", "Puntos"};
        Object[][] datos = new Object[ventasCliente.size()][9];
        
        for (int i = 0; i < ventasCliente.size(); i++) {
            Venta v = ventasCliente.get(i);
            
            String nombreConcierto = "Desconocido";
            if (v.getConcierto() != null) {
                nombreConcierto = v.getConcierto().getNombre();
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
            
            datos[i][0] = v.getFecha();  
            datos[i][1] = nombreConcierto;
            datos[i][2] = v.getZona().getNombre();
            datos[i][3] = v.getCantidadEntradas();
            datos[i][4] = "S/ " + v.getZona().getPrecio();
            datos[i][5] = descuentoText;
            datos[i][6] = String.format("S/ %.2f", montoFinal);
            datos[i][7] = tarjetaStr;
            datos[i][8] = "+" + (v.getCantidadEntradas() * 10) + " puntos";
        }
        
        DefaultTableModel modelo = new DefaultTableModel(datos, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        vista.tblHistorial.setModel(modelo);
        
        // Ajustar anchos de las columnas
        javax.swing.table.TableColumnModel columnModel = vista.tblHistorial.getColumnModel();
        if (columnModel.getColumnCount() > 7) {
            // Columna "Concierto" (índice 1) - más grande
            javax.swing.table.TableColumn colConcierto = columnModel.getColumn(1);
            colConcierto.setPreferredWidth(170);
            
            // Columna "Zona" (índice 2) - más pequeña
            javax.swing.table.TableColumn colZona = columnModel.getColumn(2);
            colZona.setMaxWidth(55);
            colZona.setMinWidth(55);
            colZona.setPreferredWidth(55);
            
            // Columna "Cantidad" (índice 3) - ajustada a la palabra
            javax.swing.table.TableColumn colCant = columnModel.getColumn(3);
            colCant.setMaxWidth(65);
            colCant.setMinWidth(65);
            colCant.setPreferredWidth(65);
        }
    }

    private void onVolver() {
        FrmMenuPrincipal menu = new FrmMenuPrincipal(this.contextoCentral);
        menu.setVisible(true);
        vista.dispose();
    }
}
