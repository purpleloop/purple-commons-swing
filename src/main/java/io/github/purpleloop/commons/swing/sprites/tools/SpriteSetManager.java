package io.github.purpleloop.commons.swing.sprites.tools;

import io.github.purpleloop.commons.exception.PurpleException;
import io.github.purpleloop.commons.swing.sprites.SpriteSet;

/** The sprite set manager. */
public class SpriteSetManager {

	/** The current sprite set. */
	private SpriteSet spriteSet;

	/**
	 * Creates a sprite set for the image file.
	 * 
	 * @param imageFileName the image file name
	 */
	public void createSpriteSetForImage(String imageFileName) throws PurpleException {
		this.spriteSet = new SpriteSet(imageFileName);
	}

	/**
	 * Loads a sprite set definition from a file.
	 * 
	 * @param spriteSetDefinitonFileName the sprite set definition file name
	 */
	public void loadsSpriteSetDefinition(String spriteSetDefinitonFileName) throws PurpleException {
		this.spriteSet.loadSpritesFrom(spriteSetDefinitonFileName);
	}

	/** @return the sprite set */
	public SpriteSet getSpriteSet() {
		return spriteSet;
	}

	/** @return true if the manager contains a sprite set, false otherwise */
	public boolean hasSpriteSet() {
		return spriteSet != null;
	}

}
