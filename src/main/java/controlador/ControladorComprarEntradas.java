package controlador;

import modelo.Cliente;
import modelo.Concierto;
import modelo.Persona;
import modelo.Tarjeta;
import modelo.Venta;
import modelo.Zona;
import vista.FrmComprarEntradas;
import vista.FrmMenuPrincipal;

import javax.swing.table.DefaultTableModel;

public class ControladorComprarEntradas {

    private final ControladorPrincipal ctrl;
    private final FrmComprarEntradas vista;
    private final coleccion.ColeccionVentas coleccionVentas;

    private Tarjeta tarjetaActiva;
    private double precioSeleccionado = 0.0;

    public ControladorComprarEntradas(ControladorPrincipal ctrl, FrmComprarEntradas vista,
            coleccion.ColeccionVentas coleccionVentas) {
        this.ctrl = ctrl;
        this.vista = vista;
        this.coleccionVentas = coleccionVentas;

        if (ctrl.getConciertoSeleccionado() == null && !ctrl.getTodosLosConciertos().isEmpty()) {
            ctrl.setConciertoSeleccionado(ctrl.getTodosLosConciertos().get(0));
        }

        inicializarVista();
        registrarListeners();
    }

    private void inicializarVista() {
        javax.swing.SpinnerNumberModel spinnerModel = new javax.swing.SpinnerNumberModel(1, 0, 4, 1);
        vista.spnCantidad.setModel(spinnerModel);

        cargarZonasEnCombo();
        cargarTarjetasEnCombo();
        onZonaSeleccionada();
        evaluarEstadoBotonCompra();

        DefaultTableModel modelo = (DefaultTableModel) vista.tblCarrito.getModel();

        modelo.setRowCount(0);

        if (vista.tblCarrito.getColumnModel().getColumnCount() > 0) {
            javax.swing.table.TableColumnModel columnModel = vista.tblCarrito.getColumnModel();
            
            javax.swing.table.TableColumn colCheck = columnModel.getColumn(0);
            colCheck.setMaxWidth(40);
            colCheck.setMinWidth(40);
            colCheck.setPreferredWidth(40);
            
            if (columnModel.getColumnCount() > 1) {
                javax.swing.table.TableColumn colZona = columnModel.getColumn(1);
                colZona.setMaxWidth(75);
                colZona.setMinWidth(75);
                colZona.setPreferredWidth(75);
            }
            
            if (columnModel.getColumnCount() > 3) {
                javax.swing.table.TableColumn colCant = columnModel.getColumn(3);
                colCant.setMaxWidth(70);
                colCant.setMinWidth(70);
                colCant.setPreferredWidth(70);
            }
            
            boolean tieneColumnaDescuento = columnModel.getColumnCount() >= 6;
            if (tieneColumnaDescuento) {
                javax.swing.table.TableColumn colDesc = columnModel.getColumn(4);
                colDesc.setPreferredWidth(130);
                colDesc.setMinWidth(110);
                
                javax.swing.table.TableColumn colSubtotal = columnModel.getColumn(5);
                colSubtotal.setMaxWidth(85);
                colSubtotal.setMinWidth(85);
                colSubtotal.setPreferredWidth(85);
            } else if (columnModel.getColumnCount() > 4) {
                javax.swing.table.TableColumn colSubtotal = columnModel.getColumn(4);
                colSubtotal.setMaxWidth(85);
                colSubtotal.setMinWidth(85);
                colSubtotal.setPreferredWidth(85);
            }
        }
    }

    private void cargarZonasEnCombo() {
        vista.cbxZonas.removeAllItems();
        Concierto c = ctrl.getConciertoSeleccionado();
        if (c != null) {

            for (Zona z : c.getTodasLasZonas()) {
                vista.cbxZonas.addItem(z.getNombre());
            }
        }

        Persona usuario = ctrl.getUsuarioLogueado();
        if (usuario instanceof Cliente && vista.lblMisPuntos != null && vista.chkUsarPuntos != null) {
            Cliente cliente = (Cliente) usuario;
            int pts = cliente.getPuntos();
            vista.lblMisPuntos.setText("Saldo: " + pts + " pts");
            vista.chkUsarPuntos.setEnabled(pts >= 10);
            vista.chkUsarPuntos.setText("Usar Puntos (" + pts + ")");
        } else if (vista.lblMisPuntos != null && vista.chkUsarPuntos != null) {
            vista.lblMisPuntos.setText("Saldo: 0 pts");
            vista.chkUsarPuntos.setEnabled(false);
        }

    }

