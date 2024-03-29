package io.github.purpleloop.commons.swing.sprites;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import io.github.purpleloop.commons.exception.PurpleException;
import io.github.purpleloop.commons.swing.image.ImageUtils;
import io.github.purpleloop.commons.swing.sprites.model.SpriteModel;

/** Tests on sprite sets. */
class SpriteSetTest {

    /** Number of hexa digits. */
    private static final int NUM_DIGITS = 11;

    /** A sprite width. */
    private static final int SPRITE_WIDTH = 5;

    /** A sprite height. */
    private static final int SPRITE_HEIGHT = 7;

    /** Tests property. */
    @Test
    void testProperty() throws PurpleException, URISyntaxException {

        SpriteModel spriteModel = new SpriteModel(
                resolveResourceWithFileName("sprite-sample-numbers.xml"));
        assertEquals("something", spriteModel.getProperty("foo"));
    }

    /**
     * End-to-end test with a sprite grid reading, rendering sprites and
     * comparing rasters.
     */
    @Test
    void testSampleNumbers() throws PurpleException, URISyntaxException {

        // Reads the expected resulting image
        BufferedImage expectedImage = ImageUtils
                .loadImageFromFile(resolveResourceWithFileName("sprite-sample-numbers-linear.png"));

        // Extracts the raster
        // Credits for raster compare :
        // https://stackoverflow.com/questions/31279009/testing-image-files-with-junit

        byte[] expectedArray = ((DataBufferByte) expectedImage.getData().getDataBuffer()).getData();

        // Loads the sprite with the sprite grid
        SpriteModel spriteModel = new SpriteModel(
                resolveResourceWithFileName("sprite-sample-numbers.xml"));
        SpriteSet spriteSet = spriteModel.getSpriteSet();

        // Creates an image with 11 hex digits in a single row
        BufferedImage actualImage = new BufferedImage(SPRITE_WIDTH * NUM_DIGITS, SPRITE_HEIGHT,
                BufferedImage.TYPE_4BYTE_ABGR);

        for (int i = 0; i < NUM_DIGITS; i++) {
            String spriteName = Integer.toString(i, 16).toUpperCase();
            spriteSet.putSprite(actualImage.getGraphics(), null, spriteName, i * SPRITE_WIDTH, 0);
        }

        // Extract the actual raster
        byte[] actualArray = ((DataBufferByte) actualImage.getData().getDataBuffer()).getData();

        // Compare rasters
        assertArrayEquals(expectedArray, actualArray);
    }

    /**
     * @param fileName the name of the resource file
     * @return resource file location from classPath
     */
    private String resolveResourceWithFileName(String fileName) throws URISyntaxException {
        ClassLoader classLoader = SpriteSetTest.class.getClassLoader();
        URL url = classLoader.getResource(fileName);
        return Paths.get(url.toURI()).toString();
    }

}
