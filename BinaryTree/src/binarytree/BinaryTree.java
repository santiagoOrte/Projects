package binarytree;

import java.util.ArrayList;
import java.util.List;

/**
 * Árbol Binario de Búsqueda (BST).
 *
 * Propiedades del BST:
 *  - Todo valor en el subárbol izquierdo es MENOR que la raíz.
 *  - Todo valor en el subárbol derecho es MAYOR que la raíz.
 *  - No se permiten valores duplicados.
 *
 * Operaciones disponibles:
 *  - insert(int)     → Insertar un valor.
 *  - delete(int)     → Eliminar un valor.
 *  - contains(int)   → Verificar si un valor existe.
 *  - getHeight()     → Altura del árbol.
 *  - getSize()       → Número total de nodos.
 *  - inOrder()       → Recorrido en orden (izq → raíz → der).
 *  - preOrder()      → Recorrido pre-orden (raíz → izq → der).
 *  - postOrder()     → Recorrido post-orden (izq → der → raíz).
 */
public class BinaryTree {

    /** Nodo raíz del árbol; null si el árbol está vacío. */
    private Node root;

    /** Número de nodos en el árbol (se actualiza en insert/delete). */
    private int size;

    // ── Constructor ────────────────────────────────────────────────────────────

    /** Crea un árbol vacío. */
    public BinaryTree() {
        this.root = null;
        this.size = 0;
    }

    // ── Getter de la raíz (paquete) ────────────────────────────────────────────

    /** @return La raíz del árbol (útil para la GUI al dibujarlo). */
    public Node getRoot() { return root; }

    /** @return Cantidad de nodos en el árbol. */
    public int getSize()  { return size;  }

    // ── Inserción ──────────────────────────────────────────────────────────────

    /**
     * Inserta un valor en el árbol.
     * Si el valor ya existe, no se inserta (sin duplicados).
     * @param value Entero a insertar.
     */
    public void insert(int value) {
        root = insertRec(root, value);
    }

    /**
     * Inserción recursiva: recorre el árbol comparando valores
     * hasta encontrar el lugar correcto para el nuevo nodo.
     */
    private Node insertRec(Node node, int value) {
        // Caso base: posición vacía → crear el nuevo nodo aquí
        if (node == null) {
            size++;
            return new Node(value);
        }

        if (value < node.getValue()) {
            // El valor es menor → va al subárbol izquierdo
            node.setLeft(insertRec(node.getLeft(), value));
        } else if (value > node.getValue()) {
            // El valor es mayor → va al subárbol derecho
            node.setRight(insertRec(node.getRight(), value));
        }
        // Si value == node.getValue() → duplicado, no se inserta

        return node; // retornar el nodo sin cambios (o actualizado)
    }

    // ── Eliminación ───────────────────────────────────────────────────────────

    /**
     * Elimina un valor del árbol.
     * Si el valor no existe, el árbol no cambia.
     * @param value Entero a eliminar.
     * @return true si se eliminó, false si no se encontró.
     */
    public boolean delete(int value) {
        int prevSize = size;
        root = deleteRec(root, value);
        return size < prevSize; // retorna true si el tamaño bajó
    }

    /**
     * Eliminación recursiva con 3 casos:
     *  1. Nodo hoja (sin hijos): simplemente se elimina.
     *  2. Nodo con un hijo: se reemplaza por ese hijo.
     *  3. Nodo con dos hijos: se reemplaza por el sucesor in-orden
     *     (el menor valor del subárbol derecho).
     */
    private Node deleteRec(Node node, int value) {
        if (node == null) return null; // valor no encontrado

        if (value < node.getValue()) {
            // Buscar en subárbol izquierdo
            node.setLeft(deleteRec(node.getLeft(), value));

        } else if (value > node.getValue()) {
            // Buscar en subárbol derecho
            node.setRight(deleteRec(node.getRight(), value));

        } else {
            // ── Nodo encontrado ──────────────────────────────────────
            size--;

            // Caso 1 y 2: sin hijo izquierdo → devolver hijo derecho (puede ser null)
            if (node.getLeft() == null) return node.getRight();

            // Caso 2: sin hijo derecho → devolver hijo izquierdo
            if (node.getRight() == null) return node.getLeft();

            // Caso 3: dos hijos → encontrar sucesor in-orden (min del subárbol derecho)
            int successorValue = findMin(node.getRight());
            node.setValue(successorValue);                         // reemplazar valor
            node.setRight(deleteRec(node.getRight(), successorValue)); // eliminar sucesor
            size++; // se decrementó arriba pero el nodo no desapareció, se reemplazó
        }

        return node;
    }

