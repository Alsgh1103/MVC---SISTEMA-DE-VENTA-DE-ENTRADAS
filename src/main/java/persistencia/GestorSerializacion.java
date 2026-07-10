/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;
import coleccion.ColeccionConciertos;
import coleccion.ColeccionPersonas;
import coleccion.ColeccionVentas;
import java.io.*;

public class GestorSerializacion {
    private static final String DIR_DATOS = "datos";
    private static final String ARCHIVO_PERSONAS = DIR_DATOS + "/personas.dat";
    private static final String ARCHIVO_CONCIERTOS = DIR_DATOS + "/conciertos.dat";
    private static final String ARCHIVO_VENTAS = DIR_DATOS + "/ventas.dat";

    public static void guardar(DatosSistema datos) {
        guardarPersonas(datos.personas);
        guardarConciertos(datos.conciertos);
        guardarVentas(datos.ventas);
    }

    public static void guardarPersonas(ColeccionPersonas personas) {
        try {
            asegurarDirectorio();
            guardarObjeto(ARCHIVO_PERSONAS, personas);
        } catch (Exception e) {
            System.out.println("Error al guardar personas: " + e.getMessage());
        }
    }

    public static void guardarConciertos(ColeccionConciertos conciertos) {
        try {
            asegurarDirectorio();
            guardarObjeto(ARCHIVO_CONCIERTOS, conciertos);
        } catch (Exception e) {
            System.out.println("Error al guardar conciertos: " + e.getMessage());
        }
    }

    public static void guardarVentas(ColeccionVentas ventas) {
        try {
            asegurarDirectorio();
            guardarObjeto(ARCHIVO_VENTAS, ventas);
        } catch (Exception e) {
            System.out.println("Error al guardar ventas: " + e.getMessage());
        }
    }

    private static void asegurarDirectorio() {
        File dir = new File(DIR_DATOS);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private static void guardarObjeto(String ruta, Object obj) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ruta))) {
            oos.writeObject(obj);
        }
    }

    private static Object cargarObjeto(String ruta) {
        File file = new File(ruta);
        if (!file.exists()) return null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    public static DatosSistema cargar() {
        // Load separated files
        ColeccionPersonas personas = (ColeccionPersonas) cargarObjeto(ARCHIVO_PERSONAS);
        ColeccionConciertos conciertos = (ColeccionConciertos) cargarObjeto(ARCHIVO_CONCIERTOS);
        ColeccionVentas ventas = (ColeccionVentas) cargarObjeto(ARCHIVO_VENTAS);

        // If files don't exist yet, DatosSistema will be created with whatever is available (or nulls).
        // Let's create new collections if they are null to prevent NullPointerException
        if (personas == null) personas = new ColeccionPersonas();
        if (conciertos == null) conciertos = new ColeccionConciertos();
        if (ventas == null) ventas = new ColeccionVentas();

        return new DatosSistema(personas, conciertos, ventas);
    }
}
