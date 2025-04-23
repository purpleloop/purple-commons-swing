package io.github.purpleloop.commons.swing.graph;

import java.util.List;
import java.util.Optional;

import io.github.purpleloop.commons.exception.PurpleException;
import io.github.purpleloop.commons.swing.graph.exception.GraphException;

/**
 * Interface defining methods for graph data structure.
 * 
 * @param <E> the graph content type
 */
public interface IGraph<E> {

    /** @return true if the graph is empty (containing no nodes). */
    boolean isEmpty();

    /**
     * @return list of all nodes of the graph
     */
    List<Node<E>> getNodes();

    /**
     * @return list of all links of the graph
     */
    List<Link<E>> getLinks();

    /**
     * @param node a given node
     * @return list of all successors of the given node in the graph
     * @throws GraphException if the node does not belong to the graph
     */
    List<Node<E>> listSuccessors(Node<E> node) throws GraphException;

    /**
     * Finds the first node containing the given object.
     * 
     * @param object object to find
     * @return first the node containing the object, if it exists
     */
    Optional<Node<E>> getNodeForObject(E object);

    /**
     * Adds a link between two nodes of the graph.
     * 
     * @param sourceNode Source node label
     * @param targetNode Target node label
     * @return the created link
     * @throws GraphException if provided nodes does not belong to the graph
     */
    Link<E> addLink(Node<E> sourceNode, Node<E> targetNode) throws GraphException;

    /**
     * Adds a link between two nodes of the graph by label.
     * 
     * For convenience, new nodes are added is provided labels does not refer to
     * existing ones.
     * 
     * @param source Source node label
     * @param target Target node label
     * @return the created link
     */
    Link<E> addLink(String source, String target);

    /**
     * Writes the graph in a DOT format file.
     * 
     * @param fileName The file name
     * @throws PurpleException in case of problems
     */
    void writeDotGraph(String fileName) throws PurpleException;

    /**
     * Load links from a given file.
     * 
     * @param fileName The file name
     * @throws PurpleException
     */
    void loadLinksFromFile(String fileName) throws PurpleException;

    /**
     * Tests if two nodes are directly linked each other.
     * 
     * @param a first node
     * @param b second node
     * @return true if nodes are directly linked together
     */
    boolean areLinked(Node<E> a, Node<E> b);

    /** @return lists all nodes that are single or that are start of an edge. */
    List<Node<E>> getStartNodes();

    /**
     * This method get a displayable form for a graph path.
     * 
     * @param pathToDisplay Path to display
     * @return a string describing the graph path
     */
    String getPathString(List<Node<E>> pathToDisplay);

}
