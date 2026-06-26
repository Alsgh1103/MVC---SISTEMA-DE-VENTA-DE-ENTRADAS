package controlador;

import modelo.Cliente;
import modelo.Persona;
import modelo.Tarjeta;
import vista.FrmCliente;
import vista.FrmTarjeta;

/**
 * Controlador dedicado a FrmTarjeta.
 *
 * Responsabilidades:
 *   - Inicializar la etiqueta de bienvenida personalizada.
 *   - Reaccionar al botón "Guardar Tarjeta":
 *       · Lee los campos de texto de la vista.
 *       · Delega la validación y construcción al Modelo (Tarjeta.crear).
 *       · Si el usuario eligió guardar la tarjeta, la persiste en el cliente.
 *       · Devuelve el control a FrmCliente mediante ControladorCliente.
 *   - Reaccionar al botón "Volver": restaurar la vista anterior.
 *
 * La vista (FrmTarjeta) es tratada como un cascarón puramente visual;
 * este controlador accede a sus componentes públicos directamente.
 */
public class ControladorTarjeta {

    private final ControladorPrincipal ctrl;
    private final FrmTarjeta vista;

    /**
     * Referencia al controlador de FrmCliente para poder devolverle
     * la tarjeta recién creada sin acoplar las vistas entre sí.
     */
    private final ControladorCliente controladorCliente;

    // ---------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------

    public ControladorTarjeta(ControladorPrincipal ctrl,
                               FrmTarjeta vista,
                               ControladorCliente controladorCliente) {
        this.ctrl               = ctrl;
        this.vista              = vista;
        this.controladorCliente = controladorCliente;

        inicializarVista();
        registrarListeners();
    }

    // ---------------------------------------------------------------
    // INICIALIZACIÓN
    // ---------------------------------------------------------------

    /** Personaliza la etiqueta de bienvenida con el nombre del usuario logueado. */
    private void inicializarVista() {
        Persona usuario = ctrl.getUsuarioLogueado();
        if (usuario != null) {
            vista.lblRegistraNombre.setText(
                "Registrar tu tarjeta, " + usuario.getNombres());
        }
    }

    // ---------------------------------------------------------------
    // REGISTRO DE LISTENERS
    // ---------------------------------------------------------------

    /** Conecta los botones de la vista con su lógica correspondiente. */
    private void registrarListeners() {
        vista.btnGuardar.addActionListener(e -> onGuardarTarjeta());
        vista.btnVolver.addActionListener(e  -> onVolver());
        // CheckTarjeta y txtNumeroTarjeta no requieren lógica adicional aquí
    }

    // ---------------------------------------------------------------
    // HANDLERS DE EVENTOS
    // ---------------------------------------------------------------

    /**
     * Guarda la tarjeta:
     *   1. Lee y normaliza los campos de la vista.
     *   2. Delega la validación completa al método de fábrica Tarjeta.crear()
     *      (campos vacíos V1, formato V2, CVV numérico V3).
     *   3. Si el usuario marcó "guardar para futuras compras", persiste en cliente.
     *   4. Notifica a ControladorCliente con la tarjeta activa.
     *   5. Muestra confirmación y cierra esta ventana.
     */
    private void onGuardarTarjeta() {
        // Lectura de la vista (normalización idéntica a la original)
        String nroTarjeta   = vista.txtNumeroTarjeta.getText().trim().replaceAll("\\s+", "");
        String titular      = vista.txtNombreTarjeta.getText().trim();
        String vencimiento  = vista.txtFechaVencimiento.getText().trim();
        String cvvStr       = vista.txtCvv.getText().trim();
        boolean guardarFuturas = vista.CheckTarjeta.isSelected();

        // Validación y construcción delegadas al Modelo —
        // Tarjeta.crear() lanza IllegalArgumentException con el mensaje exacto.
        Tarjeta nuevaTarjeta;
        try {
            nuevaTarjeta = Tarjeta.crear(nroTarjeta, titular, vencimiento, cvvStr);
        } catch (IllegalArgumentException ex) {
            javax.swing.JOptionPane.showMessageDialog(
                vista,
                ex.getMessage(),
                "Error",
                javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Si el usuario quiere guardar la tarjeta para futuras compras,
        // la persistimos en el objeto Cliente de la sesión.
        if (guardarFuturas) {
            Persona usuario = ctrl.getUsuarioLogueado();
            if (usuario instanceof Cliente) {
                ((Cliente) usuario).setTarjeta(nuevaTarjeta);
            }
        }

        // Notificar a ControladorCliente (y, transitivamente, a FrmCliente)
        // que hay una tarjeta activa para esta sesión.
        if (controladorCliente != null) {
            controladorCliente.setTarjetaActiva(nuevaTarjeta);
        }

        // Restaurar la vista del cliente y cerrar esta ventana
        FrmCliente frmCliente = obtenerFrmCliente();
        if (frmCliente != null) {
            frmCliente.setVisible(true);
        }

        javax.swing.JOptionPane.showMessageDialog(vista, "Tarjeta registrada correctamente.");
        vista.dispose();
    }

    /** Vuelve a FrmCliente sin guardar ningún dato. */
    private void onVolver() {
        FrmCliente frmCliente = obtenerFrmCliente();
        if (frmCliente != null) {
            frmCliente.setVisible(true);
        }
        vista.dispose();
    }

    // ---------------------------------------------------------------
    // UTILIDADES
    // ---------------------------------------------------------------

    /**
     * Obtiene la referencia a FrmCliente desde la vista actual.
     * FrmTarjeta guarda internamente una referencia pública a vistaCliente.
     */
    private FrmCliente obtenerFrmCliente() {
        return vista.vistaCliente;
    }
}
