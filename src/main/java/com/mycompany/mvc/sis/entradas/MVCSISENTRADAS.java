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
        FrmLogin login = new FrmLogin(ctrl); 
        login.setVisible(true);
    }
}
