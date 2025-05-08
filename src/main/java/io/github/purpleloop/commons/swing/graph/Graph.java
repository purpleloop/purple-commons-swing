package io.github.purpleloop.commons.swing.graph;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import io.github.purpleloop.commons.exception.PurpleException;
import io.github.purpleloop.commons.swing.graph.exception.GraphException;

/**
 * Models a graph data structure object.
 * 
 * @param <E> graph content type
 */
public class Graph<E> implements IGraph<E> {

    /** Max width for nodes location. */
    public static final int MAX_WIDTH = 1200;

    /** Max eight for nodes location. */
    public static final int MAX_HEIGHT = 800;

    /** The default graph id. */
    private static final String DEFAULT_GRAPH_ID = "Default";

    /** Drawing arbitrary abscissa. */
    private static final int ARBITRARY_ORDINATE = 500;

    /** Drawing arbitrary ordinate. */
    private static final int ARBITRARY_ABSCISSA = 500;

    /** Drawing reference abscissa. */
    private static final int DRAW_REFERENCE_ABSCISSA = 400;

    /** Drawing reference ordinate. */
    private static final int DRAW_REFERENCE_ORDINATE = 400;

    /** Drawing radius. */
    private static final double DRAW_RADIUS = 300.0;

    /** The node rendering layer. */
    public static final int NODE_LAYER = 1;

    /** The link rendering layer. */
    public static final int LINK_LAYER = 2;

    /** File format signature (legacy). */
    protected static final String SIGNATURE = "Graph Format 28/08/01";

    /** Class logger. */
    private static final Log LOG = LogFactory.getLog(Graph.class);

    /** Next graph object counter. */
    private int nextGraphObjectId;

    /** The graph id. */
    private String graphId;

    /** Set of the nodes of the graph. */
    private List<Node<E>> nodes;

    /** Set of the links of the graph. */
    private List<Link<E>> links;

    /**
     * Is node order change allowed ?
     * 
     * If true, the selected node becomes the first one.
     */
    private boolean allowChangeOrder = false;

    /** Is the graph oriented (one way links) ? */
    private boolean oriented = true;

    /** Are the node labeled ? */
    private boolean nodeLabels = true;

    /** Are the links labeled ? */
    private boolean linkLabels = true;

    /** Is content rendering active ? */
    private boolean renderContents = false;

    /**
     * Creates an empty graph with an id.
     * 
     * @param id the graph id
     */
    public Graph(String id) {
        setId(id);

        this.nextGraphObjectId = 0;
        this.nodes = new LinkedList<>();
        this.links = new LinkedList<>();
    }

    /** Creates an empty graph with the default id. */
    public Graph() {
        this(DEFAULT_GRAPH_ID);
    }

    /**
     * @param newId the new graph id
     */
    public void setId(String newId) {
        graphId = newId;
    }

    /**
     * @return the graph id
     */
    public String getId() {
        return graphId;
    }

    /** @param oriented is the graph oriented */
    public void setOriented(boolean oriented) {
        this.oriented = oriented;
    }

    /** @return is the graph oriented */
    public boolean isOriented() {
        return oriented;
    }

    /** @return is content rendered */
    public boolean isRenderContents() {
        return renderContents;
    }

    /** @param renderContents content rendering activation */
    public void setRenderContent(boolean renderContents) {
        this.renderContents = renderContents;
    }

    /** @return the next graph object id */
    public synchronized int getNextGraphObjectId() {
        return nextGraphObjectId++;
    }

    /**
     * Adds a node in the graph.
     * 
     * @param label The node label
     * @return the added node
     */
    public Node<E> addNode(String label) {
        Node<E> newNode = new Node<>(this, label);
        nodes.add(newNode);
        return newNode;
    }

