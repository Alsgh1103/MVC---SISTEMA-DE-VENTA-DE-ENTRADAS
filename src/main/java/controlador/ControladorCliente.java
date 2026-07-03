package controlador;

import modelo.Cliente;
import modelo.Concierto;
import modelo.Persona;
import modelo.Tarjeta;
import modelo.Venta;
import modelo.Zona;
import vista.FrmCliente;
import vista.FrmMenuPrincipal;
import vista.FrmTarjeta;

import java.util.ArrayList;

/**
 * Controlador dedicado a FrmCliente.
 *
 * Responsabilidades:
 *   - Inicializar el estado visual de la vista (etiquetas, combos).
 *   - Reaccionar a los eventos de usuario (cambio de zona, cambio de cantidad,
 *     confirmar compra, navegar a tarjeta, volver al menú).
 *   - Delegar toda la lógica de negocio a ControladorPrincipal / Modelo.
 *   - Mostrar mensajes de éxito y error con JOptionPane.
 *
 * La vista (FrmCliente) es tratada como un cascarón puramente visual;
 * este controlador accede a sus componentes públicos directamente.
 */
public class ControladorCliente {

    private final ControladorPrincipal ctrl;
    private final FrmCliente vista;
    private final coleccion.ColeccionVentas coleccionVentas;

    /** Tarjeta activa para la sesión de compra actual (puede venir de FrmTarjeta). */
    private Tarjeta tarjetaActiva;

    /** Precio unitario de la zona actualmente seleccionada en el combo. */
    private double precioSeleccionado = 0.0;

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    public ControladorCliente(ControladorPrincipal ctrl, FrmCliente vista, coleccion.ColeccionVentas coleccionVentas) {
        this.ctrl  = ctrl;
        this.vista = vista;
        this.coleccionVentas = coleccionVentas;

        // Asegurarse de tener un concierto seleccionado en el controlador principal
        if (ctrl.getConciertoSeleccionado() == null
                && !ctrl.getTodosLosConciertos().isEmpty()) {
            ctrl.setConciertoSeleccionado(ctrl.getTodosLosConciertos().get(0));
        }

        inicializarVista();
        registrarListeners();
    }

    // ---------------------------------------------------------------
    // INICIALIZACIÓN
    // ---------------------------------------------------------------

    /**
     * Configura el estado inicial de todos los componentes visuales:
     * etiqueta de bienvenida, tipo de socio, zonas y tarjetas.
     */
    private void inicializarVista() {
        // Etiqueta de bienvenida y tipo de cliente
        Persona usuario = ctrl.getUsuarioLogueado();
        if (usuario != null) {
            vista.lblBienvenida.setText(
                "Bienvenido, " + usuario.getNombres() + " " + usuario.getApellidos());
            if (usuario instanceof Cliente) {
                Cliente c = (Cliente) usuario;
                vista.lblSocio.setText(
                    c.isSocio() ? "Socio: Platino (30% desc)" : "Cliente Regular");
            }
        } else {
            vista.lblBienvenida.setText("Bienvenido, Invitado");
        }

        cargarZonasEnCombo();
        cargarTarjetasEnCombo();
        
        // Cargar los datos de la primera zona seleccionada por defecto al abrir la ventana
        onZonaSeleccionada();
        evaluarEstadoBotonCompra();
    }

    /** Rellena el combo de zonas con las zonas del concierto actualmente seleccionado. */
    private void cargarZonasEnCombo() {
        vista.cbxZonas.removeAllItems();
        Concierto concierto = ctrl.getConciertoSeleccionado();
        if (concierto != null) {
            ArrayList<Zona> lista = concierto.getTodasLasZonas();
            for (Zona z : lista) {
                vista.cbxZonas.addItem(z.getNombre());
            }
        }
    }

    /**
     * Rellena el combo de tarjetas según la tarjeta guardada del cliente.
     * Siempre agrega la opción "Agregar nueva tarjeta..." al final.
     */
    public void cargarTarjetasEnCombo() {
        vista.cbxTarjeta.removeAllItems();
        boolean tieneTarjeta = false;

        if (this.tarjetaActiva != null) {
            String num = this.tarjetaActiva.getNumero();
            String ultimos = num.length() > 4 ? num.substring(num.length() - 4) : num;
            vista.cbxTarjeta.addItem("Tarjeta term. " + ultimos);
            tieneTarjeta = true;
        } else {
            Persona usuario = ctrl.getUsuarioLogueado();
            if (usuario instanceof Cliente) {
                Cliente c = (Cliente) usuario;
                if (c.getTarjeta() != null) {
                    String num = c.getTarjeta().getNumero();
                    String ultimos = num.length() > 4
                        ? num.substring(num.length() - 4) : num;
                    vista.cbxTarjeta.addItem("Tarjeta term. " + ultimos);
                    tieneTarjeta = true;
                }
            }
        }

        if (!tieneTarjeta) {
            vista.cbxTarjeta.addItem("Sin tarjetas registradas");
        }

        vista.cbxTarjeta.addItem("Agregar nueva tarjeta...");
    }

