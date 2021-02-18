package org.fir3.cml.api.exception;

/**
 * An exception that indicates that something went wrong during translating a
 * {@link org.fir3.cml.api.model.Domain} or similar.
 */
public class TranslationException extends Exception {
    public TranslationException(String message) {
        super(message);
    }

    public TranslationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TranslationException(Throwable cause) {
        super(cause);
    }
}
