package nsg.portafolio.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 * Iconos planos dibujados en tiempo de ejecucion (sin archivos externos).
 */
public final class Iconos {

    private static final int SIZE = 20;

    private Iconos() {
    }

    private interface Dibujo {
        void dibujar(Graphics2D g, int s);
    }

    private static ImageIcon crear(Color color, Dibujo dibujo) {
        BufferedImage img = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        g.setColor(color);
        g.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        dibujo.dibujar(g, SIZE);
        g.dispose();
        return new ImageIcon(img);
    }

    private static void letra(Graphics2D g, String texto) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 12));
        FontMetrics fm = g.getFontMetrics();
        int x = (SIZE - fm.stringWidth(texto)) / 2;
        int y = (SIZE - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(texto, x, y);
    }

    public static ImageIcon play(Color c) {
        return crear(c, (g, s) -> {
            int[] xs = {6, 16, 6};
            int[] ys = {4, 10, 16};
            g.fillPolygon(xs, ys, 3);
        });
    }

    public static ImageIcon stop(Color c) {
        return crear(c, (g, s) -> g.fillRoundRect(5, 5, 10, 10, 3, 3));
    }

    public static ImageIcon restart(Color c) {
        return crear(c, (g, s) -> {
            g.drawArc(4, 4, 12, 12, 60, 280);
            g.fillPolygon(new int[]{15, 17, 13}, new int[]{4, 8, 8}, 3);
        });
    }

    public static ImageIcon config(Color c) {
        return crear(c, (g, s) -> {
            g.drawLine(3, 6, 17, 6);
            g.drawLine(3, 10, 17, 10);
            g.drawLine(3, 14, 17, 14);
            g.fillOval(5, 3, 6, 6);
            g.fillOval(10, 7, 6, 6);
            g.fillOval(6, 11, 6, 6);
        });
    }

    public static ImageIcon logs(Color c) {
        return crear(c, (g, s) -> {
            g.drawRoundRect(4, 3, 12, 14, 3, 3);
            g.drawLine(7, 7, 13, 7);
            g.drawLine(7, 10, 13, 10);
            g.drawLine(7, 13, 11, 13);
        });
    }

    public static ImageIcon help(Color c) {
        return crear(c, (g, s) -> {
            g.drawOval(3, 3, 14, 14);
            g.setFont(new Font("Segoe UI", Font.BOLD, 11));
            FontMetrics fm = g.getFontMetrics();
            String t = "?";
            g.drawString(t, (SIZE - fm.stringWidth(t)) / 2, (SIZE - fm.getHeight()) / 2 + fm.getAscent() + 1);
        });
    }

    public static ImageIcon save(Color c) {
        return crear(c, (g, s) -> {
            g.drawLine(5, 10, 8, 14);
            g.drawLine(8, 14, 15, 6);
        });
    }

    public static ImageIcon delete(Color c) {
        return crear(c, (g, s) -> {
            g.drawLine(4, 6, 16, 6);
            g.drawLine(8, 4, 12, 4);
            g.drawRoundRect(6, 6, 8, 11, 2, 2);
            g.drawLine(9, 9, 9, 14);
            g.drawLine(11, 9, 11, 14);
        });
    }

    public static ImageIcon server(Color c) {
        return crear(c, (g, s) -> {
            g.drawRoundRect(3, 4, 14, 5, 2, 2);
            g.drawRoundRect(3, 11, 14, 5, 2, 2);
            g.fillOval(5, 6, 2, 2);
            g.fillOval(5, 13, 2, 2);
        });
    }

    public static ImageIcon estado(Color c) {
        return crear(c, (g, s) -> g.fillOval(6, 6, 8, 8));
    }

    public static ImageIcon run(Color c) {
        return play(c);
    }

    public static ImageIcon maven(Color c) {
        return crear(c, (g, s) -> {
            g.fillRoundRect(2, 2, 16, 16, 4, 4);
            letra(g, "M");
        });
    }

    public static ImageIcon ant(Color c) {
        return crear(c, (g, s) -> {
            g.fillRoundRect(2, 2, 16, 16, 4, 4);
            letra(g, "A");
        });
    }

    public static ImageIcon wildfly(Color c) {
        return crear(c, (g, s) -> {
            g.fillRoundRect(2, 2, 16, 16, 4, 4);
            letra(g, "W");
        });
    }

    public static ImageIcon glassfish(Color c) {
        return crear(c, (g, s) -> {
            g.fillRoundRect(2, 2, 16, 16, 4, 4);
            letra(g, "G");
        });
    }
}
