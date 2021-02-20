package org.fir3.cml.tool.exception;

/**
 * An exception that indicates that something went wrong while tokenizing some
 * bytes.
 */
public class TokenizerException extends Exception {
    public TokenizerException(String message) {
        super(message);
    }

    public TokenizerException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenizerException(Throwable cause) {
        super(cause);
    }
}
