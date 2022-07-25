package io.github.purpleloop.commons.swing.sprites.model;

import java.awt.Point;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import io.github.purpleloop.commons.swing.sprites.Sprite;
import io.github.purpleloop.commons.swing.sprites.SpriteSet;
import io.github.purpleloop.commons.xml.XMLTools;

/**
 * Models an index of sprites in an orthogonal 2D grid over an image. Dimensions
 * are given in pixels.
 */
public class SpriteGridIndex implements IndexedSpriteSet {

    /** Name of the XML element for the grid index. */
    public static final String GRID_INDEX_ELEMENT = "gridIndex";

    /** DOM XML attribute for grid id. */
    private static final String ID_ATTRIBUTE = "id";

    /** DOM XML attribute for grid start X. */
    private static final String START_X_ATTRIBUTE = "sx";

    /** DOM XML attribute for grid start Y. */
    private static final String START_Y_ATTRIBUTE = "sy";

    /** DOM XML attribute for grid number of sprites per line. */
    private static final String SPRITES_PER_LINE_ATTRIBUTE = "numColumns";

    /** DOM XML attribute for grid number of lines. */
    private static final String NUMBER_OF_LINES_ATTRIBUTE = "numRows";

    /** DOM XML attribute for grid cell width. */
    private static final String CELL_WIDTH_ATTRIBUTE = "cellWidth";

    /** DOM XML attribute for grid cell height. */
    private static final String CELL_HEIGHT_ATTRIBUTE = "cellHeight";

    /** DOM XML attribute for grid horizontal spacing. */
    private static final String HORIZONTAL_SPACING_ATTRIBUTE = "hSpacing";

    /** DOM XML attribute for grid vertical spacing. */
    private static final String VERTICAL_SPACING_ATTRIBUTE = "vSpacing";

    /** DOM XML element for cell naming. */
    private static final String CELL_NAMING_ELEMENT = "spriteCell";
    
    /** DOM XML attribute for cell naming name. */
    private static final String CELL_NAMING_NAME_ATTRIBUTE = "name";

    /** DOM XML attribute for cell naming abscissa in the grid. */
    private static final String CELL_NAMING_Y_ATTRIBUTE = "y";

    /** DOM XML attribute for cell naming ordinate in the grid. */
    private static final String CELL_NAMING_X_ATTRIBUTE = "x";

    /** Logger of the class. */
    private static final Log LOG = LogFactory.getLog(SpriteGridIndex.class);

    /** The grid index id. */
    private int id;

    /**
     * Upper left corner of the grid. This is the "hot point" or "starting
     * point", used as reference to align the grid.
     */
    private Point startPoint;

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

    /** Names of the cells. */
    private String[][] cellNames;

    /**
     * Constructor for an initial grid index.
     * 
     * @param id id of the index
     */
    public SpriteGridIndex(int id) {

        this.id = id;
        cellNames = new String[2][2];
        setGrid(2, 2, new Point(5, 5), 50, 50, 2, 2);
    }

    /**
     * Sets the grid dimensions.
     * 
     * @param point starting point
     * @param numCols number of sprites per line
     * @param numRows number of lines
     * @param cellWidth cell width
     * @param cellHeight cell height
     * @param hSpacing horizontal padding
     * @param vSpacing vertical padding
     */
    public void setGrid(int numCols, int numRows, Point point, int cellWidth, int cellHeight,
            int hSpacing, int vSpacing) {
        this.startPoint = point;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.cellHorizontalSpacing = hSpacing;
        this.cellVerticalSpacing = vSpacing;
        this.numColumns = numCols;
        this.numRows = numRows;

        cellNames = new String[numColumns][numRows];
    }

    /** @return the starting point of the grid */
    public Point getStartPoint() {
        return startPoint;
    }

