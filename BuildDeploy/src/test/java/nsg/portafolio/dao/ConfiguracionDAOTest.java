package nsg.portafolio.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import nsg.portafolio.db.ConfigDB;
import nsg.portafolio.enums.AppServer;
import nsg.portafolio.enums.BuildTool;
import nsg.portafolio.enums.DeployMode;
import nsg.portafolio.model.Configuracion;
import org.junit.BeforeClass;
import org.junit.Test;

public class ConfiguracionDAOTest {

    @BeforeClass
    public static void inicializarBase() throws Exception {
        ConfigDB.inicializar();
    }

    @Test
    public void crudCompleto() throws Exception {
        ConfiguracionDAO dao = new ConfiguracionDAO();

        Configuracion conf = Configuracion.builder()
                .nombre_proyecto("TEST-JUNIT")
                .herramientaBuild(BuildTool.ANT)
                .antBuildFile("build.xml")
                .antTarget("dist")
                .buildDir("dist")
                .warName("test.war")
                .servidor(AppServer.GLASSFISH)
                .glassfishDeployDir("autodeploy")
                .deployMode(DeployMode.ASADMIN)
                .asadminPath("asadmin")
                .gfHost("localhost")
                .gfPort("4848")
                .gfUser("admin")
                .gfPassword("secret")
                .serverHome("glassfish")
                .domainName("domain1")
                .detenerAntesDeploy(true)
                .reiniciarDespuesDeploy(false)
                .build();

        dao.guardar(conf);
        assertNotNull(conf.getConfiguracion_id());

        Configuracion leido = dao.buscarPorId(conf.getConfiguracion_id());
        assertNotNull(leido);
        assertEquals("TEST-JUNIT", leido.getNombre_proyecto());
        assertEquals(BuildTool.ANT, leido.getHerramientaBuild());
        assertEquals(AppServer.GLASSFISH, leido.getServidor());
        assertEquals(DeployMode.ASADMIN, leido.getDeployMode());
        assertTrue(leido.isDetenerAntesDeploy());
        assertFalse(leido.isReiniciarDespuesDeploy());
        assertEquals("domain1", leido.getDomainName());

        leido.setNombre_proyecto("TEST-JUNIT-EDITADO");
        dao.guardar(leido);
        assertEquals("TEST-JUNIT-EDITADO", dao.buscarPorId(conf.getConfiguracion_id()).getNombre_proyecto());

        dao.eliminar(conf.getConfiguracion_id());
        assertNull(dao.buscarPorId(conf.getConfiguracion_id()));
    }
}
