package org.fir3.cml.tool.exception;

/**
 * An exception that indicates that something went wrong while parsing a token
 * stream.
 */
public class ParserException extends Exception {
    public ParserException(String message) {
        super(message);
    }

    public ParserException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParserException(Throwable cause) {
        super(cause);
    }
}
