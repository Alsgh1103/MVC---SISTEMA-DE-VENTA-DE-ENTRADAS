package modelo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.io.Serializable;

public class Venta implements Serializable {
    private static final long serialVersionUID = 1L;
    private static int contadorVentas = 1;
    private String idTransaccion;
    private String fecha;
    private int cantidadEntradas;
    private double monto;
    private Cliente cliente;
    private Zona zona;
    private Tarjeta tarjeta;
    private Concierto concierto;
    private String estado;

    public Venta(int cantidad, Cliente c, Zona z, Tarjeta t, Concierto concierto) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad de entradas debe ser mayor a 0.");
        }
        this.cantidadEntradas = cantidad;
        this.cliente = c;
        this.zona = z;
        this.tarjeta = t;
        this.concierto = concierto;
        this.monto = calcularTotal();

        LocalDate hoy = LocalDate.now();
        this.fecha = hoy.toString();
        DateTimeFormatter formatoId = DateTimeFormatter.ofPattern("ddMMyy");
        this.idTransaccion = hoy.format(formatoId) + String.format("%03d", contadorVentas);
        contadorVentas++;
        this.estado = "COMPLETADA";
    }

    public double calcularTotal() {
        return zona.getPrecio() * cantidadEntradas;
    }

    public void procesarCompra(int cvvIngresadoUsuario) {
        if (!zona.verificarDisponibilidad(cantidadEntradas)) {
            throw new IllegalArgumentException(
                    "No hay entradas disponibles suficientes en la zona seleccionada.");
        }

        tarjeta.registrarCompra(cantidadEntradas, cvvIngresadoUsuario);

        this.monto = calcularTotal();

        zona.reducirCapacidad(this.cantidadEntradas);

        int puntosGanados = this.cantidadEntradas * 10;
        cliente.setPuntos(cliente.getPuntos() + puntosGanados);
    }

    public void devolver() {
        if ("DEVUELTA".equals(this.estado)) {
            throw new IllegalStateException("La entrada ya ha sido devuelta.");
        }

        int puntosARestar = this.cantidadEntradas * 10;
        int puntosActuales = cliente.getPuntos();

        if (puntosActuales < puntosARestar) {
            int puntosFaltantes = puntosARestar - puntosActuales;
            double porcentajePenalidad = (puntosFaltantes * 1.0) / 100.0;
            if (porcentajePenalidad > 1.0)
                porcentajePenalidad = 1.0;

            double comision = this.monto * porcentajePenalidad;
            this.monto -= comision;

            cliente.setPuntos(0);
        } else {
            cliente.setPuntos(puntosActuales - puntosARestar);
        }

        this.estado = "DEVUELTA";
        this.zona.aumentarCapacidad(this.cantidadEntradas);
    }

    public boolean validarLimiteEntradas() {
        return tarjeta.puedeComprar(this.cantidadEntradas);
    }

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

    public Concierto getConcierto() {
        return concierto;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}