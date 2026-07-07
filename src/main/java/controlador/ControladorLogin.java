/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import vista.FrmLogin;
import vista.FrmAdminConciertos;
import vista.FrmMenuPrincipal;
import vista.FrmRegistro;
import modelo.Persona;
import modelo.Usuario;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 *
 * @author alex_
 */
public class ControladorLogin implements ActionListener {
    private FrmLogin vistaLogin;
    private ControladorPrincipal contextoCentral;
    
    public ControladorLogin(FrmLogin vistaLogin, ControladorPrincipal contextoCentral) {
        this.vistaLogin = vistaLogin;
        this.contextoCentral = contextoCentral;
        
        this.vistaLogin.btnIniciarSesion.addActionListener(this);
        this.vistaLogin.btnRegistrarse.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vistaLogin.btnIniciarSesion) {
            String correo = vistaLogin.txtCorreo.getText();
            String contrasena = new String(vistaLogin.txtContrasena.getPassword());
            iniciarSesion(correo, contrasena);
        }
        
        if (e.getSource() == vistaLogin.btnRegistrarse) {
            abrirRegistro();
        }
    }
    
    public void iniciarSesion(String correo, String contrasena) {
        vistaLogin.btnIniciarSesion.setEnabled(false);
        try {
            if (correo.isEmpty() || contrasena.isEmpty()) {
                JOptionPane.showMessageDialog(vistaLogin, "Por favor, complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        Persona p = contextoCentral.login(correo, contrasena);
        if (p != null) {
            JOptionPane.showMessageDialog(vistaLogin, "¡Bienvenido, " + p.getNombres() + "!");

            boolean esAdmin = p instanceof Usuario;
            boolean esSuperAdmin = esAdmin && p.getCorreo().equals("admin");

            if (esSuperAdmin) {
                vista.FrmAdmin ventanaSuperAdmin = new vista.FrmAdmin();
                new ControladorAdmin(ventanaSuperAdmin, contextoCentral);
                ventanaSuperAdmin.setVisible(true);
            } else if (esAdmin) {
                FrmAdminConciertos ventanaAdmin = new FrmAdminConciertos();
                new ControladorAdminConciertos(ventanaAdmin, contextoCentral);
                ventanaAdmin.setVisible(true);
            } else {
                FrmMenuPrincipal ventanaPrincipal = new FrmMenuPrincipal();
                ControladorMenuPrincipal ctrlMenu = new ControladorMenuPrincipal(ventanaPrincipal, contextoCentral);
                ctrlMenu.cargarDatosCliente();
                ventanaPrincipal.setVisible(true);
            }
            vistaLogin.dispose();
            
        } else {
            JOptionPane.showMessageDialog(vistaLogin, "Credenciales incorrectas.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        } finally {
            vistaLogin.btnIniciarSesion.setEnabled(true);
        }
    }
    public void abrirRegistro() {
        FrmRegistro registro = new FrmRegistro();
        new ControladorRegistro(registro, contextoCentral);
        registro.setVisible(true);
        vistaLogin.dispose();
    }
}
