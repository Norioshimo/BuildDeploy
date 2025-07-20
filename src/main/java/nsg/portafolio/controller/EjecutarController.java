package nsg.portafolio.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EjecutarController {

    private static final Logger log = LogManager.getLogger(EjecutarController.class);

    private String pomDir = "";
    private String buildDir = "";
    private String warName = "";
    private String wildflyDeployDir = "";
    private String mavenExecutable = "";

    public EjecutarController(String pomDir, String buildDir, String warName, String wildflyDeployDir, String mavenExecutable) {
        this.pomDir = pomDir;
        this.buildDir = buildDir;
        this.warName = warName;
        this.wildflyDeployDir = wildflyDeployDir;
        this.mavenExecutable = mavenExecutable;
    }

    public void procesar() {

        try {
            log.info(" ======================================== ");
            log.info(" Parametros configurados... ");
            log.info(" Directorio del pom.xml: " + pomDir);
            log.info(" Directorio de compilacion: " + buildDir);
            log.info(" Directorio de war: " + warName);
            log.info(" Directorio de Wildfly: " + wildflyDeployDir);
            log.info(" Directorio de Maven: " + mavenExecutable);
            log.info(" ======================================== ");
            log.info(" ");
            log.info(" ");

            CompileController compile = new CompileController(pomDir, mavenExecutable);

            if (!compile.procesar()) {// Si hubo error cortar
                return;
            }

            DeployController deploy = new DeployController(buildDir, warName, wildflyDeployDir);

            if (!deploy.procesar()) {// Si hubo error cortar
                return;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
