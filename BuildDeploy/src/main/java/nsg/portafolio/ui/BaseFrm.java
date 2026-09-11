package nsg.portafolio.ui;

import java.awt.BorderLayout;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Base comun para las ventanas: encabezado, tarjetas y pie consistentes.
 */
public abstract class BaseFrm extends JFrame {

    protected BaseFrm(String titulo) {
        super(titulo);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    protected JPanel raiz() {
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(UITheme.BG);
        panel.setBorder(BorderFactory.createEmptyBorder(18, 18, 14, 18));
        return panel;
    }

    protected JPanel encabezado(String titulo, String subtitulo) {
        JPanel panel = new JPanel(new BorderLayout(0, 2));
        panel.setOpaque(false);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(UITheme.FONT_TITLE);
        lblTitulo.setForeground(UITheme.TEXT);
        panel.add(lblTitulo, BorderLayout.NORTH);

        if (subtitulo != null && !subtitulo.isEmpty()) {
            JLabel lblSub = new JLabel(subtitulo);
            lblSub.setFont(UITheme.FONT_SUBTITLE);
            lblSub.setForeground(UITheme.MUTED);
            panel.add(lblSub, BorderLayout.SOUTH);
        }
        return panel;
    }

    protected JPanel tarjeta() {
        JPanel panel = new JPanel();
        panel.setBackground(UITheme.SURFACE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(UITheme.BORDER, 14, 1),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));
        return panel;
    }

    protected JLabel pie() {
        JLabel lbl = new JLabel("Copyright @Norio 2025 - Todos los derechos reservados", SwingConstants.CENTER);
        lbl.setFont(UITheme.FONT_SMALL);
        lbl.setForeground(UITheme.MUTED);
        return lbl;
    }
}
