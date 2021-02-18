package org.fir3.cml.api;

import org.fir3.cml.api.exception.ConfigurationException;
import org.fir3.cml.api.exception.TranslationException;
import org.fir3.cml.api.model.Environment;

import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An implementation of a translator that is capable of translating the
 * abstract representation of a CML environment to a specific programming
 * language and/or framework.
 *
 * <p>
 * Each valid implementation of this interface is required to provide a
 * {@link Info} annotation.
 * </p>
 *
 * <p>
 * Note that it is intended that translator implementations are discovered via
 * Java's SPI mechanism, and thus, you should always include the necessary
 * provider configuration file in the META-INF directory of your JAR-archive.
 * </p>
 */
public interface Translator {
    /**
     * This annotation provides some additional metadata to the users of the
     * {@link Translator} implementations.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @interface Info {
        /**
         * Returns the name of the target translator implementation.
         *
         * <p>
         * It is recommended that implementors choose names that only consist
         * of printable characters of the ASCII charset.
         * </p>
         *
         * @return  The human-readable name of the target translator
         *          implementation.
         */
        String name();
    }

    /**
     * Translates the specified <code>domain</code> to this implementation's
     * output format.
     *
     * @param environment   The environment of this translation task.
     * @param targetDomain  The full name of the domain that shall be
     *                      translated. It is required to be present in the
     *                      specified <code>environment</code>.
     *
     * @param configSource  Either a stream of bytes that provides additional
     *                      translator-specific configuration or
     *                      <code>null</code>, if there is no further
     *                      configuration.
     *
     * @throws ConfigurationException   If the provided
     *                                  <code>configSource</code> contains
     *                                  invalid data for this translator
     *                                  implementation.
     *
     * @throws TranslationException     If the translation fails due to some
     *                                  reason.
     */
    void translate(
            Environment environment,
            String targetDomain,
            InputStream configSource
    ) throws ConfigurationException, TranslationException;
}
