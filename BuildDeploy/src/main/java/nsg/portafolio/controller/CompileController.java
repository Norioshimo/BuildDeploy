package nsg.portafolio.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CompileController {

    private static final Logger log = LogManager.getLogger(CompileController.class);

    private String pomDir = null;
    private String mavenExecutable = null;

    public CompileController(String pomDir, String mavenExecutable) {
        this.pomDir = pomDir;
        this.mavenExecutable = mavenExecutable;
    }

    public boolean procesar() throws Exception {
        log.info(" ======================================== ");
        log.info(" Compilando el proyecto con Maven... ");
        log.info(" ======================================== ");

        // Ejecutar mvn clean install
        ProcessBuilder builder = new ProcessBuilder(mavenExecutable, "-f", pomDir + "\\pom.xml", "clean", "install");
        builder.redirectErrorStream(true);
        Process process = builder.start();

        // Leer la salida del proceso
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log.info(line);
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            log.info(" ======================================== ");
            log.info(" Error al compilar el proyecto Maven. Codigo de salida: " + exitCode);
            log.info(" ======================================== ");
            throw new Exception("Error al compilar. Revise el log. ");
        }

        return true;
    }

}
