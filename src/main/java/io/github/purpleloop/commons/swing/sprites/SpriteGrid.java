package io.github.purpleloop.commons.swing.sprites;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import io.github.purpleloop.commons.xml.XMLTools;

/**
 * Represents a set of {@link Sprite} organized in a bitmap image as a 2D grid.
 * This grid work as a a container for sprites.
 */
public class SpriteGrid {

	/** Logger for the class. */
	private static final Log LOG = LogFactory.getLog(SpriteGrid.class);

	/** Horizontal origin. */
	private int ox;

	/** Vertical origin. */
	private int oy;

	/** Width of a cell grid. */
	private int cellWidth;

	/** Height of a cell grid. */
	private int cellHeight;

	/** Horizontal spacing between two cells of the grid. */
	private int cellHorizontalSpacing;

	/** Vertical spacing between two cells of the grid. */
	private int cellVerticalSpacing;

	/** Number of columns in the grid. */
	private int numColumns;

	/** Number of rows in the grid. */
	private int numRows;

	/** Cells used as container for sprites. */
	private Sprite[][] spriteCells;

	/**
	 * Reads a sprite grid from XML.
	 * 
	 * @param spriteGridElement the spriteGridElement
	 */
	public SpriteGrid(Element spriteGridElement) {
		this.ox = Integer.parseInt(spriteGridElement.getAttribute("ox"));
		this.oy = Integer.parseInt(spriteGridElement.getAttribute("oy"));
		this.numColumns = Integer.parseInt(spriteGridElement.getAttribute("numColumns"));
		this.numRows = Integer.parseInt(spriteGridElement.getAttribute("numRows"));

		this.cellWidth = Integer.parseInt(spriteGridElement.getAttribute("cellWidth"));
		this.cellHeight = Integer.parseInt(spriteGridElement.getAttribute("cellHeight"));
		this.cellHorizontalSpacing = Integer.parseInt(spriteGridElement.getAttribute("cellHorizontalSpacing"));
		this.cellVerticalSpacing = Integer.parseInt(spriteGridElement.getAttribute("cellVerticalSpacing"));

		spriteCells = new Sprite[numColumns][numRows];

		for (Element spriteCellElement : XMLTools.getChildElements(spriteGridElement)) {

			int column = Integer.parseInt(spriteCellElement.getAttribute("x"));
			int row = Integer.parseInt(spriteCellElement.getAttribute("y"));

			int sox = ox + column * (cellWidth + cellHorizontalSpacing);
			int soy = oy + row * (cellHeight + cellVerticalSpacing);

			String spriteName = spriteCellElement.getAttribute("name");
			Sprite sprite = new Sprite(spriteName, sox, soy, cellWidth, cellHeight);

			Sprite previousContents = spriteCells[column][row];
			if (previousContents != null) {
				LOG.warn("Cell at (" + column + "," + row + ") is overrided : Sprite '" + previousContents.getName()
						+ "' will be lost.");
			}

			spriteCells[column][row] = sprite;
		}

	}

	/** @return Horizontal origin. */
	public int getOx() {
		return ox;
	}

	/** @param ox Horizontal origin. */
	public void setOx(int ox) {
		this.ox = ox;
	}

	/** @return Vertical origin. */
	public int getOy() {
		return oy;
	}

	/** @param oy Vertical origin. */
	public void setOy(int oy) {
		this.oy = oy;
	}

	/** @return Width of a cell grid. */
	public int getCellWidth() {
		return cellWidth;
	}

	/** @param cellWidth Width of a cell grid. */
	public void setCellWidth(int cellWidth) {
		this.cellWidth = cellWidth;
	}

	/** @return Height of a cell grid. */
	public int getCellHeight() {
		return cellHeight;
	}

	/** @param cellHeight Height of a cell grid. */
	public void setCellHeight(int cellHeight) {
		this.cellHeight = cellHeight;
	}

	/** @return Horizontal spacing between two cells of the grid. */
	public int getCellHorizontalSpacing() {
		return cellHorizontalSpacing;
	}

	/**
	 * @param cellHorizontalSpacing Horizontal spacing between two cells of the
	 *                              grid.
	 */
	public void setCellHorizontalSpacing(int cellHorizontalSpacing) {
		this.cellHorizontalSpacing = cellHorizontalSpacing;
	}

	/** @return Vertical spacing between two cells of the grid. */
	public int getCellVerticalSpacing() {
		return cellVerticalSpacing;
	}

	/**
	 * @param cellVerticalSpacing Vertical spacing between two cells of the grid.
	 */
	public void setCellVerticalSpacing(int cellVerticalSpacing) {
		this.cellVerticalSpacing = cellVerticalSpacing;
	}

	/** @return Number of rows in the grid. */
	public int getNumColumns() {
		return numColumns;
	}

	/** @param numColumns Number of rows in the grid. */
	public void setNumColumns(int numColumns) {
		this.numColumns = numColumns;
	}

	/** @return Number of columns in the grid. */
	public int getNumRows() {
		return numRows;
	}

	/** @param numRows Number of columns in the grid. */
	public void setNumRows(int numRows) {
		this.numRows = numRows;
	}

	/** @return list of all sprites in the grid */
	public List<Sprite> getSprites() {

		List<Sprite> allSprites = new ArrayList<>();
		Sprite sprite = null;

		for (int row = 0; row < numRows; row++) {
			for (int column = 0; column < numColumns; column++) {

				sprite = spriteCells[column][row];
				if (sprite != null) {
					allSprites.add(sprite);
				}
			}

		}

		return allSprites;
	}

}
