package io.github.purpleloop.commons.swing.graph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import io.github.purpleloop.commons.math.geom.GeomUtils;
import io.github.purpleloop.commons.math.geom.CartesianLineEquation;
import io.github.purpleloop.commons.swing.TextBox;
import io.github.purpleloop.commons.swing.graph.exception.GraphException;

/**
 * This class implements a simple graph link.
 * 
 * @param <E> the graph content type
 */
public class Link<E> extends GraphObject<E> implements Selectable {

    /** Class logger. */
    private static final Log LOG = LogFactory.getLog(Link.class);

    /** Angle of arrow opening. */
    public static final double ARROW_OPENING = Math.PI / 6;

    /** Size of the arrow sides. */
    private static final double ARROW_SIZE = 10;

    /** Type of the link. */
    public static final int LINK_CODE = 1;

    /** Proximity threshold for link selection. */
    protected static final int PROXIMITY_THRESHOLD = 500;

    /** The source node. */
    protected Node<E> source;

    /** The target node. */
    protected Node<E> target;

    /** Is the link selected ? */
    protected boolean selected;

    /** The link label. */
    protected String label = "link?";

    /**
     * Create a link between two graph nodes.
     * 
     * @param g the owner graph
     * @param src The source node
     * @param target The target node
     */
    public Link(Graph<E> g, Node<E> src, Node<E> target) {
        super(g);
        this.source = src;
        this.target = target;
    }

    /**
     * Creates a link from a graph data stream. Node references are searched in
     * the provided graph.
     * 
     * @param g Graph currently built (used to search nodes by their index)
     * @param in the datastream containing the graph
     */
    public Link(Graph<E> g, DataInputStream in) {

        super(g, in);
        try {
            this.label = in.readUTF();
            int sid = in.readInt();
            int did = in.readInt();
            this.source = g.getNodeByIndex(sid);
            this.target = g.getNodeByIndex(did);

            if ((source == null) || (target == null)) {
                LOG.error("Error, node cannot be found by id !");
            }

            // Unsaved : data, width, height, selected ...
        } catch (IOException | GraphException e) {
            LOG.error("Error, unable to load link " + label, e);
        }
    }

    /**
     * Creates a node from an XML element.
     * 
     * Warning : data contents is not saved.
     * 
     * @param g the owner graph
     * @param element DOM source element
     */
    public Link(Graph<E> g, Element element) throws GraphException {

        super(g, element);

        label = element.getAttribute("label");
        String sourceValue = element.getAttribute("source");
        source = g.getNodeByLabel(sourceValue)
                .orElseThrow(() -> new GraphException("Source node cannot be found"));

        String targetValue = element.getAttribute("target");
        target = g.getNodeByLabel(targetValue)
                .orElseThrow(() -> new GraphException("Target node cannot be found"));

        // Unsaved : data, width, height, selected ...
    }

    /** @return type of the link. */
    public int getLinkCode() {
        return LINK_CODE;
    }

    /**
     * Saves the link in a data stream.
     * 
     * @param out The data stream where to write
     * @param g Graph currently saved (used to search nodes by their index)
     */
    public void saveTo(DataOutputStream out, Graph<E> g) {

        super.saveTo(out);
        try {
            out.writeUTF(label);
            out.writeInt(g.getNodeIndex(source));
            out.writeInt(g.getNodeIndex(target));
            // Unsaved : data, width, height, selected ...
        } catch (IOException e) {
            LOG.error("Error, unable to save the link " + label, e);
        }
    }

    /**
     * Get an XML element representing the link.
     * 
     * @param doc the XML document
     * @param g the owning graph
     * @return XML element
     */
    public Element getXMLLink(Document doc, Graph<E> g) {

        Element linkElement = doc.createElement("link");
        super.saveToXmlElement(linkElement);

        linkElement.setAttribute("label", label);
        linkElement.setAttribute("source", source.getLabel());
        linkElement.setAttribute("target", target.getLabel());
        return linkElement;
    }

    /**
     * Saves the link in the provided print stream.
     * 
     * @param out The print stream where to write
     * @param g Graph currently saved (used to search nodes by their index)
     */
    public void saveTextTo(PrintStream out, Graph<E> g) {

        super.saveTextTo(out);
        out.println(label + "," + g.getNodeIndex(source) + "," + g.getNodeIndex(target));
        // Unsaved : data, width, height, selected ...
    }

    /**
     * @param label the new node label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the Cartesian line equation of the link support, if it exists
     */
    private Optional<CartesianLineEquation> computeCartesianLineEquation() {
        return CartesianLineEquation.fromPoints(source.xLoc(), source.yLoc(), target.xLoc(),
                target.yLoc());
    }

    /**
     * @return the source node
     */
    public Node<E> getSource() {
        return source;
    }

    /**
     * @return the target node
     */
    public Node<E> getTarget() {
        return target;
    }

    /**
     * @return the node label
     */
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("link(");
        sb.append(source);

        if (owner.isOriented()) {
            sb.append(" ->- ");
        } else {
            sb.append(" --- ");
        }
        sb.append(target);

        if (label != null && !label.isBlank()) {
            sb.append(", '");
            sb.append(label);
            sb.append("'");
        }

