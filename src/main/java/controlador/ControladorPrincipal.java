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
import vista.FrmAdmin;
import vista.FrmRegistro;
import vista.FrmLogin;
import vista.FrmMenuPrincipal;
import vista.FrmCliente;
import vista.FrmTarjeta;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;

public class ControladorPrincipal implements ActionListener {
    
    // Modelos y colecciones
    private ColeccionPersonas coleccionPersonas;
    private ColeccionConciertos coleccionConciertos;
    private Persona usuarioLogueado;
    private Concierto conciertoSeleccionado;
    
    // Referencias a TODAS las vistas
    private FrmLogin vistaLogin;
    private FrmRegistro vistaRegistro;
    private FrmAdmin vistaAdmin;
    private FrmMenuPrincipal vistaMenu;
    private FrmCliente vistaCliente;
    private FrmTarjeta vistaTarjeta;

    public ControladorPrincipal() {
        this.coleccionPersonas = new ColeccionPersonas();
        this.coleccionConciertos = new ColeccionConciertos();
        
        Usuario admin = new Usuario("99999999", "Administrador", "Sistema", "admin@gmail.com", "admin123", "ADM001");
        this.coleccionPersonas.guardarPersona(admin);
    }

    // =========================================================
    // MÉTODOS DE VINCULACIÓN (CONECTAN LOS "OÍDOS" A LOS BOTONES)
    // =========================================================

    public void vincularVistaLogin(FrmLogin v) {
        this.vistaLogin = v;
        this.vistaLogin.getBtnIniciarSesion().addActionListener(this);
        this.vistaLogin.getBtnRegistrarse().addActionListener(this);
    }

    public void vincularVistaRegistro(FrmRegistro v) {
        this.vistaRegistro = v;
        this.vistaRegistro.getBtnGuardar().addActionListener(this);
        this.vistaRegistro.getBtnVolver().addActionListener(this);
    }

    public void vincularVistaAdmin(FrmAdmin v) {
        this.vistaAdmin = v;
        this.vistaAdmin.getBtnRefrescar().addActionListener(this);
        this.vistaAdmin.getBtnRegistrarZona().addActionListener(this);
        this.vistaAdmin.getBtnRegistrarConcierto().addActionListener(this);
        this.vistaAdmin.getBtnCerrarTabla().addActionListener(this);

        if (this.conciertoSeleccionado == null) {
            this.registrarNuevoConcierto("Aniversario UNMSM Prueba", java.time.LocalDate.now());
            this.conciertoSeleccionado.getZonas()[0] = new Zona(1, "Zona VIP", 150, 100);
            this.conciertoSeleccionado.getZonas()[1] = new Zona(2, "Zona Preferencial", 80, 200);
        }
        refrescarTablaAuditoria(); 
    }

    public void vincularVistaMenu(FrmMenuPrincipal v) {
        this.vistaMenu = v;
        this.vistaMenu.getBtnComprar().addActionListener(this);
        this.vistaMenu.getBtnVerZonas().addActionListener(this);
        this.vistaMenu.getBtnMisCompras().addActionListener(this);
        this.vistaMenu.getBtnCerrarSesion().addActionListener(this);
        this.vistaMenu.getCbxConcierto().addActionListener(this);
    }

    public void vincularVistaCliente(FrmCliente v) {
        this.vistaCliente = v;
        this.vistaCliente.getBtnConfirmar().addActionListener(this);
        this.vistaCliente.getBtnVolver().addActionListener(this);
        this.vistaCliente.getCbxZonas().addActionListener(this);
        this.vistaCliente.getCbxTarjeta().addActionListener(this);
    }

    public void vincularVistaTarjeta(FrmTarjeta v) {
        this.vistaTarjeta = v;
        this.vistaTarjeta.getBtnGuardar().addActionListener(this);
        this.vistaTarjeta.getBtnVolver().addActionListener(this);
    }

    // =========================================================
    // RUTAS DE NAVEGACIÓN Y LÓGICA DE CONTROLADOR
    // =========================================================

