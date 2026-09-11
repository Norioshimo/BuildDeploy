package nsg.portafolio.utiles;

import java.awt.Toolkit;
import java.io.InputStream;
import javazoom.jl.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Reproduce avisos sonoros de exito y error.
 */
public class Sonidos {

    private static final Logger log = LogManager.getLogger(Sonidos.class);

    private static final String ARCHIVO_ERROR = "/Bad-signal-sound-effect.mp3";

    /**
     * Sonido de exito (beep del sistema, no bloqueante).
     */
    public static void exito() {
        try {
            Toolkit.getDefaultToolkit().beep();
        } catch (Exception e) {
            log.warn("No se pudo reproducir el sonido de exito: " + e.getMessage());
        }
    }

    /**
     * Sonido de error (archivo mp3 en recursos).
     */
    public static void error() {
        try {
            InputStream is = Sonidos.class.getResourceAsStream(ARCHIVO_ERROR);
            if (is == null) {
                log.info("No se encontro el archivo mp3 en recursos.");
                return;
            }
            Player player = new Player(is);
            player.play();
        } catch (Exception e) {
            log.warn("No se pudo reproducir el sonido de error: " + e.getMessage());
        }
    }

    /**
     * Compatibilidad: reproduce el sonido de error.
     */
    public static void reproducir() {
        error();
    }
}
