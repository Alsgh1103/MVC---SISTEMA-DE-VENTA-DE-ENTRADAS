package modelo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Venta {
    private static int contadorVentas = 1;
    private String idTransaccion;
    private String fecha;
    private int cantidadEntradas;
    private double monto;
    private Cliente cliente;
    private Zona zona;
    private Tarjeta tarjeta;

    // El modelo calcula el monto por sí mismo a partir de zona y descuentos.
    public Venta(int cantidad, Cliente c, Zona z, Tarjeta t) {
        // C3 — Regla de negocio: la cantidad debe ser positiva
        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de entradas debe ser mayor a 0.");
        }
        this.cantidadEntradas = cantidad;
        this.cliente = c;
        this.zona = z;
        this.tarjeta = t;
        this.monto = calcularTotal();

        LocalDate hoy = LocalDate.now();
        this.fecha = hoy.toString();
        DateTimeFormatter formatoId = DateTimeFormatter.ofPattern("ddMMyy");
        this.idTransaccion = hoy.format(formatoId) + String.format("%03d", contadorVentas);
        contadorVentas++;
    }

    public double calcularTotal() {
        return zona.getPrecio() * cantidadEntradas;
    }

    /**
     * Procesa la compra aplicando todas las reglas de negocio del dominio.
     * Lanza IllegalArgumentException con mensaje descriptivo ante cualquier fallo,
     * en lugar de retornar false silenciosamente.
     *
     * @param cvvIngresadoUsuario CVV introducido por el usuario en la vista.
     * @throws IllegalArgumentException si no hay stock (P1), se supera el límite
     *                                  de entradas (T1) o el CVV es incorrecto
     *                                  (T2).
     */
    public void procesarCompra(int cvvIngresadoUsuario) {
        // P1 — Sin disponibilidad en la zona
        if (!zona.verificarDisponibilidad(cantidadEntradas)) {
            throw new IllegalArgumentException(
                    "No hay entradas disponibles suficientes en la zona seleccionada.");
        }

        // T1 y T2 — registrarCompra lanza IllegalArgumentException si falla
        tarjeta.registrarCompra(cantidadEntradas, cvvIngresadoUsuario);

        // Compra aprobada: aplicamos las consecuencias de negocio
        this.monto = calcularTotal();

        // 1. Reducimos el stock de la zona
        zona.reducirCapacidad(this.cantidadEntradas);

        // 2. Acumulamos puntos al cliente (10 puntos por entrada)
        int puntosGanados = this.cantidadEntradas * 10;
        cliente.setPuntos(cliente.getPuntos() + puntosGanados);
    }

    /** Consulta auxiliar: ¿la tarjeta todavía puede absorber esta cantidad? */
    public boolean validarLimiteEntradas() {
        return tarjeta.puedeComprar(this.cantidadEntradas);
    }

    /**
     * Aplica el descuento por emisor de tarjeta de forma multiplicativa
     * sobre el monto ya calculado por procesarCompra().
     *
     * Debe llamarse DESPUÉS de {@link #procesarCompra(int)} para no
     * interferir con las validaciones internas de la compra.
     *
     * @param porcentaje Porcentaje de descuento (0.0 = sin descuento, 0.10 = 10%).
     */
    public void aplicarDescuentoEmisor(double porcentaje) {
        if (porcentaje > 0 && porcentaje < 1) {
            this.monto = this.monto * (1.0 - porcentaje);
        }
    }

    public void aplicarDescuentoPuntos(double porcentaje) {
        if (porcentaje > 0 && porcentaje < 1) {
            this.monto = this.monto * (1.0 - porcentaje);
        }
    }

    // ... (El resto de tus getters y el método generarEntradas() se mantienen
    // igual) ...
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

    public double getMonto() {
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