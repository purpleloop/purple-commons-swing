package io.github.purpleloop.commons.swing;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.List;

import io.github.purpleloop.commons.util.TextUtils;

/** Represent a bounding box for a text (given as a set of lines). */
public class TextBox {

    /** Width of the box. */
    private int width;

    /** Height of the box. */
    private int height;

    /** Maximal bounding hieght for the text. */
    private int maximalHeight = 0;

    /** Lines of text. */
    private List<String> lines;

    /**
     * Textbox constructor.
     * 
     * @param text the text
     * @param font the font used for rendering
     * @param fontRenderContext the rendering context
     */
    public TextBox(String text, Font font, FontRenderContext fontRenderContext) {

        lines = TextUtils.getMultiLines(text, 22);

        width = 10;
        height = 15;

        Rectangle2D lineBoundingRect;
        int lineBoundingWidth;
        int lineBoundingHeight;

        // Computes the length for a line
        for (String currentLine : lines) {

            // Rendering rectangle
            lineBoundingRect = font.getStringBounds(currentLine, fontRenderContext);

            lineBoundingWidth = (int) lineBoundingRect.getWidth() + 10;

            if (width < lineBoundingWidth) {
                width = lineBoundingWidth;
            }

            lineBoundingHeight = (int) lineBoundingRect.getHeight();

            if (maximalHeight < lineBoundingHeight) {
                maximalHeight = lineBoundingHeight;
            }

            height += lineBoundingHeight;
        }

    }

    /**
     * Renders the text in (x,y).
     * 
     * @param g the graphics used to draw
     * @param x abscissa
     * @param y ordinate
     */
    public void renderText(Graphics2D g, double x, double y) {

        int xOffset = (int) (x - (width / 2.0)) + 5;
        int yOffset = (int) (y + (1.0 - lines.size()) * maximalHeight / 2.0) - 2;
        for (String currentLine : lines) {
            g.drawString(currentLine, xOffset, yOffset);
            yOffset += maximalHeight;
        }
    }

    /** @return Height of the box. */
    public int getHeight() {
        return height;
    }

    /** @param height Height of the box. */
    public void setHeight(int height) {
        this.height = height;
    }

    /** @return width of the box */
    public int getWidth() {
        return width;
    }

    /** @param width width of the box */
    public void setWidth(int width) {
        this.width = width;
    }

}
