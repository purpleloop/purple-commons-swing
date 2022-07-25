package io.github.purpleloop.commons.swing.sprites.model;

import java.awt.geom.Rectangle2D;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import io.github.purpleloop.commons.swing.sprites.Sprite;
import io.github.purpleloop.commons.swing.sprites.SpriteSet;

/** Describes single sprite. */
public class SingleSprite {

    /** DOM-XML element name for a single sprite. */
    public static final String SINGLE_SPRITE_ELEMENT = "singleSprite";

    /** DOM-XML attribute for id. */
    private static final String ID_ATTRIBUTE = "id";

    /** DOM-XML attribute for name. */
    private static final String NAME_ATTRIBUTE = "name";
    
    /** DOM-XML attribute for sprite abscissa. */
    private static final String Y_ATTRIBUTE = "y";

    /** DOM-XML attribute for sprite ordinate. */
    private static final String X_ATTRIBUTE = "x";

    /** DOM-XML attribute for sprite width. */
    private static final String WIDTH_ATTRIBUTE = "width";

    /** DOM-XML attribute for sprite height. */
    private static final String HEIGHT_ATTRIBUTE = "height";   

    /** The single sprite id. */
    private int id;

    /** The single sprite name. */
    private String name;

    /** The bounding rectangle of the sprite. */
    private Rectangle2D storage;

    /**
     * Constructor of a single sprite.
     * 
     * @param rectangle the bounding rectangle for the sprite
     */
    public SingleSprite(Rectangle2D rectangle) {
        this.storage = rectangle;
    }

    /**
     * Constructor of a single sprite.
     * 
     * @param singleSpriteElement the XML dom element source
     */
    public SingleSprite(Element singleSpriteElement) {

        this.id = Integer.parseInt(singleSpriteElement.getAttribute(ID_ATTRIBUTE));
        this.name = singleSpriteElement.getAttribute(NAME_ATTRIBUTE);

        int x = Integer.parseInt(singleSpriteElement.getAttribute(X_ATTRIBUTE));
        int y = Integer.parseInt(singleSpriteElement.getAttribute(Y_ATTRIBUTE));

        int w = Integer.parseInt(singleSpriteElement.getAttribute(WIDTH_ATTRIBUTE));
        int h = Integer.parseInt(singleSpriteElement.getAttribute(HEIGHT_ATTRIBUTE));

        this.storage = new Rectangle2D.Double(x, y, w, h);
    }

    /**
     * Registers the single sprite in the sprite set.
     * 
     * @param spriteSet the sprite set
     */
    public void registerSprite(SpriteSet spriteSet) {

        spriteSet.addSprite(new Sprite(name, (int) storage.getX(), (int) storage.getY(),
                (int) storage.getWidth(), (int) storage.getHeight()));
    }

    /**
     * Saves the single sprite information under the given element.
     * 
     * @param document the context document
     * @param singleSpritesElement the parent element
     */
    public void saveToXml(Document document, Element singleSpritesElement) {
        Element singleSpriteElement = document.createElement(SINGLE_SPRITE_ELEMENT);

        singleSpriteElement.setAttribute(ID_ATTRIBUTE, Integer.toString(id));

        singleSpriteElement.setAttribute(NAME_ATTRIBUTE, name);
        
        singleSpriteElement.setAttribute(X_ATTRIBUTE, Integer.toString((int) storage.getX()));
        singleSpriteElement.setAttribute(Y_ATTRIBUTE, Integer.toString((int) storage.getY()));
        singleSpriteElement.setAttribute(WIDTH_ATTRIBUTE,
                Integer.toString((int) storage.getWidth()));
        singleSpriteElement.setAttribute(HEIGHT_ATTRIBUTE,
                Integer.toString((int) storage.getHeight()));

        singleSpritesElement.appendChild(singleSpriteElement);
    }

}
