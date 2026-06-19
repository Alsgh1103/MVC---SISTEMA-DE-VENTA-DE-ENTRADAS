package modelo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Venta {
    private static int contadorVentas = 1;
    private String idTransaccion;
    private String fecha;
    private int cantidadEntradas;
    private int monto;
    private Cliente cliente;
    private Zona zona;
    private Tarjeta tarjeta;

    // Eliminamos el parámetro double montoT, el modelo lo calcula por sí mismo
    public Venta(int cantidad, Cliente c, Zona z, Tarjeta t) {
        this.cantidadEntradas = cantidad;
        this.cliente = c;
        this.zona = z;
        this.tarjeta = t;
        this.monto = (int) calcularTotal(); 
        
        LocalDate hoy = LocalDate.now();
        this.fecha = hoy.toString();
        DateTimeFormatter formatoId = DateTimeFormatter.ofPattern("ddMMyy");
        this.idTransaccion = hoy.format(formatoId) + String.format("%03d", contadorVentas);
        contadorVentas++;
    }

    public double calcularTotal() {
        double total = zona.getPrecio() * cantidadEntradas;
        if (cliente.isSocio()) {
            total *= 0.7; // Regla de negocio: 30% descuento
        }
        return total;
    }

    public boolean procesarCompra(int cvvIngresadoUsuario) {
        // Validaciones de negocio
        // Nota: Asegúrate de que Zona tenga un método verificarDisponibilidad
        if (!zona.verificarDisponibilidad(cantidadEntradas)) {
            return false;
        }
        if (!validarLimiteEntradas()) {
            return false;
        }
        
        // Si el pago pasa, aplicamos las consecuencias de la regla de negocio
        if (tarjeta.registrarCompra(cantidadEntradas, cvvIngresadoUsuario)) {
            this.monto = (int) calcularTotal();
            
            // 1. Reducimos el stock (Asumiendo que creaste o crearás este método en Zona)
            zona.reducirCapacidad(this.cantidadEntradas); 
            
            // 2. Acumulamos puntos (10 puntos por entrada)
            int puntosGanados = this.cantidadEntradas * 10;
            cliente.setPuntos(cliente.getPuntos() + puntosGanados); 
            
            return true;
        }
        return false;
    }

    public boolean validarLimiteEntradas() {
        return tarjeta.puedeComprar(this.cantidadEntradas);
    }
    
    // ... (El resto de tus getters y el método generarEntradas() se mantienen igual) ...
    public ArrayList<Entrada> generarEntradas() {
        ArrayList<Entrada> entradasGeneradas = new ArrayList<>();
        for (int i = 1; i <= this.cantidadEntradas; i++) {
            Entrada nueva = new Entrada(i, "VENDIDA");
            entradasGeneradas.add(nueva);
        }
        return entradasGeneradas;
    }
    
        public static int getContadorVentas() {
        return contadorVentas;
    }

    public String getIdTransaccion() {
        return idTransaccion;
    }

    public String getFecha() {
        return fecha;
    }

    public int getCantidadEntradas() {
        return cantidadEntradas;
    }

    public int getMonto() {
        return monto;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Zona getZona() {
        return zona;
    }

    public Tarjeta getTarjeta() {
        return tarjeta;
    }
}