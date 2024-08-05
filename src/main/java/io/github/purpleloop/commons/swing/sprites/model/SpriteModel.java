package io.github.purpleloop.commons.swing.sprites.model;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import io.github.purpleloop.commons.exception.PurpleException;
import io.github.purpleloop.commons.swing.sprites.SpriteSet;
import io.github.purpleloop.commons.util.PathMode;
import io.github.purpleloop.commons.xml.XMLTools;

/**
 * Describes a sprite model.
 * 
 * This is the combination of a source image with various indexes or single
 * sprites used to define how sprites are organized and can be obtained from the
 * source image.
 */
public class SpriteModel {

    /** DOM-XML element name for sprite set descriptor. */
    private static final String SPRITES_SET_ELEMENT = "spriteSet";

    /** DOM-XML attribute name for the next id counter. */
    private static final String NEXT_ID_ATTRIBUTE = "nextId";

    /** DOM-XML element name for sprite set properties. */
    private static final String PROPERTIES_ELEMENT = "properties";

    /** DOM-XML element name for sprite source image. */
    private static final String SOURCE_IMAGE_ELEMENT = "sourceImage";

    /** DOM-XML attribute name for path on a source image. */
    private static final String SOURCE_IMAGE_PATH_ATTRIBUTE = "path";

    /** DOM-XML attribute name for path mode on a source image. */
    private static final String SOURCE_IMAGE_PATH_MODE_ATTRIBUTE = "pathMode";

    /** DOM-XML element name for sprite indexes. */
    private static final String INDEXES_ELEMENT = "indexes";

    /** DOM-XML element name for single sprites container. */
    private static final String SINGLE_SPRITES_ELEMENT = "singleSprites";

    /** Class logger. */
    public static final Log LOG = LogFactory.getLog(SpriteModel.class);

    /** A global counter for attributing ids to the sprite model elements. */
    private int nextId;

    /** The persistent part of the model (file). */
    private File modelFile;

    /** Path of the source image. */
    private String sourceImagePath;

    /** The source image path mode. */
    private PathMode sourceImagePathMode;

    /** The indexed sprite sets. */
    private List<IndexedSpriteSet> indexes;

    /** The sprite set. */
    private SpriteSet spriteSet;

    /** Single sprites. */
    private List<SingleSprite> singleSprites;

    /** Properties for sprite set use. */
    private Properties properties;

    /**
     * Constructor of the sprite model.
     * 
     * @param fileName the file name
     * @throws PurpleException in case of problem
     */
    public SpriteModel(String fileName) throws PurpleException {

        nextId = 0;
        properties = new Properties();
        indexes = new ArrayList<>();
        singleSprites = new ArrayList<>();

        if (fileName.endsWith(".xml")) {

            loadSpriteModelFromXMLFile(new File(fileName));

        } else {

            // Creation of the sprite model with a single image
            try {
                spriteSet = new SpriteSet(fileName);
                sourceImagePath = fileName;
                sourceImagePathMode = PathMode.ABSOLUTE;

            } catch (PurpleException e) {
                LOG.error("Error while creating the sprite set model", e);
            }

        }

    }

    /**
     * Get and increment the next id for elements of this model.
     * 
     * @return the next id
     */
    public int getNextId() {
        return nextId++;
    }