    // ---------------------------------------------------------------
    // REGISTRO DE LISTENERS
    // ---------------------------------------------------------------

    /**
     * Conecta cada componente interactivo de la vista con su lógica
     * correspondiente en este controlador.
     */
    private void registrarListeners() {
        // Cambio de zona en el combo → actualizar precio, disponibilidad y total
        vista.cbxZonas.addActionListener(e -> onZonaSeleccionada());

        // Cambio en el spinner de cantidad → recalcular total
        vista.spnCantidad.addChangeListener(e -> actualizarTotal());

        // Selección en el combo de tarjeta → detectar "Agregar nueva tarjeta..."
        vista.cbxTarjeta.addActionListener(e -> onTarjetaSeleccionada());

        // Botón Confirmar Compra
        vista.btnConfirmar.addActionListener(e -> onConfirmarCompra());

        // Botón Volver
        vista.btnVolver.addActionListener(e -> onVolver());
    }

    // ---------------------------------------------------------------
    // HANDLERS DE EVENTOS
    // ---------------------------------------------------------------

    /** Responde al cambio de zona: actualiza precio unitario, disponibilidad y total. */
    private void onZonaSeleccionada() {
        String nombreSeleccionado = (String) vista.cbxZonas.getSelectedItem();
        Concierto concierto = ctrl.getConciertoSeleccionado();
        if (nombreSeleccionado != null && concierto != null) {
            Zona zona = concierto.buscarZonaPorNombre(nombreSeleccionado);
            if (zona != null) {
                this.precioSeleccionado = zona.getPrecio();
                vista.lblPrecio.setText("S/ " + zona.getPrecio());
                vista.lblCapacidad.setText("Disponibles: " + zona.getCapacidadDisponible());
                actualizarTotal();
            }
        }
    }

    /**
     * Recalcula y muestra el total a pagar según cantidad, precio unitario,
     * descuento de socio (30%) y descuento por emisor de tarjeta (multiplicativos).
     */
    private void actualizarTotal() {
        int cantidad = (int) vista.spnCantidad.getValue();
        double total = this.precioSeleccionado * cantidad;

        // Descuento de socio (30%) — multiplicativo
        Persona usuario = ctrl.getUsuarioLogueado();
        if (usuario instanceof Cliente && ((Cliente) usuario).isSocio()) {
            total *= 0.70;
        }

        // Descuento por emisor de tarjeta — multiplicativo sobre el resultado anterior
        Tarjeta t = obtenerTarjetaActual();
        Concierto concierto = ctrl.getConciertoSeleccionado();
        if (t != null && concierto != null) {
            double descEmisor = concierto.getDescuentoParaEmisor(t.getEmisor());
            if (descEmisor > 0) {
                total *= (1.0 - descEmisor);
            }
        }

        vista.lblTotal.setText("Total S/ " + String.format("%.2f", total));
    }

    /**
     * Retorna la tarjeta actualmente seleccionada para la sesión de compra.
     * Prioridad: tarjetaActiva (recién registrada) > tarjeta guardada del cliente.
     */
    private Tarjeta obtenerTarjetaActual() {
        if (this.tarjetaActiva != null) return this.tarjetaActiva;
        Persona usuario = ctrl.getUsuarioLogueado();
        if (usuario instanceof Cliente) {
            return ((Cliente) usuario).getTarjeta();
        }
        return null;
    }

    /**
     * Detecta si el usuario elige "Agregar nueva tarjeta...":
     * abre FrmTarjeta y oculta la vista actual.
     */
    private void onTarjetaSeleccionada() {
        String seleccion = (String) vista.cbxTarjeta.getSelectedItem();
        if ("Agregar nueva tarjeta...".equals(seleccion)) {
            // Se instancia FrmTarjeta. En su constructor, ella misma invocará
            // a la fábrica (ctrl.crearControladorTarjeta) pasándose como vista válida.
            vista.setVisible(false);
            vista.FrmTarjeta frmTarjeta = new vista.FrmTarjeta(this.ctrl, vista);
            frmTarjeta.setVisible(true);
        }
        evaluarEstadoBotonCompra();
    }

