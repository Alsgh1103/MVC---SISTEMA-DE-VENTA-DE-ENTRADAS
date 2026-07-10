package coleccion;

import java.util.ArrayList;
import modelo.Concierto;
import modelo.Zona;
import java.io.Serializable;
public class ColeccionConciertos implements Serializable{
    private static final long serialVersionUID = 1L;
    private ArrayList<Concierto> conciertos;

    public ColeccionConciertos() {
        this.conciertos = new ArrayList<>();
    }

    public void guardarConcierto(Concierto c) {
        if (buscarPorNombreYFecha(c.getNombre(), c.getFecha()) == null) {
            conciertos.add(c);
        }
    }

    public Concierto registrarConcierto(String nombre, java.time.LocalDate fecha) throws IllegalArgumentException {
        if (buscarPorNombreYFecha(nombre, fecha) != null) {
            throw new IllegalArgumentException("Ya existe un concierto con ese nombre en esa fecha.");
        }
        Concierto nuevo = new Concierto(nombre, fecha);
        guardarConcierto(nuevo);
        return nuevo;
    }

    public Concierto buscarPorNombre(String nombre) {
        if (nombre == null) return null;
        for (Concierto c : conciertos) {
            if (nombre.trim().equalsIgnoreCase(c.getNombre().trim())) {
                return c;
            }
        }
        return null;
    }

    public Concierto buscarPorNombreYFecha(String nombre, java.time.LocalDate fecha) {
        if (nombre == null || fecha == null) return null;
        for (Concierto c : conciertos) {
            if (c.getNombre().equalsIgnoreCase(nombre) && c.getFecha().equals(fecha)) return c;
        }
        return null;
    }

    public ArrayList<Concierto> getTodosLosConciertos() {
        return conciertos;
    }

    public void eliminarConcierto(Concierto c) throws IllegalStateException {
        for (Zona z : c.getTodasLasZonas()) {
            if (z.getCapacidadDisponible() < z.getCapacidadTotal()) {
                throw new IllegalStateException("No se puede eliminar el concierto porque ya tiene transacciones.");
            }
        }
        this.conciertos.remove(c);
    }
}
