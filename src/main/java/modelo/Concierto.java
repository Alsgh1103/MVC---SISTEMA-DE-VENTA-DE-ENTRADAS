package modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;
import java.io.Serializable;

public class Concierto implements Serializable{
    private static final long serialVersionUID = 1L;
    private String nombre;
    private LocalDate fecha;
    private ArrayList<Zona> zonas;
    private String rutaImagen;
    private String rutaImagenPoster;
    private String artista;

    private Map<Tarjeta.Emisor, Double> descuentosPorEmisor = new EnumMap<>(Tarjeta.Emisor.class);

    public Concierto(String nombre, LocalDate fecha) throws IllegalArgumentException {
        if (nombre == null || nombre.trim().isEmpty()) throw new IllegalArgumentException("El nombre no puede estar vacío.");
        if (fecha == null) throw new IllegalArgumentException("La fecha no puede ser nula.");
        
        this.nombre = nombre;
        this.artista = "Desconocido"; 
        this.fecha = fecha;
        this.zonas = new ArrayList<>();

        descuentosPorEmisor.put(Tarjeta.Emisor.VISA,             0.05);
        descuentosPorEmisor.put(Tarjeta.Emisor.MASTERCARD,       0.10);
        descuentosPorEmisor.put(Tarjeta.Emisor.DINERS,           0.15);
        descuentosPorEmisor.put(Tarjeta.Emisor.AMERICAN_EXPRESS, 0.07);
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
        
        if (zonaAEliminar.getCapacidadDisponible() < zonaAEliminar.getCapacidadTotal()) {
            throw new IllegalStateException("No se puede eliminar, ya tiene entradas vendidas.");
        }
        this.zonas.remove(zonaAEliminar);
    }

    public void limpiarZonas() throws IllegalStateException {
        for (Zona z : zonas) {
            if (z.getCapacidadDisponible() < z.getCapacidadTotal()) {
                throw new IllegalStateException("No se pueden editar las zonas. Ya existen entradas vendidas.");
            }
        }
        this.zonas.clear();
    }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getArtista() { return artista; }
    public void setArtista(String artista) { this.artista = artista; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public ArrayList<Zona> getTodasLasZonas() { return zonas; }

    public double getDescuentoParaEmisor(Tarjeta.Emisor emisor) {
        return descuentosPorEmisor.getOrDefault(emisor, 0.0);
    }

    public void setDescuentoParaEmisor(Tarjeta.Emisor emisor, double porcentaje) {
        if (porcentaje < 0 || porcentaje > 1)
            throw new IllegalArgumentException("El porcentaje debe estar entre 0.0 y 1.0");
        descuentosPorEmisor.put(emisor, porcentaje);
    }

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

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }
    
    public String getRutaImagenPoster(){
        return rutaImagenPoster;
    }
    
    public void setRutaImagenPoster(String rutaImagenPoster){
        this.rutaImagenPoster = rutaImagenPoster;
    }
}
