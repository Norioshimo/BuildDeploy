package nsg.portafolio.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import javax.swing.border.AbstractBorder;

/**
 * Borde redondeado con color configurable.
 */
public class RoundedBorder extends AbstractBorder {

    private final Color color;
    private final int radio;
    private final int grosor;

    public RoundedBorder(Color color, int radio, int grosor) {
        this.color = color;
        this.radio = radio;
        this.grosor = grosor;
    }

    @Override
    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(color);
        g2.setStroke(new BasicStroke(grosor));
        g2.drawRoundRect(x + grosor / 2, y + grosor / 2, width - grosor - 1, height - grosor - 1, radio, radio);
        g2.dispose();
    }

    @Override
    public Insets getBorderInsets(Component c) {
        int p = Math.max(radio / 3, 6);
        return new Insets(p, p, p, p);
    }

    @Override
    public Insets getBorderInsets(Component c, Insets insets) {
        Insets i = getBorderInsets(c);
        insets.set(i.top, i.left, i.bottom, i.right);
        return insets;
    }
}
