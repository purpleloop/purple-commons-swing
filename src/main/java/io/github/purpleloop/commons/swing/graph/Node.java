package io.github.purpleloop.commons.swing.graph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import io.github.purpleloop.commons.math.geom.GeomUtils;
import io.github.purpleloop.commons.swing.TextBox;
import io.github.purpleloop.commons.swing.graph.shapes.RightRoundedRectangle;
import io.github.purpleloop.commons.xml.XMLTools;

/**
 * This class implements a localized graph node.
 * 
 * @param <E> the node content type
 */
public class Node<E> extends GraphObject<E> implements Selectable, Comparable<Node<E>> {

    /** Class logger. */
    private static final Log LOG = LogFactory.getLog(Node.class);

    public enum NodeShape {

        /** A rectangular shape. */
        RECT,

        /** A rounderd rectangle shape. */
        ROUND_RECT,

        /** An elliptic shape. */
        ELLIPSE,

        /** A half rounded right (D) shape. */
        HALF_ROUNDED_RIGHT
    }

    /** Rectangular shape. */
    public static final int RECT = 0;

    /** Rounderd rectangular shape. */
    public static final int ROUND_RECT = 1;

    /** Elliptic shape. */
    public static final int ELLIPSE = 2;

    /** Half rounded shape. */
    // TODO may be become deprecated - uses to check
    public static final int HALF_ROUNDED_RIGHT = 40;

    /** Selection color. */
    private static final Color SELECTION_COLOR = new Color(200, 160, 120);

    /** Label contents. */
    private String label = "Node";

    /** Object contained in the node. */
    private E contents;

    /** Abscissa of the node (center). */
    private double xl;

    /** Ordinate of the node (center). */
    private double yl;

    /** Label width. */
    private int width;

    /** Label height. */
    private int height;

    /** The node shape. */
    private NodeShape shape = NodeShape.RECT;

    /** The node color. */
    private Color col = Color.white;

    /** Is the node selected ? */
    private boolean selected;

    /** Is the node pinned ? If true, the node is immovable. */
    private boolean pinned;

    /**
     * Creates a simple default node.
     * 
     * @param g owner graph
     */
    public Node(Graph<E> g) {

        super(g);
        xl = 100;
        yl = 100;
        selected = false;
        pinned = false;
    }

    /**
     * Creates a graph node.
     * 
     * @param g owner graph
     * @param t the node label
     */
    public Node(Graph<E> g, String t) {

        this(g);
        setLabel(t);
    }

    /**
     * Creates a node from a data stream.
     * 
     * Warning, contained data is not read.
     * 
     * @param g the saved graph
     * @param in the data stream containing the graph
     */
    public Node(Graph<E> g, DataInputStream in) {

        super(g, in);
        try {
            label = in.readUTF();
            xl = in.readDouble();
            yl = in.readDouble();
            selected = false;
            pinned = false;
            // unsaved data : width, height, selected ...
        } catch (IOException e) {
            LOG.error("Error : unable to load node", e);
        }

    }

    /**
     * Creates a node from an XML element
     * 
     * Warning, contained data is not read.
     * 
     * @param g the owner graph
     * @param nodeXmlElement XML node element
     */
    public Node(Graph<E> g, Element nodeXmlElement) {

        super(g, nodeXmlElement);
        if (nodeXmlElement.getTagName().equals("node")) {
            label = nodeXmlElement.getAttribute("label");
            xl = XMLTools.getDoubleAttributeValue(nodeXmlElement, "x", 0.0);
            yl = XMLTools.getDoubleAttributeValue(nodeXmlElement, "y", 0.0);

            selected = false;
            pinned = false;
            // unsaved data : width, height, selected ...
        } else {
            LOG.error("Error, unexpected element found : " + nodeXmlElement.getTagName());
        }

    }

    /**
     * Changes the node label.
     * 
     * @param label new label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the node label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Test if the node has the given label.
     * 
     * @param test the tested label
     * @return true if the node has the given label, false otherwise
     */
    public boolean hasLabel(String test) {
        return label.equals(test);
    }

    /**
     * Tests if two nodes have the same labels.
     * 
     * @param n2 another node
     * @return true is labels are equals, false otherwise
     */
    public boolean hasSameLabel(Node<E> n2) {
        return label.equals(n2.getLabel());
    }

    /**
     * Appends text to the node label.
     * 
     * @param text added text
     */
    public void appendLabel(String text) {
        label += text;
    }

