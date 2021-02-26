package org.fir3.cml.tool.exception;

import java.io.IOException;

/**
 * An exception that indicates that something went wrong while tokenizing some
 * bytes.
 */
public class TokenizerException extends IOException {
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
