package io.github.purpleloop.commons.swing.sprites.model;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import io.github.purpleloop.commons.swing.sprites.Sprite;
import io.github.purpleloop.commons.swing.sprites.SpriteSet;

/**
 * This class manages a sprite-set organized in serials.
 * 
 * Each serial of sprites has :
 * <ul>
 * <li>it's own index</li>
 * <li>a start location (x,y),</li>
 * <li>a sprite size (width, height), and</li>
 * <li> contains a given number of sprites.</li>
 * </ul>
 * 
 * <p>
 * Naming of sprites takes the index of the serial into account and the index of
 * the sprite in the series.
 * </p>
 */
public class SerialSpriteSetIndex implements IndexedSpriteSet {

    /**
     * Offset of the serial index in the sprite names. Example :
     * <ul>
     * <li>serie 1 starts at 0</li>
     * <li>serie 2 starts at 100</li>
     * </ul>
     */
    private static final int SERIAL_OFFSET_IN_NAME = 100;

    /** Logger of the class. */
    private static final Log LOG = LogFactory.getLog(SerialSpriteSetIndex.class);

    /** A serial sprite set. */
    public class SerialSpriteSet {

        /** The name of the element for a serial sprite set. */
        private static final String SERIAL_ELEMENT = "serial";

        /** DOM XML attribute name for the serial sprite set start abscissa. */
        private static final String SERIAL_ATTRIBUTE_X = "x";

        /** DOM XML attribute name for the serial sprite set start ordinate. */
        private static final String SERIAL_ATTRIBUTE_Y = "y";

        /** DOM XML attribute name for the serial sprite set width. */
        private static final String SERIAL_ATTRIBUTE_WIDTH = "width";

        /** DOM XML attribute name for the serial sprite set height. */
        private static final String SERIAL_ATTRIBUTE_HEIGHT = "height";

        /** DOM XML attribute name for the serial sprite set count. */
        private static final String SERIAL_ATTRIBUTE_COUNT = "count";

        /** Coordinate of the first sprite. */
        private Point startPoint;

        /** Width of each sprite. */
        private int width;

        /** Height of each sprite. */
        private int height;

        /** Number of sprites. */
        private int count;

        /**
         * Create a serial sprite set from an XML element.
         * 
         * @param serialElement XML element
         */
        public SerialSpriteSet(Element serialElement) {

            int x = Integer.parseInt(serialElement.getAttribute(SERIAL_ATTRIBUTE_X));
            int y = Integer.parseInt(serialElement.getAttribute(SERIAL_ATTRIBUTE_Y));

            startPoint.setLocation(x, y);

            this.width = Integer.parseInt(serialElement.getAttribute(SERIAL_ATTRIBUTE_WIDTH));
            this.height = Integer.parseInt(serialElement.getAttribute(SERIAL_ATTRIBUTE_HEIGHT));
            this.count = Integer.parseInt(serialElement.getAttribute(SERIAL_ATTRIBUTE_COUNT));
        }

        /** @return number of sprites */
        public int getCount() {
            return count;
        }

        public void saveToXml(Document doc, Element parent) {

            Element serialElement = doc.createElement(SERIAL_ELEMENT);
            parent.appendChild(serialElement);

            serialElement.setAttribute(SERIAL_ATTRIBUTE_X, Integer.toString(startPoint.x));
            serialElement.setAttribute(SERIAL_ATTRIBUTE_Y, Integer.toString(startPoint.y));

            serialElement.setAttribute(SERIAL_ATTRIBUTE_WIDTH, Integer.toString(width));
            serialElement.setAttribute(SERIAL_ATTRIBUTE_HEIGHT, Integer.toString(height));
            serialElement.setAttribute(SERIAL_ATTRIBUTE_COUNT, Integer.toString(count));
        }

    }

    /** Name of the XML element for the serial index. */
    public static final String SERIAL_INDEX_ELEMENT = "serialIndex";

    /** The serial sprite set. */
    private List<SerialSpriteSet> series;

    /** The index id. */
    private int id;

    /**
     * Creates an indexed serial sprite set.
     * 
     * @param id the sprite id
     */
    public SerialSpriteSetIndex(int id) {
        this.id = id;
        this.series = new ArrayList<>();
    }