    @Override
    public String toString() {
        if (label != null) {
            return label;
        } else {
            return String.format("node [#%d]", getId());
        }
    }

    /**
     * Tests if the node contains the given object.
     * 
     * @param otherContents content to test
     * @return true if given object is contained in the node, false otherwise
     */
    public boolean contains(E otherContents) {

        if (contents == null) {
            return (otherContents == null);
        }

        if (otherContents == null) {
            return false;
        }

        return contents.equals(otherContents);
    }

    @Override
    public int compareTo(Node<E> other) {

        String thisLabel = getLabel();
        String otherLabel = other.getLabel();

        if (thisLabel == null && otherLabel == null) {
            return Integer.compare(this.getId(), other.getId());
        }

        // Priority to labeled nodes
        if (thisLabel == null) {
            return 1;
        }

        if (otherLabel == null) {
            return -1;
        }

        return thisLabel.compareTo(otherLabel);
    }

    /**
     * @param doc the context XML document
     * @return an XML element for the node
     */
    public Element getXMLNode(Document doc) {
        Element node = doc.createElement("node");
        super.saveToXmlElement(node);
        node.setAttribute("label", label);
        node.setAttribute("x", Double.toString(xl));
        node.setAttribute("y", Double.toString(yl));
        return node;
    }

    /**
     * Saves the node in the given datastream.
     * 
     * @param out the data stream here to write
     */
    @Override
    public void saveTo(DataOutputStream out) {

        super.saveTo(out);
        try {
            out.writeUTF(label);
            out.writeDouble(xl);
            out.writeDouble(yl);
            // unsaved data : width, height, selected ...

        } catch (IOException e) {
            LOG.error("Error, unable to save the node" + label, e);
        }
    }

    /**
     * Saves the node in the given print stream.
     * 
     * @param out the print stream where to write
     */
    @Override
    public void saveTextTo(PrintStream out) {

        super.saveTextTo(out);
        out.println(label + "," + xl + "," + yl);
        // unsaved data : width, height, selected ...
    }

    /**
     * @return Maximal abscissa (right side) of the node
     */
    public double xmax() {
        return xl + width / 2.0;
    }

    /**
     * @return Maximal ordinate (bottom side) of the node
     */
    public double ymax() {
        return yl + height / 2.0;
    }

    /**
     * Moves the node in (x,y) if it is not pinned.
     * 
     * @param x Target abscissa
     * @param y Target ordinate
     */
    public void moveTo(double x, double y) {

        // We use moveRel because we have also to move anchors with a reduced
        // effet.
        moveRel(x - xl, y - yl);
    }

    /**
     * Moves the node by the vector (dx,dy) if it is not pinned.
     * 
     * @param dx relative horizontal move
     * @param dy relative vertical move
     */
    public void moveRel(double dx, double dy) {

        if (!pinned) {

            double nx = xl + dx;
            double ny = yl + dy;

            int gWidth = Graph.MAX_WIDTH;
            int gHeight = Graph.MAX_HEIGHT;

            xl = (nx < 0) ? 0 : (nx > gWidth) ? gWidth : nx;
            yl = (ny < 0) ? 0 : (ny > gHeight) ? gHeight : ny;

            // Moves the spline anchors with an attenuated amount
            List<Link<E>> iol = owner.getIOLinks(this);
            SplineLink<E> lsp;

            for (int nbl = 0; nbl < iol.size(); nbl++) {
                if (iol.get(nbl) instanceof SplineLink) {
                    lsp = (SplineLink<E>) (iol.get(nbl));
                    lsp.getAnchor(1).moveRel(dx / 2, dy / 2);
                    lsp.getAnchor(2).moveRel(dx / 2, dy / 2);
                }
            }
        }
    }

    /**
     * @return the node abscissa (center)
     */
    public double xLoc() {
        return xl;
    }

    /**
     * @return the node ordinate (center)
     */
    public double yLoc() {
        return yl;
    }

    /**
     * @return the node width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return the node height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param newShape the new node shape
     */
    public void setShape(NodeShape newShape) {
        shape = newShape;
    }

    /**
     * @return the node shape
     */
    public NodeShape getShape() {
        return shape;
    }

    /**
     * @param newColor the new node color
     */
    public void setColor(Color newColor) {
        col = newColor;
    }

    /**
     * @return then node color
     */
    public Color getColor() {
        return col;
    }

