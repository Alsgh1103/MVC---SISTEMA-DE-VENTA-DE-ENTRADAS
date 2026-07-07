/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;
import java.io.*;
public class GestorSerializacion {
     private static final String ARCHIVO = "datos.dat";
    public static void guardar(DatosSistema datos) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            oos.writeObject(datos);
        } catch (Exception e) {
            System.out.println("Error al guardar: " + e.getMessage());
        }
    }
    public static DatosSistema cargar() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARCHIVO))) {
            return (DatosSistema) ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }
}
