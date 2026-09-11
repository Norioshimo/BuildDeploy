package nsg.portafolio.service;

/**
 * Estrategia de ciclo de vida del servidor de aplicaciones.
 */
public interface ServerLifecycleStrategy {

    void iniciar() throws Exception;

    void detener() throws Exception;

    void reiniciar() throws Exception;

    boolean estaCorriendo();

    /**
     * Puerto HTTP en el que quedo escuchando el servidor, o -1 si no se pudo
     * detectar.
     */
    default int puertoHttp() {
        return -1;
    }

    String descripcion();
}