    /** @param startPoint the starting point of the grid */
    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }

    @Override
    public int getWidth(int indexValue) {
        return this.cellWidth;
    }

    @Override
    public int getHeight(int indexValue) {
        return this.cellHeight;
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
     *            grid.
     */
    public void setCellHorizontalSpacing(int cellHorizontalSpacing) {
        this.cellHorizontalSpacing = cellHorizontalSpacing;
    }

    /** @return Vertical spacing between two cells of the grid. */
    public int getCellVerticalSpacing() {
        return cellVerticalSpacing;
    }

    /**
     * @param cellVerticalSpacing Vertical spacing between two cells of the
     *            grid.
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

    /**
     * @param spriteIndex the sprite index in the grid
     * @return abscissa of the sprite in the image
     */
    public int getX(int spriteIndex) {
        return (cellWidth + cellHorizontalSpacing) * (spriteIndex % numColumns)
                + (int) startPoint.getX();
    }

    /**
     * @param spriteIndex the sprite index in the grid
     * @return ordinate of the sprite in the image
     */
    public int getY(int spriteIndex) {
        return (cellHeight + cellVerticalSpacing) * (spriteIndex / numColumns)
                + (int) startPoint.getY();
    }

    @Override
    public void registerSprites(SpriteSet spriteSet) {

        LOG.debug("Register sprites of the sprite grid index");

        int col;
        int row;
        String name;

        for (int spriteNumber = 0; spriteNumber < getSpritesCount(); spriteNumber++) {

            col = spriteNumber % numColumns;
            row = spriteNumber / numColumns;

            name = cellNames[col][row];

            if (name == null) {
                name = "sprite" + spriteNumber;
            }

            spriteSet.addSprite(new Sprite(name, getX(spriteNumber), getY(spriteNumber), cellWidth,
                    cellHeight));
        }
    }

    /** @return the number of sprites in the grid */
    public int getSpritesCount() {
        return this.numColumns * this.numRows;
    }

    @Override
    public Optional<Integer> getIndexFor(Point p) {

        double px = p.getX();
        double py = p.getY();

        int dx = (int) (px - startPoint.getX());
        int dy = (int) (py - startPoint.getY());

        if (dx < 0 || dy < 0) {
            // Not in a cell of the grid
            return Optional.empty();
        }

        int xg = dx / (cellWidth + cellHorizontalSpacing);
        int yg = dy / (cellHeight + cellVerticalSpacing);

        int mx = dx % (cellWidth + cellHorizontalSpacing);
        int my = dy % (cellHeight + cellVerticalSpacing);

        if (mx > cellWidth || my > cellHeight) {
            // Not in a cell of the grid
            return Optional.empty();
        }

        return Optional.of(xg + yg * numColumns);
    }

    /**
     * Read sprite index from an XML element.
     * 
     * @param gridElement the XML element representing the grid
     */
    public void readFromXmlElement(Element gridElement) {
        this.id = Integer.parseInt(gridElement.getAttribute(ID_ATTRIBUTE));

        int sx = Integer.parseInt(gridElement.getAttribute(START_X_ATTRIBUTE));
        int sy = Integer.parseInt(gridElement.getAttribute(START_Y_ATTRIBUTE));
        this.startPoint = new Point(sx, sy);

        this.numColumns = Integer.parseInt(gridElement.getAttribute(SPRITES_PER_LINE_ATTRIBUTE));
        this.numRows = Integer.parseInt(gridElement.getAttribute(NUMBER_OF_LINES_ATTRIBUTE));
        this.cellWidth = Integer.parseInt(gridElement.getAttribute(CELL_WIDTH_ATTRIBUTE));
        this.cellHeight = Integer.parseInt(gridElement.getAttribute(CELL_HEIGHT_ATTRIBUTE));
        this.cellHorizontalSpacing = Integer
                .parseInt(gridElement.getAttribute(HORIZONTAL_SPACING_ATTRIBUTE));
        this.cellVerticalSpacing = Integer
                .parseInt(gridElement.getAttribute(VERTICAL_SPACING_ATTRIBUTE));

        cellNames = new String[numColumns][numRows];

        for (Element spriteCellElement : XMLTools.getChildElements(gridElement)) {

            int column = Integer.parseInt(spriteCellElement.getAttribute(CELL_NAMING_X_ATTRIBUTE));
            int row = Integer.parseInt(spriteCellElement.getAttribute(CELL_NAMING_Y_ATTRIBUTE));

            String spriteName = spriteCellElement.getAttribute(CELL_NAMING_NAME_ATTRIBUTE);

            String previousContents = cellNames[column][row];
            if (previousContents != null) {
                LOG.warn("Cell at (" + column + "," + row + ") is overrided : '" + previousContents
                        + "' will be lost.");
            }

            cellNames[column][row] = spriteName;
        }

    }

    /**
     * Save the sprite index as an XML element.
     * 
     * @param doc the XML owning document
     * @param parent XML parent element
     * @return the element representing the sprite index
     */
    public Element saveToXml(Document doc, Element parent) {

        Element gridElement = doc.createElement(GRID_INDEX_ELEMENT);
        gridElement.setAttribute(ID_ATTRIBUTE, Integer.toString(this.id));
        gridElement.setAttribute(START_X_ATTRIBUTE, Integer.toString(this.startPoint.x));
        gridElement.setAttribute(START_Y_ATTRIBUTE, Integer.toString(this.startPoint.y));
        gridElement.setAttribute(SPRITES_PER_LINE_ATTRIBUTE, Integer.toString(this.numColumns));
        gridElement.setAttribute(NUMBER_OF_LINES_ATTRIBUTE, Integer.toString(this.numRows));
        gridElement.setAttribute(CELL_WIDTH_ATTRIBUTE, Integer.toString(this.cellWidth));
        gridElement.setAttribute(CELL_HEIGHT_ATTRIBUTE, Integer.toString(this.cellHeight));
        gridElement.setAttribute(HORIZONTAL_SPACING_ATTRIBUTE,
                Integer.toString(this.cellHorizontalSpacing));
        gridElement.setAttribute(VERTICAL_SPACING_ATTRIBUTE,
                Integer.toString(this.cellVerticalSpacing));
        parent.appendChild(gridElement);

        String name;
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numColumns; col++) {
                name = cellNames[col][row];
                if (name != null) {
                    Element spriteCellElement = doc.createElement(CELL_NAMING_ELEMENT);
                    spriteCellElement.setAttribute(CELL_NAMING_NAME_ATTRIBUTE, name);
                    spriteCellElement.setAttribute(CELL_NAMING_X_ATTRIBUTE, Integer.toString(col));
                    spriteCellElement.setAttribute(CELL_NAMING_Y_ATTRIBUTE, Integer.toString(row));
                    gridElement.appendChild(spriteCellElement);
                }
            }
        }

        return gridElement;
    }

    /**
     * Translate the grid.
     * 
     * @param dx horizontal part of the movement
     * @param dy vertical part of the movement
     */
    public void translate(int dx, int dy) {
        startPoint.translate(dx, dy);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Grid index " + id);
        return sb.toString();
    }

}
