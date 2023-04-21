package io.github.purpleloop.commons.swing.color;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;

import org.junit.jupiter.api.Test;

/** Tests for AWT/Swing colors utilities. */
class ColorUtilsTest {

    /** Tests hexa color conversion. */
    @Test
    void testAwtRGBColorFromHexStringREB() {
        assertEquals(Color.BLACK, ColorUtils.awtRGBColorFromHexString("000000"));
        assertEquals(Color.RED, ColorUtils.awtRGBColorFromHexString("FF0000"));
        assertEquals(Color.GREEN, ColorUtils.awtRGBColorFromHexString("00FF00"));
        assertEquals(Color.BLUE, ColorUtils.awtRGBColorFromHexString("0000FF"));
        assertEquals(Color.WHITE, ColorUtils.awtRGBColorFromHexString("FFFFFF"));
        assertEquals(new Color(0.5f, 0.5f, 0.5f, 0.5f),
                ColorUtils.awtRGBColorFromHexString("80808080"));
    }

}
