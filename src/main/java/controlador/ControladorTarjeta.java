package controlador;

import modelo.Cliente;
import modelo.Persona;
import modelo.Tarjeta;
import vista.FrmComprarEntradas;
import vista.FrmTarjeta;

public class ControladorTarjeta {

    private final ControladorPrincipal ctrl;
    private final FrmTarjeta vista;
    private final ControladorComprarEntradas controladorCliente;

    public ControladorTarjeta(ControladorPrincipal ctrl, FrmTarjeta vista,
            ControladorComprarEntradas controladorCliente) {
        this.ctrl = ctrl;
        this.vista = vista;
        this.controladorCliente = controladorCliente;

        registrarListeners();
    }

    private void registrarListeners() {
        vista.btnGuardar.addActionListener(e -> onGuardarTarjeta());
        vista.btnVolver1.addActionListener(e -> onVolver());

        vista.txtNumeroTarjeta.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private boolean formatting = false;

            private void formatAndDetect() {
                if (formatting)
                    return;
                formatting = true;
                javax.swing.SwingUtilities.invokeLater(() -> {
                    try {
                        String text = vista.txtNumeroTarjeta.getText().replaceAll("[^\\d]", "");
                        if (text.length() > 19) {
                            text = text.substring(0, 19);
                        }
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < text.length(); i++) {
                            if (i > 0 && i % 4 == 0)
                                sb.append(" ");
                            sb.append(text.charAt(i));
                        }
                        vista.txtNumeroTarjeta.setText(sb.toString());
                        actualizarLblTipo(text);
                    } finally {
                        formatting = false;
                    }
                });
            }

            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                formatAndDetect();
            }

            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                formatAndDetect();
            }

            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                formatAndDetect();
            }
        });
    }

    private void actualizarLblTipo(String numeroSoloDigitos) {
        Tarjeta.Emisor emisor = detectarEmisorParcial(numeroSoloDigitos);

        String rutaImagen = null;
        switch (emisor) {
            case VISA:
                rutaImagen = "/img/visa.png";
                break;
            case MASTERCARD:
                rutaImagen = "/img/master.png";
                break;
            case DINERS:
                rutaImagen = "/img/dinners.png";
                break;
            case AMERICAN_EXPRESS:
                rutaImagen = "/img/amex.png";
                break;
            default:
                rutaImagen = null;
                break;
        }

        if (rutaImagen != null) {
            try {
                java.net.URL imgUrl = getClass().getResource(rutaImagen);
                if (imgUrl != null) {
                    javax.swing.ImageIcon icono = new javax.swing.ImageIcon(imgUrl);
                    vista.lblTipo.setIcon(icono);
                    vista.lblTipo.setText("");
                } else {
                    vista.lblTipo.setIcon(null);
                    vista.lblTipo.setText(emisor.toString());
                }
            } catch (Exception ex) {
                vista.lblTipo.setIcon(null);
                vista.lblTipo.setText(emisor.toString());
            }
        } else {
            vista.lblTipo.setIcon(null);
            vista.lblTipo.setText("-----");
        }
    }

    private Tarjeta.Emisor detectarEmisorParcial(String numero) {
        if (numero == null || numero.isEmpty())
            return Tarjeta.Emisor.DESCONOCIDO;
        if (numero.startsWith("4"))
            return Tarjeta.Emisor.VISA;
        if (numero.matches("^5[1-5].*") || numero.matches("^2(?:2[2-9]|[3-6]|7[0-2]).*"))
            return Tarjeta.Emisor.MASTERCARD;
        if (numero.matches("^3[47].*"))
            return Tarjeta.Emisor.AMERICAN_EXPRESS;
        if (numero.matches("^3(?:0[0-5]|[68]).*"))
            return Tarjeta.Emisor.DINERS;
        return Tarjeta.Emisor.DESCONOCIDO;
    }

    private void onGuardarTarjeta() {
        String nroTarjeta = vista.txtNumeroTarjeta.getText().trim().replaceAll("[^\\d]", "");
        String titular = vista.txtNombreTarjeta.getText().trim();

        int mesInt = vista.dateMonth.getMonth() + 1;
        int anioInt = vista.dateYear.getYear();

        java.time.YearMonth ym = java.time.YearMonth.of(anioInt, mesInt);
        String vencimiento = ym.atEndOfMonth().toString();

        String cvvStr = vista.txtCvv.getText().trim();
        boolean guardarFuturas = vista.CheckTarjeta.isSelected();

        Tarjeta nuevaTarjeta;
        try {
            nuevaTarjeta = Tarjeta.crear(nroTarjeta, titular, vencimiento, cvvStr);
        } catch (IllegalArgumentException ex) {
            javax.swing.JOptionPane.showMessageDialog(
                    vista,
                    ex.getMessage(),
                    "Error de validación",
                    javax.swing.JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (guardarFuturas) {
            Persona usuario = ctrl.getUsuarioLogueado();
            if (usuario instanceof Cliente) {
                ((Cliente) usuario).setTarjeta(nuevaTarjeta);
                ctrl.guardarPersonas();
            }
        }

        if (controladorCliente != null) {
            controladorCliente.setTarjetaActiva(nuevaTarjeta);
        }

        FrmComprarEntradas frmCliente = obtenerFrmCliente();
        if (frmCliente != null) {
            frmCliente.setVisible(true);
        }

        javax.swing.JOptionPane.showMessageDialog(vista, "Tarjeta registrada correctamente.");
        vista.dispose();
    }

    private void onVolver() {
        FrmComprarEntradas frmCliente = obtenerFrmCliente();
        if (frmCliente != null) {
            frmCliente.setVisible(true);
        }
        vista.dispose();
    }

    private FrmComprarEntradas obtenerFrmCliente() {
        if (controladorCliente != null) {
            return controladorCliente.getVista();
        }
        return null;
    }
}
