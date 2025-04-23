package io.github.purpleloop.commons.swing.graph.exception;

import io.github.purpleloop.commons.exception.PurpleException;

/** An exception for graph data structures. */
public class GraphException extends PurpleException {

    /** Serial tag. */
    private static final long serialVersionUID = 6698286326295948030L;

    /**
     * Exception constructor.
     * 
     * @param message message
     */
    public GraphException(String message) {
        super(message);
    }

    /**
     * Exception constructor.
     * 
     * @param message message
     * @param cause cause of the exception
     */
    public GraphException(String message, Throwable cause) {
        super(message, cause);
    }

}