    /**
     * Habilita o deshabilita el botón de confirmar compra dependiendo
     * de si hay una tarjeta válida seleccionada.
     */
    private void evaluarEstadoBotonCompra() {
        String seleccion = (String) vista.cbxTarjeta.getSelectedItem();
        boolean tarjetaValida = seleccion != null 
                && !seleccion.equals("Agregar nueva tarjeta...") 
                && !seleccion.equals("Sin tarjetas registradas");
        vista.btnConfirmar.setEnabled(tarjetaValida);
    }

    /**
     * Procesa la confirmación de compra usando la lógica que antes estaba en
     * ControladorPrincipal (Venta + ColeccionVentas).
     */
    private void onConfirmarCompra() {
        String nombreZona = (String) vista.cbxZonas.getSelectedItem();
        int cantidad      = (int) vista.spnCantidad.getValue();

        // Resolver tarjeta: la activa (recién registrada) tiene prioridad
        Tarjeta tarjetaUsar = this.tarjetaActiva;
        Persona usuario = ctrl.getUsuarioLogueado();
        if (tarjetaUsar == null) {
            if (usuario instanceof Cliente) {
                tarjetaUsar = ((Cliente) usuario).getTarjeta();
            }
        }

        if (tarjetaUsar == null) {
            javax.swing.JOptionPane.showMessageDialog(
                vista,
                "Por favor, registre o seleccione una tarjeta de pago.");
            return;
        }

        try {
            Concierto conciertoSeleccionado = ctrl.getConciertoSeleccionado();
            if (conciertoSeleccionado == null) throw new Exception("Seleccione un concierto primero.");

            Zona zonaEncontrada = conciertoSeleccionado.buscarZonaPorNombre(nombreZona);
            if (zonaEncontrada == null) throw new Exception("Zona no encontrada en el sistema.");

            if (!(usuario instanceof Cliente)) throw new Exception("El administrador no puede realizar compras.");

            Cliente cliente = (Cliente) usuario;

            // Validación interna de Venta
            Venta nuevaVenta = new Venta(cantidad, cliente, zonaEncontrada, tarjetaUsar);
            nuevaVenta.procesarCompra(tarjetaUsar.getCVV());

            // Descuento por emisor de tarjeta aplicado multiplicativamente DESPUÉS
            // de procesarCompra() para no interferir con las validaciones internas.
            double descEmisor = conciertoSeleccionado.getDescuentoParaEmisor(tarjetaUsar.getEmisor());
            nuevaVenta.aplicarDescuentoEmisor(descEmisor);

            // Registro (Dependencias inyectadas y centralizadas)
            conciertoSeleccionado.registrarVenta(nuevaVenta);
            this.coleccionVentas.registrarVenta(nuevaVenta);

            // Respuesta visual de éxito
            javax.swing.JOptionPane.showMessageDialog(
                vista,
                "¡Compra Exitosa!\n"
                + "Código Transacción: " + nuevaVenta.getIdTransaccion() + "\n"
                + "Monto Total: S/ "     + String.format("%.2f", nuevaVenta.getMonto()) + "\n"
                + "Asientos restantes: " + nuevaVenta.getZona().getCapacidadDisponible());

            // Navegar al menú principal
            vista.FrmMenuPrincipal menu = new vista.FrmMenuPrincipal(this.ctrl);
            menu.setVisible(true);
            vista.dispose();

        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(
                vista,
                ex.getMessage(),
                "Error en la compra",
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Navega de vuelta al menú principal. */
    private void onVolver() {
        FrmMenuPrincipal menu = new FrmMenuPrincipal(this.ctrl);
        menu.setVisible(true);
        vista.dispose();
    }

    // ---------------------------------------------------------------
    // API PÚBLICA (llamada desde ControladorTarjeta al volver)
    // ---------------------------------------------------------------

    /**
     * Establece la tarjeta activa para la sesión de compra actual.
     * También recarga el combo de tarjetas para reflejar el cambio.
     *
     * @param t  Tarjeta recién registrada desde FrmTarjeta.
     */
    public void setTarjetaActiva(Tarjeta t) {
        this.tarjetaActiva = t;
        cargarTarjetasEnCombo();
        if (t != null) {
            vista.cbxTarjeta.setSelectedIndex(0);
        }
        evaluarEstadoBotonCompra();
    }
}
