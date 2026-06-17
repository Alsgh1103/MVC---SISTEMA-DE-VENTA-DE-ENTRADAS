package modelo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Venta {
    private static int contadorVentas = 1;
    private String idTransaccion;
    private String fecha;
    private int cantidadEntradas;
    private double monto; // Cambiado a double para no perder precisión con el 30% de descuento
    private Cliente cliente;
    private Zona zona;
    private Tarjeta tarjeta;
    
    // Fidelidad estructural: Arreglo nativo en lugar de ArrayList
    private Entrada[] entradasCompradas; 

    public Venta(int cantidad, double montoT, Cliente c, Zona z, Tarjeta t) {
        this.cantidadEntradas = cantidad;
        this.cliente = c;
        this.zona = z;
        this.tarjeta = t;
        
        // La matemática nace y muere en el modelo
        this.monto = calcularTotal(); 
        
        // Generación de ID basada en tu lógica original
        LocalDate hoy = LocalDate.now();
        this.fecha = hoy.toString();
        DateTimeFormatter formatoId = DateTimeFormatter.ofPattern("ddMMyy");
        this.idTransaccion = hoy.format(formatoId) + String.format("%03d", contadorVentas);
        contadorVentas++;
        
        // Bloqueo físico en memoria: Máximo 4 espacios por transacción
        this.entradasCompradas = new Entrada[4]; 
    }

    public boolean validarLimiteEntradas() {
        return tarjeta.puedeComprar(this.cantidadEntradas);
    }

    public double calcularTotal() {
        double total = zona.getPrecio() * cantidadEntradas;
        if (cliente.isSocio()) {
            total *= 0.7; // 30% de descuento para socios
        }
        return total;
    }

    // Fusión de tu lógica original con la matriz de Entradas
    public boolean procesarCompra(int cvvIngresadoUsuario) {
        // Regla Estructural: Máximo 4 entradas por transacción
        if (this.cantidadEntradas <= 0 || this.cantidadEntradas > 4) {
            return false;
        }
        
        // Validación de disponibilidad
        if (!zona.verificarDisponibilidad(cantidadEntradas)) {
            return false;
        }
        
        // Validación con la tarjeta
        if (!validarLimiteEntradas()) {
            return false;
        }
        
        // Procesamiento del pago y reducción de capacidad
        if (tarjeta.registrarCompra(cantidadEntradas, cvvIngresadoUsuario)) {
            if (zona.reducirCapacidad(cantidadEntradas)) {
                
                // Se instancian las entradas en el arreglo estricto en lugar de ArrayList
                for (int i = 0; i < this.cantidadEntradas; i++) {
                    this.entradasCompradas[i] = new Entrada(i + 1, "VENDIDA");
                }
                
                return true; // Transacción impecable
            }
        }
        return false;
    }

    // ==========================================
    // GETTERS
    // ==========================================
    public static int getContadorVentas() { return contadorVentas; }
    public String getIdTransaccion() { return idTransaccion; }
    public String getFecha() { return fecha; }
    public int getCantidadEntradas() { return cantidadEntradas; }
    public double getMonto() { return monto; }
    public Cliente getCliente() { return cliente; }
    public Zona getZona() { return zona; }
    public Tarjeta getTarjeta() { return tarjeta; }
    
    // Método adaptado para devolver el arreglo estricto
    public Entrada[] getEntradasCompradas() {
        return entradasCompradas;
    }
}
