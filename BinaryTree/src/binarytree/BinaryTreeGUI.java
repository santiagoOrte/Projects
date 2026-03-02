package binarytree;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * BinaryTreeGUI — Interfaz gráfica para el Árbol Binario de Búsqueda.
 *
 * Funcionalidades:
 *  - Insertar valores al árbol.
 *  - Eliminar valores del árbol.
 *  - Visualizar el árbol gráficamente (nodos y conexiones dibujados).
 *  - Recorridos: In-Orden, Pre-Orden, Post-Orden.
 *  - Panel de información: altura y cantidad de nodos.
 */
public class BinaryTreeGUI extends JFrame {

    // ── Paleta de colores ──────────────────────────────────────────────────────
    private static final Color BG_MAIN       = new Color(0xF5, 0xED, 0xD8); // crema cálido
    private static final Color BG_PANEL      = new Color(0xEA, 0xDF, 0xC8); // crema oscuro
    private static final Color BG_TREE       = new Color(0xFD, 0xF8, 0xF0); // blanco cálido
    private static final Color COLOR_PRIMARY = new Color(0xA0, 0x72, 0x28); // dorado oscuro
    private static final Color COLOR_BTN     = new Color(0xC4, 0x9A, 0x3C); // amarillo quemado
    private static final Color COLOR_BTN_HOV = new Color(0xA5, 0x7E, 0x28); // hover botón
    private static final Color COLOR_DELETE  = new Color(0xB5, 0x5D, 0x35); // terracota
    private static final Color COLOR_DEL_HOV = new Color(0x95, 0x45, 0x22);
    private static final Color COLOR_NODE    = new Color(0x7A, 0x52, 0x28); // café nodo
    private static final Color COLOR_NODE_HL = new Color(0xC4, 0x9A, 0x3C); // nodo recorrido
    private static final Color COLOR_EDGE    = new Color(0xA0, 0x80, 0x50); // aristas
    private static final Color COLOR_TEXT    = new Color(0x3E, 0x28, 0x10); // texto principal
    private static final Color COLOR_MUTED   = new Color(0x8A, 0x6A, 0x40); // texto secundario
    private static final Color COLOR_RESULT  = new Color(0x2E, 0x1A, 0x08); // resultado
    private static final Color COLOR_CARD    = new Color(0xF0, 0xE8, 0xD5); // tarjeta info

    // ── Constantes de dibujo ───────────────────────────────────────────────────
    private static final int NODE_RADIUS   = 24; // radio de cada círculo de nodo
    private static final int LEVEL_HEIGHT  = 70; // separación vertical entre niveles

    // ── Lógica ────────────────────────────────────────────────────────────────
    private final BinaryTree tree = new BinaryTree();

    // ── Componentes UI ────────────────────────────────────────────────────────
    private TreePanel  treePanel;     // panel de dibujo del árbol
    private JTextField inputField;    // campo de texto para insertar/eliminar
    private JLabel     resultLabel;   // muestra resultado de operaciones
    private JLabel     heightLabel;   // altura del árbol
    private JLabel     sizeLabel;     // cantidad de nodos

    // ── Constructor ───────────────────────────────────────────────────────────