    /**
     * Test if the node is in (x,y).
     * 
     * @param x Selection abscissa
     * @param y Selection ordinate
     * @return the selected node or null
     */
    public Selectable select(int x, int y) {
        selected = isIn(x, y);
        return (selected ? this : null);
    }

    /**
     * @return is the node selected
     */
    public boolean isSelected() {
        return selected;
    }

    /** Unselect the node. */
    public void unselect() {
        selected = false;
    }

    /**
     * @param pinned changes the pinned (if true, the node is immovable) state
     *            of the node
     */
    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    /**
     * @return the pinned state of the node (if true, the node is immovable)
     *         state
     */
    public boolean isPinned() {
        return this.pinned;
    }

    /**
     * Test if the node is in (x,y).
     * 
     * @param x Tested abscissa
     * @param y Tested ordinate
     * @return true if the node is in (x,y), false otherwise
     */
    public boolean isIn(int x, int y) {

        // If there is a specific size, we use it
        if ((width != 0) && (height != 0)) {

            return (Math.abs(x - xl) <= (width / 2)) && (Math.abs(y - yl) <= (height / 2));
        } else {
            return isIn(x, y, 10);
        }

    }

    /**
     * Test if the node is in (x,y), under a given distance.
     * 
     * @param x Tested abscissa
     * @param y Tested ordinate
     * @param range maximal the distance
     * 
     * @return true if the node is in (x,y) under a given distance, false
     *         otherwise
     */
    public boolean isIn(int x, int y, int range) {
        return GeomUtils.distance(this.xl, this.yl, x, y) < range;
    }

    /**
     * Moves the node in (x,y) if it is selected.
     * 
     * @param x Target abscissa
     * @param y Target ordinate
     */
    public void dragTo(int x, int y) {
        if (selected) {
            moveTo(x, y);
        }
    }

    /**
     * Draws the node on the given graphic. Drawn elements are limited to the
     * given layer.
     * 
     * @param g the graphics where to draw
     * @param layer the layer to render
     */
    public void draw(Graphics2D g, int layer) {

        if (layer == 1) {
            if (selected) {
                g.setColor(SELECTION_COLOR);
            } else {
                g.setColor(col);
            }

            if (owner.hasNodeLabels()) {

                String str;
                if (owner.isRenderContents()) {

                    Optional<E> contentsOpt = getContents();
                    if (contentsOpt.isPresent()) {
                        str = contentsOpt.toString();
                    } else {
                        str = label;
                    }

                } else {
                    str = label;
                }

                TextBox tb = new TextBox(str, g.getFont(), g.getFontRenderContext());
                width = tb.getWidth();
                height = tb.getHeight();

                Shape nodeShape;

                switch (shape) {
                case ROUND_RECT:
                    nodeShape = new RoundRectangle2D.Double((int) xl - width / 2.0,
                            (int) yl - height / 2.0, width, height, 10, 10);
                    g.fill(nodeShape);
                    g.setColor(Color.black);
                    g.draw(nodeShape);

                    break;
                case RECT:
                    nodeShape = new Rectangle((int) xl - width / 2, (int) yl - height / 2, width,
                            height);
                    g.fill(nodeShape);
                    g.setColor(Color.black);
                    g.draw(nodeShape);
                    break;
                case ELLIPSE:

                    nodeShape = new Ellipse2D.Double((int) xl - width / 2.0,
                            (int) yl - height / 2.0, width, height);
                    g.fill(nodeShape);
                    g.setColor(Color.black);
                    g.draw(nodeShape);
                    break;
                case HALF_ROUNDED_RIGHT:

                    nodeShape = new RightRoundedRectangle((int) xl - width / 2,
                            (int) yl - height / 2, width, height);

                    g.fill(nodeShape);
                    g.setColor(Color.black);
                    g.draw(nodeShape);

                    break;
                default:

                } // switch

                tb.renderText(g, xl, yl);

            } else {
                width = 10;
                height = 10;
                g.fillOval((int) xl - 5, (int) yl - 5, 10, 10);
                g.setColor(Color.black);
                g.drawOval((int) xl - 5, (int) yl - 5, 10, 10);
            }

        } // layer test
    }

    /**
     * Computes the distance to another node.
     * 
     * @param reference another node
     * @return distance between the two nodes
     */
    public double distanceTo(Node<E> reference) {
        return GeomUtils.distance(this.xl, this.yl, reference.xl, reference.yl);
    }

}
