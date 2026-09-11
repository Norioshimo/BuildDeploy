package nsg.portafolio.ui;

import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 * Appender de Log4j2 que vuelca los eventos a un JTextArea de Swing en vivo.
 */
public class TextAreaAppender extends AbstractAppender {

    private static final int MAX_LINEAS = 5000;
    private static volatile JTextArea area;

    private final PatternLayout layout;

    private TextAreaAppender(String name, PatternLayout layout) {
        super(name, null, layout, false, Property.EMPTY_ARRAY);
        this.layout = layout;
    }

    public static void setArea(JTextArea textArea) {
        area = textArea;
    }

    /**
     * Registra el appender sobre el logger de la aplicacion (nsg.portafolio).
     */
    public static void registrar() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();

        PatternLayout layout = PatternLayout.newBuilder()
                .withPattern("%d{HH:mm:ss} %-5level %m%n")
                .build();

        TextAreaAppender appender = new TextAreaAppender("uiConsole", layout);
        appender.start();

        LoggerConfig loggerConfig = config.getLoggerConfig("nsg.portafolio");
        loggerConfig.addAppender(appender, Level.DEBUG, null);
        ctx.updateLoggers();
    }

    @Override
    public void append(LogEvent event) {
        final JTextArea destino = area;
        if (destino == null) {
            return;
        }

        final String mensaje = new String(layout.toByteArray(event));
        SwingUtilities.invokeLater(() -> {
            destino.append(mensaje);
            int lineas = destino.getLineCount();
            if (lineas > MAX_LINEAS) {
                try {
                    int fin = destino.getLineStartOffset(lineas - MAX_LINEAS);
                    destino.replaceRange("", 0, fin);
                } catch (Exception ex) {
                    destino.setText("");
                }
            }
            destino.setCaretPosition(destino.getDocument().getLength());
        });
    }
}
