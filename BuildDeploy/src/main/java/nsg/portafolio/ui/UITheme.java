package nsg.portafolio.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;

/**
 * Tema visual "flat moderno claro" centralizado para toda la aplicacion.
 */
public final class UITheme {

    public static final Color PRIMARY = new Color(0x2D6CDF);
    public static final Color PRIMARY_DARK = new Color(0x1E4FA8);
    public static final Color SUCCESS = new Color(0x2E7D32);
    public static final Color SUCCESS_DARK = new Color(0x1B5E20);
    public static final Color DANGER = new Color(0xC62828);
    public static final Color DANGER_DARK = new Color(0x8E1B1B);
    public static final Color WARNING = new Color(0xEF6C00);
    public static final Color BG = new Color(0xF5F7FA);
    public static final Color SURFACE = Color.WHITE;
    public static final Color TEXT = new Color(0x212121);
    public static final Color MUTED = new Color(0x757575);
    public static final Color BORDER = new Color(0xE0E0E0);

    private static final String FUENTE = fuenteBase();

    public static final Font FONT = new Font(FUENTE, Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font(FUENTE, Font.BOLD, 13);
    public static final Font FONT_TITLE = new Font(FUENTE, Font.BOLD, 22);
    public static final Font FONT_SUBTITLE = new Font(FUENTE, Font.PLAIN, 13);
    public static final Font FONT_SMALL = new Font(FUENTE, Font.PLAIN, 11);
    public static final Font FONT_MONO = new Font("Consolas", Font.PLAIN, 12);

    private UITheme() {
    }

    public static void aplicar() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            // Se mantiene el look and feel por defecto.
        }

        FontUIResource fuente = new FontUIResource(FONT);
        String[] claves = {
            "Label.font", "Button.font", "TextField.font", "TextArea.font", "ComboBox.font",
            "CheckBox.font", "RadioButton.font", "TitledBorder.font", "OptionPane.font",
            "Panel.font", "Table.font", "List.font", "Menu.font", "MenuItem.font", "Spinner.font"
        };
        for (String clave : claves) {
            UIManager.put(clave, fuente);
        }

        UIManager.put("nimbusBase", new ColorUIResource(PRIMARY));
        UIManager.put("nimbusSelectionBackground", new ColorUIResource(PRIMARY));
        UIManager.put("nimbusBlueGrey", new ColorUIResource(new Color(0xB0BEC5)));
        UIManager.put("control", new ColorUIResource(BG));
        UIManager.put("text", new ColorUIResource(TEXT));
        UIManager.put("OptionPane.background", new ColorUIResource(BG));
        UIManager.put("Panel.background", new ColorUIResource(BG));
    }

    private static String fuenteBase() {
        String[] candidatas = {"Segoe UI", "Arial"};
        for (String nombre : candidatas) {
            if (fuenteDisponible(nombre)) {
                return nombre;
            }
        }
        return Font.SANS_SERIF;
    }

    private static boolean fuenteDisponible(String nombre) {
        String[] familias = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String familia : familias) {
            if (familia.equalsIgnoreCase(nombre)) {
                return true;
            }
        }
        return false;
    }
}
