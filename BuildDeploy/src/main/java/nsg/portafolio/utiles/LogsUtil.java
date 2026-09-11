package nsg.portafolio.utiles;

import java.io.File;
import java.nio.file.Paths;
import nsg.portafolio.enums.AppServer;
import nsg.portafolio.model.Configuracion;

/**
 * Resolucion centralizada de las rutas de los archivos de log.
 */
public final class LogsUtil {

    private LogsUtil() {
    }

    public static File archivoLogAplicacion() {
        return new File("logs/app.log");
    }

    public static File archivoConsolaArranque() {
        return new File("logs/wildfly-consola.log");
    }

    /**
     * Devuelve el server.log del servidor configurado. Si no hay datos
     * suficientes, cae al log de la aplicacion.
     */
    public static File archivoLogServidor(Configuracion conf) {
        if (conf == null || conf.getServerHome() == null || conf.getServerHome().trim().isEmpty()) {
            return archivoLogAplicacion();
        }
        AppServer server = conf.getServidor() == null ? AppServer.WILDFLY : conf.getServidor();
        if (server == AppServer.GLASSFISH) {
            String dominio = (conf.getDomainName() == null || conf.getDomainName().trim().isEmpty())
                    ? "domain1" : conf.getDomainName().trim();
            return Paths.get(conf.getServerHome(), "domains", dominio, "logs", "server.log").toFile();
        }
        return Paths.get(conf.getServerHome(), "standalone", "log", "server.log").toFile();
    }
}
