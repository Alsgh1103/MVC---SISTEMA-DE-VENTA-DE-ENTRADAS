package modelo;

import java.time.LocalDate;
import java.util.ArrayList;

public class Concierto {
    private String nombre;
    private LocalDate fecha;
    private ArrayList<Zona> zonas;

    public Concierto(String nombre, LocalDate fecha) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.zonas = new ArrayList<>();
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
}
