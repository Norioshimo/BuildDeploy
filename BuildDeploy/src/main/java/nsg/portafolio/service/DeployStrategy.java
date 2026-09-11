package nsg.portafolio.service;

/**
 * Estrategia de despliegue del WAR (WildFly, GlassFish autodeploy, GlassFish asadmin).
 */
public interface DeployStrategy {

    boolean procesar() throws Exception;

    String descripcion();
}
