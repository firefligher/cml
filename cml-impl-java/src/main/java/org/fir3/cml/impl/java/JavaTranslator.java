package org.fir3.cml.impl.java;

import com.google.gson.Gson;
import org.fir3.cml.api.Translator;
import org.fir3.cml.api.exception.ConfigurationException;
import org.fir3.cml.api.model.Environment;
import org.fir3.cml.impl.java.config.Configuration;
import org.fir3.cml.impl.java.config.ConfigurationReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * A translator implementation that targets the Java programming language and
 * translates to Java source code.
 */
@Translator.Info(name = "java")
public final class JavaTranslator implements Translator {
    private static final Configuration DEFAULT = new Configuration();
    private final Gson gson;

    public JavaTranslator() {
        this.gson = new Gson();
    }

    @Override
    public void translate(
            Environment environment,
            String targetDomain,
            InputStream configSource
    ) throws ConfigurationException {
        // If there is some configuration, we expect it to be JSON and that it
        // is a serialized instance of the Configuration class.

        Configuration config = JavaTranslator.DEFAULT;

        if (configSource != null) {
            try {
                config = ConfigurationReader.getInstance().read(configSource);
            } catch (IOException ex) {
                throw new ConfigurationException(ex);
            }
        }

        throw new UnsupportedOperationException("Not implemented");
    }
}
