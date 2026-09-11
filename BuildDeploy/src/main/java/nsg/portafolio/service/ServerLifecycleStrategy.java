package nsg.portafolio.service;

/**
 * Estrategia de ciclo de vida del servidor de aplicaciones.
 */
public interface ServerLifecycleStrategy {

    void iniciar() throws Exception;

    void detener() throws Exception;

    void reiniciar() throws Exception;

    boolean estaCorriendo();

    String descripcion();
}