    public void cargarTarjetasEnCombo() {
        vista.cbxTarjeta.removeAllItems();
        boolean tieneTarjeta = false;

        if (this.tarjetaActiva != null) {
            String num = this.tarjetaActiva.getNumero();
            String ultimos = num.length() > 4 ? num.substring(num.length() - 4) : num;
            String emisor = this.tarjetaActiva.getEmisor().toString();
            vista.cbxTarjeta.addItem(emisor + " term. " + ultimos);
            tieneTarjeta = true;
        } else {
            Persona usuario = ctrl.getUsuarioLogueado();
            if (usuario instanceof Cliente) {
                Cliente c = (Cliente) usuario;
                if (c.getTarjeta() != null) {
                    String num = c.getTarjeta().getNumero();
                    String ultimos = num.length() > 4 ? num.substring(num.length() - 4) : num;
                    String emisor = c.getTarjeta().getEmisor().toString();
                    vista.cbxTarjeta.addItem(emisor + " term. " + ultimos);
                    tieneTarjeta = true;
                }
            }
        }

        if (!tieneTarjeta) {
            vista.cbxTarjeta.addItem("Sin tarjetas registradas");
        }
        vista.cbxTarjeta.addItem("Agregar nueva tarjeta...");
    }

    private void registrarListeners() {
        vista.cbxZonas.addActionListener(e -> onZonaSeleccionada());
        vista.cbxTarjeta.addActionListener(e -> onTarjetaSeleccionada());
        vista.btnAgregarCarrito.addActionListener(e -> onAgregarCarrito());
        vista.btnEliminarSeleccionados.addActionListener(e -> onEliminarSeleccionados());
        vista.btnConfirmar.addActionListener(e -> onConfirmarCompra());
        vista.btnVolver.addActionListener(e -> onVolver());

        if (vista.chkUsarPuntos != null) {
            vista.chkUsarPuntos.addActionListener(e -> actualizarTotalCarrito());
        }
    }

    private void onZonaSeleccionada() {
        String nombreSeleccionado = (String) vista.cbxZonas.getSelectedItem();
        Concierto concierto = ctrl.getConciertoSeleccionado();
        if (nombreSeleccionado != null && concierto != null) {
            Zona zona = concierto.buscarZonaPorNombre(nombreSeleccionado);
            if (zona != null) {
                this.precioSeleccionado = zona.getPrecio();
                if (vista.lblPrecio != null) {
                    vista.lblPrecio.setText(String.format("S/ %.2f", this.precioSeleccionado));
                }
                if (vista.lblDisponibilidad != null) {
                    vista.lblDisponibilidad.setText(String.valueOf(zona.getCapacidadDisponible()));
                }
            }
        }
    }

    private void onAgregarCarrito() {
        String nombreZona = (String) vista.cbxZonas.getSelectedItem();
        int cantidad = (int) vista.spnCantidad.getValue();

        if (cantidad <= 0) {
            javax.swing.JOptionPane.showMessageDialog(vista, "Seleccione una cantidad mayor a 0.");
            return;
        }

        int cantidadEnCarrito = obtenerCantidadTotalEnCarrito();
        if (cantidadEnCarrito + cantidad > 4) {
            javax.swing.JOptionPane.showMessageDialog(vista,
                    "Límite excedido. Solo puede comprar hasta 4 entradas en total.");
            return;
        }

        Concierto concierto = ctrl.getConciertoSeleccionado();
        Zona zona = concierto.buscarZonaPorNombre(nombreZona);

        if (zona.getCapacidadDisponible() < cantidad) {
            javax.swing.JOptionPane.showMessageDialog(vista,
                    "No hay suficiente capacidad disponible en la zona " + nombreZona);
            return;
        }

        DefaultTableModel modelo = (DefaultTableModel) vista.tblCarrito.getModel();

        boolean tieneColumnaDescuento = modelo.getColumnCount() >= 6;

        if (tieneColumnaDescuento) {
            modelo.addRow(new Object[] { false, nombreZona, this.precioSeleccionado, cantidad, "", 0.0 });
        } else {
            modelo.addRow(new Object[] { false, nombreZona, this.precioSeleccionado, cantidad, 0.0 });
        }

        actualizarTotalCarrito();
    }

    private void onEliminarSeleccionados() {
        DefaultTableModel modelo = (DefaultTableModel) vista.tblCarrito.getModel();
        for (int i = modelo.getRowCount() - 1; i >= 0; i--) {
            Boolean seleccionado = (Boolean) modelo.getValueAt(i, 0);
            if (seleccionado != null && seleccionado) {
                modelo.removeRow(i);
            }
        }
        actualizarTotalCarrito();
    }

