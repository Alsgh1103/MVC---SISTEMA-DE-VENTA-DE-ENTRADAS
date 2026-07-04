package controlador;

import modelo.Concierto;
import modelo.Venta;
import vista.FrmTransaccionesAdmin;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class ControladorTransaccionesAdmin {
    private FrmTransaccionesAdmin vista;
    private ControladorPrincipal contextoCentral;
    private Concierto conciertoFiltro;
    private ControladorAdmin ctrlAdmin;

    public ControladorTransaccionesAdmin(FrmTransaccionesAdmin vista, ControladorPrincipal contextoCentral, Concierto conciertoFiltro, ControladorAdmin ctrlAdmin) {
        this.vista = vista;
        this.contextoCentral = contextoCentral;
        this.conciertoFiltro = conciertoFiltro;
        this.ctrlAdmin = ctrlAdmin;

        if (this.conciertoFiltro != null) {
            this.vista.setTitle("Transacciones - " + conciertoFiltro.getNombre());
        } else {
            this.vista.setTitle("Todas las Transacciones");
        }
        
        this.vista.setLocationRelativeTo(null);
        
        if (this.vista.btnVolver != null) {
            this.vista.btnVolver.addActionListener(e -> volver());
        }

        cargarTransacciones();
    }

    private void volver() {
        if (ctrlAdmin != null) {
            ctrlAdmin.getVista().setVisible(true);
        }
        vista.dispose();
    }

    private void cargarTransacciones() {
        ArrayList<Venta> listaVentas;
        
        if (conciertoFiltro != null) {
            listaVentas = contextoCentral.getColeccionVentas().buscarVentasPorConcierto(conciertoFiltro);
        } else {
            listaVentas = contextoCentral.getColeccionVentas().getTodasLasVentas();
        }

        String[] columnas = {"ID Transacción", "Fecha", "Cliente", "DNI", "Zona", "Cantidad", "Total", "Tarjeta"};
        Object[][] datos = new Object[listaVentas.size()][columnas.length];

        for (int i = 0; i < listaVentas.size(); i++) {
            Venta v = listaVentas.get(i);
            datos[i][0] = v.getIdTransaccion();
            datos[i][1] = v.getFecha();
            datos[i][2] = v.getCliente().getNombres();
            datos[i][3] = v.getCliente().getDni();
            datos[i][4] = v.getZona().getNombre();
            datos[i][5] = v.getCantidadEntradas();
            datos[i][6] = String.format("S/ %.2f", v.getMonto());
            datos[i][7] = v.getTarjeta() != null ? v.getTarjeta().getEmisor().toString() : "N/A";
        }

        DefaultTableModel modelo = new DefaultTableModel(datos, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        if (vista.tblTransacciones != null) {
            vista.tblTransacciones.setModel(modelo);
        }
    }
}
