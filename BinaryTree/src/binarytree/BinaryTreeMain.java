package binarytree;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class BinaryTreeMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new BinaryTreeGUI();
        });
    }
}
