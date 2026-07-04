package controlador;

import vista.FrmRegistro;
import vista.FrmLogin;
import modelo.Persona;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorRegistro implements ActionListener {

    private FrmRegistro vistaRegistro;
    private ControladorPrincipal contextoCentral;

    public ControladorRegistro(FrmRegistro vistaRegistro, ControladorPrincipal contextoCentral) {
        this.vistaRegistro = vistaRegistro;
        this.contextoCentral = contextoCentral;
        this.vistaRegistro.controlador = this;
        this.vistaRegistro.btnGuardar.addActionListener(this);
        this.vistaRegistro.btnVolver.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vistaRegistro.btnGuardar) {
            String dni = vistaRegistro.txtDni.getText();
            String nom = vistaRegistro.txtNombre.getText();
            String ape = vistaRegistro.txtApellido.getText();
            String correo = vistaRegistro.txtCorreo.getText();
            String pass = new String(vistaRegistro.txtPass.getPassword());
            java.util.Date fechaNac = vistaRegistro.dateFechaNacimiento.getDate();

            registrarUsuario(dni, nom, ape, correo, pass, fechaNac);

        } else if (e.getSource() == vistaRegistro.btnVolver) {
            volverAlLogin();
        }
    }

    public boolean registrarUsuario(String dni, String nombres, String apellidos,
            String correo, String contrasena, java.util.Date fechaNac) {

        if (estaVacio(dni) || estaVacio(nombres) || estaVacio(apellidos)
                || estaVacio(correo) || estaVacio(contrasena) || fechaNac == null) {
            JOptionPane.showMessageDialog(vistaRegistro,
                    "Por favor, complete todos los campos.",
                    "Campos vacíos",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        java.util.Calendar hoy = java.util.Calendar.getInstance();
        java.util.Calendar nac = java.util.Calendar.getInstance();
        nac.setTime(fechaNac);
        
        int edad = hoy.get(java.util.Calendar.YEAR) - nac.get(java.util.Calendar.YEAR);
        if (hoy.get(java.util.Calendar.DAY_OF_YEAR) < nac.get(java.util.Calendar.DAY_OF_YEAR)) {
            edad--;
        }
        
        if (edad < 18) {
            JOptionPane.showMessageDialog(vistaRegistro,
                    "Debe ser mayor de 18 años para registrarse.",
                    "Edad insuficiente",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!dni.trim().matches("\\d{8}")) {
            JOptionPane.showMessageDialog(vistaRegistro,
                    "El DNI debe contener exactamente 8 dígitos numéricos.",
                    "DNI inválido",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!correo.trim().contains("@") || !correo.trim().contains(".")) {
            JOptionPane.showMessageDialog(vistaRegistro,
                    "El correo electrónico ingresado no es válido.",
                    "Correo inválido",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (contrasena.trim().length() < 6) {
            JOptionPane.showMessageDialog(vistaRegistro,
                    "La contraseña debe tener al menos 6 caracteres.",
                    "Contraseña muy corta",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }

        Persona personaPorDni = contextoCentral.getColeccionPersonas().buscarPorDni(dni.trim());
        if (personaPorDni != null) {
            JOptionPane.showMessageDialog(vistaRegistro,
                    "Ya existe un usuario registrado con el DNI ingresado.",
                    "DNI duplicado",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        Persona personaPorCorreo = contextoCentral.getColeccionPersonas().buscarPorCorreo(correo.trim());
        if (personaPorCorreo != null) {
            JOptionPane.showMessageDialog(vistaRegistro,
                    "Ya existe un usuario registrado con ese correo electrónico.",
                    "Correo duplicado",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }


        String codigoAleatorio = String.valueOf((int) (Math.random() * 9000) + 1000);

        servicio.ServicioCorreo.enviarCodigo(correo.trim(), codigoAleatorio);

        JOptionPane.showMessageDialog(vistaRegistro, "Se ha enviado un codigo de verificación, revíselo en su correo.");

        vista.FrmValidarCodigo vistaValidar = new vista.FrmValidarCodigo();
        new ControladorValidarCodigo(
                vistaValidar, contextoCentral, codigoAleatorio,
                dni.trim(), nombres.trim(), apellidos.trim(), correo.trim(), contrasena.trim());

        vistaValidar.setVisible(true);
        vistaRegistro.dispose();

        return true;

    }

    public void volverAlLogin() {
        FrmLogin login = new FrmLogin();
        new ControladorLogin(login, contextoCentral);
        login.setVisible(true);
        vistaRegistro.dispose();
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
