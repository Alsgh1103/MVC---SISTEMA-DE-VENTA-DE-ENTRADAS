package controlador;

import coleccion.ColeccionConciertos;
import coleccion.ColeccionVentas;
import modelo.Concierto;
import modelo.Venta;
import modelo.Zona;
import vista.FrmAdmin;
import java.util.ArrayList;

public class ControladorAdmin {
    private FrmAdmin vistaAdmin;
    private ControladorPrincipal contextoCentral;

    public ControladorAdmin(FrmAdmin vistaAdmin, ControladorPrincipal contextoCentral) {
        this.vistaAdmin = vistaAdmin;
        this.contextoCentral = contextoCentral;
    }

    /**
     * Puebla la tabla cruzando la información de Conciertos y Ventas globales.
     */
    public Object[][] obtenerDatosAuditoria() {
        ColeccionConciertos cc = contextoCentral.getColeccionConciertos();
        ArrayList<Concierto> todosConciertos = cc.getTodosLosConciertos();

        ColeccionVentas cv = contextoCentral.getColeccionVentas();
        ArrayList<Venta> todasLasVentas = cv.getTodasLasVentas();

        int totalFilas = 0;
        for (Concierto c : todosConciertos) {
            int cantidadZonas = c.getTodasLasZonas().size();
            totalFilas += (cantidadZonas == 0) ? 1 : cantidadZonas;
        }

        if (totalFilas == 0) return new Object[0][5];

        Object[][] matriz = new Object[totalFilas][5]; 
        int index = 0;

        for (Concierto c : todosConciertos) {
            ArrayList<Zona> zonasDelConcierto = c.getTodasLasZonas();

            if (zonasDelConcierto.isEmpty()) {
                matriz[index][0] = c.getNombre(); 
                matriz[index][1] = c.getFecha().toString();
                matriz[index][2] = "Sin zonas";
                matriz[index][3] = "-";
                matriz[index][4] = 0; 
             
                index++;
            } else {
                for (Zona z : zonasDelConcierto) {
                    matriz[index][0] = c.getNombre();
                    matriz[index][1] = c.getFecha().toString();
                    matriz[index][2] = z.getNombre();
                    matriz[index][3] = z.getCapacidadDisponible();

                    int contadorEntradasVendidas = 0;
                    for (Venta v : todasLasVentas) {

                        if (v.getZona() == z) {
                            contadorEntradasVendidas += v.getCantidadEntradas();
                        }
                    }

                    matriz[index][4] = contadorEntradasVendidas; 
                    index++;
                }
            }
        }
        return matriz;
    }
    
    public void registrarZonaEnConcierto(String nombreConc, java.time.LocalDate fechaConc, String nombreZona, int capacidad, int precio) throws IllegalArgumentException {
        ColeccionConciertos cc = contextoCentral.getColeccionConciertos();
        Concierto concierto = cc.buscarPorNombreYFecha(nombreConc, fechaConc);
        
        if (concierto == null) {
            throw new IllegalArgumentException("El concierto especificado no se encuentra disponible.");
        }
        concierto.registrarZona(nombreZona, capacidad, precio);
    }
    
    public ColeccionConciertos getColeccionConciertos() {
        return contextoCentral.getColeccionConciertos();
    }
    
    public boolean eliminarConciertoGlobal(Concierto c) {
        return contextoCentral.eliminarConciertoGlobal(c);
    }
    
    public boolean eliminarZonaDeConcierto(Concierto c, String nz) {
        return contextoCentral.eliminarZonaDeConcierto(c, nz);
    }

    public void cerrarSesion() {
        contextoCentral.cerrarSesion();

        vista.FrmLogin login = new vista.FrmLogin();
        
        ControladorLogin ctrlLogin = new ControladorLogin(login, this.contextoCentral);

        login.setVisible(true);

        vistaAdmin.dispose();
    }
}