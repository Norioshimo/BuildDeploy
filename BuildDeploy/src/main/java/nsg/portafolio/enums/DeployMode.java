package nsg.portafolio.enums;

public enum DeployMode {
    AUTODEPLOY("Autodeploy (carpeta)"),
    ASADMIN("asadmin (comando)");

    private final String etiqueta;

    DeployMode(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public static DeployMode desde(String valor) {
        if (valor == null) {
            return AUTODEPLOY;
        }
        for (DeployMode mode : values()) {
            if (mode.name().equalsIgnoreCase(valor)) {
                return mode;
            }
        }
        return AUTODEPLOY;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
