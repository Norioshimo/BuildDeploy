package nsg.portafolio.service;

/**
 * Estrategia de compilacion del WAR (Maven o Ant).
 */
public interface BuildStrategy {

    boolean procesar() throws Exception;

    String descripcion();
}
