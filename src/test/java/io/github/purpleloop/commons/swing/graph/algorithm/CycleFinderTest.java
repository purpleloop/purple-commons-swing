package io.github.purpleloop.commons.swing.graph.algorithm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import io.github.purpleloop.commons.swing.graph.Cycle;
import io.github.purpleloop.commons.swing.graph.Graph;
import io.github.purpleloop.commons.swing.graph.exception.GraphException;

/** Test for cycle finder. */
class CycleFinderTest {

    /**
     * Test find cycle - no cycles.
     * 
     * @throws GraphException in case of graph errors
     */
    @Test
    void testCycleSearchWithoutCycle() throws GraphException {
        Graph<Integer> graph = sampleGraphWithoutCycle1();

        CycleFinder<Integer> cycleFinder = new CycleFinder<>(graph);
        assertTrue(cycleFinder.getCycles().isEmpty());
    }

    /**
     * Test find cycle - a cycle.
     * 
     * @throws GraphException in case of graph errors
     */
    @Test
    void testCycleSearchWithCycle() throws GraphException {
        Graph<Integer> graph = sampleGraphWithCycle1();

        CycleFinder<Integer> cycleFinder = new CycleFinder<>(graph);
        Set<Cycle<Integer>> cyclesFound = cycleFinder.getCycles();

        assertEquals(1, cyclesFound.size());

        Cycle<Integer> cycle = cyclesFound.iterator().next();
        String pathString = graph.getPathString(cycle.getList());

        assertEquals("(A -> C -> D)", pathString);
    }

    private Graph<Integer> sampleGraphWithoutCycle1() {
        Graph<Integer> graph = new Graph<>();

        graph.addLink("A", "B");
        graph.addLink("A", "C");
        graph.addLink("C", "D");
        return graph;
    }

    private Graph<Integer> sampleGraphWithCycle1() {
        Graph<Integer> graph = new Graph<>();

        graph.addLink("A", "B");
        graph.addLink("A", "C");
        graph.addLink("C", "D");
        graph.addLink("D", "A");
        return graph;
    }

}
