package controlador;

import modelo.Persona;
import modelo.Usuario;
import modelo.Cliente;
import vista.FrmAdminUsuarios;
import vista.FrmAdmin;

import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ControladorAdminUsuarios implements ActionListener {

    private FrmAdminUsuarios vista;
    private ControladorPrincipal contextoCentral;

    public ControladorAdminUsuarios(FrmAdminUsuarios vista, ControladorPrincipal contextoCentral) {
        this.vista = vista;
        this.contextoCentral = contextoCentral;

        this.vista.btnEliminarSeleccionados.addActionListener(this);
        this.vista.btnVolver.addActionListener(this);
        if (this.vista.btnCrearUsuario != null) {
            this.vista.btnCrearUsuario.addActionListener(this);
        }

        this.vista.setLocationRelativeTo(null);

        poblarTabla();
        ajustarTamanoColumnaCheck();
    }

    private void ajustarTamanoColumnaCheck() {
        if (vista.tblUsuarios.getColumnCount() > 0) {
            TableColumn colCheck = vista.tblUsuarios.getColumnModel().getColumn(0);
            colCheck.setPreferredWidth(30);
            colCheck.setMaxWidth(30);
            colCheck.setMinWidth(30);
        }
    }

    public void poblarTabla() {
        ArrayList<Persona> personas = contextoCentral.getColeccionPersonas().getTodasLasPersonas();

        String[] columnas = {"✓", "DNI", "Nombres", "Correo", "Tipo"};
        Object[][] datos = new Object[personas.size()][5];

        for (int i = 0; i < personas.size(); i++) {
            Persona p = personas.get(i);
            datos[i][0] = false; 
            datos[i][1] = p.getDni();
            datos[i][2] = p.getNombres() + " " + p.getApellidos();
            datos[i][3] = p.getCorreo();
            datos[i][4] = (p instanceof Usuario) ? "Admin" : "Cliente";
        }

        DefaultTableModel modeloTabla = new DefaultTableModel(datos, columnas) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Boolean.class;
                return String.class;
            }
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0; 
            }
        };

        vista.tblUsuarios.setModel(modeloTabla);
        ajustarTamanoColumnaCheck();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.btnVolver) {
            volver();
        } else if (e.getSource() == vista.btnEliminarSeleccionados) {
            eliminarUsuariosSeleccionados();
        } else if (vista.btnCrearUsuario != null && e.getSource() == vista.btnCrearUsuario) {
            abrirCrearUsuario();
        }
    }

    private void abrirCrearUsuario() {
        vista.FrmAdminCrearUsuario frm = new vista.FrmAdminCrearUsuario();
        new ControladorAdminCrearUsuario(frm, contextoCentral, this);
        frm.setVisible(true);
    }

    private void eliminarUsuariosSeleccionados() {
        DefaultTableModel model = (DefaultTableModel) vista.tblUsuarios.getModel();
        int rowCount = model.getRowCount();

        ArrayList<String> correosAEliminar = new ArrayList<>();

        for (int i = 0; i < rowCount; i++) {
            Boolean checked = (Boolean) model.getValueAt(i, 0);
            if (checked != null && checked) {
                String correo = model.getValueAt(i, 3).toString();
                correosAEliminar.add(correo);
            }
        }

        if (correosAEliminar.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Por favor seleccione al menos un usuario para eliminar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check if superadmin is trying to delete themselves
        for (String c : correosAEliminar) {
            if (c.equals("admin") || c.equals("admin@gmail.com")) {
                JOptionPane.showMessageDialog(vista, "No puedes eliminar la cuenta de SuperAdministrador.", "Operación Denegada", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        int confirm = JOptionPane.showConfirmDialog(vista, 
            "¿Estás seguro que deseas eliminar los " + correosAEliminar.size() + " usuarios seleccionados?\nEsta acción no se puede deshacer.",
            "Confirmar Eliminación", 
            JOptionPane.YES_NO_OPTION, 
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean exito = false;
            for (String correo : correosAEliminar) {
                boolean eliminado = contextoCentral.getColeccionPersonas().eliminarPorCorreo(correo);
                if(eliminado) exito = true;
            }

            if (exito) {
                contextoCentral.guardarEstado();
                JOptionPane.showMessageDialog(vista, "Usuarios eliminados exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                poblarTabla();
            } else {
                JOptionPane.showMessageDialog(vista, "No se pudieron eliminar los usuarios seleccionados.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void volver() {
        FrmAdmin ventanaSuperAdmin = new FrmAdmin();
        new ControladorAdmin(ventanaSuperAdmin, contextoCentral);
        ventanaSuperAdmin.setVisible(true);
        vista.dispose();
    }
}
