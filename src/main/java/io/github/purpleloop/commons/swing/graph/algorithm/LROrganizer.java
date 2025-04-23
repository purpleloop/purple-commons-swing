package io.github.purpleloop.commons.swing.graph.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import io.github.purpleloop.commons.swing.graph.Node;
import io.github.purpleloop.commons.swing.graph.exception.GraphException;

/**
 * A left-to-right, by level, graph organizer.
 * 
 * @param <E> the graph content type.
 */
public class LROrganizer<E> extends GraphOrganizer<E> {

    /** X origin. */
    private static final int OX = 20;

    /** Y origin. */
    private static final int OY = 20;

    /** Horizontal space. */
    private static final int HSPACE = 150;

    /** Height. */
    private static final int HEIGHT = 600;

    /**
     * Organize the graph.
     * 
     * @throws GraphException in case of error on the graph
     */
    public void process() throws GraphException {

        Map<Node<E>, Integer> rowsForNodes = new HashMap<>();
        Map<Integer, List<Node<E>>> nodesForRow = new HashMap<>();

        // Begin with all start nodes put in the first row.
        List<Node<E>> nodesToVisit = new ArrayList<>(graph.getStartNodes());

        for (Node<E> node : nodesToVisit) {
            rowsForNodes.put(node, 0);
        }

        int x = OX;

        int row = 0;
        int nbNodesToVisit;

        List<Node<E>> visitedNodes = new LinkedList<>();

        while (!nodesToVisit.isEmpty()) {

            nodesForRow.put(row, nodesToVisit);
            visitedNodes.addAll(nodesToVisit);

            List<Node<E>> nextNodesToVisit = new ArrayList<>();
            nbNodesToVisit = nodesToVisit.size();

            int vspace = HEIGHT / (nbNodesToVisit + 1);
            int i = 1;

            for (Node<E> visitedNode : nodesToVisit) {
                visitedNode.moveTo(x + HSPACE, OY + i * vspace);

                for (Node<E> destination : graph.listSuccessors(visitedNode)) {

                    if (!visitedNodes.contains(destination)) {

                        rowsForNodes.put(destination, row + 1);
                        nextNodesToVisit.add(destination);
                    }

                }

                i++;

            }

            nodesToVisit = nextNodesToVisit;
            row++;
            x += HSPACE;
        }

    }

}
