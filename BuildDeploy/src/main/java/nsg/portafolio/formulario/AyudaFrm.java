package nsg.portafolio.formulario;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import nsg.portafolio.ui.BaseFrm;
import nsg.portafolio.ui.BotonPlano;
import nsg.portafolio.ui.UITheme;

/**
 * Pantalla de ayuda con ejemplos de configuracion.
 */
public class AyudaFrm extends BaseFrm {

    private static final String TEXTO = ""
            + "EJEMPLOS DE CONFIGURACION\n"
            + "============================================================\n\n"
            + "MAVEN + WILDFLY\n"
            + "------------------------------------------------------------\n"
            + "  Herramienta de build....... Maven\n"
            + "  Directorio de pom.xml...... C:\\proyecto\n"
            + "  Directorio de Maven........ C:\\maven\\bin\\mvn.cmd\n"
            + "  Directorio de compilacion.. C:\\proyecto\\target\n"
            + "  Nombre de WAR.............. mi-webapp.war\n"
            + "  Servidor................... WildFly\n"
            + "  Deployments de WildFly..... C:\\wildfly\\standalone\\deployments\n"
            + "  Carpeta del servidor....... C:\\wildfly\n\n"
            + "MAVEN + GLASSFISH (autodeploy)\n"
            + "------------------------------------------------------------\n"
            + "  Servidor................... GlassFish\n"
            + "  Carpeta autodeploy......... C:\\glassfish\\domains\\domain1\\autodeploy\n"
            + "  Modo de deploy............. Autodeploy (carpeta)\n"
            + "  Dominio GlassFish.......... domain1\n"
            + "  Carpeta del servidor....... C:\\glassfish\n\n"
            + "GLASSFISH (asadmin)\n"
            + "------------------------------------------------------------\n"
            + "  Modo de deploy............. asadmin (comando)\n"
            + "  Ruta de asadmin............ C:\\glassfish\\bin\\asadmin.bat\n"
            + "  Host admin / Puerto admin.. localhost / 4848\n"
            + "  Usuario / Password admin... admin / (tu password)\n\n"
            + "ANT\n"
            + "------------------------------------------------------------\n"
            + "  Herramienta de build....... Ant\n"
            + "  Directorio de Ant.......... C:\\ant\\bin\\ant.bat (vacio = PATH)\n"
            + "  Archivo build.xml.......... C:\\proyecto\\build.xml\n"
            + "  Target de Ant.............. dist\n"
            + "  Directorio de compilacion.. C:\\proyecto\\dist\n\n"
            + "LOGS\n"
            + "------------------------------------------------------------\n"
            + "  Aplicacion................. logs\\app.log\n"
            + "  Servidor (WildFly)......... <serverHome>\\standalone\\log\\server.log\n"
            + "  Servidor (GlassFish)....... <serverHome>\\domains\\<domain>\\logs\\server.log\n"
            + "  Consola de arranque........ logs\\wildfly-consola.log\n\n"
            + "BASE DE DATOS\n"
            + "------------------------------------------------------------\n"
            + "  SQLite en configdb\\build_deploy_config.db\n"
            + "  Si existe una base H2 previa, se migra automaticamente.\n";

    public AyudaFrm() {
        super("Ayuda");
        initUI();
        pantallaCompleta(new Dimension(640, 480));
    }

    private void initUI() {
        JPanel raiz = raiz();
        raiz.add(encabezado("Ayuda", "Ejemplos de configuracion para WildFly, GlassFish, Maven y Ant"), BorderLayout.NORTH);

        JTextArea area = new JTextArea(TEXTO);
        area.setEditable(false);
        area.setFont(UITheme.FONT_MONO);
        area.setColumns(72);
        area.setRows(24);
        area.setBackground(UITheme.SURFACE);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                new nsg.portafolio.ui.RoundedBorder(UITheme.BORDER, 14, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        raiz.add(scroll, BorderLayout.CENTER);

        JPanel sur = new JPanel(new BorderLayout());
        sur.setOpaque(false);
        JPanel botones = new JPanel();
        botones.setOpaque(false);
        BotonPlano btnCerrar = new BotonPlano("Cerrar", BotonPlano.Tipo.SECUNDARIO);
        btnCerrar.addActionListener(evt -> dispose());
        botones.add(btnCerrar);
        sur.add(botones, BorderLayout.CENTER);
        sur.add(pie(), BorderLayout.SOUTH);
        raiz.add(sur, BorderLayout.SOUTH);

        setContentPane(raiz);
    }

    public static void main(String[] args) {
        nsg.portafolio.ui.App.main(args);
    }
}
