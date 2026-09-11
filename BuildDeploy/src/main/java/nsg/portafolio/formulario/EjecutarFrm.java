package nsg.portafolio.formulario;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import nsg.portafolio.Utiles;
import nsg.portafolio.controller.EjecutarController;
import nsg.portafolio.controller.ServerController;
import nsg.portafolio.dao.ConfiguracionDAO;
import nsg.portafolio.enums.AppServer;
import nsg.portafolio.enums.BuildTool;
import nsg.portafolio.enums.DeployMode;
import nsg.portafolio.model.Configuracion;
import nsg.portafolio.ui.BaseFrm;
import nsg.portafolio.ui.BotonPlano;
import nsg.portafolio.ui.Iconos;
import nsg.portafolio.ui.TextAreaAppender;
import nsg.portafolio.ui.UITheme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Pantalla de ejecucion: resumen, control del servidor, consola en vivo y
 * lanzamiento del build & deploy.
 */
public class EjecutarFrm extends BaseFrm {

    private static final Logger log = LogManager.getLogger(EjecutarFrm.class);

    private JComboBox<Configuracion> cbConfiguraciones;
    private JTextField txtProyecto;
    private JTextField txtHerramienta;
    private JTextField txtServidor;
    private JTextField txtBuildDir;
    private JTextField txtWarName;
    private JTextField txtDestino;
    private JLabel lblEstadoServidor;
    private BotonPlano btnIniciar;
    private BotonPlano btnDetener;
    private BotonPlano btnReiniciar;
    private BotonPlano btnEstado;
    private BotonPlano btnProcesar;
    private BotonPlano btnCerrar;
    private JTextArea areaConsola;
    private JProgressBar progreso;

    private Configuracion configuracion;

    public EjecutarFrm() {
        super("Ejecutar");
        initUI();
        setLocationRelativeTo(null);
        actualizarLista();
    }