    /**
     * Loads the sprite model from an XML file.
     * 
     * @param file the sprite model description
     * @throws PurpleException in case of problem
     */
    private void loadSpriteModelFromXMLFile(File file) throws PurpleException {

        this.modelFile = file;

        try {
            Document doc = XMLTools.getDocument(file);
            Element spriteDescriptorElement = doc.getDocumentElement();

            String nextIdStr = spriteDescriptorElement.getAttribute(NEXT_ID_ATTRIBUTE);
            this.nextId = Integer.parseInt(nextIdStr);

            // Loads properties
            Optional<Element> propertiesElementsOptional = XMLTools
                    .getUniqueChildElement(spriteDescriptorElement, PROPERTIES_ELEMENT);
            XMLTools.getChildElementsStream(
                    propertiesElementsOptional.orElseThrow(() -> new PurpleException(
                            "Missing 'properties' element in the XML sprite set descriptor")))
                    .forEach(propertyElement -> {
                        String textContent = propertyElement.getTextContent();
                        properties.put(propertyElement.getNodeName(), textContent);
                    });

            // Loads sprite descriptions

            Element sourceImageElement = XMLTools
                    .getUniqueChildElement(spriteDescriptorElement, SOURCE_IMAGE_ELEMENT)
                    .orElseThrow(() -> new PurpleException(
                            "Missing 'sourceImage' element in the XML sprite set descriptor"));

            this.sourceImagePathMode = PathMode
                    .valueOf(sourceImageElement.getAttribute(SOURCE_IMAGE_PATH_MODE_ATTRIBUTE));

            this.sourceImagePath = sourceImageElement.getAttribute(SOURCE_IMAGE_PATH_ATTRIBUTE);

            LOG.info("Loading sprites from the source image " + sourceImagePath);

            this.spriteSet = new SpriteSet(resolvePath(sourceImagePathMode, sourceImagePath, file));

            Optional<Element> indexesElementOptional = XMLTools
                    .getUniqueChildElement(spriteDescriptorElement, INDEXES_ELEMENT);

            if (indexesElementOptional.isPresent()) {

                for (Element indexElement : XMLTools
                        .getChildElements(indexesElementOptional.get())) {

                    // Each index can be a grid or a serial
                    String indexType = indexElement.getTagName();

                    if (indexType.equals(SpriteGridIndex.GRID_INDEX_ELEMENT)) {
                        indexes.add(readGridSpriteSetIndex(indexElement));

                    } else if (indexType.equals(SerialSpriteSetIndex.SERIAL_INDEX_ELEMENT)) {
                        indexes.add(readSerialSpriteSet(indexElement));
                    }
                }

            }

            Optional<Element> singleSpritesElementOptional = XMLTools
                    .getUniqueChildElement(spriteDescriptorElement, SINGLE_SPRITES_ELEMENT);

            if (singleSpritesElementOptional.isPresent()) {

                for (Element indexElement : XMLTools.getChildElements(
                        singleSpritesElementOptional.get(), SingleSprite.SINGLE_SPRITE_ELEMENT)) {
                    singleSprites.add(readSingleSprite(indexElement));
                }
            }

        } catch (PurpleException e) {
            LOG.error("Error while loading sprite model from file " + file.getAbsolutePath(), e);
            throw new PurpleException("Error while reading the sprite model.", e);
        }

        registerSprites();

    }

    /**
     * Resolve the given path according to the specified pathMode.
     * 
     * @param pathMode the pathMode to use
     * @param pathToResolve the path to resolve
     * @param file the reference file
     * @return the resolved path
     * @throws PurpleException in case of problems
     */
    private String resolvePath(PathMode pathMode, String pathToResolve, File file)
            throws PurpleException {

        switch (pathMode) {

        case RELATIVE:

            // Constructs a relative path
            Path referencePath = Paths.get(file.getAbsolutePath());
            Path parentPath = referencePath.getParent();
            Path targetPath = parentPath.resolve(sourceImagePath);
            return targetPath.toFile().getAbsolutePath();

        case CLASSPATH:

            // Load resource from class loader
            ClassLoader classLoader = SpriteModel.class.getClassLoader();
            URL url = classLoader.getResource(sourceImagePath);
            try {
                return Paths.get(url.toURI()).toFile().getAbsolutePath();
            } catch (URISyntaxException e) {
                throw new PurpleException(
                        "Failed to resolve resource from the classpath " + sourceImagePath, e);
            }

        case ABSOLUTE:
        default:
            // Path is absolute - nothing to do
            return pathToResolve;
        }
    }

    /**
     * Read a serial sprite set from an XML element.
     * 
     * @param serialIndexElement the XML element
     * @return the serial sprite set index
     */
    private SerialSpriteSetIndex readSerialSpriteSet(Element serialIndexElement) {

        SerialSpriteSetIndex serialIndex = new SerialSpriteSetIndex(-1);
        serialIndex.readFromXmlElement(serialIndexElement);
        return serialIndex;
    }

    /**
     * Read a grid sprite set from an XML element.
     * 
     * @param gridElement the XML element
     * @return the grid sprite set index
     */
    private SpriteGridIndex readGridSpriteSetIndex(Element gridElement) {

        SpriteGridIndex gridIndex = new SpriteGridIndex(-1);
        gridIndex.readFromXmlElement(gridElement);
        return gridIndex;
    }

    /**
     * Read a single sprite set from an XML element.
     * 
     * @param singleSpriteElement the XML element
     * @return the single sprite
     */
    private SingleSprite readSingleSprite(Element singleSpriteElement) {
        return new SingleSprite(singleSpriteElement);
    }

    /** Register sprites for each index of the model. */
    public void registerSprites() {

        LOG.info("Register all sprites");
        spriteSet.resetRegistry();
        for (IndexedSpriteSet indexToRegister : indexes) {
            indexToRegister.registerSprites(spriteSet);
        }

        for (SingleSprite singleSprite : singleSprites) {
            singleSprite.registerSprite(spriteSet);
        }
    }

