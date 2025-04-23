package io.github.purpleloop.commons.swing.graph;

import java.awt.Graphics2D;
import java.util.BitSet;

/**
 * This class implements spline curves.
 * 
 * A spline is hold by three points :
 * <UL>
 * <LI>The A(x1,y1) point holds the start of the curve</LI>
 * <LI>The B(x2,y2) point holds the middle of the curve</LI>
 * <LI>The C(x3,y3) point holds the end of the curve</LI>
 * </UL>
 * 
 * The spline starts from I, middle of the [AB] segment, go in a tangent way to
 * B and arrives pour in J, middle of the [BC] segment, in a tangent way in
 * direction of C.
 */
public final class Spline {

    /** Draw the start part of the spline. */
    private static final int DRAW_START = 1;

    /** Draw the end part of the spline. */
    private static final int DRAW_END = 2;

    /** Draw the arrow on the spline. */
    private static final int DRAW_ARROW = 3;

    /** Rendering / test steps. */
    private static final double STEP = 15;

    private Spline() {
        // empty constructor for final class
    }

    /**
     * This method draws a spline curve on the given graphics.
     * 
     * Drawing options :
     * <UL>
     * <LI>bit 1 -> we draw the [AI] segment</LI>
     * <LI>bit 2 -> we draw the [JB] segment</LI>
     * <LI>bit 4 -> we draw the arrow in J heading in direction of B.</LI>
     * </UL>
     * 
     * @param g graphics where to draw
     * @param x1 Abscissa of the start point A
     * @param y1 Ordinate of the start point A
     * @param x2 Abscissa of the middle hold point B
     * @param y2 Ordinate of the middle hold point B
     * @param x3 Abscissa of the end point C
     * @param y3 Ordinate of the end point C
     * 
     * @param options bitset with drawing options
     */
    public static void draw(Graphics2D g, double x1, double y1, double x2, double y2, double x3,
            double y3, BitSet options) {

        int ospx;
        int ospy;
        int spx;
        int spy;

        double arrowAngle, tau, ttau, dx, dy;
        double ouv = Math.PI / 6;

        ospx = (int) ((x1 + x2) / 2);
        ospy = (int) ((y1 + y2) / 2);
        spx = ospx;
        spy = ospy;

        if (options.get(DRAW_START)) {
            g.drawLine((int) x1, (int) y1, spx, spy);
        }

        for (ttau = 0; ttau <= STEP; ttau++) {
            tau = ttau / STEP;
            spx = (int) (tau * tau / 2 * (x1 - 2 * x2 + x3) + tau * (x2 - x1) + 0.5 * (x1 + x2));
            spy = (int) (tau * tau / 2 * (y1 - 2 * y2 + y3) + tau * (y2 - y1) + 0.5 * (y1 + y2));
            g.drawLine(ospx, ospy, spx, spy);
            ospx = spx;
            ospy = spy;
        }

        if (options.get(DRAW_END)) {
            g.drawLine(spx, spy, (int) x3, (int) y3);
        }
        // draws the arrow
        if (options.get(DRAW_ARROW)) {

            dx = x3 - spx;
            dy = -(y3 - spy);

            // Angle for the arrow
            arrowAngle = (dx != 0) ? Math.atan(dy / dx) : (dy < 0 ? -(Math.PI / 2) : (Math.PI / 2));
            arrowAngle = (dx < 0) ? arrowAngle + Math.PI : arrowAngle;

            // Draws tha arrow in direction A to C
            g.drawLine(spx, spy, (int) (spx + 10 * Math.cos(arrowAngle - Math.PI + ouv)),
                    (int) (spy - 10 * Math.sin(arrowAngle - Math.PI + ouv)));
            g.drawLine(spx, spy, (int) (spx + 10 * Math.cos(arrowAngle + Math.PI - ouv)),
                    (int) (spy - 10 * Math.sin(arrowAngle + Math.PI - ouv)));

        }
    }

    /**
     * This method tests if a given M (x,y) point is "on" the spline with a
     * given precision.
     * 
     * Warning, here we ONLY test the proximity to approximation points
     * (spx,spy). and not the segments between these points.
     * 
     * @param x Tested abscissa
     * @param y Tested ordinate
     * @param x1 Abscissa of the start point A
     * @param y1 Ordinate of the start point A
     * @param x2 Abscissa of the middle hold point B
     * @param y2 Ordinate of the middle hold point B
     * @param x3 Abscissa of the end point C
     * @param y3 Ordinate of the end point C
     * @param eps precision
     * @return true if the point is approximatively on the spline, false
     *         otherwise
     */
    public static boolean test(double x, double y, double x1, double y1, double x2, double y2,
            double x3, double y3, double eps) {

        boolean found = false;
        int spx;
        int spy;
        double tau;
        double ttau;

        for (ttau = 0; (ttau <= STEP) && (!found); ttau++) {
            tau = ttau / STEP;
            spx = (int) (tau * tau / 2 * (x1 - 2 * x2 + x3) + tau * (x2 - x1) + 0.5 * (x1 + x2));
            spy = (int) (tau * tau / 2 * (y1 - 2 * y2 + y3) + tau * (y2 - y1) + 0.5 * (y1 + y2));
            found = (Math.abs(x - spx) <= eps) && (Math.abs(y - spy) <= eps);
        }

        return found;
    }

}
