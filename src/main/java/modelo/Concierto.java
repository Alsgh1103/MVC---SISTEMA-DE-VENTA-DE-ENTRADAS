package modelo;

import java.time.LocalDate;
import java.util.ArrayList;

public class Concierto {
    private String nombre;
    private LocalDate fecha;
    private ArrayList<Zona> zonas;
    private ArrayList<Venta> todasLasVentas;
    private ArrayList<Persona> todosLasPersonas;

    public Concierto(String nombre, LocalDate fecha) throws IllegalArgumentException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del concierto no puede estar vacío.");
        }
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha del concierto no puede ser nula.");
        }
        this.nombre = nombre;
        this.fecha = fecha;
        this.zonas = new ArrayList<>();
        this.todasLasVentas = new ArrayList<>();
        this.todosLasPersonas = new ArrayList<>();
    }

    public void registrarZona(String nombreZona, int capacidad, int precio) throws IllegalArgumentException {
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser mayor a 0.");
        }
        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo.");
        }
        if (nombreZona == null || nombreZona.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la zona no puede estar vacío.");
        }
        if (buscarZonaPorNombre(nombreZona) != null) {
            throw new IllegalArgumentException("Ya existe una zona registrada con el nombre '" + nombreZona + "' en este concierto.");
        }
        
        int nuevoId = this.zonas.size() + 1;
        this.zonas.add(new Zona(nuevoId, nombreZona, precio, capacidad));
    }

    public void eliminarZona(String nombreZona) throws IllegalArgumentException, IllegalStateException {
        Zona zonaAEliminar = buscarZonaPorNombre(nombreZona);
        if (zonaAEliminar == null) {
            throw new IllegalArgumentException("La zona especificada no existe.");
        }
        
        // Verificación de integridad: No borrar zonas con ventas
        for (Venta v : todasLasVentas) {
            if (v.getZona().getNombre().equalsIgnoreCase(nombreZona)) {
                throw new IllegalStateException("No se puede eliminar la zona '" + nombreZona + "' porque ya tiene entradas vendidas.");
            }
        }
        
        this.zonas.remove(zonaAEliminar);
    }

    public void limpiarZonas() throws IllegalStateException {
        if (!this.todasLasVentas.isEmpty()) {
            throw new IllegalStateException("No se pueden editar las zonas porque ya existen entradas vendidas para este concierto.");
        }
        this.zonas.clear();
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

    @Override
    public String toString() {
        return this.nombre + " (" + this.fecha.toString() + ")";
    }
}