    /**
     * Saves the sprite model to a given file.
     * 
     * @param file the file to create
     */
    public void saveToFile(File file) throws PurpleException {

        Document document = XMLTools.createDocument();

        Element spriteDescriptorElement = document.createElement(SPRITES_SET_ELEMENT);
        document.appendChild(spriteDescriptorElement);

        spriteDescriptorElement.setAttribute(NEXT_ID_ATTRIBUTE, Integer.toString(nextId));

        // Properties
        Element propertiesElement = document.createElement(PROPERTIES_ELEMENT);
        spriteDescriptorElement.appendChild(propertiesElement);

        for (Entry<Object, Object> property : properties.entrySet()) {
            Element propertyElement = document.createElement((String) property.getKey());
            propertiesElement.appendChild(propertyElement);
            propertyElement.setTextContent((String) property.getValue());
        }

        Element sourceImageElement = document.createElement(SOURCE_IMAGE_ELEMENT);
        spriteDescriptorElement.appendChild(sourceImageElement);

        sourceImageElement.setAttribute(SOURCE_IMAGE_PATH_ATTRIBUTE, sourceImagePath);
        sourceImageElement.setAttribute(SOURCE_IMAGE_PATH_MODE_ATTRIBUTE,
                sourceImagePathMode.name());

        Element indexesElement = document.createElement(INDEXES_ELEMENT);
        spriteDescriptorElement.appendChild(indexesElement);

        for (IndexedSpriteSet indexToSave : indexes) {
            indexToSave.saveToXml(document, indexesElement);
        }

        Element singleSpritesElement = document.createElement(SINGLE_SPRITES_ELEMENT);
        for (SingleSprite singleSprite : singleSprites) {
            singleSprite.saveToXml(document, singleSpritesElement);
        }

        XMLTools.writeXmlFile(document, file, XMLTools.DEFAULT_UTF8_OUTPUT);

    }

    /** @return the image used for the sprite model */
    public Image getImage() {
        return spriteSet.getSourceImage();
    }

    /**
     * @param key property key
     * @return property value
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * @return the name of the file used in the model
     */
    public String getFileName() {
        return modelFile.getAbsolutePath();
    }

    /** @return the single sprites of the model */
    public List<SingleSprite> getSingleSprites() {
        return singleSprites;
    }

    /**
     * Adds a rectangle to the model.
     * 
     * @param rect the rectangle to add
     */
    public void addRectangle(Rectangle2D rect) {
        LOG.debug("Adding a single sprite bounded to " + rect);
        singleSprites.add(new SingleSprite(rect));
    }

    /**
     * Removes a single sprite.
     * 
     * @param spriteToRemove the sprite to remove
     */
    public void removeSingleSprite(SingleSprite spriteToRemove) {
        LOG.debug("Removing single sprite " + spriteToRemove);
        singleSprites.remove(spriteToRemove);
    }

    /**
     * @return the indexes used by the model
     */
    public List<IndexedSpriteSet> getIndexes() {
        return indexes;
    }

    /**
     * Add an index to the model.
     * 
     * @param indexedSpriteSet the index to add
     */
    public void addIndex(IndexedSpriteSet indexedSpriteSet) {
        LOG.debug("Adding indexed sprite set " + indexedSpriteSet);
        indexes.add(indexedSpriteSet);
    }

    /**
     * Removes the given index.
     * 
     * @param indexToRemove the index to remove
     */
    public void removeIndex(IndexedSpriteSet indexToRemove) {
        LOG.debug("Removing indexed sprite set " + indexToRemove);
        indexes.remove(indexToRemove);
    }

    /**
     * Put the sprite at the given location.
     * 
     * @param canvas the graphics on which to paint
     * @param iob the image observer
     * @param spriteName the name of the sprite to paint
     * @param x abscissa
     * @param y ordinate
     */
    public void putSprite(Graphics canvas, ImageObserver iob, String spriteName, int x, int y) {
        this.spriteSet.putSprite(canvas, iob, spriteName, x, y);
    }

    /**
     * Put the sprite for the requested index.
     * 
     * @param canvas the graphics on which to paint
     * @param iob the image observer
     * @param index index do render
     * @param x abscissa
     * @param y ordinate
     */
    public void putSpriteForTime(Graphics canvas, ImageObserver iob, int index, int x, int y) {
        putSprite(canvas, iob, "sprite" + index, x, y);
    }

    /** @return the sprite set */
    public SpriteSet getSpriteSet() {
        return spriteSet;
    }

}
