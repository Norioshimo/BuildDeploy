package nsg.portafolio.service;

import static org.junit.Assert.assertTrue;
import nsg.portafolio.enums.AppServer;
import nsg.portafolio.enums.BuildTool;
import nsg.portafolio.enums.DeployMode;
import nsg.portafolio.model.Configuracion;
import nsg.portafolio.service.impl.AntBuildStrategy;
import nsg.portafolio.service.impl.GlassFishAsadminStrategy;
import nsg.portafolio.service.impl.GlassFishAutodeployStrategy;
import nsg.portafolio.service.impl.GlassFishLifecycleStrategy;
import nsg.portafolio.service.impl.MavenBuildStrategy;
import nsg.portafolio.service.impl.WildFlyDeployStrategy;
import nsg.portafolio.service.impl.WildFlyLifecycleStrategy;
import org.junit.Test;

public class EstrategiaFactoryTest {

    @Test
    public void mavenWildfly() {
        Configuracion conf = Configuracion.builder()
                .herramientaBuild(BuildTool.MAVEN)
                .servidor(AppServer.WILDFLY)
                .build();

        assertTrue(EstrategiaFactory.build(conf) instanceof MavenBuildStrategy);
        assertTrue(EstrategiaFactory.deploy(conf) instanceof WildFlyDeployStrategy);
        assertTrue(EstrategiaFactory.lifecycle(conf) instanceof WildFlyLifecycleStrategy);
    }

    @Test
    public void antGlassFishAutodeploy() {
        Configuracion conf = Configuracion.builder()
                .herramientaBuild(BuildTool.ANT)
                .servidor(AppServer.GLASSFISH)
                .deployMode(DeployMode.AUTODEPLOY)
                .build();

        assertTrue(EstrategiaFactory.build(conf) instanceof AntBuildStrategy);
        assertTrue(EstrategiaFactory.deploy(conf) instanceof GlassFishAutodeployStrategy);
        assertTrue(EstrategiaFactory.lifecycle(conf) instanceof GlassFishLifecycleStrategy);
    }

    @Test
    public void glassFishAsadmin() {
        Configuracion conf = Configuracion.builder()
                .herramientaBuild(BuildTool.MAVEN)
                .servidor(AppServer.GLASSFISH)
                .deployMode(DeployMode.ASADMIN)
                .build();

        assertTrue(EstrategiaFactory.deploy(conf) instanceof GlassFishAsadminStrategy);
    }
}
