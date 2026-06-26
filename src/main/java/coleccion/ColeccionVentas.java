package coleccion;

import java.util.ArrayList;
import modelo.Venta;

/**
 * Colección general para almacenar y gestionar las ventas en memoria.
 * Sigue la misma estructura y patrón de diseño de ColeccionConciertos y ColeccionPersonas.
 *
 * @author Aniee / Antigravity
 */
public class ColeccionVentas {
    private ArrayList<Venta> ventas;

    public ColeccionVentas() {
        this.ventas = new ArrayList<>();
    }

    /**
     * Registra y guarda una venta en la colección.
     * @param v Venta a guardar.
     */
    public void guardarVenta(Venta v) {
        if (v != null) {
            this.ventas.add(v);
        }
    }

    /**
     * Retorna todas las ventas de la colección.
     * @return Listado completo de ventas.
     */
    public ArrayList<Venta> getTodasLasVentas() {
        return ventas;
    }

    /**
     * Busca y filtra las ventas realizadas por un cliente específico mediante su DNI.
     * @param dni DNI del cliente.
     * @return Listado de ventas asociadas a dicho cliente.
     */
    public ArrayList<Venta> buscarVentasPorCliente(String dni) {
        ArrayList<Venta> resultado = new ArrayList<>();
        if (dni != null) {
            for (Venta v : ventas) {
                if (v.getCliente() != null && dni.trim().equalsIgnoreCase(v.getCliente().getDni().trim())) {
                    resultado.add(v);
                }
            }
        }
        return resultado;
    }
}
