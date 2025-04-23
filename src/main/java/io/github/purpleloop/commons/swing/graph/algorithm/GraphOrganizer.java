package io.github.purpleloop.commons.swing.graph.algorithm;

import io.github.purpleloop.commons.swing.graph.IGraph;

/**
 * A graph organizer.
 * 
 * @param <E> the graph content type
 */
public class GraphOrganizer<E> {

    /** Graph to manage. */
    protected IGraph<E> graph;

    /** @param graph graph to manage */
    public void setGraph(IGraph<E> graph) {
        this.graph = graph;
    }

}
