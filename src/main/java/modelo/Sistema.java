package modelo;

import java.util.ArrayList;

/**
 * @author alex_
 */
public class Sistema {
    private ArrayList<Venta> todasLasVentas;
    private ArrayList<Zona> todasLasZonas;
    private ArrayList<Persona> todosLasPersonas;

    public Sistema() {
        this.todasLasVentas = new ArrayList<>();
        this.todasLasZonas = new ArrayList<>();
        this.todosLasPersonas = new ArrayList<>();
    }

    public void agregarZona(Zona z) {
        todasLasZonas.add(z);
    }

    public ArrayList<Zona> getTodasLasZonas() {
        return todasLasZonas;
    }

    public void registrarVenta(Venta v) {
        todasLasVentas.add(v);
    }

    public ArrayList<Venta> getTodasLasVentas() {
        return todasLasVentas;
    }

    public void agregarPersona(Persona p) {
        todosLasPersonas.add(p);
    }

    public ArrayList<Persona> getTodosLasPersonas() {
        return todosLasPersonas;
    }

    public Persona buscarPersonaDni(String dni) {
        for (int i = 0; i < todosLasPersonas.size(); i++) {
            Persona p = todosLasPersonas.get(i);
            if (dni.equals(p.getDni())) {
                return p;
            }
        }
        return null;
    }

    public Usuario validarAccesoUsuario(String dni, String codigoUsuario) {
        Persona p = (buscarPersonaDni(dni));
        if (p instanceof Usuario) {
            Usuario adm = (Usuario) p;
            if (adm.validarCodigoUsuario(codigoUsuario)) {
                return adm;
            }
        }
        return null;
    }

    public Persona loginGeneral(String dni, String pass) {
        Persona p = (buscarPersonaDni(dni));
        if (p != null && p.validarContrasena(pass)) {
            return p;
        }
        return null;
    }

    public Zona buscarZonaPorNombre(String nombre) {
        for (Zona z : todasLasZonas) {
            if (z.getNombre().equalsIgnoreCase(nombre)) {
                return z;
            }
        }
        return null;
    }
}