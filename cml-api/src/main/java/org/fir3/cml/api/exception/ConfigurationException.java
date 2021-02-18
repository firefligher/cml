package org.fir3.cml.api.exception;

/**
 * An exception that indicates that something went wrong with the provided
 * configuration (IO issues, invalid syntax, inconsistent content, etc).
 */
public class ConfigurationException extends Exception {
    public ConfigurationException(String message) {
        super(message);
    }

    public ConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigurationException(Throwable cause) {
        super(cause);
    }
}
