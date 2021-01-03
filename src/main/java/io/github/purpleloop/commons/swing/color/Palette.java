package io.github.purpleloop.commons.swing.color;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.util.Arrays;

import io.github.purpleloop.commons.exception.PurpleException;

/**
 * This class models a color palette and manage indexed color model (ICM)
 * conversions.
 */
public class Palette {

    /** Indicates a color search that has failed. */
    private static final int MISSING_COLOR = -1;

    /** Color palette. */
    private Color[] colors;

    /**
     * Creates a default 16 color palette, initially all black.
     */
    public Palette() {
        colors = new Color[16];
        Arrays.fill(colors, Color.BLACK);
    }

    /**
     * Creates a palette from an indexed color model (ICM).
     * 
     * @param icm the indexed color model (ICM)
     */
    public Palette(IndexColorModel icm) {

        // Allocates the palette with the same size
        colors = new Color[icm.getMapSize()];

        // Copy colors
        for (int index = 0; index < colors.length; index++) {
            colors[index] = new Color(icm.getRGB(index), true);
        }
    }

    /**
     * Extract a palette from an indexed color image.
     * 
     * @param bim buffered image
     * @return Color palette or null if it cannot be extracted
     * @throws PurpleException in case of problems
     */
    public static Palette getPaletteFromImage(BufferedImage bim) throws PurpleException {

        int type = bim.getType();

        if (type == BufferedImage.TYPE_BYTE_INDEXED || type == BufferedImage.TYPE_BYTE_BINARY) {

            IndexColorModel icm = (IndexColorModel) bim.getColorModel();

            return new Palette(icm);

        } else {
            throw new PurpleException("The image type '" + bim.getType()
                    + "' is not supported by color palette extraction.");
        }
    }

    /**
     * Creates a new indexed color model from the palette.
     * 
     * @return indexed color model
     */
    private IndexColorModel getColorModel() {

        // Arrays of the color model
        byte[] reds = new byte[colors.length];
        byte[] greens = new byte[colors.length];
        byte[] blues = new byte[colors.length];
        byte[] alphas = new byte[colors.length];
        Color color;

        for (int index = 0; index < colors.length; index++) {

            color = colors[index];
            reds[index] = (byte) color.getRed();
            greens[index] = (byte) color.getGreen();
            blues[index] = (byte) color.getBlue();
            alphas[index] = (byte) color.getAlpha();
        }

        return new IndexColorModel(8, colors.length, reds, greens, blues, alphas);
    }

    /**
     * Checks the validity of the index in the palette.
     * 
     * @param index index to check
     * @throws IndexOutOfBoundsException if index is invalid
     */
    private void checkIndex(int index) {
        if (index < 0 || index >= colors.length) {
            throw new IndexOutOfBoundsException(
                    "The color index " + index + " does not exists (max = " + colors.length + ").");
        }
    }

    /**
     * Gets a color of the palette by it's index.
     * 
     * @param index requested index
     * @return color at the requested index
     */
    public Color getColor(int index) {
        checkIndex(index);
        return colors[index];
    }

    /**
     * Changes the color of the palette at the given index.
     * 
     * @param index requested index
     * @param col new color
     */
    public void setColor(int index, Color col) {
        checkIndex(index);
        colors[index] = col;
    }

    /**
     * @return the number of colors in the palette
     */
    public int getSize() {
        return colors.length;
    }

    /**
     * Gets the index of the first color of the palette that is equal to the
     * given color.
     * 
     * Contract : (result == -1) || getColor(result).equals(requestedColor)
     *
     * @param requestedColor Requested color
     * @return index of the first color that is equal to the given color, or -1
     *         if it cannot be found
     */
    public int getIndexOfFirstSimilar(Color requestedColor) {

        if (requestedColor == null) {
            return MISSING_COLOR;
        }

        int testedIndex = 0;
        while (testedIndex < colors.length) {
            if (colors[testedIndex].equals(requestedColor)) {
                return testedIndex;
            }
            testedIndex++;
        }
        return MISSING_COLOR;
    }

    /**
     * Creates a new image from an existing one, using the current palette as color model.
     * 
     * @param source the source image
     * @return a new image with this palette and the same raster as the source image
     */
    public BufferedImage applyOnImage(BufferedImage source) {

        int typ = source.getType();

        BufferedImage derivatedImage = new BufferedImage(source.getWidth(), source.getHeight(), typ,
                getColorModel());

        source.copyData(derivatedImage.getRaster());

        return derivatedImage;
    }

}
