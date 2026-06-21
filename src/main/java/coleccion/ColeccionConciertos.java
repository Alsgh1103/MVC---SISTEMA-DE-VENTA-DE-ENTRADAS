package coleccion;

import java.util.ArrayList;
import modelo.Concierto;

public class ColeccionConciertos {
    private ArrayList<Concierto> conciertos;

    public ColeccionConciertos() {
        this.conciertos = new ArrayList<>();
    }

    public void guardarConcierto(Concierto c) {
        if (buscarPorNombre(c.getNombre()) == null) {
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
        if (buscarPorNombre(nombre) != null) {
            throw new IllegalArgumentException("Ya existe un concierto registrado con ese nombre.");
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

    public ArrayList<Concierto> getTodosLosConciertos() {
        return conciertos;
    }
}
