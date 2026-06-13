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
