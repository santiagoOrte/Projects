package binarytree;

/**
 * Clase Nodo: unidad básica del árbol binario.
 * Cada nodo almacena un valor entero y referencias
 * a sus hijos izquierdo y derecho.
 */
public class Node {

    /** Valor entero almacenado en el nodo */
    private int value;

    /** Referencia al hijo izquierdo (menor que el padre) */
    private Node left;

    /** Referencia al hijo derecho (mayor que el padre) */
    private Node right;

    /**
     * Constructor: crea un nodo hoja (sin hijos).
     * @param value El valor entero a almacenar.
     */
    public Node(int value) {
        this.value = value;
        this.left  = null;
        this.right = null;
    }

    // ── Getters ────────────────────────────────────────────────────────────────

    /** @return El valor entero del nodo. */
    public int getValue() { return value; }

    /** @return Referencia al hijo izquierdo, o null si no existe. */
    public Node getLeft()  { return left; }

    /** @return Referencia al hijo derecho, o null si no existe. */
    public Node getRight() { return right; }

    // ── Setters ────────────────────────────────────────────────────────────────

    /** @param value Nuevo valor para este nodo. */
    public void setValue(int value) { this.value = value; }

    /** @param left Nodo que pasará a ser hijo izquierdo. */
    public void setLeft(Node left)  { this.left  = left;  }

    /** @param right Nodo que pasará a ser hijo derecho. */
    public void setRight(Node right){ this.right = right; }

    /** @return Representación en texto: "Node(valor)" */
    @Override
    public String toString() {
        return "Node(" + value + ")";
    }
}
