package com.mycompany.mvc.sis.entradas;

import controlador.ControladorPrincipal;
import vista.FrmLogin; // Cambiamos FrmAdmin por FrmLogin

public class MVCSISENTRADAS {

    public static void main(String[] args) {
        // 1. Instanciar el orquestador principal (el Modelo se inicializa por dentro)
        ControladorPrincipal controlador = new ControladorPrincipal();
        
        // 2. Instanciar la vista de Login, inyectando el controlador según tu diseño original
        FrmLogin vistaLogin = new FrmLogin(controlador);
        
        controlador.vincularVistaLogin(vistaLogin);
        
        // 3. Mostrar la interfaz de inicio de sesión
        vistaLogin.setVisible(true);
    }
}