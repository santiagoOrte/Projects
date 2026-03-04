package binarytree;

import java.awt.Color;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * ITreeView — Contrato que define todo lo que el Controlador
 * puede pedirle a la Vista, y todo lo que la Vista debe exponer
 * para que el Controlador se suscriba a sus eventos.
 *
 * ┌─────────────────────────────────────────────────────────────┐
 * │  PRINCIPIO: El Controlador solo conoce esta interfaz.       │
 * │  La Vista solo conoce esta interfaz.                        │
 * │  Ninguno de los dos sabe cómo está implementado el otro.    │
 * └─────────────────────────────────────────────────────────────┘
 *
 * Se divide en dos grupos de métodos:
 *
 *  1. COMANDOS (lo que el Controlador le ordena a la Vista):
 *     Actualizar UI, mostrar resultados, limpiar campos, animar.
 *
 *  2. SUSCRIPCIONES (lo que el Controlador registra para escuchar):
 *     Cada botón/acción de la Vista tiene un método on<Acción>()
 *     que acepta un ActionListener — el Controlador lo provee.
 */
public interface ITreeView {

    // ══════════════════════════════════════════════════════════════
    // GRUPO 1 — Comandos: el Controlador le habla a la Vista
    // ══════════════════════════════════════════════════════════════

    /**
     * @return El texto actualmente escrito en el campo de entrada.
     *         Nunca retorna null; puede ser cadena vacía.
     */
    String getInputValue();

    /** Limpia el campo de entrada de texto. */
    void clearInput();

    /**
     * Muestra un mensaje de resultado al usuario (éxito, error, info).
     * @param message Texto a mostrar (puede contener HTML).
     * @param color   Color del texto, para diferenciar tipo de mensaje.
     */
    void showResult(String message, Color color);

    /**
     * Actualiza las etiquetas de estadísticas del árbol.
     * @param height Altura actual del árbol (-1 si está vacío).
     * @param size   Número de nodos en el árbol.
     */
    void updateStats(int height, int size);

    /**
     * Solicita que el panel del árbol se redibuje.
     * Se llama después de cualquier modificación al modelo.
     */
    void repaintTree();

    /**
     * Lanza la animación de recorrido: resalta los nodos
     * de la lista en orden, con un delay entre cada uno.
     * @param values Lista de valores en el orden del recorrido.
     */
    void animateTraversal(List<Integer> values);

    /** Limpia cualquier resaltado de recorrido activo. */
    void clearHighlight();

    /** Hace visible la ventana principal. */
    void display();

    // ══════════════════════════════════════════════════════════════
    // GRUPO 2 — Suscripciones: el Controlador escucha a la Vista
    // ══════════════════════════════════════════════════════════════

    /**
     * Registra el listener que se ejecutará cuando el usuario
     * solicite insertar un valor (botón "Insertar" o Enter en el campo).
     */
    void onInsert(ActionListener listener);

    /**
     * Registra el listener que se ejecutará cuando el usuario
     * solicite eliminar un valor.
     */
    void onDelete(ActionListener listener);

    /**
     * Registra el listener que se ejecutará cuando el usuario
     * solicite limpiar todo el árbol.
     */
    void onClear(ActionListener listener);

    /** Registra el listener para el recorrido In-Orden. */
    void onInOrder(ActionListener listener);

    /** Registra el listener para el recorrido Pre-Orden. */
    void onPreOrder(ActionListener listener);

    /** Registra el listener para el recorrido Post-Orden. */
    void onPostOrder(ActionListener listener);
}
