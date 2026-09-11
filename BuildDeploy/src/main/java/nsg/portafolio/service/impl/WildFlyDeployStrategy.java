package nsg.portafolio.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import nsg.portafolio.service.DeployStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WildFlyDeployStrategy implements DeployStrategy {

    private static final Logger log = LogManager.getLogger(WildFlyDeployStrategy.class);

    private final String buildDir;
    private final String warName;
    private final String wildflyDeployDir;

    public WildFlyDeployStrategy(String buildDir, String warName, String wildflyDeployDir) {
        this.buildDir = buildDir;
        this.warName = warName;
        this.wildflyDeployDir = wildflyDeployDir;
    }

    @Override
    public boolean procesar() throws Exception {
        log.info(" ======================================== ");
        log.info(" Deployando el war en WildFly... ");
        log.info(" ======================================== ");
        log.info(" Directorio de war: " + buildDir);
        log.info(" Directorio de WildFly: " + wildflyDeployDir);

        File war = new File(buildDir, warName);
        if (!war.exists()) {
            log.error(" El archivo .war especificado no existe: " + war.getAbsolutePath());
            return false;
        }

        log.info(" Eliminar archivos de marcador (marker files) de WildFly...");
        Path deployed = Paths.get(wildflyDeployDir, warName + ".deployed");
        Path failed = Paths.get(wildflyDeployDir, warName + ".failed");
        Path dodeploy = Paths.get(wildflyDeployDir, warName + ".dodeploy");

        Files.deleteIfExists(deployed);
        Files.deleteIfExists(failed);
        Files.deleteIfExists(dodeploy);

        Path destino = Paths.get(wildflyDeployDir, war.getName());
        log.info(" Copiar al destino: " + destino);
        Files.copy(war.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);

        log.info(" Crear .dodeploy.");
        Files.createFile(dodeploy);

        log.info(" WAR copiado exitosamente en WildFly.");

        return esperarDespliegue(deployed, failed);
    }

    private boolean esperarDespliegue(Path deployed, Path failed) throws InterruptedException {
        log.info(" Esperando a que WildFly despliegue: " + warName);

        int intentos = 0;
        int timeout = 60;
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

        log.warn(" Timeout: WildFly no termino el despliegue en " + timeout + " segundos.");
        return false;
    }

    @Override
    public String descripcion() {
        return "WildFly (deployments + .dodeploy)";
    }
}
