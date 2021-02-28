package org.fir3.cml.api.exception;

/**
 * An exception that is thrown when combining two instances of
 * {@link org.fir3.cml.api.model.Domain} fails.
 */
public class CombinationException extends Exception {
    public CombinationException(String message) {
        super(message);
    }

    public CombinationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CombinationException(Throwable cause) {
        super(cause);
    }
}
