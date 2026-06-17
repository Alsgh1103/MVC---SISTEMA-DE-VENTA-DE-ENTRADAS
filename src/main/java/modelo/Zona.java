package modelo;

public class Zona {
    private String nombre;
    private int capacidadTotal;
    private int capacidadDisponible;
    private int precio;
    // Fidelidad estructural: Arreglo nativo en lugar de ArrayList
    private Entrada[] entradas;

    public Zona(int id, String name, int price, int capacity) {
        this.nombre = name;
        this.precio = price;
        this.capacidadTotal = capacity;
        this.capacidadDisponible = capacity;
        this.entradas = new Entrada[capacity]; // Inicialización estricta
    }

    public boolean generarEntradas() {
        for (int i = 0; i < capacidadTotal; i++) {
            entradas[i] = new Entrada(i + 1, "DISPONIBLE");
        }
        return true;
    }

    public Entrada[] mostrarEntrada() {
        return entradas;
    }

    public Entrada[] venderEntrada(int numero) {
        Entrada[] vendidas = new Entrada[1]; // Limitado a la firma requerida
        for (int i = 0; i < capacidadTotal; i++) {
            if (entradas[i] != null && entradas[i].getNumero() == numero && entradas[i].vender()) {
                vendidas[0] = entradas[i];
                capacidadDisponible--;
                break;
            }
        }
        return vendidas;
    }

    public boolean verificarDisponibilidad(int cantidadEntradas) {
        return cantidadEntradas <= capacidadDisponible;
    }

    public boolean reducirCapacidad(int cantidadComprada) {
        if (verificarDisponibilidad(cantidadComprada)) {
            capacidadDisponible -= cantidadComprada;
            return true;
        }
        return false;
    }

    // ==========================================
    // Métodos de auditoría requeridos por el Controlador
    // ==========================================
    public int getCapacidadRestante() {
        return capacidadDisponible;
    }

    public int getEntradasVendidas() {
        return capacidadTotal - capacidadDisponible;
    }

    // Getters y Setters originales
    public String getNombre() { return nombre; }
    public int getPrecio() { return precio; }
    public int getCapacidadTotal() { return capacidadTotal; }
    public int getCapacidadDisponible() { return capacidadDisponible; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setPrecio(int precio) { this.precio = precio; }
    public void setCapacidadTotal(int capacidadTotal) { this.capacidadTotal = capacidadTotal; }
    public void setCapacidadDisponible(int capacidadDisponible) { this.capacidadDisponible = capacidadDisponible; }
}