    @Override
    public void actionPerformed(ActionEvent e) {
        
        // ------------------ 1. EVENTOS DE LOGIN ------------------
        if (this.vistaLogin != null) {
            if (e.getSource() == this.vistaLogin.getBtnRegistrarse()) {
                this.vistaLogin.dispose();
                FrmRegistro reg = new FrmRegistro(this);
                vincularVistaRegistro(reg); // ¡AQUÍ ESTÁ LA MAGIA QUE FALTABA!
                reg.setVisible(true);
                
            } else if (e.getSource() == this.vistaLogin.getBtnIniciarSesion()) {
                String correo = this.vistaLogin.getCorreo();
                String contrasena = this.vistaLogin.getContrasena();
                
                if (correo.isEmpty() || contrasena.isEmpty()) {
                    JOptionPane.showMessageDialog(this.vistaLogin, "Complete todos los campos.");
                    return;
                }
                Persona p = this.login(correo, contrasena);
                if (p != null) {
                    JOptionPane.showMessageDialog(this.vistaLogin, "¡Bienvenido, " + p.getNombres() + "!");
                    if (p instanceof Usuario) { 
                        FrmAdmin admin = new FrmAdmin();
                        vincularVistaAdmin(admin);
                        admin.setVisible(true);
                    } else { 
                        FrmMenuPrincipal menu = new FrmMenuPrincipal(this);
                        vincularVistaMenu(menu);
                        menu.setVisible(true);
                    }
                    this.vistaLogin.dispose();
                } else {
                    JOptionPane.showMessageDialog(this.vistaLogin, "Credenciales incorrectas.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }

        // ------------------ 2. EVENTOS DE REGISTRO ------------------
        if (this.vistaRegistro != null) {
            if (e.getSource() == this.vistaRegistro.getBtnVolver()) {
                this.vistaRegistro.dispose();
                FrmLogin login = new FrmLogin(this);
                vincularVistaLogin(login); // ¡VINCULACIÓN OBLIGATORIA AL REGRESAR!
                login.setVisible(true);
                
            } else if (e.getSource() == this.vistaRegistro.getBtnGuardar()) {
                String dni = this.vistaRegistro.getDni();
                String nom = this.vistaRegistro.getNombre();
                String ape = this.vistaRegistro.getApellido();
                String correo = this.vistaRegistro.getCorreo();
                String pass = this.vistaRegistro.getPass();
                
                if (!dni.isEmpty() && !pass.isEmpty() && !nom.isEmpty() && !ape.isEmpty() && !correo.isEmpty()) {
                    this.registrarNuevoCliente(dni, nom, ape, correo, pass, false);
                    JOptionPane.showMessageDialog(this.vistaRegistro, "Registro exitoso.");
                    
                    this.vistaRegistro.dispose();
                    FrmLogin login = new FrmLogin(this);
                    vincularVistaLogin(login); // ¡VINCULACIÓN OBLIGATORIA AL REGRESAR!
                    login.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(this.vistaRegistro, "Complete todos los campos.");
                }
            }
        }

        // ------------------ 3. EVENTOS DE ADMIN ------------------
        if (this.vistaAdmin != null) {
            if (e.getSource() == this.vistaAdmin.getBtnRefrescar()) {
                refrescarTablaAuditoria();
                
            } else if (e.getSource() == this.vistaAdmin.getBtnCerrarTabla()) {
                this.cerrarSesion();
                this.vistaAdmin.dispose();
                FrmLogin login = new FrmLogin(this);
                vincularVistaLogin(login);
                login.setVisible(true);
                
            } else if (e.getSource() == this.vistaAdmin.getBtnRegistrarConcierto()) {
                // Según tu mapa de rutas: "Registrar Concierto" abre FrmMenuPrincipal
                this.vistaAdmin.dispose();
                FrmMenuPrincipal menu = new FrmMenuPrincipal(this);
                vincularVistaMenu(menu);
                menu.setVisible(true);
                
            } else if (e.getSource() == this.vistaAdmin.getBtnRegistrarZona()) { 
                // Tu botón visualmente dice "Registrar Compra" (variable btnRegistrarZona)
                // Enrutamos directamente a la ventana de compra (FrmCliente)
                this.vistaAdmin.dispose();
                FrmCliente cliente = new FrmCliente(this);
                vincularVistaCliente(cliente);
                cliente.setVisible(true);
            }
        }

        // ------------------ 4. EVENTOS DEL MENÚ PRINCIPAL ------------------
        if (this.vistaMenu != null) {
            if (e.getSource() == this.vistaMenu.getCbxConcierto()) {
                String seleccion = (String) this.vistaMenu.getCbxConcierto().getSelectedItem();
                if (seleccion != null) {
                    for (Concierto con : this.getTodosLosConciertos()) {
                        if (con.getNombre().equals(seleccion)) {
                            this.setConciertoSeleccionado(con);
                            break;
                        }
                    }
                }
            } else if (e.getSource() == this.vistaMenu.getBtnVerZonas()) {
                if (this.conciertoSeleccionado != null) {
                    StringBuilder sb = new StringBuilder("Zonas del Concierto: " + this.conciertoSeleccionado.getNombre() + "\n\n");
                    Zona[] zonas = this.conciertoSeleccionado.getZonas();
                    if (zonas != null) {
                        for (int i = 0; i < zonas.length; i++) {
                            if (zonas[i] != null) {
                                sb.append("- ").append(zonas[i].getNombre()).append(": S/ ").append(zonas[i].getPrecio())
                                  .append(" (Disponibles: ").append(zonas[i].getCapacidadDisponible()).append(")\n");
                            }
                        }
                    }
                    JOptionPane.showMessageDialog(this.vistaMenu, sb.toString());
                }
            } else if (e.getSource() == this.vistaMenu.getBtnComprar()) {
                this.vistaMenu.dispose();
                FrmCliente cliente = new FrmCliente(this);
                vincularVistaCliente(cliente);
                cliente.setVisible(true);
            } else if (e.getSource() == this.vistaMenu.getBtnCerrarSesion()) {
                this.cerrarSesion();
                this.vistaMenu.dispose();
                FrmLogin login = new FrmLogin(this);
                vincularVistaLogin(login);
                login.setVisible(true);
            }
        }

        // ------------------ 5. EVENTOS DEL CLIENTE (COMPRA) ------------------
        if (this.vistaCliente != null) {
            if (e.getSource() == this.vistaCliente.getBtnVolver()) {
                this.vistaCliente.dispose();
                FrmMenuPrincipal menu = new FrmMenuPrincipal(this);
                vincularVistaMenu(menu);
                menu.setVisible(true);
                
            } else if (e.getSource() == this.vistaCliente.getCbxZonas()) {
                String nombreZona = (String) this.vistaCliente.getCbxZonas().getSelectedItem();
                if (nombreZona != null && conciertoSeleccionado != null) {
                    Zona zona = conciertoSeleccionado.buscarZonaPorNombre(nombreZona);
                    if (zona != null) {
                        this.vistaCliente.setPrecioSeleccionado(zona.getPrecio());
                        this.vistaCliente.setPrecio(zona.getPrecio());
                        this.vistaCliente.setCapacidad(zona.getCapacidadDisponible());
                    }
                }
                
            } else if (e.getSource() == this.vistaCliente.getCbxTarjeta()) {
                String seleccion = (String) this.vistaCliente.getCbxTarjeta().getSelectedItem();
                if (seleccion != null && seleccion.equals("Agregar nueva tarjeta...")) {
                    this.vistaCliente.setVisible(false);
                    FrmTarjeta tarjeta = new FrmTarjeta(this, this.vistaCliente);
                    vincularVistaTarjeta(tarjeta);
                    tarjeta.setVisible(true);
                }
                
            } else if (e.getSource() == this.vistaCliente.getBtnConfirmar()) {
                procesarCompraBoleto(); // Ejecuta la compra delegada al modelo
            }
        }

        // ------------------ 6. EVENTOS DE TARJETA ------------------
        if (this.vistaTarjeta != null) {
            if (e.getSource() == this.vistaTarjeta.getBtnVolver()) {
                this.vistaTarjeta.dispose();
                if (this.vistaCliente != null) this.vistaCliente.setVisible(true);
                
            } else if (e.getSource() == this.vistaTarjeta.getBtnGuardar()) {
                String nro = this.vistaTarjeta.getNumeroTarjeta();
                String titular = this.vistaTarjeta.getNombreTarjeta();
                String venc = this.vistaTarjeta.getVencimiento();
                String cvvStr = this.vistaTarjeta.getCvv();
                
                if (nro.isEmpty() || titular.isEmpty() || venc.isEmpty() || cvvStr.isEmpty()) {
                    JOptionPane.showMessageDialog(this.vistaTarjeta, "Complete todos los campos.");
                    return;
                }
                
                try {
                    int cvv = Integer.parseInt(cvvStr);
                    Tarjeta nuevaTarjeta = new Tarjeta(nro, titular, venc, cvv);
                    
                    if (this.vistaTarjeta.getGuardarFuturas() && this.usuarioLogueado instanceof Cliente) {
                        ((Cliente) this.usuarioLogueado).setTarjeta(nuevaTarjeta);
                    }
                    if (this.vistaCliente != null) {
                        this.vistaCliente.setTarjetaActiva(nuevaTarjeta);
                    }
                    JOptionPane.showMessageDialog(this.vistaTarjeta, "Tarjeta registrada correctamente.");
                    this.vistaTarjeta.dispose();
                    if (this.vistaCliente != null) this.vistaCliente.setVisible(true);
                } catch(NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this.vistaTarjeta, "El CVV debe ser numérico.");
                }
            }
        }
    }

    // =========================================================
    // LÓGICA DE MEDIACIÓN (COMPRAS Y TABLAS)
    // =========================================================

    private void procesarCompraBoleto() {
        if (conciertoSeleccionado == null) return;
        
        String nombreZona = (String) this.vistaCliente.getCbxZonas().getSelectedItem();
        Zona zona = conciertoSeleccionado.buscarZonaPorNombre(nombreZona);
        if (zona == null) return;

        int cantidad = (int) this.vistaCliente.getSpnCantidad().getValue();
        if (!(this.usuarioLogueado instanceof Cliente)) return;
        Cliente cliente = (Cliente) this.usuarioLogueado;
        
        Tarjeta tarjetaUsar = this.vistaCliente.getTarjetaActiva();
        if (tarjetaUsar == null) tarjetaUsar = cliente.getTarjeta();
        
        if (tarjetaUsar == null) {
            JOptionPane.showMessageDialog(this.vistaCliente, "Registre una tarjeta de pago.");
            return;
        }

        // El Controlador confía ciegamente en el Modelo Venta y su matemática (Venta.java refactorizado)
        Venta transaccion = new Venta(cantidad, 0, cliente, zona, tarjetaUsar);
        
        if (transaccion.procesarCompra(tarjetaUsar.getCVV())) {
            this.registrarNuevaVenta(transaccion);
            cliente.setPuntos(cliente.getPuntos() + (cantidad * 10)); 
            
            JOptionPane.showMessageDialog(this.vistaCliente, 
                "¡Compra Exitosa!\nTransacción: " + transaccion.getIdTransaccion() + 
                "\nMonto: S/ " + transaccion.getMonto());
            
            this.vistaCliente.dispose();
            FrmMenuPrincipal menu = new FrmMenuPrincipal(this);
            vincularVistaMenu(menu);
            menu.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this.vistaCliente, 
                "Transacción rechazada.\nVerifique saldo, CVV, límite de 4 entradas y disponibilidad.", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refrescarTablaAuditoria() {
        if (conciertoSeleccionado == null) return;
        DefaultTableModel modelo = (DefaultTableModel) vistaAdmin.getTblVentas().getModel();
        modelo.setRowCount(0); 
        Zona[] zonas = conciertoSeleccionado.getZonas(); 
        if (zonas != null) {
            for (int i = 0; i < zonas.length; i++) {
                if (zonas[i] != null) { 
                    modelo.addRow(new Object[]{ zonas[i].getNombre(), zonas[i].getCapacidadDisponible(), (zonas[i].getCapacidadTotal() - zonas[i].getCapacidadDisponible()) });
                }
            }
        }
    }

    // =========================================================
    // TUS MÉTODOS ORIGINALES (NO TOCAR)
    // =========================================================

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

    public void registrarNuevoCliente(String dni, String nom, String ape, String correo, String pass, boolean socio) {
        Cliente nuevo = new Cliente(dni, nom, ape, correo, pass, socio);
        coleccionPersonas.guardarPersona(nuevo);
    }

    public void registrarNuevoConcierto(String nombre, java.time.LocalDate fecha) {
        Concierto nuevo = new Concierto(nombre, fecha);
        coleccionConciertos.guardarConcierto(nuevo);
        if (this.conciertoSeleccionado == null) this.conciertoSeleccionado = nuevo;
    }

    public void registrarNuevaVenta(Venta v) {
        if (conciertoSeleccionado != null) conciertoSeleccionado.registrarVenta(v);
    }

    public Concierto getConcierto() { return conciertoSeleccionado; }
    public Concierto getConciertoSeleccionado() { return conciertoSeleccionado; }
    public void setConciertoSeleccionado(Concierto c) { this.conciertoSeleccionado = c; }
    public ArrayList<Concierto> getTodosLosConciertos() { return coleccionConciertos.getTodosLosConciertos(); }
    public ColeccionPersonas getColeccionPersonas() { return coleccionPersonas; }
    public Persona getUsuarioLogueado() { return usuarioLogueado; }
    public void setUsuarioLogueado(Persona usuarioLogueado) { this.usuarioLogueado = usuarioLogueado; }
}