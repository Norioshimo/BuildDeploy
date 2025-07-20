package nsg.portafolio.model;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Configuracion {

    private Integer configuracion_id;
    private String nombre_proyecto;
    private String pomDir;
    private String buildDir;
    private String warName;
    private String wildflyDeployDir;
    private String mavenExecutable;

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
