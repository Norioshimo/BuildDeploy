/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nsg.portafolio.utiles;

import java.io.InputStream;
import javazoom.jl.player.Player;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Acer
 */
public class Sonidos {

    private static final Logger log = LogManager.getLogger(Sonidos.class);

    public static void reproducir() {
        try {
            // El archivo está en src/main/resources
            InputStream is = Sonidos.class.getResourceAsStream("/Bad-signal-sound-effect.mp3");
            if (is == null) {
                log.info("No se encontró el archivo mp3 en recursos.");
                return;
            }

            Player player = new Player(is);
            player.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
