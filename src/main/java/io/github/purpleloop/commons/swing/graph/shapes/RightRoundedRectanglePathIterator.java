package io.github.purpleloop.commons.swing.graph.shapes;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.NoSuchElementException;

/** A path iterator for rounded rectangles. */
public class RightRoundedRectanglePathIterator implements PathIterator {

    /** Last part, need to close path. */
    private static final int FINISH_VALUE = 4;

    /** Done value (also used for negative sizes). */
    private static final int DONE_VALUE = 5;

    /**
     * Amount of rectangle side for which we make a linear drawing before
     * starting the cubic curve.
     */
    private static final double LINEAR_WIDTH = 0.75;

    /** X coordinate. */
    private double x;

    /** Y coordinate. */
    private double y;

    /** Width. */
    private double w;

    /** Height. */
    private double h;

    /** Affine transformation. */
    private AffineTransform affineTransform;

    /** Index of path iteration. */
    private int index;

    /**
     * Creates a rounded rectangle path iterator.
     * 
     * @param rect the rounded rectangle
     * @param affineTransform an affine transformation
     */
    public RightRoundedRectanglePathIterator(RightRoundedRectangle rect,
            AffineTransform affineTransform) {
        this.x = rect.getX();
        this.y = rect.getY();
        this.w = rect.getWidth();
        this.h = rect.getHeight();
        this.affineTransform = affineTransform;

        if (w < 0 || h < 0) {
            index = DONE_VALUE;
        }
    }

    @Override
    public int getWindingRule() {
        return WIND_NON_ZERO;
    }

    @Override
    public boolean isDone() {
        return index >= DONE_VALUE;
    }

    @Override
    public void next() {
        index++;
    }

    @Override
    public int currentSegment(float[] coords) {

        if (isDone()) {
            // Should not be called if path is over.
            throw new NoSuchElementException(" iterator out of bounds");
        }

        if (index == FINISH_VALUE) {
            // Terminate
            return SEG_CLOSE;
        }

        // Straight parts
        if ((index < 2) || (index > 2)) {

            coords[0] = (float) x;
            coords[1] = (float) y;
            if (index == 1) {
                coords[0] += (float) w * LINEAR_WIDTH;
            }
            if (index == 3) {
                coords[1] += (float) h;
            }
            if (affineTransform != null) {
                affineTransform.transform(coords, 0, coords, 0, 1);
            }
            return (index == 0 ? SEG_MOVETO : SEG_LINETO);

        }

        // Cubic curve part to get rounded section.
        if (index == 2) {
            coords[0] = (float) (x + 1.0 * w);
            coords[1] = (float) (y + 0.0 * h);
            coords[2] = (float) (x + 1.0 * w);
            coords[3] = (float) (y + 1.0 * h);
            coords[4] = (float) (x + 0.75 * w);
            coords[5] = (float) (y + 1.0 * h);

            return SEG_CUBICTO;
        }

        throw new NoSuchElementException("invalid index for iterator");
    }

    @Override
    public int currentSegment(double[] coords) {

        if (isDone()) {
            // Should not be called if path is over.
            throw new NoSuchElementException(" iterator out of bounds");
        }

        if (index == FINISH_VALUE) {
            // Terminate
            return SEG_CLOSE;
        }

        // Straight parts
        if ((index < 2) || (index > 2)) {

            coords[0] = x;
            coords[1] = y;
            if (index == 1) {
                coords[0] += w * LINEAR_WIDTH;
            }
            if (index == 3) {
                coords[1] += h;
            }
            if (affineTransform != null) {
                affineTransform.transform(coords, 0, coords, 0, 1);
            }
            return (index == 0 ? SEG_MOVETO : SEG_LINETO);

        }

        // Cubic curve part to get rounded section.
        if (index == 2) {
            coords[0] = (x + 1.0 * w);
            coords[1] = (y + 0.0 * h);
            coords[2] = (x + 1.0 * w);
            coords[3] = (y + 1.0 * h);
            coords[4] = (x + 0.75 * w);
            coords[5] = (y + 1.0 * h);

            return SEG_CUBICTO;
        }

        throw new NoSuchElementException("invalid index for iterator");
    }

}
