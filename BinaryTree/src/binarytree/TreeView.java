package binarytree;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * TreeView — Implementación de la Vista en el patrón MVC.
 *
 * Responsabilidades:
 *  - Construir y mostrar todos los componentes Swing.
 *  - Implementar {@link ITreeView} para que el Controlador
 *    pueda darle órdenes y suscribirse a sus eventos.
 *  - NO contiene lógica de negocio.
 *  - NO llama directamente al modelo (BinaryTree).
 *    La única excepción es {@link TreePanel}, que lee el modelo
 *    solo para renderizarlo (lectura de datos de presentación).
 *
 * Flujo de eventos:
 *  Usuario hace click → Vista dispara ActionListener →
 *  Controlador recibe el evento → llama al Modelo →
 *  llama de vuelta a la Vista para actualizarla.
 */
public class TreeView extends JFrame implements ITreeView {

    // ── Paleta de colores ─────────────────────────────────────────────────────
    private static final Color BG_MAIN       = new Color(0xF5, 0xED, 0xD8);
    private static final Color BG_PANEL      = new Color(0xEA, 0xDF, 0xC8);
    private static final Color BG_TREE       = new Color(0xFD, 0xF8, 0xF0);
    private static final Color COLOR_PRIMARY = new Color(0xA0, 0x72, 0x28);
    private static final Color COLOR_BTN     = new Color(0xC4, 0x9A, 0x3C);
    private static final Color COLOR_BTN_HOV = new Color(0xA5, 0x7E, 0x28);
    private static final Color COLOR_DELETE  = new Color(0xB5, 0x5D, 0x35);
    private static final Color COLOR_DEL_HOV = new Color(0x95, 0x45, 0x22);
    private static final Color COLOR_TEXT    = new Color(0x3E, 0x28, 0x10);
    private static final Color COLOR_MUTED   = new Color(0x8A, 0x6A, 0x40);
    private static final Color COLOR_CARD    = new Color(0xF0, 0xE8, 0xD5);

    // ── Componentes que el Controlador necesita leer/actualizar ───────────────
    private final TreePanel  treePanel;   // panel de dibujo (mantenido aquí, usado por la interfaz)
    private final JTextField inputField;
    private final JLabel     resultLabel;
    private final JLabel     heightLabel;
    private final JLabel     sizeLabel;

    // ── Botones expuestos para suscripción ────────────────────────────────────
    // Son campos para poder registrar listeners desde onInsert(), onDelete(), etc.
    private final JButton btnInsert;
    private final JButton btnDelete;
    private final JButton btnClear;
    private final JButton btnInOrder;
    private final JButton btnPreOrder;
    private final JButton btnPostOrder;

    // ── Constructor ───────────────────────────────────────────────────────────

