package modelo;

import java.time.LocalDate;
import java.util.ArrayList;

public class Concierto {
    private String nombre;
    private LocalDate fecha;
    private ArrayList<Zona> zonas;
    private ArrayList<Venta> todasLasVentas;
    private ArrayList<Persona> todosLasPersonas;

    public Concierto(String nombre, LocalDate fecha) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.zonas = new ArrayList<>();
        this.todasLasVentas = new ArrayList<>();
        this.todosLasPersonas = new ArrayList<>();
    }

    public boolean agregarZona(String nombre) {
        return true;
    }

    public boolean eliminarZona(String nombre) {
        return true;
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public ArrayList<Zona> getZonas() { return zonas; }

    public void agregarZona(Zona z) {
        zonas.add(z);
    }

    public ArrayList<Zona> getTodasLasZonas() {
        return zonas;
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
        for (Zona z : zonas) {
            if (z.getNombre().equalsIgnoreCase(nombre)) {
                return z;
            }
        }
        return null;
    }
}
