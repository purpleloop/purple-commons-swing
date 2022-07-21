package io.github.purpleloop.commons.swing.sprites.exception;

/** A runtime exception to use for sprite rendering problems.
 * For the moment, unchecked for retro-compatibility.
 */
public class SpriteRenderingException extends RuntimeException {

    /** Serial tag. */
    private static final long serialVersionUID = -327727974397727068L;

    /**
     * Constructor of the exception.
     * 
     * @param message message of the exception
     */
    public SpriteRenderingException(String message) {
        super(message);
    }

    /**
     * Constructor of the exception.
     * 
     * @param message message of the exception
     * @param cause the cause of the exception
     */
    public SpriteRenderingException(String message, Throwable cause) {
        super(message, cause);
    }

}
