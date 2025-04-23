package io.github.purpleloop.commons.swing.graph;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.github.purpleloop.commons.swing.graph.exception.GraphException;

/**
 * This class implements a graph edition panel.
 * 
 * 
 * <p>
 * The component can work in focused mode. In this mode, a specific node gets
 * the focus (for instance the selected node). Only neighboring nodes of the
 * focused node are rendered around it. This allows "browsing" the graph
 * locally.
 * </p>
 * 
 * @param <E> the graph content type
 */
public class ViewPanel<E> extends JPanel {

    /** Class logger. */
    private static final Log LOG = LogFactory.getLog(ViewPanel.class);

    /** Srial tag. */
    private static final long serialVersionUID = -1398377989190420865L;

    /** THe associated graph. */
    private Graph<E> graph = null;

    /** Current selection. */
    private Selectable selection = null;

    /** Link source. */
    private Node<E> source = null;

    /** Action to do. */
    private GraphAction toDo = GraphAction.DO_NOTHING;

    /**
     * A text field here labels are edited.
     */
    private JTextField tfNodeLabel;

    /** View listeners. */
    private ArrayList<GraphViewListener<E>> listeners;

    /** The focused node. */
    private Node<E> focusedNode = null;

    /** The focalized mode activation - Focus is set on a specific node. */
    private boolean focalized = false;

    /** The text field used to edit the labels. */
    private boolean textFieldInternal;

    /** An empty layout manager class. */
    private LayoutManager lm = new LayoutManager() {

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            return null;
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            return null;
        }