    private void initUI() {
        setMinimumSize(new Dimension(820, 640));

        JPanel raiz = raiz();
        raiz.add(encabezado("Ejecutar", "Compila y despliega el WAR en el servidor seleccionado"), BorderLayout.NORTH);

        JPanel centro = new JPanel(new BorderLayout(0, 12));
        centro.setOpaque(false);

        JPanel superior = new JPanel();
        superior.setOpaque(false);
        superior.setLayout(new javax.swing.BoxLayout(superior, javax.swing.BoxLayout.Y_AXIS));

        // Tarjeta resumen
        JPanel tarjetaResumen = seccion("Resumen");
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        cbConfiguraciones = new JComboBox<>();
        cbConfiguraciones.addItemListener(evt -> cargarSeleccion());
        addCampo(form, 0, "Configuracion:", cbConfiguraciones);
        txtProyecto = campoLectura();
        addCampo(form, 1, "Nombre Proyecto:", txtProyecto);
        txtHerramienta = campoLectura();
        addCampo(form, 2, "Herramienta de build:", txtHerramienta);
        txtServidor = campoLectura();
        addCampo(form, 3, "Servidor:", txtServidor);
        txtBuildDir = campoLectura();
        addCampo(form, 4, "Directorio de compilacion:", txtBuildDir);
        txtWarName = campoLectura();
        addCampo(form, 5, "Nombre de WAR:", txtWarName);
        txtDestino = campoLectura();
        addCampo(form, 6, "Destino del deploy:", txtDestino);
        tarjetaResumen.add(form, BorderLayout.CENTER);

        // Tarjeta servidor
        JPanel tarjetaServidor = seccion("Servidor");
        JPanel controles = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 8, 4));
        controles.setOpaque(false);
        lblEstadoServidor = new JLabel("ESTADO: DESCONOCIDO");
        lblEstadoServidor.setFont(UITheme.FONT_BOLD);
        controles.add(lblEstadoServidor);
        btnIniciar = new BotonPlano("Iniciar", BotonPlano.Tipo.EXITO, Iconos.play(java.awt.Color.WHITE));
        btnIniciar.addActionListener(evt -> accionServidor("iniciar"));
        btnDetener = new BotonPlano("Detener", BotonPlano.Tipo.PELIGRO, Iconos.stop(java.awt.Color.WHITE));
        btnDetener.addActionListener(evt -> accionServidor("detener"));
        btnReiniciar = new BotonPlano("Reiniciar", BotonPlano.Tipo.SECUNDARIO, Iconos.restart(UITheme.PRIMARY));
        btnReiniciar.addActionListener(evt -> accionServidor("reiniciar"));
        btnEstado = new BotonPlano("Estado", BotonPlano.Tipo.NEUTRO, Iconos.estado(UITheme.MUTED));
        btnEstado.addActionListener(evt -> accionServidor("estado"));
        controles.add(btnIniciar);
        controles.add(btnDetener);
        controles.add(btnReiniciar);
        controles.add(btnEstado);
        tarjetaServidor.add(controles, BorderLayout.CENTER);

        superior.add(tarjetaResumen);
        superior.add(javax.swing.Box.createVerticalStrut(12));
        superior.add(tarjetaServidor);

        // Tarjeta consola
        JPanel tarjetaConsola = seccion("Consola en vivo");
        areaConsola = new JTextArea();
        areaConsola.setEditable(false);
        areaConsola.setFont(UITheme.FONT_MONO);
        areaConsola.setBackground(UITheme.SURFACE);
        JScrollPane scroll = new JScrollPane(areaConsola);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER));
        tarjetaConsola.add(scroll, BorderLayout.CENTER);
        TextAreaAppender.setArea(areaConsola);

        centro.add(superior, BorderLayout.NORTH);
        centro.add(tarjetaConsola, BorderLayout.CENTER);
        raiz.add(centro, BorderLayout.CENTER);

        // Sur
        JPanel sur = new JPanel(new BorderLayout(0, 6));
        sur.setOpaque(false);
        progreso = new JProgressBar();
        progreso.setIndeterminate(true);
        progreso.setVisible(false);
        progreso.setPreferredSize(new Dimension(100, 6));
        sur.add(progreso, BorderLayout.NORTH);

        JPanel botones = new JPanel();
        botones.setOpaque(false);
        btnProcesar = new BotonPlano("Procesar", BotonPlano.Tipo.PRIMARIO, Iconos.run(java.awt.Color.WHITE));
        btnProcesar.addActionListener(evt -> procesar());
        btnCerrar = new BotonPlano("Cerrar", BotonPlano.Tipo.SECUNDARIO);
        btnCerrar.addActionListener(evt -> cerrar());
        botones.add(btnProcesar);
        botones.add(btnCerrar);

        JPanel surCentro = new JPanel(new BorderLayout());
        surCentro.setOpaque(false);
        surCentro.add(botones, BorderLayout.CENTER);
        surCentro.add(pie(), BorderLayout.SOUTH);
        sur.add(surCentro, BorderLayout.CENTER);
        raiz.add(sur, BorderLayout.SOUTH);

        setContentPane(raiz);
        pack();
    }

    private JPanel seccion(String titulo) {
        JPanel panel = tarjeta();
        panel.setLayout(new BorderLayout(0, 10));
        JLabel lbl = new JLabel(titulo);
        lbl.setFont(UITheme.FONT_BOLD);
        lbl.setForeground(UITheme.PRIMARY);
        panel.add(lbl, BorderLayout.NORTH);
        return panel;
    }

    private JTextField campoLectura() {
        JTextField campo = new JTextField();
        campo.setEditable(false);
        campo.setBackground(new java.awt.Color(0xF7F9FC));
        return campo;
    }

    private void addCampo(JPanel panel, int fila, String etiqueta, Component campo) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        JLabel label = new JLabel(etiqueta);
        label.setFont(UITheme.FONT);
        label.setForeground(UITheme.TEXT);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        campo.setPreferredSize(new Dimension(380, 26));
        panel.add(campo, gbc);
    }

    private void actualizarLista() {
        try {
            List<Configuracion> lista = new ConfiguracionDAO().listarTodas();
            cbConfiguraciones.removeAllItems();
            if (lista.isEmpty()) {
                log.info("No hay configuraciones disponibles.");
            }
            for (Configuracion item : lista) {
                cbConfiguraciones.addItem(item);
            }
        } catch (Exception ex) {
            log.error("Error al listar configuraciones: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, "Error al listar configuraciones. " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarSeleccion() {
        configuracion = (Configuracion) cbConfiguraciones.getSelectedItem();
        if (configuracion == null) {
            return;
        }
        txtProyecto.setText(nvl(configuracion.getNombre_proyecto()));
        BuildTool tool = configuracion.getHerramientaBuild() == null ? BuildTool.MAVEN : configuracion.getHerramientaBuild();
        txtHerramienta.setText(tool.getEtiqueta());
        AppServer server = configuracion.getServidor() == null ? AppServer.WILDFLY : configuracion.getServidor();
        txtServidor.setText(server.getEtiqueta());
        txtBuildDir.setText(nvl(configuracion.getBuildDir()));
        txtWarName.setText(nvl(configuracion.getWarName()));
        txtDestino.setText(describirDestino(configuracion));
        actualizarEstadoServidor();
    }

    private String describirDestino(Configuracion conf) {
        AppServer server = conf.getServidor() == null ? AppServer.WILDFLY : conf.getServidor();
        if (server == AppServer.GLASSFISH) {
            DeployMode mode = conf.getDeployMode() == null ? DeployMode.AUTODEPLOY : conf.getDeployMode();
            if (mode == DeployMode.ASADMIN) {
                return "asadmin " + nvl(conf.getAsadminPath()) + " @ " + nvl(conf.getGfHost()) + ":" + nvl(conf.getGfPort());
            }
            return nvl(conf.getGlassfishDeployDir());
        }
        return nvl(conf.getWildflyDeployDir());
    }

    private void accionServidor(String accion) {
        if (configuracion == null) {
            aviso("Seleccione una configuracion.");
            return;
        }
        Thread hilo = new Thread(() -> {
            Utiles.componentesBlocking(getContentPane(), true);
            bloquearControles(true);
            try {
                ServerController controller = new ServerController(configuracion);
                switch (accion) {
                    case "iniciar":
                        controller.iniciar();
                        break;
                    case "detener":
                        controller.detener();
                        break;
                    case "reiniciar":
                        controller.reiniciar();
                        break;
                    default:
                        break;
                }
            } catch (Exception ex) {
                log.error("Error en accion de servidor: " + ex.getMessage(), ex);
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                actualizarEstadoServidor();
                Utiles.componentesBlocking(getContentPane(), false);
                bloquearControles(false);
            }
        });
        hilo.start();
    }

    private void actualizarEstadoServidor() {
        if (configuracion == null) {
            lblEstadoServidor.setText("ESTADO: DESCONOCIDO");
            lblEstadoServidor.setForeground(UITheme.MUTED);
            return;
        }
        try {
            boolean corriendo = new ServerController(configuracion).estaCorriendo();
            lblEstadoServidor.setText(corriendo ? "ESTADO: EN EJECUCION" : "ESTADO: DETENIDO");
            lblEstadoServidor.setForeground(corriendo ? UITheme.SUCCESS : UITheme.DANGER);
        } catch (Exception ex) {
            lblEstadoServidor.setText("ESTADO: DESCONOCIDO");
            lblEstadoServidor.setForeground(UITheme.MUTED);
        }
    }

    private void procesar() {
        if (configuracion == null) {
            aviso("Seleccione una configuracion.");
            return;
        }
        if (!validar(configuracion)) {
            return;
        }

        Thread hilo = new Thread(() -> {
            Utiles.componentesBlocking(getContentPane(), true);
            bloquearControles(true);
            btnProcesar.setText("Procesando...");
            progreso.setVisible(true);
            areaConsola.append("========================================\n");
            areaConsola.append("Iniciando build & deploy de " + configuracion.getNombre_proyecto() + "\n");

            try {
                new EjecutarController(configuracion).procesar();
                areaConsola.append("Proceso finalizado con exito.\n");
                nsg.portafolio.utiles.Sonidos.exito();
            } catch (Exception ex) {
                log.error("Error al procesar: " + ex.getMessage(), ex);
                areaConsola.append("ERROR: " + ex.getMessage() + "\n");
                nsg.portafolio.utiles.Sonidos.error();
                JOptionPane.showMessageDialog(this, "Error al procesar. " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                progreso.setVisible(false);
                btnProcesar.setText("Procesar");
                Utiles.componentesBlocking(getContentPane(), false);
                bloquearControles(false);
                actualizarEstadoServidor();
            }
        });
        hilo.start();
    }

    private void bloquearControles(boolean bloquear) {
        btnIniciar.setEnabled(!bloquear);
        btnDetener.setEnabled(!bloquear);
        btnReiniciar.setEnabled(!bloquear);
        btnEstado.setEnabled(!bloquear);
        btnProcesar.setEnabled(!bloquear);
        btnCerrar.setEnabled(!bloquear);
    }

    private boolean validar(Configuracion conf) {
        BuildTool tool = conf.getHerramientaBuild() == null ? BuildTool.MAVEN : conf.getHerramientaBuild();
        if (tool == BuildTool.ANT) {
            if (nvl(conf.getAntBuildFile()).isEmpty()) {
                aviso("La configuracion no tiene build.xml de Ant.");
                return false;
            }
        } else if (nvl(conf.getPomDir()).isEmpty() || nvl(conf.getMavenExecutable()).isEmpty()) {
            aviso("La configuracion no tiene pom.xml o ejecutable de Maven.");
            return false;
        }
        if (nvl(conf.getBuildDir()).isEmpty() || nvl(conf.getWarName()).isEmpty()) {
            aviso("La configuracion no tiene directorio de compilacion o nombre de WAR.");
            return false;
        }
        return true;
    }

    private void cerrar() {
        dispose();
        new PrincipalFrm().setVisible(true);
    }

    private void aviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private String nvl(String valor) {
        return valor == null ? "" : valor;
    }

    public static void main(String[] args) {
        nsg.portafolio.ui.App.main(args);
    }
}