    private int obtenerCantidadTotalEnCarrito() {
        DefaultTableModel modelo = (DefaultTableModel) vista.tblCarrito.getModel();
        int total = 0;
        for (int i = 0; i < modelo.getRowCount(); i++) {
            Object val = modelo.getValueAt(i, 3);
            if (val != null) {
                total += (int) val;
            }
        }
        return total;
    }

    private void actualizarTotalCarrito() {
        DefaultTableModel modelo = (DefaultTableModel) vista.tblCarrito.getModel();
        double granTotal = 0.0;

        Tarjeta t = obtenerTarjetaActual();
        Concierto concierto = ctrl.getConciertoSeleccionado();
        Persona usuario = ctrl.getUsuarioLogueado();

        double descEmisor = 0.0;
        String nombreEmisor = "";
        if (t != null && concierto != null) {
            descEmisor = concierto.getDescuentoParaEmisor(t.getEmisor());
            nombreEmisor = t.getEmisor().toString();
        }

        boolean tieneColumnaDescuento = modelo.getColumnCount() >= 6;
        
        if (tieneColumnaDescuento) {
            String colName = "Descuento";
            if (!nombreEmisor.isEmpty()) {
                colName += " (" + nombreEmisor + ")";
            }
            vista.tblCarrito.getColumnModel().getColumn(4).setHeaderValue(colName);
            vista.tblCarrito.getTableHeader().repaint();
        }

        for (int i = 0; i < modelo.getRowCount(); i++) {
            Object objPrecio = modelo.getValueAt(i, 2);
            Object objCant = modelo.getValueAt(i, 3);
            if (objPrecio == null || objCant == null) continue;
            
            double precioU = (double) objPrecio;
            int cant = (int) objCant;
            double subtotalBase = precioU * cant;

            double subtotalFinal = subtotalBase;
            String descTexto = "";

            if (descEmisor > 0) {
                subtotalFinal *= (1.0 - descEmisor);
                descTexto = (int) (descEmisor * 100) + "%";
            }

            // Lógica interactiva de puntos
            if (vista.chkUsarPuntos != null && vista.chkUsarPuntos.isSelected() && usuario instanceof Cliente) {
                Cliente c = (Cliente) usuario;
                int bloques10 = c.getPuntos() / 10;
                if (bloques10 > 5) bloques10 = 5;
                if (bloques10 > 0) {
                    double descuentoPuntos = bloques10 * 0.02;
                    subtotalFinal *= (1.0 - descuentoPuntos);
                    if (!descTexto.isEmpty())
                        descTexto += " + ";
                    descTexto += (int) (descuentoPuntos * 100) + "% (Pts)";
                }
            }

            if (descTexto.isEmpty()) {
                descTexto = "-";
            }
            
            double subtotalRedondeado = Math.round(subtotalFinal * 100.0) / 100.0;

            if (tieneColumnaDescuento) {
                modelo.setValueAt(descTexto, i, 4);
                modelo.setValueAt(subtotalRedondeado, i, 5); 
            } else {
                modelo.setValueAt(subtotalRedondeado, i, 4); 
            }

            granTotal += subtotalFinal;
        }

        if (vista.lblTotal != null) {
            vista.lblTotal.setText(String.format("Total a pagar: S/ %.2f", granTotal));
        }

        evaluarEstadoBotonCompra();
    }

    private Tarjeta obtenerTarjetaActual() {
        if (this.tarjetaActiva != null)
            return this.tarjetaActiva;
        Persona usuario = ctrl.getUsuarioLogueado();
        if (usuario instanceof Cliente) {
            return ((Cliente) usuario).getTarjeta();
        }
        return null;
    }

    private void onTarjetaSeleccionada() {
        String seleccion = (String) vista.cbxTarjeta.getSelectedItem();
        if ("Agregar nueva tarjeta...".equals(seleccion)) {
            vista.setVisible(false);
            vista.FrmTarjeta frmTarjeta = new vista.FrmTarjeta();
            new ControladorTarjeta(this.ctrl, frmTarjeta, this);
            frmTarjeta.setVisible(true);
        }
        evaluarEstadoBotonCompra();
        actualizarTotalCarrito(); 
    }