        @Override
        public void layoutContainer(Container parent) {
        }
    };

    /** Key listener for label edition. */
    private KeyListener labelKeyListener = new KeyAdapter() {

        @SuppressWarnings("unchecked")
        @Override
        public void keyReleased(KeyEvent e) {
            if (selection instanceof Node) {

                Node<E> n = (Node<E>) selection;
                n.setLabel(tfNodeLabel.getText());
            }
            repaint();
        }

    };

    /** Mouse adapter. */
    private MouseAdapter mouseAdapter = new MouseAdapter() {

        @Override
        @SuppressWarnings("unchecked")
        public void mouseClicked(MouseEvent e) {

            hideTFNodeLabel();

            if (graph != null) {

                graph.unselect();
                selection = graph.select(e.getX(), e.getY());

                if (selection instanceof Node) {

                    Node<E> selectedNode = (Node<E>) selection;

                    if (e.getClickCount() == 2) {

                        updateTfNodeLabel(e.getX(), e.getY());
                        tfNodeLabel.setVisible(true);

                    } else {

                        if (tfNodeLabel != null) {
                            tfNodeLabel.setText(selectedNode.getLabel());
                        }

                        if (toDo == GraphAction.DO_LINK) {

                            try {
                                graph.addLink(source, selectedNode);
                            } catch (GraphException e1) {
                                LOG.error("Error when linking nodes ", e1);
                            }
                            toDo = GraphAction.DO_NOTHING;
                        } else if (toDo == GraphAction.DO_SPLINE_LINK) {

                            graph.addSplineLink(source, selectedNode);
                            toDo = GraphAction.DO_NOTHING;
                        }
                        focusedNode = (Node<E>) selection;
                        fireGraphViewNodeSelection(selectedNode);

                    }
                }
                repaint();
            }

        }

        @SuppressWarnings("unchecked")
        @Override
        public void mousePressed(MouseEvent e) {

            hideTFNodeLabel();

            if (graph != null) {
                graph.unselect();
                selection = graph.select(e.getX(), e.getY());
                if (tfNodeLabel != null) {
                    if (selection instanceof Node) {
                        tfNodeLabel.setText(((Node<E>) selection).getLabel());
                    } else if (selection instanceof Link) {
                        tfNodeLabel.setText(((Link<E>) selection).getLabel());
                    }
                }
                repaint();
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {

            hideTFNodeLabel();

            if (graph != null) {
                int rx = (e.getX() / 5) * 5;
                int ry = (e.getY() / 5) * 5;
                rx = (rx < 0) ? 0 : rx;
                ry = (ry < 0) ? 0 : ry;

                graph.dragTo(rx, ry);

                getPreferredSize().setSize(graph.xmax(), graph.ymax());
                repaint();
            }
        }

        private void hideTFNodeLabel() {

            if (tfNodeLabel != null && textFieldInternal) {
                tfNodeLabel.setVisible(false);
            }

        }

        private void updateTfNodeLabel(int x, int y) {
            if (tfNodeLabel != null && textFieldInternal) {
                Rectangle rect = new Rectangle(x, y, 80, 25);
                tfNodeLabel.setBounds(rect);
            }

        }

    };

    /**
     * The graph view panel constructor.
     */
    public ViewPanel() {
        super();

        tfNodeLabel = new JTextField();

        add(tfNodeLabel);
        setLayout(lm);
        tfNodeLabel.addKeyListener(labelKeyListener);
        textFieldInternal = true;

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        toDo = GraphAction.DO_NOTHING;
        listeners = new ArrayList<>();

        setFocalized(false);
        setFocus(null);

        // Default size
        setPreferredSize(new Dimension(200, 200));

    }

    /**
     * This method enable/disable the focalised mode.
     * 
     * In focalized mode, a specific 'focused' node is the center of attention.
     * Only it's direct neighbors are rendered around it.
     * 
     * @param focalized is the focalized mode active
     */
    public void setFocalized(boolean focalized) {
        this.focalized = focalized;
    }

    /**
     * Change the focused node.
     * 
     * @param newFocusedNode the new focused node
     */
    public void setFocus(Node<E> newFocusedNode) {
        if (graph != null) {

            // Cancels the current selection
            graph.unselect();

            // Sets the focus and the selection
            focusedNode = newFocusedNode;
            selection = newFocusedNode;

            // Notifies the selection
            if (newFocusedNode != null) {
                fireGraphViewNodeSelection(newFocusedNode);
            }

        }
    }

    /** @return the focused node. */
    public Node<E> getfocus() {
        return focusedNode;
    }

    /**
     * Creates a graph edition panel.
     * 
     * @param t The text field for label editing
     */
    public ViewPanel(JTextField t) {
        this();
        tfNodeLabel = t;
        tfNodeLabel.addKeyListener(labelKeyListener);
        textFieldInternal = false;
    }

    /**
     * Add a graph view listener.
     * 
     * @param gl the listener to add
     */
    public void addGraphViewListener(GraphViewListener<E> gl) {
        listeners.add(gl);
    }

    /**
     * Notifies all listeners of the selection.
     * 
     * @param sel Selected object
     */
    protected void fireGraphViewNodeSelection(Node<E> sel) {
        Iterator<GraphViewListener<E>> it = listeners.iterator();
        while (it.hasNext()) {
            it.next().graphViewNodeSelection(sel);
        }
    }

    /**
     * Change the associated graph and updates the selection.
     * 
     * @param g new graph to render / edit
     */
    public void setGraph(Graph<E> g) {
        graph = g;
        selection = null;
        setFocus(null);
        setFocalized(false);
        if (tfNodeLabel != null) {
            tfNodeLabel.setText("");
        }

        repaint();
    }

    /**
     * @return the associated edited / rendered graph
     */
    public Graph<E> getGraph() {
        return graph;
    }

    /**
     * Sets the action to do.
     * 
     * @param code the action to do
     */
    @SuppressWarnings("unchecked")
    public void setToDo(GraphAction code) {
        toDo = code;

        if (((toDo == GraphAction.DO_LINK) || (toDo == GraphAction.DO_SPLINE_LINK))
                && (selection instanceof Node)) {
            source = (Node<E>) selection;
        }
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        // g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
        // RenderingHints.VALUE_ANTIALIAS_ON);
        // g2.setRenderingHint(RenderingHints.KEY_RENDERING,
        // RenderingHints.VALUE_RENDER_QUALITY);

        g2.setColor(Color.black);
        if (graph != null) {
            if ((!focalized) || (focusedNode == null)) {
                graph.draw(g2);
            } else {
                graph.drawWithFocus(g2, focusedNode, 1);
            }
        }

    }

    @Override
    public Dimension getPreferredSize() {
        if (graph != null) {
            return graph.getPreferredSize();
        }
        return new Dimension(0, 0);
    }

    /**
     * @return the selected graph element or null if none is selected
     */
    public Selectable getSelection() {
        return (graph != null) ? selection : null;
    }

}
