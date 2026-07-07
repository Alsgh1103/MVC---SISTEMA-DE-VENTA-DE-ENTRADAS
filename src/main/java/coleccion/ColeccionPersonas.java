package coleccion;

import java.util.ArrayList;
import modelo.Persona;
import java.io.Serializable;

public class ColeccionPersonas implements Serializable{
    private static final long serialVersionUID = 1L;
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

    public boolean eliminarPorCorreo(String correo) {
        if (correo == null) return false;
        for (int i = 0; i < personas.size(); i++) {
            if (personas.get(i).getCorreo().trim().equalsIgnoreCase(correo.trim())) {
                personas.remove(i);
                return true;
            }
        }
        return false;
    }

    public ArrayList<Persona> getTodasLasPersonas() {
        return personas;
    }
}
