package modelo;

import java.time.LocalDate;
import java.util.ArrayList;

public class Concierto {
    private String nombre;
    private LocalDate fecha;
    private ArrayList<Zona> zonas;
    // Compatibilidad temporal para compilar con la parte de Ventas
    private ArrayList<Venta> todasLasVentas = new ArrayList<>();

    public Concierto(String nombre, LocalDate fecha) throws IllegalArgumentException {
        if (nombre == null || nombre.trim().isEmpty()) throw new IllegalArgumentException("El nombre no puede estar vacío.");
        if (fecha == null) throw new IllegalArgumentException("La fecha no puede ser nula.");
        
        this.nombre = nombre;
        this.fecha = fecha;
        this.zonas = new ArrayList<>();
    }

    public void registrarZona(String nombreZona, int capacidad, int precio) throws IllegalArgumentException {
        if (capacidad <= 0) throw new IllegalArgumentException("La capacidad debe ser mayor a 0.");
        if (precio < 0) throw new IllegalArgumentException("El precio no puede ser negativo.");
        if (buscarZonaPorNombre(nombreZona) != null) throw new IllegalArgumentException("Ya existe esa zona.");
        
        int nuevoId = this.zonas.size() + 1;
        this.zonas.add(new Zona(nuevoId, nombreZona, precio, capacidad));
    }

    public void eliminarZona(String nombreZona) {
        Zona zonaAEliminar = buscarZonaPorNombre(nombreZona);
        if (zonaAEliminar == null) throw new IllegalArgumentException("La zona no existe.");
        
        // SUGERENCIA APLICADA: Validar por capacidadDisponible
        if (zonaAEliminar.getCapacidadDisponible() < zonaAEliminar.getCapacidadTotal()) {
            throw new IllegalStateException("No se puede eliminar, ya tiene entradas vendidas.");
        }
        this.zonas.remove(zonaAEliminar);
    }

    public void limpiarZonas() throws IllegalStateException {
        // SUGERENCIA APLICADA: Validar por capacidadDisponible
        for (Zona z : zonas) {
            if (z.getCapacidadDisponible() < z.getCapacidadTotal()) {
                throw new IllegalStateException("No se pueden editar las zonas. Ya existen entradas vendidas.");
            }
        }
        this.zonas.clear();
    }

    public String getNombre() { return nombre; }
    public LocalDate getFecha() { return fecha; }
    public ArrayList<Zona> getTodasLasZonas() { return zonas; }

    public Zona buscarZonaPorNombre(String nombre) {
        for (Zona z : zonas) {
            if (z.getNombre().equalsIgnoreCase(nombre)) return z;
        }
        return null;
    }

    @Override
    public String toString() {
        return this.nombre + " (" + this.fecha.toString() + ")";
    }

    // Compatibilidad temporal para compilar con la parte de Ventas
    public void registrarVenta(Venta v) {
        this.todasLasVentas.add(v);
    }

    public ArrayList<Venta> getTodasLasVentas() {
        return todasLasVentas;
    }
}
