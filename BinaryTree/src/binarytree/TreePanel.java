package binarytree;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * TreePanel — JPanel que se encarga exclusivamente de DIBUJAR el árbol.
 *
 * Responsabilidades:
 *  - Renderizar nodos y aristas del árbol de forma recursiva.
 *  - Animar los recorridos resaltando nodos en orden.
 *  - Ajustar su tamaño preferido según la profundidad del árbol.
 *
 * Lo que NO hace:
 *  - No modifica el árbol.
 *  - No maneja eventos de usuario.
 *  - No conoce al Controlador ni a la Vista principal.
 *
 * Lee el modelo directamente (BinaryTree) solo para obtener datos
 * de representación — esto es aceptable en MVC; la Vista puede
 * leer el Modelo, solo que no puede modificarlo.
 */
public class TreePanel extends JPanel {

    // ── Paleta ────────────────────────────────────────────────────────────────
    private static final Color BG_TREE     = new Color(0xFD, 0xF8, 0xF0);
    private static final Color COLOR_NODE  = new Color(0x7A, 0x52, 0x28); // café nodo
    private static final Color COLOR_HL    = new Color(0xC4, 0x9A, 0x3C); // nodo resaltado
    private static final Color COLOR_EDGE  = new Color(0xA0, 0x80, 0x50); // aristas

    // ── Constantes de geometría ───────────────────────────────────────────────
    private static final int NODE_RADIUS  = 24; // radio del círculo de cada nodo
    private static final int LEVEL_HEIGHT = 70; // distancia vertical entre niveles

    // ── Referencias ──────────────────────────────────────────────────────────
    /** Modelo que este panel observa para pintar. */
    private final BinaryTree tree;

    // ── Estado de la animación de recorrido ───────────────────────────────────
    /** Lista de valores en el orden del recorrido activo (null si no hay ninguno). */
    private List<Integer> highlightList  = null;
    /** Índice del último nodo "encendido" en la animación (-1 = sin animación). */
    private int           highlightIndex = -1;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * @param tree Referencia al árbol que este panel debe dibujar.
     *             Solo se usa para leer; nunca se modifica aquí.
     */
    public TreePanel(BinaryTree tree) {
        this.tree = tree;
        setBackground(BG_TREE);
    }

    // ── API pública (llamada desde TreeView / ITreeView) ──────────────────────

    /**
     * Inicia la animación de recorrido: enciende cada nodo de la lista
     * con un delay de 500 ms entre uno y otro.
     * Al terminar, apaga todos los nodos tras 1 segundo de pausa.
     *
     * @param values Lista de valores en el orden que deben resaltarse.
     */
    public void animateTraversal(List<Integer> values) {
        highlightList  = values;
        highlightIndex = 0;
        repaint();

        // Timer que avanza un nodo cada 500 ms
        Timer step = new Timer(500, null);
        step.addActionListener(e -> {
            highlightIndex++;
            if (highlightIndex >= values.size()) {
                step.stop();
                // Pausa de 1 segundo mostrando todos resaltados, luego apagar
                Timer reset = new Timer(1000, ev -> {
                    clearHighlight();
                    ((Timer) ev.getSource()).stop();
                });
                reset.setRepeats(false);
                reset.start();
            }
            repaint();
        });
        step.start();
    }

    /** Apaga cualquier resaltado de recorrido y redibuja. */
    public void clearHighlight() {
        highlightList  = null;
        highlightIndex = -1;
        repaint();
    }

    // ── Pintado ───────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Renderizado suavizado
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,     RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (tree.isEmpty()) {
            drawEmptyMessage(g2);
            return;
        }

        // Calcular dimensiones necesarias y ajustar tamaño preferido
        int treeHeight = tree.getHeight();
        int maxLeaves  = (int) Math.pow(2, treeHeight);
        int neededW    = Math.max(getWidth(),  maxLeaves * (NODE_RADIUS * 2 + 20) + 40);
        int neededH    = Math.max(getHeight(), (treeHeight + 1) * LEVEL_HEIGHT + 80);
        setPreferredSize(new Dimension(neededW, neededH));
        revalidate();

