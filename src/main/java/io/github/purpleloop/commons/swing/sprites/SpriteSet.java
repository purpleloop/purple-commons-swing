package io.github.purpleloop.commons.swing.sprites;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.io.File;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import io.github.purpleloop.commons.exception.PurpleException;
import io.github.purpleloop.commons.swing.image.ImageUtils;
import io.github.purpleloop.commons.xml.XMLTools;

/**
 * This class defines a sprite set that can be used for animation purposes. A
 * sprite is a named image chunk.
 */
public class SpriteSet {

	/** The default zoom factor. */
	private static final int DEFAULT_ZOOM_FACTOR = 1;

	/** The image containing the sprites. */
	private Image source;

	/** Sprite names mapping. */
	private Map<String, Sprite> sprites;

	/** Properties for sprite set use. */
	private Properties properties;

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
		properties = new Properties();
		source = spriteSource;
		sprites = new HashMap<String, Sprite>();
		zoomFactor = DEFAULT_ZOOM_FACTOR;
	}

	/**
	 * Adds a new sprite to the SpriteSet.
	 * 
	 * @param sprite Sprite
	 * 
	 */
	public void addSprite(Sprite sprite) {
		sprites.put(sprite.getName(), sprite);
	}

	/**
	 * Adds a new sprite to the SpriteSet.
	 * 
	 * @param name   Sprite name
	 * @param sprite Sprite
	 * 
	 * @deprecated for existing unnamed sprite compatibility
	 */
	@Deprecated
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
	 * @param canvas        Graphic canvas where to do the rendering
	 * @param spriteName    name of the sprite to render
	 * @param imageObserver ImageObserver to notify once drawing has been done
	 * @param x             horizontal location
	 * @param y             vertical location
	 */
	public void putSprite(Graphics canvas, ImageObserver imageObserver, String spriteName, int x, int y) {
		Sprite spriteToRender = sprites.get(spriteName);
		if (spriteToRender == null) {
			throw new RuntimeException("There is no sprite named '" + spriteName + "' in this SpriteSet.");
		}

		canvas.drawImage(source, x, y, (int) (x + spriteToRender.getWidth() * zoomFactor),
				(int) (y + spriteToRender.getHeight() * zoomFactor), spriteToRender.ox, spriteToRender.oy,
				spriteToRender.ox + spriteToRender.getWidth(), spriteToRender.oy + spriteToRender.getHeight(),
				imageObserver);
	}

	/**
	 * Exports the sprite set to an XML file.
	 * 
	 * @param fileName the destination file
	 * @throws PurpleException in case of problems
	 */
	public void saveSpritesTo(String fileName) throws PurpleException {

		File file = Paths.get(fileName).toFile();

		Document document = XMLTools.createDocument();

		Element root = document.createElement("sprite-set");
		document.appendChild(root);

		sprites.forEach((spriteName, sprite) -> root.appendChild(createSpriteXmlElement(document, sprite)));

		XMLTools.writeXmlFile(document, file, XMLTools.DEFAULT_UTF8_OUTPUT);
	}

	/**
	 * Creates an XML element for a sprite.
	 * 
	 * @param document owner XML document
	 * @param sprite   the sprite to export
	 * @return XML sprite element
	 */
	private Element createSpriteXmlElement(Document document, Sprite sprite) {
		Element spriteElement = document.createElement("sprite");
		spriteElement.setAttribute("name", sprite.getName());
		spriteElement.setAttribute("width", Integer.toString(sprite.getWidth()));
		spriteElement.setAttribute("height", Integer.toString(sprite.getHeight()));
		spriteElement.setAttribute("ox", Integer.toString(sprite.getOx()));
		spriteElement.setAttribute("oy", Integer.toString(sprite.getOy()));
		return spriteElement;
	}

	/**
	 * Loads a sprite set from a file.
	 * 
	 * @param fileName name of the XML file containing the sprite set description
	 * 
	 */
	public void loadSpritesFrom(String fileName) throws PurpleException {

		File file = Paths.get(fileName).toFile();

		if (!file.canRead()) {
			throw new PurpleException("The file " + file.getAbsolutePath() + " can't be read.");
		}

		Document document = XMLTools.getDocument(file);

		Element spriteSetElement = document.getDocumentElement();

		// Loads properties
		Element propertiesElements = XMLTools.getUniqueChildElement(spriteSetElement, "properties", true);
		XMLTools.getChildElementsStream(propertiesElements).forEach(propertyElement -> {
			String textContent = propertyElement.getTextContent();
			properties.put(propertyElement.getNodeName(), textContent);
		});

		// Loads sprites
		Element spriteElements = XMLTools.getUniqueChildElement(spriteSetElement, "sprites", true);
		spritesFromElement(spriteElements);

	}

	/**
	 * Reads sprites from a sprite XML Element.
	 * 
	 * @param spriteElements the XML Elements
	 */
	private void spritesFromElement(Element spriteElements) {
		for (Element spriteElement : XMLTools.getChildElements(spriteElements)) {

			String nodeName = spriteElement.getNodeName();
			if (nodeName.equals("sprite-grid")) {

				SpriteGrid grid = new SpriteGrid(spriteElement);
				for (Sprite sprite : grid.getSprites()) {
					addSprite(sprite);
				}

			} else if (nodeName.equals("sprite")) {

				addSprite(spriteFromElement(spriteElement));
			}

		}
	}

	/**
	 * Reads a sprite from a sprite XML Element.
	 * 
	 * @param spriteElement the XML Element
	 * @return the sprite
	 */
	private Sprite spriteFromElement(Element spriteElement) {
		return new Sprite(spriteElement.getAttribute("name"), Integer.parseInt(spriteElement.getAttribute("ox")),
				Integer.parseInt(spriteElement.getAttribute("oy")),
				Integer.parseInt(spriteElement.getAttribute("width")),
				Integer.parseInt(spriteElement.getAttribute("height")));
	}

	/**
	 * @param key property key
	 * @return property value
	 */
	public String getProperty(String key) {
		return properties.getProperty(key);
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
