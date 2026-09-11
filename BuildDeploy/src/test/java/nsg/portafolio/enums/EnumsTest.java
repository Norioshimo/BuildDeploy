package nsg.portafolio.enums;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class EnumsTest {

    @Test
    public void buildToolDesdeValores() {
        assertEquals(BuildTool.MAVEN, BuildTool.desde(null));
        assertEquals(BuildTool.MAVEN, BuildTool.desde("desconocido"));
        assertEquals(BuildTool.ANT, BuildTool.desde("ant"));
        assertEquals(BuildTool.MAVEN, BuildTool.desde("MAVEN"));
    }

    @Test
    public void appServerDesdeValores() {
        assertEquals(AppServer.WILDFLY, AppServer.desde(null));
        assertEquals(AppServer.GLASSFISH, AppServer.desde("glassfish"));
        assertEquals(AppServer.WILDFLY, AppServer.desde("otro"));
    }

    @Test
    public void deployModeDesdeValores() {
        assertEquals(DeployMode.AUTODEPLOY, DeployMode.desde(null));
        assertEquals(DeployMode.ASADMIN, DeployMode.desde("asadmin"));
        assertEquals(DeployMode.AUTODEPLOY, DeployMode.desde("otro"));
    }
}
