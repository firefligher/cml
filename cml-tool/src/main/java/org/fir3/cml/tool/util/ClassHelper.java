package org.fir3.cml.tool.util;

import java.net.URL;
import java.security.CodeSource;
import java.util.Objects;
import java.util.Optional;

/**
 * A collection of (smaller) utility methods for {@link Class} instances that
 * are necessary for the CML tool to work.
 */
public final class ClassHelper {
    /**
     * Returns the origin of the specified <code>cls</code>, which may be a
     * JAR-archive for example.
     *
     * @param cls   The class whose origin you want to determine.
     * @return  An {@link Optional} instance that either contains the URL of
     *          the class' origin, or <code>null</code>, if it is unknown.
     *
     * @throws NullPointerException If <code>cls</code> is <code>null</code>.
     */
    public static Optional<URL> getOrigin(Class<?> cls) {
        Objects.requireNonNull(cls, "Parameter cls is not allowed to be null");

        return Optional.of(cls.getProtectionDomain().getCodeSource())
                .map(CodeSource::getLocation);
    }

    private ClassHelper() {
        throw new IllegalStateException("Do not instantiate this class");
    }
}
