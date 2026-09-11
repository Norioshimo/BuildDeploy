package nsg.portafolio.service.impl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import nsg.portafolio.service.ServerLifecycleStrategy;
import nsg.portafolio.utiles.ProcesoUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WildFlyLifecycleStrategy implements ServerLifecycleStrategy {

    private static final Logger log = LogManager.getLogger(WildFlyLifecycleStrategy.class);

    private static final int PUERTO_ADMIN = 9990;

    private final String serverHome;

    public WildFlyLifecycleStrategy(String serverHome) {
        this.serverHome = serverHome;
    }

    @Override
    public void iniciar() throws Exception {
        Path script = script("standalone");
        if (!Files.exists(script)) {
            throw new Exception("No se encontro el script de arranque de WildFly: " + script);
        }

        File salida = new File("logs", "wildfly-consola.log");
        log.info(" Iniciando WildFly desde: " + script);
        ProcesoUtil.ejecutarEnSegundoPlano(Arrays.asList(script.toString()), script.getParent().toFile(), salida, log);
        log.info(" WildFly lanzado. Puede tardar unos segundos en estar disponible.");
    }

    @Override
    public void detener() throws Exception {
        Path cli = script("jboss-cli");
        if (!Files.exists(cli)) {
            throw new Exception("No se encontro jboss-cli en: " + cli);
        }

        log.info(" Deteniendo WildFly con jboss-cli...");
        int exit = ProcesoUtil.ejecutarYEsperar(
                Arrays.asList(cli.toString(), "--connect", "--command=:shutdown"),
                cli.getParent().toFile(), log);
        if (exit != 0) {
            log.warn(" jboss-cli termino con codigo " + exit + ". WildFly podria no estar en ejecucion.");
        }
    }

    @Override
    public void reiniciar() throws Exception {
        log.info(" Reiniciando WildFly...");
        try {
            detener();
        } catch (Exception ex) {
            log.warn(" No se pudo detener WildFly: " + ex.getMessage());
        }
        Thread.sleep(3000);
        iniciar();
    }

    @Override
    public boolean estaCorriendo() {
        return ProcesoUtil.puertoAbierto("localhost", PUERTO_ADMIN)
                || ProcesoUtil.puertoAbierto("localhost", 8080);
    }

    @Override
    public String descripcion() {
        return "WildFly (" + serverHome + ")";
    }

    private Path script(String nombre) {
        String extension = ProcesoUtil.esWindows() ? ".bat" : ".sh";
        return Paths.get(serverHome, "bin", nombre + extension);
    }
}
