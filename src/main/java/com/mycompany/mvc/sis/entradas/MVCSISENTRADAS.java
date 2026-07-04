package com.mycompany.mvc.sis.entradas;
import controlador.ControladorPrincipal;
import vista.FrmLogin;

/**
 *
 * @author alex_
 */
public class MVCSISENTRADAS {

    public static void main(String[] args) {
        ControladorPrincipal ctrl = new ControladorPrincipal();
        FrmLogin login = new FrmLogin(); 
        new controlador.ControladorLogin(login, ctrl);
        login.setVisible(true);
    }
}
