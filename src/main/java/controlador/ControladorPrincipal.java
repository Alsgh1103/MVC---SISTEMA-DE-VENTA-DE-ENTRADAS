package controlador;

import coleccion.ColeccionPersonas;
import coleccion.ColeccionConciertos;
import modelo.Concierto;
import modelo.Venta;
import modelo.Cliente;
import modelo.Persona;
import modelo.Usuario;
import modelo.Zona;
import modelo.Tarjeta;
import java.util.ArrayList;

public class ControladorPrincipal {
    private ColeccionPersonas coleccionPersonas;
    private ColeccionConciertos coleccionConciertos;
    private Persona usuarioLogueado;
    private Concierto conciertoSeleccionado;

    public ControladorPrincipal() {
        this.coleccionPersonas = new ColeccionPersonas();
        this.coleccionConciertos = new ColeccionConciertos();
        Usuario admin = new Usuario("99999999", "Administrador", "Sistema", "admin@gmail.com", "admin123", "ADM001");
        this.coleccionPersonas.guardarPersona(admin);
    }

    public Persona login(String correo, String contrasena) {
        Persona p = coleccionPersonas.buscarPorCorreo(correo);
        if (p != null && p.validarContrasena(contrasena)) {
            this.usuarioLogueado = p;
            return p;
        }
        return null;
    }

    public void cerrarSesion() {
        if (this.usuarioLogueado != null) {
            this.usuarioLogueado.cerrarSesion();
            this.usuarioLogueado = null;
        }
    }

    public void registrarNuevoCliente(String dni, String nombre, String apellido, String correo, String contrasena, boolean esSocio) {
        Cliente nuevo = new Cliente(dni, nombre, apellido, correo, contrasena, esSocio);
        coleccionPersonas.guardarPersona(nuevo);
    }

    public void registrarNuevaPersona(String dni, String nombre, String apellido, String correo, String contrasena, boolean esSocio) {
        registrarNuevoCliente(dni, nombre, apellido, correo, contrasena, esSocio);
    }

    public void registrarNuevoConcierto(String nombre, java.time.LocalDate fecha) {
        Concierto nuevo = new Concierto(nombre, fecha);
        coleccionConciertos.guardarConcierto(nuevo);
        if (this.conciertoSeleccionado == null) {
            this.conciertoSeleccionado = nuevo;
        }
    }

    public void registrarNuevaVenta(Venta v) {
        if (conciertoSeleccionado != null) {
            conciertoSeleccionado.registrarVenta(v);
        }
    }
    
    public String agregarZonaAlConcierto(String nombre, int capacidad, int precio) {
        try {
        // REGLAS DE NEGOCIO:
        if (capacidad <= 0) return "La capacidad debe ser mayor a 0.";
        if (precio < 0) return "El precio no puede ser negativo.";
        if (nombre == null || nombre.trim().isEmpty()) return "El nombre no puede estar vacío.";

        // Si pasa las validaciones, agregamos
        int nuevoId = conciertoSeleccionado.getTodasLasZonas().size() + 1;
        conciertoSeleccionado.agregarZona(new Zona(nuevoId, nombre, precio, capacidad));
        return "OK";
        
        } catch (NumberFormatException e) {
            return "Error: Capacidad y Precio deben ser números enteros.";
        }
    }
    
    public Object[][] getDatosZonasParaTabla() {
        if (conciertoSeleccionado == null) return new Object[0][0];

        ArrayList<Zona> zonas = conciertoSeleccionado.getTodasLasZonas();
        
        // Cambiamos el tamaño del arreglo de 3 a 4 columnas
        Object[][] datos = new Object[zonas.size()][4]; 

        for (int i = 0; i < zonas.size(); i++) {
            Zona z = zonas.get(i);
            
            // Columna 0: Nombre del concierto (Nuevo)
            datos[i][0] = conciertoSeleccionado.getNombre(); 
            // Columna 1: Nombre de la Zona
            datos[i][1] = z.getNombre();
            // Columna 2: Capacidad Disponible
            datos[i][2] = z.getCapacidadDisponible();
            // Columna 3: Entradas Vendidas (Total - Disponible)
            datos[i][3] = z.getCapacidadTotal() - z.getCapacidadDisponible();
        }
        return datos;
    }

    public Concierto getConcierto() {
        return conciertoSeleccionado;
    }

    public Concierto getConciertoSeleccionado() {
        return conciertoSeleccionado;
    }

    public void setConciertoSeleccionado(Concierto c) {
        this.conciertoSeleccionado = c;
    }

    public ArrayList<Concierto> getTodosLosConciertos() {
        return coleccionConciertos.getTodosLosConciertos();
    }

    public ColeccionPersonas getColeccionPersonas() {
        return coleccionPersonas;
    }

    public Persona getUsuarioLogueado() {
        return usuarioLogueado;
    }

    public void setUsuarioLogueado(Persona usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
    }
    
    public Venta procesarCompraConcierto(String nombreZona, int cantidad, Tarjeta tarjetaUsar) throws Exception {
        // 1. Validaciones de flujo básico (El controlador orquesta)
        if (conciertoSeleccionado == null) throw new Exception("Seleccione un concierto primero.");
        
        Zona zonaEncontrada = conciertoSeleccionado.buscarZonaPorNombre(nombreZona);
        if (zonaEncontrada == null) throw new Exception("Zona no encontrada en el sistema.");
        
        if (cantidad <= 0) throw new Exception("La cantidad de entradas debe ser mayor a 0.");

        Persona usuario = this.usuarioLogueado;
        if (!(usuario instanceof Cliente)) throw new Exception("El administrador no puede realizar compras.");
        
        Cliente cliente = (Cliente) usuario;

        // 2. Delegamos la lógica pesada al Modelo
        // Usamos el nuevo constructor que ya no pide el monto total
        Venta nuevaVenta = new Venta(cantidad, cliente, zonaEncontrada, tarjetaUsar);
        
        // 3. Le pedimos al modelo que procese. Él se encarga del stock y los puntos.
        if (nuevaVenta.procesarCompra(tarjetaUsar.getCVV())) {
            // Si la compra fue exitosa, la guardamos en el historial del concierto
            registrarNuevaVenta(nuevaVenta);
            return nuevaVenta; 
        } else {
            // Si procesarCompra retorna false, falló el stock, límite o tarjeta
            throw new Exception("La transacción fue rechazada. Verifique la disponibilidad, su límite de entradas o la tarjeta.");
        }
    }
}
