/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import vista.FrmLogin;
import vista.FrmAdmin;
import vista.FrmMenuPrincipal;
import vista.FrmRegistro;
import modelo.Persona;
import modelo.Usuario;
import javax.swing.JOptionPane;
/**
 *
 * @author alex_
 */
public class ControladorLogin {
    private FrmLogin vistaLogin;
    private ControladorPrincipal contextoCentral;
    
    public ControladorLogin(FrmLogin vistaLogin, ControladorPrincipal contextoCentral) {
        this.vistaLogin = vistaLogin;
        this.contextoCentral = contextoCentral;
    }
    
    public void iniciarSesion(String correo, String contrasena) {
        if (correo.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(vistaLogin, "Por favor, complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        Persona p = contextoCentral.login(correo, contrasena);
        if (p != null) {
            JOptionPane.showMessageDialog(vistaLogin, "¡Bienvenido, " + p.getNombres() + "!");

            boolean esAdmin = p instanceof Usuario;
            if (esAdmin) {
                FrmAdmin ventanaAdmin = new FrmAdmin(contextoCentral);
                ventanaAdmin.setVisible(true);
            } else {
                FrmMenuPrincipal ventanaPrincipal = new FrmMenuPrincipal(contextoCentral);
                ventanaPrincipal.setVisible(true);
            }
            vistaLogin.dispose();
            
        } else {
            JOptionPane.showMessageDialog(vistaLogin, "Credenciales incorrectas.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void abrirRegistro() {
        FrmRegistro registro = new FrmRegistro(contextoCentral);
        registro.setVisible(true);
        vistaLogin.dispose();
    }
}
