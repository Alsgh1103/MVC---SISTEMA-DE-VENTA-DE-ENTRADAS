import controlador.ControladorPrincipal;
import vista.FrmComprarEntradas;

public class TestRun {
    public static void main(String[] args) {
        try {
            ControladorPrincipal ctrl = new ControladorPrincipal();
            FrmComprarEntradas frm = new FrmComprarEntradas(ctrl);
            System.out.println("EXITO");
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