    /**
     * Removes a node from the graph. The removal of a node implies the removal
     * of all links connected to this node (as source and as target).
     * 
     * @param nodeToRemove then node to remove
     */
    public void removeNode(Node<E> nodeToRemove) {

        List<Link<E>> linksToRemove = new ArrayList<>();

        // Browses the list of links, checking those the node can belong.
        for (Link<E> checkedLink : links) {
            if (checkedLink.hasExtremity(nodeToRemove)) {
                // A link to remove has been found, add to the remove list
                linksToRemove.add(checkedLink);
            }
        }

        links.removeAll(linksToRemove);

        nodes.remove(nodeToRemove);
    }

    @Override
    public List<Node<E>> getNodes() {
        return nodes;
    }

    @Override
    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    /**
     * @return number of nodes in the graph
     */
    public int nodeCount() {
        return nodes.size();
    }

    /**
     * Returns a node in the graph for the provided index.
     * 
     * FIXME Beware, index may not be constant if {@link Graph#allowChangeOrder}
     * is enabled (change on selection). Seems a bad idea afterwards.
     * 
     * Nevertheless this is used for load/save graphs.
     * 
     * @param nodeIndex the requested node index
     * @return the requested node
     * @throws GraphException if node index is invalid
     */
    protected Node<E> getNodeByIndex(int nodeIndex) throws GraphException {
        try {
            return nodes.get(nodeIndex);
        } catch (IndexOutOfBoundsException e) {
            throw new GraphException("Invalid node index", e);
        }
    }

    /**
     * Get the index of a node.
     * 
     * FIXME Beware, index may not be constant if {@link Graph#allowChangeOrder}
     * is enabled (change on selection). Seems a bad idea afterwards.
     * 
     * Nevertheless this is used for load/save graphs.
     * 
     * @param n Requested node
     * @return node index
     */
    public int getNodeIndex(Node<E> n) {
        return nodes.indexOf(n);
    }

    /**
     * Returns a node in the graph for the provided label.
     * 
     * @param nodeLabel the requested node label
     * @return the requested node if it has been found, optional
     */
    public Optional<Node<E>> getNodeByLabel(String nodeLabel) {
        Node<E> testedNode = null;
        boolean found = false;
        int nodeIndex = 0;
        while ((nodeIndex < nodes.size()) && (!found)) {
            testedNode = nodes.get(nodeIndex);
            found = testedNode.getLabel().equals(nodeLabel);
            nodeIndex += (found) ? 0 : 1;
        }
        if (found) {
            return Optional.of(testedNode);
        } else {
            return Optional.empty();
        }
    }

