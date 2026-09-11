package nsg.portafolio.enums;

public enum AppServer {
    WILDFLY("WildFly"),
    GLASSFISH("GlassFish");

    private final String etiqueta;

    AppServer(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public static AppServer desde(String valor) {
        if (valor == null) {
            return WILDFLY;
        }
        for (AppServer server : values()) {
            if (server.name().equalsIgnoreCase(valor)) {
                return server;
            }
        }
        return WILDFLY;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
