package io.github.purpleloop.commons.swing.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class models a cycle in a graph. Nodes are given starting from the one
 * with the first name in lexical order.
 * 
 * @param <E> the graph content type
 */
public class Cycle<E> {

    /** Class logger. */
    private static final Log LOG = LogFactory.getLog(Cycle.class);

    /** Graph nodes of the cycle. */
    private List<Node<E>> storage;

    /**
     * Cycle constructor.
     * 
     * @param path Path containing the node to extract.
     * @param graph the graph context
     */
    public Cycle(List<Node<E>> path, IGraph<E> graph) {

        LOG.debug("Cycle to build " + graph.getPathString(path));

        storage = new ArrayList<>();

        // Get the first node of the path by name order
        Node<E> minNode = Collections.min(path);
        LOG.debug("Minimum " + minNode.getLabel());

        // Browse the list from the start node, until the end, then rewind to
        // the start node. Each time, add the found node.

        int start1 = path.indexOf(minNode);
        int stop1 = path.size();

        LOG.debug(start1 + " => " + stop1);

        for (int i = start1; i < stop1; i++) {
            storage.add(path.get(i));
        }

        int start2 = 0;
        int stop2 = start1;
        LOG.debug(start2 + " => " + stop2);

        for (int i = start2; i < stop2; i++) {
            storage.add(path.get(i));
        }
    }

    /**
     * @return List of nodes that compose the cycle.
     */
    public List<Node<E>> getList() {
        return storage;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof Cycle)) {
            return false;
        }

        Cycle<E> autre = (Cycle<E>) o;
        return storage.equals(autre.getList());
    }

    @Override
    public int hashCode() {
        return storage.hashCode();
    }

}
