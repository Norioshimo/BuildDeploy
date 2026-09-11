package nsg.portafolio.service.impl;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import nsg.portafolio.service.DeployStrategy;
import nsg.portafolio.utiles.ProcesoUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Despliegue en GlassFish usando el comando asadmin deploy.
 */
public class GlassFishAsadminStrategy implements DeployStrategy {

    private static final Logger log = LogManager.getLogger(GlassFishAsadminStrategy.class);

    private final String buildDir;
    private final String warName;
    private final String asadminPath;
    private final String host;
    private final String port;
    private final String user;
    private final String password;

    public GlassFishAsadminStrategy(String buildDir, String warName, String asadminPath,
            String host, String port, String user, String password) {
        this.buildDir = buildDir;
        this.warName = warName;
        this.asadminPath = asadminPath;
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    @Override
    public boolean procesar() throws Exception {
        log.info(" ======================================== ");
        log.info(" Deployando el war en GlassFish (asadmin)... ");
        log.info(" ======================================== ");

        File war = new File(buildDir, warName);
        if (!war.exists()) {
            log.error(" El archivo .war especificado no existe: " + war.getAbsolutePath());
            return false;
        }

        String asadmin = (asadminPath == null || asadminPath.trim().isEmpty()) ? "asadmin" : asadminPath;

        Path passwordFile = Files.createTempFile("asadmin-pass-", ".tmp");
        try {
            Files.write(passwordFile, ("AS_ADMIN_PASSWORD=" + (password == null ? "" : password))
                    .getBytes(StandardCharsets.UTF_8));

            List<String> comando = new ArrayList<>();
            comando.add(asadmin);
            comando.add("deploy");
            comando.add("--force=true");
            if (host != null && !host.trim().isEmpty()) {
                comando.add("--host");
                comando.add(host.trim());
            }
            if (port != null && !port.trim().isEmpty()) {
                comando.add("--port");
                comando.add(port.trim());
            }
            if (user != null && !user.trim().isEmpty()) {
                comando.add("--user");
                comando.add(user.trim());
            }
            comando.add("--passwordfile");
            comando.add(passwordFile.toString());
            comando.add(war.getAbsolutePath());

            int exitCode = ProcesoUtil.ejecutarYEsperar(comando, null, log);
            if (exitCode != 0) {
                log.error(" Error al desplegar con asadmin. Codigo de salida: " + exitCode);
                return false;
            }
        } finally {
            Files.deleteIfExists(passwordFile);
        }

        log.info(" WAR desplegado exitosamente con asadmin.");
        return true;
    }

    @Override
    public String descripcion() {
        return "GlassFish (asadmin deploy)";
    }
}
