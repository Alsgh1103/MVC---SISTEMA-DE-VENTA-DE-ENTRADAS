package controlador;

import vista.FrmAdminRegistrarConcierto;
import modelo.Concierto;
import coleccion.ColeccionConciertos;
import java.time.LocalDate;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ControladorAdminRegistrarConcierto implements ActionListener {
    private FrmAdminRegistrarConcierto vista;
    private ControladorPrincipal contextoCentral;
    private ControladorAdminConciertos ctrlAdminAnterior;
    private String rutaImagenTemporal = null;
    private String rutaImagenPosterTemporal = null;

    public ControladorAdminRegistrarConcierto(FrmAdminRegistrarConcierto vista, ControladorPrincipal contextoCentral,
            ControladorAdminConciertos ctrlAdminAnterior) {
        this.vista = vista;
        this.contextoCentral = contextoCentral;
        this.ctrlAdminAnterior = ctrlAdminAnterior;

        this.vista.btnGuardar.addActionListener(this);
        this.vista.btnVolver.addActionListener(this);
        if (this.vista.btnImagenBanner != null) {
            this.vista.btnImagenBanner.addActionListener(this);
        }
        if (this.vista.btnImagenPoster != null) {
            this.vista.btnImagenPoster.addActionListener(this);
        }
        

        Concierto c = contextoCentral.getConciertoSeleccionado();
        if (c != null) {
            vista.txtNombre.setText(c.getNombre());
            vista.dateFechaEvento.setDate(java.sql.Date.valueOf(c.getFecha()));
            if (vista.txtArtista != null) {
                vista.txtArtista.setText(c.getArtista());
            }
            vista.btnGuardar.setText("Guardar Cambios");
            if (vista.lblNombreImagenBanner != null) {
                String ruta = c.getRutaImagen();
                if (ruta != null && !ruta.isEmpty()) {
                    File f = new File(ruta);
                    vista.lblNombreImagenBanner.setText("Imagen actual: " + f.getName());
                } else {
                    vista.lblNombreImagenBanner.setText("Ninguna imagen seleccionada");
                }
            }
                if (vista.lblNombreImagenPoster != null) {
                    String rutaPoster = c.getRutaImagenPoster();
                    if (rutaPoster != null && !rutaPoster.isEmpty()) {
                        vista.lblNombreImagenPoster.setText("Póster actual: " + new File(rutaPoster).getName());
                    } else {
                        vista.lblNombreImagenPoster.setText("Ningún póster seleccionado");
                    }
                }
            }
        }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.btnGuardar) {
            guardarConcierto();
        } else if (e.getSource() == vista.btnVolver) {
            volver();
        } else if (vista.btnImagenBanner != null && e.getSource() == vista.btnImagenBanner) {
            seleccionarImagen();
        } else if (vista.btnImagenPoster != null && e.getSource() == vista.btnImagenPoster) {
            seleccionarImagenPoster();
        }
    }

    private void seleccionarImagen() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar Imagen del Concierto");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Imágenes (JPG, PNG)", "jpg", "jpeg", "png");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(vista);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            rutaImagenTemporal = selectedFile.getAbsolutePath();
            if (vista.lblNombreImagenBanner != null) {
                vista.lblNombreImagenBanner.setText("Imagen seleccionada: " + selectedFile.getName());
            } else {
                JOptionPane.showMessageDialog(vista, "Imagen seleccionada: " + selectedFile.getName(), "Imagen",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void seleccionarImagenPoster() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar Póster (formato 9:16)");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Imágenes (JPG, PNG)", "jpg", "jpeg", "png");
        fileChooser.setFileFilter(filter);

        int result = fileChooser.showOpenDialog(vista);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            rutaImagenPosterTemporal = selectedFile.getAbsolutePath();
            if (vista.lblNombreImagenPoster != null) {
                vista.lblNombreImagenPoster.setText("Póster seleccionado: " + selectedFile.getName());
            } else {
                JOptionPane.showMessageDialog(vista, "Póster seleccionado: " + selectedFile.getName(), "Imagen",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void guardarConcierto() {
        try {
            String nombre = vista.txtNombre.getText().trim();
            String artista = vista.txtArtista != null ? vista.txtArtista.getText().trim() : "Desconocido";
            java.util.Date utilDate = vista.dateFechaEvento.getDate();

            if (nombre.isEmpty() || utilDate == null) {
                JOptionPane.showMessageDialog(vista, "Por favor, complete todos los campos (Nombre y Fecha).",
                        "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate fecha = java.time.Instant.ofEpochMilli(utilDate.getTime())
                    .atZone(java.time.ZoneId.systemDefault()).toLocalDate();

            ColeccionConciertos coleccion = contextoCentral.getColeccionConciertos();
            Concierto conciertoEditado = contextoCentral.getConciertoSeleccionado();

            if (conciertoEditado != null) {
                conciertoEditado.setNombre(nombre);
                conciertoEditado.setArtista(artista);
                conciertoEditado.setFecha(fecha);
            } else {
                conciertoEditado = coleccion.registrarConcierto(nombre, fecha);
                conciertoEditado.setArtista(artista);
            }

            if (rutaImagenTemporal != null) {
                try {
                    File dir = new File("src/main/resources/banners");
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    String extension = rutaImagenTemporal.substring(rutaImagenTemporal.lastIndexOf("."));
                    String nombreArchivo = nombre.replaceAll("[^a-zA-Z0-9.-]", "_") + extension;
                    File dest = new File(dir, nombreArchivo);
                    Files.copy(new File(rutaImagenTemporal).toPath(), dest.toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                    conciertoEditado.setRutaImagen("/banners/" + nombreArchivo);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(vista, "No se pudo copiar la imagen: " + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

            if (rutaImagenPosterTemporal != null) {
                try {
                    File dirPoster = new File("src/main/resources/posters");
                    if (!dirPoster.exists()) {
                        dirPoster.mkdirs();
                    }
                    String extensionPoster = rutaImagenPosterTemporal.substring(rutaImagenPosterTemporal.lastIndexOf("."));
                    String nombreArchivoPoster = nombre.replaceAll("[^a-zA-Z0-9.-]", "_") + "_poster" + extensionPoster;
                    File destPoster = new File(dirPoster, nombreArchivoPoster);
                    Files.copy(new File(rutaImagenPosterTemporal).toPath(), destPoster.toPath(),
                            StandardCopyOption.REPLACE_EXISTING);
                    conciertoEditado.setRutaImagenPoster("/posters/" + nombreArchivoPoster);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(vista, "No se pudo copiar el póster: " + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }

            contextoCentral.setConciertoSeleccionado(conciertoEditado);
            contextoCentral.guardarConciertos();

            if (contextoCentral.getConciertoSeleccionado() != null
                    && vista.btnGuardar.getText().equals("Guardar Cambios")) {
                JOptionPane.showMessageDialog(vista, "Concierto actualizado exitosamente.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(vista,
                        "Concierto creado. Redirigiendo al Panel de Control para añadir Zonas.", "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            volver();

        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(vista, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Ocurrió un error inesperado al guardar.", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void volver() {
        if (ctrlAdminAnterior != null) {
            ctrlAdminAnterior.poblarComboBox();
            ctrlAdminAnterior.refrescarTabla();
            ctrlAdminAnterior.getVista().setVisible(true);
        }
        vista.dispose();
    }
}