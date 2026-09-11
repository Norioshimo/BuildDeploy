package nsg.portafolio.utiles;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import org.apache.logging.log4j.Logger;

/**
 * Utilidades para ejecutar procesos externos (mvn, ant, standalone, asadmin).
 */
public final class ProcesoUtil {

    private ProcesoUtil() {
    }

    public static boolean esWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }

    /**
     * Ejecuta un comando, espera a que termine y vuelca su salida al logger.
     *
     * @return el codigo de salida del proceso.
     */
    public static int ejecutarYEsperar(List<String> comando, File directorioTrabajo, Logger log) throws Exception {
        log.info(" Ejecutando: " + String.join(" ", comando));
        if (directorioTrabajo != null) {
            log.info(" Directorio de trabajo: " + directorioTrabajo.getAbsolutePath());
        }

        ProcessBuilder builder = new ProcessBuilder(comando);
        builder.redirectErrorStream(true);
        if (directorioTrabajo != null && directorioTrabajo.exists()) {
            builder.directory(directorioTrabajo);
        }

        Process process = builder.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line);
            }
        }

        return process.waitFor();
    }

    /**
     * Lanza un comando en segundo plano sin bloquear el hilo actual. La salida
     * se redirige a un archivo dentro de la carpeta logs.
     */
    public static Process ejecutarEnSegundoPlano(List<String> comando, File directorioTrabajo, File archivoSalida, Logger log) throws Exception {
        log.info(" Lanzando en segundo plano: " + String.join(" ", comando));

        ProcessBuilder builder = new ProcessBuilder(comando);
        builder.redirectErrorStream(true);
        if (directorioTrabajo != null && directorioTrabajo.exists()) {
            builder.directory(directorioTrabajo);
        }
        if (archivoSalida != null) {
            File padre = archivoSalida.getParentFile();
            if (padre != null && !padre.exists()) {
                padre.mkdirs();
            }
            builder.redirectOutput(ProcessBuilder.Redirect.appendTo(archivoSalida));
        }

        return builder.start();
    }

    /**
     * Ejecuta un comando y devuelve su salida combinada.
     */
    public static String capturarSalida(List<String> comando, File directorioTrabajo) throws Exception {
        ProcessBuilder builder = new ProcessBuilder(comando);
        builder.redirectErrorStream(true);
        if (directorioTrabajo != null && directorioTrabajo.exists()) {
            builder.directory(directorioTrabajo);
        }

        Process process = builder.start();
        StringBuilder salida = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                salida.append(line).append(System.lineSeparator());
            }
        }
        process.waitFor();
        return salida.toString();
    }

    /**
     * Verifica si un puerto TCP esta abierto (servidor escuchando).
     */
    public static boolean puertoAbierto(String host, int puerto) {
        String hostFinal = (host == null || host.trim().isEmpty()) ? "localhost" : host.trim();
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(hostFinal, puerto), 1500);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
