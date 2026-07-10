package controlador;

import modelo.Cliente;
import modelo.Usuario;
import vista.FrmAdminCrearUsuario;

import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

public class ControladorAdminCrearUsuario implements ActionListener {

    private FrmAdminCrearUsuario vista;
    private ControladorPrincipal contextoCentral;
    private ControladorAdminUsuarios controladorPadre;

    public ControladorAdminCrearUsuario(FrmAdminCrearUsuario vista, ControladorPrincipal contextoCentral, ControladorAdminUsuarios controladorPadre) {
        this.vista = vista;
        this.contextoCentral = contextoCentral;
        this.controladorPadre = controladorPadre;

        this.vista.btnGuardar.addActionListener(this);
        this.vista.btnVolver.addActionListener(this);

        this.vista.cbxTipoUsuario.removeAllItems();
        this.vista.cbxTipoUsuario.addItem("Cliente");
        this.vista.cbxTipoUsuario.addItem("Administrador");

        this.vista.setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.btnVolver) {
            vista.dispose();
        } else if (e.getSource() == vista.btnGuardar) {
            crearUsuario();
        }
    }

    private void crearUsuario() {
        String dni = vista.txtDni.getText().trim();
        String nombre = vista.txtNombre.getText().trim();
        String apellido = vista.txtApellido.getText().trim();
        String correo = vista.txtCorreo.getText().trim();
        String contrasena = new String(vista.txtPass.getPassword());
        String tipoSeleccionado = (String) vista.cbxTipoUsuario.getSelectedItem();

        if (dni.isEmpty() || nombre.isEmpty() || apellido.isEmpty() || correo.isEmpty() || contrasena.isEmpty() || vista.dateFechaNacimiento.getDate() == null) {
            JOptionPane.showMessageDialog(vista, "Por favor, complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (contextoCentral.getColeccionPersonas().buscarPorCorreo(correo) != null) {
            JOptionPane.showMessageDialog(vista, "El correo electrónico ya está registrado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (contextoCentral.getColeccionPersonas().buscarPorDni(dni) != null) {
            JOptionPane.showMessageDialog(vista, "El DNI ya está registrado.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if ("Administrador".equals(tipoSeleccionado)) {
                Usuario nuevoAdmin = new Usuario(dni, nombre, apellido, correo, contrasena, "ADM-" + dni);
                contextoCentral.getColeccionPersonas().guardarPersona(nuevoAdmin);
                contextoCentral.guardarPersonas();
            } else {
                contextoCentral.registrarNuevoCliente(dni, nombre, apellido, correo, contrasena);
            }

            JOptionPane.showMessageDialog(vista, "Usuario creado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            controladorPadre.poblarTabla();
            vista.dispose();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(vista, ex.getMessage(), "Error de Validación", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Ocurrió un error inesperado al crear el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
