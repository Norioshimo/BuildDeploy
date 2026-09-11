package nsg.portafolio.utiles;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class PuertoUtilTest {

    @Test
    public void wildflySocketBinding() {
        String xml = "<server><socket-binding-group><socket-binding name=\"http\" port=\"8080\"/>"
                + "<socket-binding name=\"https\" port=\"8443\"/></socket-binding-group></server>";
        assertEquals(8080, PuertoUtil.parsearPuertoWildfly(xml));
    }

    @Test
    public void wildflySocketBindingConExpresion() {
        String xml = "<socket-binding name=\"http\" port=\"${jboss.http.port:9090}\"/>";
        assertEquals(9090, PuertoUtil.parsearPuertoWildfly(xml));
    }

    @Test
    public void wildflyLog() {
        String log = "INFO [org.wildfly.extension.undertow] Undertow HTTP listener default listening on 127.0.0.1:8080";
        assertEquals(8080, PuertoUtil.parsearPuertoWildfly(log));
    }

    @Test
    public void glassfishNetworkListenerIgnoraAdmin() {
        String xml = "<network-listeners>"
                + "<network-listener name=\"admin-listener\" port=\"4848\" protocol=\"admin-listener\"/>"
                + "<network-listener name=\"http-listener-1\" port=\"8080\" protocol=\"http-listener\"/>"
                + "</network-listeners>";
        assertEquals(8080, PuertoUtil.parsearPuertoGlassFish(xml));
    }

    @Test
    public void glassfishSegundoListener() {
        String xml = "<network-listener name=\"http-listener-2\" port=\"8181\" protocol=\"http-listener\"/>";
        assertEquals(8181, PuertoUtil.parsearPuertoGlassFish(xml));
    }

    @Test
    public void glassfishLog() {
        String log = "[INFO] Network Listener http-listener-1 is now listening on port 8080";
        assertEquals(8080, PuertoUtil.parsearPuertoGlassFishLog(log));
    }

    @Test
    public void sinDatosRetornaMenosUno() {
        assertEquals(-1, PuertoUtil.parsearPuertoWildfly(null));
        assertEquals(-1, PuertoUtil.parsearPuertoGlassFish(null));
        assertEquals(-1, PuertoUtil.parsearPuertoGlassFishLog(null));
    }
}
