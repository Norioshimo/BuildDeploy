package nsg.portafolio.formulario;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JPanel;
import nsg.portafolio.ui.App;
import nsg.portafolio.ui.BaseFrm;
import nsg.portafolio.ui.Iconos;
import nsg.portafolio.ui.LogFrm;
import nsg.portafolio.ui.TarjetaBoton;
import nsg.portafolio.ui.UITheme;

/**
 * Ventana principal: menu de opciones.
 */
public class PrincipalFrm extends BaseFrm {

    public PrincipalFrm() {
        super("Build & Deploy");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initUI();
        setLocationRelativeTo(null);
    }

    private void initUI() {
        JPanel raiz = raiz();
        raiz.add(encabezado("Build & Deploy",
                "Automatiza la compilacion y el despliegue de tus aplicaciones web"),
                BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 2, 14, 14));
        grid.setOpaque(false);
        grid.add(new TarjetaBoton(Iconos.config(UITheme.PRIMARY), "Configuracion",
                "Administra proyectos, herramientas y servidores",
                () -> new ConfiguracionFrm().setVisible(true)));
        grid.add(new TarjetaBoton(Iconos.run(UITheme.SUCCESS), "Ejecutar",
                "Compila y despliega el WAR seleccionado",
                () -> new EjecutarFrm().setVisible(true)));
        grid.add(new TarjetaBoton(Iconos.logs(UITheme.WARNING), "Ver Logs",
                "Revisa el log de la aplicacion y del servidor",
                () -> new LogFrm().setVisible(true)));
        grid.add(new TarjetaBoton(Iconos.help(UITheme.PRIMARY), "Ayuda",
                "Ejemplos de configuracion para WildFly, GlassFish y Ant",
                () -> new AyudaFrm().setVisible(true)));

        raiz.add(grid, BorderLayout.CENTER);
        raiz.add(pie(), BorderLayout.SOUTH);

        setContentPane(raiz);
        pack();
        setMinimumSize(new Dimension(660, 480));
    }

    public static void main(String[] args) {
        App.main(args);
    }
}
