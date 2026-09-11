package nsg.portafolio.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;

/**
 * Boton plano con esquinas redondeadas y efecto hover, independiente del
 * Look and Feel.
 */
public class BotonPlano extends JButton {

    public enum Tipo {
        PRIMARIO, SECUNDARIO, PELIGRO, EXITO, NEUTRO
    }

    private final Tipo tipo;
    private boolean hover;

    public BotonPlano(String texto, Tipo tipo) {
        this(texto, tipo, null);
    }

    public BotonPlano(String texto, Tipo tipo, Icon icono) {
        super(texto, icono);
        this.tipo = tipo;
        setFont(UITheme.FONT_BOLD);
        setForeground(colorTexto());
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setBorder(BorderFactory.createEmptyBorder(9, 18, 9, 18));
        setIconTextGap(8);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hover = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(colorFondo());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        g2.dispose();
        super.paintComponent(g);
    }

    private Color colorFondo() {
        if (!isEnabled()) {
            return new Color(0xE0E0E0);
        }
        switch (tipo) {
            case PRIMARIO:
                return hover ? UITheme.PRIMARY_DARK : UITheme.PRIMARY;
            case PELIGRO:
                return hover ? UITheme.DANGER_DARK : UITheme.DANGER;
            case EXITO:
                return hover ? UITheme.SUCCESS_DARK : UITheme.SUCCESS;
            case SECUNDARIO:
                return hover ? new Color(0xE8F0FE) : UITheme.SURFACE;
            default:
                return hover ? new Color(0xECEFF1) : new Color(0x00000000, true);
        }
    }

    private Color colorTexto() {
        switch (tipo) {
            case PRIMARIO:
            case PELIGRO:
            case EXITO:
                return Color.WHITE;
            case SECUNDARIO:
                return UITheme.PRIMARY;
            default:
                return UITheme.TEXT;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setForeground(enabled ? colorTexto() : UITheme.MUTED);
        setCursor(enabled ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) : Cursor.getDefaultCursor());
    }
}
