package nsg.portafolio.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfigDB<T> {

    private static final Logger log = LogManager.getLogger(ConfigDB.class);

    protected static final String DB_DIR = "configdb";
    protected static final String DB_URL = "jdbc:h2:./" + DB_DIR + "/build_deploy_config";
    protected static final String DB_USER = "sa";
    protected static final String DB_PASSWORD = "";

    // Funcion para inicializar y crear la tabla, base de datos.
    public static void inicializar() throws SQLException {
        // Asegura que el directorio exista
        File dbFolder = new File(DB_DIR);
        if (!dbFolder.exists()) {
            dbFolder.mkdirs();
        }

        try (Connection conn = conectar()) {

            conn.createStatement().execute("CREATE TABLE IF NOT EXISTS configuraciones ("
                    + "configuracion_id Integer PRIMARY KEY,"
                    + "nombre_proyecto VARCHAR(1000),"
                    + "pomDir VARCHAR(1000),"
                    + "buildDir VARCHAR(1000),"
                    + "warName VARCHAR(1000),"
                    + "wildflyDeployDir VARCHAR(1000),"
                    + "mavenExecutable VARCHAR(1000)"
                    + ") ");
            conn.createStatement().execute("CREATE SEQUENCE IF NOT EXISTS seq_configuracion_id START WITH 1 INCREMENT BY 1");

        }
    }

    public static Connection conectar() {
        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException ex) {
            ex.printStackTrace();
            log.info("Error al conectar a la base H2");
        }

        return null;
    }

    public void guardar(T entity) throws SQLException {

    }

    public T buscarPorId(Integer id) throws SQLException {

        return null;
    }

    public List<T> listarTodas() throws SQLException {
        return null;
    }
}
