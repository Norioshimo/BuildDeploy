package nsg.portafolio.formulario;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import nsg.portafolio.Utiles;
import nsg.portafolio.dao.ConfiguracionDAO;
import nsg.portafolio.enums.AppServer;
import nsg.portafolio.enums.BuildTool;
import nsg.portafolio.enums.DeployMode;
import nsg.portafolio.model.Configuracion;
import nsg.portafolio.ui.BaseFrm;
import nsg.portafolio.ui.BotonPlano;
import nsg.portafolio.ui.Iconos;
import nsg.portafolio.ui.UITheme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Pantalla para crear/modificar/eliminar configuraciones de build & deploy.
 */
public class ConfiguracionFrm extends BaseFrm {

    private static final Logger log = LogManager.getLogger(ConfiguracionFrm.class);

    private static final String CARD_MAVEN = "MAVEN";
    private static final String CARD_ANT = "ANT";
    private static final String CARD_WILDFLY = "WILDFLY";
    private static final String CARD_GLASSFISH = "GLASSFISH";

    private JComboBox<Configuracion> cbConfiguraciones;
    private JTextField txtProyecto;
    private JComboBox<BuildTool> cbHerramienta;
    private JTextField txtPomDir;
    private JTextField txtBuildDir;
    private JTextField txtWarName;
    private JTextField txtMavenExec;
    private JTextField txtAntExec;
    private JTextField txtAntBuildFile;
    private JTextField txtAntTarget;
    private JPanel cardHerramienta;
    private CardLayout layoutHerramienta;
    private JComboBox<AppServer> cbServidor;
    private JPanel cardServidor;
    private CardLayout layoutServidor;
    private JTextField txtWildflyDeploy;
    private JComboBox<DeployMode> cbDeployMode;
    private JTextField txtGlassfishDeploy;
    private JTextField txtAsadminPath;
    private JTextField txtGfHost;
    private JTextField txtGfPort;
    private JTextField txtGfUser;
    private JTextField txtGfPassword;
    private JTextField txtServerHome;
    private JTextField txtDomainName;
    private JCheckBox chkDetenerAntes;
    private JCheckBox chkReiniciarDespues;
    private BotonPlano btnGuardar;
    private BotonPlano btnEliminar;

    private boolean cargando = false;

    public ConfiguracionFrm() {
        super("Configuracion");
        initUI();
        setLocationRelativeTo(null);
        actualizarLista();
    }

    private void initUI() {
        setMinimumSize(new Dimension(700, 620));

        JPanel raiz = raiz();
        raiz.add(encabezado("Configuracion", "Administra las configuraciones de build & deploy"), BorderLayout.NORTH);

        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new javax.swing.BoxLayout(contenido, javax.swing.BoxLayout.Y_AXIS));

        // --- Proyecto ---
        JPanel tarjetaProyecto = seccion("Proyecto");
        JPanel formProyecto = panelFormulario();
        cbConfiguraciones = new JComboBox<>();
        cbConfiguraciones.addItemListener(evt -> {
            if (!cargando) {
                cargarSeleccion();
            }
        });
        addCampo(formProyecto, 0, "Configuracion:", cbConfiguraciones);
        txtProyecto = new JTextField();
        addCampo(formProyecto, 1, "Nombre Proyecto (*):", txtProyecto);
        tarjetaProyecto.add(formProyecto, BorderLayout.CENTER);

        // --- Compilacion ---
        JPanel tarjetaCompilacion = seccion("Compilacion");
        JPanel formCompilacion = panelFormulario();
        cbHerramienta = new JComboBox<>(BuildTool.values());
        cbHerramienta.addActionListener(evt -> layoutHerramienta.show(cardHerramienta, cardHerramientaActual()));
        addCampo(formCompilacion, 0, "Herramienta de build:", cbHerramienta);

        layoutHerramienta = new CardLayout();
        cardHerramienta = new JPanel(layoutHerramienta);
        cardHerramienta.setOpaque(false);
        cardHerramienta.add(crearPanelMaven(), CARD_MAVEN);
        cardHerramienta.add(crearPanelAnt(), CARD_ANT);
        addFullRow(formCompilacion, 1, cardHerramienta);

        txtBuildDir = new JTextField();
        addCampo(formCompilacion, 2, "Directorio de compilacion (*):", txtBuildDir);
        txtWarName = new JTextField();
        addCampo(formCompilacion, 3, "Nombre de WAR (*):", txtWarName);
        tarjetaCompilacion.add(formCompilacion, BorderLayout.CENTER);