    /**
     * Searches for the node containing the given object.
     * 
     * @param object reference object
     * @return the first node containing the object.
     */
    public Optional<Node<E>> getNodeForObject(E object) {
        for (Node<E> testedNode : nodes) {
            if (testedNode.contains(object)) {
                return Optional.of(testedNode);
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Node<E>> getStartNodes() {

        // Initially consider all nodes of the graph.
        List<Node<E>> resultat = new LinkedList<>(nodes);

        // Remove all nodes that are destinations, meaning that another node is
        // "before"
        Node<E> targetNode;
        for (Link<E> testedLink : links) {
            targetNode = testedLink.getTarget();
            if (targetNode != null) {
                resultat.remove(targetNode);
            }
        }

        return resultat;
    }

    /** @return lists all nodes that are single or that are end of an edge. */
    public List<Node<E>> getEndNodes() {

        List<Node<E>> result = new LinkedList<>(nodes);

        // Remove all nodes that are sources, meaning that another node is
        // "after"
        Node<E> source;
        for (Link<E> testedLink : links) {
            source = testedLink.getSource();
            if (source != null) {
                result.remove(source);
            }
        }

        return result;
    }

    @Override
    public Link<E> addLink(String source, String target) {

        Node<E> sourceNode = null;
        Node<E> targetNode = null;
        for (Node<E> testedNode : nodes) {
            if (testedNode.hasLabel(source)) {
                sourceNode = testedNode;
            }
            if (testedNode.hasLabel(target)) {
                targetNode = testedNode;
            }
        }

        if (sourceNode == null) {
            sourceNode = new Node<>(this, source);
            nodes.add(sourceNode);
        }
        if (targetNode == null) {
            targetNode = new Node<>(this, target);
            nodes.add(targetNode);
        }

        try {
            return addLink(sourceNode, targetNode);
        } catch (GraphException e) {

            // Should never occur
            throw new RuntimeException(
                    "Inconsistent graph state - just added nodes could not be found.", e);
        }
    }

    @Override
    public Link<E> addLink(Node<E> sourceNode, Node<E> targetNode) throws GraphException {

        if ((sourceNode == null) || (targetNode == null)) {
            return null;
        }

        if (!nodes.contains(sourceNode)) {
            throw new GraphException("The source node does not belong to the graph");
        }

        if (!nodes.contains(targetNode)) {
            throw new GraphException("The target node does not belong to the graph");
        }

        Link<E> l = new Link<>(this, sourceNode, targetNode);
        links.add(l);
        return l;
    }

    /**
     * Adds a link between two nodes of the graph by id.
     * 
     * @param sourceNodeId Source node label
     * @param targetNodeId Target node label
     * @return the created link
     * @throws GraphException in case of error while resolving node indexes
     */
    public Link<E> addLink(int sourceNodeId, int targetNodeId) throws GraphException {

        Node<E> sourceNode = getNodeByIndex(sourceNodeId);
        Node<E> targetNode = getNodeByIndex(targetNodeId);
        return addLink(sourceNode, targetNode);
    }

    /**
     * Adds a spline link between two nodes of the graph.
     * 
     * @param sourceNode Source node label
     * @param targetNode Target node label
     * @return the created spline link
     */
    public SplineLink<E> addSplineLink(Node<E> sourceNode, Node<E> targetNode) {
        links.add(new SplineLink<>(this, sourceNode, targetNode));
        return new SplineLink<>(this, sourceNode, targetNode);
    }

    /**
     * @param linkIndex the link index
     * @return the requested link
     */
    public Link<E> getLink(int linkIndex) {
        return links.get(linkIndex);
    }

    /** @return all the links of the graph */
    public List<Link<E>> getLinks() {
        return links;
    }

    /**
     * Get all outgoing (interior) links from the given node.
     * 
     * @param node reference node
     * @return all outgoing links
     */
    public List<Link<E>> getOutgoingLinks(Node<E> node) {

        List<Link<E>> lns = new LinkedList<>();

        for (Link<E> l : links) {
            if (l.hasSource(node)) {
                lns.add(l);
            }
        }
        return lns;
    }

    /**
     * Get all incoming (exterior) links to the given node.
     * 
     * @param node reference node
     * @return all incoming links
     */
    public List<Link<E>> getIncomingLinks(Node<E> node) {

        List<Link<E>> lns = new LinkedList<>();

        for (Link<E> l : links) {
            if (l.leadsTo(node)) {
                lns.add(l);
            }
        }
        return lns;
    }

    /**
     * Get all incoming and outgoing (interior and exterior) links for the given
     * node.
     * 
     * @param node reference node
     * @return all incoming and outgoing links
     */
    public List<Link<E>> getIOLinks(Node<E> node) {

        LinkedList<Link<E>> lns = new LinkedList<>();

        for (Link<E> l : links) {
            if (l.hasExtremity(node)) {
                lns.add(l);
            }
        }
        return lns;
    }

    /**
     * @return number of links in the graph
     */
    public int linkCount() {
        return links.size();
    }

    /**
     * Transform the provided spline link into a linear link.
     * 
     * @param splineLink the spline link to transform
     */
    public void linearize(SplineLink<E> splineLink) {

        int idx = links.indexOf(splineLink);

        Link<E> nl = new Link<>(this, splineLink.getSource(), splineLink.getTarget());
        nl.setLabel(splineLink.getLabel());

        try {
            links.set(idx, nl);
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            LOG.error("Error : Transformation attempted to modify a link at a bad index.", e);

        }
    }

    /**
     * Transform the provided linear link into a spline link.
     * 
     * @param linearLink the linear link to transform
     */
    public void splinize(Link<E> linearLink) {

        int idx = links.indexOf(linearLink);

        SplineLink<E> nl = new SplineLink<>(this, linearLink.getSource(), linearLink.getTarget());
        nl.setLabel(linearLink.getLabel());

        try {
            links.set(idx, nl);
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
            LOG.error("Error : Transformation attempted to modify a link at a bad index.", e);
        }
    }

    /**
     * Remove a link from the graph.
     * 
     * @param linkToRemove the link to remove
     */
    public void removeLink(Link<E> linkToRemove) {
        links.remove(linkToRemove);
    }

    /**
     * Renders a graph on a graphic.
     * 
     * The rendering occurs in two layers, links and then nodes.
     * 
     * @param graphics Graphics where to render
     */
    public void draw(Graphics2D graphics) {

        for (Link<E> l : links) {
            l.draw(graphics, LINK_LAYER);
        }

        for (Node<E> n : nodes) {
            n.draw(graphics, NODE_LAYER);

        }
    }

    /**
     * Collects all nodes and links that can be reached from a reference node,
     * up to a given depth.
     * 
     * @param referenceNode The reference node
     * @param reachableNodes All reachable nodes
     * @param reachableLinks All reachable links
     * @param depth depth
     */
    private void getReachables(Node<E> referenceNode, int depth, List<Node<E>> reachableNodes,
            List<Link<E>> reachableLinks) {

        // The reference node is always reachable
        reachableNodes.add(referenceNode);

        // Max depth reached
        if (depth <= 0) {
            return;
        }

        Node<E> childNode = null;

        // For all links concerning the reference node
        for (Link<E> ioLink : getIOLinks(referenceNode)) {

            // Get the opposite node (child)
            childNode = ioLink.getOpposite(referenceNode);

            // If not yet processed
            if (!reachableNodes.contains(childNode)) {

                // Add the link and go in recursion
                reachableLinks.add(ioLink);
                getReachables(childNode, depth - 1, reachableNodes, reachableLinks);
            }
        }
    }

    /**
     * Render a part of the graph centered on a reference node.
     * 
     * The rendering occurs in two layers, links and then nodes.
     *
     * @param graphics Graphics used to render
     * @param reference the reference node
     * @param depth rendering depth
     */
    public void drawWithFocus(Graphics2D graphics, Node<E> reference, int depth) {

        // Move all nodes to an arbitrary location
        for (Node<E> nn : nodes) {
            nn.moveTo(ARBITRARY_ABSCISSA, ARBITRARY_ORDINATE);
        }

        // Moves only the reference node at a given location
        reference.moveTo(DRAW_REFERENCE_ABSCISSA, DRAW_REFERENCE_ORDINATE);

        if (!isOriented()) {
            drawUnOrientedWithFocus(graphics, reference, depth);

        } else {
            drawOrientedWithFocus(graphics, reference);

        }

        reference.draw(graphics, NODE_LAYER);
    }

    /**
     * Draws an oriented graph with focus on a reference node.
     * 
     * @param graphics the graphics where to write
     * @param reference the reference node
     */
    private void drawOrientedWithFocus(Graphics2D graphics, Node<E> reference) {
        List<Link<E>> focusedInLinks = getIncomingLinks(reference);
        List<Link<E>> focusedOutLinks = getOutgoingLinks(reference);

        List<Node<E>> focusedInNodes = new LinkedList<>();
        for (Link<E> l : focusedInLinks) {
            focusedInNodes.add(l.getSource());
        }

        List<Node<E>> focusedOutNodes = new LinkedList<>();
        for (Link<E> l : focusedOutLinks) {
            focusedOutNodes.add(l.getTarget());
        }

        drawNodesInHalfCircle(focusedInNodes, 1);
        drawNodesInHalfCircle(focusedOutNodes, -1);

        for (Link<E> l : focusedInLinks) {
            graphics.setColor(Color.GRAY);
            l.draw(graphics, LINK_LAYER);
        }

        for (Link<E> l : focusedOutLinks) {
            graphics.setColor(Color.BLACK);
            l.draw(graphics, LINK_LAYER);
        }

        for (Node<E> n : focusedInNodes) {
            graphics.setColor(Color.GRAY);
            n.draw(graphics, NODE_LAYER);
        }

        for (Node<E> n : focusedOutNodes) {
            graphics.setColor(Color.BLACK);
            n.draw(graphics, NODE_LAYER);
        }

        graphics.setColor(Color.BLACK);
    }

    private void drawUnOrientedWithFocus(Graphics2D graphics, Node<E> reference, int depth) {
        LinkedList<Node<E>> focusedNodes = new LinkedList<>();
        LinkedList<Link<E>> focusedLinks = new LinkedList<>();
        getReachables(reference, depth, focusedNodes, focusedLinks);

        drawNodesInCircle(focusedNodes);

        for (Link<E> l : focusedLinks) {
            l.draw(graphics, LINK_LAYER);
        }

        for (Node<E> n : focusedNodes) {
            n.draw(graphics, NODE_LAYER);
        }
    }

    /**
     * Draw given nodes in a half circular way around the reference.
     * 
     * @param nodesToDraw nodes to draw
     * @param sign sign (-1/+1) used to determine half circle to draw
     */
    private void drawNodesInHalfCircle(List<Node<E>> nodesToDraw, int sign) {

        int nodesCount = nodesToDraw.size();

        if (nodesCount > 0) {

            int nx;
            int ny;
            double da = sign * Math.PI / (nodesCount + 1.0);
            double angle = Math.PI / 2 + da;

            for (Node<E> n : nodesToDraw) {
                nx = DRAW_REFERENCE_ABSCISSA + (int) (DRAW_RADIUS * Math.cos(angle));
                ny = DRAW_REFERENCE_ORDINATE - (int) (DRAW_RADIUS * Math.sin(angle));
                n.moveTo(nx, ny);
                angle += da;
            }
        }
    }

    private void drawNodesInCircle(LinkedList<Node<E>> nodesToDraw) {

        int nodesCount = nodesToDraw.size();

        if (nodesCount > 0) {

            int nx;
            int ny;

            // Unfold all neighbor nodes in a circular way
            for (int i = 1; i < nodesCount; i++) {
                double angle = 2.0 * Math.PI * (i - 1.0) / (nodesCount - 1.0);
                nx = DRAW_REFERENCE_ABSCISSA + (int) (DRAW_RADIUS * Math.cos(angle));
                ny = DRAW_REFERENCE_ORDINATE - (int) (DRAW_RADIUS * Math.sin(angle));
                (nodesToDraw.get(i)).moveTo(nx, ny);
            }
        }
    }

    /**
     * @return maximum abscissa of the graph nodes
     */
    public double xmax() {
        double xMaxFound = 0;
        double testedX = 0;
        for (int i = 0; i < nodes.size(); i++) {
            testedX = (nodes.get(i)).xmax();
            xMaxFound = (xMaxFound < testedX) ? testedX : xMaxFound;
        }
        return xMaxFound;
    }

    /**
     * @return maximum ordinate of the graph nodes
     */
    public double ymax() {
        double yMaxFound = 0;
        double testedY = 0;
        for (Node<E> nn : nodes) {
            testedY = nn.ymax();
            yMaxFound = (yMaxFound < testedY) ? testedY : yMaxFound;
        }
        return yMaxFound;
    }

    /**
     * Return the size of the graph.
     * 
     * @return Dimension Size of the bounding box.
     */
    public Dimension getPreferredSize() {
        return new Dimension((int) xmax(), (int) ymax());
    }

    /**
     * Move all selected nodes to a given location.
     * 
     * @param x abscissa
     * @param y ordinate
     */
    public void dragTo(int x, int y) {

        for (int i = 0; i < nodes.size(); i++) {
            (nodes.get(i)).dragTo(x, y);
        }

        for (int i = 0; i < links.size(); i++) {
            links.get(i).dragTo(x, y);
        }
    }

    /**
     * Select the graph element in (x,y).
     * 
     * Beware selected element may be reordered to be displayed on the top.
     * 
     * 
     * @param x abscissa
     * @param y ordinate
     * @return a selectable element or null
     */
    @SuppressWarnings("unchecked")
    public Selectable select(int x, int y) {

        Selectable selectable = null;

        for (int i = 0; (i < nodes.size()) && (selectable == null); i++) {

            selectable = nodes.get(i).select(x, y);
        }

        if ((selectable instanceof Node) && (allowChangeOrder)) {
            // bring to front
            Node<E> n = (Node<E>) selectable;
            nodes.remove(n);
            nodes.add(n);
        }

        if (selectable == null) {
            for (int i = 0; (i < links.size()) && (selectable == null); i++) {
                selectable = links.get(i).select(x, y);
            }
            if ((selectable instanceof Link) && (allowChangeOrder)) {
                // bring to front
                Link<E> l = (Link<E>) selectable;

                links.remove(l);
                links.add(l);
            }
        }
        return selectable;
    }

    /** Unselect all. */
    public void unselect() {
        for (int i = 0; i < nodes.size(); i++) {
            (nodes.get(i)).unselect();
        }

        for (int i = 0; i < links.size(); i++) {
            (links.get(i)).unselect();
        }

    }

    /**
     * Loads a graph from a binary file.
     * 
     * @param graphFileToLoad the graph file to load
     */
    public void load(File graphFileToLoad) {

        try (DataInputStream in = new DataInputStream(new FileInputStream(graphFileToLoad));) {

            String id = in.readUTF();

            if (id.equals(SIGNATURE)) {

                nextGraphObjectId = in.readInt();

                nodes = new LinkedList<>();
                links = new LinkedList<>();

                int nodeCount = in.readInt();
                for (int i = 0; i < nodeCount; i++) {
                    nodes.add(new Node<>(this, in));
                }

                int linkCount = in.readInt();
                int linkType = 0;

                for (int i = 0; i < linkCount; i++) {

                    linkType = in.readInt();

                    if (linkType == Link.LINK_CODE) {
                        links.add(new Link<>(this, in));
                    } else if (linkType == SplineLink.LINK_CODE) {
                        links.add(new SplineLink<>(this, in));
                    } else {
                        LOG.error("Unknown link type : " + linkType);
                    }
                }

            } else {
                LOG.error("Error - bad file format for graph");
            }

        } catch (IOException e) {
            LOG.error("IO exception", e);
        }
    }

    /**
     * Saves the graph to an XML file.
     * 
     * @param fileName the file name
     */
    public void saveXML(String fileName) {

        File f;
        Document document;
        Element root;
        Element list;
        Element item;

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            // Disable external entities declaration to prevent XXE
            // vulnerabilities.
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

            DocumentBuilder db = dbf.newDocumentBuilder();

            f = new File(fileName);
            document = db.newDocument();

            root = document.createElement("graph");
            root.setAttribute("nextId", Integer.toString(nextGraphObjectId));

            document.appendChild(root);

            list = document.createElement("nodes");
            root.appendChild(list);
            for (int i = 0; i < nodes.size(); i++) {
                item = nodes.get(i).getXMLNode(document);
                list.appendChild(item);
            }

            list = document.createElement("links");
            root.appendChild(list);
            for (int i = 0; i < links.size(); i++) {
                item = links.get(i).getXMLLink(document, this);
                list.appendChild(item);
            }

            // Use a Transformer for output
            TransformerFactory tFactory = TransformerFactory.newInstance();
            tFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
            tFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

            Transformer transformer = tFactory.newTransformer();

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(f);
            transformer.transform(source, result);

        } catch (TransformerException | ParserConfigurationException e) {
            LOG.error("Save XML error", e);
        }

    }

    /**
     * Loads a graph thom an XML file.
     * 
     * @param fileName the file name
     */
    public void loadXML(String fileName) throws GraphException {

        NodeList nl2;
        Element el;
        Element linkElement;
        Node<E> n;
        Link<E> link = null;
        String type;
        int i;
        int typ;

        try {

            File f = new File(fileName);

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            // Disable external entities declaration to prevent XXE
            // vulnerabilities.
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);

            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(f);
            Element rootElement = doc.getDocumentElement();

            if (rootElement.getTagName().equals("graph")) {

                nextGraphObjectId = Integer.parseInt(rootElement.getAttribute("nextId"));
                nodes = new LinkedList<>();

                NodeList nodeListNodes = rootElement.getElementsByTagName("nodes");
                el = (Element) nodeListNodes.item(0);
                nl2 = el.getElementsByTagName("node");

                for (i = 0; i < nl2.getLength(); i++) {
                    n = new Node<>(this, (Element) nl2.item(i));
                    nodes.add(n);
                }

                links = new LinkedList<>();
                NodeList nodeListLinks = rootElement.getElementsByTagName("links");
                el = (Element) nodeListLinks.item(0);
                nl2 = el.getElementsByTagName("link");

                for (i = 0; i < nl2.getLength(); i++) {
                    linkElement = (Element) nl2.item(i);

                    type = linkElement.getAttribute("type");

                    if (type == null || type.isBlank()) {
                        typ = Link.LINK_CODE;
                    } else {
                        typ = Integer.parseInt(type);
                    }

                    if (typ == Link.LINK_CODE) {
                        link = new Link<>(this, linkElement);
                    } else {
                        link = new SplineLink<>(this, linkElement);
                    }
                    links.add(link);
                }

            } else {
                LOG.error("Root XML element is not recognized as a graph ..."
                        + rootElement.getTagName());
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            LOG.error("XML error", e);
        }

    }

    /**
     * Saves the graph in a binary file.
     * 
     * Format is :
     * <ul>
     * <li>A signature</li>
     * <li>Count and list of nodes {@link Node#saveTo}</li>
     * <li>Count and list of links {@link Link#saveTo}</li>
     * </ul>
     * 
     * @param graphFile the target graph file
     */
    public void save(File graphFile) {

        Node<E> ns;
        Link<E> ls;

        try (DataOutputStream out = new DataOutputStream(new FileOutputStream(graphFile));) {

            out.writeUTF(SIGNATURE);
            out.writeInt(nextGraphObjectId);

            out.writeInt(nodes.size());
            for (int i = 0; i < nodes.size(); i++) {
                ns = nodes.get(i);
                ns.saveTo(out);
            }

            out.writeInt(links.size());

            for (int i = 0; i < links.size(); i++) {

                ls = links.get(i);

                // Link type
                out.writeInt(ls.getLinkCode());

                ls.saveTo(out, this);
            }

            out.flush();

        } catch (IOException e) {
            LOG.error("Error : unable de to save the graph\n", e);
        }
    }

    /**
     * Saves he graph to a text file.
     * 
     * @param fileName name of the text file
     */
    public void saveText(String fileName) {

        try {
            FileOutputStream fos = new FileOutputStream(fileName);
            PrintStream ps = new PrintStream(fos);

            ps.println("Signature = " + SIGNATURE);

            ps.println(nodes.size());
            for (Node<E> ns : nodes) {
                ns.saveTextTo(ps);
            }

            ps.println(links.size());

            for (Link<E> ls : links) {
                // Type of the link
                ps.println(ls.getLinkCode());
                ls.saveTextTo(ps, this);
            }

            ps.close();
            fos.close();

        } catch (IOException e) {
            LOG.error("Error : unable de to save the graph\n", e);
        }
    }

    /**
     * @param linkLabels true if link labels are used, false otherwise
     */
    public void setLinkLabels(boolean linkLabels) {
        this.linkLabels = linkLabels;
    }

    /** @return if link labels are used, false otherwise */
    public boolean hasLinkLabels() {
        return linkLabels;
    }

    /** @return if node labels are used, false otherwise */
    public boolean hasNodeLabels() {
        return nodeLabels;
    }

    @Override
    public List<Node<E>> listSuccessors(Node<E> n) throws GraphException {

        checkBelongs(n);

        List<Node<E>> suc = new LinkedList<>();
        for (Link<E> l : links) {
            if (l.getSource().equals(n)) {
                suc.add(l.getTarget());
            }
        }
        return suc;
    }

    /**
     * Checks if the given node belongs to this graph.
     * 
     * @param testedNode the node to test
     * @throws GraphException in case of error
     */
    private void checkBelongs(Node<E> testedNode) throws GraphException {
        if (this != testedNode.getOwner()) {
            throw new GraphException("The node does not belong to the graph.");
        }
    }

    @Override
    public void loadLinksFromFile(String fileName) throws PurpleException {

        try (BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));) {
            String readLine;
            int index;
            while ((readLine = br.readLine()) != null) {
                index = readLine.indexOf("|");
                if (index != -1) {
                    String sourceStr = readLine.substring(0, index);
                    String targetStr = readLine.substring(index + 1, readLine.length());
                    addLink(sourceStr, targetStr);
                }
            }

        } catch (FileNotFoundException e) {
            throw new PurpleException("File not found " + fileName, e);
        } catch (IOException e) {
            throw new PurpleException("IOError on file " + fileName, e);
        }

    }

