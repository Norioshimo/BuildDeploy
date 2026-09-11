package nsg.portafolio.controller;

import nsg.portafolio.model.Configuracion;
import nsg.portafolio.service.BuildStrategy;
import nsg.portafolio.service.DeployStrategy;
import nsg.portafolio.service.EstrategiaFactory;
import nsg.portafolio.service.ServerLifecycleStrategy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EjecutarController {

    private static final Logger log = LogManager.getLogger(EjecutarController.class);

    private final Configuracion conf;

    public EjecutarController(Configuracion conf) {
        this.conf = conf;
    }

    public void procesar() throws Exception {
        log.info(" ======================================== ");
        log.info(" Parametros configurados... ");
        log.info(" Proyecto: " + conf.getNombre_proyecto());
        log.info(" Herramienta de build: " + conf.getHerramientaBuild());
        log.info(" Servidor: " + conf.getServidor());
        log.info(" ======================================== ");

        ServerLifecycleStrategy lifecycle = EstrategiaFactory.lifecycle(conf);
        boolean estabaCorriendo = lifecycle.estaCorriendo();
        log.info(" Estado inicial del servidor: " + (estabaCorriendo ? "EN EJECUCION" : "DETENIDO"));

        try {
            if (conf.isDetenerAntesDeploy() && estabaCorriendo) {
                log.info(" Deteniendo el servidor antes del despliegue...");
                lifecycle.detener();
            }

            BuildStrategy build = EstrategiaFactory.build(conf);
            if (!build.procesar()) {
                throw new Exception("La compilacion no se completo correctamente.");
            }

            if (!lifecycle.estaCorriendo()) {
                log.info(" El servidor no esta en ejecucion. Iniciandolo antes del despliegue...");
                lifecycle.iniciar();
                esperarArranque(lifecycle);
            }

            DeployStrategy deploy = EstrategiaFactory.deploy(conf);
            if (!deploy.procesar()) {
                throw new Exception("El despliegue no se completo correctamente.");
            }

            if (conf.isReiniciarDespuesDeploy()) {
                log.info(" Reiniciando el servidor despues del despliegue...");
                lifecycle.reiniciar();
            }
        } catch (Exception ex) {
            log.error(" Error en el proceso build & deploy: " + ex.getMessage(), ex);
            throw ex;
        }

        log.info(" Proceso build & deploy finalizado.");
    }

    private void esperarArranque(ServerLifecycleStrategy lifecycle) throws InterruptedException {
        int intentos = 0;
        int timeout = 120;
        while (intentos < timeout) {
            if (lifecycle.estaCorriendo()) {
                log.info(" Servidor disponible tras " + intentos + " segundos.");
                return;
            }
            Thread.sleep(1000);
            intentos++;
        }
        log.warn(" El servidor no respondio despues de " + timeout + " segundos; se continuara con el despliegue.");
    }

}
