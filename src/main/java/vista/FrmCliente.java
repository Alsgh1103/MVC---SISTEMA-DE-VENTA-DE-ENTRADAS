package vista;
import controlador.ControladorPrincipal;
import modelo.Cliente;

public class FrmCliente extends javax.swing.JFrame {
    private ControladorPrincipal ctrl;
    private double precioSeleccionado = 0.0;
    private modelo.Concierto conciertoSeleccionado;
    private modelo.Tarjeta tarjetaActiva;

    public FrmCliente(ControladorPrincipal ctrl){
        this.ctrl = ctrl;
        this.conciertoSeleccionado = ctrl.getConciertoSeleccionado(); 
        
        if (this.conciertoSeleccionado == null && !ctrl.getTodosLosConciertos().isEmpty()) {
            this.conciertoSeleccionado = ctrl.getTodosLosConciertos().get(0);
            ctrl.setConciertoSeleccionado(this.conciertoSeleccionado);
        }
        initComponents();
        this.setLocationRelativeTo(null);
        
        modelo.Persona usuario = ctrl.getUsuarioLogueado();
        if(usuario != null){
            lblBienvenida.setText("Bienvenido, " + usuario.getNombres() + " " + usuario.getApellidos());
            if (usuario instanceof Cliente){
                Cliente clienteReal = (Cliente) usuario;
                if(clienteReal.isSocio()){
                    lblSocio.setText("Socio: Platino (30% desc)");
                } else {
                    lblSocio.setText("Cliente Regular");
                }
            }
        } else {
            lblBienvenida.setText("Bienvenido, Invitado");
        }
        
        cargarZonasEnCombo();
        cargarTarjetasEnCombo();
    }
    
    private void cargarZonasEnCombo(){
        cbxZonas.removeAllItems();
        if (conciertoSeleccionado != null) {
            modelo.Zona[] zonasDelConcierto = conciertoSeleccionado.getZonas();
            if (zonasDelConcierto != null) {
                for (int i = 0; i < zonasDelConcierto.length; i++) {
                    modelo.Zona z = zonasDelConcierto[i];
                    if (z != null) {
                        cbxZonas.addItem(z.getNombre());
                    }
                }
            }
        }
    }
    
    private void cargarTarjetasEnCombo() {
        cbxTarjeta.removeAllItems();
        modelo.Persona usuario = ctrl.getUsuarioLogueado();
        if (usuario instanceof modelo.Cliente) {
            modelo.Cliente c = (modelo.Cliente) usuario;
            if (c.getTarjeta() != null) {
                String num = c.getTarjeta().getNumero();
                String ultimosCuatro = num.length() > 4 ? num.substring(num.length() - 4) : num;
                cbxTarjeta.addItem("Tarjeta term. " + ultimosCuatro);
            } else {
                cbxTarjeta.addItem("Sin tarjetas registradas");
            }
        }
        cbxTarjeta.addItem("Agregar nueva tarjeta...");
    }

