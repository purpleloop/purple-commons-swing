package io.github.purpleloop.commons.swing.sprites.tools;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

import io.github.purpleloop.commons.swing.sprites.Sprite;
import io.github.purpleloop.commons.swing.sprites.SpriteSet;

/** A panel where to display the sprite set. */
public class SpriteSetPanel extends JPanel {

	/** Serialization tag. */
	private static final long serialVersionUID = 5439721651840939710L;

	/** Panel width. */
	private static final int DEFAULT_PANEL_WIDTH = 300;

	/** Panel height. */
	private static final int DEFAULT_PANEL_HEIGHT = 200;

	/** Horizontal offset for the sprite name. */
	private static final int SPRITE_NAME_HORIZONTAL_OFFSET = 12;

	/** Vertical offset for the sprite name. */
	private static final int SPRITE_NAME_VERTICAL_OFFSET = 2;

	/** The color used to show sprite definitions. */
	private Color spriteDefinitionColor = Color.MAGENTA;

	/** The sprite set manager. */
	private SpriteSetManager spriteSetManager;

	/**
	 * Creates the sprite set panel.
	 * 
	 * @param spriteSetManager
	 */
	public SpriteSetPanel(SpriteSetManager spriteSetManager) {
		setPreferredSize(new Dimension(DEFAULT_PANEL_WIDTH, DEFAULT_PANEL_HEIGHT));
		this.spriteSetManager = spriteSetManager;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		SpriteSet spriteSet = spriteSetManager.getSpriteSet();
		
		if (spriteSet != null) {
			Image image = spriteSet.getSourceImage();

			g.drawImage(image, 0, 0, this);

			g.setColor(spriteDefinitionColor);

			int x, y, width, height;
			
			for (String spriteName : spriteSet.getSpritesNames()) {
				Sprite sprite = spriteSet.getSprite(spriteName);

				x = sprite.getOx();
				y = sprite.getOy();
				width = sprite.getWidth();
				height = sprite.getHeight();

				g.drawRect(x, y, width, height);
				g.drawString(spriteName, x + SPRITE_NAME_VERTICAL_OFFSET, y + SPRITE_NAME_HORIZONTAL_OFFSET);

			}
		}
	}

	/** Adjusts the panel dimensions to fit the image of the sprite set. */
	public void ajustToSpriteSetImage() {

		SpriteSet spriteSet = spriteSetManager.getSpriteSet();
		if (spriteSet != null) {
			Image image = spriteSet.getSourceImage();
			setPreferredSize(new Dimension(image.getWidth(this), image.getHeight(this)));
		}
	}

	/**
	 * Changes the color used to show sprite definitions.
	 * 
	 * @param color new color to use
	 */
	public void setSpriteDefinitionColor(Color color) {
		spriteDefinitionColor = color;
	}

}
