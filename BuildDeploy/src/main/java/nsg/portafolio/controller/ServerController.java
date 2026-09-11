package nsg.portafolio.controller;

import nsg.portafolio.model.Configuracion;
import nsg.portafolio.service.EstrategiaFactory;
import nsg.portafolio.service.ServerLifecycleStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Controla el ciclo de vida del servidor de aplicaciones desde la UI.
 */
public class ServerController {

    private static final Logger log = LogManager.getLogger(ServerController.class);

    private final Configuracion conf;
    private final ServerLifecycleStrategy lifecycle;

    public ServerController(Configuracion conf) {
        this.conf = conf;
        this.lifecycle = EstrategiaFactory.lifecycle(conf);
    }

    public void iniciar() throws Exception {
        log.info(" Iniciando servidor: " + lifecycle.descripcion());
        lifecycle.iniciar();
    }

    public void detener() throws Exception {
        log.info(" Deteniendo servidor: " + lifecycle.descripcion());
        lifecycle.detener();
    }

    public void reiniciar() throws Exception {
        log.info(" Reiniciando servidor: " + lifecycle.descripcion());
        lifecycle.reiniciar();
    }

    public boolean estaCorriendo() {
        return lifecycle.estaCorriendo();
    }

    public int puertoHttp() {
        return lifecycle.puertoHttp();
    }

    public String descripcion() {
        return lifecycle.descripcion();
    }
}
