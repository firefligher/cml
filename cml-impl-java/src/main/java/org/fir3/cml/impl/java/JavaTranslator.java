package org.fir3.cml.impl.java;

import org.fir3.cml.api.Builtin;
import org.fir3.cml.api.Translator;
import org.fir3.cml.api.exception.ConfigurationException;
import org.fir3.cml.api.model.Environment;
import org.fir3.cml.impl.java.config.Configuration;
import org.fir3.cml.impl.java.config.ConfigurationReader;
import org.fir3.cml.impl.java.config.JavaTypeInfo;
import org.fir3.cml.impl.java.config.TypeMapping;
import org.fir3.cml.impl.java.type.JavaTypeHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * A translator implementation that targets the Java programming language and
 * translates to Java source code.
 */
@Translator.Info(name = "java")
public final class JavaTranslator implements Translator {
    private static final Configuration DEFAULT;

    static {
        Set<TypeMapping> typeMappings = new HashSet<>();

        typeMappings.add(new TypeMapping(Builtin.TYPE_BIT, new JavaTypeInfo(
                JavaTypeHelper.fromString("bool"),
                JavaTypeHelper.fromString("java.lang.Boolean")
        )));

        typeMappings.add(new TypeMapping(Builtin.TYPE_SEQUENCE, new JavaTypeInfo(
                null,
                JavaTypeHelper.fromString("java.util.List<P:P1>")
        )));

        DEFAULT = new Configuration(typeMappings, null);
    }

    @Override
    public void translate(
            Environment environment,
            String targetDomain,
            InputStream configSource
    ) throws ConfigurationException {
        Configuration config = JavaTranslator.DEFAULT;

        // If there is some configuration, we expect it to be JSON and that it
        // is a serialized instance of the Configuration class.

        if (configSource != null) {
            try {
                config = ConfigurationReader.getInstance().read(configSource);
            } catch (IOException ex) {
                throw new ConfigurationException(ex);
            }
        }

        // Always merging the DEFAULT configuration into the custom one to
        // ensure that all required values are available.

        config = config.merge(JavaTranslator.DEFAULT);

        throw new UnsupportedOperationException("Not implemented");
    }
}