    /**
     * Reads a serial sprite set from an XML element.
     * 
     * @param serialIndexElement the XML element
     */
    public void readFromXmlElement(Element serialIndexElement) {

        this.id = Integer.parseInt(serialIndexElement.getAttribute("id"));

        NodeList nodeList = serialIndexElement.getElementsByTagName(SerialSpriteSet.SERIAL_ELEMENT);
        for (int indexInSerie = 0; indexInSerie < nodeList.getLength(); indexInSerie++) {
            Element serialElement = (Element) nodeList.item(indexInSerie);
            series.add(new SerialSpriteSet(serialElement));
        }
    }

    @Override
    public Element saveToXml(Document doc, Element parent) {

        Element serialIndexElement = doc.createElement(SerialSpriteSetIndex.SERIAL_INDEX_ELEMENT);
        parent.appendChild(serialIndexElement);

        serialIndexElement.setAttribute("id", Integer.toString(id));

        for (SerialSpriteSet serie : series) {
            serie.saveToXml(doc, parent);
        }

        return serialIndexElement;
    }

    @Override
    public void registerSprites(SpriteSet spriteSet) {

        LOG.debug("Register sprites of the serial sprite set index");

        int indexOfSerie = 0;

        for (SerialSpriteSet spriteSerie : series) {
            for (int indexInSerie = 0; indexInSerie < spriteSerie.getCount(); indexInSerie++) {

                String spriteName = getSpriteNameForFrame(indexOfSerie, indexInSerie);

                spriteSet.addSprite(new Sprite(spriteName,
                        spriteSerie.startPoint.x + spriteSerie.width * indexInSerie,
                        spriteSerie.startPoint.y, spriteSerie.width, spriteSerie.height));
            }

            indexOfSerie++;
        }
    }

    /**
     * Get the sprite name for a given serial an frame indexes.
     * 
     * @param indexOfSerie index of the serial
     * @param indexInSerie index in the serial
     * @return the sprite name
     */
    private String getSpriteNameForFrame(int indexOfSerie, int indexInSerie) {
        return "sprite" + (indexOfSerie * SERIAL_OFFSET_IN_NAME + indexInSerie);
    }

    @Override
    public Optional<Integer> getIndexFor(Point point) {

        Rectangle checkingRectangle = new Rectangle();

        int indexOfSerie = 0;
        for (SerialSpriteSet serialSpriteSet : series) {

            checkingRectangle.setBounds(serialSpriteSet.startPoint.x, serialSpriteSet.startPoint.y,
                    serialSpriteSet.width, serialSpriteSet.height);
            for (int indexInSerie = 0; indexInSerie < serialSpriteSet.count; indexInSerie++) {

                if (checkingRectangle.contains(point)) {
                    return Optional.of(Integer.valueOf(indexOfSerie + indexInSerie));
                }
                checkingRectangle.translate(serialSpriteSet.width, 0);

            }

            indexOfSerie += SERIAL_OFFSET_IN_NAME;
        }

        // Point is out of the serial index structure
        return Optional.empty();

    }

    @Override
    public int getX(int indexSprite) {

        SerialSpriteSet indexOfSerie = getSerieForSpriteIndex(indexSprite);
        return indexOfSerie.startPoint.x
                + (indexSprite % SERIAL_OFFSET_IN_NAME) * indexOfSerie.width;
    }

    /**
     * Get the serial sprite set for the given index.
     * 
     * @param indexSprite sprite index
     * @return serial sprite set
     */
    private SerialSpriteSet getSerieForSpriteIndex(int indexSprite) {
        return series.get(indexSprite / SERIAL_OFFSET_IN_NAME);
    }

    @Override
    public int getY(int indexSprite) {
        return getSerieForSpriteIndex(indexSprite).startPoint.y;
    }

    @Override
    public int getWidth(int indexSprite) {
        return getSerieForSpriteIndex(indexSprite).width;
    }

    @Override
    public int getHeight(int indexSprite) {
        return getSerieForSpriteIndex(indexSprite).height;
    }

    @Override
    public void translate(int dx, int dy) {

        for (SerialSpriteSet serie : series) {
            serie.startPoint.translate(dx, dy);
        }
    }

}
