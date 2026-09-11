package nsg.portafolio.utiles;

import static org.junit.Assert.assertTrue;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.Test;

public class TailArchivoTest {

    @Test
    public void emiteLineasNuevas() throws Exception {
        File temp = File.createTempFile("tail", ".log");
        temp.deleteOnExit();

        List<String> lineas = new CopyOnWriteArrayList<>();
        TailArchivo tailer = new TailArchivo(temp, lineas::add, 100L);
        try {
            tailer.iniciar();
            Thread.sleep(200);

            Files.write(temp.toPath(), "primera\n".getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
            esperar(lineas, "primera");

            Files.write(temp.toPath(), "segunda\n".getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
            esperar(lineas, "segunda");
        } finally {
            tailer.detener();
        }

        assertTrue("El tailer debe quedar detenido", !tailer.isActivo());
    }

    private void esperar(List<String> lineas, String esperado) throws InterruptedException {
        long limite = System.currentTimeMillis() + 3000;
        while (System.currentTimeMillis() < limite) {
            if (lineas.contains(esperado)) {
                return;
            }
            Thread.sleep(50);
        }
        assertTrue("No se recibio la linea: " + esperado + " -> " + lineas, lineas.contains(esperado));
    }
}
