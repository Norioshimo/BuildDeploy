package nsg.portafolio.utiles;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import nsg.portafolio.enums.AppServer;

/**
 * Deteccion del puerto HTTP en el que se levanta el servidor de aplicaciones.
 * Se apoya en la configuracion del servidor (XML), el log y, como ultimo
 * recurso, el sondeo de puertos conocidos.
 */
public final class PuertoUtil {

    private static final int[] PUERTOS_WILDFLY = {8080, 8081, 9090, 9080};
    private static final int[] PUERTOS_GLASSFISH = {8080, 8081, 8181, 9090};

    private PuertoUtil() {
    }

    public static int detectarPuertoHttp(AppServer server, String serverHome, String domainName) {
        if (server == AppServer.GLASSFISH) {
            return detectarGlassFish(serverHome, domainName);
        }
        return detectarWildFly(serverHome);
    }

    private static int detectarWildFly(String serverHome) {
        if (serverHome != null && !serverHome.trim().isEmpty()) {
            File xml = Paths.get(serverHome.trim(), "standalone", "configuration", "standalone.xml").toFile();
            int puerto = parsearPuertoWildfly(leer(xml));
            if (puerto > 0) {
                return puerto;
            }
        }
        int puerto = parsearPuertoWildfly(leer(new File("logs", "wildfly-consola.log")));
        if (puerto > 0) {
            return puerto;
        }
        return sondear(PUERTOS_WILDFLY);
    }

    private static int detectarGlassFish(String serverHome, String domainName) {
        String dominio = (domainName == null || domainName.trim().isEmpty()) ? "domain1" : domainName.trim();
        if (serverHome != null && !serverHome.trim().isEmpty()) {
            File xml = Paths.get(serverHome.trim(), "domains", dominio, "config", "domain.xml").toFile();
            int puerto = parsearPuertoGlassFish(leer(xml));
            if (puerto > 0) {
                return puerto;
            }
            File log = Paths.get(serverHome.trim(), "domains", dominio, "logs", "server.log").toFile();
            puerto = parsearPuertoGlassFishLog(leer(log));
            if (puerto > 0) {
                return puerto;
            }
        }
        return sondear(PUERTOS_GLASSFISH);
    }

    /**
     * Extrae el puerto HTTP desde el standalone.xml de WildFly o, si el
     * contenido es un log, desde la linea "Undertow HTTP listener ... :puerto".
     */
    static int parsearPuertoWildfly(String contenido) {
        if (contenido == null) {
            return -1;
        }
        Matcher m = Pattern.compile("(?is)<socket-binding\\b[^>]*name\\s*=\\s*\"http\"[^>]*>").matcher(contenido);
        while (m.find()) {
            int puerto = extraerPuerto(atributo(m.group(), "port"));
            if (puerto > 0) {
                return puerto;
            }
        }
        Matcher log = Pattern.compile("(?i)http listener[^\\r\\n]*listening on[^\\r\\n:]*:(\\d+)").matcher(contenido);
        if (log.find()) {
            return Integer.parseInt(log.group(1));
        }
        return -1;
    }

    /**
     * Extrae el puerto del primer network-listener HTTP (excluye el de admin)
     * del domain.xml de GlassFish.
     */
    static int parsearPuertoGlassFish(String contenido) {
        if (contenido == null) {
            return -1;
        }
        Matcher m = Pattern.compile("(?is)<network-listener\\b[^>]*>").matcher(contenido);
        int puerto = -1;
        while (m.find()) {
            String tag = m.group();
            String nombre = atributo(tag, "name");
            if (nombre == null) {
                continue;
            }
            String normalizado = nombre.toLowerCase();
            if (!normalizado.contains("http-listener") || normalizado.contains("admin")) {
                continue;
            }
            int candidato = extraerPuerto(atributo(tag, "port"));
            if (candidato > 0) {
                puerto = candidato;
                if (normalizado.contains("http-listener-1")) {
                    return puerto;
                }
            }
        }
        return puerto;
    }

    /**
     * Extrae el puerto desde el server.log de GlassFish.
     */
    static int parsearPuertoGlassFishLog(String contenido) {
        if (contenido == null) {
            return -1;
        }
        Matcher m = Pattern.compile("(?i)http-listener[^\\r\\n]*?port\\s+(\\d+)").matcher(contenido);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        return -1;
    }

    private static int sondear(int[] puertos) {
        for (int puerto : puertos) {
            if (ProcesoUtil.puertoAbierto("localhost", puerto)) {
                return puerto;
            }
        }
        return -1;
    }

    private static String atributo(String etiqueta, String nombre) {
        Matcher m = Pattern.compile("(?i)\\b" + nombre + "\\s*=\\s*\"([^\"]+)\"").matcher(etiqueta);
        return m.find() ? m.group(1) : null;
    }

    private static int extraerPuerto(String valor) {
        if (valor == null) {
            return -1;
        }
        Matcher m = Pattern.compile("(\\d+)").matcher(valor);
        return m.find() ? Integer.parseInt(m.group(1)) : -1;
    }

    private static String leer(File archivo) {
        if (archivo == null || !archivo.exists()) {
            return null;
        }
        try {
            return new String(Files.readAllBytes(archivo.toPath()), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            return null;
        }
    }
}
