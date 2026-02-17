package io.github.purpleloop.commons.swing.graph;

/**
 * Interface for graph selected elements.
 * 
 * May evolve to setSelected(state) + isIn(x,y)
 * 
 * @param <E> the selectable content type
 */
public interface Selectable<E> {

    /**
     * Tests if the element is in (x,y), and if it is, select and return the
     * element or null otherwise.
     * 
     * @param x Selection abscissa
     * @param y Selection ordinate
     * @return selected element or null
     */
    Selectable<E> select(int x, int y);

    /**
     * @return is the element selected
     */
    boolean isSelected();

    /** Unselect the element. */
    void unselect();

}
