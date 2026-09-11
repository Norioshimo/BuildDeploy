package nsg.portafolio.ui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.RandomAccessFile;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import nsg.portafolio.dao.ConfiguracionDAO;
import nsg.portafolio.model.Configuracion;
import nsg.portafolio.utiles.LogsUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Visor de logs: permite leer el log de la aplicacion, el log interno del
 * servidor (WildFly/GlassFish) y la consola de arranque.
 */
public class LogFrm extends BaseFrm {

    private static final Logger log = LogManager.getLogger(LogFrm.class);
    private static final int MAX_LINEAS = 2000;

    private enum Fuente {
        APLICACION("Aplicacion (logs/app.log)"),
        SERVIDOR("Servidor (server.log)"),
        ARRANQUE("Consola de arranque (wildfly-consola.log)");

        private final String etiqueta;

        Fuente(String etiqueta) {
            this.etiqueta = etiqueta;
        }

        @Override
        public String toString() {
            return etiqueta;
        }
    }

    private JComboBox<Fuente> cbFuente;
    private JComboBox<Configuracion> cbConfiguracion;
    private JTextArea area;
    private JCheckBox chkAuto;
    private Timer timer;

    public LogFrm() {
        super("Logs");
        initUI();
        cargarConfiguraciones();
        pantallaCompleta(new Dimension(660, 460));
        refrescar();
    }

    private void initUI() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel raiz = raiz();
        raiz.add(encabezado("Logs", "Consulte el log de la aplicacion o del servidor en tiempo real"), BorderLayout.NORTH);

        JPanel controles = tarjeta();
        controles.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 4));
        controles.add(new JLabel("Fuente:"));
        cbFuente = new JComboBox<>(Fuente.values());
        cbFuente.addActionListener(evt -> refrescar());
        controles.add(cbFuente);

        controles.add(new JLabel("Configuracion:"));
        cbConfiguracion = new JComboBox<>();
        cbConfiguracion.setPrototypeDisplayValue(Configuracion.builder().nombre_proyecto("Configuracion de ejemplo larga").build());
        cbConfiguracion.addActionListener(evt -> refrescar());
        controles.add(cbConfiguracion);

        chkAuto = new JCheckBox("Auto-refresh");
        chkAuto.addActionListener(evt -> actualizarTimer());
        controles.add(chkAuto);

        BotonPlano btnRefrescar = new BotonPlano("Refrescar", BotonPlano.Tipo.SECUNDARIO, Iconos.restart(UITheme.PRIMARY));
        btnRefrescar.addActionListener(evt -> refrescar());
        controles.add(btnRefrescar);

        BotonPlano btnCarpeta = new BotonPlano("Abrir carpeta", BotonPlano.Tipo.NEUTRO, Iconos.logs(UITheme.MUTED));
        btnCarpeta.addActionListener(evt -> abrirCarpeta());
        controles.add(btnCarpeta);

        BotonPlano btnLimpiar = new BotonPlano("Limpiar vista", BotonPlano.Tipo.NEUTRO, Iconos.delete(UITheme.MUTED));
        btnLimpiar.addActionListener(evt -> area.setText(""));
        controles.add(btnLimpiar);

        JPanel centro = new JPanel(new BorderLayout(0, 10));
        centro.setOpaque(false);
        centro.add(controles, BorderLayout.NORTH);

        area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 12));
        area.setRows(18);
        area.setColumns(80);
        area.setBackground(UITheme.SURFACE);
        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(UITheme.BORDER, 14, 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        centro.add(scroll, BorderLayout.CENTER);

        raiz.add(centro, BorderLayout.CENTER);

        JPanel sur = new JPanel(new BorderLayout());
        sur.setOpaque(false);
        BotonPlano btnCerrar = new BotonPlano("Cerrar", BotonPlano.Tipo.SECUNDARIO);
        btnCerrar.addActionListener(evt -> dispose());
        JPanel botones = new JPanel();
        botones.setOpaque(false);
        botones.add(btnCerrar);
        sur.add(botones, BorderLayout.CENTER);
        sur.add(pie(), BorderLayout.SOUTH);
        raiz.add(sur, BorderLayout.SOUTH);

        setContentPane(raiz);
    }

    private void cargarConfiguraciones() {
        try {
            List<Configuracion> lista = new ConfiguracionDAO().listarTodas();
            cbConfiguracion.removeAllItems();
            for (Configuracion item : lista) {
                cbConfiguracion.addItem(item);
            }
            if (lista.isEmpty()) {
                cbConfiguracion.addItem(Configuracion.builder().nombre_proyecto("(sin configuraciones)").build());
            }
        } catch (Exception ex) {
            log.error("Error al cargar configuraciones para logs: " + ex.getMessage(), ex);
        }
    }

    private void actualizarTimer() {
        if (chkAuto.isSelected()) {
            if (timer == null) {
                timer = new Timer(2000, evt -> refrescar());
            }
            timer.start();
        } else if (timer != null) {
            timer.stop();
        }
    }

    private void refrescar() {
        Fuente fuente = (Fuente) cbFuente.getSelectedItem();
        if (fuente == null) {
            return;
        }
        File archivo = resolverArchivo(fuente);
        try {
            String contenido = leerUltimas(archivo, MAX_LINEAS);
            area.setText(contenido);
            area.setCaretPosition(area.getDocument().getLength());
        } catch (Exception ex) {
            area.setText("Error al leer el archivo: " + ex.getMessage());
        }
    }

    private File resolverArchivo(Fuente fuente) {
        switch (fuente) {
            case ARRANQUE:
                return LogsUtil.archivoConsolaArranque();
            case SERVIDOR:
                return LogsUtil.archivoLogServidor((Configuracion) cbConfiguracion.getSelectedItem());
            default:
                return LogsUtil.archivoLogAplicacion();
        }
    }

    private String leerUltimas(File archivo, int maxLineas) throws Exception {
        if (!archivo.exists()) {
            return "(No existe el archivo: " + archivo.getAbsolutePath() + ")";
        }
        try (RandomAccessFile raf = new RandomAccessFile(archivo, "r")) {
            long longitud = raf.length();
            if (longitud == 0) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            long pos = longitud - 1;
            int lineas = 0;
            while (pos >= 0 && lineas < maxLineas) {
                raf.seek(pos);
                int b = raf.read();
                if (b == '\n') {
                    lineas++;
                }
                sb.append((char) b);
                pos--;
            }
            return sb.reverse().toString();
        }
    }

    private void abrirCarpeta() {
        try {
            File archivo = resolverArchivo((Fuente) cbFuente.getSelectedItem());
            File carpeta = archivo.getParentFile();
            if (carpeta == null || !carpeta.exists()) {
                JOptionPane.showMessageDialog(this, "La carpeta no existe: " + carpeta, "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Desktop.getDesktop().open(carpeta);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "No se pudo abrir la carpeta: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        UITheme.aplicar();
        TextAreaAppender.registrar();
        javax.swing.SwingUtilities.invokeLater(() -> new LogFrm().setVisible(true));
    }
}
