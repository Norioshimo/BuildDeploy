package nsg.portafolio.enums;

public enum BuildTool {
    MAVEN("Maven"),
    ANT("Ant");

    private final String etiqueta;

    BuildTool(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    public static BuildTool desde(String valor) {
        if (valor == null) {
            return MAVEN;
        }
        for (BuildTool tool : values()) {
            if (tool.name().equalsIgnoreCase(valor)) {
                return tool;
            }
        }
        return MAVEN;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
