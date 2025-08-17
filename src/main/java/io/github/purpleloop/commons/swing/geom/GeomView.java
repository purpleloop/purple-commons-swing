package io.github.purpleloop.commons.swing.geom;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.github.purpleloop.commons.math.geom.CartesianLineEquation;
import io.github.purpleloop.commons.math.geom.Point2D;
import io.github.purpleloop.commons.math.geom.Segment2D;

/** A class for geometry rendering. */
public class GeomView {

    /** View width. */
    private int width;

    /** View height. */
    private int height;

    /** A graphic where to render. */
    private Graphics2D graphics;

    /**
     * Creates a view for rendering geometry items.
     * 
     * @param graphics graphics where to render
     * @param width width of the view
     * @param height height of the view
     */
    public GeomView(Graphics2D graphics, int width, int height) {
        this.graphics = graphics;
        this.width = width;
        this.height = height;
    }

    /** @return the view bounding box as a list of segments */
    public List<Segment2D> getViewBoundingBox() {
        Point2D a = new Point2D(0, 0);
        Point2D b = new Point2D(width - 1, 0);
        Point2D c = new Point2D(width - 1, height - 1);
        Point2D d = new Point2D(0, height - 1);
    
        List<Segment2D> rectangleSegments = new ArrayList<>();
        rectangleSegments.add(new Segment2D(a, b));
        rectangleSegments.add(new Segment2D(b, c));
        rectangleSegments.add(new Segment2D(c, d));
        rectangleSegments.add(new Segment2D(d, a));
        return rectangleSegments;
    }

    /** Draw a line given by it's equation.
     * @param line the line to draw
     */
    public void drawLine(CartesianLineEquation line) {
        drawLine(line, getViewBoundingBox());
    }
    
    /** Draw a line given by it's equation.
     * @param line the line to draw
     * @param viewBoudingBox the bounding box
     */
    private void drawLine(CartesianLineEquation line, List<Segment2D> viewBoudingBox) {
        final List<Point2D> intersections = new ArrayList<>();

        for (final Segment2D segment : viewBoudingBox) {

            final Optional<Point2D> interOpt = CartesianLineEquation.intersection(line, segment);

            if (interOpt.isPresent()) {
                intersections.add(interOpt.get());
            }

        }

        if (intersections.size() == 2) {
            final Point2D point2d1 = intersections.get(0);
            final Point2D point2d2 = intersections.get(1);
            graphics.drawLine((int) point2d1.x(), (int) point2d1.y(), (int) point2d2.x(),
                    (int) point2d2.y());
        }
    }

}
