package io.github.purpleloop.commons.swing.sprites.tools;

import java.util.Optional;

import io.github.purpleloop.commons.exception.PurpleException;
import io.github.purpleloop.commons.swing.sprites.SpriteSet;
import io.github.purpleloop.commons.swing.sprites.model.SpriteModel;

/** The sprite set manager. */
public class SpriteSetManager {
    
	/** The current sprite set. */
	private SpriteModel spriteModel;

	/**
	 * Creates a sprite set for the image file.
	 * 
	 * @param imageFileName the image file name
	 */
	public void createSpriteSetForImage(String imageFileName) throws PurpleException {	    
	    this.spriteModel = new SpriteModel(imageFileName);
	}

	/**
	 * Loads a sprite set definition from a file.
	 * 
	 * @param spriteSetDefinitonFileName the sprite set definition file name
	 */
	public void loadsSpriteSetDefinition(String spriteSetDefinitonFileName) throws PurpleException {
        this.spriteModel = new SpriteModel(spriteSetDefinitonFileName);
	}

	/** @return the sprite set optional */
	public Optional<SpriteSet> getSpriteSet() {
	    
	    if (hasSpriteSet()) {
	        return Optional.of(this.spriteModel.getSpriteSet());	        
	    } else {
	        return Optional.empty();
	    }
	    
	}

	/** @return true if the manager contains a sprite set, false otherwise */
	public boolean hasSpriteSet() {
		return this.spriteModel != null;
	}

}