    /**
     * Encuentra el valor mínimo de un subárbol
     * (siempre es el nodo más a la izquierda).
     */
    private int findMin(Node node) {
        while (node.getLeft() != null) {
            node = node.getLeft();
        }
        return node.getValue();
    }

    // ── Búsqueda ──────────────────────────────────────────────────────────────

    /**
     * Verifica si un valor existe en el árbol.
     * @param value Entero a buscar.
     * @return true si existe, false si no.
     */
    public boolean contains(int value) {
        return containsRec(root, value);
    }

    private boolean containsRec(Node node, int value) {
        if (node == null) return false;
        if (value == node.getValue()) return true;
        return value < node.getValue()
            ? containsRec(node.getLeft(),  value)
            : containsRec(node.getRight(), value);
    }

    // ── Altura ────────────────────────────────────────────────────────────────

    /**
     * Calcula la altura del árbol.
     * Altura = número de aristas en el camino más largo desde la raíz a una hoja.
     * Árbol vacío → -1. Árbol con solo raíz → 0.
     * @return Altura del árbol.
     */
    public int getHeight() {
        return heightRec(root);
    }

    /** Calcula la altura de forma recursiva usando divide y vencerás. */
    private int heightRec(Node node) {
        if (node == null) return -1; // árbol/subárbol vacío
        int leftHeight  = heightRec(node.getLeft());
        int rightHeight = heightRec(node.getRight());
        return 1 + Math.max(leftHeight, rightHeight);
    }

    // ── Recorridos ────────────────────────────────────────────────────────────

    /**
     * Recorrido EN ORDEN (In-Order): Izquierda → Raíz → Derecha.
     * En un BST, esto produce los valores en orden ASCENDENTE.
     * @return Lista con los valores en orden ascendente.
     */
    public List<Integer> inOrder() {
        List<Integer> result = new ArrayList<>();
        inOrderRec(root, result);
        return result;
    }

    private void inOrderRec(Node node, List<Integer> result) {
        if (node == null) return;
        inOrderRec(node.getLeft(),  result); // 1. recorrer izquierda
        result.add(node.getValue());          // 2. visitar raíz
        inOrderRec(node.getRight(), result); // 3. recorrer derecha
    }

    /**
     * Recorrido PRE ORDEN (Pre-Order): Raíz → Izquierda → Derecha.
     * Útil para copiar o serializar la estructura del árbol.
     * @return Lista con los valores en pre-orden.
     */
    public List<Integer> preOrder() {
        List<Integer> result = new ArrayList<>();
        preOrderRec(root, result);
        return result;
    }

    private void preOrderRec(Node node, List<Integer> result) {
        if (node == null) return;
        result.add(node.getValue());          // 1. visitar raíz
        preOrderRec(node.getLeft(),  result); // 2. recorrer izquierda
        preOrderRec(node.getRight(), result); // 3. recorrer derecha
    }

    /**
     * Recorrido POST ORDEN (Post-Order): Izquierda → Derecha → Raíz.
     * Útil para eliminar el árbol o evaluar expresiones.
     * @return Lista con los valores en post-orden.
     */
    public List<Integer> postOrder() {
        List<Integer> result = new ArrayList<>();
        postOrderRec(root, result);
        return result;
    }

    private void postOrderRec(Node node, List<Integer> result) {
        if (node == null) return;
        postOrderRec(node.getLeft(),  result); // 1. recorrer izquierda
        postOrderRec(node.getRight(), result); // 2. recorrer derecha
        result.add(node.getValue());           // 3. visitar raíz
    }

    // ── Vaciar árbol ──────────────────────────────────────────────────────────

    /** Elimina todos los nodos del árbol. */
    public void clear() {
        root = null;
        size = 0;
    }

    /** @return true si el árbol no tiene nodos. */
    public boolean isEmpty() { return root == null; }
}
