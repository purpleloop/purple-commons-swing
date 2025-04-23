package io.github.purpleloop.commons.swing.graph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.PrintStream;
import java.util.BitSet;

import org.w3c.dom.Element;

import io.github.purpleloop.commons.swing.graph.exception.GraphException;

/**
 * This class implements a graph link represented by a 2-spline curve.
 * 
 * @param <E> link the content type
 */
public class SplineLink<E> extends Link<E> {

    /** The link type. */
    public static final int LINK_CODE = 2;

    /** First anchor of the spline link. */
    private Anchor<E> anchor1;

    /** Second anchor of the spline link. */
    private Anchor<E> anchor2;

    /**
     * Creates a spline link between two graph nodes.
     * 
     * @param g owner graph
     * @param src Start node
     * @param target Target node
     */
    public SplineLink(Graph<E> g, Node<E> src, Node<E> target) {

        super(g, src, target);
        if (src == target) {
            anchor1 = new Anchor<>(g, src.xLoc() - 10, src.yLoc() - 10);
            anchor2 = new Anchor<>(g, src.xLoc() + 10, src.yLoc() - 10);
        } else {
            anchor1 = new Anchor<>(g, ((2 * src.xLoc() + target.xLoc()) / 3),
                    ((2 * src.yLoc() + target.yLoc()) / 3));
            anchor2 = new Anchor<>(g, ((src.xLoc() + 2 * target.xLoc()) / 3),
                    ((src.yLoc() + 2 * target.yLoc()) / 3));
        }

    }

    /**
     * Creates a spline link from a data streeam. Nodes are searched in the
     * provided graph.
     * 
     * @param g the graph currently read, where to find nodes
     * @param in the data stream where to read
     */
    public SplineLink(Graph<E> g, DataInputStream in) {

        super(g, in);
        anchor1 = new Anchor<>(g, in);
        anchor2 = new Anchor<>(g, in);
    }

    /**
     * Creates a spline link from an XML element.
     *
     * Warning, contained data are not saved.
     * 
     * @param g the owner graph
     * @param e the XML element
     * @throws GraphException in case of error while creating the link from the
     *             element
     */
    public SplineLink(Graph<E> g, Element e) throws GraphException {
        super(g, e);
    }

    @Override
    public int getLinkCode() {
        return LINK_CODE;
    }

    @Override
    public Selectable select(int x, int y) {

        selected = isNear(x, y, PROXIMITY_THRESHOLD);
        Selectable s = (selected) ? this : null;

        if (s == null) {
            s = anchor1.select(x, y);
        }
        if (s == null) {
            s = anchor2.select(x, y);
        }

        return s;
    }

    @Override
    public void unselect() {
        selected = false;
        anchor1.unselect();
        anchor2.unselect();
    }

    @Override
    protected boolean isNear(int x, int y, double eps) {
        return Spline.test(x, y, source.xLoc(), source.yLoc(), anchor1.xLoc(), anchor1.yLoc(),
                anchor2.xLoc(), anchor2.yLoc(), 2)
                || Spline.test(x, y, anchor1.xLoc(), anchor1.yLoc(), anchor2.xLoc(), anchor2.yLoc(),
                        target.xLoc(), target.yLoc(), 2);
    }

    @Override
    public void draw(Graphics2D g, int layer) {

        double mxx;
        double myy;
        BitSet bs;

        // The link is represented by a spline. An arrow is located in the
        // middle of
        // the curve.. The label is drawn at the first third of the link.

        if (layer == Graph.LINK_LAYER) {

            if (selected) {
                g.setColor(Color.blue);
            } else {
                g.setColor(Color.black);
            }

            bs = new BitSet(3);
            bs.set(1);
            bs.set(3);

            // Draws the curve with two splines

            Spline.draw(g, source.xLoc(), source.yLoc(), anchor1.xLoc(), anchor1.yLoc(),
                    anchor2.xLoc(), anchor2.yLoc(), bs);

            bs = new BitSet(3);
            bs.set(2);

            Spline.draw(g, anchor1.xLoc(), anchor1.yLoc(), anchor2.xLoc(), anchor2.yLoc(),
                    target.xLoc(), target.yLoc(), bs);

            // Draws the anchors if one anchor is selected or if the link is
            // selected.
            if (selected || anchor1.isSelected() || anchor2.isSelected()) {
                anchor1.draw(g, layer);
                anchor2.draw(g, layer);
                // g.drawLine(at1.xLoc(),at1.yLoc(),at2.xLoc(),at2.yLoc());
            }

            mxx = (source.xLoc() + anchor1.xLoc()) / 2;
            myy = (source.yLoc() + anchor1.yLoc()) / 2;

            // Draws the label
            Rectangle2D rect = (g.getFont()).getStringBounds(label, g.getFontRenderContext());
            int width = (int) rect.getWidth();
            int height = (int) rect.getHeight();
            g.clearRect((int) mxx - (width / 2), (int) myy - (height / 2), width, height);
            g.setColor(Color.black);
            g.drawString(label, (int) mxx - (width / 2), (int) myy + (height / 2));
        }
    }

    /**
     * @param aid anchor id (1 for first, 2 for second)
     * @return the link anchor
     */
    public Anchor<E> getAnchor(int aid) {
        return (aid == 1) ? anchor1 : anchor2;
    }

    @Override
    public void saveTo(DataOutputStream out, Graph<E> g) {

        super.saveTo(out, g);
        anchor1.saveTo(out);
        anchor2.saveTo(out);

    }

    @Override
    public void saveTextTo(PrintStream out, Graph<E> g) {

        super.saveTextTo(out, g);
        anchor1.saveTextTo(out);
        anchor2.saveTextTo(out);

    }

    @Override
    public void dragTo(int x, int y) {
        anchor1.dragTo(x, y);
        anchor2.dragTo(x, y);
    }

    @Override
    public double getCoord(int a, int b) {

        if ((a == 1) && (b == 1)) {
            return (source.xLoc() + anchor1.xLoc()) / 2;
        } else if ((a == 1) && (b == 2)) {
            return (anchor2.xLoc() + target.xLoc()) / 2;
        } else if ((a == 2) && (b == 1)) {
            return (source.yLoc() + anchor1.yLoc()) / 2;
        } else if ((a == 2) && (b == 2)) {
            return (anchor2.yLoc() + target.yLoc()) / 2;
        } else {
            return 0;
        }
    }

}