        sb.append(")");
        return sb.toString();
    }

    /**
     * Returns the other end of the link.
     * 
     * @param n One of the link ends
     * @return the other end
     */
    public Node<E> getOpposite(Node<E> n) {
        return (n == source) ? target : source;
    }

    /**
     * Tests if the link starts from the given node.
     * 
     * @param tst A graph node
     * @return true if the node is the link source
     */
    public boolean hasSource(Node<E> tst) {
        return (this.source == tst);
    }

    /**
     * Tests if the link arrives at the given node.
     * 
     * @param n A graph node
     * @return true if the node is the link target
     */
    public boolean leadsTo(Node<E> n) {
        return (this.target == n);
    }

    /**
     * Returns the coordinate of the segment that holds the link.
     * 
     * @param p (1 for abscissa, 2 for ordinate)
     * @param q (1 for start, 2 for end)
     * @return the coordinate
     */
    public double getCoord(int p, int q) {

        if ((p == 1) && (q == 1)) {
            return (source.xLoc() * 2 + target.xLoc()) / 3;
        } else if ((p == 1) && (q == 2)) {
            return (source.xLoc() + target.xLoc() * 2) / 3;
        } else if ((p == 2) && (q == 1)) {
            return (source.yLoc() * 2 + target.yLoc()) / 3;
        } else if ((p == 2) && (q == 2)) {
            return (source.yLoc() + target.yLoc() * 2) / 3;
        } else {
            return 0;
        }
    }

    /**
     * Tests if the given node is one of the link ends.
     * 
     * @param n The tested node
     * @return true if the node is one of the link ends
     */
    public boolean hasExtremity(Node<E> n) {
        return (hasSource(n) || leadsTo(n));
    }

    /**
     * Draws the link on the graphic, with restriction to elements belonging to
     * the given layer.
     * 
     * @param g Graphics where to draw
     * @param layer Layer to draw
     */
    public void draw(Graphics2D g, int layer) {

        /* A link is represented by a segment of line. An arrow placed in the
         * middle shows the direction. The label is displayed at the first
         * third. */

        if (layer == Graph.LINK_LAYER) {

            if (selected) {
                g.setColor(Color.blue);
            } else {
                g.setColor(Color.black);
            }

            // Source
            double x1 = source.xLoc();
            double y1 = source.yLoc();

            // Target
            double x2 = target.xLoc();
            double y2 = target.yLoc();

            // Holder line
            g.drawLine((int) x1, (int) y1, (int) x2, (int) y2);

            double dx = x2 - x1;
            double dy = -(y2 - y1); // to fix coordinate system (up-left)

            // Holder middle coordinates
            double mx = (x1 + x2) / 2.0;
            double my = (y1 + y2) / 2.0;

            if (owner.isOriented()) {
                drawLinkArrow(g, dx, dy, mx, my);
            }

            if (owner.hasLinkLabels()) {
                // Draws the label

                TextBox tb = new TextBox(label, g.getFont(), g.getFontRenderContext());

                int width = tb.getWidth();
                int height = tb.getHeight();

                g.clearRect((int) mx - (width / 2), (int) my - (height / 2), width, height);

                g.setColor(Color.black);
                tb.renderText(g, mx, my);
            }
        }
    }

    private void drawLinkArrow(Graphics2D g, double dx, double dy, double mx, double my) {

        // Holder angle of the line support
        double ang = GeomUtils.angleForSegment(dx, dy);

        // Draws the arrow
        g.drawLine((int) mx, (int) my,
                (int) (mx + ARROW_SIZE * Math.cos(ang - Math.PI + ARROW_OPENING)),
                (int) (my - ARROW_SIZE * Math.sin(ang - Math.PI + ARROW_OPENING)));
        g.drawLine((int) mx, (int) my,
                (int) (mx + ARROW_SIZE * Math.cos(ang + Math.PI - ARROW_OPENING)),
                (int) (my - ARROW_SIZE * Math.sin(ang + Math.PI - ARROW_OPENING)));
    }

    /**
     * Tests if the point (x,y) is in the rectangle defined by the source and
     * target nodes.
     * 
     * @param x Tested point abscissa
     * @param y Tested point ordinate
     * @return true if the point (x,y) is in the rectangle, false otherwise
     */
    private boolean isInBounds(int x, int y) {
        return ((x >= Math.min(source.xLoc(), target.xLoc()))
                && (y >= Math.min(source.yLoc(), target.yLoc()))
                && (x <= Math.max(source.xLoc(), target.xLoc()))
                && (y <= Math.max(source.yLoc(), target.yLoc())));

    }

    /**
     * Tests if the given point is on the link with a given precision.
     * 
     * @param x Tested point abscissa
     * @param y Tested point ordinate
     * @param eps epsilon for test precision
     * @return true if the point (x,y) is on the link with the given precision,
     *         false otherwise
     */
    protected boolean isNear(int x, int y, double eps) {

        // Point (x,y) is sufficiently near the line supporting the link
        // according to the Cartesian line equation and the given precision.
        Optional<CartesianLineEquation> cartesianLineEquationOpt = computeCartesianLineEquation();

        if (cartesianLineEquationOpt.isPresent()) {
            return cartesianLineEquationOpt.get().isOnTheline(x, y, eps) && isInBounds(x, y);
        }

        return false;
    }

    /**
     * Tests and selects the link if it is in (x,y).
     * 
     * @param x Selection abscissa
     * @param y Selection ordinate
     * @return the selected link if it is in (x,y) or null
     */
    public Selectable select(int x, int y) {

        selected = isNear(x, y, PROXIMITY_THRESHOLD);

        return ((selected) ? this : null);
    }

    /**
     * @return true if the link is selected
     */
    public boolean isSelected() {
        return selected;
    }

    /** Unselect this link. */
    public void unselect() {
        selected = false;
    }

    /**
     * Moves recursively the selected elements.
     * 
     * @param x Abscissa of the selection target
     * @param y Ordinate of the selection target
     */
    public void dragTo(int x, int y) {
        // forbidden on a link so don't do anything
    }

    /**
     * Change the target node.
     * 
     * @param target the new target
     */
    protected void setDestination(Node<E> target) {
        this.target = target;
    }

    /**
     * Change the source node.
     * 
     * @param source the new source
     */
    protected void setSource(Node<E> source) {
        this.source = source;
    }

}
