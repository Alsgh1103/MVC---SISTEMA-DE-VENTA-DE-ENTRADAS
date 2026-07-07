/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;
import coleccion.*;
import java.io.Serializable;

public class DatosSistema implements Serializable {
    private static final long serialVersionUID = 1L;
    public ColeccionPersonas personas;
    public ColeccionConciertos conciertos;
    public ColeccionVentas ventas;
    public DatosSistema(ColeccionPersonas p, ColeccionConciertos c, ColeccionVentas v){
        this.personas = p;
        this.conciertos = c;
        this.ventas = v;
    }
    
}
