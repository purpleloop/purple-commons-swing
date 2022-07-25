package io.github.purpleloop.commons.swing.sprites;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import io.github.purpleloop.commons.exception.PurpleException;
import io.github.purpleloop.commons.swing.image.ImageUtils;
import io.github.purpleloop.commons.swing.sprites.exception.SpriteRenderingException;

/**
 * This class defines a sprite set that can be used for animation purposes. A
 * sprite is a named image chunk.
 */
public class SpriteSet {

    /** Logger of the class. */
    private static final Log LOG = LogFactory.getLog(SpriteSet.class);

    /** The default zoom factor. */
    private static final int DEFAULT_ZOOM_FACTOR = 1;

    /** The image containing the sprites. */
    private Image source;

    /** Sprite names mapping. */
    private Map<String, Sprite> sprites;

    /** Zoom factor for rendering. */
    double zoomFactor;

    /**
     * Creates a sprite set with the file whose name is given as source.
     * 
     * @param spriteFileName Sprite file name
     * @throws PurpleException in case of problems during creation
     */
    public SpriteSet(String spriteFileName) throws PurpleException {
        this(ImageUtils.loadImageFromFile(spriteFileName));
    }

    /**
     * Creates a sprite set with the given image as source.
     * 
     * @param spriteSource Sprite source
     */
    public SpriteSet(Image spriteSource) {
        source = spriteSource;
        sprites = new HashMap<>();
        zoomFactor = DEFAULT_ZOOM_FACTOR;
    }

    /** Reset the sprite registry. */
    public void resetRegistry() {
        LOG.debug("Clearing the sprite registry");
        sprites.clear();
    }

    /**
     * Adds a new sprite to the SpriteSet.
     * 
     * @param sprite Sprite
     * 
     */
    public void addSprite(Sprite sprite) {

        LOG.debug("Registering sprite " + sprite.toString());
        sprites.put(sprite.getName(), sprite);
    }

    /**
     * Adds a new sprite to the SpriteSet.
     * 
     * @param name Sprite name
     * @param sprite Sprite
     * 
     * @deprecated for existing unnamed sprite compatibility
     */
    @Deprecated(forRemoval = true)
    public void addSprite(String name, Sprite sprite) {
        sprite.setName(name);
        addSprite(sprite);
    }

    /**
     * Sets the zoom factor.
     * 
     * @param zoom Zoom factor to use
     */
    public void setZoomFactor(double zoom) {
        zoomFactor = zoom;
    }

    /**
     * Gives the collection of names of all registered sprites.
     * 
     * @return collection of sprite names
     */
    public Collection<String> getSpritesNames() {
        return sprites.keySet();
    }

    /** @return the image source or this sprite set */
    public Image getSourceImage() {
        return source;
    }

    /**
     * Renders the requested sprite, given by it's name, on given coordinates.
     * 
     * @param canvas Graphic canvas where to do the rendering
     * @param spriteName name of the sprite to render
     * @param imageObserver ImageObserver to notify once drawing has been done
     * @param x horizontal location
     * @param y vertical location
     */
    public void putSprite(Graphics canvas, ImageObserver imageObserver, String spriteName, int x,
            int y) {
        Sprite spriteToRender = sprites.get(spriteName);
        if (spriteToRender == null) {
            throw new SpriteRenderingException(
                    "There is no sprite named '" + spriteName + "' in this SpriteSet.");
        }

        canvas.drawImage(source, x, y, (int) (x + spriteToRender.getWidth() * zoomFactor),
                (int) (y + spriteToRender.getHeight() * zoomFactor), spriteToRender.ox,
                spriteToRender.oy, spriteToRender.ox + spriteToRender.getWidth(),
                spriteToRender.oy + spriteToRender.getHeight(), imageObserver);
    }

    /**
     * Gets a sprite by it's name.
     * 
     * @param spriteName the sprite name
     * @return the requested sprite if it exists, null otherwise
     */
    public Sprite getSprite(String spriteName) {
        return sprites.get(spriteName);
    }

}
