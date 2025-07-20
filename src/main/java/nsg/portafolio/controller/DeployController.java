package nsg.portafolio.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DeployController {

    private static final Logger log = LogManager.getLogger(DeployController.class);

    private String buildDir = null;
    private String warName = null;
    private String wildflyDeployDir = null;

    public DeployController(String buildDir, String warName, String wildflyDeployDir) {
        this.buildDir = buildDir;
        this.warName = warName;
        this.wildflyDeployDir = wildflyDeployDir;

    }

    public boolean procesar() throws Exception {
        log.info(" ======================================== ");
        log.info(" Deployando el war... ");
        log.info(" ======================================== ");

        log.info(" ");
        log.info(" ");
        log.info(" ======================================== ");
        log.info(" Parametros configurados... ");
        log.info(" Directorio de war: " + buildDir);
        log.info(" Directorio de Wildfly: " + wildflyDeployDir);
        log.info(" ======================================== ");

        log.info(" Buscar .war a deployar");
        File latestWar = new File(buildDir, warName);
        if (!latestWar.exists()) {
            log.info(" El archivo .war especificado no existe: " + latestWar.getAbsolutePath());
            return false;
        }

        // Antes de copiar el war, borrar los archivos 
        log.info(" Eliminar archivos de marcador (marker files) de WildFly...");
        log.info(" Antes de copiar el WAR borrar el " + warName + ".deployed si es que existe. Eliminado: " + Files.deleteIfExists(Paths.get(wildflyDeployDir, warName + ".deployed")));
        log.info(" Antes de copiar el WAR borrar el " + warName + ".failed si es que existe. Eliminado: " + Files.deleteIfExists(Paths.get(wildflyDeployDir, warName + ".failed")));
        log.info(" Antes de copiar el WAR borrar el " + warName + ".dodeploy si es que existe. Eliminado: " + Files.deleteIfExists(Paths.get(wildflyDeployDir, warName + ".dodeploy")));

        Path destino = Paths.get(wildflyDeployDir, latestWar.getName());
        log.info(" Copiar al destino: " + destino);
        Files.copy(latestWar.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);

        log.info(" Crear .dodeploy.");
        Files.createFile(Paths.get(wildflyDeployDir, latestWar.getName() + ".dodeploy"));

        log.info(" WAR copiado exitosamente en WildFly.");

        esperarDespliegue();

        return true;
    }

    private void esperarDespliegue() throws InterruptedException {
        log.info(" ");
        log.info(" ");
        log.info(" ======================================== ");
        log.info(" Esperando el deploy a wildfly");
        log.info(" Deployments directorio: " + wildflyDeployDir);
        log.info(" Nombre war: " + warName);
        log.info(" ======================================== ");

        Path deployed = Paths.get(wildflyDeployDir, warName + ".deployed");
        Path failed = Paths.get(wildflyDeployDir, warName + ".failed");

        log.info(" Esperando a que WildFly despliegue: " + warName);

        int intentos = 0;
        int timeout = 60;
        while (intentos < timeout) { // Esperar hasta 60 segundos
            if (Files.exists(deployed)) {
                log.info(" Despliegue exitoso detectado: " + deployed.getFileName());
                return;
            }
            if (Files.exists(failed)) {
                System.err.println(" Despliegue fallido detectado: " + failed.getFileName());
                return;
            }

            log.info(" Esperando " + intentos + " Segundos...");
            Thread.sleep(1000); // Espera 1 segundo antes del próximo chequeo
            intentos++;
        }

        log.info(" Timeout: WildFly no terminó el despliegue en " + timeout + " segundos.");
    }
}
