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

public class AntBuildStrategy implements BuildStrategy {

    private static final Logger log = LogManager.getLogger(AntBuildStrategy.class);

    private final String antExecutable;
    private final String antBuildFile;
    private final String antTarget;
    private final String directorioBase;

    public AntBuildStrategy(String antExecutable, String antBuildFile, String antTarget, String directorioBase) {
        this.antExecutable = antExecutable;
        this.antBuildFile = antBuildFile;
        this.antTarget = antTarget;
        this.directorioBase = directorioBase;
    }

    @Override
    public boolean procesar() throws Exception {
        log.info(" ======================================== ");
        log.info(" Compilando el proyecto con Ant... ");
        log.info(" ======================================== ");

        String ant = (antExecutable == null || antExecutable.trim().isEmpty()) ? "ant" : antExecutable;

        Path buildFile = resolverBuildFile();
        if (buildFile == null || !Files.exists(buildFile)) {
            throw new Exception("No se encontro el archivo build.xml de Ant: " + buildFile);
        }

        List<String> comando = new ArrayList<>();
        comando.add(ant);
        comando.add("-f");
        comando.add(buildFile.toString());
        if (antTarget != null && !antTarget.trim().isEmpty()) {
            comando.add(antTarget.trim());
        }

        File trabajo = (directorioBase != null && !directorioBase.trim().isEmpty())
                ? new File(directorioBase)
                : buildFile.getParent().toFile();

        int exitCode = ProcesoUtil.ejecutarYEsperar(comando, trabajo, log);
        if (exitCode != 0) {
            log.error(" Error al compilar el proyecto Ant. Codigo de salida: " + exitCode);
            throw new Exception("Error al compilar con Ant. Revise el log. Codigo: " + exitCode);
        }

        return true;
    }

    private Path resolverBuildFile() {
        if (antBuildFile != null && !antBuildFile.trim().isEmpty()) {
            Path ruta = Paths.get(antBuildFile.trim());
            if (ruta.isAbsolute() || Files.exists(ruta)) {
                return ruta;
            }
            if (directorioBase != null && !directorioBase.trim().isEmpty()) {
                return Paths.get(directorioBase, antBuildFile.trim());
            }
            return ruta;
        }
        if (directorioBase != null && !directorioBase.trim().isEmpty()) {
            return Paths.get(directorioBase, "build.xml");
        }
        return null;
    }

    @Override
    public String descripcion() {
        return "Ant (" + (antTarget == null || antTarget.trim().isEmpty() ? "target por defecto" : antTarget) + ")";
    }
}
