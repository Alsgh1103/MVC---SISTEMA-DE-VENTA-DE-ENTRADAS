package com.mycompany.mvc.sis.entradas;
import controlador.ControladorPrincipal;
import vista.FrmLogin;

public class MVCSISENTRADAS {

        public static void main(String[] args) {
        ControladorPrincipal ctrl = new ControladorPrincipal();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            persistencia.DatosSistema datosActuales = new persistencia.DatosSistema(
                ctrl.getColeccionPersonas(), 
                ctrl.getColeccionConciertos(), 
                ctrl.getColeccionVentas()
            );
            persistencia.GestorSerializacion.guardar(datosActuales);
            System.out.println("Los datos se han guardado exitosamente al salir.");
        }));

        FrmLogin login = new FrmLogin(); 
        new controlador.ControladorLogin(login, ctrl);
        login.setVisible(true);
    }

}
