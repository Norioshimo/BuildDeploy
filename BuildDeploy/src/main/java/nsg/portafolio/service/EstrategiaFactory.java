package nsg.portafolio.service;

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

/**
 * Selecciona la estrategia de build, deploy y ciclo de vida segun la
 * configuracion del proyecto.
 */
public final class EstrategiaFactory {

    private EstrategiaFactory() {
    }

    public static BuildStrategy build(Configuracion conf) {
        BuildTool tool = (conf.getHerramientaBuild() == null) ? BuildTool.MAVEN : conf.getHerramientaBuild();
        if (tool == BuildTool.ANT) {
            return new AntBuildStrategy(conf.getAntExecutable(), conf.getAntBuildFile(),
                    conf.getAntTarget(), conf.getPomDir());
        }
        return new MavenBuildStrategy(conf.getPomDir(), conf.getMavenExecutable());
    }

    public static DeployStrategy deploy(Configuracion conf) {
        AppServer server = (conf.getServidor() == null) ? AppServer.WILDFLY : conf.getServidor();
        if (server == AppServer.GLASSFISH) {
            DeployMode mode = (conf.getDeployMode() == null) ? DeployMode.AUTODEPLOY : conf.getDeployMode();
            if (mode == DeployMode.ASADMIN) {
                return new GlassFishAsadminStrategy(conf.getBuildDir(), conf.getWarName(),
                        conf.getAsadminPath(), conf.getGfHost(), conf.getGfPort(),
                        conf.getGfUser(), conf.getGfPassword());
            }
            return new GlassFishAutodeployStrategy(conf.getBuildDir(), conf.getWarName(),
                    conf.getGlassfishDeployDir());
        }
        return new WildFlyDeployStrategy(conf.getBuildDir(), conf.getWarName(), conf.getWildflyDeployDir());
    }

    public static ServerLifecycleStrategy lifecycle(Configuracion conf) {
        AppServer server = (conf.getServidor() == null) ? AppServer.WILDFLY : conf.getServidor();
        if (server == AppServer.GLASSFISH) {
            return new GlassFishLifecycleStrategy(conf.getServerHome(), conf.getDomainName());
        }
        return new WildFlyLifecycleStrategy(conf.getServerHome());
    }
}
