package nsg.portafolio.model;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nsg.portafolio.enums.AppServer;
import nsg.portafolio.enums.BuildTool;
import nsg.portafolio.enums.DeployMode;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Configuracion {

    private Integer configuracion_id;
    private String nombre_proyecto;

    // Herramienta de compilacion
    private BuildTool herramientaBuild;
    private String pomDir;
    private String buildDir;
    private String warName;
    private String mavenExecutable;
    private String antExecutable;
    private String antBuildFile;
    private String antTarget;

    // Servidor de aplicaciones
    private AppServer servidor;
    private String wildflyDeployDir;
    private String glassfishDeployDir;
    private DeployMode deployMode;
    private String asadminPath;
    private String gfHost;
    private String gfPort;
    private String gfUser;
    private String gfPassword;
    private String serverHome;
    private String domainName;

    // Automatizacion del deploy
    private Boolean detenerAntesDeploy;
    private Boolean reiniciarDespuesDeploy;

    public boolean isDetenerAntesDeploy() {
        return Boolean.TRUE.equals(detenerAntesDeploy);
    }

    public boolean isReiniciarDespuesDeploy() {
        return Boolean.TRUE.equals(reiniciarDespuesDeploy);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.configuracion_id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Configuracion other = (Configuracion) obj;
        if (!Objects.equals(this.configuracion_id, other.configuracion_id)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return (configuracion_id == null ? "" : configuracion_id + "-) ") + nombre_proyecto;
    }

}
