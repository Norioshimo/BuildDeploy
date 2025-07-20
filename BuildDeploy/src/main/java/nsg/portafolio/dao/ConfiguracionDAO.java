package nsg.portafolio.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import nsg.portafolio.db.ConfigDB;
import nsg.portafolio.model.Configuracion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfiguracionDAO extends ConfigDB<Configuracion> {

    private static final Logger log = LogManager.getLogger(ConfiguracionDAO.class);

    @Override
    public void guardar(Configuracion conf) throws SQLException {

        String sql = "";

        if (conf.getConfiguracion_id() == null) {// Si es nuevo
            sql = "INSERT INTO configuraciones (configuracion_id,nombre_proyecto, pomDir,buildDir,warName,wildflyDeployDir,mavenExecutable) VALUES (NEXT VALUE FOR seq_configuracion_id,?, ?, ?, ?, ?, ?)";

        } else {// Actualizar
            sql = "UPDATE configuraciones SET nombre_proyecto=?, pomDir=?,buildDir=?,warName=?,wildflyDeployDir=?,mavenExecutable=? "
                    + " WHERE configuracion_id=? ";

        }

        try (Connection conn = this.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, conf.getNombre_proyecto());
            stmt.setString(2, conf.getPomDir());
            stmt.setString(3, conf.getBuildDir());
            stmt.setString(4, conf.getWarName());
            stmt.setString(5, conf.getWildflyDeployDir());
            stmt.setString(6, conf.getMavenExecutable());

            if (conf.getConfiguracion_id() != null) {
                stmt.setInt(7, conf.getConfiguracion_id());
            }

            stmt.executeUpdate();
        }
    }

    @Override
    public Configuracion buscarPorId(Integer configuracion_id) throws SQLException {
        String sql = "SELECT * FROM configuraciones WHERE configuracion_id = ?";
        try (Connection conn = this.conectar();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, configuracion_id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Configuracion conf = new Configuracion();
                conf.setNombre_proyecto(rs.getString("nombre_proyecto"));
                conf.setConfiguracion_id(rs.getInt("configuracion_id"));
                conf.setPomDir(rs.getString("pomDir"));
                conf.setBuildDir(rs.getString("buildDir"));
                conf.setWarName(rs.getString("warName"));
                conf.setWildflyDeployDir(rs.getString("wildflyDeployDir"));
                conf.setMavenExecutable(rs.getString("mavenExecutable"));
                return conf;
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
                Configuracion conf = new Configuracion();
                conf.setNombre_proyecto(rs.getString("nombre_proyecto"));
                conf.setConfiguracion_id(rs.getInt("configuracion_id"));
                conf.setPomDir(rs.getString("pomDir"));
                conf.setBuildDir(rs.getString("buildDir"));
                conf.setWarName(rs.getString("warName"));
                conf.setWildflyDeployDir(rs.getString("wildflyDeployDir"));
                conf.setMavenExecutable(rs.getString("mavenExecutable"));
                lista.add(conf);
            }
        }

        return lista;
    }

}
