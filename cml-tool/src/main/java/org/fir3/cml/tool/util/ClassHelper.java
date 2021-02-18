package org.fir3.cml.tool.util;

import java.net.URL;
import java.security.CodeSource;
import java.util.Optional;

/**
 * A collection of (smaller) utility methods that are necessary for the CML
 * tool to work.
 */
public final class ClassHelper {
    /**
     * Returns the origin of the specified <code>cls</code>, which may be a
     * JAR-archive for example.
     *
     * @param cls   The class whose origin you want to determine.
     * @return  An {@link Optional} instance that either contains the URL of
     *          the class' origin, or <code>null</code>, if it is unknown.
     */
    public static Optional<URL> getOrigin(Class<?> cls) {
        return Optional.of(cls.getProtectionDomain().getCodeSource())
                .map(CodeSource::getLocation);
    }

    private ClassHelper() { }
}
