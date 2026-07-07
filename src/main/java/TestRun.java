import controlador.ControladorComprarEntradas;
import controlador.ControladorPrincipal;
import vista.FrmComprarEntradas;

public class TestRun {
    public static void main(String[] args) {
        try {
            ControladorPrincipal ctrl = new ControladorPrincipal();
            FrmComprarEntradas vista = new FrmComprarEntradas();
            new ControladorComprarEntradas(ctrl, vista, ctrl.getColeccionVentas());
            vista.setVisible(true);
            System.out.println("EXITO");
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
