package io.github.purpleloop.commons.swing.graph;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A graph anchor.
 * 
 * @param <E> graph content type
 */
public class Anchor<E> extends GraphObject<E> implements Selectable {

    /** Class logger. */
    private static final Log LOG = LogFactory.getLog(Anchor.class);

    /** The anchor size in pixels. */
    public static final int ANCHOR_SIZE = 5;

    /** Horizontal location. */
    private double xl;

    /** Vertical location. */
    private double yl;

    /** Selection state. */
    private boolean selected;

    /**
     * Creates a simple anchor, unselected.
     * 
     * @param ownerGraph Owner graph
     * @param x Abscissa
     * @param y Ordinate
     */
    public Anchor(Graph<E> ownerGraph, double x, double y) {
        super(ownerGraph);
        xl = x;
        yl = y;
        selected = false;
    }

    /**
     * Creates an anchor from a data stream.
     * 
     * @param ownerGraph Read owner graph
     * @param in Data input stream
     */
    public Anchor(Graph<E> ownerGraph, DataInputStream in) {
        super(ownerGraph, in);
        try {
            xl = in.readDouble();
            yl = in.readDouble();
            selected = false;

        } catch (IOException e) {
            LOG.error("Error, unable to load anchor.", e);
        }
    }

    /**
     * Saves a spline anchor in the provided data stream.
     * 
     * @param out Stream where to write
     */
    @Override
    public void saveTo(DataOutputStream out) {

        super.saveTo(out);
        try {
            out.writeDouble(xl);
            out.writeDouble(yl);

        } catch (IOException e) {
            LOG.error("Error : unable to save anchor in (" + xl + yl + ").", e);
        }
    }

    /**
     * Saves a spline anchor in the provided print stream.
     * 
     * @param out PrintStream where to write
     */
    @Override
    public void saveTextTo(PrintStream out) {
        super.saveTextTo(out);
        out.println(xl + "," + yl);
    }

    /**
     * @return Abscissa
     */
    public double xLoc() {
        return xl;
    }

    /**
     * @return Ordinate
     */
    public double yLoc() {
        return yl;
    }

    @Override
    public Selectable select(int x, int y) {
        selected = (Math.abs(x - xl) <= ANCHOR_SIZE) && (Math.abs(y - yl) <= ANCHOR_SIZE);
        return (selected ? this : null);
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void unselect() {
        selected = false;
    }

    /**
     * Draws on the graphic, restricting to the specified layer.
     * 
     * @param g Graphics used to draw
     * @param layer Layer to render
     */
    public void draw(Graphics2D g, int layer) {

        if (layer == Graph.LINK_LAYER) {
            if (selected) {
                g.setColor(Color.blue);
            } else {
                g.setColor(Color.black);
            }

            g.drawLine((int) xl - ANCHOR_SIZE, (int) yl + ANCHOR_SIZE, (int) xl + ANCHOR_SIZE,
                    (int) yl - ANCHOR_SIZE);
            g.drawLine((int) xl - ANCHOR_SIZE, (int) yl - ANCHOR_SIZE, (int) xl + ANCHOR_SIZE,
                    (int) yl + ANCHOR_SIZE);

        }
    }

    /**
     * Moves the element in (x,y).
     * 
     * @param x New abscissa
     * @param y New ordinate
     */
    public void moveTo(double x, double y) {
        moveRel(x - xl, y - yl);
    }

    /**
     * Moves the element with the vector (dx,dy).
     * 
     * @param dx Relative horizontal move
     * @param dy Relative vertical move
     */
    public void moveRel(double dx, double dy) {
        xl = xl + dx;
        yl = yl + dy;
        xl = (xl < 0) ? 0 : xl;
        yl = (yl < 0) ? 0 : yl;
    }

    /**
     * Moves the element in (x,y) if it is selected.
     * 
     * @param x New abscissa
     * @param y New ordinate
     */
    public void dragTo(int x, int y) {
        if (selected) {
            moveTo(x, y);
        }
    }

}
