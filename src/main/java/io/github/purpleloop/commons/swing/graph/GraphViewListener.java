package io.github.purpleloop.commons.swing.graph;

/**
 * A listener for a graph view.
 * 
 * @param <E> the graph content type
 */
public interface GraphViewListener<E> {

    /**
     * Reacts to the selection of a node.
     * 
     * @param selectedNode the selected node
     */
    void graphViewNodeSelection(Node<E> selectedNode);

}