    @Override
    public String getPathString(List<Node<E>> pathToDisplay) {
        StringBuilder description = new StringBuilder("(");
        boolean already = false;

        for (Node<E> n : pathToDisplay) {
            if (already) {
                description.append(" -> ");
                description.append(n.getLabel());
            } else {
                description.append(n.getLabel());
                already = true;
            }
        }

        description.append(")");
        return description.toString();
    }

    @Override
    public void writeDotGraph(String fileName) throws PurpleException {

        File file = new File(fileName);
        try (PrintWriter pw = new PrintWriter(file);

        ) {
            LOG.debug("Writing DOT file " + file.getAbsolutePath());

            pw.println("digraph G {");
            pw.println(" rankdir=LR; ");
            pw.println("ratio=compress;");
            pw.println("concentrate=true;");
            pw.println();

            int id = 0;
            for (Node<E> n : nodes) {
                pw.println("N" + (id++) + "[ label=\"" + n.getLabel() + "\" ];");
            }

            for (Link<E> e : getLinks()) {
                int src = nodes.indexOf(e.getSource());
                int dst = nodes.indexOf(e.getTarget());
                pw.println("N" + src + " -> N" + dst + ";");
            }

            pw.println("}");

            pw.flush();

        } catch (IOException e) {
            throw new PurpleException("Failed to write DOT graph.", e);
        }

    }

    @Override
    public boolean areLinked(Node<E> a, Node<E> b) {
        int i = 0;
        boolean found = false;
        Link<E> l = null;

        while ((i < links.size()) && (!found)) {
            l = links.get(i);
            found = l.hasExtremity(a) && l.hasExtremity(b);
            if (!found) {
                i++;
            }
        }
        return found;
    }

    /**
     * @param x selection abscissa
     * @param y selection ordinate
     * @param range proximity
     * @return the node near the location (x, y) or null
     */
    public Node<E> getElementIn(int x, int y, int range) {

        int i = 0;
        Node<E> p = null;

        while (i < nodes.size()) {
            p = nodes.get(i);

            if (p.isIn(x, y, range)) {
                return p;
            }
            i++;
        }
        return null;
    }

}
