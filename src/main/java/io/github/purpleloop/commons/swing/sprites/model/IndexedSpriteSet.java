package io.github.purpleloop.commons.swing.sprites.model;

import java.awt.Point;
import java.util.Optional;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import io.github.purpleloop.commons.swing.sprites.SpriteSet;

/**
 * Interface for an indexed sprite set. Each sprite can be accessed by a unique
 * numerical index value.
 */
public interface IndexedSpriteSet {

    /**
     * Register the sprites in the given sprite set, according to the index
     * structure.
     * 
     * @param spriteSet the sprite set in which sprites will be registered
     */
    void registerSprites(SpriteSet spriteSet);

    /**
     * Get the index value for the sprite at a given 2D point.
     * 
     * @param point the point to test
     * @return optional of the index value of the sprite, if there is one at the
     *         given point
     */
    Optional<Integer> getIndexFor(Point point);

    /**
     * @param indexValue the sprite index value
     * @return abscissa of the upper left corner of the sprite with the given
     *         index
     */
    int getX(int indexValue);

    /**
     * @param indexValue the sprite index value
     * @return ordinate of the upper left corner of the sprite with the given
     *         index
     */
    int getY(int indexValue);

    /**
     * @param indexValue the sprite index value
     * @return width of the sprite
     */
    int getWidth(int indexValue);

    /**
     * @param indexValue the sprite index value
     * @return height of the sprite
     */
    int getHeight(int indexValue);

    /**
     * Translate the indexed structure by a (dx, dy) vector.
     * 
     * @param dx horizontal part of the movement
     * @param dy vertical part of the movement
     */
    void translate(int dx, int dy);

    /**
     * Save the sprite index as an XML element.
     * 
     * @param doc the XML owning document
     * @param parent XML parent element
     * @return the element representing the sprite index
     */
    Element saveToXml(Document doc, Element parent);

}
