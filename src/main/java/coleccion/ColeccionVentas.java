package coleccion;

import java.util.ArrayList;
import modelo.Venta;

public class ColeccionVentas {
    private ArrayList<Venta> ventas;

    public ColeccionVentas() {
        this.ventas = new ArrayList<>();
    }

    public void registrarVenta(Venta v) {
        if (v != null) {
            this.ventas.add(v);
        }
    }

    public ArrayList<Venta> getTodasLasVentas() {
        return this.ventas;
    }
    
    public int getCantidadTotalVentas() {
        return this.ventas.size();
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
