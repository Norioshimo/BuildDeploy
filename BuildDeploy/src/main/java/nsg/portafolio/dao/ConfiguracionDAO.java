package nsg.portafolio.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import nsg.portafolio.db.ConfigDB;
import nsg.portafolio.enums.AppServer;
import nsg.portafolio.enums.BuildTool;
import nsg.portafolio.enums.DeployMode;
import nsg.portafolio.model.Configuracion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfiguracionDAO extends ConfigDB<Configuracion> {

    private static final Logger log = LogManager.getLogger(ConfiguracionDAO.class);

    private static final String COLUMNAS = "nombre_proyecto,herramienta_build,pomDir,buildDir,"
            + "warName,mavenExecutable,antExecutable,antBuildFile,antTarget,servidor,wildflyDeployDir,"
            + "glassfishDeployDir,deployMode,asadminPath,gfHost,gfPort,gfUser,gfPassword,serverHome,domainName,"
            + "detenerAntesDeploy,reiniciarDespuesDeploy,detenerAlFinalizar";

    private static final String PARAMETROS = "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?";

    @Override
    public void guardar(Configuracion conf) throws SQLException {
        boolean nuevo = (conf.getConfiguracion_id() == null);

        String sql;
        if (nuevo) {
            sql = "INSERT INTO configuraciones (" + COLUMNAS + ") VALUES (" + PARAMETROS + ")";
        } else {
            sql = "UPDATE configuraciones SET nombre_proyecto=?,herramienta_build=?,pomDir=?,buildDir=?,warName=?,"
                    + "mavenExecutable=?,antExecutable=?,antBuildFile=?,antTarget=?,servidor=?,wildflyDeployDir=?,"
                    + "glassfishDeployDir=?,deployMode=?,asadminPath=?,gfHost=?,gfPort=?,gfUser=?,gfPassword=?,"
                    + "serverHome=?,domainName=?,detenerAntesDeploy=?,reiniciarDespuesDeploy=?,detenerAlFinalizar=? "
                    + "WHERE configuracion_id=?";
        }

        try (Connection conn = this.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            int i = 1;
            stmt.setString(i++, conf.getNombre_proyecto());
            stmt.setString(i++, enumName(conf.getHerramientaBuild(), BuildTool.MAVEN));
            stmt.setString(i++, conf.getPomDir());
            stmt.setString(i++, conf.getBuildDir());
            stmt.setString(i++, conf.getWarName());
            stmt.setString(i++, conf.getMavenExecutable());
            stmt.setString(i++, conf.getAntExecutable());
            stmt.setString(i++, conf.getAntBuildFile());
            stmt.setString(i++, conf.getAntTarget());
            stmt.setString(i++, enumName(conf.getServidor(), AppServer.WILDFLY));
            stmt.setString(i++, conf.getWildflyDeployDir());
            stmt.setString(i++, conf.getGlassfishDeployDir());
            stmt.setString(i++, enumName(conf.getDeployMode(), DeployMode.AUTODEPLOY));
            stmt.setString(i++, conf.getAsadminPath());
            stmt.setString(i++, conf.getGfHost());
            stmt.setString(i++, conf.getGfPort());
            stmt.setString(i++, conf.getGfUser());
            stmt.setString(i++, conf.getGfPassword());
            stmt.setString(i++, conf.getServerHome());
            stmt.setString(i++, conf.getDomainName());
            stmt.setInt(i++, conf.isDetenerAntesDeploy() ? 1 : 0);
            stmt.setInt(i++, conf.isReiniciarDespuesDeploy() ? 1 : 0);
            stmt.setInt(i++, conf.isDetenerAlFinalizar() ? 1 : 0);

            if (!nuevo) {
                stmt.setInt(i, conf.getConfiguracion_id());
            }

            stmt.executeUpdate();

            if (nuevo) {
                try (ResultSet keys = stmt.getGeneratedKeys()) {
                    if (keys.next()) {
                        conf.setConfiguracion_id(keys.getInt(1));
                    }
                }
            }
        }
    }

    @Override
    public Configuracion buscarPorId(Integer configuracion_id) throws SQLException {
        String sql = "SELECT * FROM configuraciones WHERE configuracion_id = ?";
        try (Connection conn = this.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, configuracion_id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Configuracion> listarTodas() throws SQLException {
        String sql = "SELECT * FROM configuraciones order by nombre_proyecto,configuracion_id";
        List<Configuracion> lista = new ArrayList<>();

        try (Connection conn = this.conectar();
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapear(rs));
            }
        }

        return lista;
    }

    @Override
    public void eliminar(Integer configuracion_id) throws SQLException {
        String sql = "DELETE FROM configuraciones WHERE configuracion_id = ?";
        try (Connection conn = this.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, configuracion_id);
            stmt.executeUpdate();
            log.info("Configuracion eliminada: " + configuracion_id);
        }
    }

    private Configuracion mapear(ResultSet rs) throws SQLException {
        Configuracion conf = new Configuracion();
        conf.setConfiguracion_id(rs.getInt("configuracion_id"));
        conf.setNombre_proyecto(rs.getString("nombre_proyecto"));
        conf.setHerramientaBuild(BuildTool.desde(rs.getString("herramienta_build")));
        conf.setPomDir(rs.getString("pomDir"));
        conf.setBuildDir(rs.getString("buildDir"));
        conf.setWarName(rs.getString("warName"));
        conf.setMavenExecutable(rs.getString("mavenExecutable"));
        conf.setAntExecutable(rs.getString("antExecutable"));
        conf.setAntBuildFile(rs.getString("antBuildFile"));
        conf.setAntTarget(rs.getString("antTarget"));
        conf.setServidor(AppServer.desde(rs.getString("servidor")));
        conf.setWildflyDeployDir(rs.getString("wildflyDeployDir"));
        conf.setGlassfishDeployDir(rs.getString("glassfishDeployDir"));
        conf.setDeployMode(DeployMode.desde(rs.getString("deployMode")));
        conf.setAsadminPath(rs.getString("asadminPath"));
        conf.setGfHost(rs.getString("gfHost"));
        conf.setGfPort(rs.getString("gfPort"));
        conf.setGfUser(rs.getString("gfUser"));
        conf.setGfPassword(rs.getString("gfPassword"));
        conf.setServerHome(rs.getString("serverHome"));
        conf.setDomainName(rs.getString("domainName"));
        conf.setDetenerAntesDeploy(rs.getInt("detenerAntesDeploy") == 1);
        conf.setReiniciarDespuesDeploy(rs.getInt("reiniciarDespuesDeploy") == 1);
        conf.setDetenerAlFinalizar(rs.getInt("detenerAlFinalizar") == 1);
        return conf;
    }

    private String enumName(Enum<?> valor, Enum<?> porDefecto) {
        return (valor == null ? porDefecto : valor).name();
    }

}
