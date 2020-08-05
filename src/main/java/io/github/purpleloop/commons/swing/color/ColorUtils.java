package io.github.purpleloop.commons.swing.color;

import java.awt.Color;

import io.github.purpleloop.commons.util.HexTools;

/** Utilities for AWT/Swing colors. */
public final class ColorUtils {
    
    /**
     * Creates a AWT color from an hexadecimal RGB/RGBA value.
     * 
     * Decomposes the hexadecimal string, 2 digits for red, green, blue and a possible alpha (transparency). 
     * 
     * @param hexStr hexadecimal string
     * @return AWT Color
     */
    public static Color awtRGBColorFromHexString(String hexStr) {

        // Two digits per color component (RGB ou RGBA)
        int numComponents = hexStr.length() / 2;

        if (numComponents != 3 && numComponents != 4) {
            throw new IllegalArgumentException("Invalid color componants for RGB/RGBA " + hexStr);
        }

        String hexStrUpperCased = hexStr.toUpperCase();
        float[] colorComponents = new float[numComponents];
        for (int componentIndex = 0; componentIndex < numComponents; componentIndex++) {
            int hi = HexTools.HEXA.indexOf(hexStrUpperCased.charAt(2 * componentIndex));
            int lo = HexTools.HEXA.indexOf(hexStrUpperCased.charAt(2 * componentIndex + 1));
            colorComponents[componentIndex] = (float) ((hi * 16 + lo) / 255.0);
        }

        if (numComponents == 3) {
            return new Color(colorComponents[0], colorComponents[1], colorComponents[2]);
        } else {
            return new Color(colorComponents[0], colorComponents[1], colorComponents[2], colorComponents[3]);
        }

    }
    
}
