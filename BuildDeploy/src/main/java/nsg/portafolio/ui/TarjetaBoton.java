package nsg.portafolio.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Tarjeta clicable con icono, titulo y descripcion (usada en el menu).
 */
public class TarjetaBoton extends JPanel {

    private final RoundedBorder bordeNormal = new RoundedBorder(UITheme.BORDER, 16, 1);
    private final RoundedBorder bordeHover = new RoundedBorder(UITheme.PRIMARY, 16, 2);

    public TarjetaBoton(Icon icono, String titulo, String descripcion, Runnable accion) {
        setLayout(new BorderLayout(14, 0));
        setBackground(UITheme.SURFACE);
        setBorder(BorderFactory.createCompoundBorder(bordeNormal, BorderFactory.createEmptyBorder(16, 18, 16, 18)));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel lblIcono = new JLabel(escalar(icono, 34));
        add(lblIcono, BorderLayout.WEST);

        JPanel textos = new JPanel(new BorderLayout(0, 2));
        textos.setOpaque(false);
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new java.awt.Font(UITheme.FONT_BOLD.getFamily(), java.awt.Font.BOLD, 15));
        lblTitulo.setForeground(UITheme.TEXT);
        JLabel lblDesc = new JLabel(descripcion);
        lblDesc.setFont(UITheme.FONT_SMALL);
        lblDesc.setForeground(UITheme.MUTED);
        textos.add(lblTitulo, BorderLayout.NORTH);
        textos.add(lblDesc, BorderLayout.SOUTH);
        add(textos, BorderLayout.CENTER);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBorder(BorderFactory.createCompoundBorder(bordeHover, BorderFactory.createEmptyBorder(16, 18, 16, 18)));
                setBackground(new Color(0xF0F5FF));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBorder(BorderFactory.createCompoundBorder(bordeNormal, BorderFactory.createEmptyBorder(16, 18, 16, 18)));
                setBackground(UITheme.SURFACE);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (accion != null) {
                    accion.run();
                }
            }
        });
    }

    private ImageIcon escalar(Icon icono, int tamano) {
        if (icono instanceof ImageIcon) {
            Image img = ((ImageIcon) icono).getImage().getScaledInstance(tamano, tamano, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        }
        return null;
    }
}
