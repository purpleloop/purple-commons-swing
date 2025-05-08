package io.github.purpleloop.commons.swing.graph;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import io.github.purpleloop.commons.xml.XMLTools;

/**
 * A base class for graph objects.
 * 
 * <UL>
 * <LI>Nodes</LI>
 * <LI>Links</LI>
 * <LI>Spline links</LI>
 * <LI>Spline links anchors</LI>
 * </UL>
 * 
 * @param <E> the content type of the stored objects
 */
public class GraphObject<E> {

    /** Class logger. */
    private static final Log LOG = LogFactory.getLog(GraphObject.class);

    /** Owning graph. */
    protected Graph<E> owner;

    /**
     * The stored contents if any.
     * 
     * Notice : Discussions exists on the practice of 'using an optional as a
     * field'. Kept as it is as it is somewhat practical indeed. This prevent
     * creating the optional on the fly for each access.
     */
    private Optional<E> contents;

    /** The id of the graph object in the owning graph. */
    private int id;

    /**
     * Base constructor for a graph object.
     * 
     * @param owner the owner graph
     */
    public GraphObject(Graph<E> owner) {
        this.id = owner.getNextGraphObjectId();
        this.owner = owner;
        this.contents = Optional.empty();
    }

    /**
     * Base constructor for a graph object with a given content.
     * 
     * @param owner the owner graph
     * @param contents the data to store, nullable
     */
    public GraphObject(Graph<E> owner, E contents) {

        this.id = owner.getNextGraphObjectId();
        this.owner = owner;
        this.contents = Optional.ofNullable(contents);
    }

    /**
     * Base constructor for a graph object with a given content.
     * 
     * @param owner the owner graph
     * @param in Data input stream
     */
    public GraphObject(Graph<E> owner, DataInputStream in) {
        this.owner = owner;

        try {
            this.id = in.readInt();
        } catch (IOException e) {
            LOG.error("Error, unable to load graph object.", e);
        }
    }

    /**
     * Base constructor for a graph object from an XML element.
     * 
     * @param owner the owner graph
     * @param xmlElement the XML element
     */
    public GraphObject(Graph<E> owner, Element xmlElement) {

        this.owner = owner;
        this.id = XMLTools.getIntegerAttributeValue(xmlElement, "id", -1);
    }

    /** @return the graph that owns this graph object */
    public Graph<E> getOwner() {
        return owner;
    }

    /**
     * Tests if two graph objects belong to the same graph.
     * 
     * @param otherGraphObject other graph object
     * @return true if the two objects belong to the same graph, false otherwise
     */
    public boolean hasSameOwner(GraphObject<E> otherGraphObject) {
        return (owner == otherGraphObject.getOwner());
    }

    /** @return The graph object id */
    public int getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(owner.getId(), getId());
    }

    @Override
    public boolean equals(Object otherObject) {

        if (this == otherObject) {
            return true;
        }

        if (!(otherObject instanceof GraphObject<?>)) {
            return false;
        }

        GraphObject<?> otherGraphObject = (GraphObject<?>) otherObject;
        return hashCode() == otherGraphObject.hashCode();
    }

    @Override
    public String toString() {
        return String.format("GraphObject[#%d]", getId());
    }

    /**
     * @return presence of data contained in this graph object
     */
    public boolean hasContents() {
        return this.contents.isPresent();
    }

    /**
     * @return the data stored in this graph object,  if any
     */
    public Optional<E> getContents() {
        return this.contents;
    }

    /**
     * Stores a new content in the graph object.
     * 
     * @param contents the content to store, nullable
     */
    public void setContents(E contents) {
        this.contents = Optional.ofNullable(contents);
    }

    /**
     * Saves a spline anchor in the provided data stream.
     * 
     * @param out Stream where to write
     */
    public void saveTo(DataOutputStream out) {

        try {
            out.writeInt(id);
        } catch (IOException e) {
            LOG.error("Error : unable to graph object " + id, e);
        }

    }

    /**
     * Saves a graph object in the provided print stream.
     * 
     * @param out PrintStream where to write
     */
    public void saveTextTo(PrintStream out) {
        out.println(id);
    }

    /**
     * Saves the attributes on the given XML element.
     * 
     * @param element XML element where to save the attributes of this graph
     *            object
     */
    public void saveToXmlElement(Element element) {
        element.setAttribute("id", Integer.toString(id));
    }

}
