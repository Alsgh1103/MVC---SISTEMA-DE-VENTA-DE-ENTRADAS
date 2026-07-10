package componentes;

import modelo.Concierto;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import controlador.ControladorMenuPrincipal;

public class PanelConciertoTarjeta extends JPanel {
    private Concierto concierto;
    private Image imagenPoster;
    private boolean isHovered = false;
    private ControladorMenuPrincipal controlador;

    public PanelConciertoTarjeta(Concierto concierto, ControladorMenuPrincipal controlador) {
        this.concierto = concierto;
        this.controlador = controlador;
        this.setPreferredSize(new Dimension(200, 300)); 
        this.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (concierto.getRutaImagenPoster() != null && !concierto.getRutaImagenPoster().isEmpty()) {
            java.net.URL imgUrl = getClass().getResource(concierto.getRutaImagenPoster());
            if (imgUrl != null) {
                ImageIcon icon = new ImageIcon(imgUrl);
                icon.getImage().flush();
                imagenPoster = icon.getImage();
            }
        }

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (controlador != null) {
                    controlador.comprarEntradasConcierto(concierto);
                }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(30, 30, 30));
        g2.fillRect(0, 0, getWidth(), getHeight());

        int altoImagen = 230; 

        if (imagenPoster != null) {
            int imgW = imagenPoster.getWidth(this);
            int imgH = imagenPoster.getHeight(this);
            if (imgW > 0 && imgH > 0) {
                double scale = Math.min((double) getWidth() / imgW, (double) altoImagen / imgH);
                int scaledW = (int) (imgW * scale);
                int scaledH = (int) (imgH * scale);
                int xImg = (getWidth() - scaledW) / 2;
                int yImg = (altoImagen - scaledH) / 2;
                g2.drawImage(imagenPoster, xImg, yImg, scaledW, scaledH, this);
            }
        } else {
            g2.setColor(Color.DARK_GRAY);
            g2.fillRect(0, 0, getWidth(), altoImagen);
        }

        if (isHovered) {
            g2.setColor(new Color(0, 0, 0, 170)); 
            g2.fillRect(0, 0, getWidth(), altoImagen);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 18));
            String texto = "Comprar entradas";
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(texto)) / 2;
            int y = (altoImagen - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(texto, x, y);
        }

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        FontMetrics fmNombre = g2.getFontMetrics();
        int xNombre = (getWidth() - fmNombre.stringWidth(concierto.getNombre())) / 2;
        g2.drawString(concierto.getNombre(), xNombre, altoImagen + 28);
        
        if (concierto.getArtista() != null) {
            g2.setColor(new Color(170, 170, 170)); 
            g2.setFont(new Font("Segoe UI", Font.ITALIC, 13));
            FontMetrics fmArtista = g2.getFontMetrics();
            int xArtista = (getWidth() - fmArtista.stringWidth(concierto.getArtista())) / 2;
            g2.drawString(concierto.getArtista(), xArtista, altoImagen + 48);
        }
    }
}