        // Dibujar desde la raíz centrada, con offset inicial = 1/4 del ancho
        drawSubtree(g2, tree.getRoot(), neededW / 2, 50, neededW / 4);
    }

    // ── Dibujo recursivo ──────────────────────────────────────────────────────

    /**
     * Dibuja el subárbol cuya raíz es {@code node} de forma recursiva.
     *
     * @param g2     Contexto gráfico.
     * @param node   Nodo raíz del subárbol actual.
     * @param x      Coordenada X del centro del nodo.
     * @param y      Coordenada Y del centro del nodo.
     * @param offset Desplazamiento horizontal hacia cada hijo.
     *               Se reduce a la mitad en cada nivel para que el árbol
     *               se "comprima" a medida que profundiza.
     */
    private void drawSubtree(Graphics2D g2, Node node, int x, int y, int offset) {
        if (node == null) return;

        int childY = y + LEVEL_HEIGHT;

        // ── Aristas (antes que los nodos para que queden detrás) ──────────────
        // El stroke/color se restablece antes de CADA arista porque la llamada
        // recursiva modifica g2 al pintar nodos (ver bug anterior).
        if (node.getLeft() != null) {
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(COLOR_EDGE);
            drawEdge(g2, x, y, x - offset, childY);
            drawSubtree(g2, node.getLeft(), x - offset, childY, offset / 2);
        }
        if (node.getRight() != null) {
            // Restablecer después de la recursión izquierda
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(COLOR_EDGE);
            drawEdge(g2, x, y, x + offset, childY);
            drawSubtree(g2, node.getRight(), x + offset, childY, offset / 2);
        }

        // ── Nodo actual ───────────────────────────────────────────────────────
        drawNode(g2, node.getValue(), x, y);
    }

    /**
     * Dibuja la arista entre dos nodos.
     * Los extremos se recortan al borde de los círculos para no superponerse.
     */
    private void drawEdge(Graphics2D g2, int x1, int y1, int x2, int y2) {
        double dx   = x2 - x1, dy = y2 - y1;
        double dist = Math.sqrt(dx * dx + dy * dy);
        double ux   = dx / dist, uy = dy / dist;

        int sx = (int) (x1 + ux * (NODE_RADIUS + 2));
        int sy = (int) (y1 + uy * (NODE_RADIUS + 2));
        int ex = (int) (x2 - ux * (NODE_RADIUS + 2));
        int ey = (int) (y2 - uy * (NODE_RADIUS + 2));

        g2.drawLine(sx, sy, ex, ey);
    }

    /**
     * Dibuja un nodo como círculo con su valor en el centro.
     * Si el nodo forma parte del recorrido activo (ya fue "encendido"),
     * se pinta en dorado en lugar del café estándar.
     */
    private void drawNode(Graphics2D g2, int value, int cx, int cy) {
        boolean highlighted = isHighlighted(value);
        int r = NODE_RADIUS;
        int x = cx - r, y = cy - r, d = r * 2;

        // Sombra suave desplazada 2-4 px
        g2.setColor(new Color(0, 0, 0, 30));
        g2.fillOval(x + 2, y + 4, d, d);

        // Relleno del nodo
        g2.setColor(highlighted ? COLOR_HL : COLOR_NODE);
        g2.fillOval(x, y, d, d);

        // Borde del nodo
        g2.setStroke(new BasicStroke(2f));
        g2.setColor(highlighted ? new Color(0xFF, 0xE8, 0x80) : new Color(0x5A, 0x38, 0x18));
        g2.drawOval(x, y, d, d);

        // Valor numérico centrado
        g2.setColor(highlighted ? new Color(0x3E, 0x28, 0x10) : Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, value >= 100 ? 11 : 13));
        FontMetrics fm = g2.getFontMetrics();
        String txt = String.valueOf(value);
        g2.drawString(txt,
            cx - fm.stringWidth(txt) / 2,
            cy + fm.getAscent() / 2 - 1
        );
    }

    /**
     * Verifica si {@code value} debe estar resaltado en este momento.
     * Un nodo está resaltado si apareció en la lista de recorrido
     * en algún índice menor o igual al índice actual de la animación.
     */
    private boolean isHighlighted(int value) {
        if (highlightList == null || highlightIndex < 0) return false;
        int upTo = Math.min(highlightIndex, highlightList.size() - 1);
        for (int i = 0; i <= upTo; i++) {
            if (highlightList.get(i) == value) return true;
        }
        return false;
    }

    /** Mensaje centrado que se muestra cuando el árbol no tiene nodos. */
    private void drawEmptyMessage(Graphics2D g2) {
        String line1 = "El árbol está vacío";
        String line2 = "Inserta un valor para comenzar →";
        int w = getWidth(), h = getHeight();

        g2.setColor(new Color(0xC0, 0xA8, 0x80));
        g2.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(line1, (w - fm.stringWidth(line1)) / 2, h / 2 - 14);

        g2.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        fm = g2.getFontMetrics();
        g2.drawString(line2, (w - fm.stringWidth(line2)) / 2, h / 2 + 12);
    }
}
