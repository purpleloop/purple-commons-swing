package io.github.purpleloop.commons.swing.sprites;

/**
 * Image used for animation purposes.
 * 
 * The sprite image is a part of a more global image loaded once.
 */
public class Sprite {

	/** Sprite name. */
	protected String name;

	/** Horizontal location of the sprite in the tileset. */
	protected int ox;

	/** Vertical location of the sprite in the tileset. */
	protected int oy;

	/** Sprite width. */
	protected int width;

	/** Sprite height. */
	protected int height;

	/**
	 * Creates a sprite.
	 * 
	 * @param name         name of the sprite
	 * @param xOrigin      Horizontal location of the sprite in the tileset
	 * @param yOrigin      Vertical location of the sprite in the tileset
	 * @param spriteWidth  sprite width
	 * @param spriteHeight sprite height
	 */
	public Sprite(String name, int xOrigin, int yOrigin, int spriteWidth, int spriteHeight) {

		this.name = name;
		ox = xOrigin;
		oy = yOrigin;
		width = spriteWidth;
		height = spriteHeight;
	}

	/** @return name of the sprite */
	public String getName() {
		return name;
	}

	/** @param name name of the sprite */
	public void setName(String name) {
		this.name = name;
	}

	/** @return Horizontal location of the sprite in the tileset */
	public int getOx() {
		return ox;
	}

	/** @return Vertical location of the sprite in the tileset */
	public int getOy() {
		return oy;
	}

	/** @return Sprite width */
	public int getWidth() {
		return width;
	}

	/** @return Sprite height */
	public int getHeight() {
		return height;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Sprite(\"");
		sb.append(name);
		sb.append("\", ox=");
		sb.append(ox);
		sb.append(", oy=");
		sb.append(oy);
		sb.append(", w=");
		sb.append(width);
		sb.append(", h=");
		sb.append(height);
		sb.append(")");
		return sb.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Sprite)) {
			return false;
		}
		Sprite otherSprite = (Sprite) other;

		// Sprites are identified by their name that must be unique.
		return this.name.equals(otherSprite.name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

}