    /**
     * Construye la ventana y todos sus componentes.
     * @param tree Árbol usado por {@link TreePanel} para renderizarse.
     *             La Vista no llama a métodos de modificación sobre él.
     */
    public TreeView(BinaryTree tree) {
        super("🌳  Árbol Binario de Búsqueda");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 680));
        setSize(1200, 750);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_MAIN);
        setLayout(new BorderLayout(0, 0));

        // Crear el panel de dibujo con referencia al modelo (solo lectura)
        treePanel = new TreePanel(tree);

        // Inicializar campos antes de buildUI para poder referenciarlos
        inputField  = buildInputField();
        resultLabel = buildResultLabel();
        heightLabel = statLabel("Altura:  —");
        sizeLabel   = statLabel("Nodos:   0");

        // Crear botones (aún sin listeners; el Controlador los registrará)
        btnInsert   = flatButton("＋  Insertar",    COLOR_BTN,                COLOR_BTN_HOV);
        btnDelete   = flatButton("－  Eliminar",    COLOR_DELETE,             COLOR_DEL_HOV);
        btnClear    = flatButton("🗑  Limpiar todo", new Color(0x88,0x66,0x44), new Color(0x66,0x48,0x28));
        btnInOrder  = flatButton("↔  In-Orden",     new Color(0x5C,0x7A,0x44), new Color(0x3E,0x5A,0x28));
        btnPreOrder = flatButton("▶  Pre-Orden",    new Color(0x44,0x6A,0x8A), new Color(0x2C,0x50,0x6E));
        btnPostOrder= flatButton("◀  Post-Orden",   new Color(0x7A,0x44,0x7A), new Color(0x5A,0x28,0x5A));

        buildUI();
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

    /** Panel izquierdo: entrada, botones y estadísticas. */
    private JPanel buildControlPanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(240, 0));
        panel.setBackground(BG_PANEL);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 16, 20, 16));

        // Valor a insertar/eliminar
        panel.add(sectionLabel("Valor"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(inputField);
        panel.add(Box.createVerticalStrut(12));

        // Operaciones sobre el árbol
        panel.add(sectionLabel("Modificar árbol"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(btnInsert);
        panel.add(Box.createVerticalStrut(6));
        panel.add(btnDelete);
        panel.add(Box.createVerticalStrut(6));
        panel.add(btnClear);
        panel.add(Box.createVerticalStrut(20));
        panel.add(separator());
        panel.add(Box.createVerticalStrut(16));

        // Recorridos
        panel.add(sectionLabel("Recorridos"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(btnInOrder);
        panel.add(Box.createVerticalStrut(6));
        panel.add(btnPreOrder);
        panel.add(Box.createVerticalStrut(6));
        panel.add(btnPostOrder);
        panel.add(Box.createVerticalStrut(20));
        panel.add(separator());
        panel.add(Box.createVerticalStrut(16));

        // Estadísticas
        panel.add(sectionLabel("Estadísticas"));
        panel.add(Box.createVerticalStrut(8));
        panel.add(buildStatsCard());
        panel.add(Box.createVerticalGlue());

        // Resultado de última operación
        panel.add(resultLabel);
        return panel;
    }

    /** Tarjeta de altura y número de nodos. */
    private JPanel buildStatsCard() {
        JPanel card = new JPanel(new GridLayout(2, 1, 0, 6));
        card.setBackground(COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BTN, 1, true),
            new EmptyBorder(10, 14, 10, 14)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 78));
        card.add(heightLabel);
        card.add(sizeLabel);
        return card;
    }

    /** Área central con scroll que contiene el panel de dibujo. */
    private JScrollPane buildTreeArea() {
        JScrollPane scroll = new JScrollPane(treePanel);
        scroll.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, COLOR_BTN));
        scroll.getViewport().setBackground(BG_TREE);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        return scroll;
    }

    // ── Helpers de construcción ───────────────────────────────────────────────

    private JTextField buildInputField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setBackground(new Color(0xFF, 0xFB, 0xF2));
        field.setForeground(COLOR_TEXT);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_BTN, 1, true),
            new EmptyBorder(4, 10, 4, 10)
        ));
        return field;
    }

    private JLabel buildResultLabel() {
        JLabel lbl = new JLabel(" ");
        lbl.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lbl.setForeground(COLOR_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return lbl;
    }

    /**
     * Botón flat con efecto hover pintado manualmente.
     * Sin listeners — se registran desde el Controlador.
     */
    private JButton flatButton(String text, Color normal, Color hover) {
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
        return btn;
    }

    private JLabel sectionLabel(String text) {
        JLabel lbl = new JLabel(text.toUpperCase());
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 10));
        lbl.setForeground(COLOR_MUTED);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    private JLabel statLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(COLOR_TEXT);
        return lbl;
    }

    private JSeparator separator() {
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(0xC8, 0xB8, 0x98));
        return sep;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Implementación de ITreeView — COMANDOS
    // ══════════════════════════════════════════════════════════════════════════

    @Override
    public String getInputValue() {
        return inputField.getText().trim();
    }

    @Override
    public void clearInput() {
        inputField.setText("");
    }

    @Override
    public void showResult(String message, Color color) {
        resultLabel.setText("<html><body style='width:200px'>" + message + "</body></html>");
        resultLabel.setForeground(color);
    }

    @Override
    public void updateStats(int height, int size) {
        heightLabel.setText("Altura:  " + (height == -1 ? "—" : height));
        sizeLabel.setText("Nodos:   " + size);
    }

    @Override
    public void repaintTree() {
        treePanel.repaint();
    }

    @Override
    public void animateTraversal(List<Integer> values) {
        treePanel.animateTraversal(values);
    }

    @Override
    public void clearHighlight() {
        treePanel.clearHighlight();
    }

    @Override
    public void display() {
        setVisible(true);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // Implementación de ITreeView — SUSCRIPCIONES
    // El Controlador llama a estos métodos para registrar sus listeners.
    // La Vista no sabe qué hace cada listener; solo los ejecuta cuando
    // ocurre el evento correspondiente.
    // ══════════════════════════════════════════════════════════════════════════

    @Override
    public void onInsert(ActionListener listener) {
        btnInsert.addActionListener(listener);
        // También insertar al presionar Enter en el campo de texto
        inputField.addActionListener(listener);
    }

    @Override
    public void onDelete(ActionListener listener) {
        btnDelete.addActionListener(listener);
    }

    @Override
    public void onClear(ActionListener listener) {
        btnClear.addActionListener(listener);
    }

    @Override
    public void onInOrder(ActionListener listener) {
        btnInOrder.addActionListener(listener);
    }

    @Override
    public void onPreOrder(ActionListener listener) {
        btnPreOrder.addActionListener(listener);
    }

    @Override
    public void onPostOrder(ActionListener listener) {
        btnPostOrder.addActionListener(listener);
    }
}
