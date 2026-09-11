package nsg.portafolio.ui;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import nsg.portafolio.db.ConfigDB;
import nsg.portafolio.formulario.PrincipalFrm;

/**
 * Punto de entrada de la aplicacion: aplica el tema, inicializa la base de
 * datos, registra la consola de logs y abre la ventana principal.
 */
public final class App {

    private App() {
    }

    public static void main(String[] args) {
        UITheme.aplicar();

        try {
            ConfigDB.inicializar();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null,
                    "Error al inicializar la base de datos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        TextAreaAppender.registrar();

        SwingUtilities.invokeLater(() -> new PrincipalFrm().setVisible(true));
    }
}
