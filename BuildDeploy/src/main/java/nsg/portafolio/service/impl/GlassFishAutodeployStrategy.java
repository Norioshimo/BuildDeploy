package nsg.portafolio.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import nsg.portafolio.service.DeployStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Despliegue en GlassFish dejando el WAR en la carpeta autodeploy del dominio.
 */
public class GlassFishAutodeployStrategy implements DeployStrategy {

    private static final Logger log = LogManager.getLogger(GlassFishAutodeployStrategy.class);

    private final String buildDir;
    private final String warName;
    private final String glassfishDeployDir;

    public GlassFishAutodeployStrategy(String buildDir, String warName, String glassfishDeployDir) {
        this.buildDir = buildDir;
        this.warName = warName;
        this.glassfishDeployDir = glassfishDeployDir;
    }

    @Override
    public boolean procesar() throws Exception {
        log.info(" ======================================== ");
        log.info(" Deployando el war en GlassFish (autodeploy)... ");
        log.info(" ======================================== ");
        log.info(" Directorio de war: " + buildDir);
        log.info(" Directorio autodeploy de GlassFish: " + glassfishDeployDir);

        File war = new File(buildDir, warName);
        if (!war.exists()) {
            log.error(" El archivo .war especificado no existe: " + war.getAbsolutePath());
            return false;
        }

        Path deployed = Paths.get(glassfishDeployDir, warName + "_deployed");
        Path failed = Paths.get(glassfishDeployDir, warName + "_deploymentfailed");

        log.info(" Eliminar marcadores previos de GlassFish...");
        Files.deleteIfExists(deployed);
        Files.deleteIfExists(failed);

        Path destino = Paths.get(glassfishDeployDir, war.getName());
        log.info(" Copiar al destino: " + destino);
        Files.copy(war.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);

        log.info(" WAR copiado exitosamente en autodeploy de GlassFish.");

        return esperarDespliegue(deployed, failed);
    }

    private boolean esperarDespliegue(Path deployed, Path failed) throws InterruptedException {
        log.info(" Esperando a que GlassFish despliegue: " + warName);

        int intentos = 0;
        int timeout = 90;
        while (intentos < timeout) {
            if (Files.exists(deployed)) {
                log.info(" Despliegue exitoso detectado: " + deployed.getFileName());
                return true;
            }
            if (Files.exists(failed)) {
                log.error(" Despliegue fallido detectado: " + failed.getFileName());
                return false;
            }

            log.info(" Esperando " + intentos + " Segundos...");
            Thread.sleep(1000);
            intentos++;
        }

        log.warn(" Timeout: GlassFish no termino el despliegue en " + timeout + " segundos.");
        return false;
    }

    @Override
    public String descripcion() {
        return "GlassFish (autodeploy)";
    }
}
