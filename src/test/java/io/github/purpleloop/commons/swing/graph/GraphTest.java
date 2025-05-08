package io.github.purpleloop.commons.swing.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import io.github.purpleloop.commons.exception.PurpleException;
import io.github.purpleloop.commons.swing.graph.exception.GraphException;

/** Tests on graphs. */
class GraphTest {

    /** Tests the empty graph. */
    @Test
    void testEmptyGraph() {
        Graph<Object> emptyGraph = new Graph<>();

        assertTrue(emptyGraph.isEmpty());
        assertEquals(0, emptyGraph.nodeCount());
        assertTrue(emptyGraph.getStartNodes().isEmpty());
        assertTrue(emptyGraph.getEndNodes().isEmpty());

        assertTrue(emptyGraph.getNodeByLabel("dummy").isEmpty());

        assertEquals(0, emptyGraph.linkCount());
    }

    /** Tests a single node graph. */
    @Test
    void testSingleNodeGraph() {
        Graph<Object> singleNodeGraph = new Graph<>();
        Node<Object> node = singleNodeGraph.addNode("dummy");

        assertFalse(singleNodeGraph.isEmpty());
        assertEquals(1, singleNodeGraph.nodeCount());
        assertEquals(node, singleNodeGraph.getStartNodes().get(0));
        assertEquals(node, singleNodeGraph.getEndNodes().get(0));
        assertEquals(node, singleNodeGraph.getNodeByLabel("dummy").get());

        assertEquals(0, singleNodeGraph.linkCount());

        singleNodeGraph.removeNode(node);
        assertTrue(singleNodeGraph.isEmpty());
    }

    /**
     * Tests a simple graph.
     * 
     * @throws GraphException in cases of error
     */
    @Test
    void testSimpleGraph1() throws GraphException {
        Graph<Integer> simpleGraph1 = simpleGraph();

        assertFalse(simpleGraph1.isEmpty());
        assertTrue(simpleGraph1.isOriented());

        assertEquals(4, simpleGraph1.nodeCount());
        assertEquals("A", simpleGraph1.getStartNodes().get(0).getLabel());
        assertEquals("B",
                simpleGraph1.getEndNodes().stream().findFirst().map(Node::getLabel).get());
        assertTrue(simpleGraph1.getEndNodes().stream().map(Node::getLabel).toList().contains("D"));

        assertEquals(3, simpleGraph1.linkCount());

        // Focus on A node
        Optional<Node<Integer>> nodeByLabelAOpt = simpleGraph1.getNodeByLabel("A");
        assertTrue(nodeByLabelAOpt.isPresent());
        Node<Integer> aNode = nodeByLabelAOpt.get();

        assertTrue(simpleGraph1.getIncomingLinks(aNode).isEmpty());
        assertEquals(2, simpleGraph1.getOutgoingLinks(aNode).size());
        assertEquals("BC", simpleGraph1.listSuccessors(aNode).stream().map(Node::getLabel)
                .collect(Collectors.joining()));

        // Node removal
        simpleGraph1.removeNode(aNode);
        assertEquals(3, simpleGraph1.nodeCount());
        assertEquals(1, simpleGraph1.linkCount());

        // Link removal
        simpleGraph1.removeLink(simpleGraph1.getLinks().get(0));
        assertEquals(3, simpleGraph1.nodeCount());
        assertEquals(0, simpleGraph1.linkCount());

    }

    /**
     * Test saving/loading a simple graph as dataStream.
     * 
     * @throws GraphException in cases of error
     */
    @Test
    void testSaveLoadDataStreamSimpleGraph1() throws GraphException {

        Path tempPath = Paths.get(System.getProperty("java.io.tmpdir"));
        Path dataExportPath = tempPath.resolve("TestGraph-output.dat");

        File dataExportFile = dataExportPath.toFile();
        simpleGraph().save(dataExportFile);

        Graph<Integer> g = new Graph<>();
        g.load(dataExportFile);

        assertEquals(4, g.nodeCount());
        assertEquals(3, g.linkCount());

        dataExportFile.delete();
    }

    /**
     * Test saving/loading a simple graph as XML.
     * 
     * @throws GraphException in cases of error
     */
    @Test
    void testSaveLoadXmlSimpleGraph1() throws GraphException {

        Path tempPath = Paths.get(System.getProperty("java.io.tmpdir"));
        Path dataExportPath = tempPath.resolve("TestGraph-output.xml");

        File dataExportFile = dataExportPath.toFile();
        String absolutePath = dataExportFile.getAbsolutePath();

        simpleGraph().saveXML(absolutePath);

        Graph<Integer> g = new Graph<>();
        g.loadXML(absolutePath);

        assertEquals(4, g.nodeCount());
        assertEquals(3, g.linkCount());

        dataExportFile.delete();
    }

    /** Test for DOT format output. */
    @Test
    void testWriteDot() {
        Graph<Integer> g = simpleGraph();

        try {

            Path tempPath = Paths.get(System.getProperty("java.io.tmpdir"));
            Path dotExportPath = tempPath.resolve("TestGraph-output.dot");
            File file = dotExportPath.toFile();
            String absolutePath = file.getAbsolutePath();

            g.writeDotGraph(absolutePath);

            assertTrue(file.exists());
            file.delete();

        } catch (PurpleException e) {
            fail(e.getMessage());
        }

    }

    /** @return a simple graph of integers. */
    private static Graph<Integer> simpleGraph() {
        Graph<Integer> g = new Graph<>();
        g.setOriented(true);
        g.addLink("A", "B");
        g.addLink("A", "C");
        g.addLink("C", "D");
        return g;
    }

}
