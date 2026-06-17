package modelo;

import java.time.LocalDate;
import java.util.ArrayList;

public class Concierto {
    private String nombre;
    private LocalDate fecha;
    
    // Fidelidad estructural: Arreglo nativo de tamaño fijo para asegurar el límite de 4 zonas
    private Zona[] zonasDisponibles;
    private int contadorZonas;
    
    private ArrayList<Venta> todasLasVentas;
    private ArrayList<Persona> todosLasPersonas;

    public Concierto(String nombre, LocalDate fecha) {
        this.nombre = nombre;
        this.fecha = fecha;
        
        // Inicialización estricta requerida por la regla de negocio
        this.zonasDisponibles = new Zona[4]; 
        this.contadorZonas = 0;
        
        this.todasLasVentas = new ArrayList<>();
        this.todosLasPersonas = new ArrayList<>();
    }

    // ========================================================
    // MÉTODOS DE ZONAS ADAPTADOS PARA EL ARREGLO NATIVO
    // ========================================================

    public boolean agregarZona(Zona z) {
        if (contadorZonas < 4) {
            zonasDisponibles[contadorZonas] = z;
            contadorZonas++;
            return true;
        }
        return false; // Rechaza automáticamente si se intenta agregar una quinta zona
    }

    public Zona[] getZonas() { 
        return zonasDisponibles; 
    }

    public Zona buscarZonaPorNombre(String nombre) {
        for (int i = 0; i < contadorZonas; i++) {
            if (zonasDisponibles[i] != null && zonasDisponibles[i].getNombre().equalsIgnoreCase(nombre)) {
                return zonasDisponibles[i];
            }
        }
        return null; // Retorna null si la zona no existe
    }

    // ========================================================
    // RESTO DE TU CÓDIGO INTACTO
    // ========================================================

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

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
        Persona p = buscarPersonaDni(dni);
        if (p instanceof Usuario) {
            Usuario adm = (Usuario) p;
            if (adm.validarCodigoUsuario(codigoUsuario)) {
                return adm;
            }
        }
        return null;
    }

    public Persona loginGeneral(String dni, String pass) {
        Persona p = buscarPersonaDni(dni);
        if (p != null && p.validarContrasena(pass)) {
            return p;
        }
        return null;
    }
}