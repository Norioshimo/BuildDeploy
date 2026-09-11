package nsg.portafolio.formulario;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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

    private static final int ANCHO_UNA_COLUMNA = 660;

    private final TarjetaBoton[] tarjetas = new TarjetaBoton[4];
    private JPanel grid;
    private int columnasActuales = -1;

    public PrincipalFrm() {
        super("Build & Deploy");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initUI();
        adaptarPantalla(new Dimension(600, 440));
    }

    private void initUI() {
        JPanel raiz = raiz();
        raiz.add(encabezado("Build & Deploy",
                "Automatiza la compilacion y el despliegue de tus aplicaciones web"),
                BorderLayout.NORTH);

        tarjetas[0] = new TarjetaBoton(Iconos.config(UITheme.PRIMARY), "Configuracion",
                "Administra proyectos, herramientas y servidores",
                () -> new ConfiguracionFrm().setVisible(true));
        tarjetas[1] = new TarjetaBoton(Iconos.run(UITheme.SUCCESS), "Ejecutar",
                "Compila y despliega el WAR seleccionado",
                () -> new EjecutarFrm().setVisible(true));
        tarjetas[2] = new TarjetaBoton(Iconos.logs(UITheme.WARNING), "Ver Logs",
                "Revisa el log de la aplicacion y del servidor",
                () -> new LogFrm().setVisible(true));
        tarjetas[3] = new TarjetaBoton(Iconos.help(UITheme.PRIMARY), "Ayuda",
                "Ejemplos de configuracion para WildFly, GlassFish y Ant",
                () -> new AyudaFrm().setVisible(true));

        grid = new JPanel(new GridLayout(2, 2, 14, 14));
        grid.setOpaque(false);
        for (TarjetaBoton tarjeta : tarjetas) {
            grid.add(tarjeta);
        }
        columnasActuales = 2;
        raiz.add(grid, BorderLayout.CENTER);
        raiz.add(pie(), BorderLayout.SOUTH);

        setContentPane(raiz);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent evt) {
                reacomodarGrid();
            }
        });
    }

    private void reacomodarGrid() {
        int columnas = getWidth() < ANCHO_UNA_COLUMNA ? 1 : 2;
        if (columnas == columnasActuales) {
            return;
        }
        columnasActuales = columnas;
        int filas = (int) Math.ceil(tarjetas.length / (double) columnas);
        grid.removeAll();
        grid.setLayout(new GridLayout(filas, columnas, 14, 14));
        for (TarjetaBoton tarjeta : tarjetas) {
            grid.add(tarjeta);
        }
        grid.revalidate();
        grid.repaint();
    }

    public static void main(String[] args) {
        App.main(args);
    }
}