    private void evaluarEstadoBotonCompra() {
        String seleccion = (String) vista.cbxTarjeta.getSelectedItem();
        boolean tarjetaValida = seleccion != null
                && !seleccion.equals("Agregar nueva tarjeta...")
                && !seleccion.equals("Sin tarjetas registradas");

        if (tarjetaValida) {
            Tarjeta t = obtenerTarjetaActual();
            int cantidadEnCarrito = obtenerCantidadTotalEnCarrito();

            if (t != null) {
                int cantidadPrevia = t.getCantidadComprada();
                if (cantidadPrevia + cantidadEnCarrito > 4) {
                    vista.btnConfirmar.setEnabled(false);
                    vista.btnConfirmar.setText("Límite Alcanzado (Máx 4)");
                    return;
                }
            }
        }

        vista.btnConfirmar.setEnabled(tarjetaValida && obtenerCantidadTotalEnCarrito() > 0);
        vista.btnConfirmar.setText("Confirmar Compra");
    }

    private void onConfirmarCompra() {
        DefaultTableModel modelo = (DefaultTableModel) vista.tblCarrito.getModel();
        if (modelo.getRowCount() == 0) {
            javax.swing.JOptionPane.showMessageDialog(vista, "El carrito está vacío. Agregue entradas primero.");
            return;
        }

        Tarjeta tarjetaUsar = obtenerTarjetaActual();
        if (tarjetaUsar == null) {
            javax.swing.JOptionPane.showMessageDialog(vista, "Por favor, registre o seleccione una tarjeta de pago.");
            return;
        }

        Persona usuario = ctrl.getUsuarioLogueado();
        if (!(usuario instanceof Cliente)) {
            javax.swing.JOptionPane.showMessageDialog(vista, "El administrador no puede realizar compras.");
            return;
        }

        Cliente cliente = (Cliente) usuario;
        Concierto concierto = ctrl.getConciertoSeleccionado();

        try {
            double granTotalMonto = 0;
            StringBuilder resumenTransacciones = new StringBuilder("¡Compra Exitosa!\n\n");

            boolean usoPuntos = vista.chkUsarPuntos != null && vista.chkUsarPuntos.isSelected();
            int bloques10 = cliente.getPuntos() / 10;
            if (bloques10 > 5) bloques10 = 5;
            double descuentoPuntos = usoPuntos && bloques10 > 0 ? bloques10 * 0.02 : 0.0;

            for (int i = 0; i < modelo.getRowCount(); i++) {
                String nombreZona = (String) modelo.getValueAt(i, 1);
                int cantidad = (int) modelo.getValueAt(i, 3);

                Zona zona = concierto.buscarZonaPorNombre(nombreZona);

                Venta nuevaVenta = new Venta(cantidad, cliente, zona, tarjetaUsar, concierto);
                nuevaVenta.procesarCompra(tarjetaUsar.getCVV());

                double descEmisor = concierto.getDescuentoParaEmisor(tarjetaUsar.getEmisor());
                nuevaVenta.aplicarDescuentoEmisor(descEmisor);

                if (descuentoPuntos > 0) {
                    nuevaVenta.aplicarDescuentoPuntos(descuentoPuntos);
                }


                this.coleccionVentas.registrarVenta(nuevaVenta);

                granTotalMonto += nuevaVenta.getMonto();
                resumenTransacciones.append("- ").append(cantidad).append("x ").append(nombreZona)
                        .append(" (Cod: ").append(nuevaVenta.getIdTransaccion()).append(")\n");
            }

            if (usoPuntos && bloques10 > 0) {
                cliente.gastarPuntos(bloques10 * 10);
            }

            resumenTransacciones.append("\nMonto Total Pagado: S/ ").append(String.format("%.2f", granTotalMonto));

            javax.swing.JOptionPane.showMessageDialog(vista, resumenTransacciones.toString());

            FrmMenuPrincipal menu = new FrmMenuPrincipal();
            ControladorMenuPrincipal ctrlMenu = new ControladorMenuPrincipal(menu, this.ctrl);
            ctrlMenu.cargarDatosCliente();
            menu.setVisible(true);
            vista.dispose();

        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(vista, ex.getMessage(), "Error en la compra",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onVolver() {
        FrmMenuPrincipal menu = new FrmMenuPrincipal();
        ControladorMenuPrincipal ctrlMenu = new ControladorMenuPrincipal(menu, this.ctrl);
        ctrlMenu.cargarDatosCliente();
        menu.setVisible(true);
        vista.dispose();
    }

    public void setTarjetaActiva(Tarjeta t) {
        this.tarjetaActiva = t;
        cargarTarjetasEnCombo();
        if (t != null) {
            vista.cbxTarjeta.setSelectedIndex(0);
        }
        evaluarEstadoBotonCompra();
        actualizarTotalCarrito();
    }

    public FrmComprarEntradas getVista() {
        return vista;
    }
}
