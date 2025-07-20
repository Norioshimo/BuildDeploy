package nsg.portafolio;

import java.awt.Component;
import java.awt.Container;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Utiles {

    public static void componentesBlocking(Container container, boolean bloquear) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JTextField
                    || comp instanceof JTextArea
                    || comp instanceof JButton
                    || comp instanceof JComboBox
                    || comp instanceof JCheckBox
                    || comp instanceof JRadioButton) {
                comp.setEnabled(!bloquear);
            }
        }
    }
}