    public BinaryTreeGUI() {
        super("🌳  Árbol Binario de Búsqueda");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 680));
        setSize(1200, 750);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_MAIN);
        setLayout(new BorderLayout(0, 0));

        buildUI();
        refreshStats();
        setVisible(true);
    }

    // ── Construcción de la UI ─────────────────────────────────────────────────

    private void buildUI() {
        add(buildHeader(),       BorderLayout.NORTH);
        add(buildControlPanel(), BorderLayout.WEST);
        add(buildTreeArea(),     BorderLayout.CENTER);
    }

    /** Barra superior con título. */
    private JPanel buildHeader() {
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 24, 12));
        header.setBackground(COLOR_PRIMARY);

        JLabel title = new JLabel("Árbol Binario de Búsqueda  —  BST Visualizer");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(0xFF, 0xF4, 0xE0));
        header.add(title);

        return header;
    }

    /** Panel izquierdo: controles, botones y estadísticas. */
    private JPanel buildControlPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(240, 0));
        panel.setBackground(BG_PANEL);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 16, 20, 16));

        // ── Input ──────────────────────────────────────────────────────────────
        panel.add(sectionLabel("Valor"));
        panel.add(Box.createVerticalStrut(6));

        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        inputField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        inputField.setBackground(new Color(0xFF, 0xFB, 0xF2));
        inputField.setForeground(COLOR_TEXT);
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BTN, 1, true),
            new EmptyBorder(4, 10, 4, 10)
        ));
        // Insertar al presionar Enter
        inputField.addActionListener(e -> doInsert());
        panel.add(inputField);

        panel.add(Box.createVerticalStrut(12));

        // ── Botones Insertar / Eliminar ────────────────────────────────────────
        panel.add(sectionLabel("Modificar árbol"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(flatButton("＋  Insertar",   COLOR_BTN,    COLOR_BTN_HOV,   e -> doInsert()));
        panel.add(Box.createVerticalStrut(6));
        panel.add(flatButton("－  Eliminar",   COLOR_DELETE, COLOR_DEL_HOV,   e -> doDelete()));
        panel.add(Box.createVerticalStrut(6));
        panel.add(flatButton("🗑  Limpiar todo", new Color(0x88,0x66,0x44),
                                                 new Color(0x66,0x48,0x28), e -> doClear()));

        panel.add(Box.createVerticalStrut(20));
        panel.add(separator());
        panel.add(Box.createVerticalStrut(16));

        // ── Recorridos ────────────────────────────────────────────────────────
        panel.add(sectionLabel("Recorridos"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(flatButton("↔  In-Orden",    new Color(0x5C,0x7A,0x44),
                                               new Color(0x3E,0x5A,0x28), e -> doTraversal("in")));
        panel.add(Box.createVerticalStrut(6));
        panel.add(flatButton("▶  Pre-Orden",   new Color(0x44,0x6A,0x8A),
                                               new Color(0x2C,0x50,0x6E), e -> doTraversal("pre")));
        panel.add(Box.createVerticalStrut(6));
        panel.add(flatButton("◀  Post-Orden",  new Color(0x7A,0x44,0x7A),
                                               new Color(0x5A,0x28,0x5A), e -> doTraversal("post")));

        panel.add(Box.createVerticalStrut(20));
        panel.add(separator());
        panel.add(Box.createVerticalStrut(16));

        // ── Estadísticas ──────────────────────────────────────────────────────
        panel.add(sectionLabel("Estadísticas"));
        panel.add(Box.createVerticalStrut(8));

        JPanel statsCard = new JPanel(new GridLayout(2, 1, 0, 6));
        statsCard.setBackground(COLOR_CARD);
        statsCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BTN, 1, true),
            new EmptyBorder(10, 14, 10, 14)
        ));
        statsCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 78));

        heightLabel = statLabel("Altura: —");
        sizeLabel   = statLabel("Nodos:  0");
        statsCard.add(heightLabel);
        statsCard.add(sizeLabel);
        panel.add(statsCard);

        panel.add(Box.createVerticalGlue());

        // ── Resultado de la operación ─────────────────────────────────────────
        resultLabel = new JLabel(" ");
        resultLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        resultLabel.setForeground(COLOR_MUTED);
        resultLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panel.add(resultLabel);

        return panel;
    }

    /** Panel central con el árbol dibujado. */
    private JScrollPane buildTreeArea() {
        treePanel = new TreePanel();
        treePanel.setBackground(BG_TREE);

        JScrollPane scroll = new JScrollPane(treePanel);
        scroll.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, COLOR_BTN));
        scroll.getViewport().setBackground(BG_TREE);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        return scroll;
    }

    // ── Acciones ──────────────────────────────────────────────────────────────

    /** Inserta el valor del inputField en el árbol. */
    private void doInsert() {
        try {
            int value = Integer.parseInt(inputField.getText().trim());
            if (tree.contains(value)) {
                showResult("⚠  El valor " + value + " ya existe.", COLOR_DELETE);
            } else {
                tree.insert(value);
                showResult("✔  Insertado: " + value, new Color(0x4A, 0x7A, 0x30));
            }
            inputField.setText("");
            treePanel.setHighlight(null); // quitar resaltado de recorrido
            refreshAll();
        } catch (NumberFormatException e) {
            showResult("⚠  Ingresa un número entero válido.", COLOR_DELETE);
        }
    }

    /** Elimina el valor del inputField del árbol. */
    private void doDelete() {
        try {
            int value = Integer.parseInt(inputField.getText().trim());
            boolean deleted = tree.delete(value);
            if (deleted) {
                showResult("✔  Eliminado: " + value, new Color(0x7A, 0x30, 0x28));
            } else {
                showResult("⚠  El valor " + value + " no existe.", COLOR_DELETE);
            }
            inputField.setText("");
            treePanel.setHighlight(null);
            refreshAll();
        } catch (NumberFormatException e) {
            showResult("⚠  Ingresa un número entero válido.", COLOR_DELETE);
        }
    }

    /** Limpia el árbol completo. */
    private void doClear() {
        tree.clear();
        treePanel.setHighlight(null);
        showResult("🗑  Árbol vaciado.", COLOR_MUTED);
        refreshAll();
    }

    /**
     * Ejecuta un recorrido y muestra el resultado resaltando los nodos
     * en el panel del árbol.
     * @param type "in", "pre" o "post"
     */
    private void doTraversal(String type) {
        if (tree.isEmpty()) {
            showResult("⚠  El árbol está vacío.", COLOR_DELETE);
            return;
        }

        List<Integer> result;
        String label;

        switch (type) {
            case "in"   -> { result = tree.inOrder();   label = "In-Orden"; }
            case "pre"  -> { result = tree.preOrder();  label = "Pre-Orden"; }
            default     -> { result = tree.postOrder(); label = "Post-Orden"; }
        }

        // Mostrar resultado textual
        showResult(label + ": " + result.toString(), COLOR_RESULT);

        // Resaltar los nodos en el orden del recorrido (animación simple por timer)
        treePanel.animateTraversal(result);
    }

    // ── Helpers UI ────────────────────────────────────────────────────────────

    /** Actualiza etiquetas de estadísticas y redibuja el árbol. */
    private void refreshAll() {
        refreshStats();
        treePanel.repaint();
    }

    private void refreshStats() {
        int h = tree.isEmpty() ? -1 : tree.getHeight();
        heightLabel.setText("Altura:  " + (h == -1 ? "—" : h));
        sizeLabel.setText("Nodos:   " + tree.getSize());
    }

    private void showResult(String msg, Color color) {
        resultLabel.setText("<html><body style='width:200px'>" + msg + "</body></html>");
        resultLabel.setForeground(color);
    }

    /** Crea una etiqueta de sección (título de grupo). */
    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text.toUpperCase());
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(COLOR_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    /** Crea una etiqueta de estadística. */
    private JLabel statLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(COLOR_TEXT);
        return lbl;
    }

    /** Separador horizontal. */
    private JSeparator separator() {
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(0xC8, 0xB8, 0x98));
        return sep;
    }

    /**
     * Crea un botón flat con colores personalizados y efecto hover.
     * @param text      Etiqueta del botón.
     * @param normal    Color normal.
     * @param hover     Color al pasar el cursor.
     * @param action    Acción al hacer click.
     */
    private JButton flatButton(String text, Color normal, Color hover, ActionListener action) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getModel().isRollover() ? hover : normal);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setForeground(new Color(0xFF, 0xF4, 0xE0));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        btn.addActionListener(action);
        return btn;
    }

    // ── Panel de dibujo del árbol ─────────────────────────────────────────────

    /**
     * TreePanel: JPanel personalizado que dibuja el árbol de forma recursiva.
     * El árbol se centra horizontalmente y crece hacia abajo.
     */
    private class TreePanel extends JPanel {

        /** Lista de nodos a resaltar durante la animación de recorrido. */
        private List<Integer> highlightList = null;
        /** Índice del nodo actualmente resaltado en la animación. */
        private int highlightIndex = -1;

        public TreePanel() {
            setBackground(BG_TREE);
        }

        // ── Animación de recorrido ────────────────────────────────────────────

        /**
         * Anima el recorrido: resalta cada nodo de la lista con un delay de 500ms.
         * @param values Lista de valores en el orden del recorrido.
         */
        public void animateTraversal(List<Integer> values) {
            highlightList  = values;
            highlightIndex = 0;
            repaint();

            // Timer que avanza de nodo en nodo cada 500ms
            Timer timer = new Timer(500, null);
            timer.addActionListener(e -> {
                highlightIndex++;
                if (highlightIndex >= values.size()) {
                    timer.stop();
                    // Mantener todos resaltados un segundo y luego resetear
                    new Timer(1000, ev -> {
                        highlightList  = null;
                        highlightIndex = -1;
                        repaint();
                        ((Timer) ev.getSource()).stop();
                    }) {{ setRepeats(false); }}.start();
                }
                repaint();
            });
            timer.start();
        }

        public void setHighlight(List<Integer> list) {
            highlightList  = list;
            highlightIndex = -1;
            repaint();
        }

        // ── Pintura ───────────────────────────────────────────────────────────

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            // Activar antialiasing para líneas y texto suaves
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,   RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            if (tree.isEmpty()) {
                drawEmptyMessage(g2);
                return;
            }

            // Calcular dimensiones necesarias para el árbol
            int treeHeight = tree.getHeight();
            int treeDepth  = treeHeight + 1;
            // Ancho mínimo necesario para nivel más ancho (hoja)
            int maxLeaves  = (int) Math.pow(2, treeHeight);
            int panelW     = Math.max(getWidth(), maxLeaves * (NODE_RADIUS * 2 + 20) + 40);
            int panelH     = Math.max(getHeight(), treeDepth * LEVEL_HEIGHT + 80);
            setPreferredSize(new Dimension(panelW, panelH));
            revalidate();

            // Dibujar recursivamente desde la raíz
            drawSubtree(g2, tree.getRoot(), panelW / 2, 50, panelW / 4);
        }

        /**
         * Dibuja el subárbol de forma recursiva.
         * @param g2      Contexto gráfico.
         * @param node    Nodo actual.
         * @param x       Posición X del centro del nodo.
         * @param y       Posición Y del centro del nodo.
         * @param offset  Desplazamiento horizontal para los hijos.
         */
        private void drawSubtree(Graphics2D g2, Node node, int x, int y, int offset) {
            if (node == null) return;

            int childY = y + LEVEL_HEIGHT; // Y del siguiente nivel

            // Dibujar aristas antes que los nodos (para que queden detrás).
            // IMPORTANTE: el color/stroke se restablece antes de CADA arista porque
            // la llamada recursiva de drawSubtree modifica el estado gráfico al pintar nodos.
            if (node.getLeft()  != null) {
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(COLOR_EDGE);
                drawEdge(g2, x, y, x - offset, childY);
                drawSubtree(g2, node.getLeft(),  x - offset, childY, offset / 2);
            }
            if (node.getRight() != null) {
                // Restablecer después de la recursión del hijo izquierdo
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.setColor(COLOR_EDGE);
                drawEdge(g2, x, y, x + offset, childY);
                drawSubtree(g2, node.getRight(), x + offset, childY, offset / 2);
            }

            // Dibujar el nodo
            drawNode(g2, node.getValue(), x, y);
        }

        /**
         * Dibuja una arista (línea) entre dos nodos.
         * Se acorta en los extremos para no superponerse con los círculos.
         */
        private void drawEdge(Graphics2D g2, int x1, int y1, int x2, int y2) {
            // Calcular el vector unitario del segmento
            double dx   = x2 - x1;
            double dy   = y2 - y1;
            double dist = Math.sqrt(dx * dx + dy * dy);
            double ux   = dx / dist;
            double uy   = dy / dist;

            // Puntos de inicio y fin ajustados al borde de los círculos
            int sx = (int) (x1 + ux * (NODE_RADIUS + 2));
            int sy = (int) (y1 + uy * (NODE_RADIUS + 2));
            int ex = (int) (x2 - ux * (NODE_RADIUS + 2));
            int ey = (int) (y2 - uy * (NODE_RADIUS + 2));

            g2.drawLine(sx, sy, ex, ey);
        }

        /**
         * Dibuja un nodo (círculo con el valor en el centro).
         * Si el nodo está resaltado por el recorrido, usa un color diferente.
         */
        private void drawNode(Graphics2D g2, int value, int cx, int cy) {
            boolean highlighted = isHighlighted(value);

            int r = NODE_RADIUS;
            int x = cx - r;
            int y = cy - r;
            int d = r * 2;

            // Sombra suave
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillOval(x + 2, y + 4, d, d);

            // Fondo del nodo
            Color fill = highlighted ? COLOR_NODE_HL : COLOR_NODE;
            g2.setColor(fill);
            g2.fillOval(x, y, d, d);

            // Borde del nodo
            g2.setStroke(new BasicStroke(2f));
            g2.setColor(highlighted ? new Color(0xFF, 0xE8, 0x80) : new Color(0x5A, 0x38, 0x18));
            g2.drawOval(x, y, d, d);

            // Texto del valor
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
         * Verifica si un valor está "resaltado" en la animación actual.
         * Se considera resaltado si apareció hasta el índice actual en la lista.
         */
        private boolean isHighlighted(int value) {
            if (highlightList == null || highlightIndex < 0) return false;
            int upTo = Math.min(highlightIndex, highlightList.size() - 1);
            for (int i = 0; i <= upTo; i++) {
                if (highlightList.get(i) == value) return true;
            }
            return false;
        }

        /** Muestra un mensaje centrado cuando el árbol está vacío. */
        private void drawEmptyMessage(Graphics2D g2) {
            String line1 = "El árbol está vacío";
            String line2 = "Inserta un valor para comenzar →";
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            g2.setColor(new Color(0xC0, 0xA8, 0x80));
            FontMetrics fm = g2.getFontMetrics();
            int w = getWidth(), h = getHeight();
            g2.drawString(line1, (w - fm.stringWidth(line1)) / 2, h / 2 - 14);
            g2.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            fm = g2.getFontMetrics();
            g2.drawString(line2, (w - fm.stringWidth(line2)) / 2, h / 2 + 12);
        }
    }
}