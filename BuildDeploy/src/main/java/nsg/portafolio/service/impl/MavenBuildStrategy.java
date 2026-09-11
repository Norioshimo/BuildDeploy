package nsg.portafolio.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import nsg.portafolio.service.BuildStrategy;
import nsg.portafolio.utiles.ProcesoUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MavenBuildStrategy implements BuildStrategy {

    private static final Logger log = LogManager.getLogger(MavenBuildStrategy.class);

    private final String pomDir;
    private final String mavenExecutable;

    public MavenBuildStrategy(String pomDir, String mavenExecutable) {
        this.pomDir = pomDir;
        this.mavenExecutable = mavenExecutable;
    }

    @Override
    public boolean procesar() throws Exception {
        log.info(" ======================================== ");
        log.info(" Compilando el proyecto con Maven... ");
        log.info(" ======================================== ");

        if (mavenExecutable == null || mavenExecutable.trim().isEmpty()) {
            throw new Exception("No se configuro la ruta del ejecutable de Maven.");
        }
        if (pomDir == null || pomDir.trim().isEmpty()) {
            throw new Exception("No se configuro el directorio del pom.xml.");
        }

        Path pomFile = Paths.get(pomDir, "pom.xml");
        if (!Files.exists(pomFile)) {
            throw new Exception("No existe el archivo pom.xml en: " + pomFile);
        }

        validarEjecutable(mavenExecutable);

        List<String> comando = new ArrayList<>();
        comando.add(mavenExecutable);
        comando.add("-f");
        comando.add(pomFile.toString());
        comando.add("clean");
        comando.add("install");

        int exitCode = ProcesoUtil.ejecutarYEsperar(comando, new File(pomDir), log);
        if (exitCode != 0) {
            log.error(" Error al compilar el proyecto Maven. Codigo de salida: " + exitCode);
            throw new Exception("Error al compilar con Maven. Revise el log. Codigo: " + exitCode);
        }

        return true;
    }

    private void validarEjecutable(String ruta) throws Exception {
        boolean esRuta = ruta.contains("/") || ruta.contains("\\");
        if (esRuta && !Files.exists(Paths.get(ruta))) {
            throw new Exception("No existe el ejecutable de Maven: " + ruta);
        }
    }

    @Override
    public String descripcion() {
        return "Maven (mvn clean install)";
    }
}
