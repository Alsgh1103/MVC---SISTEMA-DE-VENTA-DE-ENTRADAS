/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import vista.FrmAdmin;
import vista.FrmAdminConciertos;
import vista.FrmAdminUsuarios;
import vista.FrmLogin;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorAdmin implements ActionListener {

    private FrmAdmin vista;
    private ControladorPrincipal contextoCentral;

    public ControladorAdmin(FrmAdmin vista, ControladorPrincipal contextoCentral) {
        this.vista = vista;
        this.contextoCentral = contextoCentral;

        this.vista.btnConciertos.addActionListener(this);
        this.vista.btnUsuarios.addActionListener(this);
        this.vista.btnCerrarSesion.addActionListener(this);
        
        this.vista.setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.btnConciertos) {
            FrmAdminConciertos frmConciertos = new FrmAdminConciertos();
            new ControladorAdminConciertos(frmConciertos, contextoCentral);
            frmConciertos.setVisible(true);
            vista.dispose();
        } else if (e.getSource() == vista.btnUsuarios) {
            FrmAdminUsuarios frmUsuarios = new FrmAdminUsuarios();
            new ControladorAdminUsuarios(frmUsuarios, contextoCentral);
            frmUsuarios.setVisible(true);
            vista.dispose();
        } else if (e.getSource() == vista.btnCerrarSesion) {
            FrmLogin login = new FrmLogin();
            new ControladorLogin(login, contextoCentral);
            login.setVisible(true);
            vista.dispose();
        }
    }
}
