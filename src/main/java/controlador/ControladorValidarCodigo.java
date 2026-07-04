/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import vista.FrmValidarCodigo;
import vista.FrmLogin;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorValidarCodigo implements ActionListener {
    private FrmValidarCodigo vistaValidar;
    private ControladorPrincipal contextoCentral;
    private String codigoGenerado;
    private String dniTemp;
    private String nomTemp;
    private String apeTemp;
    private String correoTemp;
    private String passTemp;

    public ControladorValidarCodigo(FrmValidarCodigo vistaValidar, ControladorPrincipal contextoCentral,
            String codigoGenerado, String dni, String nom, String ape, String correo, String pass) {
        this.vistaValidar = vistaValidar;
        this.contextoCentral = contextoCentral;
        this.codigoGenerado = codigoGenerado;
        this.dniTemp = dni;
        this.nomTemp = nom;
        this.apeTemp = ape;
        this.correoTemp = correo;
        this.passTemp = pass;
        this.vistaValidar.btnVerificar.addActionListener(this);
        this.vistaValidar.btnCancelar.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vistaValidar.btnVerificar) {
            String codigoIngresado = vistaValidar.txtCodigo.getText().trim();
            if (codigoIngresado.equals(codigoGenerado)) {
                contextoCentral.registrarNuevoCliente(dniTemp, nomTemp, apeTemp, correoTemp, passTemp);
                JOptionPane.showMessageDialog(vistaValidar,
                        "¡Registro completado con éxito! Ahora puede iniciar sesión.");
                FrmLogin login = new FrmLogin();
                new ControladorLogin(login, contextoCentral);
                login.setVisible(true);
                vistaValidar.dispose();

            } else {
                JOptionPane.showMessageDialog(vistaValidar, "Código incorrecto. Por favor, intente de nuevo.", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == vistaValidar.btnCancelar) {
            FrmLogin login = new FrmLogin();
            new ControladorLogin(login, contextoCentral);
            login.setVisible(true);
            vistaValidar.dispose();
        }
    }
}
