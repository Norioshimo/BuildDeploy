package nsg.portafolio.ui;

import static org.junit.Assert.assertTrue;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;

public class TextAreaAppenderTest {

    @Test
    public void registraYVuelcaEnElArea() throws Exception {
        UITheme.aplicar();
        TextAreaAppender.registrar();

        final JTextArea area = new JTextArea();
        TextAreaAppender.setArea(area);

        LogManager.getLogger(TextAreaAppenderTest.class).info("mensaje-consola-ui");

        // Espera a que el EDT procese los invokeLater pendientes.
        SwingUtilities.invokeAndWait(() -> {
        });
        Thread.sleep(200);

        assertTrue("El area debe contener el mensaje de log",
                area.getText().contains("mensaje-consola-ui"));
    }
}
