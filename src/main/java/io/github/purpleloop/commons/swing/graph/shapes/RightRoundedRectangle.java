package io.github.purpleloop.commons.swing.graph.shapes;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/** Shape of a rectangular shape with rounded angles. */
public class RightRoundedRectangle implements Shape {

    /** Bounding box. */
    private Rectangle bounds;

    /**
     * Creates a rounded rectangle.
     * 
     * @param x X coordinate of the rectangle
     * @param y Y coordinate of the rectangle
     * @param width width of the rectangle
     * @param height height of the rectangle
     * 
     */
    public RightRoundedRectangle(int x, int y, int width, int height) {
        bounds = new Rectangle(x, y, width, height);
    }

    @Override
    public Rectangle getBounds() {
        return bounds;
    }

    @Override
    public Rectangle2D getBounds2D() {
        return getBounds();
    }

    @Override
    public boolean contains(double x, double y) {
        return bounds.contains(x, y);
    }

    @Override
    public boolean contains(Point2D p) {
        return bounds.contains(p);
    }

    @Override
    public boolean intersects(double x, double y, double w, double h) {

        return bounds.intersects(x, y, w, h);
    }

    @Override
    public boolean intersects(Rectangle2D r) {
        return bounds.intersects(r);
    }

    @Override
    public boolean contains(double x, double y, double w, double h) {
        return bounds.contains(x, y, w, h);
    }

    @Override
    public boolean contains(Rectangle2D r) {
        return bounds.contains(r);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at) {
        return new RightRoundedRectanglePathIterator(this, at);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return getPathIterator(at);
    }

    /** @return x coordinate */
    public double getX() {
        return bounds.getX();
    }

    /** @return y coordinate */
    public double getY() {
        return bounds.getY();
    }

    /** @return width */
    public double getWidth() {
        return bounds.getWidth();
    }

    /** @return height */
    public double getHeight() {
        return bounds.getHeight();
    }

}
