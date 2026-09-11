package nsg.portafolio.utiles;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;

/**
 * Sigue un archivo en vivo (tipo tail -f): lee las lineas nuevas a medida que
 * se escriben y las entrega por callback. Es agnostico de la UI.
 */
public class TailArchivo {

    private final File archivo;
    private final Consumer<String> callback;
    private final long intervaloMs;
    private final boolean desdeInicio;

    private volatile boolean activo;
    private Thread hilo;

    public TailArchivo(File archivo, Consumer<String> callback) {
        this(archivo, callback, 500L, false);
    }

    public TailArchivo(File archivo, Consumer<String> callback, long intervaloMs) {
        this(archivo, callback, intervaloMs, false);
    }

    public TailArchivo(File archivo, Consumer<String> callback, long intervaloMs, boolean desdeInicio) {
        this.archivo = archivo;
        this.callback = callback;
        this.intervaloMs = intervaloMs;
        this.desdeInicio = desdeInicio;
    }

    public synchronized void iniciar() {
        if (activo) {
            return;
        }
        activo = true;
        hilo = new Thread(this::seguir, "tail-" + archivo.getName());
        hilo.setDaemon(true);
        hilo.start();
    }

    public synchronized void detener() {
        activo = false;
        if (hilo != null) {
            hilo.interrupt();
            try {
                hilo.join(2000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            hilo = null;
        }
    }

    public boolean isActivo() {
        return activo;
    }

    private void seguir() {
        long posicion = -1L;
        ByteArrayOutputStream pendiente = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];

        while (activo) {
            try {
                if (!archivo.exists()) {
                    dormir();
                    continue;
                }

                try (RandomAccessFile raf = new RandomAccessFile(archivo, "r")) {
                    long longitud = raf.length();
                    if (posicion < 0) {
                        posicion = desdeInicio ? 0L : longitud;
                        pendiente.reset();
                    } else if (longitud < posicion) {
                        posicion = 0L;
                        pendiente.reset();
                    }

                    if (longitud == posicion) {
                        dormir();
                        continue;
                    }

                    raf.seek(posicion);
                    int leidos;
                    while (activo && (leidos = raf.read(buffer)) != -1) {
                        for (int i = 0; i < leidos; i++) {
                            int b = buffer[i] & 0xFF;
                            if (b == '\n') {
                                emitir(pendiente);
                            } else if (b != '\r') {
                                pendiente.write(b);
                            }
                        }
                        posicion = raf.getFilePointer();
                    }
                }
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception ex) {
                try {
                    dormir();
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    private void emitir(ByteArrayOutputStream pendiente) {
        String linea = new String(pendiente.toByteArray(), StandardCharsets.UTF_8);
        pendiente.reset();
        try {
            callback.accept(linea);
        } catch (Exception ex) {
            // Un callback defectuoso no debe detener el seguimiento.
        }
    }

    private void dormir() throws InterruptedException {
        Thread.sleep(intervaloMs);
    }
}
