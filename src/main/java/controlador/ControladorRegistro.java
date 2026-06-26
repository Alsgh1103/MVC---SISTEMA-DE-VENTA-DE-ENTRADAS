package controlador;

import vista.FrmRegistro;
import vista.FrmLogin;
import modelo.Persona;
import javax.swing.JOptionPane;


public class ControladorRegistro {

    private FrmRegistro vistaRegistro;
    private ControladorPrincipal contextoCentral;

    public ControladorRegistro(FrmRegistro vistaRegistro, ControladorPrincipal contextoCentral) {
        this.vistaRegistro = vistaRegistro;
        this.contextoCentral = contextoCentral;
    }

    
    public boolean registrarUsuario(String dni, String nombres, String apellidos,
                                    String correo, String contrasena) {

       
        if (estaVacio(dni) || estaVacio(nombres) || estaVacio(apellidos)
                || estaVacio(correo) || estaVacio(contrasena)) {
            JOptionPane.showMessageDialog(vistaRegistro,
                    "Por favor, complete todos los campos.",
                    "Campos vacíos",
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

        
        contextoCentral.registrarNuevoCliente(
                dni.trim(),
                nombres.trim(),
                apellidos.trim(),
                correo.trim(),
                contrasena.trim(),
                false
        );

        JOptionPane.showMessageDialog(vistaRegistro,
                "¡Registro exitoso! Bienvenido, " + nombres.trim() + ".\nYa puedes iniciar sesión.",
                "Registro completado",
                JOptionPane.INFORMATION_MESSAGE);

        
        volverAlLogin();
        return true;
    }

    
    public void volverAlLogin() {
        FrmLogin login = new FrmLogin(contextoCentral);
        login.setVisible(true);
        vistaRegistro.dispose();
    }

    private boolean estaVacio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
