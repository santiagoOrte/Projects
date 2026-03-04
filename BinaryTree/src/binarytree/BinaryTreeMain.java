package binarytree;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main — Punto de entrada de la aplicación.
 *
 * Su única responsabilidad es construir los tres componentes del MVC
 * y conectarlos entre sí. Nada más.
 *
 * Orden de construcción:
 *  1. Modelo  → BinaryTree (no depende de nadie)
 *  2. Vista   → TreeView   (necesita el modelo solo para que TreePanel lo lea)
 *  3. Control → TreeController (necesita modelo + vista para conectarlos)
 *  4. show()  → hace visible la ventana
 *
 *  ┌─────────┐          ┌──────────────────┐          ┌─────────────────┐
 *  │  Main   │ ──new──► │   BinaryTree     │          │                 │
 *  │         │          │    (Modelo)      │◄─────────│  TreeController │
 *  │         │ ──new──► │   TreeView       │          │  (Controlador)  │
 *  │         │          │    (Vista)       │◄─────────│                 │
 *  │         │ ──new──► └──────────────────┘          └─────────────────┘
 *  └─────────┘
 */
public class BinaryTreeMain {

    public static void main(String[] args) {
        // Ejecutar en el Event Dispatch Thread (EDT) — obligatorio en Swing
        // para garantizar seguridad con los componentes de la interfaz.
        SwingUtilities.invokeLater(() -> {
            try {
                // Look & Feel del sistema operativo para mejor integración visual
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Si falla, Swing usa su L&F por defecto — no es crítico
            }

            // 1. Modelo
            BinaryTree model = new BinaryTree();

            // 2. Vista (recibe el modelo solo para que TreePanel pueda dibujarlo)
            TreeView view = new TreeView(model);

            // 3. Controlador — conecta modelo y vista, registra todos los listeners
            new TreeController(model, view);

            // 4. Mostrar la ventana
            view.display();
        });
    }
}
