package coleccion;

import java.util.ArrayList;
import modelo.Concierto;

public class ColeccionConciertos {
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
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del concierto no puede estar vacío.");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha del concierto no puede ser nula.");
        }
        if (buscarPorNombreYFecha(nombre, fecha) != null) {
            throw new IllegalArgumentException("Ya existe un concierto registrado con ese nombre en esa fecha.");
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
            if (nombre.trim().equalsIgnoreCase(c.getNombre().trim()) && fecha.equals(c.getFecha())) {
                return c;
            }
        }
        return null;
    }

    public ArrayList<Concierto> getTodosLosConciertos() {
        return conciertos;
    }

    public void eliminarConcierto(Concierto c) throws IllegalStateException {
        if (!c.getTodasLasVentas().isEmpty()) {
            throw new IllegalStateException("No se puede eliminar el concierto porque ya tiene transacciones registradas.");
        }
        this.conciertos.remove(c);
    }
}