    public void setTarjetaActiva(modelo.Tarjeta t) {
        this.tarjetaActiva = t;
        cargarTarjetasEnCombo();
        if (t != null) {
            cbxTarjeta.setSelectedIndex(0); 
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblBienvenida = new javax.swing.JLabel();
        lblSocio = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        cbxZonas = new javax.swing.JComboBox<>();
        lblDisponibilidad = new javax.swing.JLabel();
        lblPrecioU = new javax.swing.JLabel();
        lblCapacidad = new javax.swing.JLabel();
        lblPrecio = new javax.swing.JLabel();
        spnCantidad = new javax.swing.JSpinner();
        lblTotal = new javax.swing.JLabel();
        lblTotalAPagar = new javax.swing.JLabel();
        lblZonas = new javax.swing.JLabel();
        lblEntradas = new javax.swing.JLabel();
        btnConfirmar = new javax.swing.JButton();
        btnVolver = new javax.swing.JButton();
        cbxTarjeta = new javax.swing.JComboBox<>();
        lblTarjeta = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblBienvenida.setText("Bienvenido");

        lblSocio.setText("Socio: ");

        cbxZonas.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbxZonas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxZonasActionPerformed(evt);
            }
        });

        lblDisponibilidad.setText("Capacidad Disponible: ");

        lblPrecioU.setText("Precio Unitario: ");

        lblCapacidad.setText("x");

        lblPrecio.setText("S/");

        lblTotal.setText("Total");

        lblTotalAPagar.setText("TOTAL A PAGAR:");

        lblZonas.setText("Seleccione Zona:");

        lblEntradas.setText("Cantidad de entradas");

        btnConfirmar.setText("Confirmar Compra");
        btnConfirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmarActionPerformed(evt);
            }
        });

        btnVolver.setText("Volver");
        btnVolver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVolverActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnConfirmar)
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addComponent(lblPrecioU))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(lblDisponibilidad)
                                        .addGap(56, 56, 56)))
                                .addGap(10, 10, 10)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblCapacidad)
                                    .addComponent(lblPrecio)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(172, 172, 172)
                                .addComponent(spnCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblTotalAPagar)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblEntradas)
                                .addComponent(lblZonas)
                                .addComponent(btnVolver)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(75, 75, 75)
                                .addComponent(lblTotal)
                                .addGap(57, 80, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cbxZonas, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE))))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblZonas)
                    .addComponent(cbxZonas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPrecioU)
                    .addComponent(lblPrecio))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDisponibilidad)
                    .addComponent(lblCapacidad))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEntradas)
                    .addComponent(spnCantidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotalAPagar)
                    .addComponent(lblTotal))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 40, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConfirmar)
                    .addComponent(btnVolver))
                .addGap(16, 16, 16))
        );

        cbxTarjeta.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cbxTarjeta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbxTarjetaActionPerformed(evt);
            }
        });

        lblTarjeta.setText("Seleccione Tarjeta");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(57, 57, 57)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(36, 36, 36)
                                .addComponent(lblTarjeta))
                            .addComponent(lblBienvenida))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSocio)
                            .addComponent(cbxTarjeta, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(56, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBienvenida)
                    .addComponent(lblSocio))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbxTarjeta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTarjeta))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(22, 22, 22))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cbxZonasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxZonasActionPerformed

    }//GEN-LAST:event_cbxZonasActionPerformed

    private void btnConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarActionPerformed

    }//GEN-LAST:event_btnConfirmarActionPerformed

    private void cbxTarjetaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbxTarjetaActionPerformed

    }//GEN-LAST:event_cbxTarjetaActionPerformed

    private void btnVolverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVolverActionPerformed

    }//GEN-LAST:event_btnVolverActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConfirmar;
    private javax.swing.JButton btnVolver;
    private javax.swing.JComboBox<String> cbxTarjeta;
    private javax.swing.JComboBox<String> cbxZonas;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblBienvenida;
    private javax.swing.JLabel lblCapacidad;
    private javax.swing.JLabel lblDisponibilidad;
    private javax.swing.JLabel lblEntradas;
    private javax.swing.JLabel lblPrecio;
    private javax.swing.JLabel lblPrecioU;
    private javax.swing.JLabel lblSocio;
    private javax.swing.JLabel lblTarjeta;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotalAPagar;
    private javax.swing.JLabel lblZonas;
    private javax.swing.JSpinner spnCantidad;
    // End of variables declaration//GEN-END:variables

    public javax.swing.JButton getBtnConfirmar() { return btnConfirmar; }
    public javax.swing.JButton getBtnVolver() { return btnVolver; }
    public javax.swing.JComboBox<String> getCbxZonas() { return cbxZonas; }
    public javax.swing.JComboBox<String> getCbxTarjeta() { return cbxTarjeta; }
    public javax.swing.JSpinner getSpnCantidad() { return spnCantidad; }
    
    // Métodos para actualizar visualmente la interfaz de acuerdo al cálculo
    public double getPrecioSeleccionado() { return this.precioSeleccionado; }
    public void setPrecioSeleccionado(double p) { this.precioSeleccionado = p; }
    public void setPrecio(double precio) { lblPrecio.setText("S/ " + precio); }
    public void setCapacidad(int disp) { lblCapacidad.setText("Disponibles: " + disp); }
    public void setTotal(double total) { lblTotal.setText("Total a pagar: S/ " + total); }
    public modelo.Tarjeta getTarjetaActiva() { return this.tarjetaActiva; }


}
