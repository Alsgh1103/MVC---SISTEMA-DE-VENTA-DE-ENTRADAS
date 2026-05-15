/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.mvc.sis.entradas;
import controlador.ControladorPrincipal;
import vista.FrmMenuPrincipal;

/**
 *
 * @author alex_
 */
public class MVCSISENTRADAS {

    public static void main(String[] args) {
        ControladorPrincipal ctrl = new ControladorPrincipal();
        FrmMenuPrincipal vista    = new FrmMenuPrincipal(ctrl); 
        vista.setVisible(true);
    }
}
