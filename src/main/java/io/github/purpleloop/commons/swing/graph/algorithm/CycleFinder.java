package io.github.purpleloop.commons.swing.graph.algorithm;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import io.github.purpleloop.commons.swing.graph.Cycle;
import io.github.purpleloop.commons.swing.graph.IGraph;
import io.github.purpleloop.commons.swing.graph.Node;
import io.github.purpleloop.commons.swing.graph.exception.GraphException;

/**
 * Find cycles in a graph.
 * 
 * @param <E> the graph content type.
 */
public class CycleFinder<E> {

    /** The graph we are working on. */
    private IGraph<E> graph;

    /**
     * Cycle finder constructor.
     * 
     * @param graph The graph we are working on
     */
    public CycleFinder(IGraph<E> graph) {
        this.graph = graph;
    }

    /**
     * Detects cycles in the graph.
     * 
     * @return list of cycles found
     * @throws GraphException in case of errors encountered on the graph
     */
    public Set<Cycle<E>> getCycles() throws GraphException {

        Set<Cycle<E>> cycles = new HashSet<>();

        // Start from empty path
        List<Node<E>> nodePath = new LinkedList<>();

        // Consider all graph nodes
        for (Node<E> consideredNode : graph.getNodes()) {

            // Consider all path starting with the given node
            nodePath.add(consideredNode);

            // Find all cycles for these paths
            checkCyclesForPath(nodePath, cycles);

            // continue (reset path)
            nodePath.remove(consideredNode);
        }

        return cycles;
    }

    /**
     * Check if there exist cycles for the given path.
     * 
     * @param pathToCheck the path to check
     * @param cycles collected cycles
     * @throws GraphException in case of graph error
     */
    private void checkCyclesForPath(List<Node<E>> pathToCheck, Set<Cycle<E>> cycles)
            throws GraphException {

        // Consider all successors
        for (Node<E> successorCandidates : graph.listSuccessors(pathToCheck.getLast())) {

            int pathIndex = pathToCheck.indexOf(successorCandidates);

            if (pathIndex != -1) {

                Cycle<E> cycle = new Cycle<>(pathToCheck.subList(pathIndex, pathToCheck.size()),
                        graph);

                // The successor is in the path, so we found a cycle.
                cycles.add(cycle);

            } else {
                // We add the successor to the path, since it still does not
                // form a loop.
                pathToCheck.add(successorCandidates);
                checkCyclesForPath(pathToCheck, cycles);
                pathToCheck.remove(successorCandidates);
            }

        }
    }

}
