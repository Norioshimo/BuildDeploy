package nsg.portafolio.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ConfigDB<T> {

    private static final Logger log = LogManager.getLogger(ConfigDB.class);

    protected static final String DB_DIR = "configdb";
    protected static final String DB_NAME = "build_deploy_config";
    protected static final String DB_URL = "jdbc:sqlite:./" + DB_DIR + "/" + DB_NAME + ".db";
    protected static final String DB_USER = "sa";
    protected static final String DB_PASSWORD = "";

    static {
        cargarDriver("org.sqlite.JDBC");
        cargarDriver("org.h2.Driver");
    }

    private static void cargarDriver(String clase) {
        try {
            Class.forName(clase);
        } catch (ClassNotFoundException ex) {
            log.warn("No se encontro el driver JDBC " + clase + " en el classpath.");
        }
    }

    /**
     * Inicializa el directorio, la base SQLite, la tabla y ejecuta la migracion
     * desde H2 si existiera.
     */
    public static void inicializar() throws SQLException {
        File dbFolder = new File(DB_DIR);
        if (!dbFolder.exists()) {
            dbFolder.mkdirs();
        }

        try (Connection conn = conectar()) {
            conn.createStatement().execute("CREATE TABLE IF NOT EXISTS configuraciones ("
                    + "configuracion_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "nombre_proyecto VARCHAR(1000),"
                    + "herramienta_build VARCHAR(20),"
                    + "pomDir VARCHAR(1000),"
                    + "buildDir VARCHAR(1000),"
                    + "warName VARCHAR(1000),"
                    + "mavenExecutable VARCHAR(1000),"
                    + "antExecutable VARCHAR(1000),"
                    + "antBuildFile VARCHAR(1000),"
                    + "antTarget VARCHAR(200),"
                    + "servidor VARCHAR(20),"
                    + "wildflyDeployDir VARCHAR(1000),"
                    + "glassfishDeployDir VARCHAR(1000),"
                    + "deployMode VARCHAR(20),"
                    + "asadminPath VARCHAR(1000),"
                    + "gfHost VARCHAR(200),"
                    + "gfPort VARCHAR(20),"
                    + "gfUser VARCHAR(200),"
                    + "gfPassword VARCHAR(200),"
                    + "serverHome VARCHAR(1000),"
                    + "domainName VARCHAR(200),"
                    + "detenerAntesDeploy INTEGER DEFAULT 0,"
                    + "reiniciarDespuesDeploy INTEGER DEFAULT 0"
                    + ") ");
        }

        MigradorH2ASQLite.migrarSiEsNecesario();
    }

    public static Connection conectar() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            if (conn == null) {
                throw new SQLException("No se pudo establecer la conexion a la base SQLite.");
            }
            return conn;
        } catch (SQLException ex) {
            log.error("Error al conectar a la base SQLite: " + ex.getMessage(), ex);
            throw ex;
        }
    }

    public abstract void guardar(T entity) throws SQLException;

    public abstract T buscarPorId(Integer id) throws SQLException;

    public abstract List<T> listarTodas() throws SQLException;

    public abstract void eliminar(Integer id) throws SQLException;
}
