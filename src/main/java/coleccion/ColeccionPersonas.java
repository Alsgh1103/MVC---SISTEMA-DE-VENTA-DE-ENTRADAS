package coleccion;

import java.util.ArrayList;
import modelo.Persona;

public class ColeccionPersonas {
    private ArrayList<Persona> personas;

    public ColeccionPersonas() {
        this.personas = new ArrayList<>();
    }

    public void guardarPersona(Persona p) {
        if (buscarPorDni(p.getDni()) == null) {
            personas.add(p);
        }
    }

    public Persona buscarPorDni(String dni) {
        if (dni == null) return null;
        for (Persona p : personas) {
            if (dni.trim().equalsIgnoreCase(p.getDni().trim())) {
                return p;
            }
        }
        return null;
    }

    public Persona buscarPorCorreo(String correo) {
        if (correo == null) return null;
        for (Persona p : personas) {
            if (correo.trim().equalsIgnoreCase(p.getCorreo().trim())) {
                return p;
            }
        }
        return null;
    }

    public ArrayList<Persona> getTodasLasPersonas() {
        return personas;
    }
}
