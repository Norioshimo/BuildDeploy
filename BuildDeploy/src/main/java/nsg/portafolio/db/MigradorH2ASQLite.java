package nsg.portafolio.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Migra una base de datos H2 heredada (configdb/build_deploy_config.mv.db) a
 * SQLite. Solo se ejecuta cuando existe el archivo H2 y la base SQLite aun no
 * tiene registros.
 */
public final class MigradorH2ASQLite {

    private static final Logger log = LogManager.getLogger(MigradorH2ASQLite.class);

    private MigradorH2ASQLite() {
    }

    public static void migrarSiEsNecesario() {
        File h2File = new File(ConfigDB.DB_DIR, ConfigDB.DB_NAME + ".mv.db");
        if (!h2File.exists()) {
            return;
        }

        try {
            if (contarRegistrosSQLite() > 0) {
                log.info("La base SQLite ya contiene datos; no se migra H2.");
                return;
            }

            int migrados = migrar();
            if (migrados > 0) {
                File respaldo = new File(h2File.getAbsolutePath() + ".migrado");
                if (h2File.renameTo(respaldo)) {
                    log.info("Base H2 migrada a SQLite (" + migrados + " registros). Archivo anterior: " + respaldo.getName());
                } else {
                    log.info("Base H2 migrada a SQLite (" + migrados + " registros). No se pudo renombrar el archivo H2.");
                }
            }
        } catch (Exception ex) {
            log.error("Error al migrar la base H2 a SQLite: " + ex.getMessage(), ex);
        }
    }

    private static int contarRegistrosSQLite() throws Exception {
        try (Connection conn = ConfigDB.conectar();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM configuraciones")) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    private static int migrar() throws Exception {
        Class.forName("org.h2.Driver");

        String urlH2 = "jdbc:h2:./" + ConfigDB.DB_DIR + "/" + ConfigDB.DB_NAME;
        int migrados = 0;

        try (Connection h2 = DriverManager.getConnection(urlH2, "sa", "");
                Statement st = h2.createStatement();
                ResultSet rs = st.executeQuery("SELECT * FROM configuraciones")) {

            String insert = "INSERT INTO configuraciones (configuracion_id,nombre_proyecto,herramienta_build,pomDir,"
                    + "buildDir,warName,mavenExecutable,servidor,wildflyDeployDir,deployMode,detenerAntesDeploy,reiniciarDespuesDeploy) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,?,0,0)";

            try (Connection sqlite = ConfigDB.conectar();
                    PreparedStatement ps = sqlite.prepareStatement(insert)) {

                while (rs.next()) {
                    ps.setInt(1, rs.getInt("configuracion_id"));
                    ps.setString(2, rs.getString("nombre_proyecto"));
                    ps.setString(3, "MAVEN");
                    ps.setString(4, rs.getString("pomDir"));
                    ps.setString(5, rs.getString("buildDir"));
                    ps.setString(6, rs.getString("warName"));
                    ps.setString(7, rs.getString("mavenExecutable"));
                    ps.setString(8, "WILDFLY");
                    ps.setString(9, rs.getString("wildflyDeployDir"));
                    ps.setString(10, "AUTODEPLOY");
                    ps.addBatch();
                }

                int[] resultados = ps.executeBatch();
                for (int r : resultados) {
                    if (r >= 0 || r == Statement.SUCCESS_NO_INFO) {
                        migrados++;
                    }
                }
            }
        }

        return migrados;
    }
}
