package io.github.purpleloop.commons.swing.color;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.Color;

import org.junit.jupiter.api.Test;

/** Tests for AWT/Swing colors utilities. */
class ColorUtilsTest {

    /** RGB Hex color for white. */
    private static final String WHITE_RGB_HEX = "FFFFFF";

    /** RGB Hex color for blue. */
    private static final String BLUE_RGB_HEX = "0000FF";

    /** RGB Hex color for green. */
    private static final String GREEN_RGB_HEX = "00FF00";

    /** RGB Hex color for red. */
    private static final String RED_RGB_HEX = "FF0000";

    /** RGB Hex color for . */
    private static final String BLACK_RGB_HEX = "000000";

    /** Hex alpha value for OPAQUE. */
    private static final String OPAQUE_ALPHA_HEX = "FF";

    /** Grey half opaque Color. */
    private static final Color GREY_HALF_OPAQUE_COLOR = new Color(0.5f, 0.5f, 0.5f, 0.5f);

    /** RGBA Hex color for grey half opaque. */
    private static final String GREY_HALF_OPAQUE_RGBA_HEX = "80808080";

    /** Tests hexa color conversion. */
    @Test
    void testAwtRGBColorFromHexStringRGB() {
        assertEquals(Color.BLACK, ColorUtils.awtRGBColorFromHexString(BLACK_RGB_HEX));
        assertEquals(Color.RED, ColorUtils.awtRGBColorFromHexString(RED_RGB_HEX));
        assertEquals(Color.GREEN, ColorUtils.awtRGBColorFromHexString(GREEN_RGB_HEX));
        assertEquals(Color.BLUE, ColorUtils.awtRGBColorFromHexString(BLUE_RGB_HEX));
        assertEquals(Color.WHITE, ColorUtils.awtRGBColorFromHexString(WHITE_RGB_HEX));
        assertEquals(GREY_HALF_OPAQUE_COLOR,
                ColorUtils.awtRGBColorFromHexString(GREY_HALF_OPAQUE_RGBA_HEX));
    }

    /** Tests color hexa conversion. */
    @Test
    void testHexStringRGBFromAwtRGBColor() {
        assertEquals(BLACK_RGB_HEX + OPAQUE_ALPHA_HEX, ColorUtils.getRGBHexString(Color.BLACK));
        assertEquals(RED_RGB_HEX + OPAQUE_ALPHA_HEX, ColorUtils.getRGBHexString(Color.RED));
        assertEquals(GREEN_RGB_HEX + OPAQUE_ALPHA_HEX, ColorUtils.getRGBHexString(Color.GREEN));
        assertEquals(BLUE_RGB_HEX + OPAQUE_ALPHA_HEX, ColorUtils.getRGBHexString(Color.BLUE));
        assertEquals(WHITE_RGB_HEX + OPAQUE_ALPHA_HEX, ColorUtils.getRGBHexString(Color.WHITE));
        assertEquals(GREY_HALF_OPAQUE_RGBA_HEX, ColorUtils.getRGBHexString(GREY_HALF_OPAQUE_COLOR));
    }

}
