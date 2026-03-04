package binarytree;

import java.awt.Color;
import java.util.List;

/**
 * TreeController — Controlador en el patrón MVC.
 *
 * Responsabilidades:
 *  - Recibir eventos de la Vista (a través de los ActionListeners
 *    registrados en ITreeView).
 *  - Llamar al Modelo (BinaryTree) para ejecutar las operaciones.
 *  - Llamar de vuelta a la Vista (ITreeView) para reflejar los cambios.
 *
 * Lo que NO hace:
 *  - No construye componentes Swing.
 *  - No dibuja nada.
 *  - No accede a widgets directamente.
 *
 * ┌──────────┐   eventos    ┌────────────────┐   opera   ┌─────────────┐
 * │  Vista   │ ──────────► │  Controlador   │ ────────► │   Modelo    │
 * │ ITreeView│ ◄────────── │ TreeController │ ◄──────── │ BinaryTree  │
 * └──────────┘  actualizar └────────────────┘  datos    └─────────────┘
 *
 * Colores de feedback al usuario:
 *  - Verde     → operación exitosa (inserción/eliminación).
 *  - Terracota → error o elemento no encontrado.
 *  - Gris café → información neutral (limpiar, recorridos).
 */
public class TreeController {

    // ── Colores de feedback ───────────────────────────────────────────────────
    private static final Color COLOR_SUCCESS  = new Color(0x4A, 0x7A, 0x30); // verde oliva
    private static final Color COLOR_ERROR    = new Color(0xB5, 0x5D, 0x35); // terracota
    private static final Color COLOR_INFO     = new Color(0x2E, 0x1A, 0x08); // café oscuro
    private static final Color COLOR_MUTED    = new Color(0x8A, 0x6A, 0x40); // café suave

    // ── Dependencias ──────────────────────────────────────────────────────────
    private final BinaryTree model;
    private final ITreeView  view;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Crea el controlador y registra TODOS los listeners en la Vista.
     * Después de este constructor, el sistema está listo para responder
     * a cualquier acción del usuario.
     *
     * @param model El árbol binario (Modelo).
     * @param view  La interfaz de la vista (puede ser cualquier implementación).
     */
    public TreeController(BinaryTree model, ITreeView view) {
        this.model = model;
        this.view  = view;

        // Registrar cada listener en la vista — suscripción del controlador
        view.onInsert(e   -> handleInsert());
        view.onDelete(e   -> handleDelete());
        view.onClear(e    -> handleClear());
        view.onInOrder(e  -> handleTraversal(TraversalType.IN_ORDER));
        view.onPreOrder(e -> handleTraversal(TraversalType.PRE_ORDER));
        view.onPostOrder(e-> handleTraversal(TraversalType.POST_ORDER));

        // Mostrar estadísticas iniciales (árbol vacío)
        refreshStats();
    }

    // ── Manejadores de eventos ─────────────────────────────────────────────────

    /**
     * Intenta insertar el valor del campo de entrada en el árbol.
     *
     * Casos:
     *  - Input inválido (no es entero)  → mensaje de error.
     *  - Valor duplicado                → mensaje de aviso.
     *  - Inserción exitosa              → mensaje de éxito + redibujar.
     */
    private void handleInsert() {
        String raw = view.getInputValue();
        try {
            int value = Integer.parseInt(raw);

            if (model.contains(value)) {
                view.showResult("⚠  El valor " + value + " ya existe en el árbol.", COLOR_ERROR);
                return;
            }

            model.insert(value);
            view.clearInput();
            view.clearHighlight();
            view.showResult("✔  Insertado: " + value, COLOR_SUCCESS);
            refreshStats();
            view.repaintTree();

        } catch (NumberFormatException e) {
            view.showResult("⚠  \"" + raw + "\" no es un entero válido.", COLOR_ERROR);
        }
    }

    /**
     * Intenta eliminar el valor del campo de entrada del árbol.
     *
     * Casos:
     *  - Input inválido       → mensaje de error.
     *  - Valor no encontrado  → mensaje de aviso.
     *  - Eliminación exitosa  → mensaje + redibujar.
     */
    private void handleDelete() {
        String raw = view.getInputValue();
        try {
            int value = Integer.parseInt(raw);
            boolean deleted = model.delete(value);

            view.clearInput();
            view.clearHighlight();

            if (deleted) {
                view.showResult("✔  Eliminado: " + value, new Color(0x7A, 0x30, 0x28));
            } else {
                view.showResult("⚠  El valor " + value + " no está en el árbol.", COLOR_ERROR);
            }

            refreshStats();
            view.repaintTree();

        } catch (NumberFormatException e) {
            view.showResult("⚠  \"" + raw + "\" no es un entero válido.", COLOR_ERROR);
        }
    }

    /**
     * Vacía el árbol completamente.
     */
    private void handleClear() {
        model.clear();
        view.clearHighlight();
        view.showResult("🗑  Árbol vaciado.", COLOR_MUTED);
        refreshStats();
        view.repaintTree();
    }

    /**
     * Ejecuta un recorrido y lanza la animación en la Vista.
     *
     * @param type El tipo de recorrido a ejecutar.
     */
    private void handleTraversal(TraversalType type) {
        if (model.isEmpty()) {
            view.showResult("⚠  El árbol está vacío.", COLOR_ERROR);
            return;
        }

        List<Integer> result;
        String label;

        // Seleccionar recorrido según el tipo
        switch (type) {
            case IN_ORDER   -> { result = model.inOrder();   label = "In-Orden";   }
            case PRE_ORDER  -> { result = model.preOrder();  label = "Pre-Orden";  }
            default         -> { result = model.postOrder(); label = "Post-Orden"; }
        }

        // Mostrar resultado textual y lanzar animación en la vista
        view.showResult(label + ": " + result.toString(), COLOR_INFO);
        view.animateTraversal(result);
    }

    // ── Helpers internos ──────────────────────────────────────────────────────

    /**
     * Lee las estadísticas actuales del modelo y se las envía a la vista.
     * Se llama después de cada operación que modifica el árbol.
     */
    private void refreshStats() {
        int height = model.isEmpty() ? -1 : model.getHeight();
        view.updateStats(height, model.getSize());
    }

    // ── Tipos de recorrido (enum interno) ─────────────────────────────────────

    /**
     * Enum para los tres tipos de recorrido disponibles.
     * Hace el código del switch más legible y elimina strings "mágicos".
     */
    private enum TraversalType {
        IN_ORDER, PRE_ORDER, POST_ORDER
    }
}
