package nsg.portafolio.service.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import nsg.portafolio.service.ServerLifecycleStrategy;
import nsg.portafolio.utiles.ProcesoUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GlassFishLifecycleStrategy implements ServerLifecycleStrategy {

    private static final Logger log = LogManager.getLogger(GlassFishLifecycleStrategy.class);

    private final String serverHome;
    private final String domainName;

    public GlassFishLifecycleStrategy(String serverHome, String domainName) {
        this.serverHome = serverHome;
        this.domainName = (domainName == null || domainName.trim().isEmpty()) ? "domain1" : domainName.trim();
    }

    @Override
    public void iniciar() throws Exception {
        Path asadmin = script();
        if (!Files.exists(asadmin)) {
            throw new Exception("No se encontro asadmin en: " + asadmin);
        }

        log.info(" Iniciando dominio GlassFish '" + domainName + "'...");
        int exit = ProcesoUtil.ejecutarYEsperar(
                Arrays.asList(asadmin.toString(), "start-domain", domainName),
                asadmin.getParent().toFile(), log);
        if (exit != 0) {
            throw new Exception("No se pudo iniciar el dominio GlassFish '" + domainName + "'.");
        }
    }

    @Override
    public void detener() throws Exception {
        Path asadmin = script();
        if (!Files.exists(asadmin)) {
            throw new Exception("No se encontro asadmin en: " + asadmin);
        }

        log.info(" Deteniendo dominio GlassFish '" + domainName + "'...");
        int exit = ProcesoUtil.ejecutarYEsperar(
                Arrays.asList(asadmin.toString(), "stop-domain", domainName),
                asadmin.getParent().toFile(), log);
        if (exit != 0) {
            log.warn(" asadmin stop-domain termino con codigo " + exit + ".");
        }
    }

    @Override
    public void reiniciar() throws Exception {
        log.info(" Reiniciando dominio GlassFish '" + domainName + "'...");
        try {
            detener();
        } catch (Exception ex) {
            log.warn(" No se pudo detener GlassFish: " + ex.getMessage());
        }
        Thread.sleep(3000);
        iniciar();
    }

    @Override
    public boolean estaCorriendo() {
        try {
            Path asadmin = script();
            if (!Files.exists(asadmin)) {
                return false;
            }
            String salida = ProcesoUtil.capturarSalida(
                    Arrays.asList(asadmin.toString(), "list-domains"), asadmin.getParent().toFile());
            for (String linea : salida.split("\\R")) {
                String normalizada = linea.toLowerCase();
                if (normalizada.contains(domainName.toLowerCase()) && normalizada.contains("running")) {
                    return true;
                }
            }
        } catch (Exception ex) {
            log.warn(" No se pudo consultar el estado de GlassFish: " + ex.getMessage());
        }
        return false;
    }

    @Override
    public String descripcion() {
        return "GlassFish (" + serverHome + ", dominio " + domainName + ")";
    }

    private Path script() {
        String extension = ProcesoUtil.esWindows() ? ".bat" : ".sh";
        return Paths.get(serverHome, "bin", "asadmin" + extension);
    }
}