        // --- Servidor ---
        JPanel tarjetaServidor = seccion("Servidor");
        JPanel formServidor = panelFormulario();
        cbServidor = new JComboBox<>(AppServer.values());
        cbServidor.addActionListener(evt -> layoutServidor.show(cardServidor, cardServidorActual()));
        addCampo(formServidor, 0, "Servidor:", cbServidor);

        layoutServidor = new CardLayout();
        cardServidor = new JPanel(layoutServidor);
        cardServidor.setOpaque(false);
        cardServidor.add(crearPanelWildFly(), CARD_WILDFLY);
        cardServidor.add(crearPanelGlassFish(), CARD_GLASSFISH);
        addFullRow(formServidor, 1, cardServidor);

        txtServerHome = new JTextField();
        addCampo(formServidor, 2, "Carpeta del servidor (home):", txtServerHome);

        JPanel automatizacion = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 6, 0));
        automatizacion.setOpaque(false);
        chkDetenerAntes = new JCheckBox("Detener antes del deploy");
        chkReiniciarDespues = new JCheckBox("Reiniciar despues del deploy");
        automatizacion.add(chkDetenerAntes);
        automatizacion.add(chkReiniciarDespues);
        addFullRow(formServidor, 3, automatizacion);
        tarjetaServidor.add(formServidor, BorderLayout.CENTER);

        contenido.add(tarjetaProyecto);
        contenido.add(javax.swing.Box.createVerticalStrut(12));
        contenido.add(tarjetaCompilacion);
        contenido.add(javax.swing.Box.createVerticalStrut(12));
        contenido.add(tarjetaServidor);

        JScrollPane scroll = new JScrollPane(contenido);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        raiz.add(scroll, BorderLayout.CENTER);

        JPanel sur = new JPanel(new BorderLayout());
        sur.setOpaque(false);
        JPanel botones = new JPanel();
        botones.setOpaque(false);
        btnGuardar = new BotonPlano("Guardar", BotonPlano.Tipo.EXITO, Iconos.save(java.awt.Color.WHITE));
        btnGuardar.addActionListener(evt -> guardar());
        btnEliminar = new BotonPlano("Eliminar", BotonPlano.Tipo.PELIGRO, Iconos.delete(java.awt.Color.WHITE));
        btnEliminar.addActionListener(evt -> eliminar());
        btnEliminar.setEnabled(false);
        botones.add(btnGuardar);
        botones.add(btnEliminar);
        sur.add(botones, BorderLayout.CENTER);
        sur.add(pie(), BorderLayout.SOUTH);
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

    private JPanel panelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        return panel;
    }

    private JPanel crearPanelMaven() {
        JPanel p = panelFormulario();
        txtPomDir = new JTextField();
        txtMavenExec = new JTextField();
        addCampo(p, 0, "Directorio de pom.xml (*):", txtPomDir);
        addCampo(p, 1, "Directorio de Maven (*):", txtMavenExec);
        return p;
    }

    private JPanel crearPanelAnt() {
        JPanel p = panelFormulario();
        txtAntExec = new JTextField();
        txtAntBuildFile = new JTextField();
        txtAntTarget = new JTextField();
        addCampo(p, 0, "Directorio de Ant:", txtAntExec);
        addCampo(p, 1, "Archivo build.xml (*):", txtAntBuildFile);
        addCampo(p, 2, "Target de Ant:", txtAntTarget);
        return p;
    }

    private JPanel crearPanelWildFly() {
        JPanel p = panelFormulario();
        txtWildflyDeploy = new JTextField();
        addCampo(p, 0, "Deployments de WildFly (*):", txtWildflyDeploy);
        return p;
    }

    private JPanel crearPanelGlassFish() {
        JPanel p = panelFormulario();
        txtGlassfishDeploy = new JTextField();
        cbDeployMode = new JComboBox<>(DeployMode.values());
        txtAsadminPath = new JTextField();
        txtGfHost = new JTextField();
        txtGfPort = new JTextField();
        txtGfUser = new JTextField();
        txtGfPassword = new JTextField();
        txtDomainName = new JTextField();

        addCampo(p, 0, "Carpeta autodeploy (*):", txtGlassfishDeploy);
        addCampo(p, 1, "Modo de deploy:", cbDeployMode);
        addCampo(p, 2, "Ruta de asadmin:", txtAsadminPath);
        addCampo(p, 3, "Host admin:", txtGfHost);
        addCampo(p, 4, "Puerto admin:", txtGfPort);
        addCampo(p, 5, "Usuario admin:", txtGfUser);
        addCampo(p, 6, "Password admin:", txtGfPassword);
        addCampo(p, 7, "Dominio GlassFish:", txtDomainName);
        return p;
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
        campo.setPreferredSize(new Dimension(330, 28));
        panel.add(campo, gbc);
    }

    private void addFullRow(JPanel panel, int fila, Component campo) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(3, 5, 3, 5);
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(campo, gbc);
    }

    private String cardHerramientaActual() {
        return (cbHerramienta.getSelectedItem() == BuildTool.ANT) ? CARD_ANT : CARD_MAVEN;
    }

    private String cardServidorActual() {
        return (cbServidor.getSelectedItem() == AppServer.GLASSFISH) ? CARD_GLASSFISH : CARD_WILDFLY;
    }

    private void cargarSeleccion() {
        Configuracion seleccion = (Configuracion) cbConfiguraciones.getSelectedItem();
        if (seleccion == null) {
            return;
        }
        if (seleccion.getConfiguracion_id() == null) {
            setearCampos();
            btnEliminar.setEnabled(false);
            return;
        }

        txtProyecto.setText(nvl(seleccion.getNombre_proyecto()));
        cbHerramienta.setSelectedItem(seleccion.getHerramientaBuild() == null ? BuildTool.MAVEN : seleccion.getHerramientaBuild());
        txtPomDir.setText(nvl(seleccion.getPomDir()));
        txtMavenExec.setText(nvl(seleccion.getMavenExecutable()));
        txtAntExec.setText(nvl(seleccion.getAntExecutable()));
        txtAntBuildFile.setText(nvl(seleccion.getAntBuildFile()));
        txtAntTarget.setText(nvl(seleccion.getAntTarget()));
        txtBuildDir.setText(nvl(seleccion.getBuildDir()));
        txtWarName.setText(nvl(seleccion.getWarName()));
        cbServidor.setSelectedItem(seleccion.getServidor() == null ? AppServer.WILDFLY : seleccion.getServidor());
        txtWildflyDeploy.setText(nvl(seleccion.getWildflyDeployDir()));
        txtGlassfishDeploy.setText(nvl(seleccion.getGlassfishDeployDir()));
        cbDeployMode.setSelectedItem(seleccion.getDeployMode() == null ? DeployMode.AUTODEPLOY : seleccion.getDeployMode());
        txtAsadminPath.setText(nvl(seleccion.getAsadminPath()));
        txtGfHost.setText(nvl(seleccion.getGfHost()));
        txtGfPort.setText(nvl(seleccion.getGfPort()));
        txtGfUser.setText(nvl(seleccion.getGfUser()));
        txtGfPassword.setText(nvl(seleccion.getGfPassword()));
        txtServerHome.setText(nvl(seleccion.getServerHome()));
        txtDomainName.setText(nvl(seleccion.getDomainName()));
        chkDetenerAntes.setSelected(seleccion.isDetenerAntesDeploy());
        chkReiniciarDespues.setSelected(seleccion.isReiniciarDespuesDeploy());

        layoutHerramienta.show(cardHerramienta, cardHerramientaActual());
        layoutServidor.show(cardServidor, cardServidorActual());
        btnEliminar.setEnabled(true);
    }

    private void setearCampos() {
        txtProyecto.setText("");
        txtPomDir.setText("");
        txtMavenExec.setText("");
        txtAntExec.setText("");
        txtAntBuildFile.setText("");
        txtAntTarget.setText("");
        txtBuildDir.setText("");
        txtWarName.setText("");
        txtWildflyDeploy.setText("");
        txtGlassfishDeploy.setText("");
        txtAsadminPath.setText("");
        txtGfHost.setText("");
        txtGfPort.setText("");
        txtGfUser.setText("");
        txtGfPassword.setText("");
        txtServerHome.setText("");
        txtDomainName.setText("");
        chkDetenerAntes.setSelected(false);
        chkReiniciarDespues.setSelected(false);
    }

    private void guardar() {
        Configuracion seleccion = (Configuracion) cbConfiguraciones.getSelectedItem();
        if (!validar()) {
            return;
        }

        try {
            Utiles.componentesBlocking(getContentPane(), true);
            btnGuardar.setText("Guardando...");

            Configuracion conf = Configuracion.builder()
                    .configuracion_id(seleccion == null ? null : seleccion.getConfiguracion_id())
                    .nombre_proyecto(txtProyecto.getText().trim())
                    .herramientaBuild((BuildTool) cbHerramienta.getSelectedItem())
                    .pomDir(txtPomDir.getText().trim())
                    .buildDir(txtBuildDir.getText().trim())
                    .warName(txtWarName.getText().trim())
                    .mavenExecutable(txtMavenExec.getText().trim())
                    .antExecutable(txtAntExec.getText().trim())
                    .antBuildFile(txtAntBuildFile.getText().trim())
                    .antTarget(txtAntTarget.getText().trim())
                    .servidor((AppServer) cbServidor.getSelectedItem())
                    .wildflyDeployDir(txtWildflyDeploy.getText().trim())
                    .glassfishDeployDir(txtGlassfishDeploy.getText().trim())
                    .deployMode((DeployMode) cbDeployMode.getSelectedItem())
                    .asadminPath(txtAsadminPath.getText().trim())
                    .gfHost(txtGfHost.getText().trim())
                    .gfPort(txtGfPort.getText().trim())
                    .gfUser(txtGfUser.getText().trim())
                    .gfPassword(txtGfPassword.getText())
                    .serverHome(txtServerHome.getText().trim())
                    .domainName(txtDomainName.getText().trim())
                    .detenerAntesDeploy(chkDetenerAntes.isSelected())
                    .reiniciarDespuesDeploy(chkReiniciarDespues.isSelected())
                    .build();

            new ConfiguracionDAO().guardar(conf);
            JOptionPane.showMessageDialog(this, "Configuracion guardada con exito", "Status", JOptionPane.INFORMATION_MESSAGE);
            log.info("Configuracion guardada: " + conf);
            actualizarLista();
        } catch (Exception ex) {
            log.error("Error al guardar configuracion: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, "Error al guardar. " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Utiles.componentesBlocking(getContentPane(), false);
            btnGuardar.setText("Guardar");
            Configuracion actual = (Configuracion) cbConfiguraciones.getSelectedItem();
            btnEliminar.setEnabled(actual != null && actual.getConfiguracion_id() != null);
        }
    }

    private void eliminar() {
        Configuracion seleccion = (Configuracion) cbConfiguraciones.getSelectedItem();
        if (seleccion == null || seleccion.getConfiguracion_id() == null) {
            aviso("Seleccione una configuracion existente");
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(this,
                "¿Eliminar la configuracion '" + seleccion.getNombre_proyecto() + "'?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            new ConfiguracionDAO().eliminar(seleccion.getConfiguracion_id());
            JOptionPane.showMessageDialog(this, "Configuracion eliminada", "Status", JOptionPane.INFORMATION_MESSAGE);
            actualizarLista();
        } catch (Exception ex) {
            log.error("Error al eliminar configuracion: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, "Error al eliminar. " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validar() {
        if (txtProyecto.getText().trim().isEmpty()) {
            aviso("Nombre Proyecto es obligatorio!");
            return false;
        }
        if (cbHerramienta.getSelectedItem() == BuildTool.ANT) {
            if (txtAntBuildFile.getText().trim().isEmpty()) {
                aviso("El archivo build.xml de Ant es obligatorio!");
                return false;
            }
        } else if (txtPomDir.getText().trim().isEmpty()) {
            aviso("Directorio de pom.xml es obligatorio!");
            return false;
        }
        if (txtBuildDir.getText().trim().isEmpty()) {
            aviso("Directorio de compilacion es obligatorio!");
            return false;
        }
        if (txtWarName.getText().trim().isEmpty()) {
            aviso("Nombre de WAR es obligatorio!");
            return false;
        }
        if (cbServidor.getSelectedItem() == AppServer.GLASSFISH) {
            if (cbDeployMode.getSelectedItem() == DeployMode.ASADMIN) {
                if (txtAsadminPath.getText().trim().isEmpty()) {
                    aviso("La ruta de asadmin es obligatoria!");
                    return false;
                }
            } else if (txtGlassfishDeploy.getText().trim().isEmpty()) {
                aviso("La carpeta autodeploy de GlassFish es obligatoria!");
                return false;
            }
        } else if (txtWildflyDeploy.getText().trim().isEmpty()) {
            aviso("Deployments de WildFly es obligatorio!");
            return false;
        }
        return true;
    }

    private void aviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void actualizarLista() {
        try {
            cargando = true;
            List<Configuracion> lista = new ConfiguracionDAO().listarTodas();
            cbConfiguraciones.removeAllItems();
            cbConfiguraciones.addItem(Configuracion.builder().nombre_proyecto("Nuevo").build());
            for (Configuracion item : lista) {
                cbConfiguraciones.addItem(item);
            }
            cbConfiguraciones.setSelectedIndex(0);
        } catch (Exception ex) {
            log.error("Error al listar configuraciones: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, "Error al listar configuraciones. " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            cargando = false;
            setearCampos();
            layoutHerramienta.show(cardHerramienta, cardHerramientaActual());
            layoutServidor.show(cardServidor, cardServidorActual());
        }
    }

    private String nvl(String valor) {
        return valor == null ? "" : valor;
    }

    public static void main(String[] args) {
        nsg.portafolio.ui.App.main(args);
    }
}
